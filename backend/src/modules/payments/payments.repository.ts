import { pool, query } from '../../config/database';
import { SubscriptionsRepository } from '../subscriptions/subscriptions.repository';

export interface TransactionDbEntity {
  id: string;
  user_id: string;
  plan_id: string;
  amount: number;
  currency: string;
  payment_method: string;
  external_reference: string;
  status: string;
  created_at: Date;
  updated_at: Date;
}

export interface PurchaseIntentDbEntity {
  id: string;
  user_id: string;
  plan_id: string;
  quoted_amount: number;
  currency: string;
  payment_method: string;
  status: string;
  idempotency_key: string;
  expires_at: Date;
  created_at: Date;
  updated_at: Date;
}

export interface RefundDbEntity {
  id: string;
  transaction_id: string;
  amount: number;
  status: string;
  reason: string | null;
  created_at: Date;
}

export class PaymentsRepository {
  static async createTransaction(
    userId: string,
    planId: string,
    amount: number,
    paymentMethod: string,
    externalReference: string
  ): Promise<TransactionDbEntity> {
    const result = await query(
      `INSERT INTO payment_transactions (user_id, plan_id, amount, payment_method, external_reference, status) 
       VALUES ($1, $2, $3, $4, $5, 'PENDING') 
       RETURNING id, user_id, plan_id, amount, currency, payment_method, external_reference, status, created_at, updated_at`,
      [userId, planId, amount, paymentMethod, externalReference]
    );
    return {
      ...result.rows[0],
      amount: parseFloat(result.rows[0].amount),
    };
  }

  static async findTransactionById(txId: string): Promise<TransactionDbEntity | null> {
    const result = await query('SELECT * FROM payment_transactions WHERE id = $1', [txId]);
    if (!result.rowCount || result.rowCount === 0) return null;
    return {
      ...result.rows[0],
      amount: parseFloat(result.rows[0].amount),
    };
  }

  static async findTransactionByRef(ref: string): Promise<TransactionDbEntity | null> {
    const result = await query('SELECT * FROM payment_transactions WHERE external_reference = $1', [ref]);
    if (!result.rowCount || result.rowCount === 0) return null;
    return {
      ...result.rows[0],
      amount: parseFloat(result.rows[0].amount),
    };
  }

  // ATOMIC DATABASE TRANSACTION FOR RELIABLE VERIFICATION & SUBSCRIPTION CREATION
  static async verifyAndActivateTransaction(
    txId: string,
    verificationSource: string,
    metadata: any = {}
  ): Promise<boolean> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');

      // 1. Lock and retrieve transaction
      const txResult = await client.query(
        'SELECT * FROM payment_transactions WHERE id = $1 FOR UPDATE',
        [txId]
      );
      if (!txResult.rowCount || txResult.rowCount === 0) {
        throw new Error('Transaction not found');
      }

      const tx = txResult.rows[0];
      if (tx.status !== 'PENDING') {
        // Idempotency: Already processed! Avoid creating multiple entitlements.
        await client.query('COMMIT');
        return true;
      }

      // 2. Fetch the plan duration
      const planResult = await client.query(
        'SELECT duration_days FROM subscription_plans WHERE id = $1',
        [tx.plan_id]
      );
      const durationDays = planResult.rows[0].duration_days;

      // 3. Update Transaction status
      await client.query(
        'UPDATE payment_transactions SET status = \'SUCCESS\', updated_at = CURRENT_TIMESTAMP WHERE id = $1',
        [txId]
      );

      // 4. Record the Payment Verification Event
      await client.query(
        'INSERT INTO payment_events (transaction_id, event_type, payload) VALUES ($1, $2, $3)',
        [txId, verificationSource, JSON.stringify(metadata)]
      );

      // 5. Create active user subscription
      const expiresAt = new Date();
      expiresAt.setDate(expiresAt.getDate() + durationDays);

      await client.query(
        `INSERT INTO subscriptions (user_id, plan_id, status, expires_at) 
         VALUES ($1, $2, 'ACTIVE', $3)`,
        [tx.user_id, tx.plan_id, expiresAt]
      );

      // 6. Generate active premium entitlements
      await client.query(
        `INSERT INTO entitlements (user_id, feature_key, expires_at) 
         VALUES ($1, 'premium_signals', $2), ($1, 'premium_news', $2)
         ON CONFLICT DO NOTHING`,
        [tx.user_id, expiresAt]
      );

      // 7. Write Structured Audit Log
      await client.query(
        `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
         VALUES ($1, 'SYSTEM', 'TRANSACTION_VERIFIED_AND_SUBSCRIBED', 'payment_transactions', $2, 'SUCCESS')`,
        [tx.user_id, txId]
      );

      await client.query('COMMIT');
      return true;
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }

  // --- NEW PURCHASE INTENT OPERATIONS ---

  static async createPurchaseIntent(
    userId: string,
    planId: string,
    quotedAmount: number,
    currency: string,
    paymentMethod: string,
    idempotencyKey: string,
    expiresAt: Date
  ): Promise<PurchaseIntentDbEntity> {
    const result = await query(
      `INSERT INTO purchase_intents (user_id, plan_id, quoted_amount, currency, payment_method, idempotency_key, expires_at, status) 
       VALUES ($1, $2, $3, $4, $5, $6, $7, 'CREATED') 
       RETURNING id, user_id, plan_id, quoted_amount, currency, payment_method, status, idempotency_key, expires_at, created_at, updated_at`,
      [userId, planId, quotedAmount, currency, paymentMethod, idempotencyKey, expiresAt]
    );
    return {
      ...result.rows[0],
      quoted_amount: parseFloat(result.rows[0].quoted_amount),
    };
  }

  static async findPurchaseIntentById(intentId: string): Promise<PurchaseIntentDbEntity | null> {
    const result = await query('SELECT * FROM purchase_intents WHERE id = $1', [intentId]);
    if (!result.rowCount || result.rowCount === 0) return null;
    return {
      ...result.rows[0],
      quoted_amount: parseFloat(result.rows[0].quoted_amount),
    };
  }

  static async findPurchaseIntentByIdempotencyKey(key: string): Promise<PurchaseIntentDbEntity | null> {
    const result = await query('SELECT * FROM purchase_intents WHERE idempotency_key = $1', [key]);
    if (!result.rowCount || result.rowCount === 0) return null;
    return {
      ...result.rows[0],
      quoted_amount: parseFloat(result.rows[0].quoted_amount),
    };
  }

  static async updatePurchaseIntentStatus(intentId: string, status: string): Promise<void> {
    await query(
      `UPDATE purchase_intents SET status = $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2`,
      [status, intentId]
    );
  }

  // --- NEW REFUND OPERATIONS ---

  static async issueRefund(
    transactionId: string,
    amount: number,
    reason: string,
    actorId: string | null
  ): Promise<RefundDbEntity> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');

      // 1. Lock and check if transaction exists and is successful
      const txResult = await client.query(
        'SELECT * FROM payment_transactions WHERE id = $1 FOR UPDATE',
        [transactionId]
      );
      if (!txResult.rowCount || txResult.rowCount === 0) {
        throw new Error('Transaction not found');
      }

      const tx = txResult.rows[0];
      if (tx.status !== 'SUCCESS') {
        throw new Error('Can only refund successful transactions');
      }

      // 2. Insert Refund Record
      const refundResult = await client.query(
        `INSERT INTO refunds (transaction_id, amount, status, reason) 
         VALUES ($1, $2, 'SUCCESS', $3) 
         RETURNING id, transaction_id, amount, status, reason, created_at`,
        [transactionId, amount, reason]
      );

      // 3. Mark User Subscriptions as EXPIRED or REVOKED
      await client.query(
        `UPDATE subscriptions SET status = 'REVOKED', updated_at = CURRENT_TIMESTAMP 
         WHERE user_id = $1 AND plan_id = $2 AND status = 'ACTIVE'`,
        [tx.user_id, tx.plan_id]
      );

      // 4. Revoke active premium entitlements
      await client.query(
        `DELETE FROM entitlements WHERE user_id = $1 AND feature_key IN ('premium_signals', 'premium_news')`,
        [tx.user_id]
      );

      // 5. Update parent transaction state
      await client.query(
        `UPDATE payment_transactions SET status = 'REFUNDED', updated_at = CURRENT_TIMESTAMP WHERE id = $1`,
        [transactionId]
      );

      // 6. Write Audit Log
      await client.query(
        `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result, metadata) 
         VALUES ($1, 'ADMIN', 'SUBSCRIPTION_REFUNDED', 'payment_transactions', $2, 'SUCCESS', $3)`,
        [actorId, transactionId, JSON.stringify({ amount, reason })]
      );

      await client.query('COMMIT');
      return {
        ...refundResult.rows[0],
        amount: parseFloat(refundResult.rows[0].amount),
      };
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }

  // --- CANCEL SUBSCRIPTION OPERATION ---

  static async cancelSubscription(subscriptionId: string, userId: string): Promise<boolean> {
    const result = await query(
      `UPDATE subscriptions SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP 
       WHERE id = $1 AND user_id = $2 AND status = 'ACTIVE'`,
      [subscriptionId, userId]
    );

    if (result.rowCount && result.rowCount > 0) {
      // Record in audit log
      await query(
        `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
         VALUES ($1, 'USER', 'SUBSCRIPTION_CANCELLED', 'subscriptions', $2, 'SUCCESS')`,
        [userId, subscriptionId]
      );
      return true;
    }
    return false;
  }
}

