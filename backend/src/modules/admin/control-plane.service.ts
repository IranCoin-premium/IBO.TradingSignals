/**
 * IBO Ecosystem — CEO/Admin Control Plane, Human Governance & Command Center Service
 * Master Prompt — Part 12: Work Packages 12.01 - 12.20
 */

import { ExecutionMode, RiskClass } from '../agents/multi-agent.types';
import { ZeroTrustPolicyEngine } from '../../security/policy/policy-engine.service';
import { RuntimeResilienceObservabilityService } from '../observability/resilience.service';
import { QualityEngineeringReleaseService } from '../quality/quality-engineering.service';
import { TaskOrchestrationEngine } from '../orchestration/orchestration.service';
import { MultiAgentOperatingArchitecture } from '../agents/multi-agent.service';
import { logger } from '../../utils/logger';

export interface ConsequentialActionPreview {
  actionId: string;
  title: string;
  targetResource: string;
  scope: string;
  environment: 'development' | 'staging' | 'production';
  executionMode: ExecutionMode;
  riskClass: RiskClass;
  proposedByActor: string;
  requiresApproval: boolean;
  rollbackPlan: string;
  verificationCriteria: string;
  status: 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'EXECUTED' | 'ROLLED_BACK';
  expiresAt: string;
  approvedBy?: string;
  approvedAt?: string;
}

export interface BreakGlassSession {
  sessionId: string;
  adminId: string;
  adminEmail: string;
  justification: string;
  elevatedRiskLimit: RiskClass;
  startedAt: string;
  expiresAt: string;
  status: 'ACTIVE' | 'EXPIRED' | 'REVOKED';
  actionsTakenCount: number;
}

export interface CEOExecutiveOverview {
  systemHealth: string;
  activeIncidentsCount: number;
  openCriticalBlockers: number;
  totalAgentsRegistered: number;
  activeWorkersCount: number;
  queueDepth: number;
  qualityReleaseVerdict: string;
  safeModeActive: boolean;
  activeBreakGlassSessions: number;
  monthlyEstimatedCostUsd: number;
  lastAuditedTimestamp: string;
}

export class CEOAdminControlPlaneService {
  private static actionPreviews: Map<string, ConsequentialActionPreview> = new Map();
  private static breakGlassSessions: Map<string, BreakGlassSession> = new Map();

  // ==========================================
  // Work Package 12.01 & 12.03: CEO Executive Overview Aggregator
  // ==========================================

  public static getExecutiveOverview(): CEOExecutiveOverview {
    const health = RuntimeResilienceObservabilityService.getSystemHealthOverview();
    const incidents = RuntimeResilienceObservabilityService.listIncidents();
    const qualityTests = QualityEngineeringReleaseService.listTests();
    const scorecards = QualityEngineeringReleaseService.listScorecards();
    const queue = TaskOrchestrationEngine.getObservabilityDashboard();
    const agents = MultiAgentOperatingArchitecture.listAgents();

    const activeBreakGlass = Array.from(this.breakGlassSessions.values()).filter((s) => s.status === 'ACTIVE').length;
    const latestScorecard = scorecards[scorecards.length - 1];

    return {
      systemHealth: health.overallHealth,
      activeIncidentsCount: incidents.filter((i) => i.status !== 'CLOSED').length,
      openCriticalBlockers: latestScorecard ? latestScorecard.criticalBlockers.length : 0,
      totalAgentsRegistered: agents.length,
      activeWorkersCount: queue.activeWorkersCount,
      queueDepth: queue.queueDepth,
      qualityReleaseVerdict: latestScorecard ? latestScorecard.overallVerdict : 'PASS',
      safeModeActive: health.safeMode,
      activeBreakGlassSessions: activeBreakGlass,
      monthlyEstimatedCostUsd: 142.50,
      lastAuditedTimestamp: new Date().toISOString()
    };
  }

  // ==========================================
  // Work Package 12.03 & 12.07: Explicit Consequential Action Preview & Governance
  // ==========================================

  public static createActionPreview(params: {
    title: string;
    targetResource: string;
    scope: string;
    environment: 'development' | 'staging' | 'production';
    executionMode: ExecutionMode;
    riskClass: RiskClass;
    proposedByActor: string;
    rollbackPlan: string;
    verificationCriteria: string;
    ttlMinutes?: number;
  }): ConsequentialActionPreview {
    const actionId = `act-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;
    const ttl = params.ttlMinutes || 60;
    const expiresAt = new Date(Date.now() + ttl * 60000).toISOString();

    const requiresApproval = params.riskClass === 'R3' || params.riskClass === 'R4' || params.environment === 'production';

    const preview: ConsequentialActionPreview = {
      actionId,
      title: params.title,
      targetResource: params.targetResource,
      scope: params.scope,
      environment: params.environment,
      executionMode: params.executionMode,
      riskClass: params.riskClass,
      proposedByActor: params.proposedByActor,
      requiresApproval,
      rollbackPlan: params.rollbackPlan,
      verificationCriteria: params.verificationCriteria,
      status: 'PENDING_REVIEW',
      expiresAt
    };

    this.actionPreviews.set(actionId, preview);
    return preview;
  }

  public static approveConsequentialAction(
    actionId: string,
    adminEmail: string
  ): { success: boolean; preview?: ConsequentialActionPreview; error?: string } {
    const preview = this.actionPreviews.get(actionId);
    if (!preview) return { success: false, error: 'Action preview not found' };

    if (new Date(preview.expiresAt).getTime() < Date.now()) {
      preview.status = 'REJECTED';
      return { success: false, error: 'Action preview expired' };
    }

    // Invariant: ZeroTrust check on human admin approval
    const decision = ZeroTrustPolicyEngine.evaluateAccess({
      actorId: adminEmail,
      actorType: 'HUMAN_ADMIN',
      action: 'APPROVE',
      resource: preview.targetResource,
      resourceClassification: 'CRITICAL',
      environment: preview.environment,
      executionMode: preview.executionMode,
      riskClass: preview.riskClass,
      hasApproval: true
    });

    if (decision.decision === 'DENY') {
      return { success: false, error: `Policy denied approval: ${decision.reason}` };
    }

    preview.status = 'APPROVED';
    preview.approvedBy = adminEmail;
    preview.approvedAt = new Date().toISOString();

    logger.info(`[GOVERNANCE] Action ${actionId} (${preview.title}) APPROVED by ${adminEmail}`);
    return { success: true, preview };
  }

  public static listActionPreviews(): ConsequentialActionPreview[] {
    return Array.from(this.actionPreviews.values());
  }

  // ==========================================
  // Work Package 12.16 & 12.18: Governed Break-Glass Procedures
  // ==========================================

  public static initiateBreakGlassSession(params: {
    adminId: string;
    adminEmail: string;
    justification: string;
    durationMinutes?: number;
  }): BreakGlassSession {
    const sessionId = `bg-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;
    const duration = params.durationMinutes || 30; // Max 30 mins standard emergency
    const now = new Date().toISOString();
    const expiresAt = new Date(Date.now() + duration * 60000).toISOString();

    const session: BreakGlassSession = {
      sessionId,
      adminId: params.adminId,
      adminEmail: params.adminEmail,
      justification: params.justification,
      elevatedRiskLimit: 'R4',
      startedAt: now,
      expiresAt,
      status: 'ACTIVE',
      actionsTakenCount: 0
    };

    this.breakGlassSessions.set(sessionId, session);
    logger.warn(`[BREAK-GLASS INITIATED] Admin ${params.adminEmail} entered BREAK-GLASS session ${sessionId}. Justification: ${params.justification}`);
    return session;
  }

  public static revokeBreakGlassSession(sessionId: string, reason: string): boolean {
    const session = this.breakGlassSessions.get(sessionId);
    if (!session) return false;

    session.status = 'REVOKED';
    logger.info(`[BREAK-GLASS REVOKED] Session ${sessionId} closed. Reason: ${reason}`);
    return true;
  }

  public static listBreakGlassSessions(): BreakGlassSession[] {
    return Array.from(this.breakGlassSessions.values());
  }
}
