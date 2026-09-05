/**
 * 24/7 Multilingual Support AI Agent Engine
 * Part 07: Read-Only Conversational Support Assistant
 */

export interface SupportChatContext {
  userId?: string;
  languageCode: 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';
  userSubscription?: {
    planName: string;
    status: 'ACTIVE' | 'EXPIRED' | 'TRIAL' | 'NONE';
    expiresAt?: string;
  };
}

export interface SupportChatResponse {
  reply: string;
  isAiDisclosed: boolean;
  escalateToHuman: boolean;
  languageCode: string;
  riskDisclosureIncluded: boolean;
}

export class SupportAIAgent {
  private static readonly RISK_DISCLOSURE_MAP: Record<string, string> = {
    fa: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
    en: 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.',
    ar: 'هذه الإشارات ليست نصيحة مالية؛ ينطوي تداول الخيارات الثنائية على مخاطر عالية لفقدان رأس المال.',
    hi: 'ये संकेत वित्तीय सलाह नहीं हैं; बाइनری ऑप्शंस ट्रेडिंग में पूंजी हानि का उच्च जोखिम होता है।',
    tr: 'Bu sinyaller finansal tavsiye niteliğinde değildir; ikili opsiyon işlemleri yüksek sermaye kaybı riski taşır.',
    ru: 'Эти сигналы не являются финансовой рекомендацией; торговля бинарными опционами сопряжена с высоким риском потери капитала.'
  };

  /**
   * Check if user is inquiring about human vs AI identity
   */
  public static isAskingAboutIdentity(query: string): boolean {
    const q = query.toLowerCase();
    return (
      q.includes('انسان') ||
      q.includes('ربات') ||
      q.includes('بات') ||
      q.includes('آدم') ||
      q.includes('human') ||
      q.includes('bot') ||
      q.includes('ai') ||
      q.includes('real person') ||
      q.includes('إنسان') ||
      q.includes('روبوت') ||
      q.includes('bot musun') ||
      q.includes('insan mısın') ||
      q.includes('вы человек') ||
      q.includes('робот') ||
      q.includes('इंसान') ||
      q.includes('रोबोट')
    );
  }

  /**
   * Process incoming user chat query (Strict Read-Only Mode)
   */
  public async processMessage(
    userMessage: string,
    context: SupportChatContext
  ): Promise<SupportChatResponse> {
    const lang = context.languageCode || 'fa';
    const riskNote = SupportAIAgent.RISK_DISCLOSURE_MAP[lang] || SupportAIAgent.RISK_DISCLOSURE_MAP.en;
    let reply = '';
    let isAiDisclosed = false;
    let escalateToHuman = false;

    // 1. Honesty & Identity Check: Truthfully disclose AI status
    if (SupportAIAgent.isAskingAboutIdentity(userMessage)) {
      isAiDisclosed = true;
      switch (lang) {
        case 'en':
          reply = `Yes, I am the 24/7 AI Support Assistant for the IBO Trading Signals platform. I am here to help answer your questions about subscriptions, signal connections, and app features. If you need assistance from a human specialist, I can gladly escalate your request.`;
          break;
        case 'ar':
          reply = `نعم، أنا مساعد الذكاء الاصطناعي للدعم على مدار 24/7 لمنصة إشارات IBO. أنا هنا لمساعدتك في استفسارات الاشتراك والاتصال. إذا كنت بحاجة إلى التحدث مع موظف دعم بشري، يمكنني إحالة طلبك فوراً.`;
          break;
        case 'tr':
          reply = `Evet, ben IBO İşlem Sinyalleri platformunun 7/24 Yapay Zeka Destek Asistanıyım. Abonelikler, sinyal bağlantıları ve özellikler hakkındaki sorularınızı yanıtlamak için buradayım. İnsan bir uzmana bağlanmak isterseniz talebinizi hemen yönlendirebilirim.`;
          break;
        case 'ru':
          reply = `Да, я ИИ-ассистент круглосуточной поддержки платформы торговых сигналов IBO. Я готов помочь вам с вопросами по подписке, подключению сигналов и функциям платформы. При необходимости я могу перевести диалог на живого оператора.`;
          break;
        case 'hi':
          reply = `हाँ, मैं IBO ट्रेडिंग सिग्नल्स प्लेटफॉर्म का 24/7 AI सहायता सहायक हूँ। मैं सदस्यता और सिग्नल्स से जुड़े आपके सवालों में मदद के लिए यहाँ हूँ। यदि आप किसी मानव विशेषज्ञ से बात करना चाहते हैं, तो मैं आपका अनुरोध अग्रेषित कर सकता हूँ।`;
          break;
        case 'fa':
        default:
          reply = `بله، من دستیار هوش مصنوعی پشتیبانی ۲۴ ساعته پلتفرم IBO هستم و برای پاسخ‌گویی سریع به سوالات شما درباره اشتراک‌ها، اتصال سیگنال و امکانات برنامه طراحی شده‌ام. در صورتی که نیاز به بررسی توسط همکاران انسانی ما داشته باشید، می‌توانم تیکت شما را به کارشناس پشتیبانی ارجاع دهم.`;
          break;
      }
      return {
        reply: `${reply}\n\n⚠️ ${riskNote}`,
        isAiDisclosed: true,
        escalateToHuman: false,
        languageCode: lang,
        riskDisclosureIncluded: true
      };
    }

    // 2. Subscription Status Inquiries (Read-Only)
    if (userMessage.includes('اشتراک') || userMessage.toLowerCase().includes('subscription') || userMessage.toLowerCase().includes('plan')) {
      const sub = context.userSubscription;
      if (sub && sub.status === 'ACTIVE') {
        switch (lang) {
          case 'en':
            reply = `Your subscription (${sub.planName}) is currently ACTIVE. Expiry date: ${sub.expiresAt || 'Active VIP'}. Signals are being pushed in real-time.`;
            break;
          case 'fa':
          default:
            reply = `اشتراک شما (${sub.planName}) هم‌اکنون فعال است. تاریخ انقضا: ${sub.expiresAt || 'دسترسی فعال VIP'}. دریافت سیگنال‌ها به صورت آنی فعال می‌باشد.`;
            break;
        }
      } else {
        switch (lang) {
          case 'en':
            reply = `You do not have an active subscription at this moment. You can view our available plans and subscribe via the Plans tab.`;
            break;
          case 'fa':
          default:
            reply = `در حال حاضر اشتراک فعالی برای حساب شما ثبت نشده است. می‌توانید از بخش پلن‌های اشتراک، پلن مورد نظر خود را انتخاب و فعال نمایید.`;
            break;
        }
      }
    } else if (userMessage.toLowerCase().includes('human') || userMessage.includes('اپراتور') || userMessage.includes('کارشناس')) {
      escalateToHuman = true;
      reply = lang === 'en'
        ? `I have escalated your inquiry to our human technical support team. A team member will follow up with you shortly.`
        : `درخواست شما جهت بررسی به کارشناس پشتیبانی انسانی ارجاع داده شد. کارشناسان ما به زودی از طریق پیام یا تیکت پاسخ شما را ارسال خواهند کرد.`;
    } else {
      // General informative reply
      reply = lang === 'en'
        ? `Thank you for reaching out to 24/7 IBO Support. How can we assist you with our trading signal platform today?`
        : `با تشکر از تماس شما با پشتیبانی ۲۴ ساعته IBO. چطور می‌توانم در زمینه اشتراک و سیگنال‌های معاملاتی به شما کمک کنم؟`;
    }

    return {
      reply: `${reply}\n\n⚠️ ${riskNote}`,
      isAiDisclosed,
      escalateToHuman,
      languageCode: lang,
      riskDisclosureIncluded: true
    };
  }
}
