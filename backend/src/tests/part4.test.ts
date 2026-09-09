import { ObservabilityService } from '../modules/observability/observability.service';
import { ControlCenterService } from '../modules/admin/control-center.service';
import { WebExperienceInspector, REPRESENTATIVE_VIEWPORTS } from '../modules/qa/web-experience-inspector';
import { AutomationPlatformAdapter } from '../modules/automation/automation-adapter';

describe('Part 04 — Agent Control Center, Observability, Chief Agent & Multi-Viewport QA', () => {
  describe('Work Package 4.2 & 4.4: Observability, UTC & Secret Redaction', () => {
    it('should format UTC timestamps into display timezone (+03:30 for Tehran)', () => {
      const utc = '2026-09-07T12:00:00.000Z';
      const tehranFormatted = ObservabilityService.formatWithTimezone(utc, 210);
      expect(tehranFormatted).toContain('2026-09-07 15:30:00 +03:30');
    });

    it('should scrub synthetic secrets from logs without leaking raw credentials', () => {
      const rawPayload = {
        user: 'admin_test',
        password: 'SuperSecretPassword123!',
        token: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummyPayload.signaturePart',
        nested: {
          apiKey: 'FAKE_DUMMY_KEY_REDACTED_12345',
          note: 'Public log message'
        }
      };

      const { redacted, wasScrubbed } = ObservabilityService.redactObject(rawPayload);
      expect(wasScrubbed).toBe(true);
      expect(redacted.password).toBe('[REDACTED]');
      expect(redacted.token).toBe('[REDACTED]');
      expect(redacted.nested.apiKey).toBe('[REDACTED]');
      expect(redacted.nested.note).toBe('Public log message');
    });
  });

  describe('Work Package 4.3: Admin-Safe Explanation Summary', () => {
    it('should create sanitized Admin-Safe explanation without raw chain-of-thought leaks', () => {
      const explanation = ObservabilityService.createAdminSafeExplanation({
        taskId: 'task-safe-001',
        agentId: 'CAC',
        objective: 'Fix header layout on mobile',
        planSummary: 'Inspect CSS min-width',
        verifiedEvidence: ['Observed 320px horizontal overflow with Bearer secret-leaked-token'],
        observedFacts: ['Navigation container has min-width: 480px'],
        assumptions: ['Mobile viewport is <= 390px'],
        alternativesConsidered: ['Drop navigation', 'Use collapsible hamburger'],
        decision: 'Use collapsible hamburger menu',
        decisionRationale: 'Preserves touch target while preventing overflow',
        uncertainty: 'Physical foldables not tested',
        riskAssessment: 'Low',
        confidenceScore: 0.95,
        toolsUsed: ['WPEI_VIEWPORT_AUDIT'],
        mcpUsed: ['mcp-ui-inspector'],
        rulesEvaluated: ['RULE-RESPONSIVE-001'],
        hooksTriggered: ['before_code_edit'],
        testsRun: ['viewport_test'],
        finalResult: 'PASS',
        nextAction: 'Create pull request'
      });

      expect(explanation.confidenceScore).toBe(0.95);
      expect(explanation.verifiedEvidence[0]).toContain('Bearer [REDACTED]');
      expect(explanation.decision).toContain('collapsible hamburger');
    });
  });

  describe('Work Package 4.5 & 4.7: Control Center & Chief Agent Consultation', () => {
    it('should return complete system health and registered agents in dashboard overview', () => {
      const overview = ControlCenterService.getDashboardOverview();
      expect(overview.systemHealth).toBe('HEALTHY');
      expect(overview.totalRegisteredAgents).toBeGreaterThanOrEqual(5);
      expect(overview.agents.some(a => a.agentId === 'CAC')).toBe(true);
      expect(overview.agents.some(a => a.agentId === 'VTQI')).toBe(true);
    });

    it('should handle admin chat consultation for Login Modal inquiry with specialist feedback', () => {
      const chatRes = ControlCenterService.handleChiefAgentChat('به نظرم فرم ورود یا لاگین باید به صورت مودال (Modal) شود.', 'admin@ibo.com');
      expect(chatRes.actionTaken).toBe('CONSULTED_VTQI_AND_WPEI');
      expect(chatRes.responsePersian).toContain('موبایل');
      expect(chatRes.safeExplanation.confidenceScore).toBeGreaterThan(0.9);
      expect(chatRes.proposalRequired).toBe(true);
    });

    it('should initialize persistent research when research is requested via admin chat', () => {
      const chatRes = ControlCenterService.handleChiefAgentChat('بررسی کن آیا بازار استرالیا آمادگی راه اندازی دارد؟', 'admin@ibo.com');
      expect(chatRes.actionTaken).toBe('PERSISTENT_RESEARCH_INITIATED');
      expect(chatRes.initiativeId).toBeDefined();
      expect(chatRes.responsePersian).toContain('طرح پژوهشی پایدار');
    });
  });

  describe('Work Package 4.10: Automation Platform Adapter (n8n & Make)', () => {
    it('should translate external n8n workflow execution to internal task and trace ID', () => {
      const exec = AutomationPlatformAdapter.translateExecution({
        platform: 'n8n',
        externalExecutionId: 'exec_n8n_998124',
        workflowName: 'Daily-Market-Sync',
        triggerEvent: 'scheduled_cron',
        initiatedBy: 'n8n_cloud_trigger',
        parameters: { target: 'TR' }
      });

      expect(exec.platform).toBe('n8n');
      expect(exec.internalTaskId).toContain('task-auto-exec_n8n');
      expect(exec.traceId).toContain('trace-auto-n8n');
      expect(exec.status).toBe('SUCCESS');
    });
  });

  describe('Work Package 4.11, 4.12, 4.13: Multi-Viewport Matrix & Visual QA (VQAA)', () => {
    it('should include representative viewports from Extra Small Mobile (320px) to Ultrawide (2560px)', () => {
      expect(REPRESENTATIVE_VIEWPORTS.length).toBeGreaterThanOrEqual(8);
      const xs = REPRESENTATIVE_VIEWPORTS.find(v => v.id === 'xs-mobile');
      expect(xs).toBeDefined();
      expect(xs?.width).toBe(320);

      const ultrawide = REPRESENTATIVE_VIEWPORTS.find(v => v.id === 'ultrawide');
      expect(ultrawide).toBeDefined();
      expect(ultrawide?.width).toBe(2560);
    });

    it('should detect horizontal overflow when content width exceeds viewport width', () => {
      const xs = REPRESENTATIVE_VIEWPORTS.find(v => v.id === 'xs-mobile')!;
      const res = WebExperienceInspector.inspectVisualLayout(xs, 380, true, false);
      expect(res.passed).toBe(false);
      expect(res.horizontalOverflow).toBe(true);
      expect(res.defects.some(d => d.includes('سرریز افقی'))).toBe(true);
    });

    it('should enforce brand logo preservation (RULE-BRAND-001/005)', () => {
      const std = REPRESENTATIVE_VIEWPORTS.find(v => v.id === 'std-mobile')!;
      // Logo missing
      const res1 = WebExperienceInspector.inspectVisualLayout(std, 360, false, false);
      expect(res1.passed).toBe(false);
      expect(res1.defects.some(d => d.includes('RULE-BRAND-005'))).toBe(true);

      // Logo distorted
      const res2 = WebExperienceInspector.inspectVisualLayout(std, 360, true, true);
      expect(res2.passed).toBe(false);
      expect(res2.defects.some(d => d.includes('RULE-BRAND-002'))).toBe(true);
    });
  });

  describe('Work Package 4.14 & 4.15: Text QA (TQAA) & Arabic/Persian RTL Specialization', () => {
    it('should detect developer placeholders and variable leaks in text', () => {
      const res = WebExperienceInspector.inspectTextQuality('fa', 'خوش آمدید {{userName}} به پنل معاملاتی', 'rtl');
      expect(res.passed).toBe(false);
      expect(res.developerPlaceholdersPresent).toBe(true);
    });

    it('should detect improper Latin question marks in RTL Persian/Arabic content', () => {
      const res = WebExperienceInspector.inspectTextQuality('fa', 'آیا مایل به فعال‌سازی سیگنال هستید?', 'rtl');
      expect(res.passed).toBe(false);
      expect(res.issues.some(i => i.includes('علامت سوال لاتین'))).toBe(true);
    });

    it('should pass cleanly formatted RTL Persian risk disclosure line', () => {
      const res = WebExperienceInspector.inspectTextQuality(
        'fa',
        'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
        'rtl'
      );
      expect(res.passed).toBe(true);
      expect(res.issues.length).toBe(0);
    });
  });

  describe('Work Package 4.16: Dual QA Gate (Visual + Text Dual Gate)', () => {
    it('should reject release when Visual QA passes but Text QA fails', () => {
      const gate = ControlCenterService.evaluateDualQaGate('build-01', 'PASS', 'FAIL', 'PASS');
      expect(gate.overallGateResult).toBe('FAIL');
      expect(gate.reasons.some(r => r.includes('Text QA Failed'))).toBe(true);
    });

    it('should reject release when Visual QA fails but Text QA passes', () => {
      const gate = ControlCenterService.evaluateDualQaGate('build-02', 'FAIL', 'PASS', 'PASS');
      expect(gate.overallGateResult).toBe('FAIL');
      expect(gate.reasons.some(r => r.includes('Visual QA Failed'))).toBe(true);
    });

    it('should approve release when both Visual QA, Text QA, and Responsive QA pass', () => {
      const gate = ControlCenterService.evaluateDualQaGate('build-03', 'PASS', 'PASS', 'PASS');
      expect(gate.overallGateResult).toBe('PASS');
      expect(gate.reasons.length).toBe(0);
    });
  });
});
