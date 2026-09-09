/**
 * IBO Ecosystem — 24/7 Task Orchestration, Workers, Queue, Schedulers & Recovery Test Suite
 * Master Prompt — Part 07: Work Packages 7.1 - 7.28
 */

import { TaskOrchestrationEngine } from '../modules/orchestration/orchestration.service';
import { OrchestrationTask, WorkerDefinition } from '../modules/orchestration/orchestration.types';

describe('Part 07 — Task Orchestration, Queue, Workers, Schedulers & 24/7 Autonomy', () => {

  describe('Work Package 7.2 & 7.3: Task Entity, State Machine & Invariants', () => {
    it('should create a task with default QUEUED state and track attempts', () => {
      const { task, deduplicated } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Fix mobile header alignment',
        description: 'Adjust CSS/Compose layout for 360px viewport',
        requester: 'ChiefAgentCoordinator',
        priority: 'P1',
        severity: 'MEDIUM',
        owner: 'UIUXAgent',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 3,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT', 'RUN_TESTS'],
        correlationId: 'corr-test-01',
        traceId: 'trace-test-01'
      });

      expect(deduplicated).toBe(false);
      expect(task.status).toBe('QUEUED');
      expect(task.priority).toBe('P1');
      expect(task.attempts.length).toBe(0);
    });

    it('should reject invalid state transitions (e.g., QUEUED directly to SUCCESS)', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Direct transition test',
        description: 'Testing illegal leap',
        requester: 'Admin',
        priority: 'P3',
        severity: 'LOW',
        owner: 'DevOps',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 2,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['RUN_TESTS'],
        correlationId: 'corr-test-02',
        traceId: 'trace-test-02'
      });

      const result = TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'SUCCESS');
      expect(result.success).toBe(false);
      expect(result.error).toContain('Invalid transition from QUEUED to SUCCESS');
    });

    it('should permit valid lifecycle transitions (QUEUED -> CLAIMED -> RUNNING -> VERIFYING -> SUCCESS)', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Full lifecycle test',
        description: 'Testing valid workflow',
        requester: 'Admin',
        priority: 'P2',
        severity: 'LOW',
        owner: 'DevOps',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 2,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT'],
        correlationId: 'corr-test-03',
        traceId: 'trace-test-03'
      });

      expect(TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'CLAIMED').success).toBe(true);
      expect(TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'RUNNING').success).toBe(true);
      expect(TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'VERIFYING').success).toBe(true);
      expect(TaskOrchestrationEngine.updateTaskStatus(task.taskId, 'SUCCESS').success).toBe(true);
    });
  });

  describe('Work Package 7.4 & 7.19: Idempotency, Deduplication & Storm Defense', () => {
    it('should deduplicate tasks with the same idempotency key', () => {
      const idempotencyKey = 'idem-unique-key-999';

      const first = TaskOrchestrationEngine.createTask({
        type: 'build_validation',
        title: 'First build trigger',
        description: 'Run automated build',
        requester: 'CI/CD',
        priority: 'P1',
        severity: 'HIGH',
        owner: 'DevOps',
        environment: 'staging',
        maximumRuntimeMs: 60000,
        maxRetries: 1,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['RUN_TESTS'],
        correlationId: 'corr-idem-1',
        traceId: 'trace-idem-1',
        idempotencyKey
      });

      const second = TaskOrchestrationEngine.createTask({
        type: 'build_validation',
        title: 'Duplicate build trigger',
        description: 'Run automated build replay',
        requester: 'CI/CD',
        priority: 'P1',
        severity: 'HIGH',
        owner: 'DevOps',
        environment: 'staging',
        maximumRuntimeMs: 60000,
        maxRetries: 1,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['RUN_TESTS'],
        correlationId: 'corr-idem-2',
        traceId: 'trace-idem-2',
        idempotencyKey
      });

      expect(first.deduplicated).toBe(false);
      expect(second.deduplicated).toBe(true);
      expect(second.task.taskId).toBe(first.task.taskId);
    });
  });

  describe('Work Package 7.6, 7.7 & 7.11: Worker Claiming, Concurrency, Locking & Fallbacks', () => {
    it('should allow healthy worker with matching capabilities to claim queued task', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Fix button style',
        description: 'Fix padding',
        requester: 'CAC',
        priority: 'P2',
        severity: 'LOW',
        owner: 'UIUXAgent',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 3,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT', 'RUN_TESTS'],
        correlationId: 'corr-claim-1',
        traceId: 'trace-claim-1'
      });

      const claimResult = TaskOrchestrationEngine.claimTask(task.taskId, 'worker-opencode-primary');
      expect(claimResult.success).toBe(true);
      expect(claimResult.task?.status).toBe('CLAIMED');
      expect(claimResult.task?.assignedWorker).toBe('worker-opencode-primary');

      const { attempt } = TaskOrchestrationEngine.startExecution(task.taskId);
      expect(attempt.attemptNumber).toBe(1);
      expect(attempt.workerId).toBe('worker-opencode-primary');
      expect(attempt.status).toBe('RUNNING');
    });

    it('should switch to compatible fallback worker upon transient failure', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Refactor auth controller',
        description: 'Clean up imports',
        requester: 'CAC',
        priority: 'P2',
        severity: 'LOW',
        owner: 'CodingAgent',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 3,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT', 'RUN_TESTS'],
        correlationId: 'corr-fallback-1',
        traceId: 'trace-fallback-1'
      });

      TaskOrchestrationEngine.claimTask(task.taskId, 'worker-opencode-primary');
      TaskOrchestrationEngine.startExecution(task.taskId);

      // Simulate primary worker failure
      const failResult = TaskOrchestrationEngine.handleExecutionFailure(
        task.taskId,
        'Process killed / memory limit exceeded',
        'TRANSIENT'
      );

      expect(failResult.nextAction).toBe('FALLBACK');
      expect(failResult.task.assignedWorker).toBe('worker-cline-fallback');
      expect(failResult.task.status).toBe('RETRY');
    });

    it('should escalate immediately on non-retryable authorization failures without infinite loop', () => {
      const { task } = TaskOrchestrationEngine.createTask({
        type: 'live_trading_operation',
        title: 'Modify binary options broker connection',
        description: 'Unauthorized trading attempt',
        requester: 'AgentX',
        priority: 'P0',
        severity: 'CRITICAL',
        owner: 'ChiefAgentCoordinator',
        environment: 'production',
        maximumRuntimeMs: 30000,
        maxRetries: 3,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT'],
        correlationId: 'corr-auth-fail-1',
        traceId: 'trace-auth-fail-1'
      });

      TaskOrchestrationEngine.claimTask(task.taskId, 'worker-opencode-primary');
      TaskOrchestrationEngine.startExecution(task.taskId);

      const failResult = TaskOrchestrationEngine.handleExecutionFailure(
        task.taskId,
        'Access denied: live trading operations are strictly forbidden',
        'AUTHORIZATION'
      );

      expect(failResult.nextAction).toBe('ESCALATE');
      expect(failResult.task.status).toBe('ESCALATED');
      expect(failResult.task.escalation?.reason).toContain('Non-retryable failure (AUTHORIZATION)');
    });
  });

  describe('Work Package 7.8 & 7.19: Resource Locking & Cycle Detection', () => {
    it('should lock shared resource and reject concurrent write lock attempts', () => {
      const resource = 'file:backend/src/routes/auth.ts';
      const lock1 = TaskOrchestrationEngine.acquireResourceLock(resource, 'task-agent-1');
      const lock2 = TaskOrchestrationEngine.acquireResourceLock(resource, 'task-agent-2');

      expect(lock1).toBe(true);
      expect(lock2).toBe(false);

      TaskOrchestrationEngine.releaseResourceLocks('task-agent-1');
      const lock3 = TaskOrchestrationEngine.acquireResourceLock(resource, 'task-agent-2');
      expect(lock3).toBe(true);
    });

    it('should detect circular task dependencies and report cycle', () => {
      const taskA: OrchestrationTask = {
        taskId: 'task-cycle-A',
        type: 'build',
        title: 'Task A',
        description: '',
        requester: 'CAC',
        priority: 'P2',
        severity: 'LOW',
        status: 'QUEUED',
        owner: 'DevOps',
        environment: 'development',
        createdAt: '',
        updatedAt: '',
        maximumRuntimeMs: 1000,
        retryCount: 0,
        maxRetries: 1,
        attempts: [],
        dependencyIds: ['task-cycle-B'],
        blockingTaskIds: [],
        resourceLocks: [],
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: [],
        correlationId: 'c1',
        traceId: 't1'
      };

      const taskB: OrchestrationTask = {
        ...taskA,
        taskId: 'task-cycle-B',
        dependencyIds: ['task-cycle-C']
      };

      const taskC: OrchestrationTask = {
        ...taskA,
        taskId: 'task-cycle-C',
        dependencyIds: ['task-cycle-A'] // creates A -> B -> C -> A
      };

      const hasCycle = TaskOrchestrationEngine.detectCycles([taskA, taskB, taskC]);
      expect(hasCycle).toBe(true);
    });
  });

  describe('Work Package 7.12: Circuit Breaker for External Integrations', () => {
    it('should trip circuit breaker to OPEN after threshold consecutive failures', () => {
      const serviceId = 'mcp-external-payment-provider';
      expect(TaskOrchestrationEngine.getCircuitBreaker(serviceId).state).toBe('CLOSED');

      TaskOrchestrationEngine.recordCircuitFailure(serviceId);
      TaskOrchestrationEngine.recordCircuitFailure(serviceId);
      const state3 = TaskOrchestrationEngine.recordCircuitFailure(serviceId);

      expect(state3).toBe('OPEN');
      expect(TaskOrchestrationEngine.getCircuitBreaker(serviceId).state).toBe('OPEN');

      // Recover
      TaskOrchestrationEngine.recordCircuitSuccess(serviceId);
      expect(TaskOrchestrationEngine.getCircuitBreaker(serviceId).state).toBe('CLOSED');
    });
  });

  describe('Work Package 7.14 & 7.25: Orphan Detection & Restart Recovery', () => {
    it('should recover orphaned running tasks whose worker crashed', () => {
      // Register a crash-simulated worker
      const crashedWorker: WorkerDefinition = {
        workerId: 'worker-crashed-node',
        name: 'Crashed Node',
        type: 'DEVELOPMENT',
        version: '1.0.0',
        capabilities: ['CODE_EDIT'],
        supportedTaskTypes: ['dev_fix'],
        environment: 'development',
        maxConcurrency: 1,
        currentRunningCount: 1,
        status: 'INACTIVE',
        health: 'OFFLINE',
        lastHeartbeat: new Date(Date.now() - 3600000).toISOString(),
        currentTasks: [],
        permissions: { allowedTools: [], deniedTools: [], mcpDependencies: [] }
      };
      TaskOrchestrationEngine.registerWorker(crashedWorker);

      const { task } = TaskOrchestrationEngine.createTask({
        type: 'dev_fix',
        title: 'Orphaned task on dead worker',
        description: 'Orphan test',
        requester: 'Supervisor',
        priority: 'P2',
        severity: 'LOW',
        owner: 'CodingAgent',
        environment: 'development',
        maximumRuntimeMs: 30000,
        maxRetries: 3,
        approvalStatus: 'NOT_REQUIRED',
        requiredCapabilities: ['CODE_EDIT'],
        correlationId: 'corr-orphan-1',
        traceId: 'trace-orphan-1'
      });

      task.status = 'RUNNING';
      task.assignedWorker = 'worker-crashed-node';

      const recovery = TaskOrchestrationEngine.recoverOrphanTasks();
      expect(recovery.recoveredCount).toBeGreaterThanOrEqual(1);
      expect(TaskOrchestrationEngine.getTask(task.taskId)?.status).toBe('QUEUED');
    });
  });

  describe('Work Package 7.16 & 7.17: Scheduled Tasks & Misfire Policy', () => {
    it('should expose active schedules and trigger overdue jobs with misfire handling', () => {
      const schedules = TaskOrchestrationEngine.listSchedules();
      expect(schedules.length).toBeGreaterThanOrEqual(2);

      // Force one schedule to be overdue
      schedules[0].nextRun = new Date(Date.now() - 1000).toISOString();
      const trigger = TaskOrchestrationEngine.triggerScheduledJobs();
      expect(trigger.executed.length).toBeGreaterThanOrEqual(1);
    });
  });

  describe('Work Package 7.22 & 7.26: Observability Dashboard & Finite Simulation', () => {
    it('should return complete orchestration metrics across priorities and statuses', () => {
      const dashboard = TaskOrchestrationEngine.getObservabilityDashboard();
      expect(dashboard).toHaveProperty('queueDepth');
      expect(dashboard).toHaveProperty('runningCount');
      expect(dashboard).toHaveProperty('deadLetterCount');
      expect(dashboard).toHaveProperty('activeWorkersCount');
      expect(dashboard.activeWorkersCount).toBeGreaterThanOrEqual(3);
    });
  });
});
