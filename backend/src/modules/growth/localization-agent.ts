/**
 * IBO Ecosystem — Localization Intelligence Agent (LIA)
 * Part 03: Work Packages 3.7 & 3.8
 * 
 * Rules:
 * 1. Translation != Localization.
 * 2. Arabic Specialization: Do not treat all Arabic speakers identically (ar-SA vs ar-AE vs ar-EG).
 * 3. Base language content is shared; regional differences are handled via localized overrides only when justified.
 */

export interface LocalizationRecord {
  contentKey: string;
  baseLanguage: string;      // e.g. 'en', 'ar'
  locale: string;            // e.g. 'en-CA', 'ar-SA'
  market: string;            // e.g. 'CA', 'SA'
  baseText: string;
  localizedText: string;
  isOverrideJustified: boolean;
  justificationReason: string;
  culturalSensitivityChecked: boolean;
  numberFormatVerified: boolean;
  reviewStatus: 'DRAFT' | 'VERIFIED_BY_LIA' | 'APPROVED';
}

export class LocalizationIntelligenceAgent {
  /**
   * Evaluates if a regional override is justified or if the shared base language is sufficient.
   * Prevents unnecessary duplication or speculative dialect changes.
   */
  public static evaluateRegionalOverride(
    baseLang: string,
    targetLocale: string,
    baseText: string,
    proposedOverride: string
  ): { needsOverride: boolean; justification: string } {
    // English regional cases (e.g. en-US vs en-CA or en-AU)
    if (baseLang === 'en') {
      if (targetLocale === 'en-AU' || targetLocale === 'en-GB') {
        // Legitimate spelling differences or currency/time references
        if (/color|center|program|license/i.test(baseText)) {
          return {
            needsOverride: true,
            justification: 'Spelling or terminology variation identified for regional locale.'
          };
        }
      }
      return {
        needsOverride: false,
        justification: 'No significant regional vocabulary or formatting discrepancy detected; use shared English base.'
      };
    }

    // Arabic regional specialization (ar-001 neutral baseline vs ar-SA / ar-AE / ar-EG)
    if (baseLang === 'ar') {
      // In financial products, dialect is avoided in favor of Modern Standard Arabic (فصحى)
      // but regional legal warnings or regulatory terms may require override
      if (targetLocale === 'ar-SA' && /هيئة السوق المالية|CMA|ضريبة/i.test(proposedOverride)) {
        return {
          needsOverride: true,
          justification: 'Saudi CMA regulatory terminology requirement.'
        };
      }
      if (targetLocale === 'ar-AE' && /VARA|سلطة دبي لتنظيم الأصول الافتراضية/i.test(proposedOverride)) {
        return {
          needsOverride: true,
          justification: 'UAE VARA regulatory terminology requirement.'
        };
      }
      return {
        needsOverride: false,
        justification: 'Neutral Modern Standard Arabic (ar-001) is sufficient and preferred for universal clarity.'
      };
    }

    return {
      needsOverride: false,
      justification: 'Base language presentation satisfies locale requirements.'
    };
  }
}
