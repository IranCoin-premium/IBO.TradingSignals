import { SUPPORTED_LANGUAGES_REGISTRY } from '../modules/growth/locale-registry';
import { ContextResolver, UserContextInput } from '../modules/growth/context-resolver';
import { MARKET_PROFILES_REGISTRY, MarketProfileValidator } from '../modules/growth/market-profile';
import { SEOIntelligenceEngine, SEOFinding } from '../modules/growth/seo-geo-intelligence';
import { LocalizationIntelligenceAgent } from '../modules/growth/localization-agent';
import { PaymentReadinessAgent } from '../modules/growth/payment-readiness';
import { MCPBoundaryGuard } from '../modules/growth/mcp-boundary-guard';

describe('Part 03 — Global Growth Intelligence, International SEO, GEO & Localization', () => {
  describe('Work Package 3.1 & 3.2: Multilingual & Locale Separation', () => {
    it('should distinguish language from country (English != UK, Arabic != monolithic)', () => {
      const en = SUPPORTED_LANGUAGES_REGISTRY['en'];
      expect(en).toBeDefined();
      expect(en.recognizedLocales).toContain('en-US');
      expect(en.recognizedLocales).toContain('en-CA');
      expect(en.recognizedLocales).toContain('en-AU');
      expect(en.recognizedLocales).toContain('en-GB');

      const ar = SUPPORTED_LANGUAGES_REGISTRY['ar'];
      expect(ar).toBeDefined();
      expect(ar.defaultLocale).toBe('ar-001'); // Modern Standard Arabic neutral baseline
      expect(ar.recognizedLocales).toContain('ar-SA');
      expect(ar.recognizedLocales).toContain('ar-AE');
      expect(ar.recognizedLocales).toContain('ar-EG');
    });
  });

  describe('Work Package 3.3: Context Resolution Engine & Priority', () => {
    it('should give absolute priority to explicit user language choice over network inference', () => {
      const input: UserContextInput = {
        explicitLanguage: 'fa',
        clientHeaderLocale: 'en-US,en;q=0.9',
        networkDetectedCountry: 'US'
      };

      const resolved = ContextResolver.resolve(input);
      expect(resolved.effectiveLanguage).toBe('fa');
      expect(resolved.resolutionSource).toBe('EXPLICIT_USER_SELECTION');
      expect(resolved.confidence).toBe(1.0);
    });

    it('should resolve English user from Canada to en-CA without forcing UK', () => {
      const input: UserContextInput = {
        clientHeaderLocale: 'en-CA,en;q=0.9',
        networkDetectedCountry: 'CA'
      };

      const resolved = ContextResolver.resolve(input);
      expect(resolved.effectiveLanguage).toBe('en');
      expect(resolved.effectiveLocale).toBe('en-CA');
      expect(resolved.resolutionSource).toBe('BROWSER_DEVICE_LOCALE');
    });
  });

  describe('Work Package 3.4: Market Profile & Promotion Guards', () => {
    it('should reject promoting an unverified market to VERIFIED_SUPPORTED without evidence', () => {
      const result = MarketProfileValidator.validateStatusPromotion(
        { marketCode: 'AU', paymentMethodsAvailable: ['USDT_TRC20'] },
        'VERIFIED_SUPPORTED',
        'too short'
      );

      expect(result.allowed).toBe(false);
      expect(result.reason).toContain('PROMOTION REJECTED');
    });

    it('should maintain Australia as RESEARCH_REQUIRED in registry due to regulatory research', () => {
      const au = MARKET_PROFILES_REGISTRY['AU'];
      expect(au.marketStatus).toBe('RESEARCH_REQUIRED');
      expect(au.productAvailability.signalsFeed).toBe(false);
    });
  });

  describe('Work Package 3.5 & 3.6: SEO & Generative Search (GEO) Anti-Spam Guards', () => {
    it('should reject mass doorway page generation proposals', () => {
      const proposal: SEOFinding = {
        id: 'seo-spam-001',
        type: 'CONTENT_OPPORTUNITY',
        title: 'Mass generate 500 country pages',
        description: 'Auto-scale doorway pages for every city and country',
        affectedPath: '/geo-pages',
        recommendedAction: 'mass-produce doorway pages to trick search engines',
        isSpamRisk: true,
        usefulnessScore: 0.2,
        confidence: 0.9
      };

      const evalResult = SEOIntelligenceEngine.evaluateProposal(proposal);
      expect(evalResult.approved).toBe(false);
      expect(evalResult.rejectionReasons.some(r => r.includes('RULE-SEO-002'))).toBe(true);
      expect(evalResult.rejectionReasons.some(r => r.includes('RULE-SEO-006'))).toBe(true);
    });

    it('should reject #1 ranking guarantees', () => {
      const proposal: SEOFinding = {
        id: 'seo-false-002',
        type: 'SEO_HYPOTHESIS',
        title: 'Guaranteed #1 ranking trick',
        description: 'Promise top 1 rank in 3 days',
        affectedPath: '/home',
        recommendedAction: 'Use secret trick to guarantee #1 rank',
        isSpamRisk: false,
        usefulnessScore: 0.8,
        confidence: 0.9
      };

      const evalResult = SEOIntelligenceEngine.evaluateProposal(proposal);
      expect(evalResult.approved).toBe(false);
      expect(evalResult.rejectionReasons.some(r => r.includes('RULE-SEO-004'))).toBe(true);
    });
  });

  describe('Work Package 3.7 & 3.8: Localization Intelligence & Arabic Specialization', () => {
    it('should not force regional override for standard English when shared base is adequate', () => {
      const res = LocalizationIntelligenceAgent.evaluateRegionalOverride(
        'en',
        'en-CA',
        'Welcome to our financial signals platform',
        'Welcome to our financial signals platform'
      );
      expect(res.needsOverride).toBe(false);
    });

    it('should approve regional override in Arabic only when legitimate regulatory difference exists', () => {
      const res = LocalizationIntelligenceAgent.evaluateRegionalOverride(
        'ar',
        'ar-SA',
        'إشارات باینری آپشن',
        'إشارات باینری آپشن خاضعة لإرشادات هيئة السوق المالية'
      );
      expect(res.needsOverride).toBe(true);
      expect(res.justification).toContain('Saudi CMA');
    });
  });

  describe('Work Package 3.10 & 3.11: Payment Readiness & 90th Minute Human Handoff', () => {
    it('should require official HTTPS source for payment research', () => {
      const res = PaymentReadinessAgent.assessPaymentGateway('AU', 'LocalPay', 'http://unverified-blog.com', false);
      expect(res.assessment.status).toBe('RESEARCH');
      expect(res.assessment.blockerDescription).toContain('RULE-PAYMENT-001');
    });

    it('should stop at the KYC/Identity boundary and produce a Persian Human Action Package without leaking secrets', () => {
      const res = PaymentReadinessAgent.assessPaymentGateway('AE', 'NowPayments', 'https://nowpayments.io/docs', true);
      expect(res.assessment.status).toBe('HUMAN_ACTION_REQUIRED');
      expect(res.humanActionPackage).toBeDefined();

      const hap = res.humanActionPackage!;
      expect(hap.persianInstructions.stepTitle).toContain('تکمیل فعال‌سازی درگاه');
      expect(hap.persianInstructions.securityWarning).toContain('تحت هیچ شرایطی');
      expect(hap.persianInstructions.forbiddenToShareWithAi).toContain('کلمات بازیابی کیف پول (12/24 Secret Words)');
      expect(hap.notificationMetadata.readyForDispatch).toBe(true);
    });
  });

  describe('Work Package 3.12 & 3.13: MCP Tool Boundaries & Collaboration Pipeline', () => {
    it('should reject an agent invoking tools outside its permitted role', () => {
      // SEO Agent attempting to dispatch a human payment package
      const auth = MCPBoundaryGuard.authorizeToolInvocation('SEO_AGENT', 'DISPATCH_HUMAN_PACKAGE');
      expect(auth.authorized).toBe(false);
      expect(auth.reason).toContain('RULE-MCP-008 VIOLATION');
    });

    it('should allow legitimate tool invocation within role boundary', () => {
      const auth = MCPBoundaryGuard.authorizeToolInvocation('SEO_AGENT', 'SEARCH_RESEARCH');
      expect(auth.authorized).toBe(true);
    });

    it('should generate an auditable record for cross-agent collaboration handoffs', () => {
      const handoff = MCPBoundaryGuard.recordHandoff({
        traceId: 'trace-collab-101',
        initiatingAgent: 'SEO_AGENT',
        receivingAgent: 'MARKET_INTELLIGENCE_AGENT',
        stage: 'SEO_DEMAND',
        payloadSummary: 'Identified growing demand for Persian/Arabic options signals in GCC region',
        timestamp: new Date().toISOString(),
        verified: true
      });

      expect(handoff.success).toBe(true);
      expect(handoff.logEntry).toContain('[COLLABORATION_TRACE][trace-collab-101]');
    });
  });
});
