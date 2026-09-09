/**
 * IBO Ecosystem — Market Profile Standard & Discovery Model
 * Part 03: Work Packages 3.4, 3.8 & 3.9
 * 
 * Rules:
 * 1. Markets must NEVER be marked 'VERIFIED_SUPPORTED' without concrete evidence.
 * 2. Arabic markets are specialized with individual profiles (ar-SA, ar-AE, ar-EG) rather than treated as a monolith.
 * 3. English != UK: Australia, Canada, USA are separate market profiles.
 */

export type MarketStatus =
  | 'VERIFIED_SUPPORTED'
  | 'RESEARCH_REQUIRED'
  | 'RESTRICTED_OR_BLOCKED'
  | 'NOT_AVAILABLE';

export interface MarketProfile {
  marketCode: string;          // ISO 3166-1 alpha-2 (e.g. 'IR', 'AE', 'TR', 'AU')
  marketName: string;          // Common English name
  primaryLanguage: string;     // ISO 639-1
  supportedLocales: string[];  // BCP 47
  marketStatus: MarketStatus;
  evidenceStrength: 'CONFIRMED_OPERATIONAL' | 'HIGH_DEMAND_RESEARCH' | 'ANECDOTAL_OR_UNVERIFIED';
  paymentMethodsAvailable: string[];
  productAvailability: {
    signalsFeed: boolean;
    vipSubscription: boolean;
    salesPartnership: boolean;
    support247: boolean;
  };
  regulatoryNotes: string;
  mandatoryRiskDisclosureLine: string;
}

/**
 * Initial market profiles verified in the IBO architecture
 */
export const MARKET_PROFILES_REGISTRY: Record<string, MarketProfile> = {
  IR: {
    marketCode: 'IR',
    marketName: 'Iran',
    primaryLanguage: 'fa',
    supportedLocales: ['fa-IR'],
    marketStatus: 'VERIFIED_SUPPORTED',
    evidenceStrength: 'CONFIRMED_OPERATIONAL',
    paymentMethodsAvailable: ['USDT_TRC20', 'USDT_ERC20', 'USDT_TON', 'NOWPAYMENTS', 'MANUAL_CARD_TRANSFER'],
    productAvailability: {
      signalsFeed: true,
      vipSubscription: true,
      salesPartnership: true,
      support247: true
    },
    regulatoryNotes: 'Informational analysis & signals only. Zero direct custodial funds or broker executions.',
    mandatoryRiskDisclosureLine: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.'
  },
  TR: {
    marketCode: 'TR',
    marketName: 'Turkey',
    primaryLanguage: 'tr',
    supportedLocales: ['tr-TR'],
    marketStatus: 'VERIFIED_SUPPORTED',
    evidenceStrength: 'CONFIRMED_OPERATIONAL',
    paymentMethodsAvailable: ['USDT_TRC20', 'USDT_TON', 'CRYPTO'],
    productAvailability: {
      signalsFeed: true,
      vipSubscription: true,
      salesPartnership: true,
      support247: true
    },
    regulatoryNotes: 'Top 5 crypto & derivative adoption market. High mobile trading presence.',
    mandatoryRiskDisclosureLine: 'Bu sinyaller finansal tavsiye değildir; ikili opsiyon işlemleri sermaye kaybı yüksek riski taşır.'
  },
  AE: {
    marketCode: 'AE',
    marketName: 'United Arab Emirates',
    primaryLanguage: 'ar',
    supportedLocales: ['ar-AE', 'en-US'],
    marketStatus: 'VERIFIED_SUPPORTED',
    evidenceStrength: 'CONFIRMED_OPERATIONAL',
    paymentMethodsAvailable: ['USDT_TRC20', 'USDT_TON', 'CRYPTO'],
    productAvailability: {
      signalsFeed: true,
      vipSubscription: true,
      salesPartnership: true,
      support247: true
    },
    regulatoryNotes: 'High demand for Islamic swap-free binary option analysis and crypto settlement.',
    mandatoryRiskDisclosureLine: 'هذه الإشارات ليست نصيحة مالية؛ تداول الخيارات الثنائية ينطوي على مخاطر عالية لخسارة رأس المال.'
  },
  SA: {
    marketCode: 'SA',
    marketName: 'Saudi Arabia',
    primaryLanguage: 'ar',
    supportedLocales: ['ar-SA'],
    marketStatus: 'VERIFIED_SUPPORTED',
    evidenceStrength: 'CONFIRMED_OPERATIONAL',
    paymentMethodsAvailable: ['USDT_TRC20', 'CRYPTO'],
    productAvailability: {
      signalsFeed: true,
      vipSubscription: true,
      salesPartnership: true,
      support247: true
    },
    regulatoryNotes: 'Strict consumer disclosure requirements. Modern Standard Arabic + regional numeric conventions.',
    mandatoryRiskDisclosureLine: 'هذه الإشارات ليست نصيحة مالية؛ تداول الخيارات الثنائية ينطوي على مخاطر عالية لخسارة رأس المال.'
  },
  AU: {
    marketCode: 'AU',
    marketName: 'Australia',
    primaryLanguage: 'en',
    supportedLocales: ['en-AU'],
    marketStatus: 'RESEARCH_REQUIRED', // Example: English users appear from AU, but market is under research
    evidenceStrength: 'HIGH_DEMAND_RESEARCH',
    paymentMethodsAvailable: ['USDT_TRC20'],
    productAvailability: {
      signalsFeed: false, // Not yet turned on until regulatory and payment reviews pass
      vipSubscription: false,
      salesPartnership: false,
      support247: true
    },
    regulatoryNotes: 'ASIC regulations on binary options retail marketing require strict legal verification before full rollout.',
    mandatoryRiskDisclosureLine: 'These signals are not financial advice; binary options trading carries a high risk of capital loss.'
  }
};

export class MarketProfileValidator {
  /**
   * Prevents marking a market as VERIFIED_SUPPORTED without concrete evidence
   */
  public static validateStatusPromotion(
    currentProfile: Partial<MarketProfile>,
    targetStatus: MarketStatus,
    evidenceNote: string
  ): { allowed: boolean; reason?: string } {
    if (targetStatus === 'VERIFIED_SUPPORTED') {
      if (!evidenceNote || evidenceNote.trim().length < 20) {
        return {
          allowed: false,
          reason: 'PROMOTION REJECTED: A market cannot be marked VERIFIED_SUPPORTED without a documented, verified evidence citation.'
        };
      }
      if (!currentProfile.paymentMethodsAvailable || currentProfile.paymentMethodsAvailable.length === 0) {
        return {
          allowed: false,
          reason: 'PROMOTION REJECTED: Market has no verified payment settlement methods configured.'
        };
      }
      if (!currentProfile.mandatoryRiskDisclosureLine || currentProfile.mandatoryRiskDisclosureLine.trim().length < 10) {
        return {
          allowed: false,
          reason: 'PROMOTION REJECTED: Mandatory risk disclosure line is missing for this market.'
        };
      }
    }
    return { allowed: true };
  }
}
