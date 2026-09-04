import { Request, Response } from 'express';
import { SupportRepository } from './support.repository';
import { logger } from '../../utils/logger';
import crypto from 'crypto';

export class SupportController {
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

      // Automated least-privilege support assistant response generation based on topic
      let autoResponse = 'پشتیبانی ۲۴ ساعته: درخواست شما ثبت شد و به زودی کارشناسان ما بررسی خواهند کرد.';
      if (userLocale === 'en') {
        autoResponse = '24/7 Support: Your ticket has been received and will be reviewed shortly.';
      } else if (userLocale === 'ar') {
        autoResponse = 'الدعم على مدار الساعة: تم استلام تذكرتك وسيتم مراجعتها قريباً.';
      } else if (userLocale === 'tr') {
        autoResponse = '7/24 Destek: Talebiniz alınmıştır ve yakında incelenecektir.';
      } else if (userLocale === 'ru') {
        autoResponse = 'Круглосуточная поддержка: Ваше обращение получено и скоро будет рассмотрено.';
      } else if (userLocale === 'es') {
        autoResponse = 'Soporte 24/7: Su ticket ha sido recibido y será revisado pronto.';
      }

      const ticket = await SupportRepository.createTicket(
        ticketId,
        user.userId || user.id,
        userLocale,
        subject,
        message
      );

      // Attach automated assistant response safely
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
}
