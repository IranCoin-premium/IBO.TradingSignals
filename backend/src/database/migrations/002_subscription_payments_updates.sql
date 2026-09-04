-- ==============================================================================
-- IBO TRADING SIGNALS - SUBSCRIPTIONS & PAYMENTS UPDATES MIGRATION
-- ==============================================================================

-- 1. PURCHASE INTENTS TABLE
CREATE TABLE IF NOT EXISTS purchase_intents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id VARCHAR(50) NOT NULL REFERENCES subscription_plans(id),
    quoted_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    payment_method VARCHAR(50) NOT NULL, -- TETHER, SHETAB, ZARINPAL
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED', -- CREATED, PENDING_PAYMENT, PAYMENT_PROCESSING, PAID, FAILED, EXPIRED, CANCELLED
    idempotency_key VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. REFUNDS TABLE
CREATE TABLE IF NOT EXISTS refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES payment_transactions(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_purchase_intents_user ON purchase_intents(user_id);
CREATE INDEX IF NOT EXISTS idx_purchase_intents_idempotency ON purchase_intents(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_refunds_transaction ON refunds(transaction_id);
