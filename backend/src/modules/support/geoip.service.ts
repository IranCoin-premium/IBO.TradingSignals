/**
 * Geo-IP and Automatic Language Detection Service
 * Part 07: Localization and Initial Language Onboarding
 */

export interface GeoDetectionResult {
  ip: string;
  countryCode: string;
  suggestedLanguage: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';
  direction: 'rtl' | 'ltr';
  confidence: number;
}

export class GeoIpService {
  /**
   * Country code to default language mapping for the 6 target markets
   */
  private static readonly COUNTRY_TO_LANG_MAP: Record<string, 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru'> = {
    // Persian
    IR: 'fa',
    AF: 'fa',
    TJ: 'fa',

    // Arabic (GCC & MENA with high Islamic binary options demand)
    SA: 'ar',
    AE: 'ar',
    QA: 'ar',
    KW: 'ar',
    OM: 'ar',
    BH: 'ar',
    EG: 'ar',
    IQ: 'ar',
    JO: 'ar',
    LB: 'ar',

    // Hindi (India - Dominant Asia-Pacific retail volume share)
    IN: 'hi',

    // Turkish (Turkey - Top derivative trading market)
    TR: 'tr',
    AZ: 'tr',

    // Russian (Russia & CIS - Major Quotex/PocketOption volume)
    RU: 'ru',
    BY: 'ru',
    KZ: 'ru',
    UZ: 'ru',
    AM: 'ru',
    KG: 'ru'
  };

  /**
   * Detect client country and suggested language from IP header
   */
  public static detectLanguageFromIp(clientIp: string, customHeaderCountry?: string): GeoDetectionResult {
    const country = (customHeaderCountry || this.mockResolveIpCountry(clientIp)).toUpperCase();
    const suggestedLanguage = this.COUNTRY_TO_LANG_MAP[country] || 'en';
    const direction = (suggestedLanguage === 'fa' || suggestedLanguage === 'ar') ? 'rtl' : 'ltr';

    return {
      ip: clientIp || '127.0.0.1',
      countryCode: country,
      suggestedLanguage,
      direction,
      confidence: country === 'US' || country === 'UNKNOWN' ? 0.7 : 0.95
    };
  }

  /**
   * Safe country resolver (supports Cloudflare CF-IPCountry or internal ranges)
   */
  private static mockResolveIpCountry(ip: string): string {
    if (!ip || ip === '127.0.0.1' || ip === '::1') {
      return 'IR'; // Default baseline for IBO project
    }
    // Simple heuristic for simulation & local testing
    if (ip.startsWith('185.') || ip.startsWith('5.') || ip.startsWith('2.176.')) return 'IR';
    if (ip.startsWith('212.175.') || ip.startsWith('88.255.')) return 'TR';
    if (ip.startsWith('95.24.') || ip.startsWith('178.62.')) return 'RU';
    if (ip.startsWith('103.') || ip.startsWith('117.')) return 'IN';
    if (ip.startsWith('94.200.') || ip.startsWith('86.96.')) return 'AE';
    return 'US';
  }
}
