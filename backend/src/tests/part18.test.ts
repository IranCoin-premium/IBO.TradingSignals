/**
 * IBO Ecosystem — Billing, Subscriptions, Entitlements & Financial Controls Test Suite
 * Master Prompt — Part 18: Work Packages 18.01 - 18.20
 */

import crypto from 'crypto';
import { BillingEntitlementEngine } from '../modules/billing/billing.service';

describe('Part 18 — Billing, Subscriptions, Entitlements, Payment Readiness & Financial Reconciliation', () => {

  describe('Work Package 18.01 & 18.02: Canonical Plans & Equal Core Capabilities', () => {
    it('should list all 5 canonical subscription durations with equal core entitlement keys', () => {
      const plans = BillingEntitlementEngine.listPlans();
      expect(plans.length).toBe(5);

      const durations = plans.map(p => p.duration);
      expect(durations).toContain('WEEKLY');
      expect(durations).toContain('MONTHLY');
      expect(durations).toContain('THREE_MONTH');
      expect(durations).toContain('SIX_MONTH');
      expect(durations).toContain('YEARLY');

      // Verify invariant: Equal core capabilities across all durations
      for (const plan of plans) {
        expect(plan.entitlements).toContain('SIGNALS_BINARY_CORE');
        expect(plan.entitlements).toContain('SIGNALS_VIP_FAST');
        expect(plan.entitlements).toContain('TECHNICAL_INDICATORS');
        expect(plan.entitlements).toContain('AI_INSIGHTS');
      }
    });
  });

  describe('Work Package 18.04 & 18.05: Idempotency & Payment State Ingestion', () => {
    it('should initiate a new payment transaction with pending state', () => {
      const res = BillingEntitlementEngine.initiatePayment({
        userId: 'usr-trader-01',
        planId: 'PLAN-MONTHLY',
        provider: 'NOWPAYMENTS_SANDBOX',
        idempotencyKey: 'idem-pay-test-001'
      });

      expect(res.success).toBe(true);
      expect(res.duplicate).toBe(false);
      expect(res.payment).toBeDefined();
      expect(res.payment?.state).toBe('PENDING');
      expect(res.payment?.amountUsd).toBe(29.99);
    });

    it('should prevent double-charging by returning existing transaction on duplicate idempotency key', () => {
      const resDuplicate = BillingEntitlementEngine.initiatePayment({
        userId: 'usr-trader-01',
        planId: 'PLAN-MONTHLY',
        provider: 'NOWPAYMENTS_SANDBOX',
        idempotencyKey: 'idem-pay-test-001'
      });

      expect(resDuplicate.success).toBe(true);
      expect(resDuplicate.duplicate).toBe(true);
      expect(resDuplicate.payment?.idempotencyKey).toBe('idem-pay-test-001');
    });
  });

  describe('Work Package 18.06 & 18.07: Cryptographic Webhook Verification & Entitlement Provisioning', () => {
    it('should reject webhook processing when HMAC signature is invalid', () => {
      const init = BillingEntitlementEngine.initiatePayment({
        userId: 'usr-trader-02',
        planId: 'PLAN-YEARLY',
        provider: 'STRIPE_SANDBOX',
        idempotencyKey: 'idem-pay-test-002'
      });
      const paymentId = init.payment!.paymentId;

      const result = BillingEntitlementEngine.verifyWebhookAndSettlePayment({
        paymentId,
        signatureHeader: 'invalid-fake-signature',
        webhookSecret: 'secret-webhook-key-123',
        payloadRaw: JSON.stringify({ paymentId, status: 'SUCCESS' }),
        statusReported: 'SUCCESS'
      });

      expect(result.verified).toBe(false);
      expect(result.settled).toBe(false);
      expect(result.error).toBe('Invalid webhook signature');
    });

    it('should settle payment, create subscription, and activate entitlements upon valid signature', () => {
      const init = BillingEntitlementEngine.initiatePayment({
        userId: 'usr-trader-03',
        planId: 'PLAN-3MONTH',
        provider: 'NOWPAYMENTS_SANDBOX',
        idempotencyKey: 'idem-pay-test-003'
      });
      const paymentId = init.payment!.paymentId;
      const secret = 'prod-webhook-hmac-secret-xyz';
      const payloadRaw = JSON.stringify({ paymentId, status: 'SUCCESS' });
      const signatureHeader = crypto.createHmac('sha256', secret).update(payloadRaw).digest('hex');

      const result = BillingEntitlementEngine.verifyWebhookAndSettlePayment({
        paymentId,
        signatureHeader,
        webhookSecret: secret,
        payloadRaw,
        statusReported: 'SUCCESS'
      });

      expect(result.verified).toBe(true);
      expect(result.settled).toBe(true);
      expect(result.subscription).toBeDefined();
      expect(result.subscription?.state).toBe('ACTIVE');

      // Verify entitlement access
      expect(BillingEntitlementEngine.hasEntitlement('usr-trader-03', 'SIGNALS_BINARY_CORE')).toBe(true);
      expect(BillingEntitlementEngine.hasEntitlement('usr-trader-03', 'SIGNALS_VIP_FAST')).toBe(true);
      expect(BillingEntitlementEngine.hasEntitlement('usr-trader-03', 'AI_INSIGHTS')).toBe(true);
    });

    it('should revoke entitlements when payment dispute / chargeback is received', () => {
      const init = BillingEntitlementEngine.initiatePayment({
        userId: 'usr-trader-04',
        planId: 'PLAN-WEEKLY',
        provider: 'STRIPE_SANDBOX',
        idempotencyKey: 'idem-pay-test-004'
      });
      const paymentId = init.payment!.paymentId;
      const secret = 'webhook-dispute-secret';

      // First settle
      const payloadRaw1 = JSON.stringify({ paymentId, status: 'SUCCESS' });
      const sig1 = crypto.createHmac('sha256', secret).update(payloadRaw1).digest('hex');
      BillingEntitlementEngine.verifyWebhookAndSettlePayment({
        paymentId,
        signatureHeader: sig1,
        webhookSecret: secret,
        payloadRaw: payloadRaw1,
        statusReported: 'SUCCESS'
      });
      expect(BillingEntitlementEngine.hasEntitlement('usr-trader-04', 'SIGNALS_BINARY_CORE')).toBe(true);

      // Now dispute
      const payloadRaw2 = JSON.stringify({ paymentId, status: 'DISPUTED' });
      const sig2 = crypto.createHmac('sha256', secret).update(payloadRaw2).digest('hex');
      const disputeRes = BillingEntitlementEngine.verifyWebhookAndSettlePayment({
        paymentId,
        signatureHeader: sig2,
        webhookSecret: secret,
        payloadRaw: payloadRaw2,
        statusReported: 'DISPUTED'
      });

      expect(disputeRes.verified).toBe(true);
      expect(disputeRes.settled).toBe(false);
      expect(BillingEntitlementEngine.hasEntitlement('usr-trader-04', 'SIGNALS_BINARY_CORE')).toBe(false);
    });
  });

  describe('Work Package 18.16 & 18.18: Financial Reconciliation Engine', () => {
    it('should audit transactions and record a balanced financial reconciliation record', () => {
      const rec = BillingEntitlementEngine.runFinancialReconciliation();
      expect(rec.reconciliationId).toBeDefined();
      expect(rec.status).toBe('BALANCED');
      expect(rec.totalTransactionsAudited).toBeGreaterThanOrEqual(4);
      expect(rec.settledSumUsd).toBeGreaterThan(0);
      expect(rec.discrepanciesCount).toBe(0);

      const history = BillingEntitlementEngine.getReconciliationHistory();
      expect(history.length).toBeGreaterThanOrEqual(1);
    });
  });
});
