import { pool, query } from '../../config/database';

export interface ReferralCodeRow { id: string; code: string; owner_user_id: string; is_active: boolean; created_at?: Date; }
export interface AttributionRow { referred_user_id: string; referral_code_id: string; bound_at: Date; }
export interface PartnerProfileRow { id: string; user_id: string; display_name: string; tier: string; payout_method: string; is_verified: boolean; }
export interface CommissionRuleRow { tier: string; percent: string; min_payout_amount: string; is_active: boolean; }
export interface ConversionRow { id: string; referral_code_id: string; referred_user_id: string; transaction_id: string | null; amount: string; currency: string; commission_amount: string; commission_percent: string; status: string; }
export interface PayoutRow { id: string; partner_user_id: string; amount: string; currency: string; payout_method: string; status: string; decision_note: string | null; }

export type Executor = { query: (sql: string, params?: any[]) => Promise<any> };

export const ReferralRepository = {
  async createCode(code: string, userId: string, exec?: Executor): Promise<ReferralCodeRow> {
    const db = exec ? exec.query : (query as any);
    const r = await db(`INSERT INTO referral_codes (code, owner_user_id) VALUES ($1, $2) RETURNING *`, [code, userId]);
    return r.rows[0];
  },
  async findCodeByCode(code: string): Promise<ReferralCodeRow | null> {
    const r = await query(`SELECT * FROM referral_codes WHERE code = $1 LIMIT 1`, [code]);
    return r.rows[0] || null;
  },
  async findCodeById(id: string): Promise<ReferralCodeRow | null> {
    const r = await query(`SELECT * FROM referral_codes WHERE id = $1 LIMIT 1`, [id]);
    return r.rows[0] || null;
  },
  async findCodeByOwner(userId: string): Promise<ReferralCodeRow | null> {
    const r = await query(`SELECT * FROM referral_codes WHERE owner_user_id = $1 AND is_active = TRUE LIMIT 1`, [userId]);
    return r.rows[0] || null;
  },
  async insertClick(codeId: string, fingerprint: string | null, ipHash: string | null, utm: string | null, isSpam: boolean): Promise<void> {
    await query(`INSERT INTO referral_clicks (referral_code_id, visitor_fingerprint, ip_hash, utm_source, is_spam) VALUES ($1, $2, $3, $4, $5)`, [codeId, fingerprint, ipHash, utm, isSpam]);
  },
  async countRecentClicks(fingerprint: string): Promise<number> {
    const r = await query(`SELECT COUNT(*)::int AS count FROM referral_clicks WHERE visitor_fingerprint = $1 AND created_at > NOW() - INTERVAL '60 seconds'`, [fingerprint]);
    return Number(r.rows[0]?.count || 0);
  },
  async countClicksByCode(codeId: string): Promise<number> {
    const r = await query(`SELECT COUNT(*)::int AS count FROM referral_clicks WHERE referral_code_id = $1`, [codeId]);
    return Number(r.rows[0]?.count || 0);
  },
  async insertAttribution(userId: string, codeId: string, exec?: Executor): Promise<AttributionRow> {
    const db = exec ? exec.query : (query as any);
    const r = await db(`INSERT INTO referral_attributions (referred_user_id, referral_code_id) VALUES ($1, $2) RETURNING *`, [userId, codeId]);
    return r.rows[0];
  },
  async findAttributionByUser(userId: string): Promise<AttributionRow | null> {
    const r = await query(`SELECT * FROM referral_attributions WHERE referred_user_id = $1 LIMIT 1`, [userId]);
    return r.rows[0] || null;
  },
  async findPartnerByUser(userId: string): Promise<PartnerProfileRow | null> {
    const r = await query(`SELECT * FROM partner_profiles WHERE user_id = $1 LIMIT 1`, [userId]);
    return r.rows[0] || null;
  },
  async findRuleByTier(tier: string): Promise<CommissionRuleRow | null> {
    const r = await query(`SELECT tier, percent, min_payout_amount, is_active FROM commission_rules WHERE tier = $1 AND is_active = TRUE LIMIT 1`, [tier]);
    return r.rows[0] || null;
  },
  async listActiveRules(): Promise<CommissionRuleRow[]> {
    const r = await query(`SELECT tier, percent, min_payout_amount, is_active FROM commission_rules WHERE is_active = TRUE ORDER BY percent ASC`);
    return r.rows || [];
  },
  async findConversionByTransaction(transactionId: string): Promise<ConversionRow | null> {
    const r = await query(`SELECT * FROM referral_conversions WHERE transaction_id = $1 LIMIT 1`, [transactionId]);
    return r.rows[0] || null;
  },
  async findConversionById(id: string): Promise<ConversionRow | null> {
    const r = await query(`SELECT * FROM referral_conversions WHERE id = $1 LIMIT 1`, [id]);
    return r.rows[0] || null;
  },
  async insertConversion(p: { referral_code_id: string; referred_user_id: string; order_id: string | null; transaction_id: string | null; plan_id: string | null; amount: string; currency: string; commission_amount: string; commission_percent: string; }, exec?: Executor): Promise<ConversionRow> {
    const db = exec ? exec.query : (query as any);
    const r = await db(
      `INSERT INTO referral_conversions (referral_code_id, referred_user_id, order_id, transaction_id, plan_id, amount, currency, commission_amount, commission_percent) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING *`,
      [p.referral_code_id, p.referred_user_id, p.order_id, p.transaction_id, p.plan_id, p.amount, p.currency, p.commission_amount, p.commission_percent]
    );
    return r.rows[0];
  },
  async markConversionReversed(id: string, exec?: Executor): Promise<void> {
    const db = exec ? exec.query : (query as any);
    await db(`UPDATE referral_conversions SET status = 'reversed' WHERE id = $1`, [id]);
  },
  async insertLedger(partnerUserId: string, conversionId: string | null, entryType: 'accrual' | 'reversal' | 'payout', amount: string, currency: string, note: string | null, exec?: Executor): Promise<void> {
    const db = exec ? exec.query : (query as any);
    await db(`INSERT INTO commission_ledger (partner_user_id, conversion_id, entry_type, amount, currency, note) VALUES ($1, $2, $3, $4, $5, $6)`, [partnerUserId, conversionId, entryType, amount, currency, note]);
  },
  async getBalance(partnerUserId: string, currency = 'USD'): Promise<string> {
    const r = await query(`SELECT COALESCE(SUM(CASE WHEN entry_type IN ('payout', 'reversal') THEN -amount ELSE amount END), 0) AS balance FROM commission_ledger WHERE partner_user_id = $1 AND currency = $2`, [partnerUserId, currency]);
    return String(r.rows[0]?.balance ?? '0');
  },
  async countConversionsByCode(codeId: string): Promise<number> {
    const r = await query(`SELECT COUNT(*)::int AS count FROM referral_conversions WHERE referral_code_id = $1 AND status = 'attributed'`, [codeId]);
    return Number(r.rows[0]?.count || 0);
  },
  async insertPayout(partnerUserId: string, amount: string, currency: string, method: string, requestedBy: string): Promise<PayoutRow> {
    const r = await query(`INSERT INTO payouts (partner_user_id, amount, currency, payout_method, status, requested_by) VALUES ($1, $2, $3, $4, 'pending', $5) RETURNING *`, [partnerUserId, amount, currency, method, requestedBy]);
    return r.rows[0];
  },
  async findPayoutById(id: string): Promise<PayoutRow | null> {
    const r = await query(`SELECT * FROM payouts WHERE id = $1 LIMIT 1`, [id]);
    return r.rows[0] || null;
  },
  async listPayoutsByPartner(userId: string): Promise<PayoutRow[]> {
    const r = await query(`SELECT * FROM payouts WHERE partner_user_id = $1 ORDER BY created_at DESC`, [userId]);
    return r.rows || [];
  },
  async updatePayoutStatus(id: string, status: string, decidedBy: string, note: string | null, exec?: Executor): Promise<PayoutRow> {
    const db = exec ? exec.query : (query as any);
    const r = await db(`UPDATE payouts SET status = $2, decided_by = $3, decision_note = $4, decided_at = NOW() WHERE id = $1 RETURNING *`, [id, status, decidedBy, note]);
    return r.rows[0];
  },
  async listTabs(): Promise<any[]> {
    const r = await query(`SELECT * FROM platform_tabs ORDER BY sort_order ASC`);
    return r.rows || [];
  },
  async withTransaction<T>(fn: (exec: Executor) => Promise<T>): Promise<T> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');
      const exec: Executor = { query: (sql: string, params?: any[]) => (client as any).query(sql, params) };
      const out = await fn(exec);
      await client.query('COMMIT');
      return out;
    } catch (err) {
      await client.query('ROLLBACK');
      throw err;
    } finally {
      client.release();
    }
  },
};
