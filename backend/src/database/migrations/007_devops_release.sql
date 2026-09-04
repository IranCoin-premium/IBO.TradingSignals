-- Migration 007: DevOps, Release and Observability Audit Logs

CREATE TABLE IF NOT EXISTS release_audit_logs (
    release_id VARCHAR(100) PRIMARY KEY,
    version VARCHAR(50) NOT NULL,
    environment VARCHAR(50) NOT NULL,
    commit_sha VARCHAR(64) NOT NULL,
    status VARCHAR(50) DEFAULT 'SUCCESS' CHECK (status IN ('SUCCESS', 'FAILED', 'ROLLED_BACK', 'CANARY')),
    deployed_by VARCHAR(100) NOT NULL,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS system_metrics (
    metric_id SERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value NUMERIC(12,4) NOT NULL,
    tags JSONB DEFAULT '{}',
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
