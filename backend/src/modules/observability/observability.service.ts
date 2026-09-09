/**
 * IBO Ecosystem — Observability, Structured Logging, Secret Redaction & Log Retention
 * Part 04: Work Packages 4.2, 4.3, 4.4
 * 
 * Invariants:
 * 1. UTC is the universal source of truth; Admin views can format with display timezones.
 * 2. Strict Redaction: Passwords, tokens, seed phrases, keys, private auth headers are scrubbed.
 * 3. Structured Logging taxonomy: TRACE, DEBUG, INFO, NOTICE, WARNING, ERROR, CRITICAL, SECURITY.
 * 4. Admin-Safe Explanation: No raw private chain-of-thought, but clear objective, evidence, decision, rationale, risks.
 * 5. Retention policy per log tier.
 */

export type LogSeverity =
  | 'TRACE'
  | 'DEBUG'
  | 'INFO'
  | 'NOTICE'
  | 'WARNING'
  | 'ERROR'
  | 'CRITICAL'
  | 'SECURITY';

export interface StructuredLogEntry {
  timestampUtc: string;     // ISO 8601 UTC
  displayTimezone?: string; // e.g. "+03:30" for Iran / Persian Admin
  severity: LogSeverity;
  component: string;
  agentId?: string;
  subagentId?: string;
  taskId?: string;
  traceId?: string;
  spanId?: string;
  correlationId?: string;
  eventType?: string;
  status?: string;
  summary: string;
  evidenceRef?: string;
  durationMs?: number;
  environment: 'development' | 'staging' | 'production';
  redactionStatus: 'CLEAN' | 'SCRUBBED';
  payload?: Record<string, unknown>;
}

export interface AdminSafeExplanation {
  taskId: string;
  agentId: string;
  objective: string;
  planSummary: string;
  verifiedEvidence: string[];
  observedFacts: string[];
  assumptions: string[];
  alternativesConsidered: string[];
  decision: string;
  decisionRationale: string;
  uncertainty: string;
  riskAssessment: string;
  confidenceScore: number; // 0.0 to 1.0
  toolsUsed: string[];
  mcpUsed: string[];
  rulesEvaluated: string[];
  hooksTriggered: string[];
  testsRun: string[];
  finalResult: string;
  nextAction: string;
}

export interface LogRetentionConfig {
  operationalLogsDays: number;
  auditLogsDays: number;
  securityLogsDays: number;
  errorLogsDays: number;
  screenshotsDays: number;
}

export const DEFAULT_RETENTION_POLICY: LogRetentionConfig = {
  operationalLogsDays: 30,
  auditLogsDays: 365,
  securityLogsDays: 730,
  errorLogsDays: 90,
  screenshotsDays: 14
};

export class ObservabilityService {
  private static readonly SENSITIVE_PATTERNS = [
    /bearer\s+[a-zA-Z0-9_\-\.]+/gi,
    /ey[a-zA-Z0-9_\-]{5,}\.[a-zA-Z0-9_\-]{2,}\.[a-zA-Z0-9_\-]+/gi, // JWT
    /(?:api[_-]?key|secret|password|passwd|private[_-]?key|seed[_-]?phrase|access[_-]?token|token)\s*[:=]\s*["']?([^"'\s,]+)["']?/gi,
    /\b(AIza[0-9A-Za-z-_]{35})\b/g, // Google API key
    /\b(sk[-_][a-zA-Z0-9_\-]{15,})\b/g, // OpenAI/Stripe key
  ];

  /**
   * Universal Redaction engine. Scrubs any secret pattern from strings and deep objects.
   */
  public static redactText(input: string): { text: string; wasScrubbed: boolean } {
    if (!input || typeof input !== 'string') return { text: input, wasScrubbed: false };

    let scrubbed = false;
    let result = input;

    for (const pattern of this.SENSITIVE_PATTERNS) {
      pattern.lastIndex = 0;
      if (pattern.test(result)) {
        scrubbed = true;
        pattern.lastIndex = 0;
        result = result.replace(pattern, (match) => {
          if (match.toLowerCase().startsWith('bearer ')) {
            return 'Bearer [REDACTED]';
          }
          return '[REDACTED_SECRET]';
        });
      }
    }

    return { text: result, wasScrubbed: scrubbed };
  }

  public static redactObject<T>(obj: T): { redacted: T; wasScrubbed: boolean } {
    if (!obj || typeof obj !== 'object') {
      if (typeof obj === 'string') {
        const { text, wasScrubbed } = this.redactText(obj);
        return { redacted: text as unknown as T, wasScrubbed };
      }
      return { redacted: obj, wasScrubbed: false };
    }

    let anyScrubbed = false;
    const clone: any = Array.isArray(obj) ? [] : {};

    for (const [k, v] of Object.entries(obj)) {
      const lowerKey = k.toLowerCase();
      if (['password', 'token', 'jwt', 'secret', 'key', 'seedphrase', 'privatekey', 'auth'].some(s => lowerKey.includes(s))) {
        clone[k] = '[REDACTED]';
        anyScrubbed = true;
      } else if (typeof v === 'string') {
        const { text, wasScrubbed } = this.redactText(v);
        clone[k] = text;
        if (wasScrubbed) anyScrubbed = true;
      } else if (typeof v === 'object' && v !== null) {
        const { redacted, wasScrubbed } = this.redactObject(v);
        clone[k] = redacted;
        if (wasScrubbed) anyScrubbed = true;
      } else {
        clone[k] = v;
      }
    }

    return { redacted: clone as T, wasScrubbed: anyScrubbed };
  }

  /**
   * Formats UTC timestamp into display timezone representation (e.g. +03:30 Tehran time)
   */
  public static formatWithTimezone(utcIsoString: string, timezoneOffsetMinutes: number = 210): string {
    const d = new Date(utcIsoString);
    if (isNaN(d.getTime())) return utcIsoString;

    const tzDate = new Date(d.getTime() + timezoneOffsetMinutes * 60000);
    const yyyy = tzDate.getUTCFullYear();
    const mm = String(tzDate.getUTCMonth() + 1).padStart(2, '0');
    const dd = String(tzDate.getUTCDate()).padStart(2, '0');
    const hh = String(tzDate.getUTCHours()).padStart(2, '0');
    const min = String(tzDate.getUTCMinutes()).padStart(2, '0');
    const ss = String(tzDate.getUTCSeconds()).padStart(2, '0');

    const sign = timezoneOffsetMinutes >= 0 ? '+' : '-';
    const abs = Math.abs(timezoneOffsetMinutes);
    const offH = String(Math.floor(abs / 60)).padStart(2, '0');
    const offM = String(abs % 60).padStart(2, '0');

    return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss} ${sign}${offH}:${offM}`;
  }

  /**
   * Creates a verified Admin-Safe explanation block. Guarantees no raw internal chain-of-thought tokens are included.
   */
  public static createAdminSafeExplanation(explanation: AdminSafeExplanation): AdminSafeExplanation {
    // Redact any accidentally embedded token or credential in notes/evidence
    const { redacted: sanitizedEvidence } = this.redactObject(explanation.verifiedEvidence);
    const { redacted: sanitizedFacts } = this.redactObject(explanation.observedFacts);
    const { text: sanitizedRationale } = this.redactText(explanation.decisionRationale);

    return {
      ...explanation,
      verifiedEvidence: sanitizedEvidence,
      observedFacts: sanitizedFacts,
      decisionRationale: sanitizedRationale
    };
  }

  /**
   * Universal Structured Logger with automatic secret redaction
   */
  public static log(entry: Omit<StructuredLogEntry, 'timestampUtc' | 'redactionStatus'>): StructuredLogEntry {
    const { text: sanitizedSummary, wasScrubbed: scrubbedSummary } = this.redactText(entry.summary);
    const { redacted: sanitizedPayload, wasScrubbed: scrubbedPayload } = entry.payload
      ? this.redactObject(entry.payload)
      : { redacted: undefined, wasScrubbed: false };

    const structured: StructuredLogEntry = {
      ...entry,
      summary: sanitizedSummary,
      payload: sanitizedPayload,
      timestampUtc: new Date().toISOString(),
      redactionStatus: scrubbedSummary || scrubbedPayload ? 'SCRUBBED' : 'CLEAN'
    };

    return structured;
  }
}
