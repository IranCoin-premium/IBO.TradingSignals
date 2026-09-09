import { KnowledgeEngine, KnowledgeItem, ConflictRecord } from '../modules/admin/knowledge-engine.service';
import { SUPPORTED_LANGUAGES_REGISTRY } from '../modules/growth/locale-registry';
import { MARKET_PROFILES_REGISTRY } from '../modules/growth/market-profile';
import { SEOIntelligenceEngine, SEOFinding } from '../modules/growth/seo-geo-intelligence';
import { PaymentReadinessAgent } from '../modules/growth/payment-readiness';

describe('Part 06 — AI Knowledge, RAG, Memory, Skills, MCP Governance & Research Intelligence', () => {
  describe('Work Package 6.1 & 6.2: Knowledge Layers & Baseline Audit', () => {
    it('should have Layer A (Project Constitution) seeded and immutable', () => {
      const { results } = KnowledgeEngine.retrieveKnowledge({
        agentRole: 'ANY',
        query: 'CONST-001'
      });
      expect(results.length).toBe(1);
      expect(results[0].layer).toBe('LAYER_A_CONSTITUTION');
      expect(results[0].content).toContain('این سیگنال‌ها توصیه مالی نیستند');

      // Attempt to overwrite Layer A constitution - must fail
      const overwriteAttempt = KnowledgeEngine.ingestKnowledge({
        knowledgeId: 'CONST-001',
        layer: 'LAYER_A_CONSTITUTION',
        type: 'POLICY',
        title: 'Tampered Constitution',
        domain: 'LEGAL',
        sourceType: 'TAMPERED',
        sourceReference: 'ATTACK',
        sourceAuthority: 'UNVERIFIED',
        owner: 'ATTACKER',
        createdAt: new Date().toISOString(),
        version: '2.0.0',
        language: 'fa',
        confidence: 0.1,
        verificationStatus: 'UNVERIFIED_SOURCE',
        sensitivity: 'PUBLIC',
        allowedRoles: ['*'],
        summary: 'تغییر غیرمجاز',
        content: 'متن غیرقانونی',
        evidence: [],
        tags: []
      });

      expect(overwriteAttempt.success).toBe(false);
      expect(overwriteAttempt.reason).toContain('Layer A (Project Constitution) is immutable');
    });
  });

  describe('Work Package 6.3 & 6.4: Controlled Ingestion & Redaction', () => {
    it('should scrub synthetic secrets from ingested knowledge content and evidence', () => {
      const ingestion = KnowledgeEngine.ingestKnowledge({
        knowledgeId: 'DOC-ENV-01',
        layer: 'LAYER_B_DOCUMENTATION',
        type: 'RUNBOOK',
        title: 'Production Staging Deploy Guide',
        domain: 'DEVOPS',
        sourceType: 'INTERNAL_DOC',
        sourceReference: 'RUNBOOK-01',
        sourceAuthority: 'OFFICIAL_PRIMARY',
        owner: 'DEVOPS_LEAD',
        createdAt: new Date().toISOString(),
        version: '1.0.0',
        language: 'en',
        confidence: 0.95,
        verificationStatus: 'VERIFIED_FACT',
        sensitivity: 'INTERNAL',
        allowedRoles: ['DEVOPS', 'ADMIN'],
        summary: 'Runbook with credential handling',
        content: 'Use token FAKE_test_token_redacted_12345 to connect',
        evidence: ['Connecting via FAKE_test_evidence_redacted_67890'],
        tags: ['deploy', 'runbook']
      });

      expect(ingestion.success).toBe(true);
      expect(ingestion.item?.content).not.toContain('FAKE_test_token_redacted_12345');
      expect(ingestion.item?.content).toContain('[REDACTED_SECRET]');
      expect(ingestion.item?.evidence[0]).toContain('[REDACTED_SECRET]');
    });

    it('should reject duplicate document versions', () => {
      const item = {
        knowledgeId: 'DOC-DUP-01',
        layer: 'LAYER_B_DOCUMENTATION' as const,
        type: 'API_DOCUMENTATION' as const,
        title: 'Duplicate Testing Spec',
        domain: 'API',
        sourceType: 'SPEC',
        sourceReference: 'API-SPEC-V1',
        sourceAuthority: 'OFFICIAL_PRIMARY' as const,
        owner: 'CORE_TEAM',
        createdAt: new Date().toISOString(),
        version: '1.0.0',
        language: 'en',
        confidence: 0.9,
        verificationStatus: 'VERIFIED_FACT' as const,
        sensitivity: 'INTERNAL' as const,
        allowedRoles: ['*'],
        summary: 'Testing Duplicate',
        content: 'Identical stable API spec content.',
        evidence: [],
        tags: ['spec']
      };

      const first = KnowledgeEngine.ingestKnowledge(item);
      expect(first.success).toBe(true);

      const second = KnowledgeEngine.ingestKnowledge(item);
      expect(second.success).toBe(false);
      expect(second.reason).toContain('DUPLICATE_DOCUMENT');
    });
  });

  describe('Work Package 6.6, 6.7, 6.8: Hybrid Retrieval & Access Control', () => {
    beforeAll(() => {
      KnowledgeEngine.ingestKnowledge({
        knowledgeId: 'DOC-SECRET-PAYMENT-01',
        layer: 'LAYER_B_DOCUMENTATION',
        type: 'PAYMENT',
        title: 'Confidential Merchant Banking Credentials Protocol',
        domain: 'FINANCE',
        sourceType: 'INTERNAL_PROTOCOL',
        sourceReference: 'BANK-01',
        sourceAuthority: 'OFFICIAL_PRIMARY',
        owner: 'CFO',
        createdAt: new Date().toISOString(),
        version: '1.0.0',
        language: 'en',
        confidence: 1.0,
        verificationStatus: 'VERIFIED_FACT',
        sensitivity: 'CONFIDENTIAL',
        allowedRoles: ['FINANCE_DIRECTOR', 'SUPER_ADMIN'],
        summary: 'Restricted banking flow',
        content: 'Confidential protocol details',
        evidence: [],
        tags: ['banking', 'secret']
      });

      KnowledgeEngine.ingestKnowledge({
        knowledgeId: 'DOC-SAUDI-PAYMENT-01',
        layer: 'LAYER_E_MARKET_LOCALIZATION',
        type: 'PAYMENT',
        title: 'Saudi Arabia Mada & Apple Pay Market Requirements',
        domain: 'PAYMENT',
        sourceType: 'OFFICIAL_PROVIDER',
        sourceReference: 'SAMA-DOC-2026',
        sourceAuthority: 'REGULATORY',
        owner: 'PAYMENT_RESEARCH',
        createdAt: new Date().toISOString(),
        version: '1.0.0',
        language: 'ar',
        locale: 'ar-SA',
        countryScope: 'SA',
        confidence: 0.98,
        verificationStatus: 'VERIFIED_FACT',
        sensitivity: 'PUBLIC',
        allowedRoles: ['*'],
        summary: 'Mada integration requirements for Saudi Arabia',
        content: 'متطلبات بوابة مدى والمدفوعات الإلكترونية في المملكة العربية السعودية',
        evidence: ['SAMA Regulatory Circular 2026'],
        tags: ['saudi', 'mada', 'payments']
      });
    });

    it('should deny unauthorized agent from accessing confidential documents', () => {
      const retrieval = KnowledgeEngine.retrieveKnowledge({
        agentRole: 'GENERAL_RESEARCH_WORKER',
        query: 'Confidential Merchant Banking'
      });

      expect(retrieval.results.length).toBe(0);
      expect(retrieval.deniedCount).toBeGreaterThan(0);
      expect(retrieval.auditRecorded).toBe(true);
    });

    it('should accurately filter by countryScope and locale in hybrid retrieval', () => {
      const retrieval = KnowledgeEngine.retrieveKnowledge({
        agentRole: 'LIA',
        query: 'mada',
        countryScope: 'SA'
      });

      expect(retrieval.results.length).toBe(1);
      expect(retrieval.results[0].countryScope).toBe('SA');
      expect(retrieval.results[0].locale).toBe('ar-SA');
    });
  });

  describe('Work Package 6.11: Conflict Detection', () => {
    it('should register and expose conflicting claims with distinct sources and authorities', () => {
      const conflict: ConflictRecord = {
        conflictId: 'CONF-01',
        domain: 'PAYMENT_LIMITS',
        topic: 'TRC20 USDT Withdrawal Minimum',
        claimA: {
          claimText: 'Minimum withdrawal is 10 USDT',
          source: 'Internal Support FAQ v1',
          date: '2026-01-10',
          authority: 'SECONDARY_ANALYSIS',
          scope: 'GLOBAL'
        },
        claimB: {
          claimText: 'Minimum withdrawal is 25 USDT due to energy fees',
          source: 'Gateway Direct API Spec v3',
          date: '2026-08-15',
          authority: 'OFFICIAL_PRIMARY',
          scope: 'GLOBAL'
        },
        detectedAt: new Date().toISOString(),
        status: 'CONFLICT_DETECTED'
      };

      KnowledgeEngine.registerConflict(conflict);
      const conflicts = KnowledgeEngine.getConflicts();
      const found = conflicts.find(c => c.conflictId === 'CONF-01');

      expect(found).toBeDefined();
      expect(found?.status).toBe('CONFLICT_DETECTED');
      expect(found?.claimA.authority).toBe('SECONDARY_ANALYSIS');
      expect(found?.claimB.authority).toBe('OFFICIAL_PRIMARY');
    });
  });

  describe('Work Package 6.12: Freshness & Expiration', () => {
    it('should filter out expired knowledge when requireFresh is requested', () => {
      KnowledgeEngine.ingestKnowledge({
        knowledgeId: 'DOC-PROMO-EXPIRED',
        layer: 'LAYER_C_OPERATIONAL',
        type: 'RUNBOOK',
        title: 'Expired New Year Discount Code',
        domain: 'GROWTH',
        sourceType: 'CAMPAIGN',
        sourceReference: 'CAMP-2025',
        sourceAuthority: 'INTERNAL_CONSTITUTION',
        owner: 'MARKETING',
        createdAt: '2025-01-01T00:00:00Z',
        validUntil: '2025-01-15T00:00:00Z',
        version: '1.0.0',
        language: 'fa',
        confidence: 0.9,
        verificationStatus: 'VERIFIED_FACT',
        sensitivity: 'PUBLIC',
        allowedRoles: ['*'],
        summary: 'تخفیف منقضی‌شده',
        content: 'کد تخفیف قدیمی',
        evidence: [],
        tags: ['expired_promo']
      });

      const freshRetrieval = KnowledgeEngine.retrieveKnowledge({
        agentRole: 'ANY',
        query: 'Expired New Year Discount Code',
        requireFresh: true
      });

      expect(freshRetrieval.results.length).toBe(0);
    });
  });

  describe('Work Package 6.13, 6.15 & 6.16: Skills Catalog & MCP Permissions', () => {
    it('should expose tested skills catalog and enforce MCP permission boundary', () => {
      const skills = KnowledgeEngine.getSkillsCatalog();
      expect(skills.some(s => s.skillId === 'SKILL-OFFICIAL-RESEARCH-V1')).toBe(true);
      expect(skills.find(s => s.skillId === 'SKILL-OFFICIAL-RESEARCH-V1')?.testStatus).toBe('PASSED');

      // Test MCP permission guard
      const allowCheck = KnowledgeEngine.authorizeMCPTool('mcp-core-governance', 'CAC', 'ISSUE_TRACKER');
      expect(allowCheck.authorized).toBe(true);

      const hardDenyCheck = KnowledgeEngine.authorizeMCPTool('mcp-core-governance', 'CAC', 'LIVE_TRADING_EXECUTE');
      expect(hardDenyCheck.authorized).toBe(false);
      expect(hardDenyCheck.reason).toContain('hard-denied by MCP security policy');
    });
  });

  describe('Work Package 6.14, 6.22, 6.23: Research Intelligence, Stopping & Anti-Hallucination', () => {
    it('should halt with INSUFFICIENT_EVIDENCE on unverified or fictional queries', () => {
      const res = KnowledgeEngine.executeOfficialResearch('unsupported_fictional_query', 'MARS', 3);
      expect(res.stoppingReason).toBe('INSUFFICIENT_EVIDENCE');
      expect(res.status).toBe('STOPPED');
      expect(res.confidence).toBe(0.0);
      expect(res.recommendation).toContain('Anti-Hallucination Guard');
    });

    it('should halt at the 90th minute Human Action Boundary on bank/KYC operations', () => {
      const res = KnowledgeEngine.executeOfficialResearch('افتتاح حساب بانکی و احراز هویت تاجر در درگاه', 'AE', 5);
      expect(res.stoppingReason).toBe('HUMAN_ACTION_REQUIRED');
      expect(res.humanActionRequired).toBe(true);
      expect(res.recommendation).toContain('بسته اقدام انسانی');
    });
  });

  describe('Work Package 6.17 - 6.21: Localization, SEO & GEO Knowledge Guards', () => {
    it('should preserve language != country in registry (English != UK, Arabic != monolithic)', () => {
      expect(SUPPORTED_LANGUAGES_REGISTRY.en.recognizedLocales).toContain('en-CA');
      expect(SUPPORTED_LANGUAGES_REGISTRY.en.recognizedLocales).toContain('en-AU');
      expect(SUPPORTED_LANGUAGES_REGISTRY.ar.recognizedLocales).toContain('ar-SA');
      expect(SUPPORTED_LANGUAGES_REGISTRY.ar.recognizedLocales).toContain('ar-AE');
    });

    it('should reject SEO/GEO spam claiming #1 guaranteed ranking or thin doorway pages', () => {
      const spamProposal: SEOFinding = {
        id: 'seo-spam-01',
        type: 'CONTENT_OPPORTUNITY',
        title: 'Guarantee #1 rank with mass doorway pages',
        description: 'scale thousands of doorway pages to trick AI and guarantee #1 on google',
        affectedPath: '/seo/doorway',
        recommendedAction: 'mass-produce pages',
        isSpamRisk: true,
        usefulnessScore: 0.3,
        confidence: 0.5
      };

      const evalResult = SEOIntelligenceEngine.evaluateProposal(spamProposal);
      expect(evalResult.approved).toBe(false);
      expect(evalResult.rejectionReasons.some(r => r.includes('RULE-SEO-002'))).toBe(true);
      expect(evalResult.rejectionReasons.some(r => r.includes('RULE-SEO-004'))).toBe(true);
    });
  });

  describe('Work Package 6.25: Architecture Decision Records (ADRs)', () => {
    it('should track accepted ADRs for architecture governance', () => {
      const adrs = KnowledgeEngine.getADRs();
      expect(adrs.some(a => a.decisionId === 'ADR-001')).toBe(true);
      expect(adrs.find(a => a.decisionId === 'ADR-001')?.status).toBe('ACCEPTED');
    });
  });

  describe('Work Package 6.28: Observability Dashboard', () => {
    it('should return complete knowledge observability metrics', () => {
      const overview = KnowledgeEngine.getKnowledgeObservabilityOverview();
      expect(overview.totalDocuments).toBeGreaterThanOrEqual(2);
      expect(overview.totalSkills).toBeGreaterThanOrEqual(1);
      expect(overview.totalMCPServers).toBeGreaterThanOrEqual(1);
      expect(overview.totalADRs).toBeGreaterThanOrEqual(1);
      expect(overview.retrievalAuditsCount).toBeGreaterThanOrEqual(1);
    });
  });
});
