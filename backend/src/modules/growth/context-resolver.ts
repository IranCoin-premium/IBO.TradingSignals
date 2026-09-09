/**
 * IBO Ecosystem — Context Resolution Engine
 * Part 03: Work Package 3.3
 * 
 * Invariants:
 * 1. Explicit User Preference ALWAYS overrides weak network/IP inference.
 * 2. Privacy-first: no high-precision GPS coordinate tracking for simple locale presentation.
 * 3. Language is separated from Country and Market Eligibility.
 */

import { SUPPORTED_LANGUAGES_REGISTRY, LanguageDefinition } from './locale-registry';

export type ResolutionSource =
  | 'EXPLICIT_USER_SELECTION'
  | 'ACCOUNT_PREFERENCE'
  | 'BROWSER_DEVICE_LOCALE'
  | 'CONSENTED_REGION'
  | 'NETWORK_IP_INFERENCE'
  | 'DEFAULT_BASELINE';

export interface UserContextInput {
  explicitLanguage?: string;
  explicitCountry?: string;
  accountLanguage?: string;
  accountCountry?: string;
  clientHeaderLocale?: string; // e.g. "Accept-Language: en-CA,en;q=0.9"
  consentedRegion?: string;    // e.g. "CA"
  networkDetectedCountry?: string; // e.g. from CF-IPCountry
}

export interface ResolvedContext {
  effectiveLanguage: string;
  effectiveLocale: string;
  inferredCountry: string;
  direction: 'rtl' | 'ltr';
  resolutionSource: ResolutionSource;
  confidence: number;
  privacyCompliant: boolean;
  notes: string[];
}

export class ContextResolver {
  /**
   * Resolves the user context using strict priority:
   * Explicit User Preference > Account Preference > Browser/Device Locale > Consented Region > Network IP Inference > Baseline
   */
  public static resolve(input: UserContextInput): ResolvedContext {
    const notes: string[] = [];

    // 1. Explicit user selection (highest authority)
    if (input.explicitLanguage && SUPPORTED_LANGUAGES_REGISTRY[input.explicitLanguage.toLowerCase()]) {
      const lang = input.explicitLanguage.toLowerCase();
      const def = SUPPORTED_LANGUAGES_REGISTRY[lang];
      const country = (input.explicitCountry || input.accountCountry || input.consentedRegion || 'UNKNOWN').toUpperCase();
      const localeCandidate = `${lang}-${country}`;
      const effectiveLocale = def.recognizedLocales.includes(localeCandidate) ? localeCandidate : def.defaultLocale;

      return {
        effectiveLanguage: lang,
        effectiveLocale,
        inferredCountry: country,
        direction: def.direction,
        resolutionSource: 'EXPLICIT_USER_SELECTION',
        confidence: 1.0,
        privacyCompliant: true,
        notes: ['Resolved by explicit user selection. Overrode any network inferences.']
      };
    }

    // 2. Account level preferences
    if (input.accountLanguage && SUPPORTED_LANGUAGES_REGISTRY[input.accountLanguage.toLowerCase()]) {
      const lang = input.accountLanguage.toLowerCase();
      const def = SUPPORTED_LANGUAGES_REGISTRY[lang];
      const country = (input.accountCountry || input.consentedRegion || 'UNKNOWN').toUpperCase();
      const localeCandidate = `${lang}-${country}`;
      const effectiveLocale = def.recognizedLocales.includes(localeCandidate) ? localeCandidate : def.defaultLocale;

      return {
        effectiveLanguage: lang,
        effectiveLocale,
        inferredCountry: country,
        direction: def.direction,
        resolutionSource: 'ACCOUNT_PREFERENCE',
        confidence: 0.95,
        privacyCompliant: true,
        notes: ['Resolved from registered user account profile.']
      };
    }

    // 3. Browser / Device Locale header (Accept-Language parsing)
    if (input.clientHeaderLocale) {
      const parsed = this.parseAcceptLanguage(input.clientHeaderLocale);
      if (parsed) {
        const def = SUPPORTED_LANGUAGES_REGISTRY[parsed.lang];
        return {
          effectiveLanguage: parsed.lang,
          effectiveLocale: parsed.locale,
          inferredCountry: parsed.country || 'UNKNOWN',
          direction: def.direction,
          resolutionSource: 'BROWSER_DEVICE_LOCALE',
          confidence: 0.85,
          privacyCompliant: true,
          notes: [`Parsed from Accept-Language header (${input.clientHeaderLocale})`]
        };
      }
    }

    // 4. Consented Region + Network fallback
    if (input.consentedRegion || input.networkDetectedCountry) {
      const country = (input.consentedRegion || input.networkDetectedCountry || 'IR').toUpperCase();
      const mapped = this.mapCountryToLocale(country);
      const def = SUPPORTED_LANGUAGES_REGISTRY[mapped.lang];

      return {
        effectiveLanguage: mapped.lang,
        effectiveLocale: mapped.locale,
        inferredCountry: country,
        direction: def.direction,
        resolutionSource: input.consentedRegion ? 'CONSENTED_REGION' : 'NETWORK_IP_INFERENCE',
        confidence: input.consentedRegion ? 0.75 : 0.60,
        privacyCompliant: true,
        notes: [`Inferred from coarse geographic signal (${country}). Explicit language preference absent.`]
      };
    }

    // 5. Default project baseline (Persian - IBO Home)
    const baseDef = SUPPORTED_LANGUAGES_REGISTRY['fa'];
    return {
      effectiveLanguage: 'fa',
      effectiveLocale: 'fa-IR',
      inferredCountry: 'IR',
      direction: baseDef.direction,
      resolutionSource: 'DEFAULT_BASELINE',
      confidence: 0.50,
      privacyCompliant: true,
      notes: ['No explicit or inferred signals detected; fallback to IBO default baseline.']
    };
  }

  private static parseAcceptLanguage(header: string): { lang: string; locale: string; country?: string } | null {
    const parts = header.split(',').map(p => p.trim().split(';')[0]);
    for (const part of parts) {
      const [langCode, countryCode] = part.split('-');
      const l = langCode.toLowerCase();
      if (SUPPORTED_LANGUAGES_REGISTRY[l]) {
        const def = SUPPORTED_LANGUAGES_REGISTRY[l];
        if (countryCode) {
          const candidate = `${l}-${countryCode.toUpperCase()}`;
          return {
            lang: l,
            locale: def.recognizedLocales.includes(candidate) ? candidate : def.defaultLocale,
            country: countryCode.toUpperCase()
          };
        }
        return {
          lang: l,
          locale: def.defaultLocale
        };
      }
    }
    return null;
  }

  private static mapCountryToLocale(countryCode: string): { lang: string; locale: string } {
    switch (countryCode) {
      case 'IR':
      case 'AF':
      case 'TJ':
        return { lang: 'fa', locale: 'fa-IR' };
      case 'SA':
        return { lang: 'ar', locale: 'ar-SA' };
      case 'AE':
        return { lang: 'ar', locale: 'ar-AE' };
      case 'EG':
        return { lang: 'ar', locale: 'ar-EG' };
      case 'TR':
        return { lang: 'tr', locale: 'tr-TR' };
      case 'RU':
      case 'BY':
      case 'KZ':
        return { lang: 'ru', locale: 'ru-RU' };
      case 'ES':
      case 'MX':
      case 'AR':
      case 'CO':
        return { lang: 'es', locale: 'es-ES' };
      case 'US':
        return { lang: 'en', locale: 'en-US' };
      case 'GB':
        return { lang: 'en', locale: 'en-GB' };
      case 'CA':
        return { lang: 'en', locale: 'en-CA' };
      case 'AU':
        return { lang: 'en', locale: 'en-AU' };
      default:
        return { lang: 'en', locale: 'en-US' };
    }
  }
}
