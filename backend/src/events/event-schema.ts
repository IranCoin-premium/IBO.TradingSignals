/**
 * IBO Ecosystem — Core Event Schema & Validation
 * Part 02: Event Foundation & Non-Blocking Coordination
 */

export type EventSeverity = 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';

export type EventSource = 
  | 'BACKEND_API'
  | 'ANDROID_APP'
  | 'CI_CD_PIPELINE'
  | 'N8N_ORCHESTRATOR'
  | 'AGENT_SUPERVISOR'
  | 'VTQI_SYSTEM'
  | 'CAC_COORDINATOR';

export type StandardEventType =
  | 'repository_changed'
  | 'build_failed'
  | 'test_failed'
  | 'application_error_detected'
  | 'visual_issue_detected'
  | 'text_issue_detected'
  | 'scheduled_audit_due'
  | 'monitoring_alert'
  | 'manual_review_requested';

export interface IBOEvent<T = Record<string, unknown>> {
  eventId: string;          // UUID v4 format
  eventType: StandardEventType;
  source: EventSource;
  timestamp: string;        // ISO 8601 UTC
  environment: 'development' | 'staging' | 'production';
  correlationId?: string;   // Trace id connecting events to workflows
  severity: EventSeverity;
  payload: T;
}

/**
 * Validates an incoming event against structural invariants.
 * Prevents sensitive information (passwords, tokens, private keys) from polluting event logs.
 */
export function validateIBOEvent(event: Partial<IBOEvent>): { valid: boolean; errors: string[] } {
  const errors: string[] = [];

  if (!event.eventId || typeof event.eventId !== 'string' || event.eventId.trim().length < 8) {
    errors.push('eventId must be a valid non-empty identifier (minimum 8 chars)');
  }

  const validTypes: StandardEventType[] = [
    'repository_changed',
    'build_failed',
    'test_failed',
    'application_error_detected',
    'visual_issue_detected',
    'text_issue_detected',
    'scheduled_audit_due',
    'monitoring_alert',
    'manual_review_requested'
  ];

  if (!event.eventType || !validTypes.includes(event.eventType)) {
    errors.push(`eventType '${event.eventType}' is not an authorized standard event type`);
  }

  const validSources: EventSource[] = [
    'BACKEND_API',
    'ANDROID_APP',
    'CI_CD_PIPELINE',
    'N8N_ORCHESTRATOR',
    'AGENT_SUPERVISOR',
    'VTQI_SYSTEM',
    'CAC_COORDINATOR'
  ];

  if (!event.source || !validSources.includes(event.source)) {
    errors.push(`source '${event.source}' is not an authorized event source`);
  }

  if (!event.timestamp || isNaN(Date.parse(event.timestamp))) {
    errors.push('timestamp must be a valid ISO 8601 date string');
  }

  if (!event.environment || !['development', 'staging', 'production'].includes(event.environment)) {
    errors.push('environment must be development, staging, or production');
  }

  if (!event.severity || !['INFO', 'WARNING', 'ERROR', 'CRITICAL'].includes(event.severity)) {
    errors.push('severity must be INFO, WARNING, ERROR, or CRITICAL');
  }

  // Security Check: Ensure payload does NOT contain raw secret patterns
  if (event.payload) {
    const payloadStr = JSON.stringify(event.payload);
    if (/AIza[0-9A-Za-z_-]{20,}/.test(payloadStr) ||
        /sk-[A-Za-z0-9]{20,}/.test(payloadStr) ||
        /ghp_[A-Za-z0-9]{30,}/.test(payloadStr) ||
        /password|secret|privateKey/i.test(payloadStr) && /[:=]\s*["'][^"']{8,}["']/.test(payloadStr)) {
      errors.push('SECURITY VIOLATION: Payload contains potential secrets, tokens or private keys');
    }
  }

  return {
    valid: errors.length === 0,
    errors
  };
}
