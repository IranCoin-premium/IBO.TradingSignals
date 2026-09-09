/**
 * IBO Ecosystem — Task Orchestration Engine, Queue & Worker Supervisor
 * Master Prompt — Part 07: Implementation
 */

import {
  OrchestrationTask,
  OrchestrationTaskStatus,
  OrchestrationPriority,
  WorkerDefinition,
  ScheduleRecord,
  CircuitBreakerRecord,
  CircuitBreakerState,
  RetryClassification,
  ResourceLock,
  ExecutionAttempt
} from './orchestration.types';
import { logger } from '../../utils/logger';
import { ObservabilityService } from '../observability/observability.service';

export type CreateTaskInput = Omit<
  OrchestrationTask,
  'taskId' | 'status' | 'createdAt' | 'updatedAt' | 'retryCount' | 'attempts' | 'dependencyIds' | 'blockingTaskIds' | 'resourceLocks' | 'maximumRuntimeMs' | 'maxRetries' | 'approvalStatus' | 'requiredCapabilities' | 'correlationId' | 'traceId'
> & {
  taskId?: string;
  dependencyIds?: string[];
  blockingTaskIds?: string[];
  resourceLocks?: string[];
  maximumRuntimeMs?: number;
  maxRetries?: number;
  approvalStatus?: 'NOT_REQUIRED' | 'PENDING' | 'APPROVED' | 'REJECTED';
  requiredCapabilities?: string[];
  correlationId?: string;
  traceId?: string;
};

export class TaskOrchestrationEngine {
  private static tasks: Map<string, OrchestrationTask> = new Map();
  private static workers: Map<string, WorkerDefinition> = new Map();
  private static schedules: Map<string, ScheduleRecord> = new Map();
  private static circuitBreakers: Map<string, CircuitBreakerRecord> = new Map();
  private static resourceLocks: Map<string, ResourceLock> = new Map();
  private static idempotencyRegistry: Map<string, string> = new Map(); // key -> taskId
  private static deadLetterQueue: OrchestrationTask[] = [];
  private static eventStormRegistry: Map<string, { count: number; firstSeen: number }> = new Map();

  static {
    this.seedWorkersAndSchedules();
  }

  private static seedWorkersAndSchedules(): void {
    const defaultWorkers: WorkerDefinition[] = [
      {
        workerId: 'worker-opencode-primary',
        name: 'OpenCode Primary Coding Worker',
        type: 'DEVELOPMENT',
        version: '1.0.0',
        capabilities: ['CODE_EDIT', 'INSPECT_REPO', 'RUN_TESTS'],
        supportedTaskTypes: ['dev_fix', 'code_refactor', 'feature_implementation'],
        environment: 'development',
        maxConcurrency: 2,
        currentRunningCount: 0,
        status: 'ACTIVE',
        health: 'HEALTHY',
        lastHeartbeat: new Date().toISOString(),
        currentTasks: [],
        permissions: {
          allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
          deniedTools: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS'],
          mcpDependencies: ['mcp-core-governance']
        },
        fallbackGroup: 'coding-fallback-group'
      },
      {
        workerId: 'worker-cline-fallback',
        name: 'Cline Secondary/Reviewer Worker',
        type: 'DEVELOPMENT',
        version: '1.0.0',
        capabilities: ['CODE_EDIT', 'INSPECT_REPO', 'RUN_TESTS', 'CODE_REVIEW'],
        supportedTaskTypes: ['dev_fix', 'code_refactor', 'feature_implementation'],
        environment: 'development',
        maxConcurrency: 2,
        currentRunningCount: 0,
        status: 'ACTIVE',
        health: 'HEALTHY',
        lastHeartbeat: new Date().toISOString(),
        currentTasks: [],
        permissions: {
          allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
          deniedTools: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS'],
          mcpDependencies: ['mcp-core-governance']
        },
        fallbackGroup: 'coding-fallback-group'
      },
      {
        workerId: 'worker-qa-inspector',
        name: 'Quality Assurance & Multi-Viewport Worker',
        type: 'QA',
        version: '1.0.0',
        capabilities: ['VIEWPORT_CHECK', 'TEXT_QA', 'TEST_RUNNER'],
        supportedTaskTypes: ['visual_qa', 'text_qa', 'e2e_verification'],
        environment: 'staging',
        maxConcurrency: 4,
        currentRunningCount: 0,
        status: 'ACTIVE',
        health: 'HEALTHY',
        lastHeartbeat: new Date().toISOString(),
        currentTasks: [],
        permissions: {
          allowedTools: ['VIEWPORT_RENDER', 'TEST_RUNNER', 'DIFF_INSPECT'],
          deniedTools: ['FILE_WRITE', 'LIVE_TRADING_EXECUTE'],
          mcpDependencies: ['mcp-core-governance']
        }
      },
      {
        workerId: 'worker-research-intelligence',
        name: 'Market & Payments Research Worker',
        type: 'RESEARCH',
        version: '1.0.0',
        capabilities: ['HTTPS_DOC_SCRAPE', 'REGULATORY_SEARCH'],
        supportedTaskTypes: ['payment_research', 'market_intelligence', 'geo_seo_audit'],
        environment: 'development',
        maxConcurrency: 2,
        currentRunningCount: 0,
        status: 'ACTIVE',
        health: 'HEALTHY',
        lastHeartbeat: new Date().toISOString(),
        currentTasks: [],
        permissions: {
          allowedTools: ['HTTPS_FETCH', 'EVIDENCE_EXTRACT'],
          deniedTools: ['BANK_API_EXECUTE', 'KYC_SUBMIT'],
          mcpDependencies: ['mcp-core-governance']
        }
      }
    ];

    for (const w of defaultWorkers) {
      this.workers.set(w.workerId, w);
    }

    const defaultSchedules: ScheduleRecord[] = [
      {
        scheduleId: 'sched-daily-security-audit',
        taskType: 'security_audit',
        title: 'Daily Automated Security & Secret Leak Audit',
        frequency: 'DAILY',
        timezone: 'UTC',
        enabled: true,
        nextRun: new Date(Date.now() + 86400000).toISOString(),
        misfirePolicy: 'RUN_ONCE_AFTER_RECOVERY',
        owner: 'DevOpsSupervisor',
        status: 'ACTIVE'
      },
      {
        scheduleId: 'sched-hourly-knowledge-freshness',
        taskType: 'knowledge_freshness_audit',
        title: 'Hourly Knowledge Base & Freshness Scan',
        frequency: 'HOURLY',
        timezone: 'UTC',
        enabled: true,
        nextRun: new Date(Date.now() + 3600000).toISOString(),
        misfirePolicy: 'SKIP',
        owner: 'ChiefAgentCoordinator',
        status: 'ACTIVE'
      }
    ];

    for (const s of defaultSchedules) {
      this.schedules.set(s.scheduleId, s);
    }
  }

  // ==========================================
  // Work Package 7.2 & 7.3: Task State Machine & Transitions
  // ==========================================

  public static isValidTransition(from: OrchestrationTaskStatus, to: OrchestrationTaskStatus): boolean {
    const validMap: Record<OrchestrationTaskStatus, OrchestrationTaskStatus[]> = {
      NEW: ['QUEUED', 'BLOCKED', 'CANCELLED'],
      QUEUED: ['CLAIMED', 'BLOCKED', 'CANCEL_REQUESTED', 'EXPIRED'],
      CLAIMED: ['RUNNING', 'QUEUED', 'BLOCKED'],
      RUNNING: ['VERIFYING', 'RETRY', 'BLOCKED', 'FAILED', 'CANCEL_REQUESTED', 'DEAD_LETTER'],
      WAITING: ['QUEUED', 'RUNNING', 'CANCEL_REQUESTED'],
      VERIFYING: ['SUCCESS', 'PARTIAL_SUCCESS', 'RETRY', 'FAILED'],
      RETRY: ['QUEUED', 'FAILED', 'ESCALATED'],
      SUCCESS: [],
      PARTIAL_SUCCESS: ['ESCALATED', 'SUCCESS'],
      BLOCKED: ['QUEUED', 'ESCALATED', 'CANCELLED'],
      FAILED: ['RETRY', 'ESCALATED', 'DEAD_LETTER'],
      ESCALATED: ['QUEUED', 'CANCELLED'],
      CANCEL_REQUESTED: ['CANCELLED', 'RUNNING'],
      CANCELLED: [],
      EXPIRED: ['QUEUED'],
      DEAD_LETTER: ['QUEUED']
    };

    return validMap[from]?.includes(to) ?? false;
  }

  public static createTask(
    params: CreateTaskInput
  ): { task: OrchestrationTask; deduplicated: boolean } {
    // Check Event Storm
    if (params.parentEventId) {
      const now = Date.now();
      const stormKey = `${params.type}:${params.parentEventId}`;
      const entry = this.eventStormRegistry.get(stormKey) || { count: 0, firstSeen: now };
      entry.count += 1;
      this.eventStormRegistry.set(stormKey, entry);
      if (entry.count > 50 && now - entry.firstSeen < 60000) {
        logger.warn(`Event storm detected for ${stormKey}, throttling event.`);
      }
    }

    // Check Idempotency Key
    if (params.idempotencyKey && this.idempotencyRegistry.has(params.idempotencyKey)) {
      const existingId = this.idempotencyRegistry.get(params.idempotencyKey)!;
      const existingTask = this.tasks.get(existingId);
      if (existingTask) {
        return { task: existingTask, deduplicated: true };
      }
    }

    // Check deduplication key
    if (params.deduplicationKey) {
      for (const t of this.tasks.values()) {
        if (
          t.deduplicationKey === params.deduplicationKey &&
          (t.status === 'NEW' || t.status === 'QUEUED' || t.status === 'RUNNING')
        ) {
          return { task: t, deduplicated: true };
        }
      }
    }

    const taskId = params.taskId || `task-orch-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`;
    const now = new Date().toISOString();

    const task: OrchestrationTask = {
      ...params,
      taskId,
      status: 'QUEUED',
      createdAt: now,
      updatedAt: now,
      retryCount: 0,
      attempts: [],
      dependencyIds: params.dependencyIds || [],
      blockingTaskIds: params.blockingTaskIds || [],
      resourceLocks: params.resourceLocks || [],
      maximumRuntimeMs: params.maximumRuntimeMs || 60000,
      maxRetries: params.maxRetries ?? 3,
      approvalStatus: params.approvalStatus || 'NOT_REQUIRED',
      requiredCapabilities: params.requiredCapabilities || [],
      correlationId: params.correlationId || `corr-${Date.now()}`,
      traceId: params.traceId || `trace-${Date.now()}`
    };

    // Check for direct resource conflicts
    if (task.resourceLocks.length > 0) {
      for (const res of task.resourceLocks) {
        if (this.resourceLocks.has(res)) {
          const activeLock = this.resourceLocks.get(res)!;
          if (activeLock.taskId !== task.taskId) {
            task.status = 'WAITING';
            task.blockingTaskIds.push(activeLock.taskId);
          }
        }
      }
    }

    // Check dependency satisfaction
    if (task.dependencyIds.length > 0) {
      const unsatisfied = task.dependencyIds.some((depId) => {
        const depTask = this.tasks.get(depId);
        return !depTask || depTask.status !== 'SUCCESS';
      });
      if (unsatisfied) {
        task.status = 'WAITING';
      }
    }

    this.tasks.set(taskId, task);
    if (params.idempotencyKey) {
      this.idempotencyRegistry.set(params.idempotencyKey, taskId);
    }

    return { task, deduplicated: false };
  }

  public static getTask(taskId: string): OrchestrationTask | undefined {
    return this.tasks.get(taskId);
  }

  public static listTasks(filter?: { status?: OrchestrationTaskStatus; priority?: OrchestrationPriority }): OrchestrationTask[] {
    let list = Array.from(this.tasks.values());
    if (filter?.status) {
      list = list.filter((t) => t.status === filter.status);
    }
    if (filter?.priority) {
      list = list.filter((t) => t.priority === filter.priority);
    }
    return list;
  }

  public static updateTaskStatus(
    taskId: string,
    newStatus: OrchestrationTaskStatus,
    reason?: string
  ): { success: boolean; task?: OrchestrationTask; error?: string } {
    const task = this.tasks.get(taskId);
    if (!task) return { success: false, error: 'Task not found' };

    if (!this.isValidTransition(task.status, newStatus)) {
      return {
        success: false,
        error: `Invalid transition from ${task.status} to ${newStatus}`
      };
    }

    task.status = newStatus;
    task.updatedAt = new Date().toISOString();
    if (newStatus === 'SUCCESS' || newStatus === 'CANCELLED' || newStatus === 'FAILED' || newStatus === 'DEAD_LETTER') {
      task.completedAt = new Date().toISOString();
      this.releaseResourceLocks(task.taskId);
    }

    return { success: true, task };
  }

  // ==========================================
  // Work Package 7.6, 7.7 & 7.11: Worker Claiming, Concurrency & Fallbacks
  // ==========================================

  public static registerWorker(worker: WorkerDefinition): void {
    this.workers.set(worker.workerId, worker);
  }

  public static getWorker(workerId: string): WorkerDefinition | undefined {
    return this.workers.get(workerId);
  }

  public static listWorkers(): WorkerDefinition[] {
    return Array.from(this.workers.values());
  }

  public static claimTask(
    taskId: string,
    workerId: string
  ): { success: boolean; task?: OrchestrationTask; error?: string } {
    const task = this.tasks.get(taskId);
    if (!task) return { success: false, error: 'Task not found' };

    if (task.status !== 'QUEUED') {
      return { success: false, error: `Task is in state ${task.status}, not QUEUED` };
    }

    const worker = this.workers.get(workerId);
    if (!worker) return { success: false, error: 'Worker not found' };

    if (worker.status === 'DRAINING' || worker.status === 'INACTIVE' || worker.health !== 'HEALTHY') {
      return { success: false, error: `Worker ${workerId} is ${worker.health} / ${worker.status} and cannot claim tasks` };
    }

    if (worker.isQuarantined) {
      return { success: false, error: `Worker ${workerId} is quarantined due to abnormal behavior` };
    }

    if (worker.currentRunningCount >= worker.maxConcurrency) {
      return { success: false, error: `Worker concurrency limit reached (${worker.maxConcurrency})` };
    }

    // Check capability match
    const hasCapabilities = task.requiredCapabilities.every((cap) => worker.capabilities.includes(cap));
    if (!hasCapabilities) {
      return { success: false, error: 'Worker lacks required capabilities' };
    }

    // Atomic claim
    task.status = 'CLAIMED';
    task.assignedWorker = workerId;
    task.updatedAt = new Date().toISOString();

    worker.currentRunningCount += 1;
    worker.currentTasks.push(taskId);

    return { success: true, task };
  }

  public static startExecution(taskId: string): { attempt: ExecutionAttempt; task: OrchestrationTask } {
    const task = this.tasks.get(taskId)!;
    task.status = 'RUNNING';
    task.startedAt = task.startedAt || new Date().toISOString();
    task.updatedAt = new Date().toISOString();

    const attemptId = `att-${task.taskId}-${task.attempts.length + 1}`;
    task.currentAttemptId = attemptId;

    const attempt: ExecutionAttempt = {
      attemptId,
      attemptNumber: task.attempts.length + 1,
      workerId: task.assignedWorker || 'unknown',
      startedAt: new Date().toISOString(),
      status: 'RUNNING'
    };

    task.attempts.push(attempt);
    return { attempt, task };
  }

  public static handleExecutionFailure(
    taskId: string,
    error: string,
    classification: RetryClassification
  ): { nextAction: 'RETRY' | 'FALLBACK' | 'ESCALATE' | 'DEAD_LETTER'; task: OrchestrationTask } {
    const task = this.tasks.get(taskId)!;
    const currentAttempt = task.attempts.find((a) => a.attemptId === task.currentAttemptId);
    if (currentAttempt) {
      currentAttempt.status = 'FAILED';
      currentAttempt.error = error;
      currentAttempt.endedAt = new Date().toISOString();
    }

    task.retryCount += 1;

    // Non-retryable: AUTHORIZATION, INPUT, LOGIC
    if (classification === 'AUTHORIZATION' || classification === 'INPUT' || classification === 'LOGIC') {
      task.status = 'ESCALATED';
      task.error = error;
      task.escalation = {
        reason: `Non-retryable failure (${classification}): ${error}`,
        escalatedAt: new Date().toISOString()
      };
      this.releaseResourceLocks(task.taskId);
      return { nextAction: 'ESCALATE', task };
    }

    // Poison task check
    if (task.retryCount >= task.maxRetries) {
      task.status = 'DEAD_LETTER';
      task.error = `Retries exhausted (${task.maxRetries}): ${error}`;
      this.deadLetterQueue.push(task);
      this.releaseResourceLocks(task.taskId);
      return { nextAction: 'DEAD_LETTER', task };
    }

    // Check Fallback worker compatibility
    const currentWorker = this.workers.get(task.assignedWorker || '');
    if (currentWorker?.fallbackGroup) {
      const fallbackWorker = Array.from(this.workers.values()).find(
        (w) =>
          w.fallbackGroup === currentWorker.fallbackGroup &&
          w.workerId !== currentWorker.workerId &&
          w.health === 'HEALTHY' &&
          w.status === 'ACTIVE'
      );

      if (fallbackWorker) {
        task.assignedWorker = fallbackWorker.workerId;
        task.status = 'RETRY';
        return { nextAction: 'FALLBACK', task };
      }
    }

    task.status = 'RETRY';
    return { nextAction: 'RETRY', task };
  }

  // ==========================================
  // Work Package 7.12: Circuit Breaker
  // ==========================================

  public static getCircuitBreaker(serviceId: string): CircuitBreakerRecord {
    if (!this.circuitBreakers.has(serviceId)) {
      this.circuitBreakers.set(serviceId, {
        serviceId,
        state: 'CLOSED',
        failureCount: 0,
        threshold: 3,
        cooldownMs: 30000
      });
    }
    return this.circuitBreakers.get(serviceId)!;
  }

  public static recordCircuitSuccess(serviceId: string): void {
    const cb = this.getCircuitBreaker(serviceId);
    cb.failureCount = 0;
    cb.state = 'CLOSED';
  }

  public static recordCircuitFailure(serviceId: string): CircuitBreakerState {
    const cb = this.getCircuitBreaker(serviceId);
    cb.failureCount += 1;
    cb.lastFailureAt = new Date().toISOString();

    if (cb.failureCount >= cb.threshold) {
      cb.state = 'OPEN';
      cb.cooldownUntil = new Date(Date.now() + cb.cooldownMs).toISOString();
    }
    return cb.state;
  }

  // ==========================================
  // Work Package 7.14 & 7.25: Restart & Orphan Recovery
  // ==========================================

  public static recoverOrphanTasks(): { recoveredCount: number; escalatedCount: number } {
    let recoveredCount = 0;
    let escalatedCount = 0;

    for (const task of this.tasks.values()) {
      if (task.status === 'RUNNING' || task.status === 'CLAIMED') {
        const worker = this.workers.get(task.assignedWorker || '');
        if (!worker || worker.health === 'OFFLINE' || worker.health === 'UNRESPONSIVE') {
          // Worker crashed or disappeared
          if (task.retryCount < task.maxRetries) {
            task.status = 'QUEUED';
            task.assignedWorker = undefined;
            recoveredCount++;
          } else {
            task.status = 'ESCALATED';
            task.escalation = {
              reason: 'Worker crashed with no result and retries exhausted',
              escalatedAt: new Date().toISOString()
            };
            escalatedCount++;
          }
        }
      }
    }

    return { recoveredCount, escalatedCount };
  }

  // ==========================================
  // Work Package 7.16 & 7.17: Scheduling & Misfire Policy
  // ==========================================

  public static listSchedules(): ScheduleRecord[] {
    return Array.from(this.schedules.values());
  }

  public static triggerScheduledJobs(): { executed: string[]; skipped: string[] } {
    const executed: string[] = [];
    const skipped: string[] = [];
    const now = new Date().toISOString();

    for (const sched of this.schedules.values()) {
      if (!sched.enabled || sched.status !== 'ACTIVE') continue;

      if (sched.nextRun <= now) {
        // Evaluate misfire
        const overdueMs = Date.now() - new Date(sched.nextRun).getTime();
        if (overdueMs > 3600000 && sched.misfirePolicy === 'SKIP') {
          skipped.push(sched.scheduleId);
          sched.nextRun = new Date(Date.now() + 3600000).toISOString();
          continue;
        }

        // Spawn task
        const { task } = this.createTask({
          type: sched.taskType,
          title: sched.title,
          description: `Scheduled trigger from ${sched.scheduleId}`,
          requester: 'Scheduler',
          priority: 'P2',
          severity: 'LOW',
          owner: sched.owner,
          environment: 'production',
          maximumRuntimeMs: 30000,
          maxRetries: 2,
          approvalStatus: 'NOT_REQUIRED',
          requiredCapabilities: ['TEST_RUNNER'],
          correlationId: `sched-corr-${sched.scheduleId}`,
          traceId: `sched-trace-${Date.now()}`,
          deduplicationKey: `sched:${sched.scheduleId}:${sched.nextRun}`
        });

        sched.lastRun = now;
        sched.nextRun = new Date(Date.now() + 86400000).toISOString();
        executed.push(task.taskId);
      }
    }

    return { executed, skipped };
  }

  // ==========================================
  // Work Package 7.19 & 7.20: Resource Locking & Deadlock/Poison Defense
  // ==========================================

  public static acquireResourceLock(resourceId: string, taskId: string, ttlMs: number = 30000): boolean {
    const existing = this.resourceLocks.get(resourceId);
    const now = Date.now();

    if (existing && new Date(existing.expiresAt).getTime() > now && existing.taskId !== taskId) {
      return false; // locked by another task
    }

    this.resourceLocks.set(resourceId, {
      resourceId,
      taskId,
      lockedAt: new Date().toISOString(),
      expiresAt: new Date(now + ttlMs).toISOString()
    });

    const task = this.tasks.get(taskId);
    if (task && !task.resourceLocks.includes(resourceId)) {
      task.resourceLocks.push(resourceId);
    }

    return true;
  }

  public static releaseResourceLocks(taskId: string): void {
    for (const [resId, lock] of this.resourceLocks.entries()) {
      if (lock.taskId === taskId) {
        this.resourceLocks.delete(resId);
      }
    }
  }

  public static detectCycles(tasks: OrchestrationTask[]): boolean {
    const graph: Map<string, string[]> = new Map();
    for (const t of tasks) {
      graph.set(t.taskId, t.dependencyIds || []);
    }

    const visited: Set<string> = new Set();
    const recStack: Set<string> = new Set();

    function hasCycle(node: string): boolean {
      visited.add(node);
      recStack.add(node);

      for (const neighbor of graph.get(node) || []) {
        if (!visited.has(neighbor) && hasCycle(neighbor)) {
          return true;
        } else if (recStack.has(neighbor)) {
          return true;
        }
      }

      recStack.delete(node);
      return false;
    }

    for (const node of graph.keys()) {
      if (!visited.has(node)) {
        if (hasCycle(node)) return true;
      }
    }
    return false;
  }

  public static getObservabilityDashboard(): {
    queueDepth: number;
    runningCount: number;
    deadLetterCount: number;
    activeWorkersCount: number;
    priorityDistribution: Record<OrchestrationPriority, number>;
    statusDistribution: Record<string, number>;
  } {
    const priorityDist: Record<OrchestrationPriority, number> = { P0: 0, P1: 0, P2: 0, P3: 0, P4: 0 };
    const statusDist: Record<string, number> = {};

    let queueDepth = 0;
    let runningCount = 0;

    for (const t of this.tasks.values()) {
      priorityDist[t.priority] = (priorityDist[t.priority] || 0) + 1;
      statusDist[t.status] = (statusDist[t.status] || 0) + 1;

      if (t.status === 'QUEUED') queueDepth++;
      if (t.status === 'RUNNING') runningCount++;
    }

    return {
      queueDepth,
      runningCount,
      deadLetterCount: this.deadLetterQueue.length,
      activeWorkersCount: Array.from(this.workers.values()).filter((w) => w.status === 'ACTIVE').length,
      priorityDistribution: priorityDist,
      statusDistribution: statusDist
    };
  }
}
