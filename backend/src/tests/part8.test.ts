/**
 * IBO Ecosystem — Multi-Agent Operating Architecture Test Suite
 * Master Prompt — Part 08: Work Packages 8.1 - 8.28
 */

import { MultiAgentOperatingArchitecture } from '../modules/agents/multi-agent.service';
import { TaskOrchestrationEngine } from '../modules/orchestration/orchestration.service';
import { AgentContract } from '../modules/agents/multi-agent.types';

describe('Part 08 — Multi-Agent Operating Architecture & Governance', () => {

  describe('Work Package 8.2 & 8.3: Agent Registry & Machine-Readable Contracts', () => {
    it('should register and retrieve canonical specialists with valid contracts', () => {
      const agents = MultiAgentOperatingArchitecture.listAgents();
      expect(agents.length).toBeGreaterThanOrEqual(4);

      const chief = MultiAgentOperatingArchitecture.getAgent('agent-chief-coordinator');
      expect(chief).toBeDefined();
      expect(chief?.contract.identity.agentName).toBe('Chief Agent Coordinator');
      expect(chief?.contract.scope.forbidden).toContain('direct_trading_execution');
      expect(chief?.lifecycleStatus).toBe('READY');
    });

    it('should allow dynamic registration of compliant specialist agent', () => {
      const customContract: AgentContract = {
        identity: {
          agentId: 'agent-seo-intelligence',
          agentName: 'SEO Intelligence Specialist',
          version: '1.0.0',
          agentType: 'MARKETING',
          owner: 'ChiefAgentCoordinator'
        },
        mission: 'Technical SEO audits, schema markup verification and regional sitemap analysis.',
        scope: {
          allowed: ['sitemap_audit', 'meta_tag_review'],
          forbidden: ['modify_core_backend', 'direct_live_publish']
        },
        capabilities: ['SEO_AUDIT', 'SCHEMA_CHECK'],
        inputs: ['URL_LIST'],
        outputs: ['SEO_REPORT'],
        tools: { allowed: ['HTTP_GET'], denied: ['FILE_WRITE'] },
        mcpAccess: ['mcp-core-governance'],
        dataScope: ['META_TAGS'],
        memoryScope: 'TASK',
        executionModes: ['READ_ONLY', 'DRY_RUN'],
        riskClass: 'R0',
        permissions: ['READ_PUBLIC_PAGES'],
        delegationLimits: { maxDepth: 1, maxChildren: 2, maxActiveChildren: 1 },
        approvalPolicy: { requiredForModes: [], requiredForRisk: [] },
        verificationPolicy: { requiresIndependentVerifier: false, verifierRoles: [] },
        budget: { timeBudgetMs: 15000, tokenBudget: 10000, costBudgetUsd: 0.1 },
        failurePolicy: { maxRetries: 1, escalateOnNonRetryable: true }
      };

      MultiAgentOperatingArchitecture.registerAgent(customContract);
      const retrieved = MultiAgentOperatingArchitecture.getAgent('agent-seo-intelligence');
      expect(retrieved).toBeDefined();
      expect(retrieved?.contract.capabilities).toContain('SEO_AUDIT');
    });
  });

  describe('Work Package 8.4 & 8.5: Capability Registry & Agent Router', () => {
    it('should route task to agent based on required capabilities and execution mode', () => {
      const routeResult = MultiAgentOperatingArchitecture.routeTaskToAgent(
        ['VIEWPORT_CHECK', 'VISUAL_DIFF'],
        'STAGING',
        'R1'
      );

      expect(routeResult.selectedAgentId).toBe('agent-visual-qa');
    });

    it('should fail closed when no healthy agent satisfies required capabilities', () => {
      const routeResult = MultiAgentOperatingArchitecture.routeTaskToAgent(
        ['NON_EXISTENT_QUANTUM_CAPABILITY'],
        'STAGING',
        'R1'
      );

      expect(routeResult.selectedAgentId).toBeUndefined();
      expect(routeResult.reason).toContain('No healthy agent satisfies capabilities');
    });
  });

  describe('Work Package 8.7 & 8.10: Governed Delegation & Execution Mode Gate', () => {
    it('should block child task from elevating execution mode above parent authority', () => {
      // Create parent task in STAGING mode
      const { task: parentTask } = TaskOrchestrationEngine.createTask({
        type: 'feature_dev',
        title: 'Parent feature development in Staging',
        description: 'Implement new UI component',
        requester: 'ChiefAgentCoordinator',
        priority: 'P2',
        severity: 'LOW',
        owner: 'agent-chief-coordinator',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 2,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['PLANNING'],
        correlationId: 'corr-del-01',
        traceId: 'trace-del-01'
      });

      // Child attempts PRODUCTION execution mode (Privilege escalation)
      const delegateResult = MultiAgentOperatingArchitecture.delegateSubTask(parentTask.taskId, {
        type: 'direct_prod_deploy',
        title: 'Child attempting unauthorized production execution',
        description: 'Deploy to live servers',
        requiredCapabilities: ['CODE_EDIT'],
        executionMode: 'PRODUCTION', // Higher than parent
        riskClass: 'R3',
        assignedAgentId: 'agent-engineering-lead'
      });

      expect(delegateResult.success).toBe(false);
      expect(delegateResult.error).toContain('Privilege escalation blocked');
    });

    it('should successfully delegate child task within permitted authority bounds', () => {
      const { task: parentTask } = TaskOrchestrationEngine.createTask({
        type: 'feature_dev',
        title: 'Parent feature task',
        description: 'Parent staging work',
        requester: 'ChiefAgentCoordinator',
        priority: 'P2',
        severity: 'LOW',
        owner: 'agent-chief-coordinator',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 2,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['PLANNING'],
        correlationId: 'corr-del-02',
        traceId: 'trace-del-02'
      });

      const delegateResult = MultiAgentOperatingArchitecture.delegateSubTask(parentTask.taskId, {
        type: 'code_refactor',
        title: 'Child refactoring subtask',
        description: 'Refactor layout styles',
        requiredCapabilities: ['CODE_EDIT'],
        executionMode: 'STAGING',
        riskClass: 'R1',
        assignedAgentId: 'agent-engineering-lead'
      });

      expect(delegateResult.success).toBe(true);
      expect(delegateResult.childTask).toBeDefined();
      expect(delegateResult.childTask?.parentTaskId).toBe(parentTask.taskId);
    });
  });

  describe('Work Package 8.9: Conflict Resolution & Preservation of Dissent', () => {
    it('should fail closed (BLOCK) when Security specialist reports FAIL', () => {
      const resolution = MultiAgentOperatingArchitecture.resolveSpecialistConflict([
        { agentId: 'agent-engineering-lead', role: 'Engineering', verdict: 'PASS', evidence: 'Code compiles', confidence: 0.95 },
        { agentId: 'agent-visual-qa', role: 'VisualQA', verdict: 'PASS', evidence: 'Layout aligns', confidence: 0.90 },
        { agentId: 'agent-security-guardian', role: 'Security', verdict: 'FAIL', evidence: 'Raw secret token pattern found', confidence: 0.99 }
      ]);

      expect(resolution.finalVerdict).toBe('BLOCK');
      expect(resolution.dissentRecords.length).toBe(1);
      expect(resolution.rationale).toContain('Security policy conflict detected; failing closed');
    });

    it('should escalate to human admin when specialists disagree on non-security outcomes', () => {
      const resolution = MultiAgentOperatingArchitecture.resolveSpecialistConflict([
        { agentId: 'agent-engineering-lead', role: 'Engineering', verdict: 'PASS', evidence: 'Tests pass', confidence: 0.90 },
        { agentId: 'agent-visual-qa', role: 'VisualQA', verdict: 'FAIL', evidence: '360px viewport overflow', confidence: 0.88 }
      ]);

      expect(resolution.finalVerdict).toBe('ESCALATE_HUMAN');
      expect(resolution.dissentRecords.length).toBe(1);
    });
  });

  describe('Work Package 8.11: Risk Matrix & Approval Gates', () => {
    it('should create pending approval for R3/R4 tasks and update task status on decision', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'schema_migration',
        title: 'Alter database tables',
        description: 'Add index',
        requester: 'ChiefAgentCoordinator',
        priority: 'P1',
        severity: 'HIGH',
        owner: 'agent-engineering-lead',
        environment: 'staging',
        maximumRuntimeMs: 30000,
        maxRetries: 1,
        approvalStatus: 'PENDING',
        requiredCapabilities: ['CODE_EDIT'],
        correlationId: 'corr-appr-01',
        traceId: 'trace-appr-01'
      });

      const approval = MultiAgentOperatingArchitecture.requestApproval({
        taskId: task.taskId,
        requestedBy: 'agent-engineering-lead',
        action: 'APPLY_DB_INDEX',
        environment: 'staging',
        executionMode: 'STAGING',
        riskClass: 'R3',
        scope: ['database:schema'],
        evidence: { migrationSql: 'CREATE INDEX idx_orders ON orders(created_at);' }
      });

      expect(approval.status).toBe('PENDING');

      // Admin approves
      const decision = MultiAgentOperatingArchitecture.submitApprovalDecision(
        approval.approvalId,
        'APPROVED',
        'admin@ibo.com',
        'Migration approved after staging inspection'
      );

      expect(decision.success).toBe(true);
      expect(decision.approval?.status).toBe('APPROVED');
      expect(TaskOrchestrationEngine.getTask(task.taskId)?.approvalStatus).toBe('APPROVED');
    });
  });

  describe('Work Package 8.21 & 8.22: Agent Quarantine & Decision Journal', () => {
    it('should quarantine abnormal agent and log to decision journal', () => {
      const quarantineResult = MultiAgentOperatingArchitecture.quarantineAgent(
        'agent-engineering-lead',
        'Repeated malformed output and schema failure'
      );

      expect(quarantineResult).toBe(true);
      expect(MultiAgentOperatingArchitecture.getAgent('agent-engineering-lead')?.lifecycleStatus).toBe('QUARANTINED');

      const entry = MultiAgentOperatingArchitecture.recordDecisionJournal({
        objective: 'Quarantine malfunctioning engineering agent',
        contextSnapshot: { agentId: 'agent-engineering-lead' },
        specialistsConsulted: ['agent-chief-coordinator', 'agent-security-guardian'],
        evidence: { failureCount: 5 },
        conflictsDetected: [],
        optionsConsidered: ['Retry', 'Quarantine'],
        decision: 'Quarantine agent and assign fallback worker',
        riskClass: 'R2',
        executionPlan: ['quarantineAgent', 'alertAdmin'],
        verificationPlan: ['verifyNoNewTasksAssigned']
      });

      expect(entry.decisionId).toBeDefined();
      expect(MultiAgentOperatingArchitecture.getDecisionJournal().length).toBeGreaterThanOrEqual(1);

      // Restore agent for subsequent test cleanups
      const restored = MultiAgentOperatingArchitecture.getAgent('agent-engineering-lead')!;
      restored.lifecycleStatus = 'READY';
      restored.healthStatus = 'HEALTHY';
    });
  });
});
