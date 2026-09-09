-- ====================================================================
-- Migration 014: Part 12 — Referral & Sales Partnership (server-authoritative)
-- + Platform Tab Registry (backend-first readiness for new client tabs)
-- Invariants: commissions computed ONLY in backend; ledger append-only;
-- payouts follow strict state machine pending→approved→paid (admin-only).
-- ====================================================================

-- 1. REFERRAL CODES (unique per owner)
CREATE TABLE IF NOT EXISTS referral_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(32) UNIQUE NOT NULL,
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ref_codes_owner ON referral_codes(owner_user_id);

-- 2. REFERRAL CLICKS (attribution funnel entry; spam-flagged)
CREATE TABLE IF NOT EXISTS referral_clicks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referral_code_id UUID NOT NULL REFERENCES referral_codes(id) ON DELETE CASCADE,
    visitor_fingerprint VARCHAR(128),
    ip_hash VARCHAR(64),
    utm_source VARCHAR(100),
    is_spam BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ref_clicks_code ON referral_clicks(referral_code_id, created_at);

-- 3. REFERRAL ATTRIBUTIONS (code → signup binding, one per user)
CREATE TABLE IF NOT EXISTS referral_attributions (
    referred_user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    referral_code_id UUID NOT NULL REFERENCES referral_codes(id) ON DELETE CASCADE,
    bound_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. PARTNER PROFILES (payout info + tier; one per user)
CREATE TABLE IF NOT EXISTS partner_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    display_name VARCHAR(100) NOT NULL,
    payout_method VARCHAR(30) NOT NULL DEFAULT 'crypto_usdt'
        CHECK (payout_method IN ('crypto_usdt', 'crypto_btc', 'bank_rial')),
    payout_details JSONB DEFAULT '{}'::jsonb,
    tier VARCHAR(20) NOT NULL DEFAULT 'standard'
        CHECK (tier IN ('standard', 'silver', 'gold', 'platinum')),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. COMMISSION RULES (tiered %, server-side source of truth)
CREATE TABLE IF NOT EXISTS commission_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tier VARCHAR(20) NOT NULL UNIQUE,
    percent NUMERIC(5,2) NOT NULL CHECK (percent >= 0 AND percent <= 90),
    min_payout_amount NUMERIC(18,8) NOT NULL DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. REFERRAL CONVERSIONS (attributed PAID events; idempotent per transaction)
CREATE TABLE IF NOT EXISTS referral_conversions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referral_code_id UUID NOT NULL REFERENCES referral_codes(id) ON DELETE CASCADE,
    referred_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id UUID,
    transaction_id UUID UNIQUE,
    plan_id UUID,
    amount NUMERIC(18,8) NOT NULL CHECK (amount > 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    commission_amount NUMERIC(18,8) NOT NULL DEFAULT 0 CHECK (commission_amount >= 0),
    commission_percent NUMERIC(5,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'attributed' CHECK (status IN ('attributed', 'reversed')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. COMMISSION LEDGER (append-only; accrual/reversal/payout)
CREATE TABLE IF NOT EXISTS commission_ledger (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conversion_id UUID REFERENCES referral_conversions(id),
    entry_type VARCHAR(20) NOT NULL CHECK (entry_type IN ('accrual', 'reversal', 'payout')),
    amount NUMERIC(18,8) NOT NULL CHECK (amount > 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ledger_partner ON commission_ledger(partner_user_id, created_at);

-- 8. PAYOUTS (state machine: pending→approved→paid | pending→rejected/cancelled)
CREATE TABLE IF NOT EXISTS payouts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount NUMERIC(18,8) NOT NULL CHECK (amount > 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    payout_method VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'approved', 'paid', 'rejected', 'cancelled')),
    requested_by UUID NOT NULL,
    decided_by UUID,
    decision_note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    decided_at TIMESTAMP WITH TIME ZONE
);

-- 9. PLATFORM TAB REGISTRY (readiness manifest consumed by AI Studio client)
CREATE TABLE IF NOT EXISTS platform_tabs (
    tab_key VARCHAR(60) PRIMARY KEY,
    title_i18n_key VARCHAR(100) NOT NULL,
    route VARCHAR(120) NOT NULL,
    icon VARCHAR(60),
    audience VARCHAR(20) NOT NULL DEFAULT 'user'
        CHECK (audience IN ('public', 'user', 'partner', 'admin')),
    status VARCHAR(30) NOT NULL DEFAULT 'backend_ready'
        CHECK (status IN ('planned', 'backend_ready', 'frontend_ready', 'published')),
    feature_flags JSONB DEFAULT '{}'::jsonb,
    sort_order INTEGER DEFAULT 100,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO platform_tabs (tab_key, title_i18n_key, route, icon, audience, status, sort_order) VALUES
    ('referral_partner',   'tabs.referralPartner',   '/partner',           'handshake', 'partner', 'backend_ready', 50),
    ('sales_partnership',  'tabs.salesPartnership',  '/sales-partnership', 'storefront','public',  'backend_ready', 51),
    ('affiliate_dashboard','tabs.affiliateDashboard','/affiliate',         'insights',  'user',    'backend_ready', 52)
ON CONFLICT (tab_key) DO NOTHING;

-- Seed tiered commission rules (display via public landing; computed server-side only)
INSERT INTO commission_rules (tier, percent, min_payout_amount) VALUES
    ('standard', 15.00, 10),
    ('silver',   20.00, 10),
    ('gold',     25.00, 25),
    ('platinum', 30.00, 50)
ON CONFLICT (tier) DO NOTHING;
