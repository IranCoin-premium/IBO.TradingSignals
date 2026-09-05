-- ====================================================================
-- Migration 013: Master Part 11 - Full Autonomous Agent Operations,
-- Self-Tuning Instruction Versions & Circuit Breaker Governance
-- ====================================================================

-- 1. Table: agent_instruction_versions
-- Tracks all system instructions for AI agents, whether created by human admins
-- or self-tuned by the agent itself after passing the 10-level staging quality gate.
CREATE TABLE IF NOT EXISTS agent_instruction_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_name VARCHAR(100) NOT NULL,
    version_number INTEGER NOT NULL,
    instruction_text TEXT NOT NULL,
    created_by VARCHAR(50) NOT NULL DEFAULT 'admin' CHECK (created_by IN ('self', 'admin')),
    test_result JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT FALSE,
    change_summary TEXT,
    diff_against_previous TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_agent_version UNIQUE (agent_name, version_number)
);

CREATE INDEX IF NOT EXISTS idx_agent_instruction_active ON agent_instruction_versions(agent_name, is_active);

-- 2. Table: agent_autonomy_settings
-- Configures autonomous execution bounds, retry ceilings, and circuit breakers.
CREATE TABLE IF NOT EXISTS agent_autonomy_settings (
    agent_name VARCHAR(100) PRIMARY KEY,
    autonomy_enabled BOOLEAN DEFAULT TRUE,
    max_retry INTEGER DEFAULT 3 CHECK (max_retry BETWEEN 1 AND 10),
    circuit_breaker_threshold INTEGER DEFAULT 5 CHECK (circuit_breaker_threshold BETWEEN 1 AND 20),
    failures_last_24h INTEGER DEFAULT 0,
    circuit_broken BOOLEAN DEFAULT FALSE,
    conservativeness_level VARCHAR(30) DEFAULT 'conservative' 
        CHECK (conservativeness_level IN ('conservative', 'normal', 'aggressive')),
    allowed_file_patterns TEXT[] DEFAULT ARRAY['frontend/src/**', 'translations/**'],
    allowed_tables TEXT[] DEFAULT ARRAY['translations', 'media_assets', 'support_messages'],
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Table: agent_autonomy_logs
-- Records all unassisted autonomous actions and staging/production quality-gate results.
CREATE TABLE IF NOT EXISTS agent_autonomy_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_name VARCHAR(100) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    environment VARCHAR(20) NOT NULL DEFAULT 'staging' CHECK (environment IN ('staging', 'production')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('passed_all_levels', 'rolled_back_at_level', 'circuit_breaker_tripped', 'pending_human_review')),
    failed_level INTEGER,
    attempt_count INTEGER DEFAULT 1,
    details JSONB DEFAULT '{}'::jsonb,
    audit_log_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_autonomy_logs_agent ON agent_autonomy_logs(agent_name, status);

-- 4. Seed Autonomy Settings for the 4 Core AI Agents
INSERT INTO agent_autonomy_settings (agent_name, autonomy_enabled, max_retry, circuit_breaker_threshold, conservativeness_level, allowed_file_patterns, allowed_tables)
VALUES
    ('UIUXAgent', TRUE, 3, 5, 'conservative', ARRAY['frontend/src/components/**', 'frontend/src/styles/**'], ARRAY['translations']),
    ('TranslatorAgent', TRUE, 3, 5, 'normal', ARRAY['translations/**', 'locales/**'], ARRAY['translations', 'languages']),
    ('ImageVideoAgent', TRUE, 3, 5, 'conservative', ARRAY['assets/**', 'media/**'], ARRAY['media_assets']),
    ('SupportAgent', TRUE, 3, 5, 'conservative', ARRAY['support/knowledge_base/**'], ARRAY['support_messages', 'support_conversations'])
ON CONFLICT (agent_name) DO UPDATE SET
    autonomy_enabled = EXCLUDED.autonomy_enabled,
    max_retry = EXCLUDED.max_retry,
    circuit_breaker_threshold = EXCLUDED.circuit_breaker_threshold,
    conservativeness_level = EXCLUDED.conservativeness_level;

-- 5. Seed Initial Production Instructions (v1) for the 4 Core AI Agents
INSERT INTO agent_instruction_versions (agent_name, version_number, instruction_text, created_by, test_result, is_active, change_summary, activated_at)
VALUES
    (
        'UIUXAgent',
        1,
        'Master UI/UX Agent. Preserves brand assets, maintains 8dp grid, passes 10-level quality gate before production deployment. Risk disclosure is permanent and non-negotiable.',
        'admin',
        '{"status": "passed_all_levels", "environment": "staging", "levels_tested": 10}'::jsonb,
        TRUE,
        'Initial base instruction for autonomous UI/UX optimization.',
        CURRENT_TIMESTAMP
    ),
    (
        'TranslatorAgent',
        1,
        'Translator Agent for 6 languages (fa, en, ar, hi, tr, ru). Maintains glossary consistency, enforces CSS logical properties, prevents untranslated placeholders, and guarantees legal risk disclosure in all translations.',
        'admin',
        '{"status": "passed_all_levels", "environment": "staging", "levels_tested": 10}'::jsonb,
        TRUE,
        'Initial base instruction for financial multilingual translation.',
        CURRENT_TIMESTAMP
    ),
    (
        'ImageVideoAgent',
        1,
        'Media Generator using Google Flow (nanobanana). Strictly adheres to Style Reference Set (1-10 assets with permanent logo). Never uses third-party copyrighted trademarks or misleading profit claims. Supports 5 aspect ratios and 15-60s teasers.',
        'admin',
        '{"status": "passed_all_levels", "environment": "staging", "levels_tested": 10}'::jsonb,
        TRUE,
        'Initial base instruction for consistent brand media and motion graphics.',
        CURRENT_TIMESTAMP
    ),
    (
        'SupportAgent',
        1,
        '24/7 AI Support Assistant. Operates in strict READ-ONLY mode. Truthfully discloses AI identity when queried. Appends persistent binary options risk disclosure. Never guarantees financial profits.',
        'admin',
        '{"status": "passed_all_levels", "environment": "staging", "levels_tested": 10}'::jsonb,
        TRUE,
        'Initial base instruction for transparent read-only client support.',
        CURRENT_TIMESTAMP
    )
ON CONFLICT (agent_name, version_number) DO NOTHING;
