/**
 * IBO Ecosystem — Global Growth & Multilingual/Locale Models
 * Part 03: Work Packages 3.1 & 3.2
 */

export type Direction = 'rtl' | 'ltr';

export interface LanguageDefinition {
  languageCode: string;       // ISO 639-1 (e.g. 'fa', 'en', 'ar', 'ru', 'tr', 'es')
  languageName: string;       // Native name (e.g. 'فارسی', 'English')
  script: string;             // ISO 15924 (e.g. 'Arab', 'Latn', 'Cyrl')
  direction: Direction;
  defaultLocale: string;      // Standard default BCP 47 (e.g. 'fa-IR', 'en-US', 'ar-001')
  recognizedLocales: string[];
  numberFormatting: 'persian' | 'arabic-indic' | 'latin';
  dateConventions: 'solar-hijri' | 'gregorian' | 'islamic-lunar';
}

/**
 * Standard registry of supported language cores in the IBO ecosystem.
 * Verifies that:
 * 1. Language != Country (e.g. 'en' supports multiple countries: US, CA, AU, GB).
 * 2. Arabic != single monolithic culture (ar-001 neutral baseline with regional profiles).
 */
export const SUPPORTED_LANGUAGES_REGISTRY: Record<string, LanguageDefinition> = {
  fa: {
    languageCode: 'fa',
    languageName: 'فارسی',
    script: 'Arab',
    direction: 'rtl',
    defaultLocale: 'fa-IR',
    recognizedLocales: ['fa-IR', 'fa-AF'],
    numberFormatting: 'persian',
    dateConventions: 'solar-hijri'
  },
  en: {
    languageCode: 'en',
    languageName: 'English',
    script: 'Latn',
    direction: 'ltr',
    defaultLocale: 'en-US',
    recognizedLocales: ['en-US', 'en-GB', 'en-CA', 'en-AU', 'en-NZ'],
    numberFormatting: 'latin',
    dateConventions: 'gregorian'
  },
  ar: {
    languageCode: 'ar',
    languageName: 'العربية',
    script: 'Arab',
    direction: 'rtl',
    defaultLocale: 'ar-001', // Modern Standard Arabic neutral baseline
    recognizedLocales: ['ar-001', 'ar-SA', 'ar-AE', 'ar-EG', 'ar-KW', 'ar-QA'],
    numberFormatting: 'arabic-indic',
    dateConventions: 'gregorian'
  },
  ru: {
    languageCode: 'ru',
    languageName: 'Русский',
    script: 'Cyrl',
    direction: 'ltr',
    defaultLocale: 'ru-RU',
    recognizedLocales: ['ru-RU', 'ru-BY', 'ru-KZ'],
    numberFormatting: 'latin',
    dateConventions: 'gregorian'
  },
  tr: {
    languageCode: 'tr',
    languageName: 'Türkçe',
    script: 'Latn',
    direction: 'ltr',
    defaultLocale: 'tr-TR',
    recognizedLocales: ['tr-TR', 'tr-CY'],
    numberFormatting: 'latin',
    dateConventions: 'gregorian'
  },
  es: {
    languageCode: 'es',
    languageName: 'Español',
    script: 'Latn',
    direction: 'ltr',
    defaultLocale: 'es-ES',
    recognizedLocales: ['es-ES', 'es-MX', 'es-AR', 'es-CO'],
    numberFormatting: 'latin',
    dateConventions: 'gregorian'
  }
};
