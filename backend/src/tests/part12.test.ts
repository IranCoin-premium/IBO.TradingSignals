/**
 * IBO Ecosystem — CEO/Admin Control Plane & Human Governance Test Suite
 * Master Prompt — Part 12: Work Packages 12.01 - 12.20
 */

import { CEOAdminControlPlaneService } from '../modules/admin/control-plane.service';

describe('Part 12 — CEO / Admin Control Plane, Human Governance & Command Center', () => {

  describe('Work Package 12.01 & 12.03: Executive Dashboard & Cross-System Aggregation', () => {
    it('should aggregate ecosystem-wide state into high-level CEO executive overview', () => {
      const overview = CEOAdminControlPlaneService.getExecutiveOverview();

      expect(overview.systemHealth).toBeDefined();
      expect(overview.totalAgentsRegistered).toBeGreaterThanOrEqual(1);
      expect(overview.monthlyEstimatedCostUsd).toBeGreaterThan(0);
      expect(overview.lastAuditedTimestamp).toBeDefined();
    });
  });

  describe('Work Package 12.03 & 12.07: Explicit Consequential Action Preview & Governance', () => {
    it('should create an action preview with target scope, risk, rollback plan and verification criteria', () => {
      const preview = CEOAdminControlPlaneService.createActionPreview({
        title: 'Upgrade Trading Indicator Database Index',
        targetResource: 'database:postgres_indexes',
        scope: 'database:schema_alter',
        environment: 'production',
        executionMode: 'PRODUCTION',
        riskClass: 'R3',
        proposedByActor: 'agent-engineering-lead',
        rollbackPlan: 'DROP INDEX CONCURRENTLY idx_signals_timestamp',
        verificationCriteria: 'Query latency p95 < 25ms under 1000 RPS'
      });

      expect(preview.actionId).toBeDefined();
      expect(preview.status).toBe('PENDING_REVIEW');
      expect(preview.requiresApproval).toBe(true);

      // Admin approves
      const approval = CEOAdminControlPlaneService.approveConsequentialAction(preview.actionId, 'ali.khani0916@gmail.com');
      expect(approval.success).toBe(true);
      expect(approval.preview?.status).toBe('APPROVED');
      expect(approval.preview?.approvedBy).toBe('ali.khani0916@gmail.com');
    });

    it('should reject approval on expired action preview', () => {
      const preview = CEOAdminControlPlaneService.createActionPreview({
        title: 'Immediate Maintenance Routine',
        targetResource: 'system:worker_restart',
        scope: 'worker:ops',
        environment: 'staging',
        executionMode: 'STAGING',
        riskClass: 'R2',
        proposedByActor: 'agent-ops',
        rollbackPlan: 'None needed',
        verificationCriteria: 'Health probe returns 200 OK',
        ttlMinutes: -5 // Expired 5 mins ago
      });

      const approval = CEOAdminControlPlaneService.approveConsequentialAction(preview.actionId, 'ali.khani0916@gmail.com');
      expect(approval.success).toBe(false);
      expect(approval.error).toContain('Action preview expired');
    });
  });

  describe('Work Package 12.16 & 12.18: Governed Break-Glass Session Procedures', () => {
    it('should initiate, track and revoke time-limited emergency break-glass sessions', () => {
      const session = CEOAdminControlPlaneService.initiateBreakGlassSession({
        adminId: 'admin-001',
        adminEmail: 'ali.khani0916@gmail.com',
        justification: 'Emergency database lockup mitigation during trading session',
        durationMinutes: 15
      });

      expect(session.sessionId).toBeDefined();
      expect(session.status).toBe('ACTIVE');
      expect(session.elevatedRiskLimit).toBe('R4');

      const revoked = CEOAdminControlPlaneService.revokeBreakGlassSession(session.sessionId, 'Incident resolved');
      expect(revoked).toBe(true);
    });
  });
});
