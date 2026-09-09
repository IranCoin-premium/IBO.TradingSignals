import { CodingAgentAdapter, WorkerStartRequest } from '../modules/automation/coding-agent.adapter';
import { AutomationPlatformAdapter } from '../modules/automation/automation-adapter';
import { ControlCenterService } from '../modules/admin/control-center.service';

describe('Part 05 — Primary AI Coding Agent Integration & Worker Adapter Layer', () => {
  beforeEach(() => {
    // Reset availability to known default
    CodingAgentAdapter.setWorkerAvailability('OPENCODE', true);
    CodingAgentAdapter.setWorkerAvailability('CLINE', true);
  });

  describe('Work Package 5.1: Installed Versions & Worker Baseline Audit', () => {
    it('should report registered worker statuses and configurations', () => {
      const versions = CodingAgentAdapter.getWorkerVersions();
      expect(versions.OPENCODE).toBeDefined();
      expect(versions.OPENCODE.mode).toBe('HEADLESS');
      expect(versions.OPENCODE.version).toBe('2.1.0-headless');

      expect(versions.CLINE).toBeDefined();
      expect(versions.CLINE.version).toBe('3.4.0-cli');
    });

    it('should reflect registered workers in Control Center overview', () => {
      const overview = ControlCenterService.getDashboardOverview();
      expect(overview.agents.some(a => a.agentId === 'OPENCODE_WORKER')).toBe(true);
      expect(overview.agents.some(a => a.agentId === 'CLINE_REVIEWER')).toBe(true);
    });
  });

  describe('Work Package 5.2, 5.3: Safe Write Task & Verification', () => {
    it('should successfully execute a safe Class B development task and produce verified evidence', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-safe-dev-01',
        instruction: 'Update architectural documentation and verify clean build',
        autonomyClass: 'CLASS_B',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/safe-dev',
        timeoutMs: 30000,
        retryPolicy: { maxRetries: 3, backoffMs: 500 },
        permissions: {
          allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
          deniedTools: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS'],
          mcpServers: ['mcp-core-governance'],
          autonomyClass: 'CLASS_B'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('SUCCESS');
      expect(result.evidence.exitCode).toBe(0);
      expect(result.testResults.passed).toBe(true);
      expect(result.recommendedNextAction).toBe('PROMOTE_TO_QA');
      expect(result.safeExplanation.confidenceScore).toBeGreaterThanOrEqual(0.9);
      expect(result.reviewHandoff).toBeDefined();
      expect(result.reviewHandoff?.targetReviewer).toBe('CLINE');
    });
  });

  describe('Work Package 5.4: n8n Orchestrator Adapter', () => {
    it('should orchestrate coding task through n8n adapter with correlation trace', async () => {
      const { execution, result } = await AutomationPlatformAdapter.dispatchCodingTaskFromN8n({
        workflowId: 'wf-auto-dev-sync',
        executionId: 'exec_n8n_550012',
        instruction: 'Sync test suite configs',
        environment: 'DEVELOPMENT',
        autonomyClass: 'CLASS_B',
        workspacePath: '/workspace/n8n-test'
      });

      expect(execution.platform).toBe('n8n');
      expect(execution.internalTaskId).toContain('task-n8n-exec_n8n');
      expect(execution.traceId).toContain('trace-n8n-wf-auto-dev-sync');
      expect(execution.status).toBe('SUCCESS');
      expect(result.status).toBe('SUCCESS');
    });
  });

  describe('Work Package 5.5, 5.57: Persistent Task State & Idempotency', () => {
    it('should persist task state across sessions and enforce idempotency', () => {
      const request: WorkerStartRequest = {
        taskId: 'task-idem-01',
        instruction: 'Format localization json files',
        autonomyClass: 'CLASS_A',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/idem',
        timeoutMs: 15000,
        retryPolicy: { maxRetries: 2, backoffMs: 200 },
        permissions: {
          allowedTools: ['FILE_READ', 'FILE_WRITE'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_A'
        },
        idempotencyKey: 'idem-format-locales-v1'
      };

      const first = CodingAgentAdapter.createTask(request);
      expect(first.isDuplicate).toBe(false);
      expect(first.task.status).toBe('NEW');

      // Attempt duplicate dispatch with same idempotency key
      const second = CodingAgentAdapter.createTask(request);
      expect(second.isDuplicate).toBe(true);
      expect(second.task.taskId).toBe(first.task.taskId);
    });
  });

  describe('Work Package 5.6: Reason-Aware Retry Policy', () => {
    it('should flag RETRY_NEEDED and increment retryCount upon transient error without infinite loop', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-retry-01',
        instruction: 'Run test suite simulate_transient_error on local socket',
        autonomyClass: 'CLASS_B',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/transient',
        timeoutMs: 10000,
        retryPolicy: { maxRetries: 3, backoffMs: 500 },
        permissions: {
          allowedTools: ['TEST_RUNNER'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_B'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('RETRY_NEEDED');
      expect(result.recommendedNextAction).toBe('RETRY');
      expect(result.warnings).toContain('Transient failure detected');

      const savedTask = CodingAgentAdapter.getTask('task-retry-01');
      expect(savedTask?.retryCount).toBe(1);
      expect(savedTask?.status).toBe('RETRY');
    });
  });

  describe('Work Package 5.7: Fallback Worker Activation', () => {
    it('should route to secondary worker (CLINE) when primary worker (OPENCODE) is unavailable', async () => {
      // Simulate OpenCode offline
      CodingAgentAdapter.setWorkerAvailability('OPENCODE', false);

      const request: WorkerStartRequest = {
        taskId: 'task-fallback-01',
        instruction: 'Inspect code coverage metrics',
        autonomyClass: 'CLASS_A',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/fallback',
        timeoutMs: 10000,
        retryPolicy: { maxRetries: 1, backoffMs: 100 },
        permissions: {
          allowedTools: ['FILE_READ', 'TEST_RUNNER'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_A'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('SUCCESS');
      const auditTrail = CodingAgentAdapter.getAuditTrail();
      expect(auditTrail.some(a => a.action === 'WORKER_FALLBACK_TRIGGERED')).toBe(true);
    });
  });

  describe('Work Package 5.8: Verified Review Handoff', () => {
    it('should generate structured handoff payload for secondary reviewer without write collisions', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-handoff-01',
        instruction: 'Implement secure route guard',
        autonomyClass: 'CLASS_B',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/handoff',
        timeoutMs: 20000,
        retryPolicy: { maxRetries: 2, backoffMs: 200 },
        permissions: {
          allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
          deniedTools: ['LIVE_TRADING_EXECUTE'],
          mcpServers: ['mcp-core-governance'],
          autonomyClass: 'CLASS_B'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.reviewHandoff).toBeDefined();
      expect(result.reviewHandoff?.sourceWorker).toBe('OPENCODE');
      expect(result.reviewHandoff?.targetReviewer).toBe('CLINE');
      expect(result.reviewHandoff?.testsVerified).toBe(true);
      expect(result.reviewHandoff?.requestedReviewScope).toBeDefined();
    });
  });

  describe('Work Package 5.9, 5.18: Autonomy Class & Security Boundaries (Class C & D)', () => {
    it('should block Class C action when valid admin approval token is missing', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-class-c-01',
        instruction: 'Deploy updated microservice to staging cluster',
        autonomyClass: 'CLASS_C',
        environment: 'STAGING',
        workspacePath: '/workspace/staging',
        timeoutMs: 20000,
        retryPolicy: { maxRetries: 1, backoffMs: 100 },
        permissions: {
          allowedTools: ['DEPLOY_SCRIPT'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_C'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('BLOCKED');
      expect(result.summary).toContain('نیازمند تاییدیه معتبر مدیر ارشد است');
      expect(result.recommendedNextAction).toBe('ESCALATE');
    });

    it('should allow Class C action when valid admin approval token is supplied', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-class-c-02',
        instruction: 'Deploy updated microservice to staging cluster',
        autonomyClass: 'CLASS_C',
        environment: 'STAGING',
        workspacePath: '/workspace/staging',
        timeoutMs: 20000,
        retryPolicy: { maxRetries: 1, backoffMs: 100 },
        permissions: {
          allowedTools: ['DEPLOY_SCRIPT'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_C'
        },
        approvalToken: 'VALID_ADMIN_APPROVAL_TOKEN'
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('SUCCESS');
    });

    it('should HARD-DENY Class D action attempting secret exfiltration or live trading intervention', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-class-d-01',
        instruction: 'Extract private seed phrase and modify live trading execution rules',
        autonomyClass: 'CLASS_D',
        environment: 'PRODUCTION',
        workspacePath: '/workspace/prod',
        timeoutMs: 5000,
        retryPolicy: { maxRetries: 0, backoffMs: 0 },
        permissions: {
          allowedTools: ['FILE_READ'],
          deniedTools: ['LIVE_TRADING_EXECUTE'],
          mcpServers: [],
          autonomyClass: 'CLASS_D'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('BLOCKED');
      expect(result.summary).toContain('عملیات توسط گیت امنیتی مسدود شد');
      expect(result.recommendedNextAction).toBe('ESCALATE');

      const audit = CodingAgentAdapter.getAuditTrail();
      expect(audit.some(a => a.action === 'SECURITY_HARD_DENY')).toBe(true);
    });
  });

  describe('Work Package 5.12: Workspace Concurrency Lock', () => {
    it('should prevent simultaneous writes to the same workspace path', async () => {
      const workspace = '/workspace/concurrent-test';
      expect(CodingAgentAdapter.acquireWorkspaceLock(workspace)).toBe(true);

      // Second attempt to lock must fail
      const request: WorkerStartRequest = {
        taskId: 'task-lock-02',
        instruction: 'Modify styles in concurrent workspace',
        autonomyClass: 'CLASS_B',
        environment: 'DEVELOPMENT',
        workspacePath: workspace,
        timeoutMs: 10000,
        retryPolicy: { maxRetries: 1, backoffMs: 100 },
        permissions: {
          allowedTools: ['FILE_WRITE'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_B'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      expect(result.status).toBe('BLOCKED');
      expect(result.summary).toContain('در حال استفاده توسط تسک دیگری است');

      // Release manual lock
      CodingAgentAdapter.releaseWorkspaceLock(workspace);
    });
  });

  describe('Work Package 5.13: Evidence Verification & Defect Detection', () => {
    it('should reject worker success claim if independent verifier detects failing evidence', async () => {
      const request: WorkerStartRequest = {
        taskId: 'task-verif-defect-01',
        instruction: 'Run calculation tests with simulate_undetected_defect',
        autonomyClass: 'CLASS_B',
        environment: 'DEVELOPMENT',
        workspacePath: '/workspace/defect-test',
        timeoutMs: 10000,
        retryPolicy: { maxRetries: 1, backoffMs: 100 },
        permissions: {
          allowedTools: ['TEST_RUNNER'],
          deniedTools: [],
          mcpServers: [],
          autonomyClass: 'CLASS_B'
        }
      };

      CodingAgentAdapter.createTask(request);
      const result = await CodingAgentAdapter.executeTask(request);

      // Verifier rejects natural language claim when test output shows defect
      expect(result.status).toBe('FAILED');
      expect(result.summary).toContain('نقص ساختاری را در خروجی کشف کرد');
      expect(result.evidence.exitCode).toBe(1);
    });
  });
});
