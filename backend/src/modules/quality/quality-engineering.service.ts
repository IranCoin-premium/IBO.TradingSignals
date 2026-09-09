/**
 * IBO Ecosystem — Quality Engineering, Test Registry & Release Gate Service
 * Master Prompt — Part 11: Work Packages 11.01 - 11.20
 */

import { ExecutionMode, RiskClass } from '../agents/multi-agent.types';
import { logger } from '../../utils/logger';

export type TestCategory =
  | 'UNIT'
  | 'CONTRACT'
  | 'INTEGRATION'
  | 'BROWSER_E2E'
  | 'PWA'
  | 'NATIVE_MOBILE'
  | 'VISUAL_REGRESSION'
  | 'TEXT_QA'
  | 'ACCESSIBILITY'
  | 'SECURITY_REGRESSION'
  | 'PERFORMANCE'
  | 'RESILIENCE'
  | 'FLAKY_QUARANTINE';

export type TestResultStatus = 'PASS' | 'WARN' | 'FAIL' | 'BLOCKED' | 'SKIPPED';

export interface TestRegistryEntry {
  testId: string;
  name: string;
  category: TestCategory;
  owner: string;
  riskClass: RiskClass;
  allowedExecutionModes: ExecutionMode[];
  isFlaky: boolean;
  flakyQuarantineReason?: string;
  lastExecutionStatus?: TestResultStatus;
  lastExecutionEvidence?: string;
  lastExecutedAt?: string;
}

export interface ReleaseQualityScorecard {
  scorecardId: string;
  releaseVersion: string;
  targetEnvironment: 'development' | 'staging' | 'production';
  targetExecutionMode: ExecutionMode;
  evaluatedAt: string;
  overallVerdict: 'PASS' | 'PASS_WITH_WARNINGS' | 'BLOCKED' | 'FAILED';
  totalTests: number;
  passedCount: number;
  warnCount: number;
  failedCount: number;
  blockedCount: number;
  criticalBlockers: string[];
  findingsByTaxonomy: Record<string, number>;
  independentVerifierDecision: 'APPROVED' | 'REJECTED' | 'CONDITIONAL';
  verifierNotes: string;
}

export class QualityEngineeringReleaseService {
  private static testRegistry: Map<string, TestRegistryEntry> = new Map();
  private static releaseScorecards: Map<string, ReleaseQualityScorecard> = new Map();

  static {
    this.seedCanonicalTestRegistry();
  }

  // ==========================================
  // Work Package 11.01 & 11.02: Test Registry & Canonical Taxonomy
  // ==========================================

  private static seedCanonicalTestRegistry(): void {
    const defaultSuites: Array<Omit<TestRegistryEntry, 'lastExecutedAt' | 'lastExecutionStatus'>> = [
      {
        testId: 'TEST-UNIT-CORE-001',
        name: 'Unit Tests — Backend Core, Engine & Auth Models',
        category: 'UNIT',
        owner: 'EngineeringSpecialist',
        riskClass: 'R1',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      },
      {
        testId: 'TEST-CONTRACT-001',
        name: 'Contract Tests — OpenCode, Cline & MCP Schema Invariants',
        category: 'CONTRACT',
        owner: 'EngineeringSpecialist',
        riskClass: 'R1',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      },
      {
        testId: 'TEST-INTEGRATION-001',
        name: 'Integration Tests — Orchestration, Queue & Resilience Hooks',
        category: 'INTEGRATION',
        owner: 'EngineeringSpecialist',
        riskClass: 'R2',
        allowedExecutionModes: ['DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      },
      {
        testId: 'TEST-SEC-REGRESSION-001',
        name: 'Security Regression — Zero-Trust, Secret Leaks & Privilege Defense',
        category: 'SECURITY_REGRESSION',
        owner: 'SecurityGuardian',
        riskClass: 'R0',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      },
      {
        testId: 'TEST-VISUAL-REGRESSION-001',
        name: 'Visual Regression — Multi-Viewport 320px-2560px Soft-UI Matrix',
        category: 'VISUAL_REGRESSION',
        owner: 'VisualQA',
        riskClass: 'R1',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING'],
        isFlaky: false
      },
      {
        testId: 'TEST-TEXT-QA-001',
        name: 'Text QA — RTL Persian/Arabic & Legal Disclaimer Integrity',
        category: 'TEXT_QA',
        owner: 'TextQA',
        riskClass: 'R0',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      },
      {
        testId: 'TEST-ACCESSIBILITY-001',
        name: 'Accessibility — Focus Order, ARIA Semantics & Contrast Thresholds',
        category: 'ACCESSIBILITY',
        owner: 'VisualQA',
        riskClass: 'R1',
        allowedExecutionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING'],
        isFlaky: false
      },
      {
        testId: 'TEST-RESILIENCE-001',
        name: 'Resilience — Circuit Breakers, Recovery Loops & Safe Mode',
        category: 'RESILIENCE',
        owner: 'ObservabilityAgent',
        riskClass: 'R2',
        allowedExecutionModes: ['DRY_RUN', 'STAGING'],
        isFlaky: false
      },
      {
        testId: 'TEST-NATIVE-ANDROID-001',
        name: 'Native Mobile — Android Compose & Robolectric Local JVM Suite',
        category: 'NATIVE_MOBILE',
        owner: 'NativeQASpecialist',
        riskClass: 'R2',
        allowedExecutionModes: ['DRY_RUN', 'STAGING', 'PRODUCTION'],
        isFlaky: false
      }
    ];

    for (const suite of defaultSuites) {
      this.testRegistry.set(suite.testId, {
        ...suite,
        lastExecutionStatus: 'PASS',
        lastExecutedAt: new Date().toISOString()
      });
    }
  }

  public static listTests(): TestRegistryEntry[] {
    return Array.from(this.testRegistry.values());
  }

  public static getTest(testId: string): TestRegistryEntry | undefined {
    return this.testRegistry.get(testId);
  }

  public static updateTestResult(testId: string, status: TestResultStatus, evidence: string): boolean {
    const t = this.testRegistry.get(testId);
    if (!t) return false;

    t.lastExecutionStatus = status;
    t.lastExecutionEvidence = evidence;
    t.lastExecutedAt = new Date().toISOString();
    return true;
  }

  // ==========================================
  // Work Package 11.13: Flaky Test Governance & Quarantine
  // ==========================================

  public static quarantineFlakyTest(testId: string, reason: string): boolean {
    const t = this.testRegistry.get(testId);
    if (!t) return false;

    t.isFlaky = true;
    t.category = 'FLAKY_QUARANTINE';
    t.flakyQuarantineReason = reason;
    logger.warn(`Test ${testId} quarantined as FLAKY: ${reason}`);
    return true;
  }

  // ==========================================
  // Work Package 11.14 & 11.20: Release Quality Scorecard & Gate Evaluation
  // ==========================================

  public static evaluateReleaseGate(params: {
    releaseVersion: string;
    targetEnvironment: 'development' | 'staging' | 'production';
    targetExecutionMode: ExecutionMode;
    testResults: Array<{ testId: string; status: TestResultStatus; notes?: string }>;
    independentVerifier: { name: string; decision: 'APPROVED' | 'REJECTED' | 'CONDITIONAL'; notes: string };
  }): ReleaseQualityScorecard {
    const scorecardId = `gate-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;
    const now = new Date().toISOString();

    const criticalBlockers: string[] = [];
    let passed = 0;
    let warn = 0;
    let failed = 0;
    let blocked = 0;

    const taxonomyCounts: Record<string, number> = {};

    for (const tr of params.testResults) {
      const reg = this.testRegistry.get(tr.testId);
      const cat = reg?.category || 'UNKNOWN';
      taxonomyCounts[cat] = (taxonomyCounts[cat] || 0) + 1;

      if (tr.status === 'PASS') {
        passed++;
      } else if (tr.status === 'WARN') {
        warn++;
      } else if (tr.status === 'FAIL') {
        failed++;
        if (reg?.riskClass === 'R0' || reg?.riskClass === 'R3' || reg?.riskClass === 'R4' || cat === 'SECURITY_REGRESSION') {
          criticalBlockers.push(`Critical failure on [${cat}] ${tr.testId}: ${tr.notes || 'Unsatisfied test assertion'}`);
        }
      } else if (tr.status === 'BLOCKED') {
        blocked++;
        criticalBlockers.push(`Blocked prerequisite on [${cat}] ${tr.testId}`);
      }
    }

    // Invariant: If independent verifier rejected, release is blocked
    if (params.independentVerifier.decision === 'REJECTED') {
      criticalBlockers.push(`Independent verifier (${params.independentVerifier.name}) explicitly REJECTED release: ${params.independentVerifier.notes}`);
    }

    let overallVerdict: 'PASS' | 'PASS_WITH_WARNINGS' | 'BLOCKED' | 'FAILED' = 'PASS';
    if (criticalBlockers.length > 0) {
      overallVerdict = 'BLOCKED';
    } else if (failed > 0) {
      overallVerdict = 'FAILED';
    } else if (warn > 0) {
      overallVerdict = 'PASS_WITH_WARNINGS';
    }

    const scorecard: ReleaseQualityScorecard = {
      scorecardId,
      releaseVersion: params.releaseVersion,
      targetEnvironment: params.targetEnvironment,
      targetExecutionMode: params.targetExecutionMode,
      evaluatedAt: now,
      overallVerdict,
      totalTests: params.testResults.length,
      passedCount: passed,
      warnCount: warn,
      failedCount: failed,
      blockedCount: blocked,
      criticalBlockers,
      findingsByTaxonomy: taxonomyCounts,
      independentVerifierDecision: params.independentVerifier.decision,
      verifierNotes: params.independentVerifier.notes
    };

    this.releaseScorecards.set(scorecardId, scorecard);
    return scorecard;
  }

  public static getScorecard(scorecardId: string): ReleaseQualityScorecard | undefined {
    return this.releaseScorecards.get(scorecardId);
  }

  public static listScorecards(): ReleaseQualityScorecard[] {
    return Array.from(this.releaseScorecards.values());
  }
}
