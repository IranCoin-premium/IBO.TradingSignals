import { Response, NextFunction, Router } from 'express';
import { z } from 'zod';
import { ReferralService } from './referral.service';
import { ReferralRepository } from './referral.repository';
import { AuthenticatedRequest } from '../../middleware/auth';
import { authenticateToken, requireRoles } from '../../middleware/auth';
import { CustomError } from '../../middleware/error';
import crypto from 'crypto';

const payoutRequestSchema = z.object({
  amount: z.number().positive('مبلغ برداشت باید بزرگ‌تر از صفر باشد'),
  method: z.enum(['crypto_usdt', 'crypto_btc', 'bank_rial']),
  currency: z.string().length(3).optional().default('USD'),
});

const payoutDecisionSchema = z.object({
  action: z.enum(['approve', 'reject', 'mark_paid', 'cancel']),
  note: z.string().max(500).nullable().optional().default(null),
});

const bindSignupSchema = z.object({
  userId: z.string().uuid('شناسه کاربر نامعتبر است'),
  code: z.string().min(4).max(32),
});

const reverseSchema = z.object({
  conversionId: z.string().uuid('شناسه تبدیل نامعتبر است'),
  reason: z.string().min(4, 'علت برگشت الزامی است'),
});

function fail(statusCode: number, code: string, message: string): never {
  const error: CustomError = new Error(message);
  error.statusCode = statusCode;
  error.code = code;
  throw error;
}

// Public: sales-partnership landing (risk line always present)
export const getPartnershipLanding = async (_req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    res.status(200).json({ status: 'success', data: await ReferralService.getLandingData() });
  } catch (error) { next(error); }
};

// Public: track referral click (spam-flagged, code validated server-side)
export const trackReferralClick = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const code = String(req.params.code || '').trim();
    if (!code) fail(400, 'MISSING_CODE', 'کد معرف الزامی است.');
    const fingerprint = typeof req.body?.fingerprint === 'string' ? req.body.fingerprint.slice(0, 128) : null;
    const utm = typeof req.body?.utm_source === 'string' ? req.body.utm_source.slice(0, 100) : null;
    const ipHash = crypto.createHash('sha256').update(req.ip || 'unknown').digest('hex');
    const result = await ReferralService.trackClick(code, fingerprint, ipHash, utm);
    res.status(200).json({ status: 'success', data: result });
  } catch (error) { next(error); }
};

// Authenticated user: get-or-create own referral code
export const getMyReferralCode = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    const row = await ReferralService.getOrCreateCode(req.user.id);
    res.status(200).json({ status: 'success', data: { code: row.code, created_at: row.created_at ?? null } });
  } catch (error) { next(error); }
};

// Authenticated partner: dashboard (clicks, conversions, balance, payouts — read-only)
export const getPartnerDashboard = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    res.status(200).json({ status: 'success', data: await ReferralService.getDashboard(req.user.id) });
  } catch (error) { next(error); }
};

// Authenticated partner: request payout (server validates balance + tier minimum)
export const requestPayout = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    const parsed = payoutRequestSchema.safeParse(req.body);
    if (!parsed.success) {
      const error: CustomError = new Error('داده‌های برداشت نامعتبر است');
      error.statusCode = 400; error.code = 'VALIDATION_ERROR';
      error.details = parsed.error.flatten().fieldErrors;
      return next(error);
    }
    const payout = await ReferralService.requestPayout(req.user.id, parsed.data.amount, parsed.data.method, parsed.data.currency);
    res.status(201).json({ status: 'success', data: payout });
  } catch (error) { next(error); }
};

// Admin: payout decision (strict state machine)
export const decidePayout = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    const parsed = payoutDecisionSchema.safeParse(req.body);
    if (!parsed.success) {
      const error: CustomError = new Error('اقدام برداشت نامعتبر است');
      error.statusCode = 400; error.code = 'VALIDATION_ERROR';
      error.details = parsed.error.flatten().fieldErrors;
      return next(error);
    }
    const result = await ReferralService.decidePayout(req.params.payoutId, parsed.data.action, req.user.id, parsed.data.note ?? null);
    res.status(200).json({ status: 'success', data: result });
  } catch (error) { next(error); }
};

// Admin: reverse conversion on refund (negative ledger entry, append-only)
export const reverseConversion = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    const parsed = reverseSchema.safeParse(req.body);
    if (!parsed.success) {
      const error: CustomError = new Error('داده‌های برگشت نامعتبر است');
      error.statusCode = 400; error.code = 'VALIDATION_ERROR';
      error.details = parsed.error.flatten().fieldErrors;
      return next(error);
    }
    const result = await ReferralService.reverseConversion(parsed.data.conversionId, req.user.id, parsed.data.reason);
    res.status(200).json({ status: 'success', data: result });
  } catch (error) { next(error); }
};

// Internal (service-agent or admin): bind signup attribution after registration flow
export const bindSignupAttribution = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) fail(401, 'UNAUTHORIZED', 'احراز هویت انجام نشده است.');
    const parsed = bindSignupSchema.safeParse(req.body);
    if (!parsed.success) {
      const error: CustomError = new Error('داده‌های ارجاع نامعتبر است');
      error.statusCode = 400; error.code = 'VALIDATION_ERROR';
      error.details = parsed.error.flatten().fieldErrors;
      return next(error);
    }
    const result = await ReferralService.bindSignup(parsed.data.userId, parsed.data.code);
    res.status(200).json({ status: 'success', data: result });
  } catch (error) { next(error); }
};

// Admin: list registered platform tabs (readiness manifest for client integration)
export const listPlatformTabs = async (_req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    res.status(200).json({ status: 'success', data: await ReferralRepository.listTabs() });
  } catch (error) { next(error); }
};

const router = Router();
// Public
router.get('/partnership', getPartnershipLanding);
router.post('/click/:code', trackReferralClick);
// Authenticated user/partner
router.get('/my-code', authenticateToken as any, getMyReferralCode);
router.get('/dashboard', authenticateToken as any, getPartnerDashboard);
router.post('/payouts', authenticateToken as any, requestPayout);
// Admin
router.post('/admin/payouts/:payoutId/decision', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, decidePayout);
router.post('/admin/conversions/reverse', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, reverseConversion);
router.get('/admin/platform-tabs', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, listPlatformTabs);

export default router;
