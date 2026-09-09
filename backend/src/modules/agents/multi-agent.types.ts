/**
 * IBO Ecosystem — Multi-Agent Operating Architecture Types & Contracts
 * Master Prompt — Part 08
 */

import {
  OrchestrationPriority,
  OrchestrationSeverity,
  OrchestrationTaskStatus,
  ResourceBudget
} from '../orchestration/orchestration.types';

export type ExecutionMode = 'READ_ONLY' | 'DRY_RUN' | 'STAGING' | 'PRODUCTION';
export type RiskClass = 'R0' | 'R1' | 'R2' | 'R3' | 'R4';
export type AgentLifecycleStatus =
  | 'REGISTERED'
  | 'STARTING'
  | 'READY'
  | 'BUSY'
  | 'DEGRADED'
  | 'PAUSED'
  | 'DRAINING'
  | 'QUARANTINED'
  | 'OFFLINE'
  | 'RETIRED';

export type CollaborationMode =
  | 'SEQUENTIAL'
  | 'PARALLEL'
  | 'SPECIALIST_REVIEW'
  | 'DEBATE'
  | 'MULTI_REVIEW';

export type StructuredMessageType =
  | 'TASK_REQUEST'
  | 'TASK_ACCEPTED'
  | 'TASK_REJECTED'
  | 'TASK_PROGRESS'
  | 'EVIDENCE_REPORT'
  | 'QUESTION'
  | 'BLOCKED_REPORT'
  | 'REVIEW_REQUEST'
  | 'REVIEW_RESULT'
  | 'CONFLICT_REPORT'
  | 'ESCALATION_REQUEST'
  | 'HANDOFF'
  | 'RESULT'
  | 'VERIFICATION_REQUEST'
  | 'VERIFICATION_RESULT';

export interface StructuredAgentMessage {
  messageId: string;
  schemaVersion: '1.0.0';
  correlationId: string;
  taskId: string;
  senderAgentId: string;
  receiverAgentId: string;
  messageType: StructuredMessageType;
  timestamp: string;
  payload: Record<string, unknown>;
  evidenceRefs?: string[];
  executionMode: ExecutionMode;
}

export interface AgentContract {
  identity: {
    agentId: string;
    agentName: string;
    version: string;
    agentType: string;
    owner: string;
  };
  mission: string;
  scope: {
    allowed: string[];
    forbidden: string[];
  };
  capabilities: string[];
  inputs: string[];
  outputs: string[];
  tools: {
    allowed: string[];
    denied: string[];
  };
  mcpAccess: string[];
  dataScope: string[];
  memoryScope: 'GLOBAL_PROJECT' | 'TEAM' | 'AGENT' | 'TASK' | 'SESSION' | 'TEMPORARY';
  executionModes: ExecutionMode[];
  riskClass: RiskClass;
  permissions: string[];
  delegationLimits: {
    maxDepth: number;
    maxChildren: number;
    maxActiveChildren: number;
  };
  approvalPolicy: {
    requiredForModes: ExecutionMode[];
    requiredForRisk: RiskClass[];
  };
  verificationPolicy: {
    requiresIndependentVerifier: boolean;
    verifierRoles: string[];
  };
  budget: {
    timeBudgetMs: number;
    tokenBudget: number;
    costBudgetUsd: number;
  };
  failurePolicy: {
    maxRetries: number;
    escalateOnNonRetryable: boolean;
  };
}

export interface RegisteredAgent {
  contract: AgentContract;
  lifecycleStatus: AgentLifecycleStatus;
  healthStatus: 'HEALTHY' | 'DEGRADED' | 'UNRESPONSIVE' | 'OFFLINE' | 'QUARANTINED';
  lastHeartbeat: string;
  currentRunningTasks: string[];
  concurrencyCount: number;
  maxConcurrency: number;
  totalTasksExecuted: number;
  successRate: number;
  isShadowMode?: boolean;
}

export interface ApprovalObject {
  approvalId: string;
  taskId: string;
  requestedBy: string;
  action: string;
  environment: 'development' | 'staging' | 'production';
  executionMode: ExecutionMode;
  riskClass: RiskClass;
  scope: string[];
  evidence: Record<string, unknown>;
  expiry: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED' | 'REVOKED';
  approvedBy?: string;
  approvedAt?: string;
  decisionNote?: string;
}

export interface DecisionJournalEntry {
  decisionId: string;
  objective: string;
  contextSnapshot: Record<string, unknown>;
  specialistsConsulted: string[];
  evidence: Record<string, unknown>;
  conflictsDetected: string[];
  optionsConsidered: string[];
  decision: string;
  riskClass: RiskClass;
  approvalRef?: string;
  executionPlan: string[];
  verificationPlan: string[];
  resultStatus?: string;
  timestamp: string;
  postReviewNotes?: string;
}

export interface TaskGraphNode {
  nodeId: string;
  taskId?: string;
  agentRole: string;
  objective: string;
  executionMode: ExecutionMode;
  dependencies: string[];
  status: OrchestrationTaskStatus;
  approvalGate?: boolean;
  verificationGate?: boolean;
}

export interface TaskGraph {
  graphId: string;
  objective: string;
  nodes: Map<string, TaskGraphNode>;
  criticalPath: string[];
  riskClass: RiskClass;
  budget: ResourceBudget;
  deadline?: string;
  status: 'PLANNED' | 'RUNNING' | 'COMPLETED' | 'BLOCKED' | 'FAILED';
}
