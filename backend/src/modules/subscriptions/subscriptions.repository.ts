import { query } from '../../config/database';

export interface PlanDbEntity {
  id: string;
  title: string;
  price: number;
  duration_days: number;
  description: string | null;
  created_at: Date;
}

export interface SubscriptionDbEntity {
  id: string;
  user_id: string;
  plan_id: string;
  status: string;
  created_at: Date;
  expires_at: Date;
  updated_at: Date;
}

export interface EntitlementDbEntity {
  id: string;
  user_id: string;
  feature_key: string;
  created_at: Date;
  expires_at: Date;
}

export class SubscriptionsRepository {
  static async getPlans(): Promise<PlanDbEntity[]> {
    const result = await query('SELECT id, title, price, duration_days, description, created_at FROM subscription_plans');
    return result.rows.map(row => ({
      ...row,
      price: parseFloat(row.price),
    }));
  }

  static async findPlanById(planId: string): Promise<PlanDbEntity | null> {
    const result = await query('SELECT id, title, price, duration_days, description, created_at FROM subscription_plans WHERE id = $1', [planId]);
    if (!result.rowCount || result.rowCount === 0) return null;
    return {
      ...result.rows[0],
      price: parseFloat(result.rows[0].price),
    };
  }

  static async getActiveSubscription(userId: string): Promise<SubscriptionDbEntity | null> {
    const result = await query(
      `SELECT id, user_id, plan_id, status, created_at, expires_at, updated_at 
       FROM subscriptions 
       WHERE user_id = $1 AND status = 'ACTIVE' AND expires_at > CURRENT_TIMESTAMP
       ORDER BY expires_at DESC LIMIT 1`,
      [userId]
    );
    return result.rowCount && result.rowCount > 0 ? result.rows[0] : null;
  }

  static async createSubscription(userId: string, planId: string, durationDays: number): Promise<SubscriptionDbEntity> {
    const expiresAt = new Date();
    expiresAt.setDate(expiresAt.getDate() + durationDays);

    const result = await query(
      `INSERT INTO subscriptions (user_id, plan_id, status, expires_at) 
       VALUES ($1, $2, 'ACTIVE', $3) 
       RETURNING id, user_id, plan_id, status, created_at, expires_at, updated_at`,
      [userId, planId, expiresAt]
    );

    // Seed Entitlement for signals/news
    await query(
      `INSERT INTO entitlements (user_id, feature_key, expires_at) 
       VALUES ($1, 'premium_signals', $2), ($1, 'premium_news', $2)
       ON CONFLICT DO NOTHING`,
      [userId, expiresAt]
    );

    return result.rows[0];
  }

  static async getUserEntitlements(userId: string): Promise<EntitlementDbEntity[]> {
    const result = await query(
      'SELECT id, user_id, feature_key, created_at, expires_at FROM entitlements WHERE user_id = $1 AND expires_at > CURRENT_TIMESTAMP',
      [userId]
    );
    return result.rows;
  }
}
