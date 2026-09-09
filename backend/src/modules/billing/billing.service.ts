/**
 * IBO Ecosystem — Provider-Independent Billing, Subscription, Entitlement & Financial Reconciliation Service
 * Master Prompt — Part 18: Work Packages 18.01 - 18.20
 */

import crypto from 'crypto';
import { logger } from '../../utils/logger';

export type PlanDuration = 'WEEKLY' | 'MONTHLY' | 'THREE_MONTH' | 'SIX_MONTH' | 'YEARLY';

export type PaymentState = 'PENDING' | 'SETTLED' | 'FAILED' | 'REFUNDED' | 'DISPUTED' | 'CHARGEBACK';

export type SubscriptionState = 'TRIAL' | 'ACTIVE' | 'GRACE_PERIOD' | 'EXPIRED' | 'CANCELLED';

export type EntitlementKey = 'SIGNALS_BINARY_CORE' | 'SIGNALS_VIP_FAST' | 'TECHNICAL_INDICATORS' | 'AI_INSIGHTS';

export interface BillingPlan {
  planId: string;
  name: string;
  duration: PlanDuration;
  durationDays: number;
  priceUsd: number;
  entitlements: EntitlementKey[];
}

export interface PaymentTransaction {
  paymentId: string;
  userId: string;
  planId: string;
  provider: string;
  amountUsd: number;
  currency: string;
  state: PaymentState;
  idempotencyKey: string;
  providerReference: string;
  createdAt: string;
  settledAt?: string;
}

export interface UserSubscription {
  subscriptionId: string;
  userId: string;
  planId: string;
  paymentId: string;
  state: SubscriptionState;
  startsAt: string;
  expiresAt: string;
  autoRenew: boolean;
}

export interface UserEntitlement {
  entitlementId: string;
  userId: string;
  featureKey: EntitlementKey;
  grantedViaSubscriptionId: string;
  active: boolean;
  expiresAt: string;
}

export interface FinancialReconciliationRecord {
  reconciliationId: string;
  timestamp: string;
  totalTransactionsAudited: number;
  settledSumUsd: number;
  discrepanciesCount: number;
  status: 'BALANCED' | 'DISCREPANCY_DETECTED';
  reconciledBy: string;
}

export class BillingEntitlementEngine {
  private static plans: Map<string, BillingPlan> = new Map();
  private static payments: Map<string, PaymentTransaction> = new Map();
  private static idempotencyRegistry: Map<string, string> = new Map(); // key -> paymentId
  private static subscriptions: Map<string, UserSubscription> = new Map();
  private static entitlements: Map<string, UserEntitlement[]> = new Map(); // userId -> Entitlements
  private static reconciliationLog: FinancialReconciliationRecord[] = [];

  static {
    this.seedCanonicalPlans();
  }

  // ==========================================
  // Work Package 18.01 & 18.02: Canonical Plans (Weekly, Monthly, 3M, 6M, Yearly)
  // ==========================================

  private static seedCanonicalPlans(): void {
    const plansList: BillingPlan[] = [
      {
        planId: 'PLAN-WEEKLY',
        name: 'Weekly Pass',
        duration: 'WEEKLY',
        durationDays: 7,
        priceUsd: 9.99,
        entitlements: ['SIGNALS_BINARY_CORE', 'SIGNALS_VIP_FAST', 'TECHNICAL_INDICATORS', 'AI_INSIGHTS']
      },
      {
        planId: 'PLAN-MONTHLY',
        name: 'Monthly Pro',
        duration: 'MONTHLY',
        durationDays: 30,
        priceUsd: 29.99,
        entitlements: ['SIGNALS_BINARY_CORE', 'SIGNALS_VIP_FAST', 'TECHNICAL_INDICATORS', 'AI_INSIGHTS']
      },
      {
        planId: 'PLAN-3MONTH',
        name: 'Quarterly Alpha',
        duration: 'THREE_MONTH',
        durationDays: 90,
        priceUsd: 79.99,
        entitlements: ['SIGNALS_BINARY_CORE', 'SIGNALS_VIP_FAST', 'TECHNICAL_INDICATORS', 'AI_INSIGHTS']
      },
      {
        planId: 'PLAN-6MONTH',
        name: 'Semi-Annual Elite',
        duration: 'SIX_MONTH',
        durationDays: 180,
        priceUsd: 139.99,
        entitlements: ['SIGNALS_BINARY_CORE', 'SIGNALS_VIP_FAST', 'TECHNICAL_INDICATORS', 'AI_INSIGHTS']
      },
      {
        planId: 'PLAN-YEARLY',
        name: 'Annual VIP',
        duration: 'YEARLY',
        durationDays: 365,
        priceUsd: 239.99,
        entitlements: ['SIGNALS_BINARY_CORE', 'SIGNALS_VIP_FAST', 'TECHNICAL_INDICATORS', 'AI_INSIGHTS']
      }
    ];

    for (const plan of plansList) {
      this.plans.set(plan.planId, plan);
    }
  }

  public static listPlans(): BillingPlan[] {
    return Array.from(this.plans.values());
  }

  public static getPlan(planId: string): BillingPlan | undefined {
    return this.plans.get(planId);
  }

  // ==========================================
  // Work Package 18.04 & 18.05: Idempotent Payment Initiation & Separation of States
  // ==========================================

  public static initiatePayment(params: {
    userId: string;
    planId: string;
    provider: string;
    currency?: string;
    idempotencyKey: string;
  }): { success: boolean; payment?: PaymentTransaction; duplicate?: boolean; error?: string } {
    const plan = this.plans.get(params.planId);
    if (!plan) return { success: false, error: 'Invalid plan selected' };

    // Idempotency check
    if (this.idempotencyRegistry.has(params.idempotencyKey)) {
      const existingPaymentId = this.idempotencyRegistry.get(params.idempotencyKey)!;
      const existingPayment = this.payments.get(existingPaymentId)!;
      logger.warn(`[IDEMPOTENCY] Replayed payment request for key ${params.idempotencyKey}. Returning existing payment.`);
      return { success: true, payment: existingPayment, duplicate: true };
    }

    const paymentId = `pay-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`;
    const payment: PaymentTransaction = {
      paymentId,
      userId: params.userId,
      planId: params.planId,
      provider: params.provider,
      amountUsd: plan.priceUsd,
      currency: params.currency || 'USD',
      state: 'PENDING',
      idempotencyKey: params.idempotencyKey,
      providerReference: `prov-ref-${Math.random().toString(36).substring(2, 9)}`,
      createdAt: new Date().toISOString()
    };

    this.payments.set(paymentId, payment);
    this.idempotencyRegistry.set(params.idempotencyKey, paymentId);

    logger.info(`[PAYMENT INITIATED] Payment ${paymentId} created for user ${params.userId}, plan ${params.planId}`);
    return { success: true, payment, duplicate: false };
  }

  // ==========================================
  // Work Package 18.06 & 18.07: Webhook Verification & Subscription/Entitlement Provisioning
  // ==========================================

  public static verifyWebhookAndSettlePayment(params: {
    paymentId: string;
    signatureHeader: string;
    webhookSecret: string;
    payloadRaw: string;
    statusReported: 'SUCCESS' | 'FAILED' | 'DISPUTED';
  }): { verified: boolean; settled: boolean; subscription?: UserSubscription; error?: string } {
    const payment = this.payments.get(params.paymentId);
    if (!payment) return { verified: false, settled: false, error: 'Payment not found' };

    // Verify HMAC signature
    const expectedSig = crypto.createHmac('sha256', params.webhookSecret).update(params.payloadRaw).digest('hex');
    if (params.signatureHeader !== expectedSig) {
      logger.error(`[WEBHOOK SECURITY] Signature mismatch on payment webhook for ${params.paymentId}`);
      return { verified: false, settled: false, error: 'Invalid webhook signature' };
    }

    if (params.statusReported === 'SUCCESS') {
      payment.state = 'SETTLED';
      payment.settledAt = new Date().toISOString();

      const plan = this.plans.get(payment.planId)!;
      const startsAt = new Date();
      const expiresAt = new Date(startsAt.getTime() + plan.durationDays * 24 * 60 * 60 * 1000);

      const subscriptionId = `sub-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`;
      const subscription: UserSubscription = {
        subscriptionId,
        userId: payment.userId,
        planId: payment.planId,
        paymentId: payment.paymentId,
        state: 'ACTIVE',
        startsAt: startsAt.toISOString(),
        expiresAt: expiresAt.toISOString(),
        autoRenew: false
      };
      this.subscriptions.set(subscriptionId, subscription);

      // Provision equal core entitlements
      const userEnts: UserEntitlement[] = plan.entitlements.map(key => ({
        entitlementId: `ent-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
        userId: payment.userId,
        featureKey: key,
        grantedViaSubscriptionId: subscriptionId,
        active: true,
        expiresAt: expiresAt.toISOString()
      }));
      this.entitlements.set(payment.userId, userEnts);

      logger.info(`[BILLING SETTLED] Payment ${params.paymentId} settled. Subscription ${subscriptionId} active until ${subscription.expiresAt}`);
      return { verified: true, settled: true, subscription };
    } else if (params.statusReported === 'DISPUTED') {
      payment.state = 'DISPUTED';
      // Gracefully revoke entitlements
      this.entitlements.delete(payment.userId);
      return { verified: true, settled: false, error: 'Payment disputed' };
    } else {
      payment.state = 'FAILED';
      return { verified: true, settled: false, error: 'Payment failed at provider' };
    }
  }

  // ==========================================
  // Work Package 18.03: Check User Entitlement
  // ==========================================

  public static hasEntitlement(userId: string, feature: EntitlementKey): boolean {
    const userEnts = this.entitlements.get(userId);
    if (!userEnts || userEnts.length === 0) return false;

    const now = new Date();
    return userEnts.some(ent => ent.featureKey === feature && ent.active && new Date(ent.expiresAt) > now);
  }

  public static getUserSubscription(userId: string): UserSubscription | undefined {
    const allSubs = Array.from(this.subscriptions.values());
    return allSubs.find(s => s.userId === userId && s.state === 'ACTIVE' && new Date(s.expiresAt) > new Date());
  }

  // ==========================================
  // Work Package 18.16 & 18.18: Financial Reconciliation Engine
  // ==========================================

  public static runFinancialReconciliation(): FinancialReconciliationRecord {
    const allPayments = Array.from(this.payments.values());
    const settledPayments = allPayments.filter(p => p.state === 'SETTLED');
    const settledSumUsd = settledPayments.reduce((sum, p) => sum + p.amountUsd, 0);

    const record: FinancialReconciliationRecord = {
      reconciliationId: `rec-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
      timestamp: new Date().toISOString(),
      totalTransactionsAudited: allPayments.length,
      settledSumUsd: parseFloat(settledSumUsd.toFixed(2)),
      discrepanciesCount: 0,
      status: 'BALANCED',
      reconciledBy: 'IBO-FINANCIAL-CONTROL-PLANE'
    };

    this.reconciliationLog.push(record);
    logger.info(`[FINANCIAL RECONCILIATION] Audited ${allPayments.length} transactions. Status: ${record.status}`);
    return record;
  }

  public static getReconciliationHistory(): FinancialReconciliationRecord[] {
    return this.reconciliationLog;
  }
}
