-- Migration 004: Support Tickets and Localization Baseline

CREATE TABLE IF NOT EXISTS support_tickets (
    ticket_id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    locale VARCHAR(10) DEFAULT 'fa',
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'PENDING_AGENT', 'RESOLVED', 'CLOSED')),
    response TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_preferences (
    user_id VARCHAR(100) PRIMARY KEY,
    locale VARCHAR(10) DEFAULT 'fa',
    notifications_enabled BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
