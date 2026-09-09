-- ====================================================================
-- Migration 010: Master Part 07 - Multilingual Architecture & 24/7 AI Support
-- Configures 6 verified target languages, translation seeds, and read-only support chat
-- ====================================================================

-- 1. Insert or update 6 Verified Market Languages
INSERT INTO languages (code, name_native, direction, is_active) VALUES
('fa', 'فارسی', 'rtl', TRUE),
('en', 'English', 'ltr', TRUE),
('ar', 'العربية', 'rtl', TRUE),
('hi', 'हिन्दी', 'ltr', TRUE),
('tr', 'Türkçe', 'ltr', TRUE),
('ru', 'Русский', 'ltr', TRUE)
ON CONFLICT (code) DO UPDATE 
SET name_native = EXCLUDED.name_native,
    direction = EXCLUDED.direction,
    is_active = EXCLUDED.is_active;

-- 2. Add preferred_language and geo_country to users if not already present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'preferred_language') THEN
        ALTER TABLE users ADD COLUMN preferred_language VARCHAR(10) DEFAULT 'fa' REFERENCES languages(code);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'geo_country') THEN
        ALTER TABLE users ADD COLUMN geo_country VARCHAR(10) DEFAULT 'IR';
    END IF;
END $$;

-- 3. Seed Mandatory Risk Disclosures in Translations Table for all 6 Languages
INSERT INTO translations (entity_type, entity_id, language_code, content) VALUES
('ui_string', 'risk_disclosure', 'fa', 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.'),
('ui_string', 'risk_disclosure', 'en', 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.'),
('ui_string', 'risk_disclosure', 'ar', 'هذه الإشارات ليست نصيحة مالية؛ ينطوي تداول الخيارات الثنائية على مخاطر عالية لفقدان رأس المال.'),
('ui_string', 'risk_disclosure', 'hi', 'ये संकेत वित्तीय सलाह नहीं हैं; बाइनरी ऑप्शंस ट्रेडिंग में पूंजी हानि का उच्च जोखिम होता है।'),
('ui_string', 'risk_disclosure', 'tr', 'Bu sinyaller finansal tavsiye niteliğinde değildir; ikili opsiyon işlemleri yüksek sermaye kaybı riski taşır.'),
('ui_string', 'risk_disclosure', 'ru', 'Эти сигналы не являются финансовой рекомендацией; торговля бинарными опционами сопряжена с высоким риском потери капитала.')
ON CONFLICT DO NOTHING;

-- 4. 24/7 Read-Only AI Support Conversation History
CREATE TABLE IF NOT EXISTS support_conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    language_code VARCHAR(10) NOT NULL REFERENCES languages(code),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CLOSED', 'ESCALATED_HUMAN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS support_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES support_conversations(id) ON DELETE CASCADE,
    sender_type VARCHAR(20) NOT NULL CHECK (sender_type IN ('USER', 'AI_AGENT', 'HUMAN_SUPERVISOR')),
    content TEXT NOT NULL,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_support_conv_user ON support_conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_support_msgs_conv ON support_messages(conversation_id);
