-- ====================================================================
-- Migration 009: Master Part 06 - UI/UX Change Log & 10-Level Test Governance
-- Links UI enhancements directly to immutable audit_logs table
-- ====================================================================

CREATE TABLE IF NOT EXISTS ui_change_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    audit_log_id UUID REFERENCES audit_logs(id) ON DELETE CASCADE,
    agent_id VARCHAR(100) NOT NULL,
    component_target VARCHAR(255) NOT NULL,
    change_type VARCHAR(50) NOT NULL CHECK (change_type IN (
        'STYLE_DRIFT',
        'MODAL_CONVERSION',
        'SPACING_ADJUSTMENT',
        'ANIMATION_REFINEMENT',
        'ACCESSIBILITY_FIX',
        'TYPOGRAPHY_ALIGNMENT'
    )),
    diff_summary TEXT NOT NULL,
    style_drift_percentage NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    level_test_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (level_test_status IN (
        'PENDING',
        'TESTING_LEVEL_1_3',
        'TESTING_LEVEL_4_6',
        'TESTING_LEVEL_7_9',
        'TESTING_LEVEL_10',
        'PASSED_LEVEL_10',
        'FAILED_ROLLED_BACK',
        'CANCELLED_MAX_RETRIES_EXCEEDED'
    )),
    failed_level INTEGER CHECK (failed_level BETWEEN 1 AND 10),
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    rolled_back BOOLEAN NOT NULL DEFAULT FALSE,
    rollback_reason TEXT,
    before_state JSONB NOT NULL,
    after_state JSONB NOT NULL,
    test_run_details JSONB DEFAULT '{}',
    human_review_required BOOLEAN NOT NULL DEFAULT FALSE,
    human_review_status VARCHAR(50) DEFAULT 'UNREVIEWED' CHECK (human_review_status IN ('UNREVIEWED', 'APPROVED', 'REJECTED')),
    human_reviewer_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ui_change_log_audit ON ui_change_log(audit_log_id);
CREATE INDEX IF NOT EXISTS idx_ui_change_log_agent ON ui_change_log(agent_id);
CREATE INDEX IF NOT EXISTS idx_ui_change_log_status ON ui_change_log(level_test_status);
CREATE INDEX IF NOT EXISTS idx_ui_change_log_review ON ui_change_log(human_review_required, human_review_status);
