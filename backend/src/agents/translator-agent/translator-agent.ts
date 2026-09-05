/**
 * Translator Agent Engine & Pre-Publish Validation Gate
 * Part 07: Localization and Multilingual Management
 */

export interface TranslationInput {
  entityType: 'ui_string' | 'news' | 'journal';
  entityId: string;
  sourceText: string;
  sourceLanguage: string;
  targetLanguage: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';
}

export interface ValidationGateResult {
  valid: boolean;
  errors: string[];
}

export class TranslatorAgent {
  private agentId = 'agent-financial-translator';

  // Seed dictionary for standard financial and UI strings
  private static readonly LOCALIZED_STRINGS: Record<string, Record<string, string>> = {
    risk_disclosure: {
      fa: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
      en: 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.',
      ar: 'هذه الإشارات ليست نصيحة مالية؛ ينطوي تداول الخيارات الثنائية على مخاطر عالية لفقدان رأس المال.',
      hi: 'ये संकेत वित्तीय सलाह नहीं हैं; बाइनरी ऑप्शंस ट्रेडिंग में पूंजी हानि का उच्च जोखिम होता है।',
      tr: 'Bu sinyaller finansal tavsiye niteliğinde değildir; ikili opsiyon işlemleri yüksek sermaye kaybı riski taşır.',
      ru: 'Эти сигналы не являются финансовой рекомендацией; торговля бинарными опционами сопряжена с высоким риском потери капитала.'
    },
    action_call: {
      fa: 'خرید (صعودی)',
      en: 'CALL (Higher)',
      ar: 'شراء (صعود)',
      hi: 'कॉल (ऊपर)',
      tr: 'YUKARI (Al)',
      ru: 'ВВЕРХ (Call)'
    },
    action_put: {
      fa: 'فروش (نزولی)',
      en: 'PUT (Lower)',
      ar: 'بيع (هبوط)',
      hi: 'पुट (नीचे)',
      tr: 'AŞAĞI (Sat)',
      ru: 'ВНИЗ (Put)'
    },
    support_24_7: {
      fa: 'پشتیبانی ۲۴ ساعته',
      en: '24/7 Support',
      ar: 'الدعم على مدار 24 ساعة',
      hi: '24/7 सहायता',
      tr: '7/24 Canlı Destek',
      ru: 'Круглосуточная поддержка'
    }
  };

  /**
   * Pre-publish Quality Gate: Validates translated string before publishing
   */
  public validatePrePublish(
    sourceText: string,
    translatedText: string,
    targetLanguage: string
  ): ValidationGateResult {
    const errors: string[] = [];

    // 1. Check for empty or missing text
    if (!translatedText || translatedText.trim().length === 0) {
      errors.push('Translated text cannot be empty.');
      return { valid: false, errors };
    }

    // 2. Untranslated placeholders check
    const forbiddenPlaceholders = ['TODO', '[UNTRANSLATED]', 'N/A', 'UNDEFINED', 'NULL'];
    for (const ph of forbiddenPlaceholders) {
      if (translatedText.toUpperCase().includes(ph)) {
        errors.push(`Translated text contains forbidden placeholder '${ph}'.`);
      }
    }

    // 3. Template variable preservation (e.g. {amount}, {asset}, {time})
    const sourceVariables = sourceText.match(/\{[a-zA-Z0-9_]+\}/g) || [];
    for (const variable of sourceVariables) {
      if (!translatedText.includes(variable)) {
        errors.push(`Missing template variable '${variable}' in translated output.`);
      }
    }

    // 4. Character Length Budget check: Alert if expansion exceeds 50% for short strings (<100 chars)
    if (sourceText.length < 100 && translatedText.length > sourceText.length * 1.6 + 10) {
      errors.push(`Length budget exceeded: translation is ${translatedText.length} chars vs source ${sourceText.length} chars (>160%).`);
    }

    // 5. Mandatory risk disclosure preservation
    if (sourceText.includes('ریسک بالای از دست دادن سرمایه') || sourceText.toLowerCase().includes('high risk of capital loss')) {
      const standardDisclosures = Object.values(TranslatorAgent.LOCALIZED_STRINGS.risk_disclosure);
      const riskKeywords = [
        'ریسک بالای از دست دادن سرمایه',
        'high risk of capital loss',
        'مخاطر عالية لفقدان رأس المال',
        'पूंजी हानि का उच्च जोखिम',
        'yüksek sermaye kaybı riski',
        'риском потери капитала'
      ];
      const containsValidDisclosure = standardDisclosures.some(d => translatedText.includes(d)) ||
                                      riskKeywords.some(kw => translatedText.toLowerCase().includes(kw.toLowerCase()));
      if (!containsValidDisclosure) {
        errors.push('Mandatory risk disclosure is missing in the translated target content.');
      }
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * Translates an entity string using localized financial register
   */
  public translateString(
    entityId: string,
    targetLanguage: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru',
    fallbackText?: string
  ): string {
    const knownEntity = TranslatorAgent.LOCALIZED_STRINGS[entityId];
    if (knownEntity && knownEntity[targetLanguage]) {
      return knownEntity[targetLanguage];
    }
    return fallbackText || entityId;
  }
}
