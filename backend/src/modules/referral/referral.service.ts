import crypto from 'crypto';
import { CustomError } from '../../middleware/error';
import { RISK_DISCLOSURE_LINE } from '../../utils/constants';
import { ReferralRepository, ReferralCodeRow, ConversionRow, PayoutRow } from './referral.repository';

const CLICK_SPAM_THRESHOLD_PER_MINUTE = 10;

export interface ConversionInput {
  referredUserId: string;
  orderId?: string | null;
  transactionId?: string | null;
  planId?: string | null;
  amount: string;
  currency?: string;
}

function fail(statusCode: number, code: string, message: string): never {
  const error: CustomError = new Error(message);
  error.statusCode = statusCode;
  error.code = code;
  throw error;
}

function generateCode(): string {
  return `IBO${crypto.randomBytes(5).toString('base64url').replace(/[^A-Za-z0-9]/g, '').toUpperCase().slice(0, 8)}`;
}

export const ReferralService = {
  // L1: Get-or-create caller's unique referral code (server-side uniqueness)
  async getOrCreateCode(userId: string): Promise<ReferralCodeRow> {
    const existing = await ReferralRepository.findCodeByOwner(userId);
    if (existing) return existing;
    for (let attempt = 0; attempt < 5; attempt++) {
      try {
        return await ReferralRepository.createCode(generateCode(), userId);
      } catch (err: any) {
        if (attempt === 4) fail(500, 'CODE_GENERATION_FAILED', 'ساخت کد معرف با خطا مواجه شد.');
      }
    }
    fail(500, 'CODE_GENERATION_FAILED', 'ساخت کد معرف با خطا مواجه شد.');
  },

  // L2/L7: Public click tracking with click-spam detection hook
  async trackClick(code: string, fingerprint: string | null, ipHash: string | null, utm: string | null) {
    const codeRow = await ReferralRepository.findCodeByCode(code);
    if (!codeRow || !codeRow.is_active) {
      fail(404, 'REFERRAL_CODE_NOT_FOUND', 'کد معرف یافت نشد یا غیرفعال است.');
    }
    let isSpam = false;
    if (fingerprint) {
      const recent = await ReferralRepository.countRecentClicks(fingerprint);
      isSpam = recent >= CLICK_SPAM_THRESHOLD_PER_MINUTE;
    }
    await ReferralRepository.insertClick(codeRow.id, fingerprint, ipHash, utm, isSpam);
    return { tracked: true, spam: isSpam };
  },

  // L3: Bind signup to referral code; self-referral blocked server-side
  async bindSignup(userId: string, code: string) {
    const codeRow = await ReferralRepository.findCodeByCode(code);
    if (!codeRow || !codeRow.is_active) {
      fail(404, 'REFERRAL_CODE_NOT_FOUND', 'کد معرف یافت نشد یا غیرفعال است.');
    }
    if (codeRow.owner_user_id === userId) {
      fail(400, 'SELF_REFERRAL_BLOCKED', 'استفاده از کد معرف خودتان مجاز نیست.');
    }
    const existing = await ReferralRepository.findAttributionByUser(userId);
    if (existing) return { bound: true, alreadyBound: true };
    const attribution = await ReferralRepository.insertAttribution(userId, codeRow.id);
    return { bound: true, alreadyBound: false, attribution };
  },

  // L4/L5: Server-computed conversion (idempotent per transaction_id)
  async recordConversion(input: ConversionInput): Promise<{ conversion: ConversionRow; idempotent: boolean } | null> {
    const attribution = await ReferralRepository.findAttributionByUser(input.referredUserId);
    if (!attribution) return null; // no attribution → no commission (fail-closed)
    const codeRow = await ReferralRepository.findCodeById(attribution.referral_code_id);
    if (!codeRow) return null;
    if (codeRow.owner_user_id === input.referredUserId) {
      fail(400, 'SELF_REFERRAL_BLOCKED', 'کمیسیون برای خودارجاعی محاسبه نمی‌شود.');
    }
    if (input.transactionId) {
      const existing = await ReferralRepository.findConversionByTransaction(input.transactionId);
      if (existing) return { conversion: existing, idempotent: true }; // L5: replay-safe
    }
    const partner = await ReferralRepository.findPartnerByUser(codeRow.owner_user_id);
    const tier = partner?.tier || 'standard';
    const rule = await ReferralRepository.findRuleByTier(tier);
    const percent = Number(rule?.percent ?? 0);
    const currency = input.currency || 'USD';
    // Commission computed ONLY here (server-authoritative, anti-tamper)
    const commissionAmount = (Number(input.amount) * percent / 100).toFixed(8);
    const amountStr = Number(input.amount).toFixed(8);

    // Atomic: conversion + ledger accrual commit together
    const conversion = await ReferralRepository.withTransaction(async (exec) => {
      const conv = await ReferralRepository.insertConversion({
        referral_code_id: codeRow.id,
        referred_user_id: input.referredUserId,
        order_id: input.orderId ?? null,
        transaction_id: input.transactionId ?? null,
        plan_id: input.planId ?? null,
        amount: amountStr,
        currency,
        commission_amount: commissionAmount,
        commission_percent: percent.toFixed(2),
      }, exec);
      await ReferralRepository.insertLedger(codeRow.owner_user_id, conv.id, 'accrual', commissionAmount, currency, `Commission ${percent}% (tier: ${tier})`, exec);
      return conv;
    });
    return { conversion, idempotent: false };
  },

  // L9: Refund-driven reversal creates negative-flow ledger entry (append-only)
  async reverseConversion(conversionId: string, adminId: string, reason: string) {
    const conversion = await ReferralRepository.findConversionById(conversionId);
    if (!conversion) fail(404, 'CONVERSION_NOT_FOUND', 'تبدیل یافت نشد.');
    if (conversion.status === 'reversed') {
      fail(409, 'ALREADY_REVERSED', 'این تبدیل قبلاً برگشت خورده است.');
    }
    const codeRow = await ReferralRepository.findCodeById(conversion.referral_code_id);
    if (!codeRow) fail(404, 'REFERRAL_CODE_NOT_FOUND', 'کد معرف یافت نشد.');
    await ReferralRepository.withTransaction(async (exec) => {
      await ReferralRepository.markConversionReversed(conversion.id, exec);
      await ReferralRepository.insertLedger(codeRow.owner_user_id, conversion.id, 'reversal', conversion.commission_amount, conversion.currency, `Reversal by ${adminId}: ${reason}`, exec);
    });
    return { reversed: true, ledgerAmount: conversion.commission_amount };
  },

  // Partner dashboard (read-only values served from backend truth)
  async getDashboard(userId: string) {
    const codeRow = await ReferralRepository.findCodeByOwner(userId);
    if (!codeRow) {
      return { hasCode: false, code: null, clicks: 0, conversions: 0, balance: '0.00000000', currency: 'USD', payouts: [] as PayoutRow[] };
    }
    const [clicks, conversions, balance, payouts] = await Promise.all([
      ReferralRepository.countClicksByCode(codeRow.id),
      ReferralRepository.countConversionsByCode(codeRow.id),
      ReferralRepository.getBalance(userId),
      ReferralRepository.listPayoutsByPartner(userId),
    ]);
    return { hasCode: true, code: codeRow.code, clicks, conversions, balance, currency: 'USD', payouts };
  },

  // L6: Payout request validated against ledger balance + tier minimum
  async requestPayout(userId: string, amount: number, method: string, currency = 'USD') {
    const partner = await ReferralRepository.findPartnerByUser(userId);
    const tier = partner?.tier || 'standard';
    const rule = await ReferralRepository.findRuleByTier(tier);
    const minPayout = Number(rule?.min_payout_amount ?? 10);
    const balance = Number(await ReferralRepository.getBalance(userId, currency));
    if (amount <= 0) fail(400, 'INVALID_AMOUNT', 'مبلغ برداشت باید بزرگ‌تر از صفر باشد.');
    if (amount > balance) fail(400, 'INSUFFICIENT_BALANCE', 'مبلغ درخواستی از موجودی کمیسیون شما بیشتر است.');
    if (amount < minPayout) fail(400, 'BELOW_MIN_PAYOUT', `حداقل مبلغ برداشت برای سطح ${tier} برابر ${minPayout} است.`);
    return ReferralRepository.insertPayout(userId, amount.toFixed(8), currency, method, userId);
  },

  // L7: Strict payout state machine; admin-only transitions
  async decidePayout(payoutId: string, action: 'approve' | 'reject' | 'mark_paid' | 'cancel', adminId: string, note: string | null) {
    const payout = await ReferralRepository.findPayoutById(payoutId);
    if (!payout) fail(404, 'PAYOUT_NOT_FOUND', 'درخواست برداشت یافت نشد.');
    const allowed: Record<string, string[]> = {
      pending: ['approve', 'reject', 'cancel'],
      approved: ['mark_paid', 'cancel'],
    };
    if (!allowed[payout.status]?.includes(action)) {
      fail(409, 'INVALID_PAYOUT_TRANSITION', `گذار وضعیت ${action} از حالت ${payout.status} مجاز نیست.`);
    }
    const statusMap: Record<string, string> = { approve: 'approved', reject: 'rejected', mark_paid: 'paid', cancel: 'cancelled' };
    const newStatus = statusMap[action];
    if (action === 'mark_paid') {
      await ReferralRepository.withTransaction(async (exec) => {
        await ReferralRepository.updatePayoutStatus(payout.id, newStatus, adminId, note, exec);
        await ReferralRepository.insertLedger(payout.partner_user_id, null, 'payout', payout.amount, payout.currency, `Payout ${payout.id} paid by ${adminId}`, exec);
      });
    } else {
      await ReferralRepository.updatePayoutStatus(payout.id, newStatus, adminId, note);
    }
    return { payoutId: payout.id, status: newStatus };
  },

  // L9 helper: find conversion by payment transaction id and reverse it (refund flow).
  // No attributed conversion → nothing to reverse (returns null, never throws).
  async reverseConversionByTransaction(transactionId: string, adminId: string, reason: string) {
    const conversion = await ReferralRepository.findConversionByTransaction(transactionId);
    if (!conversion) return null;
    return this.reverseConversion(conversion.id, adminId, reason);
  },

  // Public landing payload: risk line mandatory, zero income guarantees
  async getLandingData() {
    const rules = await ReferralRepository.listActiveRules();
    return {
      program: 'referral_sales_partnership',
      risk_disclosure: RISK_DISCLOSURE_LINE,
      income_guarantees: 'none',
      commission_tiers: rules.map((r) => ({ tier: r.tier, percent: Number(r.percent) })),
      how_it_works: ['register', 'share_code', 'referee_subscribes', 'commission_accrues_server_side', 'admin_approved_payout'],
    };
  },
};
