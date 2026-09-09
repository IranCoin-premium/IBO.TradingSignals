/**
 * IBO Ecosystem — 24/7 Task Orchestration, Queue, Workers, Scheduling & Governance
 * Master Prompt — Part 07: Work Packages 7.1 - 7.28
 *
 * Invariants:
 * 1. Always-on infrastructure + Event-driven & Scheduled tasks + On-demand AI Agents.
 * 2. Strict separation: Orchestrator != Queue != Worker != Scheduler != Verifier != Policy.
 * 3. Durable state: SQLite / In-memory durable persistence with restart & orphan recovery.
 * 4. Priority: P0 (Critical/Safety), P1 (Urgent), P2 (High), P3 (Normal), P4 (Low). (Priority != Permission).
 * 5. Idempotency & storm protection: deduplication, jitter, backoff, circuit breaker.
 * 6. Hard safety & Human boundaries: No live trading mods, no secrets leaking, KYC/Banking = Persian Action Package.
 */

export type OrchestrationPriority = 'P0' | 'P1' | 'P2' | 'P3' | 'P4';
export type OrchestrationSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type OrchestrationTaskStatus =
  | 'NEW'
  | 'QUEUED'
  | 'CLAIMED'
  | 'RUNNING'
  | 'WAITING'
  | 'VERIFYING'
  | 'RETRY'
  | 'SUCCESS'
  | 'PARTIAL_SUCCESS'
  | 'BLOCKED'
  | 'FAILED'
  | 'ESCALATED'
  | 'CANCEL_REQUESTED'
  | 'CANCELLED'
  | 'EXPIRED'
  | 'DEAD_LETTER';

export type WorkerHealthStatus =
  | 'HEALTHY'
  | 'DEGRADED'
  | 'UNRESPONSIVE'
  | 'OFFLINE'
  | 'DRAINING'
  | 'MAINTENANCE'
  | 'QUARANTINED';

export type RetryClassification =
  | 'TRANSIENT'
  | 'CONFIGURATION'
  | 'AUTHORIZATION'
  | 'INPUT'
  | 'LOGIC'
  | 'BUG'
  | 'RESOURCE'
  | 'UNKNOWN';

export type CircuitBreakerState = 'CLOSED' | 'OPEN' | 'HALF_OPEN';

export interface ResourceBudget {
  maxModelCalls: number;
  maxResearchCalls: number;
  maxExecutionTimeMs: number;
  maxCostUsd: number;
}

export interface ResourceLock {
  resourceId: string; // e.g., 'repo:branch:main', 'file:auth.ts', 'db:schema'
  taskId: string;
  lockedAt: string;
  expiresAt: string;
}

export interface ExecutionAttempt {
  attemptId: string;
  attemptNumber: number;
  workerId: string;
  startedAt: string;
  endedAt?: string;
  status: OrchestrationTaskStatus;
  error?: string;
  output?: Record<string, unknown>;
  verification?: {
    verifiedBy: string;
    passed: boolean;
    reason?: string;
  };
}

export interface OrchestrationTask {
  taskId: string;
  parentEventId?: string;
  parentTaskId?: string;
  initiativeId?: string;
  type: string;
  title: string;
  description: string;
  requester: string;
  priority: OrchestrationPriority;
  severity: OrchestrationSeverity;
  status: OrchestrationTaskStatus;
  owner: string;
  assignedAgent?: string;
  assignedWorker?: string;
  environment: 'development' | 'staging' | 'production';
  createdAt: string;
  scheduledAt?: string;
  startedAt?: string;
  updatedAt: string;
  completedAt?: string;
  deadline?: string;
  maximumRuntimeMs: number;
  retryCount: number;
  maxRetries: number;
  currentAttemptId?: string;
  attempts: ExecutionAttempt[];
  dependencyIds: string[];
  blockingTaskIds: string[];
  resourceLocks: string[];
  approvalStatus: 'NOT_REQUIRED' | 'PENDING' | 'APPROVED' | 'REJECTED';
  requiredCapabilities: string[];
  result?: Record<string, unknown>;
  verification?: Record<string, unknown>;
  error?: string;
  escalation?: {
    reason: string;
    escalatedAt: string;
    escalationPackage?: Record<string, unknown>;
  };
  correlationId: string;
  traceId: string;
  idempotencyKey?: string;
  deduplicationKey?: string;
  budget?: ResourceBudget;
  usedBudget?: {
    modelCalls: number;
    researchCalls: number;
    executionTimeMs: number;
    costUsd: number;
  };
  isPoison?: boolean;
}

export interface WorkerDefinition {
  workerId: string;
  name: string;
  type: string;
  version: string;
  capabilities: string[];
  supportedTaskTypes: string[];
  environment: 'development' | 'staging' | 'production';
  maxConcurrency: number;
  currentRunningCount: number;
  status: 'ACTIVE' | 'DRAINING' | 'INACTIVE';
  health: WorkerHealthStatus;
  lastHeartbeat: string;
  currentTasks: string[];
  permissions: {
    allowedTools: string[];
    deniedTools: string[];
    mcpDependencies: string[];
  };
  fallbackGroup?: string;
  isQuarantined?: boolean;
}

export interface ScheduleRecord {
  scheduleId: string;
  taskType: string;
  title: string;
  frequency: string; // e.g. '0 0 * * *' or 'EVERY_HOUR'
  timezone: string;
  enabled: boolean;
  lastRun?: string;
  nextRun: string;
  misfirePolicy: 'SKIP' | 'RUN_ONCE_AFTER_RECOVERY' | 'CATCH_UP';
  owner: string;
  status: 'ACTIVE' | 'PAUSED';
}

export interface CircuitBreakerRecord {
  serviceId: string;
  state: CircuitBreakerState;
  failureCount: number;
  lastFailureAt?: string;
  cooldownUntil?: string;
  threshold: number;
  cooldownMs: number;
}
