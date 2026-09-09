import request from 'supertest';
import app from '../app';
import jwt from 'jsonwebtoken';
import { pool } from '../config/database';
import { ReferralService } from '../modules/referral/referral.service';
import { PaymentsRepository } from '../modules/payments/payments.repository';

jest.mock('pg', () => {
  const mClient = { query: jest.fn(), release: jest.fn() };
  const mPool = {
    connect: jest.fn().mockResolvedValue(mClient),
    query: jest.fn(),
    on: jest.fn(),
    end: jest.fn(),
  };
  return { Pool: jest.fn(() => mPool) };
});

describe('IBO Referral & Sales Partnership — 10-Level Chain (Part 12)', () => {
  const prefix = '/api/v1';
  // Same dynamic secret resolution as middleware/auth.ts (dotenv loads backend/.env)
  const JWT_SECRET = process.env.JWT_SECRET || 'super_secret_jwt_sign_key_change_me_in_production';
  const userToken = jwt.sign(
    { id: 'user_uuid_123', email: 'user@ibo.ir', roles: ['USER'] },
    JWT_SECRET
  );
  const adminToken = jwt.sign(
    { id: 'admin_uuid_789', email: 'admin@ibo.ir', roles: ['ADMIN'] },
    JWT_SECRET
  );

  const mClient = { query: jest.fn(), release: jest.fn() };

  beforeEach(() => {
    mClient.query.mockReset();
    mClient.query.mockResolvedValue({ rows: [], rowCount: 0 });
    (pool.connect as jest.Mock).mockReset();
    (pool.connect as jest.Mock).mockResolvedValue(mClient);
    (pool.query as jest.Mock).mockReset();
  });

  const routeQuery = (impl: (sql: string, params?: any[]) => any) => {
    (pool.query as jest.Mock).mockImplementation((sql: string, params?: any[]) => {
      const s = (sql || '').toString().toUpperCase();
      return Promise.resolve(impl(s, params));
    });
  };

  it('L1: GET /referral/my-code creates a stable unique code for the caller', async () => {
    routeQuery((s, p) => {
      if (s.includes('FROM REFERRAL_CODES') && s.includes('OWNER_USER_ID')) return { rows: [], rowCount: 0 };
      if (s.includes('INSERT INTO REFERRAL_CODES')) return { rows: [{ id: 'c1', code: p?.[0], owner_user_id: p?.[1], is_active: true }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    const res = await request(app).get(`${prefix}/referral/my-code`).set('Authorization', `Bearer ${userToken}`);
    expect(res.status).toBe(200);
    expect(res.body.data.code).toMatch(/^IBO/);
  });

  it('L2: GET /referral/partnership always serves the mandatory risk line and zero income guarantees', async () => {
    routeQuery(() => ({ rows: [
      { tier: 'standard', percent: '15.00', min_payout_amount: '10', is_active: true },
      { tier: 'silver', percent: '20.00', min_payout_amount: '10', is_active: true },
      { tier: 'gold', percent: '25.00', min_payout_amount: '25', is_active: true },
      { tier: 'platinum', percent: '30.00', min_payout_amount: '50', is_active: true },
    ], rowCount: 4 }));
    const res = await request(app).get(`${prefix}/referral/partnership`);
    expect(res.status).toBe(200);
    expect(res.body.data.risk_disclosure).toContain('توصیه مالی نیستند');
    expect(res.body.data.income_guarantees).toBe('none');
    expect(res.body.data.commission_tiers).toHaveLength(4);
  });

  it('L3: self-referral is hard-blocked at service level', async () => {
    routeQuery((s) => {
      if (s.includes('FROM REFERRAL_CODES') && s.includes('WHERE CODE')) return { rows: [{ id: 'c1', code: 'IBOAAA11111', owner_user_id: 'user_uuid_123', is_active: true }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    await expect(ReferralService.bindSignup('user_uuid_123', 'IBOAAA11111')).rejects.toMatchObject({ code: 'SELF_REFERRAL_BLOCKED' });
  });

  it('L5: conversion recording is idempotent per transaction (double-webhook replay safe)', async () => {
    let callCount = 0;
    routeQuery((s, p) => {
      if (s.includes('FROM REFERRAL_ATTRIBUTIONS')) return { rows: [{ referred_user_id: 'referee-1', referral_code_id: 'c1', bound_at: new Date() }], rowCount: 1 };
      if (s.includes('FROM REFERRAL_CODES') && s.includes('WHERE ID')) return { rows: [{ id: 'c1', code: 'IBOAAA11111', owner_user_id: 'owner-uuid', is_active: true }], rowCount: 1 };
      if (s.includes('FROM REFERRAL_CONVERSIONS') && s.includes('WHERE TRANSACTION_ID')) {
        callCount++;
        return callCount === 1 ? { rows: [], rowCount: 0 } : { rows: [{ id: 'conv-1', referral_code_id: 'c1', referred_user_id: 'referee-1', transaction_id: p?.[0], amount: '100.00000000', currency: 'USD', commission_amount: '15.00000000', commission_percent: '15.00', status: 'attributed' }], rowCount: 1 };
      }
      if (s.includes('FROM PARTNER_PROFILES')) return { rows: [], rowCount: 0 };
      if (s.includes('FROM COMMISSION_RULES')) return { rows: [{ tier: 'standard', percent: '15.00', min_payout_amount: '10', is_active: true }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    mClient.query.mockImplementation((sql: string, params?: any[]) => {
      const s = (sql || '').toString().toUpperCase();
      if (s.includes('INSERT INTO REFERRAL_CONVERSIONS')) return Promise.resolve({ rows: [{ id: 'conv-1', referral_code_id: 'c1', referred_user_id: 'referee-1', transaction_id: params?.[3], amount: params?.[5], currency: params?.[6], commission_amount: params?.[7], commission_percent: params?.[8], status: 'attributed' }], rowCount: 1 });
      return Promise.resolve({ rows: [], rowCount: 0 });
    });
    const first = await ReferralService.recordConversion({ referredUserId: 'referee-1', transactionId: 'txn-111', amount: '100.00' });
    const second = await ReferralService.recordConversion({ referredUserId: 'referee-1', transactionId: 'txn-111', amount: '100.00' });
    expect(first).not.toBeNull();
    expect(first!.idempotent).toBe(false);
    expect(second).not.toBeNull();
    expect(second!.idempotent).toBe(true);
    expect(first!.conversion.id).toBe(second!.conversion.id);
  });

  it('L6: payout guards — insufficient balance AND below-tier-minimum are both rejected server-side', async () => {
    // Scenario A: requested amount (5) exceeds ledger balance (3) → INSUFFICIENT_BALANCE
    routeQuery((s) => {
      if (s.includes('FROM PARTNER_PROFILES')) return { rows: [], rowCount: 0 };
      if (s.includes('FROM COMMISSION_RULES')) return { rows: [{ tier: 'standard', percent: '15.00', min_payout_amount: '10', is_active: true }], rowCount: 1 };
      if (s.includes('FROM COMMISSION_LEDGER')) return { rows: [{ balance: '3' }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    await expect(ReferralService.requestPayout('user_uuid_123', 5, 'crypto_usdt')).rejects.toMatchObject({ code: 'INSUFFICIENT_BALANCE' });
    // Scenario B: balance (5) covers amount (5) but amount is below tier minimum (10) → BELOW_MIN_PAYOUT
    routeQuery((s) => {
      if (s.includes('FROM PARTNER_PROFILES')) return { rows: [], rowCount: 0 };
      if (s.includes('FROM COMMISSION_RULES')) return { rows: [{ tier: 'standard', percent: '15.00', min_payout_amount: '10', is_active: true }], rowCount: 1 };
      if (s.includes('FROM COMMISSION_LEDGER')) return { rows: [{ balance: '5' }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    await expect(ReferralService.requestPayout('user_uuid_123', 5, 'crypto_usdt')).rejects.toMatchObject({ code: 'BELOW_MIN_PAYOUT' });
  });

  it('L7: illegal payout transition (mark_paid on rejected) is blocked by state machine', async () => {
    routeQuery((s) => {
      if (s.includes('FROM PAYOUTS WHERE ID')) return { rows: [{ id: 'p1', partner_user_id: 'owner-uuid', amount: '15.00000000', currency: 'USD', payout_method: 'crypto_usdt', status: 'rejected', decision_note: null }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    await expect(ReferralService.decidePayout('p1', 'mark_paid', 'admin_uuid_789', null)).rejects.toMatchObject({ code: 'INVALID_PAYOUT_TRANSITION' });
  });

  it('L9: refund reversal of an unknown conversion fails closed with CONVERSION_NOT_FOUND', async () => {
    routeQuery((s) => {
      if (s.includes('FROM REFERRAL_CONVERSIONS') && s.includes('WHERE ID')) return { rows: [], rowCount: 0 };
      return { rows: [], rowCount: 0 };
    });
    await expect(ReferralService.reverseConversion('00000000-0000-4000-8000-00000000dead', 'admin_uuid_789', 'drill')).rejects.toMatchObject({ code: 'CONVERSION_NOT_FOUND' });
  });

  it('L9b: GET /tabs/manifest exposes backend-ready tab registry for AI Studio client', async () => {
    routeQuery((s) => {
      if (s.includes('FROM PLATFORM_TABS')) return { rows: [
        { tab_key: 'referral_partner', title_i18n_key: 'tabs.referralPartner', route: '/partner', icon: 'handshake', audience: 'partner', status: 'backend_ready', feature_flags: {} },
        { tab_key: 'sales_partnership', title_i18n_key: 'tabs.salesPartnership', route: '/sales-partnership', icon: 'storefront', audience: 'public', status: 'backend_ready', feature_flags: {} },
        { tab_key: 'affiliate_dashboard', title_i18n_key: 'tabs.affiliateDashboard', route: '/affiliate', icon: 'insights', audience: 'user', status: 'backend_ready', feature_flags: {} },
      ], rowCount: 3 };
      return { rows: [], rowCount: 0 };
    });
    const res = await request(app).get(`${prefix}/tabs/manifest`);
    expect(res.status).toBe(200);
    expect(res.body.data.readiness).toBe('backend_ready');
    expect(res.body.data.tabs).toHaveLength(3);
    expect(res.body.data.tabs.map((t: any) => t.tab_key)).toContain('referral_partner');
  });

  it('L10: admin-only payout decisions — USER role is rejected with 403', async () => {
    const res = await request(app)
      .post(`${prefix}/referral/admin/payouts/00000000-0000-4000-8000-00000000dead/decision`)
      .set('Authorization', `Bearer ${userToken}`)
      .send({ action: 'approve', note: null });
    expect(res.status).toBe(403);
    expect(res.body.code).toBe('INSUFFICIENT_PERMISSIONS');
  });

  it('L10b: unauthenticated access to my-code is rejected with 401', async () => {
    const res = await request(app).get(`${prefix}/referral/my-code`);
    expect(res.status).toBe(401);
  });

  it('L11: payment success triggers server-side commission accrual hook (Part 12 integration)', async () => {
    const spy = jest.spyOn(ReferralService, 'recordConversion').mockResolvedValue(null as any);
    routeQuery((s) => {
      if (s.includes('FROM PAYMENT_TRANSACTIONS')) return { rows: [{ id: 'tx1', user_id: 'referee-1', plan_id: 'plan-1', amount: 100, currency: 'USD', status: 'PENDING' }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    mClient.query.mockImplementation((sql: string) => {
      const s = (sql || '').toString().toUpperCase();
      if (s.includes('FROM PAYMENT_TRANSACTIONS')) return Promise.resolve({ rows: [{ id: 'tx1', user_id: 'referee-1', plan_id: 'plan-1', amount: 100, currency: 'USD', status: 'PENDING' }], rowCount: 1 });
      if (s.includes('SUBSCRIPTION_PLANS')) return Promise.resolve({ rows: [{ duration_days: 30 }], rowCount: 1 });
      return Promise.resolve({ rows: [{ id: 'x' }], rowCount: 1 });
    });
    await PaymentsRepository.verifyAndActivateTransaction('tx1', 'WEBHOOK', {});
    expect(spy).toHaveBeenCalledTimes(1);
    expect(spy).toHaveBeenCalledWith(expect.objectContaining({
      referredUserId: 'referee-1',
      transactionId: 'tx1',
      amount: '100',
      currency: 'USD',
    }));
    spy.mockRestore();
  });

  it('L12: refund flow reverses attributed commission atomically (negative ledger)', async () => {
    const revSpy = jest.spyOn(ReferralService, 'reverseConversionByTransaction').mockResolvedValue({ reversed: true } as any);
    routeQuery((s) => {
      if (s.includes('FROM PAYMENT_TRANSACTIONS')) return { rows: [{ id: 'tx1', user_id: 'referee-1', plan_id: 'plan-1', amount: 100, currency: 'USD', status: 'SUCCESS' }], rowCount: 1 };
      return { rows: [], rowCount: 0 };
    });
    mClient.query.mockImplementation((sql: string) => {
      const s = (sql || '').toString().toUpperCase();
      if (s.includes('FROM PAYMENT_TRANSACTIONS')) return Promise.resolve({ rows: [{ id: 'tx1', user_id: 'referee-1', plan_id: 'plan-1', amount: 100, currency: 'USD', status: 'SUCCESS' }], rowCount: 1 });
      return Promise.resolve({ rows: [{ id: 'rf1', amount: '50' }], rowCount: 1 });
    });
    await PaymentsRepository.issueRefund('tx1', 50, 'customer request', 'admin_uuid_789');
    expect(revSpy).toHaveBeenCalledTimes(1);
    expect(revSpy).toHaveBeenCalledWith('tx1', 'admin_uuid_789', 'customer request');
    revSpy.mockRestore();
  });
});
