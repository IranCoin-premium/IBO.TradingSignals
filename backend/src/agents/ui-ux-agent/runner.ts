/**
 * CLI / GitHub Action Entrypoint for UI/UX Agent Runner
 * Part 06: Scheduled execution (every 4 hours)
 */
import { UIUXAgent } from './ui-ux-agent';

async function main() {
  console.log('================================================================');
  console.log('  IBO UI/UX CONTINUOUS EVOLUTION & QUALITY GATE AGENT');
  console.log('  Schedule: Every 4 Hours | 10-Level Quality Gate & Auto-Rollback');
  console.log('================================================================');

  const agent = new UIUXAgent();
  const proposal = agent.createAuthModalConversionProposal();

  console.log(`[TARGET] Component: ${proposal.componentTarget}`);
  console.log(`[TARGET] Change Type: ${proposal.changeType}`);
  console.log(`[TARGET] Style Drift: ${proposal.styleDriftPercentage}% (Limit: <= 8.0%)`);

  const result = await agent.processUIProposal(proposal);

  console.log(`[RESULT] Approved: ${result.approved}`);
  console.log(`[RESULT] Audit Log ID: ${result.auditLogId}`);
  console.log(`[RESULT] Status: ${result.uiChangeRecord.levelTestStatus}`);
  console.log(`[RESULT] Message: ${result.message}`);

  if (!result.approved && result.uiChangeRecord.humanReviewRequired) {
    console.warn('[NOTICE] Human review flagged in admin dashboard.');
  }

  console.log('=== UI/UX Quality Gate Check Completed ===');
}

main().catch(err => {
  console.error('[FATAL] UI/UX Agent runner crashed:', err);
  process.exit(1);
});
