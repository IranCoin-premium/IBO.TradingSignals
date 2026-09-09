/**
 * IBO Ecosystem — Controlled Task Schema & Lifecycle Machine
 * Part 02: Task Foundation & Auditable Work Units
 */

import { StandardEventType } from './event-schema';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export type TaskStatus =
  | 'NEW'
  | 'QUEUED'
  | 'RUNNING'
  | 'VERIFYING'
  | 'SUCCESS'
  | 'RETRY'
  | 'BLOCKED'
  | 'FAILED'
  | 'ESCALATED';

export type TaskOwner =
  | 'VTQI'
  | 'CODING_AGENT'
  | 'UI_UX_AGENT'
  | 'TRANSLATOR_AGENT'
  | 'MEDIA_AGENT'
  | 'HUMAN_ADMIN';

export type StandardTaskType =
  | 'inspect_login_error'
  | 'run_build_validation'
  | 'review_visual_layout'
  | 'check_persian_text'
  | 'inspect_test_failure'
  | 'review_rtl_direction'
  | 'review_component_consistency'
  | 'audit_security_secrets'
  | 'resolve_merge_conflict';

export interface IBOTask {
  taskId: string;            // UUID v4 format
  parentEventId?: string;    // Links task to the event that triggered it
  taskType: StandardTaskType;
  owner: TaskOwner;
  priority: TaskPriority;
  status: TaskStatus;
  environment: 'development' | 'staging' | 'production';
  createdAt: string;         // ISO 8601
  updatedAt: string;         // ISO 8601
  retryCount: number;        // Max allowed: 3
  correlationId?: string;
  metadata?: Record<string, unknown>;
}

export interface StructuredWorkerResult {
  taskId: string;
  workerId: string;
  resultStatus: 'SUCCESS' | 'RETRY_NEEDED' | 'BLOCKED' | 'FATAL_FAILED';
  summary: string;
  findings: Array<{
    ruleId: string;
    severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
    description: string;
    targetComponent?: string;
    targetFile?: string;
    suggestedFix?: string;
  }>;
  changedResources: string[];
  evidence: Record<string, unknown>;
  testsRun: string[];
  testResults: {
    passed: boolean;
    passedTests: number;
    failedTests: number;
    outputLogSummary: string;
  };
  confidence: number;       // Range: 0.0 to 1.0
  recommendedNextAction: 'PROMOTE' | 'REQUEST_DEV_FIX' | 'ESCALATE_TO_ADMIN' | 'ABORT';
}

/**
 * Validates task transition invariants according to Part 1 and Part 2 lifecycle.
 */
export function validateTaskTransition(currentStatus: TaskStatus, nextStatus: TaskStatus): boolean {
  const allowedTransitions: Record<TaskStatus, TaskStatus[]> = {
    NEW: ['QUEUED', 'BLOCKED'],
    QUEUED: ['RUNNING', 'BLOCKED'],
    RUNNING: ['VERIFYING', 'BLOCKED', 'FAILED'],
    VERIFYING: ['SUCCESS', 'RETRY', 'FAILED'],
    SUCCESS: [], // Terminal
    RETRY: ['RUNNING', 'FAILED'],
    BLOCKED: ['QUEUED', 'ESCALATED'],
    FAILED: ['ESCALATED'],
    ESCALATED: ['QUEUED'] // Can only be re-queued after human intervention
  };

  return allowedTransitions[currentStatus]?.includes(nextStatus) ?? false;
}
