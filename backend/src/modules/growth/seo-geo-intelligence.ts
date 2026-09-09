/**
 * IBO Ecosystem — SEO Intelligence & Generative Search Optimization (GEO)
 * Part 03: Work Packages 3.5 & 3.6
 * 
 * Rules:
 * - RULE-SEO-001 to RULE-SEO-010: No keyword stuffing, no thin doorway pages, no #1 ranking promises.
 * - RULE-GEO-001 to RULE-GEO-009: GEO is NOT an AI trick; no mass low-value AI spam.
 * - Explicit separation between VERIFIED_TECHNICAL_ISSUE, SEO_HYPOTHESIS, and CONTENT_OPPORTUNITY.
 */

export type SEOFindingType =
  | 'VERIFIED_TECHNICAL_ISSUE'
  | 'SEO_HYPOTHESIS'
  | 'CONTENT_OPPORTUNITY'
  | 'GEO_FACTUAL_CLARITY_GAP';

export interface SEOFinding {
  id: string;
  type: SEOFindingType;
  title: string;
  description: string;
  affectedPath: string;
  recommendedAction: string;
  isSpamRisk: boolean;
  usefulnessScore: number; // 0.0 to 1.0 (Must be >= 0.7 to proceed)
  confidence: number;
}

export class SEOIntelligenceEngine {
  /**
   * Evaluates an SEO or Content proposal against strict anti-spam & GEO invariants
   */
  public static evaluateProposal(finding: SEOFinding): { approved: boolean; rejectionReasons: string[] } {
    const rejectionReasons: string[] = [];

    // Rule 1 & 4: Reject mass generation or promises of rank 1
    const textCorpus = `${finding.title} ${finding.description} ${finding.recommendedAction}`.toLowerCase();
    if (/mass[- ]produce|mass[- ]generate|scale[- ]thousands|doorway[- ]pages/.test(textCorpus)) {
      rejectionReasons.push('RULE-SEO-002 VIOLATION: Mass doorway or thin pages are strictly prohibited.');
    }

    if (/guarantee.*#1|promise.*top 1|rank 1 guaranteed/.test(textCorpus)) {
      rejectionReasons.push('RULE-SEO-004 VIOLATION: Never promise or guarantee #1 search engine ranking.');
    }

    if (/trick ai|manipulate llm|keyword stuffing|hidden text/.test(textCorpus)) {
      rejectionReasons.push('RULE-GEO-002 VIOLATION: Generative AI manipulation tactics are strictly prohibited.');
    }

    // Rule 6: Usefulness score threshold
    if (finding.usefulnessScore < 0.70) {
      rejectionReasons.push(`RULE-SEO-006 VIOLATION: Proposal usefulness score (${finding.usefulnessScore}) is below minimum threshold (0.70).`);
    }

    if (finding.isSpamRisk) {
      rejectionReasons.push('SECURITY & QUALITY VIOLATION: Flagged as potential search engine spam.');
    }

    return {
      approved: rejectionReasons.length === 0,
      rejectionReasons
    };
  }

  /**
   * Generates a verified technical sitemap / canonical audit check
   */
  public static auditTechnicalConsistency(
    sitemapUrls: string[],
    canonicalMap: Record<string, string>,
    supportedLangs: string[]
  ): { verifiedIssues: string[]; clean: boolean } {
    const issues: string[] = [];

    for (const url of sitemapUrls) {
      if (!canonicalMap[url]) {
        issues.push(`VERIFIED_TECHNICAL_ISSUE: Missing canonical mapping for sitemap URL: ${url}`);
      }
    }

    return {
      verifiedIssues: issues,
      clean: issues.length === 0
    };
  }
}
