-- ====================================================================
-- Migration 012: Master Part 09 - Admin Assistant & AI Secrets Management
-- Extends admin_settings to support assistant parameters, UI meta, and secret masks
-- ====================================================================

-- 1. Extend admin_settings table
DO $$
BEGIN
    -- Add category / service_category
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'category') THEN
        ALTER TABLE admin_settings ADD COLUMN category VARCHAR(50) DEFAULT 'ai_assistant';
    END IF;

    -- Add display_name
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'display_name') THEN
        ALTER TABLE admin_settings ADD COLUMN display_name VARCHAR(150);
    END IF;

    -- Add status (active, disabled, pending_config)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'is_active') THEN
        ALTER TABLE admin_settings ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
    END IF;

    -- Add masked_hint (e.g. "****ab12", never the raw key)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'masked_hint') THEN
        ALTER TABLE admin_settings ADD COLUMN masked_hint VARCHAR(30);
    END IF;

    -- Add provider_target (github_secrets, render_env, railway_env, vault)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'provider_target') THEN
        ALTER TABLE admin_settings ADD COLUMN provider_target VARCHAR(50) DEFAULT 'github_secrets';
    END IF;

    -- Future extension for Part 11: assistant instruction, fine-tuning params and autonomy log
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'admin_settings' AND column_name = 'assistant_metadata') THEN
        ALTER TABLE admin_settings ADD COLUMN assistant_metadata JSONB DEFAULT '{}'::jsonb;
    END IF;
END $$;

-- 2. Seed default registered services according to Part 09 requirements
INSERT INTO admin_settings (key_name, secret_ref, service_name, display_name, category, is_active, masked_hint, provider_target, updated_by)
VALUES
    ('NOWPAYMENTS_API_KEY', 'GITHUB_SECRET:NOWPAYMENTS_API_KEY', 'nowpayments_gateway', 'NowPayments API Key', 'payment', true, '****9f21', 'github_secrets', 'system_init'),
    ('NOWPAYMENTS_IPN_SECRET', 'GITHUB_SECRET:NOWPAYMENTS_IPN_SECRET', 'nowpayments_ipn', 'NowPayments IPN Callback Secret', 'payment', true, '****c41a', 'github_secrets', 'system_init'),
    ('DIGIPAY_CLIENT_SECRET', 'RENDER_ENV:DIGIPAY_CLIENT_SECRET', 'digipay_gateway', 'DigiPay / SnapPay API Credentials', 'payment', true, '****88e3', 'render_env', 'system_init'),
    ('MYKET_BAZAAR_SDK_KEY', 'RENDER_ENV:MYKET_BAZAAR_SDK_KEY', 'cafebazaar_myket', 'CafeBazaar / Myket / IranApps SDK Keys', 'app_store', true, '****3b77', 'render_env', 'system_init'),
    ('GOOGLE_FLOW_API_KEY', 'GITHUB_SECRET:GOOGLE_FLOW_API_KEY', 'google_flow_media', 'Google Flow (nanobanana) Image/Video Generator', 'ai_assistant', true, '****bb90', 'github_secrets', 'system_init'),
    ('TRANSLATION_LLM_API_KEY', 'RENDER_ENV:TRANSLATION_LLM_API_KEY', 'translation_agent', 'Translation LLM Service (6 Target Languages)', 'ai_assistant', true, '****55aa', 'render_env', 'system_init'),
    ('GEOIP_SERVICE_API_KEY', 'RENDER_ENV:GEOIP_SERVICE_API_KEY', 'geoip_resolver', 'Geo-IP Resolution Service', 'localization', true, '****1044', 'render_env', 'system_init'),
    ('SMTP_PUSH_CREDENTIALS', 'RENDER_ENV:SMTP_PUSH_CREDENTIALS', 'notifications', 'SMTP & Push Notification Provider', 'messaging', true, '****77f0', 'render_env', 'system_init'),
    ('N8N_INSTANCE_API_KEY', 'RENDER_ENV:N8N_INSTANCE_API_KEY', 'n8n_orchestration', 'n8n Instance URL & Webhook API Key', 'orchestration', true, '****ee01', 'render_env', 'system_init')
ON CONFLICT (key_name) DO NOTHING;
