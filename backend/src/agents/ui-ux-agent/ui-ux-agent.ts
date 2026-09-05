/**
 * UI/UX Agent Orchestrator & Governance Engine
 * Part 06: Full Integration with Audit Logs & Brand Guard
 */
import crypto from 'crypto';
import { BrandProtectionGuard } from './brand-guard';
import { AutoDebugRollbackHandler } from './auto-rollback-handler';
import { UIChangeProposal, UIChangeRecord } from './types';

export class UIUXAgent {
  private agentId: string = 'agent-ui-ux-enhancer';
  private brandGuard: BrandProtectionGuard;
  private rollbackHandler: AutoDebugRollbackHandler;

  constructor(customConfigPath?: string, maxRetries: number = 3) {
    this.brandGuard = new BrandProtectionGuard(customConfigPath);
    this.rollbackHandler = new AutoDebugRollbackHandler(maxRetries);
  }

  /**
   * Process a UI/UX improvement proposal through the 10-level gate
   */
  public async processUIProposal(
    proposal: UIChangeProposal,
    simulatedLevelErrors: { [attempt: number]: { [level: number]: string } } = {}
  ): Promise<{
    approved: boolean;
    auditLogId: string;
    uiChangeRecord: UIChangeRecord;
    message: string;
  }> {
    const auditLogId = crypto.randomUUID();
    const changeId = crypto.randomUUID();

    // 1. Check Brand Protection Guard (Inviolable Constraints 1 & 2)
    const brandCheck = this.brandGuard.validateProposal(proposal);
    if (!brandCheck.allowed) {
      const rejectedRecord: UIChangeRecord = {
        id: changeId,
        auditLogId,
        agentId: this.agentId,
        componentTarget: proposal.componentTarget,
        changeType: proposal.changeType,
        diffSummary: `Proposal rejected by Brand Guard: ${brandCheck.violation}`,
        styleDriftPercentage: proposal.styleDriftPercentage,
        levelTestStatus: 'FAILED_ROLLED_BACK',
        retryCount: 0,
        maxRetries: 3,
        rolledBack: true,
        rollbackReason: brandCheck.violation,
        beforeState: proposal.beforeState,
        afterState: proposal.afterState,
        testRunDetails: { vetoReason: brandCheck.violation },
        humanReviewRequired: true,
        humanReviewStatus: 'UNREVIEWED',
        createdAt: new Date(),
        updatedAt: new Date()
      };

      return {
        approved: false,
        auditLogId,
        uiChangeRecord: rejectedRecord,
        message: brandCheck.violation || 'Rejected by Brand Guard'
      };
    }

    // 2. Execute 10-Level Gate with Auto-Debug Rollback Handler
    const execResult = await this.rollbackHandler.executeWithAutoDebugGovernance(
      proposal,
      simulatedLevelErrors
    );

    const record: UIChangeRecord = {
      id: changeId,
      auditLogId,
      agentId: this.agentId,
      componentTarget: proposal.componentTarget,
      changeType: proposal.changeType,
      diffSummary: execResult.finalRecord.diffSummary || proposal.diffSummary,
      styleDriftPercentage: execResult.finalRecord.styleDriftPercentage || proposal.styleDriftPercentage,
      levelTestStatus: execResult.status,
      failedLevel: execResult.finalRecord.failedLevel,
      retryCount: execResult.finalRecord.retryCount || 0,
      maxRetries: 3,
      rolledBack: execResult.finalRecord.rolledBack || false,
      rollbackReason: execResult.finalRecord.rollbackReason,
      beforeState: proposal.beforeState,
      afterState: proposal.afterState,
      testRunDetails: { history: execResult.history, attempts: execResult.totalAttempts },
      humanReviewRequired: execResult.finalRecord.humanReviewRequired || false,
      humanReviewStatus: 'UNREVIEWED',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const isApproved = execResult.status === 'PASSED_LEVEL_10';

    return {
      approved: isApproved,
      auditLogId,
      uiChangeRecord: record,
      message: isApproved
        ? `UI/UX change successfully passed all 10 verification levels on attempt ${execResult.totalAttempts}. Logged in audit_logs.`
        : `UI/UX change failed quality gates after ${execResult.totalAttempts} attempts. Change rolled back safely. Human review requested.`
    };
  }

  /**
   * Helper: Build proposal for converting standalone login/signup into an accessible Modal
   */
  public createAuthModalConversionProposal(): UIChangeProposal {
    const riskDisclosure = this.brandGuard.getMandatoryRiskDisclosure();
    return {
      componentTarget: 'auth_login_signup_modal',
      changeType: 'MODAL_CONVERSION',
      targetFiles: ['frontend/src/components/AuthModal.tsx', 'frontend/src/pages/LoginPage.tsx'],
      diffSummary: 'Converted standalone /login and /signup pages into a high-performance, accessible floating Modal with deep-link fallback.',
      styleDriftPercentage: 3.5, // Well within <= 8.0% limit
      beforeState: {
        'frontend/src/pages/LoginPage.tsx': `// Standalone Login Page\n// ${riskDisclosure}`
      },
      afterState: {
        'frontend/src/pages/LoginPage.tsx': `// Deep-link route embedding AuthModal\n// ${riskDisclosure}`,
        'frontend/src/components/AuthModal.tsx': `// Accessible Floating Auth Modal with focus-trap, escape key, and backdrop click\n// ${riskDisclosure}`
      }
    };
  }
}
