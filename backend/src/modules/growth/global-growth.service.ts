/**
 * IBO Ecosystem — Global Growth Operating System & International SEO Engine
 * Master Prompt — Part 17: Work Packages 17.01 - 17.20
 */

import { SEOIntelligenceEngine, SEOFinding } from './seo-geo-intelligence';
import { LocalizationIntelligenceAgent } from './localization-agent';
import { SUPPORTED_LANGUAGES_REGISTRY } from './locale-registry';
import { ZeroTrustPolicyEngine } from '../../security/policy/policy-engine.service';
import { logger } from '../../utils/logger';

export interface GrowthContentOpportunity {
  opportunityId: string;
  keyword: string;
  category: 'SEO_HYPOTHESIS' | 'CONTENT_OPPORTUNITY' | 'GEO_FACTUAL_CLARITY_GAP';
  targetLocale: string;
  usefulnessScore: number;
  isSpamRisk: boolean;
  status: 'RESEARCHED' | 'GENERATED' | 'QA_PASSED' | 'STAGED' | 'PUBLISHED';
}

export interface LocalizedContentCampaign {
  campaignId: string;
  opportunityId: string;
  title: string;
  description: string;
  riskDisclosure: string;
  lang: string;
  locale: string;
  schemaJsonLd: Record<string, any>;
  trafficClicks: number;
  conversionCount: number;
  measuredCausalityStatus: 'ASSOCIATION_ONLY' | 'CAUSAL_PROOF_PENDING';
}

export interface GrowthInsightReport {
  insightId: string;
  domain: string;
  evidence: string;
  hypothesis: string;
  unresolvedUncertainties: string[];
  confidence: number;
  generatedAt: string;
}

export class GlobalGrowthEngineService {
  private static opportunities: Map<string, GrowthContentOpportunity> = new Map();
  private static campaigns: Map<string, LocalizedContentCampaign> = new Map();
  private static growthInsights: GrowthInsightReport[] = [];

  // ==========================================
  // Work Package 17.01 & 17.02: Ingest SEO Content Opportunities
  // ==========================================

  public static ingestOpportunity(opportunity: Omit<GrowthContentOpportunity, 'opportunityId' | 'status'>): {
    accepted: boolean;
    reason: string;
    opportunity?: GrowthContentOpportunity;
  } {
    const opportunityId = `opp-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;

    // Invariant: Delegate anti-spam & GEO checks to SEOIntelligenceEngine
    const evaluation = SEOIntelligenceEngine.evaluateProposal({
      id: opportunityId,
      type: opportunity.category,
      title: opportunity.keyword,
      description: `Opportunity target: ${opportunity.keyword}`,
      affectedPath: `/growth/${opportunity.targetLocale}/${opportunity.keyword}`,
      recommendedAction: 'Generate educational landing page',
      isSpamRisk: opportunity.isSpamRisk,
      usefulnessScore: opportunity.usefulnessScore,
      confidence: 0.9
    });

    if (!evaluation.approved) {
      return { accepted: false, reason: `Opportunity rejected by SEO Gate: ${evaluation.rejectionReasons.join(', ')}` };
    }

    const storedOpportunity: GrowthContentOpportunity = {
      ...opportunity,
      opportunityId,
      status: 'RESEARCHED'
    };

    this.opportunities.set(opportunityId, storedOpportunity);
    return { accepted: true, reason: 'Growth opportunity accepted', opportunity: storedOpportunity };
  }

  // ==========================================
  // Work Package 17.03 & 17.05: Programmatic Content Generation & Localization
  // ==========================================

  public static generateContent(opportunityId: string): {
    success: boolean;
    campaign?: LocalizedContentCampaign;
    error?: string;
  } {
    const opp = this.opportunities.get(opportunityId);
    if (!opp) return { success: false, error: 'Opportunity not found' };

    const lang = opp.targetLocale.split('-')[0];
    const campaignId = `cam-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`;

    // Invariant: Enforce mandatory localized risk disclosure
    const riskDisclosures: Record<string, string> = {
      fa: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
      en: 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.',
      ar: 'هذه الإشارات ليست نصيحة مالية؛ ينطوي تداول الخيارات الثنائية على مخاطر عالية لفقدان رأس المال.'
    };
    const riskDisclosure = riskDisclosures[lang] || riskDisclosures['en'];

    // Invariant: Regional localization override assessment
    const overrideEval = LocalizationIntelligenceAgent.evaluateRegionalOverride(
      lang,
      opp.targetLocale,
      `Shared ${lang} template content`,
      `Customized regional ${opp.targetLocale} content`
    );

    const title = overrideEval.needsOverride 
      ? `IBO Signals - Regional ${opp.targetLocale} Index`
      : `IBO Trading Signals - ${opp.keyword}`;

    const campaign: LocalizedContentCampaign = {
      campaignId,
      opportunityId,
      title,
      description: `Comprehensive educational guide about ${opp.keyword} in ${opp.targetLocale}.`,
      riskDisclosure,
      lang,
      locale: opp.targetLocale,
      schemaJsonLd: {
        '@context': 'https://schema.org',
        '@type': 'EducationalOccupationalCredential',
        name: title,
        description: `Learn to analyze trading signals safely.`
      },
      trafficClicks: 0,
      conversionCount: 0,
      measuredCausalityStatus: 'ASSOCIATION_ONLY'
    };

    opp.status = 'GENERATED';
    this.campaigns.set(campaignId, campaign);

    logger.info(`[GROWTH CONTENT] Generated campaign ${campaignId} for target ${opp.targetLocale}. Overrides applied: ${overrideEval.needsOverride}`);
    return { success: true, campaign };
  }

  // ==========================================
  // Work Package 17.07 & 17.11: Content QA, Staging, and Publishing
  // ==========================================

  public static qaAndPublishCampaign(campaignId: string): { success: boolean; error?: string } {
    const campaign = this.campaigns.get(campaignId);
    if (!campaign) return { success: false, error: 'Campaign not found' };

    const opp = this.opportunities.get(campaign.opportunityId);
    if (!opp) return { success: false, error: 'Associated opportunity not found' };

    // QA Check: Verify mandatory risk disclosure presence
    if (!campaign.riskDisclosure) {
      return { success: false, error: 'QA Rejected: Missing mandatory risk warning disclosure.' };
    }

    opp.status = 'PUBLISHED';
    logger.info(`[CAMPAIGN PUBLISHED] Campaign ${campaignId} successfully published. Loc: /growth/${campaign.locale}/${opp.keyword}`);
    return { success: true };
  }

  // ==========================================
  // Work Package 16.14: Product Insight Intelligence Generator (Growth Context)
  // ==========================================

  public static recordGrowthInsight(report: Omit<GrowthInsightReport, 'insightId' | 'generatedAt'>): GrowthInsightReport {
    const insight: GrowthInsightReport = {
      ...report,
      insightId: `gri-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
      generatedAt: new Date().toISOString()
    };

    this.growthInsights.push(insight);
    return insight;
  }

  public static listOpportunities(): GrowthContentOpportunity[] {
    return Array.from(this.opportunities.values());
  }

  public static listCampaigns(): LocalizedContentCampaign[] {
    return Array.from(this.campaigns.values());
  }

  public static listInsights(): GrowthInsightReport[] {
    return this.growthInsights;
  }
}
