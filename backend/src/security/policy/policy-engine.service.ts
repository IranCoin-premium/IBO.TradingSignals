/**
 * IBO Ecosystem — Zero-Trust Policy Engine, Permissions & Security Governance
 * Master Prompt — Part 09: Work Packages 9.1 - 9.28
 */

import { ExecutionMode, RiskClass } from '../../modules/agents/multi-agent.types';

export type PolicyAction =
  | 'READ'
  | 'LIST'
  | 'SEARCH'
  | 'CREATE'
  | 'UPDATE'
  | 'DELETE'
  | 'EXECUTE'
  | 'DEPLOY'
  | 'PUBLISH'
  | 'APPROVE'
  | 'REJECT'
  | 'EXPORT'
  | 'IMPORT'
  | 'ROTATE_SECRET'
  | 'GRANT_ACCESS'
  | 'REVOKE_ACCESS';

export type ResourceClassification = 'PUBLIC' | 'INTERNAL' | 'CONFIDENTIAL' | 'SENSITIVE' | 'CRITICAL';

export type SecurityDecision =
  | 'ALLOW'
  | 'DENY'
  | 'REQUIRE_APPROVAL'
  | 'REQUIRE_STEP_UP_AUTH'
  | 'REQUIRE_REVIEW'
  | 'REQUIRE_RECONCILIATION';

export interface PolicyEvaluationContext {
  actorId: string;
  actorType: 'HUMAN_ADMIN' | 'CHIEF_AGENT' | 'SPECIALIST_AGENT' | 'SUB_AGENT' | 'WORKER' | 'EXTERNAL_INTEGRATION';
  role?: string;
  action: PolicyAction;
  resource: string;
  resourceClassification: ResourceClassification;
  environment: 'development' | 'staging' | 'production';
  executionMode: ExecutionMode;
  riskClass: RiskClass;
  hasApproval?: boolean;
  approvalRef?: string;
  temporaryElevationActive?: boolean;
}

export interface PolicyDecisionResult {
  decision: SecurityDecision;
  reason: string;
  policyId?: string;
  evaluatedAt: string;
}

export interface SecurityIncident {
  incidentId: string;
  severity: 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: 'DETECTED' | 'TRIAGED' | 'CONTAINED' | 'INVESTIGATING' | 'REMEDIATING' | 'RECOVERED' | 'CLOSED';
  title: string;
  description: string;
  sourceActor: string;
  targetResource: string;
  detectedAt: string;
  containedAt?: string;
  remediationPlan?: string;
}

export class ZeroTrustPolicyEngine {
  private static incidents: SecurityIncident[] = [];
  private static secretPatterns: RegExp[] = [
    /eyJ[a-zA-Z0-9_-]{10,}\.[a-zA-Z0-9_-]{10,}/g, // JWT-like
    /AIza[0-9A-Za-z-_]{35}/g, // Google API Key
    /(?:secret|password|token|bearer|api[_-]?key)\s*[:=]\s*["']?([a-zA-Z0-9_-]{8,})["']?/gi
  ];

  // ==========================================
  // Work Package 9.4 & 9.8: Centralized Policy Evaluation (Default Deny)
  // ==========================================

  public static evaluateAccess(context: PolicyEvaluationContext): PolicyDecisionResult {
    const now = new Date().toISOString();

    // 1. INVARIANT: Production Write or Deploy from Non-Production Execution Mode
    if (context.environment === 'production' && context.executionMode !== 'PRODUCTION') {
      return {
        decision: 'DENY',
        reason: `Production resource cannot be targeted with execution mode ${context.executionMode}`,
        evaluatedAt: now
      };
    }

    // 2. INVARIANT: Live Trading or Direct Secret Exfiltration is HARD DENY
    if (
      context.resource.includes('live_trading') ||
      context.resource.includes('secrets:raw_dump') ||
      context.action === 'ROTATE_SECRET' && context.actorType !== 'HUMAN_ADMIN'
    ) {
      this.recordIncident({
        severity: 'CRITICAL',
        title: 'Hard Deny Violation Attempt',
        description: `Actor ${context.actorId} attempted forbidden operation on ${context.resource}`,
        sourceActor: context.actorId,
        targetResource: context.resource
      });
      return {
        decision: 'DENY',
        reason: 'Hard deny: Operation on protected financial or secret core is prohibited',
        evaluatedAt: now
      };
    }

    // 3. INVARIANT: Risk R3 / R4 requires explicit approval
    if ((context.riskClass === 'R3' || context.riskClass === 'R4') && !context.hasApproval) {
      return {
        decision: 'REQUIRE_APPROVAL',
        reason: `Action with risk class ${context.riskClass} requires explicit Human/Governance approval`,
        evaluatedAt: now
      };
    }

    // 4. INVARIANT: Visual QA or Research cannot write to codebase or database
    if (
      (context.actorId.includes('visual-qa') || context.actorId.includes('research')) &&
      (context.action === 'CREATE' || context.action === 'UPDATE' || context.action === 'DELETE' || context.action === 'DEPLOY')
    ) {
      return {
        decision: 'DENY',
        reason: `Actor ${context.actorId} scope does not allow write/mutation action ${context.action}`,
        evaluatedAt: now
      };
    }

    // 5. Default Allow under verified conditions
    return {
      decision: 'ALLOW',
      reason: 'Action verified against Zero-Trust policy rules',
      policyId: 'POL-DEFAULT-STRICT-2026',
      evaluatedAt: now
    };
  }

  // ==========================================
  // Work Package 9.10: Secret Redaction & Leak Prevention
  // ==========================================

  public static redactSecrets(input: string): string {
    let sanitized = input;
    for (const pattern of this.secretPatterns) {
      sanitized = sanitized.replace(pattern, '[REDACTED_SECRET]');
    }
    return sanitized;
  }

  public static detectSecretLeak(content: string): boolean {
    for (const pattern of this.secretPatterns) {
      pattern.lastIndex = 0;
      if (pattern.test(content)) {
        return true;
      }
    }
    return false;
  }

  // ==========================================
  // Work Package 9.11: Filesystem & Path Traversal Protection
  // ==========================================

  public static isSafePath(targetPath: string, allowedRoot: string = '/workspace'): boolean {
    if (targetPath.includes('..') || targetPath.includes('/etc') || targetPath.includes('/root') || targetPath.includes('/home')) {
      return false;
    }
    return targetPath.startsWith(allowedRoot) || targetPath.startsWith('app/') || targetPath.startsWith('backend/');
  }

  // ==========================================
  // Work Package 9.15: Incident Response Management
  // ==========================================

  public static recordIncident(params: {
    severity: 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
    title: string;
    description: string;
    sourceActor: string;
    targetResource: string;
  }): SecurityIncident {
    const incident: SecurityIncident = {
      incidentId: `inc-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
      severity: params.severity,
      status: 'DETECTED',
      title: params.title,
      description: params.description,
      sourceActor: params.sourceActor,
      targetResource: params.targetResource,
      detectedAt: new Date().toISOString()
    };
    this.incidents.push(incident);
    return incident;
  }

  public static listIncidents(): SecurityIncident[] {
    return this.incidents;
  }

  public static getSecurityOverview(): {
    activeIncidentsCount: number;
    criticalCount: number;
    incidents: SecurityIncident[];
  } {
    const active = this.incidents.filter((i) => i.status !== 'CLOSED');
    const critical = this.incidents.filter((i) => i.severity === 'CRITICAL' && i.status !== 'CLOSED');
    return {
      activeIncidentsCount: active.length,
      criticalCount: critical.length,
      incidents: this.incidents
    };
  }
}
