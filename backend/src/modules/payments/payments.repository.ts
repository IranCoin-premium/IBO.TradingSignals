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

  // --- PART 04: PAYMENT GATEWAY INTEGRATIONS ---

  static async submitPaymentReceipt(orderId: string, filePath: string, trackingCode: string): Promise<any> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');
      const receiptRes = await client.query(
        `INSERT INTO payment_receipts (order_id, file_path, tracking_code, review_status)
         VALUES ($1, $2, $3, 'pending')
         RETURNING *`,
        [orderId, filePath, trackingCode]
      );

      await client.query(
        `UPDATE orders SET status = 'awaiting_manual_review', updated_at = CURRENT_TIMESTAMP WHERE id = $1`,
        [orderId]
      );

      await client.query(
        `INSERT INTO audit_logs (actor, action, target_entity, after_state)
         VALUES ('human_admin', 'RECEIPT_SUBMITTED', 'orders', $1)`,
        [JSON.stringify({ orderId, trackingCode, filePath })]
      );

      await client.query('COMMIT');
      return receiptRes.rows[0];
    } catch (err) {
      await client.query('ROLLBACK');
      throw err;
    } finally {
      client.release();
    }
  }

  static async reviewPaymentReceipt(
    receiptId: string,
    reviewedBy: string,
    reviewStatus: 'approved' | 'rejected',
    reason?: string
  ): Promise<any> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');

      const receiptRes = await client.query(
        `SELECT r.*, o.user_id, o.plan_id, o.amount FROM payment_receipts r
         JOIN orders o ON r.order_id = o.id
         WHERE r.id = $1 FOR UPDATE`,
        [receiptId]
      );

      if (!receiptRes.rowCount || receiptRes.rowCount === 0) {
        throw new Error('Receipt not found');
      }

      const receipt = receiptRes.rows[0];

      await client.query(
        `UPDATE payment_receipts 
         SET review_status = $1, reviewed_by = $2, reviewed_at = CURRENT_TIMESTAMP
         WHERE id = $3`,
        [reviewStatus, reviewedBy, receiptId]
      );

      if (reviewStatus === 'approved') {
        // Mark Order as paid
        await client.query(
          `UPDATE orders SET status = 'paid', updated_at = CURRENT_TIMESTAMP WHERE id = $1`,
          [receipt.order_id]
        );

        // Fetch Plan details to calculate subscription duration
        const planRes = await client.query('SELECT duration_days FROM plans WHERE id = $1', [receipt.plan_id]);
        const durationDays = planRes.rows[0]?.duration_days || 30;

        const startAt = new Date();
        const endAt = new Date();
        endAt.setDate(endAt.getDate() + durationDays);

        // Activate Subscription
        await client.query(
          `INSERT INTO subscriptions (user_id, plan_id, status, start_at, end_at, payment_method)
           VALUES ($1, $2, 'active', $3, $4, 'card_to_card')`,
          [receipt.user_id, receipt.plan_id, startAt, endAt]
        );

        // Audit log
        await client.query(
          `INSERT INTO audit_logs (actor, action, target_entity, after_state)
           VALUES ('human_admin', 'RECEIPT_APPROVED', 'orders', $1)`,
          [JSON.stringify({ orderId: receipt.order_id, receiptId, status: 'PAID' })]
        );
      } else {
        await client.query(
          `UPDATE orders SET status = 'failed', updated_at = CURRENT_TIMESTAMP WHERE id = $1`,
          [receipt.order_id]
        );

        await client.query(
          `INSERT INTO audit_logs (actor, action, target_entity, after_state)
           VALUES ('human_admin', 'RECEIPT_REJECTED', 'orders', $1)`,
          [JSON.stringify({ orderId: receipt.order_id, receiptId, status: 'FAILED', reason })]
        );
      }

      await client.query('COMMIT');
      return { receiptId, reviewStatus, orderId: receipt.order_id };
    } catch (err) {
      await client.query('ROLLBACK');
      throw err;
    } finally {
      client.release();
    }
  }

  static async createInstallmentPlan(
    orderId: string,
    totalAmount: number,
    installmentCount: number = 4
  ): Promise<any[]> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');
      const installmentAmount = (totalAmount / installmentCount).toFixed(8);
      const createdInstallments = [];

      for (let i = 1; i <= installmentCount; i++) {
        const dueDate = new Date();
        dueDate.setMonth(dueDate.getMonth() + (i - 1));

        const res = await client.query(
          `INSERT INTO installments (order_id, installment_number, due_date, status, amount)
           VALUES ($1, $2, $3, $4, $5)
           RETURNING *`,
          [orderId, i, dueDate, i === 1 ? 'pending' : 'pending', installmentAmount]
        );
        createdInstallments.push(res.rows[0]);
      }

      await client.query('COMMIT');
      return createdInstallments;
    } catch (err) {
      await client.query('ROLLBACK');
      throw err;
    } finally {
      client.release();
    }
  }

  static async markInstallmentPaid(
    orderId: string,
    installmentNumber: number
  ): Promise<boolean> {
    const client = await pool.connect();
    try {
      await client.query('BEGIN');

      const res = await client.query(
        `UPDATE installments SET status = 'paid', paid_at = CURRENT_TIMESTAMP
         WHERE order_id = $1 AND installment_number = $2
         RETURNING *`,
        [orderId, installmentNumber]
      );

      if (installmentNumber === 1 && res.rowCount && res.rowCount > 0) {
        // First installment paid -> Activate subscription
        await client.query(
          `UPDATE orders SET status = 'paid', updated_at = CURRENT_TIMESTAMP WHERE id = $1`,
          [orderId]
        );

        const orderRes = await client.query(
          `SELECT o.user_id, o.plan_id, o.payment_method, p.duration_days 
           FROM orders o JOIN plans p ON o.plan_id = p.id WHERE o.id = $1`,
          [orderId]
        );

        if (orderRes.rowCount && orderRes.rowCount > 0) {
          const row = orderRes.rows[0];
          const startAt = new Date();
          const endAt = new Date();
          endAt.setDate(endAt.getDate() + (row.duration_days || 30));

          await client.query(
            `INSERT INTO subscriptions (user_id, plan_id, status, start_at, end_at, payment_method)
             VALUES ($1, $2, 'active', $3, $4, $5)`,
            [row.user_id, row.plan_id, startAt, endAt, row.payment_method]
          );
        }
      }

      await client.query('COMMIT');
      return true;
    } catch (err) {
      await client.query('ROLLBACK');
      throw err;
    } finally {
      client.release();
    }
  }

  static async logWebhook(source: string, payload: any, processed: boolean = false): Promise<void> {
    await query(
      `INSERT INTO webhooks_log (source, raw_payload, processed)
       VALUES ($1, $2, $3)`,
      [source, JSON.stringify(payload), processed]
    );
  }
}

