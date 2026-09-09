-- ====================================================================
-- Migration 008: Master Database Architecture (Part 03 Specification)
-- Project: IBO Binary Option Trading Signals
-- Compliance: 3NF, UUID Primary Keys, High-Precision NUMERIC(18,8),
--             Secret References (Zero Raw Secrets), Audit Traceability
-- ====================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. LANGUAGES
CREATE TABLE IF NOT EXISTS languages (
    code VARCHAR(10) PRIMARY KEY,
    name_native VARCHAR(100) NOT NULL,
    direction VARCHAR(10) NOT NULL CHECK (direction IN ('rtl', 'ltr')),
    is_active BOOLEAN DEFAULT TRUE
);

-- 2. USERS
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_or_email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'analyst', 'admin')),
    preferred_language VARCHAR(10) DEFAULT 'fa' REFERENCES languages(code) ON UPDATE CASCADE ON DELETE SET NULL,
    country_code VARCHAR(10),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_phone_or_email ON users(phone_or_email);

-- 3. PLANS
CREATE TABLE IF NOT EXISTS plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    duration_days INTEGER NOT NULL CHECK (duration_days > 0),
    price_irr NUMERIC(18, 8) NOT NULL CHECK (price_irr >= 0),
    price_usd_equivalent NUMERIC(18, 8) NOT NULL CHECK (price_usd_equivalent >= 0),
    is_installment BOOLEAN DEFAULT FALSE,
    installment_count INTEGER DEFAULT 1 CHECK (installment_count >= 1)
);

-- 4. SUBSCRIPTIONS
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES plans(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('active', 'expired', 'cancelled')),
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_method VARCHAR(50) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);
CREATE INDEX IF NOT EXISTS idx_subscriptions_end_at ON subscriptions(end_at);

-- 5. ORDERS
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES plans(id) ON DELETE RESTRICT,
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN (
        'myket', 'bazaar', 'iranapps', 'card_to_card', 'shaba', 
        'nowpayments', 'googleplay_pending', 'digipay', 'snappay', 'other_pending'
    )),
    status VARCHAR(40) NOT NULL CHECK (status IN (
        'pending', 'awaiting_manual_review', 'processing', 'paid', 'failed'
    )),
    amount NUMERIC(18, 8) NOT NULL CHECK (amount >= 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'IRR',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- 6. PAYMENT RECEIPTS (FOR CARD TO CARD MANUAL VERIFICATION)
CREATE TABLE IF NOT EXISTS payment_receipts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    file_path VARCHAR(500) NOT NULL,
    tracking_code VARCHAR(100) NOT NULL,
    reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    review_status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (review_status IN ('pending', 'approved', 'rejected')),
    reviewed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_payment_receipts_order ON payment_receipts(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_receipts_status ON payment_receipts(review_status);

-- 7. INSTALLMENTS (FOR DIGIPAY / SNAPPAY BNPL)
CREATE TABLE IF NOT EXISTS installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    installment_number INTEGER NOT NULL CHECK (installment_number > 0),
    due_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'paid', 'overdue')),
    amount NUMERIC(18, 8) NOT NULL CHECK (amount >= 0),
    paid_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_installments_order ON installments(order_id);
CREATE INDEX IF NOT EXISTS idx_installments_due_status ON installments(due_date, status);

-- 8. WEBHOOKS LOG (FOR ALL INTEGRATION PLATFORMS)
CREATE TABLE IF NOT EXISTS webhooks_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source VARCHAR(30) NOT NULL CHECK (source IN (
        'myket', 'bazaar', 'iranapps', 'nowpayments', 'digipay', 'snappay', 'other'
    )),
    raw_payload JSONB NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_webhooks_log_source_processed ON webhooks_log(source, processed);

-- 9. ANALYSTS
CREATE TABLE IF NOT EXISTS analysts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    display_name VARCHAR(100) NOT NULL,
    verified BOOLEAN DEFAULT FALSE
);

-- 10. SIGNALS
CREATE TABLE IF NOT EXISTS signals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    analyst_id UUID NOT NULL REFERENCES analysts(id) ON DELETE RESTRICT,
    symbol VARCHAR(30) NOT NULL,
    option_type VARCHAR(10) NOT NULL CHECK (option_type IN ('call', 'put')),
    entry_reference NUMERIC(18, 8) NOT NULL,
    strike_reference NUMERIC(18, 8) NOT NULL,
    expiry_at TIMESTAMP WITH TIME ZONE NOT NULL,
    payout_rate NUMERIC(5, 2) NOT NULL CHECK (payout_rate >= 0),
    confidence_level NUMERIC(5, 2) NOT NULL CHECK (confidence_level >= 0 AND confidence_level <= 100),
    ai_review_status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (ai_review_status IN ('approved', 'rejected', 'pending')),
    published_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_signals_status_expires ON signals(ai_review_status, expires_at);
CREATE INDEX IF NOT EXISTS idx_signals_symbol ON signals(symbol);

-- 11. SIGNAL DELIVERIES (TRACEABILITY PER USER)
CREATE TABLE IF NOT EXISTS signal_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    signal_id UUID NOT NULL REFERENCES signals(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    delivered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_signal_deliveries_signal_user ON signal_deliveries(signal_id, user_id);

-- 12. TRANSLATIONS
CREATE TABLE IF NOT EXISTS translations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(30) NOT NULL CHECK (entity_type IN ('ui_string', 'news', 'journal')),
    entity_id VARCHAR(100) NOT NULL,
    language_code VARCHAR(10) NOT NULL REFERENCES languages(code) ON UPDATE CASCADE ON DELETE CASCADE,
    content TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_translations_lookup ON translations(entity_type, entity_id, language_code);

-- 13. MEDIA ASSETS (GENERATED BY AI AGENTS)
CREATE TABLE IF NOT EXISTS media_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(10) NOT NULL CHECK (type IN ('image', 'video')),
    purpose VARCHAR(20) NOT NULL CHECK (purpose IN ('news', 'journal', 'marketing')),
    source_url VARCHAR(500) NOT NULL,
    has_watermark BOOLEAN DEFAULT FALSE,
    has_logo BOOLEAN DEFAULT FALSE,
    created_by_agent VARCHAR(100) NOT NULL,
    style_reference_id UUID REFERENCES media_assets(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_media_assets_purpose ON media_assets(purpose);

-- 14. ADMIN SETTINGS (POINTERS/REFERENCES TO SECRETS VAULT ONLY)
CREATE TABLE IF NOT EXISTS admin_settings (
    key_name VARCHAR(100) PRIMARY KEY,
    secret_ref VARCHAR(255) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 15. AUDIT LOGS (IMMUTABLE AUDIT TRAIL FOR ADMIN & AI AGENTS)
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor VARCHAR(20) NOT NULL CHECK (actor IN ('human_admin', 'ai_agent')),
    agent_name VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(100) NOT NULL,
    before_state JSONB,
    after_state JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor_created ON audit_logs(actor, created_at);
CREATE INDEX IF NOT EXISTS idx_audit_logs_target ON audit_logs(target_entity);
