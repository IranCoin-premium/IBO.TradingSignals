import { Request, Response } from 'express';
import { SupportRepository } from './support.repository';
import { logger } from '../../utils/logger';
import { GeoIpService } from './geoip.service';
import { SupportAIAgent } from '../../agents/support-agent/support-ai-agent';
import { TranslatorAgent } from '../../agents/translator-agent/translator-agent';
import crypto from 'crypto';

export class SupportController {
  private static supportAIAgent = new SupportAIAgent();
  private static translatorAgent = new TranslatorAgent();

  static async createTicket(req: Request, res: Response): Promise<void> {
    try {
      const user = (req as any).user;
      if (!user) {
        res.status(401).json({
          errorCode: 'UNAUTHORIZED',
          message: 'دسترسی مجاز نیست. لطفا وارد حساب کاربری خود شوید.'
        });
        return;
      }

      const { subject, message, locale } = req.body;
      if (!subject || !message) {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'موضوع و متن پیام پشتیبانی الزامی است.'
        });
        return;
      }

      const ticketId = `tkt_${crypto.randomBytes(8).toString('hex')}`;
      const userLocale = locale || 'fa';

      let autoResponse = 'پشتیبانی ۲۴ ساعته: درخواست شما ثبت شد و به زودی کارشناسان ما بررسی خواهند کرد.';
      if (userLocale === 'en') {
        autoResponse = '24/7 Support: Your ticket has been received and will be reviewed shortly.';
      } else if (userLocale === 'ar') {
        autoResponse = 'الدعم على مدار الساعة: تم استلام تذكرتك وسيتم مراجعتها قريباً.';
      } else if (userLocale === 'tr') {
        autoResponse = '7/24 Destek: Talebiniz alınmıştır ve yakında incelenecektir.';
      } else if (userLocale === 'ru') {
        autoResponse = 'Круглосуточная поддержка: Ваше обращение получено и скоро будет рассмотрено.';
      } else if (userLocale === 'hi') {
        autoResponse = '24/7 सहायता: आपका टिकट प्राप्त हो गया है और जल्द ही समीक्षा की जाएगी।';
      }

      await SupportRepository.createTicket(
        ticketId,
        user.userId || user.id,
        userLocale,
        subject,
        message
      );

      const updatedTicket = await SupportRepository.updateTicketResponse(
        ticketId,
        autoResponse,
        'PENDING_AGENT'
      );

      logger.info(`[SUPPORT TICKET] Created ticket ${ticketId} for user ${user.userId || user.id} in locale ${userLocale}`);

      res.status(201).json({
        success: true,
        ticket: updatedTicket,
        message: 'Support ticket created successfully with 24/7 assistant response.'
      });
    } catch (error: any) {
      logger.error('Error creating support ticket:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در ثبت تیکت پشتیبانی.'
      });
    }
  }

  static async getTickets(req: Request, res: Response): Promise<void> {
    try {
      const user = (req as any).user;
      if (!user) {
        res.status(401).json({
          errorCode: 'UNAUTHORIZED',
          message: 'دسترسی مجاز نیست.'
        });
        return;
      }

      const tickets = await SupportRepository.getTicketsByUserId(user.userId || user.id);
      res.status(200).json({
        success: true,
        tickets
      });
    } catch (error: any) {
      logger.error('Error fetching support tickets:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در دریافت لیست تیکت‌ها.'
      });
    }
  }

  /**
   * Part 07: 24/7 Multilingual AI Chat (Strict Read-Only Access & Truthful Identity)
   */
  static async chatWithAi(req: Request, res: Response): Promise<void> {
    try {
      const user = (req as any).user;
      const { message, languageCode } = req.body;

      if (!message || typeof message !== 'string') {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'پیام چت الزامی است.'
        });
        return;
      }

      const selectedLanguage = (languageCode || 'fa') as 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';

      // Read-only mock context for user's subscription
      const userContext = {
        userId: user ? (user.userId || user.id) : undefined,
        languageCode: selectedLanguage,
        userSubscription: user ? {
          planName: 'VIP Pro (۱ ماهه)',
          status: 'ACTIVE' as const,
          expiresAt: '2026-10-01'
        } : undefined
      };

      const result = await SupportController.supportAIAgent.processMessage(message, userContext);

      res.status(200).json({
        success: true,
        reply: result.reply,
        isAiDisclosed: result.isAiDisclosed,
        escalateToHuman: result.escalateToHuman,
        languageCode: result.languageCode,
        riskDisclosureIncluded: result.riskDisclosureIncluded
      });
    } catch (error: any) {
      logger.error('Error processing AI support chat:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در ارتباط با دستیار هوشمند پشتیبانی.'
      });
    }
  }

  /**
   * Part 07: Geo-IP Automatic Language Detection
   */
  static async detectGeoLanguage(req: Request, res: Response): Promise<void> {
    try {
      const clientIp = (req.headers['x-forwarded-for'] as string) || req.socket.remoteAddress || '127.0.0.1';
      const cfCountry = req.headers['cf-ipcountry'] as string | undefined;

      const detection = GeoIpService.detectLanguageFromIp(clientIp, cfCountry);
      res.status(200).json({
        success: true,
        detection
      });
    } catch (error: any) {
      logger.error('Error detecting geo language:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'خطا در تشخیص موقعیت جغرافیایی و زبان.'
      });
    }
  }

  /**
   * Part 07: Get 6 Verified Target Languages
   */
  static async getLanguages(req: Request, res: Response): Promise<void> {
    const languages = [
      { code: 'fa', nameNative: 'فارسی', nameEnglish: 'Persian', direction: 'rtl', isActive: true },
      { code: 'en', nameNative: 'English', nameEnglish: 'English', direction: 'ltr', isActive: true },
      { code: 'ar', nameNative: 'العربية', nameEnglish: 'Arabic', direction: 'rtl', isActive: true },
      { code: 'hi', nameNative: 'हिन्दी', nameEnglish: 'Hindi', direction: 'ltr', isActive: true },
      { code: 'tr', nameNative: 'Türkçe', nameEnglish: 'Turkish', direction: 'ltr', isActive: true },
      { code: 'ru', nameNative: 'Русский', nameEnglish: 'Russian', direction: 'ltr', isActive: true }
    ];
    res.status(200).json({
      success: true,
      languages
    });
  }

  /**
   * Part 07: Get Translations for specified language
   */
  static async getTranslations(req: Request, res: Response): Promise<void> {
    const { lang } = req.params;
    const targetLang = (lang || 'fa') as 'fa' | 'en' | 'ar' | 'hi' | 'tr' | 'ru';

    const strings = {
      risk_disclosure: SupportController.translatorAgent.translateString('risk_disclosure', targetLang),
      action_call: SupportController.translatorAgent.translateString('action_call', targetLang),
      action_put: SupportController.translatorAgent.translateString('action_put', targetLang),
      support_24_7: SupportController.translatorAgent.translateString('support_24_7', targetLang)
    };

    res.status(200).json({
      success: true,
      languageCode: targetLang,
      translations: strings
    });
  }
}
