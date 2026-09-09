/**
 * IBO Ecosystem — Production Readiness, Release Engineering & Deployment Governance Test Suite
 * Master Prompt — Part 14: Work Packages 14.01 - 14.20
 */

import { ProductionReleaseEngineeringService } from '../modules/release/production-release.service';
import { QualityEngineeringReleaseService } from '../modules/quality/quality-engineering.service';
import { CEOAdminControlPlaneService } from '../modules/admin/control-plane.service';

describe('Part 14 — Production Readiness, CI/CD, Release Engineering & Deployment Governance', () => {

  let validScorecardId: string;
  let blockedScorecardId: string;
  let approvedActionId: string;

  beforeAll(() => {
    // Evaluate a passing scorecard
    const passScorecard = QualityEngineeringReleaseService.evaluateReleaseGate({
      releaseVersion: 'v2.0.0-final',
      targetEnvironment: 'production',
      targetExecutionMode: 'PRODUCTION',
      testResults: [
        { testId: 'TEST-UNIT-CORE-001', status: 'PASS' },
        { testId: 'TEST-SEC-REGRESSION-001', status: 'PASS' }
      ],
      independentVerifier: {
        name: 'ChiefAgentCoordinator',
        decision: 'APPROVED',
        notes: 'All quality gates passed.'
      }
    });
    validScorecardId = passScorecard.scorecardId;

    // Evaluate a blocked scorecard
    const blockScorecard = QualityEngineeringReleaseService.evaluateReleaseGate({
      releaseVersion: 'v2.0.0-broken',
      targetEnvironment: 'production',
      targetExecutionMode: 'PRODUCTION',
      testResults: [
        { testId: 'TEST-SEC-REGRESSION-001', status: 'FAIL', notes: 'Security check failed' }
      ],
      independentVerifier: {
        name: 'SecurityGuardian',
        decision: 'REJECTED',
        notes: 'Rejected due to security check failure'
      }
    });
    blockedScorecardId = blockScorecard.scorecardId;

    // Create an approved consequential action preview
    const action = CEOAdminControlPlaneService.createActionPreview({
      title: 'Deploy Production Release v2.0.0-final',
      targetResource: 'deployment:cloud_run_prod',
      scope: 'prod:deployment',
      environment: 'production',
      executionMode: 'PRODUCTION',
      riskClass: 'R3',
      proposedByActor: 'human-owner',
      rollbackPlan: 'Rollback to v1.9.5 revision',
      verificationCriteria: 'Health probe returns 200 OK across 3 instances'
    });

    CEOAdminControlPlaneService.approveConsequentialAction(action.actionId, 'ali.khani0916@gmail.com');
    approvedActionId = action.actionId;
  });

  describe('Work Package 14.01 & 14.03: Release Candidate Governance & Quality Gate Invariants', () => {
    it('should register release candidate as READY_FOR_DEPLOYMENT when scorecard passes', () => {
      const rc = ProductionReleaseEngineeringService.createReleaseCandidate({
        version: 'v2.0.0-final',
        gitCommitHash: 'a1b2c3d4e5f6',
        environmentTarget: 'production',
        scorecardId: validScorecardId
      });

      expect(rc.releaseCandidateId).toBeDefined();
      expect(rc.verificationStatus).toBe('READY_FOR_DEPLOYMENT');
    });

    it('should REJECT release candidate creation if associated scorecard has BLOCKED or FAILED verdict', () => {
      const rc = ProductionReleaseEngineeringService.createReleaseCandidate({
        version: 'v2.0.0-broken',
        gitCommitHash: 'ff00ff00ff00',
        environmentTarget: 'production',
        scorecardId: blockedScorecardId
      });

      expect(rc.verificationStatus).toBe('REJECTED');
    });
  });

  describe('Work Package 14.10 & 14.18: Rollback Rehearsal & Disaster Readiness', () => {
    it('should execute deterministic automated rollback rehearsal in sub-2-second target window', () => {
      const rcList = ProductionReleaseEngineeringService.listCandidates();
      const targetRc = rcList[0];

      const rehearsal = ProductionReleaseEngineeringService.executeRollbackRehearsal(
        targetRc.releaseCandidateId,
        'Simulated latency spike on primary API'
      );

      expect(rehearsal.status).toBe('PASSED');
      expect(rehearsal.timeToRollbackMs).toBeLessThan(2000);
      expect(rehearsal.dataIntegrityCheck).toBe('INTACT');
    });
  });

  describe('Work Package 14.05 & 14.14: Governed Production Promotion', () => {
    it('should promote release candidate to DEPLOYED when approved action preview is present', () => {
      const rcList = ProductionReleaseEngineeringService.listCandidates();
      const readyRc = rcList.find((c) => c.verificationStatus === 'READY_FOR_DEPLOYMENT');

      const promotion = ProductionReleaseEngineeringService.promoteToProduction({
        releaseCandidateId: readyRc!.releaseCandidateId,
        approvedByAdminEmail: 'ali.khani0916@gmail.com',
        actionPreviewId: approvedActionId
      });

      expect(promotion.success).toBe(true);
      expect(promotion.releaseCandidate?.verificationStatus).toBe('DEPLOYED');
    });

    it('should deny promotion if consequential action preview is missing or unapproved', () => {
      const readyRc = ProductionReleaseEngineeringService.createReleaseCandidate({
        version: 'v2.0.1-candidate',
        gitCommitHash: 'c3d4e5f6a1b2',
        environmentTarget: 'production',
        scorecardId: validScorecardId
      });

      const promotion = ProductionReleaseEngineeringService.promoteToProduction({
        releaseCandidateId: readyRc.releaseCandidateId,
        approvedByAdminEmail: 'ali.khani0916@gmail.com',
        actionPreviewId: 'non-existent-action-id'
      });

      expect(promotion.success).toBe(false);
      expect(promotion.error).toContain('Valid APPROVED Consequential Action Preview required');
    });
  });
});
