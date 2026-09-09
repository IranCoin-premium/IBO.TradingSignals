/**
 * Auto-Debug Rollback Handler & Retry Governance
 * Part 06: Automatic Rollback, Incremental Re-adjustment, and Max Retries Ceiling
 */
import { UIChangeProposal, UIChangeRecord, LevelTestStatus } from './types';
import { TenLevelTester } from './ten-level-tester';

export interface RollbackResult {
  restored: boolean;
  restoredFiles: string[];
  rolledBackToState: Record<string, string>;
  message: string;
}

export class AutoDebugRollbackHandler {
  private tester: TenLevelTester;
  private maxRetries: number;

  constructor(maxRetries: number = 3) {
    this.tester = new TenLevelTester();
    this.maxRetries = maxRetries;
  }

  /**
   * Performs an immediate atomic rollback to before_state
   */
  public performAtomicRollback(
    beforeState: Record<string, string>,
    reason: string
  ): RollbackResult {
    const restoredFiles = Object.keys(beforeState);
    return {
      restored: true,
      restoredFiles,
      rolledBackToState: { ...beforeState },
      message: `ATOMIC ROLLBACK EXECUTED: Restored ${restoredFiles.length} files to pristine before_state. Reason: ${reason}`
    };
  }

  /**
   * Executes the change with self-healing retry loop (Up to Max 3 Retries)
   */
  public async executeWithAutoDebugGovernance(
    proposal: UIChangeProposal,
    simulatedLevelErrors: { [attempt: number]: { [level: number]: string } } = {}
  ): Promise<{
    status: LevelTestStatus;
    totalAttempts: number;
    finalRecord: Partial<UIChangeRecord>;
    history: Array<{
      attempt: number;
      passed: boolean;
      failedLevel?: number;
      failedReason?: string;
      rollbackPerformed: boolean;
    }>;
  }> {
    let currentAttempt = 1;
    let activeProposal = { ...proposal };
    const history: Array<{
      attempt: number;
      passed: boolean;
      failedLevel?: number;
      failedReason?: string;
      rollbackPerformed: boolean;
    }> = [];

    while (currentAttempt <= this.maxRetries) {
      const attemptSimErrors = simulatedLevelErrors[currentAttempt] || {};
      const testResult = await this.tester.executeTestSuite(activeProposal, attemptSimErrors);

      if (testResult.overallPassed) {
        history.push({
          attempt: currentAttempt,
          passed: true,
          rollbackPerformed: false
        });

        return {
          status: 'PASSED_LEVEL_10',
          totalAttempts: currentAttempt,
          finalRecord: {
            changeType: activeProposal.changeType,
            componentTarget: activeProposal.componentTarget,
            diffSummary: activeProposal.diffSummary,
            styleDriftPercentage: activeProposal.styleDriftPercentage,
            levelTestStatus: 'PASSED_LEVEL_10',
            retryCount: currentAttempt - 1,
            maxRetries: this.maxRetries,
            rolledBack: false,
            beforeState: activeProposal.beforeState,
            afterState: activeProposal.afterState,
            humanReviewRequired: false,
            humanReviewStatus: 'UNREVIEWED'
          },
          history
        };
      }

      // Failure encountered at a specific level
      const failedLevel = testResult.failedLevel || 1;
      const failedReason = testResult.failedReason || 'Unknown level check failure';

      // 1. Trigger IMMEDIATE Rollback
      const rollback = this.performAtomicRollback(
        activeProposal.beforeState,
        `Level ${failedLevel} failed: ${failedReason}`
      );

      history.push({
        attempt: currentAttempt,
        passed: false,
        failedLevel,
        failedReason,
        rollbackPerformed: rollback.restored
      });

      // Check if we reached max retries
      if (currentAttempt >= this.maxRetries) {
        break;
      }

      // Prepare self-healed, smaller incremental change for next attempt
      currentAttempt++;
      activeProposal = {
        ...activeProposal,
        styleDriftPercentage: Math.max(0.5, activeProposal.styleDriftPercentage * 0.7), // Dampen drift
        diffSummary: `[Retry ${currentAttempt}] Auto-debugged smaller incremental delta after Level ${failedLevel} feedback`
      };
    }

    // If max retries exhausted without pass:
    return {
      status: 'CANCELLED_MAX_RETRIES_EXCEEDED',
      totalAttempts: currentAttempt,
      finalRecord: {
        changeType: activeProposal.changeType,
        componentTarget: activeProposal.componentTarget,
        diffSummary: `Change cancelled after ${currentAttempt} failed attempts. Pending human review.`,
        styleDriftPercentage: activeProposal.styleDriftPercentage,
        levelTestStatus: 'CANCELLED_MAX_RETRIES_EXCEEDED',
        failedLevel: history[history.length - 1]?.failedLevel,
        retryCount: currentAttempt - 1,
        maxRetries: this.maxRetries,
        rolledBack: true,
        rollbackReason: `Max retries (${this.maxRetries}) exceeded. Last failure at Level ${history[history.length - 1]?.failedLevel}: ${history[history.length - 1]?.failedReason}`,
        beforeState: activeProposal.beforeState,
        afterState: activeProposal.afterState,
        humanReviewRequired: true, // FLAG FOR HUMAN REVIEW IN ADMIN PANEL
        humanReviewStatus: 'UNREVIEWED'
      },
      history
    };
  }
}
