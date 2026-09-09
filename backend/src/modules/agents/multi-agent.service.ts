/**
 * IBO Ecosystem — Multi-Agent Operating Architecture Core
 * Master Prompt — Part 08: Work Packages 8.1 - 8.28
 */

import {
  AgentContract,
  RegisteredAgent,
  ExecutionMode,
  RiskClass,
  ApprovalObject,
  DecisionJournalEntry,
  StructuredAgentMessage,
  TaskGraph,
  TaskGraphNode,
  CollaborationMode
} from './multi-agent.types';
import { TaskOrchestrationEngine } from '../orchestration/orchestration.service';
import { OrchestrationTask, OrchestrationTaskStatus } from '../orchestration/orchestration.types';
import { logger } from '../../utils/logger';

export class MultiAgentOperatingArchitecture {
  private static agentRegistry: Map<string, RegisteredAgent> = new Map();
  private static approvals: Map<string, ApprovalObject> = new Map();
  private static decisionJournal: DecisionJournalEntry[] = [];
  private static messageLogs: StructuredAgentMessage[] = [];
  private static taskGraphs: Map<string, TaskGraph> = new Map();

  static {
    this.seedDefaultSpecialists();
  }

  // ==========================================
  // Work Package 8.2 & 8.3: Agent Registry & Contracts
  // ==========================================

  private static seedDefaultSpecialists(): void {
    const specialists: AgentContract[] = [
      {
        identity: {
          agentId: 'agent-chief-coordinator',
          agentName: 'Chief Agent Coordinator',
          version: '1.0.0',
          agentType: 'COORDINATOR',
          owner: 'HumanAdmin'
        },
        mission: 'Strategic orchestration, task graph construction, specialist consultation and governance enforcement.',
        scope: {
          allowed: ['plan_generation', 'specialist_consultation', 'task_graph_dispatch', 'evidence_review', 'risk_analysis'],
          forbidden: ['direct_trading_execution', 'bypass_policy', 'bypass_execution_mode', 'unrestricted_child_spawning']
        },
        capabilities: ['PLANNING', 'DELEGATION', 'RISK_ANALYSIS', 'TASK_GRAPH_BUILDER', 'SPECIALIST_ROUTING'],
        inputs: ['USER_OBJECTIVE', 'INCIDENT_ALERT', 'SCHEDULED_TRIGGER'],
        outputs: ['TASK_GRAPH', 'DELEGATED_TASK', 'ADMIN_SAFE_EXPLANATION', 'ESCALATION_PACKAGE'],
        tools: {
          allowed: ['REGISTRY_QUERY', 'TASK_DISPATCH', 'OBSERVABILITY_QUERY'],
          denied: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS', 'DIRECT_DB_DROP']
        },
        mcpAccess: ['mcp-core-governance'],
        dataScope: ['ALL_METADATA', 'AUDIT_LOGS'],
        memoryScope: 'GLOBAL_PROJECT',
        executionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        riskClass: 'R2',
        permissions: ['ORCHESTRATION_MANAGE', 'TASK_DELEGATE'],
        delegationLimits: { maxDepth: 4, maxChildren: 12, maxActiveChildren: 4 },
        approvalPolicy: { requiredForModes: ['PRODUCTION'], requiredForRisk: ['R3', 'R4'] },
        verificationPolicy: { requiresIndependentVerifier: true, verifierRoles: ['RELEASE_GOVERNANCE', 'HUMAN_ADMIN'] },
        budget: { timeBudgetMs: 120000, tokenBudget: 100000, costBudgetUsd: 1.0 },
        failurePolicy: { maxRetries: 2, escalateOnNonRetryable: true }
      },
      {
        identity: {
          agentId: 'agent-engineering-lead',
          agentName: 'Engineering Specialist Agent',
          version: '1.0.0',
          agentType: 'ENGINEERING',
          owner: 'ChiefAgentCoordinator'
        },
        mission: 'Software implementation, refactoring, test execution and build validation within sandboxed workspaces.',
        scope: {
          allowed: ['code_edit', 'test_execution', 'build_validation', 'workspace_management'],
          forbidden: ['production_deployment_direct', 'trading_logic_alteration', 'secret_extraction']
        },
        capabilities: ['CODE_EDIT', 'RUN_TESTS', 'INSPECT_REPO', 'BUILD_VALIDATION'],
        inputs: ['ENGINEERING_TASK_SPEC'],
        outputs: ['WORKER_RESULT', 'DIFF_PATCH', 'TEST_EVIDENCE'],
        tools: {
          allowed: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER', 'LINTER'],
          denied: ['LIVE_TRADING_EXECUTE', 'SECRET_DUMP']
        },
        mcpAccess: ['mcp-core-governance'],
        dataScope: ['SRC_CODE', 'TEST_FILES', 'CONFIG_DEV'],
        memoryScope: 'TEAM',
        executionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING'],
        riskClass: 'R2',
        permissions: ['WORKSPACE_WRITE', 'TEST_EXECUTE'],
        delegationLimits: { maxDepth: 2, maxChildren: 4, maxActiveChildren: 2 },
        approvalPolicy: { requiredForModes: ['PRODUCTION'], requiredForRisk: ['R3', 'R4'] },
        verificationPolicy: { requiresIndependentVerifier: true, verifierRoles: ['CODE_REVIEW_AGENT', 'QA_AGENT'] },
        budget: { timeBudgetMs: 60000, tokenBudget: 50000, costBudgetUsd: 0.5 },
        failurePolicy: { maxRetries: 3, escalateOnNonRetryable: true }
      },
      {
        identity: {
          agentId: 'agent-visual-qa',
          agentName: 'Visual QA & Viewport Specialist',
          version: '1.0.0',
          agentType: 'QA',
          owner: 'ChiefAgentCoordinator'
        },
        mission: 'Multi-viewport layout, spacing, typography, contrast and brand presentation verification.',
        scope: {
          allowed: ['viewport_check', 'typography_review', 'clipping_detection', 'soft_ui_audit'],
          forbidden: ['backend_code_edit', 'trading_logic_changes', 'payment_decisions']
        },
        capabilities: ['VIEWPORT_CHECK', 'VISUAL_DIFF', 'SOFT_UI_AUDIT', 'BRAND_CHECK'],
        inputs: ['SCREENSHOT_REF', 'VIEWPORT_MATRIX'],
        outputs: ['VISUAL_QA_EVIDENCE', 'DEFECT_FINDINGS'],
        tools: {
          allowed: ['VIEWPORT_RENDER', 'DIFF_INSPECT'],
          denied: ['FILE_WRITE', 'LIVE_TRADING_EXECUTE']
        },
        mcpAccess: ['mcp-core-governance'],
        dataScope: ['SCREENSHOTS', 'UI_TOKENS'],
        memoryScope: 'TASK',
        executionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING'],
        riskClass: 'R1',
        permissions: ['READ_UI_ASSETS'],
        delegationLimits: { maxDepth: 1, maxChildren: 2, maxActiveChildren: 1 },
        approvalPolicy: { requiredForModes: ['PRODUCTION'], requiredForRisk: ['R3', 'R4'] },
        verificationPolicy: { requiresIndependentVerifier: false, verifierRoles: [] },
        budget: { timeBudgetMs: 30000, tokenBudget: 20000, costBudgetUsd: 0.2 },
        failurePolicy: { maxRetries: 2, escalateOnNonRetryable: true }
      },
      {
        identity: {
          agentId: 'agent-security-guardian',
          agentName: 'Security & Secret Policy Guardian',
          version: '1.0.0',
          agentType: 'SECURITY',
          owner: 'HumanAdmin'
        },
        mission: 'Immutable secret leak prevention, authorization enforcement, and zero-trust policy evaluation.',
        scope: {
          allowed: ['secret_leak_scan', 'permission_audit', 'policy_evaluation', 'attack_vector_defense'],
          forbidden: ['bypass_security_rules', 'export_unredacted_secrets']
        },
        capabilities: ['SECURITY_AUDIT', 'SECRET_SCAN', 'POLICY_EVALUATION'],
        inputs: ['CODEBASE_DIFF', 'EVENT_PAYLOAD', 'AUTH_REQUEST'],
        outputs: ['SECURITY_VERDICT', 'VULNERABILITY_REPORT'],
        tools: {
          allowed: ['STATIC_SCANNER', 'PERM_CHECK'],
          denied: ['SECRET_EXFILTRATE']
        },
        mcpAccess: ['mcp-core-governance'],
        dataScope: ['AUDIT_LOGS', 'SECURITY_FINDINGS'],
        memoryScope: 'GLOBAL_PROJECT',
        executionModes: ['READ_ONLY', 'DRY_RUN', 'STAGING', 'PRODUCTION'],
        riskClass: 'R0',
        permissions: ['SECURITY_ENFORCE'],
        delegationLimits: { maxDepth: 1, maxChildren: 2, maxActiveChildren: 1 },
        approvalPolicy: { requiredForModes: [], requiredForRisk: [] },
        verificationPolicy: { requiresIndependentVerifier: false, verifierRoles: [] },
        budget: { timeBudgetMs: 30000, tokenBudget: 20000, costBudgetUsd: 0.2 },
        failurePolicy: { maxRetries: 1, escalateOnNonRetryable: true }
      }
    ];

    for (const spec of specialists) {
      this.registerAgent(spec);
    }
  }

  public static registerAgent(contract: AgentContract): void {
    const registered: RegisteredAgent = {
      contract,
      lifecycleStatus: 'READY',
      healthStatus: 'HEALTHY',
      lastHeartbeat: new Date().toISOString(),
      currentRunningTasks: [],
      concurrencyCount: 0,
      maxConcurrency: contract.delegationLimits.maxActiveChildren,
      totalTasksExecuted: 0,
      successRate: 1.0
    };
    this.agentRegistry.set(contract.identity.agentId, registered);
  }

  public static getAgent(agentId: string): RegisteredAgent | undefined {
    return this.agentRegistry.get(agentId);
  }

  public static listAgents(): RegisteredAgent[] {
    return Array.from(this.agentRegistry.values());
  }

  // ==========================================
  // Work Package 8.4 & 8.5: Capability Registry & Router
  // ==========================================

  public static routeTaskToAgent(
    requiredCapabilities: string[],
    executionMode: ExecutionMode,
    riskClass: RiskClass
  ): { selectedAgentId?: string; fallbackAgentIds: string[]; reason: string } {
    const candidates = Array.from(this.agentRegistry.values()).filter((agent) => {
      if (agent.lifecycleStatus !== 'READY' || agent.healthStatus !== 'HEALTHY') return false;
      if (!agent.contract.executionModes.includes(executionMode)) return false;
      return requiredCapabilities.every((cap) => agent.contract.capabilities.includes(cap));
    });

    if (candidates.length === 0) {
      return {
        fallbackAgentIds: [],
        reason: `No healthy agent satisfies capabilities [${requiredCapabilities.join(', ')}] in mode ${executionMode}`
      };
    }

    // Sort by lowest concurrency load and highest success rate
    candidates.sort((a, b) => a.concurrencyCount - b.concurrencyCount || b.successRate - a.successRate);

    const primary = candidates[0];
    const fallbacks = candidates.slice(1).map((c) => c.contract.identity.agentId);

    return {
      selectedAgentId: primary.contract.identity.agentId,
      fallbackAgentIds: fallbacks,
      reason: `Selected agent ${primary.contract.identity.agentName} (Concurrency: ${primary.concurrencyCount}/${primary.maxConcurrency})`
    };
  }

  // ==========================================
  // Work Package 8.7 & 8.8: Governed Delegation & Structured Communication
  // ==========================================

  public static delegateSubTask(
    parentTaskId: string,
    childTaskParams: {
      type: string;
      title: string;
      description: string;
      requiredCapabilities: string[];
      executionMode: ExecutionMode;
      riskClass: RiskClass;
      assignedAgentId: string;
      budget?: { maxModelCalls: number; maxResearchCalls: number; maxExecutionTimeMs: number; maxCostUsd: number };
    }
  ): { success: boolean; childTask?: OrchestrationTask; error?: string } {
    const parent = TaskOrchestrationEngine.getTask(parentTaskId);
    if (!parent) return { success: false, error: 'Parent task not found' };

    const parentAgent = this.agentRegistry.get(parent.owner);
    const childAgent = this.agentRegistry.get(childTaskParams.assignedAgentId);

    if (!childAgent) return { success: false, error: 'Target child agent not registered' };

    // Strict Rule: Child Execution Mode <= Parent Execution Authority
    const modeRank: Record<ExecutionMode, number> = { READ_ONLY: 0, DRY_RUN: 1, STAGING: 2, PRODUCTION: 3 };
    const parentMode: ExecutionMode = (parent.environment === 'production' ? 'PRODUCTION' : 'STAGING') as ExecutionMode;

    if (modeRank[childTaskParams.executionMode] > modeRank[parentMode]) {
      return {
        success: false,
        error: `Privilege escalation blocked: Child requested mode ${childTaskParams.executionMode} exceeding parent mode ${parentMode}`
      };
    }

    // Strict Rule: Max delegation depth
    const currentDepth = (parent.dependencyIds?.length || 0) + 1;
    if (currentDepth > (parentAgent?.contract.delegationLimits.maxDepth || 4)) {
      return { success: false, error: 'Maximum delegation depth exceeded' };
    }

    // Create child task under orchestrator
    const { task } = TaskOrchestrationEngine.createTask({
      type: childTaskParams.type,
      title: childTaskParams.title,
      description: childTaskParams.description,
      requester: parent.taskId,
      priority: parent.priority,
      severity: parent.severity,
      owner: childTaskParams.assignedAgentId,
      environment: childTaskParams.executionMode === 'PRODUCTION' ? 'production' : 'development',
      maximumRuntimeMs: childTaskParams.budget?.maxExecutionTimeMs || 30000,
      maxRetries: 2,
      approvalStatus: childTaskParams.riskClass === 'R3' || childTaskParams.riskClass === 'R4' ? 'PENDING' : 'NOT_REQUIRED',
      requiredCapabilities: childTaskParams.requiredCapabilities,
      parentTaskId: parent.taskId,
      correlationId: parent.correlationId,
      traceId: `trace-sub-${Date.now()}`
    });

    // Send Structured Message
    const msg: StructuredAgentMessage = {
      messageId: `msg-${Date.now()}`,
      schemaVersion: '1.0.0',
      correlationId: parent.correlationId,
      taskId: task.taskId,
      senderAgentId: parent.owner,
      receiverAgentId: childTaskParams.assignedAgentId,
      messageType: 'TASK_REQUEST',
      timestamp: new Date().toISOString(),
      payload: { instruction: childTaskParams.description, riskClass: childTaskParams.riskClass },
      executionMode: childTaskParams.executionMode
    };
    this.messageLogs.push(msg);

    return { success: true, childTask: task };
  }

  // ==========================================
  // Work Package 8.9: Conflict Resolution & Preservation of Dissent
  // ==========================================

  public static resolveSpecialistConflict(
    findings: Array<{ agentId: string; role: string; verdict: 'PASS' | 'WARN' | 'FAIL'; evidence: string; confidence: number }>
  ): {
    finalVerdict: 'ACCEPT' | 'ACCEPT_CONDITIONAL' | 'REQUEST_MORE_EVIDENCE' | 'BLOCK' | 'ESCALATE_HUMAN';
    dissentRecords: Array<{ agentId: string; finding: string }>;
    rationale: string;
  } {
    const fails = findings.filter((f) => f.verdict === 'FAIL');
    const warns = findings.filter((f) => f.verdict === 'WARN');
    const passes = findings.filter((f) => f.verdict === 'PASS');

    const dissentRecords = [...fails, ...warns].map((f) => ({
      agentId: f.agentId,
      finding: `[${f.role}] ${f.verdict}: ${f.evidence} (confidence: ${f.confidence})`
    }));

    if (fails.length > 0) {
      // Check if security or financial risk is present
      const securityFail = fails.some((f) => f.role.toLowerCase().includes('security'));
      if (securityFail) {
        return {
          finalVerdict: 'BLOCK',
          dissentRecords,
          rationale: 'Security policy conflict detected; failing closed.'
        };
      }
      return {
        finalVerdict: 'ESCALATE_HUMAN',
        dissentRecords,
        rationale: `Conflict among specialists (${fails.length} FAIL, ${passes.length} PASS). Human decision required.`
      };
    }

    if (warns.length > 0) {
      return {
        finalVerdict: 'ACCEPT_CONDITIONAL',
        dissentRecords,
        rationale: 'All critical checks passed; minor non-blocking warnings preserved in audit journal.'
      };
    }

    return {
      finalVerdict: 'ACCEPT',
      dissentRecords: [],
      rationale: 'Unanimous pass across all specialist agents with verifiable evidence.'
    };
  }

  // ==========================================
  // Work Package 8.11: Risk Matrix & Approval Gates
  // ==========================================

  public static requestApproval(params: {
    taskId: string;
    requestedBy: string;
    action: string;
    environment: 'development' | 'staging' | 'production';
    executionMode: ExecutionMode;
    riskClass: RiskClass;
    scope: string[];
    evidence: Record<string, unknown>;
  }): ApprovalObject {
    const approvalId = `appr-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;
    const approval: ApprovalObject = {
      approvalId,
      ...params,
      expiry: new Date(Date.now() + 86400000).toISOString(), // 24h
      status: 'PENDING'
    };
    this.approvals.set(approvalId, approval);
    return approval;
  }

  public static submitApprovalDecision(
    approvalId: string,
    decision: 'APPROVED' | 'REJECTED',
    decidedBy: string,
    note?: string
  ): { success: boolean; approval?: ApprovalObject; error?: string } {
    const approval = this.approvals.get(approvalId);
    if (!approval) return { success: false, error: 'Approval not found' };

    if (approval.status !== 'PENDING') {
      return { success: false, error: `Approval already resolved as ${approval.status}` };
    }

    approval.status = decision;
    approval.approvedBy = decidedBy;
    approval.approvedAt = new Date().toISOString();
    approval.decisionNote = note;

    const task = TaskOrchestrationEngine.getTask(approval.taskId);
    if (task) {
      task.approvalStatus = decision;
      if (decision === 'APPROVED') {
        TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'QUEUED');
      } else {
        TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'BLOCKED');
      }
    }

    return { success: true, approval };
  }

  // ==========================================
  // Work Package 8.21: Agent Quarantine & Health
  // ==========================================

  public static quarantineAgent(agentId: string, reason: string): boolean {
    const agent = this.agentRegistry.get(agentId);
    if (!agent) return false;

    agent.lifecycleStatus = 'QUARANTINED';
    agent.healthStatus = 'QUARANTINED';
    logger.warn(`Agent ${agentId} placed into QUARANTINE: ${reason}`);
    return true;
  }

  // ==========================================
  // Work Package 8.22: Decision Journal
  // ==========================================

  public static recordDecisionJournal(entry: Omit<DecisionJournalEntry, 'decisionId' | 'timestamp'>): DecisionJournalEntry {
    const journal: DecisionJournalEntry = {
      decisionId: `dec-${Date.now()}`,
      timestamp: new Date().toISOString(),
      ...entry
    };
    this.decisionJournal.push(journal);
    return journal;
  }

  public static getDecisionJournal(): DecisionJournalEntry[] {
    return this.decisionJournal;
  }

  public static getMessageLogs(): StructuredAgentMessage[] {
    return this.messageLogs;
  }
}
