/**
 * IBO Ecosystem — Quality Engineering, Test Registry & Release Gate Test Suite
 * Master Prompt — Part 11: Work Packages 11.01 - 11.20
 */

import { QualityEngineeringReleaseService } from '../modules/quality/quality-engineering.service';

describe('Part 11 — Quality Engineering, Test Automation, Independent Verification & Release Gates', () => {

  describe('Work Package 11.01 & 11.02: Canonical Test Registry & Classification', () => {
    it('should maintain a comprehensive registry covering unit, contract, integration, visual, text, accessibility, security and resilience suites', () => {
      const tests = QualityEngineeringReleaseService.listTests();

      expect(tests.length).toBeGreaterThanOrEqual(8);
      const categories = tests.map((t) => t.category);
      expect(categories).toContain('UNIT');
      expect(categories).toContain('CONTRACT');
      expect(categories).toContain('INTEGRATION');
      expect(categories).toContain('SECURITY_REGRESSION');
      expect(categories).toContain('VISUAL_REGRESSION');
      expect(categories).toContain('TEXT_QA');
      expect(categories).toContain('ACCESSIBILITY');
      expect(categories).toContain('RESILIENCE');
    });
  });

  describe('Work Package 11.13: Flaky Test Governance & Quarantine Isolation', () => {
    it('should isolate and mark flaky tests to prevent release pollution while preserving debt tracking', () => {
      const testId = 'TEST-UNIT-CORE-001';
      const success = QualityEngineeringReleaseService.quarantineFlakyTest(testId, 'Intermittent timing issue in CI worker');

      expect(success).toBe(true);
      const test = QualityEngineeringReleaseService.getTest(testId);
      expect(test?.isFlaky).toBe(true);
      expect(test?.category).toBe('FLAKY_QUARANTINE');
      expect(test?.flakyQuarantineReason).toContain('Intermittent timing issue');
    });
  });

  describe('Work Package 11.14 & 11.20: Release Quality Scorecard & Independent Verification Gate', () => {
    it('should BLOCK release when a critical security or risk R0/R3 test fails', () => {
      const scorecard = QualityEngineeringReleaseService.evaluateReleaseGate({
        releaseVersion: 'v2.0.0-rc1',
        targetEnvironment: 'production',
        targetExecutionMode: 'PRODUCTION',
        testResults: [
          { testId: 'TEST-UNIT-CORE-001', status: 'PASS' },
          { testId: 'TEST-SEC-REGRESSION-001', status: 'FAIL', notes: 'Simulated SQL injection vulnerability detected' }
        ],
        independentVerifier: {
          name: 'SecurityGuardian',
          decision: 'REJECTED',
          notes: 'Security regression suite has open failures'
        }
      });

      expect(scorecard.overallVerdict).toBe('BLOCKED');
      expect(scorecard.criticalBlockers.length).toBeGreaterThanOrEqual(1);
      expect(scorecard.criticalBlockers[0]).toContain('Critical failure on [SECURITY_REGRESSION]');
    });

    it('should APPROVE release when all test suites pass and independent verifier approves', () => {
      const scorecard = QualityEngineeringReleaseService.evaluateReleaseGate({
        releaseVersion: 'v2.0.0-stable',
        targetEnvironment: 'production',
        targetExecutionMode: 'PRODUCTION',
        testResults: [
          { testId: 'TEST-UNIT-CORE-001', status: 'PASS' },
          { testId: 'TEST-CONTRACT-001', status: 'PASS' },
          { testId: 'TEST-INTEGRATION-001', status: 'PASS' },
          { testId: 'TEST-SEC-REGRESSION-001', status: 'PASS' },
          { testId: 'TEST-TEXT-QA-001', status: 'PASS' }
        ],
        independentVerifier: {
          name: 'ChiefCoordinatorAgent',
          decision: 'APPROVED',
          notes: 'All quality gates, visual proofs, text QA and zero-trust policies satisfied.'
        }
      });

      expect(scorecard.overallVerdict).toBe('PASS');
      expect(scorecard.criticalBlockers.length).toBe(0);
      expect(scorecard.independentVerifierDecision).toBe('APPROVED');
    });
  });
});
