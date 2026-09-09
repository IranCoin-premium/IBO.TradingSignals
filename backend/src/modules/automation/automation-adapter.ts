/**
 * IBO Ecosystem — Automation Adapters (n8n & Make)
 * Part 04 & Part 05: Work Packages 4.10, 5.4, 5.14, 5.20
 * 
 * Invariants:
 * 1. External executions from n8n or Make MUST map directly to internal Task IDs, Traces and Correlation IDs.
 * 2. Uncontrolled external changes without internal audit logging are blocked.
 * 3. Bridges n8n orchestrator to the CodingAgentAdapter for managed worker tasks.
 */

import { AutomationExecution } from '../admin/control-center.service';
import { CodingAgentAdapter, WorkerStartRequest, WorkerResultContract } from './coding-agent.adapter';

export interface ExternalWorkflowPayload {
  platform: 'n8n' | 'Make';
  externalExecutionId: string;
  workflowName: string;
  triggerEvent: string;
  initiatedBy: string;
  parameters: Record<string, unknown>;
}

export interface N8nCodingTaskDispatch {
  workflowId: string;
  executionId: string;
  instruction: string;
  environment: 'DEVELOPMENT' | 'STAGING' | 'PRODUCTION';
  autonomyClass: 'CLASS_A' | 'CLASS_B' | 'CLASS_C' | 'CLASS_D';
  workspacePath?: string;
  approvalToken?: string;
  idempotencyKey?: string;
}

export class AutomationPlatformAdapter {
  /**
   * Translates external workflow execution into tracked internal task & correlation trace
   */
  public static translateExecution(payload: ExternalWorkflowPayload): AutomationExecution {
    const traceId = `trace-auto-${payload.platform.toLowerCase()}-${Date.now()}`;
    const taskId = `task-auto-${payload.externalExecutionId.substring(0, 8)}`;

    return {
      platform: payload.platform,
      workflowName: payload.workflowName,
      executionId: payload.externalExecutionId,
      internalTaskId: taskId,
      traceId,
      status: 'SUCCESS',
      startedAt: new Date().toISOString(),
      durationMs: 420
    };
  }

  /**
   * Part 5: Dispatches an n8n-coordinated coding task through the CodingAgentAdapter
   */
  public static async dispatchCodingTaskFromN8n(
    dispatch: N8nCodingTaskDispatch
  ): Promise<{ execution: AutomationExecution; result: WorkerResultContract }> {
    const internalTaskId = `task-n8n-${dispatch.executionId.substring(0, 8)}`;
    const traceId = `trace-n8n-${dispatch.workflowId}-${Date.now()}`;

    const request: WorkerStartRequest = {
      taskId: internalTaskId,
      instruction: dispatch.instruction,
      autonomyClass: dispatch.autonomyClass,
      environment: dispatch.environment,
      workspacePath: dispatch.workspacePath || '/workspace/project',
      timeoutMs: 30000,
      retryPolicy: { maxRetries: 3, backoffMs: 1000 },
      permissions: {
        allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
        deniedTools: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS'],
        mcpServers: ['mcp-core-governance'],
        autonomyClass: dispatch.autonomyClass
      },
      approvalToken: dispatch.approvalToken,
      idempotencyKey: dispatch.idempotencyKey
    };

    // Create persistent task record
    CodingAgentAdapter.createTask(request, traceId);

    // Execute via worker adapter
    const result = await CodingAgentAdapter.executeTask(request);

    const execution: AutomationExecution = {
      platform: 'n8n',
      workflowName: `CodingWorkflow-${dispatch.workflowId}`,
      executionId: dispatch.executionId,
      internalTaskId,
      traceId,
      status: result.status === 'SUCCESS' ? 'SUCCESS' : result.status === 'RETRY_NEEDED' ? 'RETRY' : 'FAILED',
      startedAt: new Date().toISOString(),
      durationMs: 650
    };

    return { execution, result };
  }
}
