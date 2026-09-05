-- ====================================================================
-- Migration 011: Master Part 08 - Media Assets Expansion & Style Reference Rotation
-- Adds aspect_ratio, style_reference_week, brand consistency & video metadata
-- ====================================================================

-- 1. Extend media_assets table with required Part 08 columns
DO $$
BEGIN
    -- Add aspect_ratio column with check constraint for 5 mandatory ratios
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'aspect_ratio') THEN
        ALTER TABLE media_assets ADD COLUMN aspect_ratio VARCHAR(10) NOT NULL DEFAULT '16:9'
            CHECK (aspect_ratio IN ('1:1', '16:9', '9:16', '4:5', '3:2'));
    END IF;

    -- Add style_reference_week (e.g. 2026-W36) to track weekly 10-asset rotation
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'style_reference_week') THEN
        ALTER TABLE media_assets ADD COLUMN style_reference_week VARCHAR(20);
    END IF;

    -- Add is_style_reference flag (true if this asset is part of the current active 1-10 reference set)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'is_style_reference') THEN
        ALTER TABLE media_assets ADD COLUMN is_style_reference BOOLEAN DEFAULT FALSE;
    END IF;

    -- Add is_permanent_logo_reference (true for background-removed brand logo, never rotated out)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'is_permanent_logo_reference') THEN
        ALTER TABLE media_assets ADD COLUMN is_permanent_logo_reference BOOLEAN DEFAULT FALSE;
    END IF;

    -- Add attribution & SEO metadata
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'news_source_name') THEN
        ALTER TABLE media_assets ADD COLUMN news_source_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'platform_branding_text') THEN
        ALTER TABLE media_assets ADD COLUMN platform_branding_text VARCHAR(255) DEFAULT 'IBO Binary Option Trading Signals — yourdomain.com';
    END IF;

    -- Add background removal method (google_flow_native, rembg_fallback, none)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'bg_removal_method') THEN
        ALTER TABLE media_assets ADD COLUMN bg_removal_method VARCHAR(50) DEFAULT 'none'
            CHECK (bg_removal_method IN ('google_flow_native', 'rembg_fallback', 'none'));
    END IF;

    -- Add duration_seconds for motion graphics / teaser videos (15-60 seconds)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'duration_seconds') THEN
        ALTER TABLE media_assets ADD COLUMN duration_seconds NUMERIC(5,2);
    END IF;

    -- Add copyright & brand compliance verification status
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'media_assets' AND column_name = 'compliance_status') THEN
        ALTER TABLE media_assets ADD COLUMN compliance_status VARCHAR(30) DEFAULT 'VERIFIED'
            CHECK (compliance_status IN ('VERIFIED', 'FLAGGED_COPYRIGHT', 'FLAGGED_MISLEADING'));
    END IF;
END $$;

-- 2. Indexes for efficient style reference lookup and weekly batch queries
CREATE INDEX IF NOT EXISTS idx_media_assets_style_ref ON media_assets(is_style_reference, style_reference_week);
CREATE INDEX IF NOT EXISTS idx_media_assets_aspect_ratio ON media_assets(aspect_ratio);
CREATE INDEX IF NOT EXISTS idx_media_assets_compliance ON media_assets(compliance_status);

-- 3. Seed Permanent Background-Removed Brand Logo as style reference anchor
INSERT INTO media_assets (
    id,
    type,
    purpose,
    source_url,
    has_watermark,
    has_logo,
    created_by_agent,
    style_reference_id,
    aspect_ratio,
    style_reference_week,
    is_style_reference,
    is_permanent_logo_reference,
    bg_removal_method,
    compliance_status
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'image',
    'marketing',
    '/assets/branding/ibo_logo_nobg.png',
    TRUE,
    TRUE,
    'agent-brand-guardian',
    NULL,
    '1:1',
    'PERMANENT',
    TRUE,
    TRUE,
    'google_flow_native',
    'VERIFIED'
) ON CONFLICT (id) DO NOTHING;
