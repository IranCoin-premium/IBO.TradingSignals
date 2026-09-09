import { validateIBOEvent, IBOEvent } from '../events/event-schema';
import { validateTaskTransition, IBOTask } from '../events/task-schema';
import { VTQISystemCoordinator } from '../agents/vtqi/vtqi-coordinator';

describe('Part 02 — Infrastructure, Events & VTQI Foundations', () => {
  describe('Work Package 2.4: Minimal Event Schema', () => {
    it('should validate a compliant IBOEvent with required fields', () => {
      const validEvent: IBOEvent = {
        eventId: 'evt-1002-3004-9005',
        eventType: 'visual_issue_detected',
        source: 'VTQI_SYSTEM',
        timestamp: new Date().toISOString(),
        environment: 'development',
        severity: 'WARNING',
        payload: {
          component: 'SubscriptionScreen',
          defect: 'Text overflow in card header'
        }
      };

      const result = validateIBOEvent(validEvent);
      expect(result.valid).toBe(true);
      expect(result.errors).toHaveLength(0);
    });

    it('should reject an event containing prohibited secret patterns', () => {
      const leakyEvent: Partial<IBOEvent> = {
        eventId: 'evt-leaky-001',
        eventType: 'application_error_detected',
        source: 'BACKEND_API',
        timestamp: new Date().toISOString(),
        environment: 'development',
        severity: 'CRITICAL',
        payload: {
          secret: 'sk-abcdef12345678901234567890'
        }
      };

      const result = validateIBOEvent(leakyEvent);
      expect(result.valid).toBe(false);
      expect(result.errors.some(e => e.includes('SECURITY VIOLATION'))).toBe(true);
    });
  });

  describe('Work Package 2.5 & 2.6: Task Schema & State Transitions', () => {
    it('should permit valid lifecycle transitions', () => {
      expect(validateTaskTransition('NEW', 'QUEUED')).toBe(true);
      expect(validateTaskTransition('QUEUED', 'RUNNING')).toBe(true);
      expect(validateTaskTransition('RUNNING', 'VERIFYING')).toBe(true);
      expect(validateTaskTransition('VERIFYING', 'SUCCESS')).toBe(true);
      expect(validateTaskTransition('VERIFYING', 'RETRY')).toBe(true);
      expect(validateTaskTransition('RETRY', 'RUNNING')).toBe(true);
      expect(validateTaskTransition('RUNNING', 'BLOCKED')).toBe(true);
      expect(validateTaskTransition('BLOCKED', 'ESCALATED')).toBe(true);
    });

    it('should block illegal status jumps (e.g. NEW directly to SUCCESS)', () => {
      expect(validateTaskTransition('NEW', 'SUCCESS')).toBe(false);
      expect(validateTaskTransition('QUEUED', 'SUCCESS')).toBe(false);
      expect(validateTaskTransition('SUCCESS', 'RUNNING')).toBe(false);
    });
  });

  describe('Work Package 2.8 & 2.9: VTQI Scope Enforcement & Boundaries', () => {
    it('should permit valid visual and typography tasks for VTQI', () => {
      const validTask: IBOTask = {
        taskId: 'task-vtqi-001',
        taskType: 'review_visual_layout',
        owner: 'VTQI',
        priority: 'MEDIUM',
        status: 'NEW',
        environment: 'development',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        retryCount: 0,
        metadata: {
          screen: 'SubscriptionScreen',
          check: 'RTL Persian numeral spacing'
        }
      };

      const result = VTQISystemCoordinator.validateTaskScope(validTask);
      expect(result.allowed).toBe(true);
    });

    it('should strictly reject out-of-scope trading, signal, or financial tasks', () => {
      const tradingTask: IBOTask = {
        taskId: 'task-vtqi-invalid',
        taskType: 'review_visual_layout',
        owner: 'VTQI',
        priority: 'HIGH',
        status: 'NEW',
        environment: 'development',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        retryCount: 0,
        metadata: {
          action: 'calculate binary option trading signal payout'
        }
      };

      const result = VTQISystemCoordinator.validateTaskScope(tradingTask);
      expect(result.allowed).toBe(false);
      expect(result.reason).toContain('SECURITY & BOUNDARY VIOLATION');
    });

    it('should return a structured worker result with fail-closed status upon scope rejection', () => {
      const outOfScopeTask: IBOTask = {
        taskId: 'task-vtqi-db-mutation',
        taskType: 'review_visual_layout',
        owner: 'VTQI',
        priority: 'URGENT',
        status: 'RUNNING',
        environment: 'development',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        retryCount: 0,
        metadata: {
          target: 'update postgres database schema'
        }
      };

      const workerResult = VTQISystemCoordinator.processInspection(outOfScopeTask, {
        componentName: 'Database',
        filePath: 'backend/src/database',
        scope: 'VISUAL'
      });

      expect(workerResult.resultStatus).toBe('FATAL_FAILED');
      expect(workerResult.recommendedNextAction).toBe('ABORT');
      expect(workerResult.testResults.passed).toBe(false);
    });
  });
});
