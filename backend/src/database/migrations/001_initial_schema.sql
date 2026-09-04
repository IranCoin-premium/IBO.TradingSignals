-- ==============================================================================
-- IBO TRADING SIGNALS - INITIAL DATABASE SCHEMA MIGRATION
-- ==============================================================================

-- Enable UUID extension if not enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. IDENTITY & AUTHENTICATION DOMAIN
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(100),
    account_status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, PENDING
    locale VARCHAR(10) DEFAULT 'fa', -- 'fa', 'en', etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS password_hashes (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(50) PRIMARY KEY,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) REFERENCES roles(name) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role)
);

-- Seed Default Roles
INSERT INTO roles (name, description) VALUES
('USER', 'Regular trading platform user'),
('STAFF', 'Signals provider and support operator'),
('ADMIN', 'Full system administrator'),
('SUPER_ADMIN', 'Root administrative owner'),
('SERVICE_AGENT', 'Automated AI service scheduler')
ON CONFLICT (name) DO NOTHING;

-- 2. SUBSCRIPTIONS & PLANS DOMAIN
CREATE TABLE IF NOT EXISTS subscription_plans (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    duration_days INTEGER NOT NULL DEFAULT 30,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id VARCHAR(50) NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, EXPIRED, CANCELLED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS entitlements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    feature_key VARCHAR(100) NOT NULL, -- e.g. "premium_signals", "premium_news"
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Seed Initial Subscription Plans
INSERT INTO subscription_plans (id, title, price, duration_days, description) VALUES
('FREE', 'پلن رایگان سیگنال‌ها', 0.00, 3650, 'سیگنال‌های رایگان و بخش آموزش همگانی'),
('BRONZE', 'پلن برنزی ۳۰ روزه', 15.00, 30, 'سیگنال‌های وی‌آی‌پی با ضریب سوددهی مناسب'),
('SILVER', 'پلن نقره‌ای ۹۰ روزه', 40.00, 90, 'دسترسی همزمان به سیگنال‌ها، ژورنال و اخبار تحلیل بازارهای باینری'),
('GOLD', 'پلن طلایی ۱۸۰ روزه', 75.00, 180, 'کل خدمات پریمیوم به همراه آنالیز ریسک به صورت تمام‌وقت')
ON CONFLICT (id) DO NOTHING;

-- 3. PAYMENTS & TRANSACTIONS DOMAIN
CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id VARCHAR(50) NOT NULL REFERENCES subscription_plans(id),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    payment_method VARCHAR(50), -- e.g. "USDT_TRC20", "CARD_FALLBACK"
    external_reference VARCHAR(255) UNIQUE, -- transaction hash or unique provider tag
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID REFERENCES payment_transactions(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL, -- e.g. "USDT_CONFIRMED", "ADMIN_OVERRIDE"
    payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. SIGNALS & CONTENT METADATA DOMAIN
CREATE TABLE IF NOT EXISTS signals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(100) NOT NULL,
    asset VARCHAR(50) NOT NULL, -- e.g. "EUR/USD", "BTC/USDT"
    entry_price DECIMAL(18, 8) NOT NULL,
    current_price DECIMAL(18, 8),
    direction VARCHAR(10) NOT NULL, -- "CALL" or "PUT"
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, WON, LOST, VETOED
    rationale TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS news_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    body TEXT NOT NULL,
    image_url VARCHAR(255),
    premium BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. AUDIT TRAILS & JOBS SYSTEM
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id UUID, -- References users(id) but NULLable for system/agent events
    actor_type VARCHAR(50) NOT NULL, -- "USER", "SYSTEM", "AI_AGENT"
    action VARCHAR(100) NOT NULL, -- "LOGIN", "PAYMENT_RECEIVED", "SIGNAL_PUBLISHED", "ROLE_UPDATED"
    resource_type VARCHAR(100) NOT NULL, -- "users", "signals", "subscriptions"
    resource_id VARCHAR(100),
    result VARCHAR(50) DEFAULT 'SUCCESS', -- SUCCESS, FAILURE
    request_id VARCHAR(100), -- correlation ID
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for optimal performance
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_entitlements_user ON entitlements(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_ref ON payment_transactions(external_reference);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_signals_status ON signals(status);
