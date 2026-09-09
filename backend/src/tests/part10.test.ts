/**
 * IBO Ecosystem — Observability, Runtime Resilience & Incident Management Test Suite
 * Master Prompt — Part 10: Work Packages 10.1 - 10.28
 */

import { RuntimeResilienceObservabilityService } from '../modules/observability/resilience.service';
import { ObservabilityService } from '../modules/observability/observability.service';

describe('Part 10 — Observability, Runtime Resilience & Incident Detection', () => {

  describe('Work Package 10.2 & 10.3: Structured Logs & Redaction Invariants', () => {
    it('should scrub raw secrets from structured logs while preserving trace and correlation IDs', () => {
      const log = ObservabilityService.log({
        severity: 'INFO',
        component: 'TaskRunner',
        summary: 'Worker connected with token=AIzaSySecretToken123456789012345678901',
        environment: 'staging',
        traceId: 'trace-obs-01',
        correlationId: 'corr-obs-01'
      });

      expect(log.redactionStatus).toBe('SCRUBBED');
      expect(log.summary).not.toContain('AIzaSySecretToken123456789012345678901');
      expect(log.summary.includes('[REDACTED]') || log.summary.includes('[REDACTED_SECRET]')).toBe(true);
      expect(log.traceId).toBe('trace-obs-01');
    });
  });

  describe('Work Package 10.5 & 10.21: Component Health Probes & Golden Signals', () => {
    it('should aggregate component health across orchestrator, agents, policy engine and database', () => {
      const overview = RuntimeResilienceObservabilityService.getSystemHealthOverview();

      expect(overview.overallHealth).toBeDefined();
      expect(overview.components['TaskOrchestrator']).toBeDefined();
      expect(overview.components['MultiAgentRegistry']).toBeDefined();
      expect(overview.components['ZeroTrustPolicyEngine']).toBeDefined();
      expect(overview.metrics.cpuUsagePct).toBeGreaterThan(0);
    });
  });

  describe('Work Package 10.12 & 10.13: Incident Lifecycle Management', () => {
    it('should declare an incident with severity, track timeline, and transition lifecycle states', () => {
      const incident = RuntimeResilienceObservabilityService.declareIncident({
        severity: 'SEV-2',
        title: 'High latency on MCP Gateway',
        affectedComponents: ['MCPServers', 'TaskOrchestrator'],
        rootCauseHypothesis: 'External MCP network saturation',
        evidenceRefs: ['ev-mcp-lat-01']
      });

      expect(incident.incidentId).toBeDefined();
      expect(incident.status).toBe('DETECTED');
      expect(incident.timeline.length).toBe(1);

      // Transition to ACKNOWLEDGED then RECOVERED
      RuntimeResilienceObservabilityService.updateIncidentState(
        incident.incidentId,
        'ACKNOWLEDGED',
        'Investigating network routes',
        'HumanAdmin'
      );
      
      const updated = RuntimeResilienceObservabilityService.getIncident(incident.incidentId);
      expect(updated?.status).toBe('ACKNOWLEDGED');
      expect(updated?.timeline.length).toBe(2);
    });
  });

  describe('Work Package 10.14 & 10.16: Governed Auto-Recovery & Loop Defense', () => {
    it('should execute bounded auto-recovery and halt with escalation when recovery loop threshold is reached', () => {
      const target = 'Worker-Transient-01';

      // 1st recovery attempt: PASS
      const res1 = RuntimeResilienceObservabilityService.executeGovernedRecovery({
        targetComponent: target,
        recoveryAction: 'RESTART_WORKER',
        scope: 'worker:instance',
        maxAttempts: 2
      });
      expect(res1.success).toBe(true);
      expect(res1.loopDetected).toBe(false);

      // 2nd recovery attempt: PASS
      const res2 = RuntimeResilienceObservabilityService.executeGovernedRecovery({
        targetComponent: target,
        recoveryAction: 'RESTART_WORKER',
        scope: 'worker:instance',
        maxAttempts: 2
      });
      expect(res2.success).toBe(true);

      // 3rd recovery attempt: LOOP DETECTED & HALTED
      const res3 = RuntimeResilienceObservabilityService.executeGovernedRecovery({
        targetComponent: target,
        recoveryAction: 'RESTART_WORKER',
        scope: 'worker:instance',
        maxAttempts: 2
      });
      expect(res3.success).toBe(false);
      expect(res3.loopDetected).toBe(true);
      expect(res3.actionTaken).toBe('HALT_AND_ESCALATE');
    });
  });

  describe('Work Package 10.4 & 10.28: End-to-End Trace Verification', () => {
    it('should record and retrieve full end-to-end trace from requester to verification', () => {
      RuntimeResilienceObservabilityService.recordTrace({
        traceId: 'trace-e2e-100',
        correlationId: 'corr-e2e-100',
        taskId: 'task-e2e-100',
        humanRequester: 'admin@ibo.com',
        coordinatorAgent: 'agent-chief-coordinator',
        assignedSpecialist: 'agent-engineering-lead',
        workerAdapter: 'worker-opencode-primary',
        executionMode: 'STAGING',
        environment: 'staging',
        toolsInvoked: ['FILE_READ', 'TEST_RUNNER'],
        mcpServers: ['mcp-core-governance'],
        steps: [
          { stepName: 'PLANNING', status: 'SUCCESS', durationMs: 120 },
          { stepName: 'CODE_EDIT', status: 'SUCCESS', durationMs: 450 },
          { stepName: 'TEST_EXECUTION', status: 'SUCCESS', durationMs: 1200 }
        ],
        verificationStatus: 'VERIFIED_PASS',
        finalStatus: 'SUCCESS'
      });

      const retrieved = RuntimeResilienceObservabilityService.getTrace('trace-e2e-100');
      expect(retrieved).toBeDefined();
      expect(retrieved?.steps.length).toBe(3);
      expect(retrieved?.verificationStatus).toBe('VERIFIED_PASS');
    });
  });

  describe('Work Package 10.27: Governed Safe Mode Activation', () => {
    it('should toggle safe mode and reflect in system health status', () => {
      RuntimeResilienceObservabilityService.setSafeMode(true, 'Simulated critical storage incident');
      const health = RuntimeResilienceObservabilityService.getSystemHealthOverview();
      expect(health.safeMode).toBe(true);
      expect(health.overallHealth).toBe('DEGRADED');

      // Reset
      RuntimeResilienceObservabilityService.setSafeMode(false, 'Incident resolved');
    });
  });
});
