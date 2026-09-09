/**
 * IBO Ecosystem — Zero-Trust Security, Permissions, Policy & Secret Governance Test Suite
 * Master Prompt — Part 09: Work Packages 9.1 - 9.28
 */

import { ZeroTrustPolicyEngine } from '../security/policy/policy-engine.service';
import { SecurityGuardian } from '../security/guardian/guardian';

describe('Part 09 — Security, Permissions, Policy Engine & Trust Governance', () => {

  describe('Work Package 9.4 & 9.8: Zero-Trust Policy Engine & Execution Mode Enforcement', () => {
    it('should DENY production deployment when task execution mode is STAGING or READ_ONLY', () => {
      const decision = ZeroTrustPolicyEngine.evaluateAccess({
        actorId: 'agent-engineering-lead',
        actorType: 'SPECIALIST_AGENT',
        action: 'DEPLOY',
        resource: 'production:backend_api',
        resourceClassification: 'CRITICAL',
        environment: 'production',
        executionMode: 'STAGING', // Mode mismatch
        riskClass: 'R3'
      });

      expect(decision.decision).toBe('DENY');
      expect(decision.reason).toContain('Production resource cannot be targeted with execution mode STAGING');
    });

    it('should HARD-DENY live trading access or raw secret dumping attempts', () => {
      const decision = ZeroTrustPolicyEngine.evaluateAccess({
        actorId: 'agent-chief-coordinator',
        actorType: 'CHIEF_AGENT',
        action: 'EXECUTE',
        resource: 'live_trading:pocket_option_broker',
        resourceClassification: 'CRITICAL',
        environment: 'production',
        executionMode: 'PRODUCTION',
        riskClass: 'R4'
      });

      expect(decision.decision).toBe('DENY');
      expect(decision.reason).toContain('Hard deny');
    });

    it('should REQUIRE_APPROVAL for R3/R4 high-risk operations lacking explicit approval', () => {
      const decision = ZeroTrustPolicyEngine.evaluateAccess({
        actorId: 'agent-engineering-lead',
        actorType: 'SPECIALIST_AGENT',
        action: 'UPDATE',
        resource: 'database:schema_orders',
        resourceClassification: 'CRITICAL',
        environment: 'staging',
        executionMode: 'STAGING',
        riskClass: 'R3',
        hasApproval: false
      });

      expect(decision.decision).toBe('REQUIRE_APPROVAL');
      expect(decision.reason).toContain('requires explicit Human/Governance approval');
    });

    it('should ALLOW safe verified operations matching agent scope and policy', () => {
      const decision = ZeroTrustPolicyEngine.evaluateAccess({
        actorId: 'agent-engineering-lead',
        actorType: 'SPECIALIST_AGENT',
        action: 'READ',
        resource: 'repository:src/utils',
        resourceClassification: 'INTERNAL',
        environment: 'development',
        executionMode: 'STAGING',
        riskClass: 'R1'
      });

      expect(decision.decision).toBe('ALLOW');
    });
  });

  describe('Work Package 9.10: Secret Redaction & Leak Prevention', () => {
    it('should detect and redact API keys, JWT tokens, and secrets from logs and outputs', () => {
      const sampleLog = 'Worker executed with API_KEY="AIzaSyA1234567890123456789012345678901" and bearer="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0"';
      
      const hasLeak = ZeroTrustPolicyEngine.detectSecretLeak(sampleLog);
      expect(hasLeak).toBe(true);

      const redacted = ZeroTrustPolicyEngine.redactSecrets(sampleLog);
      expect(redacted).not.toContain('AIzaSyA1234567890123456789012345678901');
      expect(redacted).toContain('[REDACTED_SECRET]');
    });
  });

  describe('Work Package 9.11: Filesystem Boundary & Path Traversal Defense', () => {
    it('should reject path traversal attempts escaping workspace boundaries', () => {
      expect(ZeroTrustPolicyEngine.isSafePath('../../etc/passwd')).toBe(false);
      expect(ZeroTrustPolicyEngine.isSafePath('/root/.ssh/id_rsa')).toBe(false);
      expect(ZeroTrustPolicyEngine.isSafePath('/home/admin/.env')).toBe(false);
      expect(ZeroTrustPolicyEngine.isSafePath('/workspace/src/app.ts')).toBe(true);
      expect(ZeroTrustPolicyEngine.isSafePath('app/src/main/java/com/example/MainActivity.kt')).toBe(true);
    });
  });

  describe('Work Package 9.15: Security Incidents & Observability', () => {
    it('should record security incident and provide dashboard counts', () => {
      const incident = ZeroTrustPolicyEngine.recordIncident({
        severity: 'HIGH',
        title: 'Unauthorized Sudo Execution Attempt',
        description: 'Worker attempted privileged shell execution',
        sourceActor: 'worker-opencode-primary',
        targetResource: 'system:root_shell'
      });

      expect(incident.incidentId).toBeDefined();
      expect(incident.status).toBe('DETECTED');

      const overview = ZeroTrustPolicyEngine.getSecurityOverview();
      expect(overview.activeIncidentsCount).toBeGreaterThanOrEqual(1);
    });
  });

  describe('Work Package 9.1: Automated Security Guardian Scan', () => {
    it('should scan codebase and report zero uncontained P0 vulnerabilities', () => {
      const guardian = new SecurityGuardian();
      const findings = guardian.runScan();
      const openP0 = findings.filter((f) => f.severity === 'P0' && f.status === 'OPEN');
      expect(openP0.length).toBe(0);
    });
  });
});
