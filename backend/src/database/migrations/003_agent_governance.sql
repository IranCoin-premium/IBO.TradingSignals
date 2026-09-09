-- Migration 003: Agent Orchestration, Supervisor & Governance Foundation

CREATE TABLE IF NOT EXISTS agents (
    agent_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    permissions TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent_tools (
    tool_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    risk_level VARCHAR(20) NOT NULL CHECK (risk_level IN ('R0', 'R1', 'R2', 'R3', 'R4')),
    required_permissions TEXT[] NOT NULL DEFAULT '{}',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent_jobs (
    job_id VARCHAR(100) PRIMARY KEY,
    agent_id VARCHAR(100) REFERENCES agents(agent_id),
    task_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'REJECTED')),
    environment VARCHAR(50) DEFAULT 'DEVELOPMENT' CHECK (environment IN ('DEVELOPMENT', 'STAGING', 'PRODUCTION')),
    result JSONB,
    error TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS agent_audit_logs (
    audit_id VARCHAR(100) PRIMARY KEY,
    agent_id VARCHAR(100),
    job_id VARCHAR(100),
    action VARCHAR(255) NOT NULL,
    target_resource VARCHAR(255) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    outcome VARCHAR(50) NOT NULL CHECK (outcome IN ('APPROVED', 'DENIED', 'VETOED', 'EXECUTED', 'ROLLED_BACK')),
    correlation_id VARCHAR(100),
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent_kill_switch (
    switch_key VARCHAR(100) PRIMARY KEY,
    enabled BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed default global kill switch & core tools
INSERT INTO agent_kill_switch (switch_key, enabled) VALUES ('GLOBAL_KILL_SWITCH', FALSE) ON CONFLICT (switch_key) DO NOTHING;
INSERT INTO agent_kill_switch (switch_key, enabled) VALUES ('PRODUCTION_WRITE_KILL_SWITCH', TRUE) ON CONFLICT (switch_key) DO NOTHING;

INSERT INTO agent_tools (tool_id, name, risk_level, required_permissions) VALUES 
('read_repository', 'Read Repository', 'R0', ARRAY['REPO_READ']),
('run_tests', 'Run Tests', 'R1', ARRAY['RUN_TESTS']),
('create_content_draft', 'Create Content Draft', 'R1', ARRAY['PUBLISH_CONTENT']),
('modify_production_db', 'Modify Production DB', 'R4', ARRAY['WRITE_DATABASE', 'MANAGE_SECURITY'])
ON CONFLICT (tool_id) DO NOTHING;
