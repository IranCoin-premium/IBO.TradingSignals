import { Router, Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { authenticateToken, requireRoles, AuthenticatedRequest } from '../../middleware/auth';
import { CustomError } from '../../middleware/error';
import { query, pool } from '../../config/database';
import { logger } from '../../utils/logger';
import { PaymentProvidersService } from './providers.service';

const router = Router();

// ====================================================================
// 1. APP STORE IN-APP BILLING (Myket / Bazaar / IranApps)
// ====================================================================
const storeVerifySchema = z.object({
  orderId: z.string().min(1, 'شناسه سفارش الزامی است'),
  purchaseToken: z.string().min(4, 'توکن خرید نامعتبر است'),
  productId: z.string().min(1, 'شناسه محصول الزامی است'),
});

router.post(
  '/:provider/verify',
  authenticateToken as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const provider = req.params.provider?.toLowerCase() as 'myket' | 'bazaar' | 'iranapps';
      if (!['myket', 'bazaar', 'iranapps'].includes(provider)) {
        const error: CustomError = new Error('ارائه‌دهنده درگاه استور نامعتبر است');
        error.statusCode = 400;
        return next(error);
      }

      const parseResult = storeVerifySchema.safeParse(req.body);
      if (!parseResult.success) {
        const error: CustomError = new Error('داده‌های ارسالی نامعتبر است');
        error.statusCode = 400;
        error.details = parseResult.error.flatten().fieldErrors;
        return next(error);
      }

      const { orderId, purchaseToken, productId } = parseResult.data;

      // Server-to-Server token verification against official store API
      const verifyResult = await PaymentProvidersService.verifyAppStorePurchase(
        provider,
        productId,
        purchaseToken,
        orderId
      );

      if (!verifyResult.isValid) {
        const error: CustomError = new Error('اعتبارسنجی رسید خرید در سرور استور ناموفق بود');
        error.statusCode = 400;
        error.details = { provider, reason: verifyResult.errorMessage };
        return next(error);
      }

      // Update Order & Activate Subscription atomically
      const client = await pool.connect();
      try {
        await client.query('BEGIN');

        // Check and lock order
        const orderRes = await client.query('SELECT * FROM orders WHERE id = $1 FOR UPDATE', [orderId]);
        if (orderRes.rowCount === 0) {
          // Fallback check in payment_transactions
          await client.query(
            `UPDATE payment_transactions 
             SET status = 'PAID', updated_at = NOW() 
             WHERE id = $1`,
            [orderId]
          );
        } else {
          await client.query(
            `UPDATE orders 
             SET status = 'paid', updated_at = NOW() 
             WHERE id = $1`,
            [orderId]
          );
        }

        // Insert / Update Subscription
        const userId = req.user?.id;
        if (userId) {
          const planId = orderRes.rows[0]?.plan_id || 'PRO_30D';
          const startDate = new Date();
          const endDate = new Date();
          endDate.setDate(endDate.getDate() + 30);

          await client.query(
            `INSERT INTO subscriptions (user_id, plan_id, status, start_at, end_at, payment_method)
             VALUES ($1, $2, 'active', $3, $4, $5)
             ON CONFLICT DO NOTHING`,
            [userId, planId, startDate, endDate, provider]
          );
        }

        await client.query(
          `INSERT INTO audit_logs (actor, action, target_entity, after_state)
           VALUES ('human_admin', 'IAP_VERIFIED_PAID', 'orders', $1)`,
          [JSON.stringify({ orderId, provider, purchaseToken })]
        );

        await client.query('COMMIT');
      } catch (e) {
        await client.query('ROLLBACK');
        throw e;
      } finally {
        client.release();
      }

      res.status(200).json({
        status: 'success',
        message: 'پرداخت درون‌برنامه‌ای استور با موفقیت تایید و اشتراک شما فعال گردید.',
        data: {
          orderId,
          provider,
          status: 'PAID',
        },
      });
    } catch (err) {
      next(err);
    }
  }
);

// ====================================================================
// 2. CARD-TO-CARD RECEIPT SUBMISSION
// ====================================================================
const cardReceiptSchema = z.object({
  orderId: z.string().min(1, 'شناسه سفارش الزامی است'),
  trackingCode: z.string().min(4, 'شماره پیگیری فیش بانکی الزامی است'),
  filePath: z.string().min(5, 'مسیر فایل فیش در فضای ابری الزامی است'),
  fileMimeType: z.string().default('image/jpeg'),
  fileSizeBytes: z.number().default(1024 * 500),
});

router.post(
  '/card-transfer/submit-receipt',
  authenticateToken as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const parseResult = cardReceiptSchema.safeParse(req.body);
      if (!parseResult.success) {
        const error: CustomError = new Error('اطلاعات ارسالی رسید نامعتبر است');
        error.statusCode = 400;
        error.details = parseResult.error.flatten().fieldErrors;
        return next(error);
      }

      const { orderId, trackingCode, filePath, fileMimeType, fileSizeBytes } = parseResult.data;

      // Validate file size and type
      const fileValidation = PaymentProvidersService.validateReceiptFile({
        mimetype: fileMimeType,
        size: fileSizeBytes,
      });

      if (!fileValidation.valid) {
        const error: CustomError = new Error(fileValidation.error || 'فایل ارسالی نامعتبر است');
        error.statusCode = 400;
        return next(error);
      }

      // Record in payment_receipts and transition order to awaiting_manual_review
      const client = await pool.connect();
      try {
        await client.query('BEGIN');

        await client.query(
          `UPDATE orders SET status = 'awaiting_manual_review', updated_at = NOW() WHERE id = $1`,
          [orderId]
        );

        const receiptRes = await client.query(
          `INSERT INTO payment_receipts (order_id, file_path, tracking_code, review_status)
           VALUES ($1, $2, $3, 'pending')
           RETURNING *`,
          [orderId, filePath, trackingCode]
        );

        await client.query(
          `INSERT INTO audit_logs (actor, action, target_entity, after_state)
           VALUES ('human_admin', 'RECEIPT_SUBMITTED', 'payment_receipts', $1)`,
          [JSON.stringify({ orderId, trackingCode, receiptId: receiptRes.rows[0].id })]
        );

        await client.query('COMMIT');

        res.status(201).json({
          status: 'success',
          message: 'فیش واریزی شما با موفقیت ثبت شد و در صف بررسی اپراتور قرار گرفت.',
          data: receiptRes.rows[0],
        });
      } catch (e) {
        await client.query('ROLLBACK');
        throw e;
      } finally {
        client.release();
      }
    } catch (err) {
      next(err);
    }
  }
);

// ====================================================================
// ADMIN REVIEW OF CARD-TO-CARD RECEIPTS
// ====================================================================
const reviewReceiptSchema = z.object({
  action: z.enum(['approve', 'reject']),
  reason: z.string().optional(),
});

router.post(
  '/receipts/:receiptId/review',
  authenticateToken as any,
  requireRoles(['ADMIN', 'SUPER_ADMIN']) as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const { receiptId } = req.params;
      const parseResult = reviewReceiptSchema.safeParse(req.body);
      if (!parseResult.success) {
        const error: CustomError = new Error('اقدام بازبینی مشخص شده نامعتبر است');
        error.statusCode = 400;
        return next(error);
      }

      const { action, reason } = parseResult.data;
      const adminId = req.user?.id;

      const client = await pool.connect();
      try {
        await client.query('BEGIN');

        const receiptRes = await client.query(
          'SELECT * FROM payment_receipts WHERE id = $1 FOR UPDATE',
          [receiptId]
        );
        if (receiptRes.rowCount === 0) {
          throw new Error('رسید مورد نظر یافت نشد');
        }

        const receipt = receiptRes.rows[0];
        const newReviewStatus = action === 'approve' ? 'approved' : 'rejected';

        await client.query(
          `UPDATE payment_receipts 
           SET review_status = $1, reviewed_by = $2, reviewed_at = NOW() 
           WHERE id = $3`,
          [newReviewStatus, adminId, receiptId]
        );

        if (action === 'approve') {
          // Transition order to paid
          await client.query(
            `UPDATE orders SET status = 'paid', updated_at = NOW() WHERE id = $1`,
            [receipt.order_id]
          );

          // Find order user to activate subscription
          const orderRes = await client.query('SELECT * FROM orders WHERE id = $1', [receipt.order_id]);
          if (orderRes.rowCount && orderRes.rowCount > 0) {
            const order = orderRes.rows[0];
            const startDate = new Date();
            const endDate = new Date();
            endDate.setDate(endDate.getDate() + 30);

            await client.query(
              `INSERT INTO subscriptions (user_id, plan_id, status, start_at, end_at, payment_method)
               VALUES ($1, $2, 'active', $3, $4, 'card_to_card')
               ON CONFLICT DO NOTHING`,
              [order.user_id, order.plan_id, startDate, endDate]
            );
          }
        } else {
          // Reject order
          await client.query(
            `UPDATE orders SET status = 'failed', updated_at = NOW() WHERE id = $1`,
            [receipt.order_id]
          );
        }

        await client.query(
          `INSERT INTO audit_logs (actor, action, target_entity, after_state)
           VALUES ('human_admin', $1, 'payment_receipts', $2)`,
          [
            `RECEIPT_${newReviewStatus.toUpperCase()}`,
            JSON.stringify({ receiptId, action, reason, adminId }),
          ]
        );

        await client.query('COMMIT');

        res.status(200).json({
          status: 'success',
          message:
            action === 'approve'
              ? 'رسید تایید و اشتراک کاربر فعال شد.'
              : `رسید رد شد. علت: ${reason || 'عدم تطابق فیش'}`,
        });
      } catch (e: any) {
        await client.query('ROLLBACK');
        const error: CustomError = new Error(e.message || 'خطا در بازبینی فیش');
        error.statusCode = 400;
        return next(error);
      } finally {
        client.release();
      }
    } catch (err) {
      next(err);
    }
  }
);

// ====================================================================
// 3. SHEBA / POL / PAYA / SATNA (Direct Bank Transfer Tracking)
// ====================================================================
const bankTrackingSchema = z.object({
  orderId: z.string().min(1, 'شناسه سفارش الزامی است'),
  trackingCode: z.string().min(4, 'کد پیگیری بانکی الزامی است'),
  bankName: z.string().min(2, 'نام بانک واریزکننده الزامی است'),
});

router.post(
  '/bank-transfer/submit-tracking',
  authenticateToken as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const parseResult = bankTrackingSchema.safeParse(req.body);
      if (!parseResult.success) {
        const error: CustomError = new Error('داده‌های پیگیری حواله بانکی ناقص است');
        error.statusCode = 400;
        error.details = parseResult.error.flatten().fieldErrors;
        return next(error);
      }

      const { orderId, trackingCode, bankName } = parseResult.data;

      await query(
        `UPDATE orders SET status = 'awaiting_manual_review', updated_at = NOW() WHERE id = $1`,
        [orderId]
      );

      await query(
        `INSERT INTO audit_logs (actor, action, target_entity, after_state)
         VALUES ('human_admin', 'BANK_TRANSFER_SUBMITTED', 'orders', $1)`,
        [JSON.stringify({ orderId, trackingCode, bankName })]
      );

      res.status(200).json({
        status: 'success',
        message: 'کد رهگیری حواله بانکی (شبا/پایا/ساتنا) ثبت شد و در انتظار استعلام مالی قرار گرفت.',
        data: { orderId, trackingCode, status: 'AWAITING_MANUAL_REVIEW' },
      });
    } catch (err) {
      next(err);
    }
  }
);

// ====================================================================
// 4. NOWPAYMENTS DIGITAL ASSET (CRYPTO / USDT)
// ====================================================================
router.post(
  '/digital-asset/create-invoice',
  authenticateToken as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const { orderId } = req.body;
      if (!orderId) {
        const error: CustomError = new Error('شناسه سفارش الزامی است');
        error.statusCode = 400;
        return next(error);
      }

      // Fetch order
      const orderRes = await query('SELECT * FROM orders WHERE id = $1', [orderId]);
      const amountUsd = orderRes.rowCount ? parseFloat(orderRes.rows[0].amount) : 25.0;

      const invoice = await PaymentProvidersService.createNowPaymentsInvoice({
        orderId,
        amountUsd,
        description: 'اشتراک VIP سیگنال‌های باینری آپشن IBO',
        callbackUrl: `${process.env.APP_URL || 'https://api.ibo.ir'}/api/v1/webhooks/nowpayments`,
        successUrl: `${process.env.FRONTEND_URL || 'https://ibo.ir'}/payment/success?orderId=${orderId}`,
      });

      // Update order to processing
      await query(`UPDATE orders SET status = 'processing', updated_at = NOW() WHERE id = $1`, [orderId]);

      res.status(201).json({
        status: 'success',
        message: 'فاکتور پرداخت آنلاین تتر با موفقیت صادر شد.',
        data: invoice,
      });
    } catch (err) {
      next(err);
    }
  }
);

// NOWPAYMENTS IPN WEBHOOK
router.post('/webhooks/nowpayments', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const signature = req.headers['x-nowpayments-sig'] as string | undefined;
    const payload = req.body;

    // Log raw webhook
    await query(
      `INSERT INTO webhooks_log (source, raw_payload, processed)
       VALUES ('nowpayments', $1, false)`,
      [JSON.stringify(payload)]
    );

    // Verify HMAC SHA512 signature
    const isSignatureValid = PaymentProvidersService.verifyNowPaymentsSignature(payload, signature);
    if (!isSignatureValid && process.env.APP_ENV !== 'test') {
      const error: CustomError = new Error('امضای دیجیتال وب‌هووک NowPayments نامعتبر است');
      error.statusCode = 401;
      return next(error);
    }

    const { payment_status, order_id } = payload;
    logger.info(`NowPayments IPN received: status=${payment_status}, orderId=${order_id}`);

    if (['finished', 'confirmed'].includes(payment_status)) {
      // Mark PAID
      await query(`UPDATE orders SET status = 'paid', updated_at = NOW() WHERE id = $1`, [order_id]);
      // Mark webhook processed
      await query(
        `UPDATE webhooks_log SET processed = true WHERE source = 'nowpayments' AND raw_payload->>'order_id' = $1`,
        [order_id]
      );
    } else if (['waiting', 'confirming', 'sending'].includes(payment_status)) {
      await query(`UPDATE orders SET status = 'processing', updated_at = NOW() WHERE id = $1`, [order_id]);
    } else if (['failed', 'refunded', 'expired'].includes(payment_status)) {
      await query(`UPDATE orders SET status = 'failed', updated_at = NOW() WHERE id = $1`, [order_id]);
    }

    res.status(200).json({ status: 'success', message: 'IPN processed' });
  } catch (err) {
    next(err);
  }
});

// ====================================================================
// 5. DIGIPAY & SNAPPAY BNPL (4 INSTALLMENTS)
// ====================================================================
router.post(
  '/:provider/create-installment-plan',
  authenticateToken as any,
  async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    try {
      const provider = req.params.provider?.toLowerCase() as 'digipay' | 'snappay';
      if (!['digipay', 'snappay'].includes(provider)) {
        const error: CustomError = new Error('ارائه‌دهنده اقساط نامعتبر است');
        error.statusCode = 400;
        return next(error);
      }

      const { orderId } = req.body;
      if (!orderId) {
        const error: CustomError = new Error('شناسه سفارش الزامی است');
        error.statusCode = 400;
        return next(error);
      }

      const orderRes = await query('SELECT * FROM orders WHERE id = $1', [orderId]);
      const totalAmount = orderRes.rowCount ? parseFloat(orderRes.rows[0].amount) : 4000000;

      const plan = await PaymentProvidersService.createInstallmentPlan(provider, orderId, totalAmount);

      res.status(201).json({
        status: 'success',
        message: `قرارداد ۴ قسطه ${provider === 'digipay' ? 'دیجی‌پی' : 'اسنپ‌پی'} با موفقیت ایجاد شد.`,
        data: plan,
      });
    } catch (err) {
      next(err);
    }
  }
);

// ====================================================================
// 6. AVAILABLE PAYMENT GATEWAYS DIRECTORY (Including Disabled / Coming Soon)
// ====================================================================
router.get('/gateways/available', async (req: Request, res: Response) => {
  res.status(200).json({
    status: 'success',
    data: [
      {
        id: 'bazaar',
        name: 'کافه‌بازار (پرداخت درون‌برنامه‌ای)',
        type: 'in_app_purchase',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'myket',
        name: 'مایکت (پرداخت درون‌برنامه‌ای)',
        type: 'in_app_purchase',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'iranapps',
        name: 'ایران‌اپس',
        type: 'in_app_purchase',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'card_to_card',
        name: 'کارت‌به‌کارت و ارسال فیش واریز',
        type: 'manual_verification',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'shaba',
        name: 'حواله شبا (پایا / پل / ساتنا)',
        type: 'bank_transfer',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'nowpayments',
        name: 'ارز دیجیتال / تتر (NowPayments)',
        type: 'crypto',
        status: 'active',
        currencies: ['USDT', 'BTC', 'ETH'],
      },
      {
        id: 'snappay',
        name: 'اسنپ‌پی (پرداخت اقساطی ۴ ماهه)',
        type: 'bnpl',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'digipay',
        name: 'دیجی‌پی (پرداخت اقساطی ۴ ماهه)',
        type: 'bnpl',
        status: 'active',
        currencies: ['IRR'],
      },
      {
        id: 'googleplay',
        name: 'Google Play Billing',
        type: 'in_app_purchase',
        status: 'coming_soon',
        translation_key: 'googleplay_coming_soon',
        currencies: ['USD', 'EUR'],
      },
      {
        id: 'papara_troy',
        name: 'Papara / Troy (Turkey)',
        type: 'local_gateway',
        status: 'coming_soon',
        translation_key: 'turkey_gateway_coming_soon',
        currencies: ['TRY'],
      },
      {
        id: 'mir_sbp',
        name: 'Mir / SBP (Russia)',
        type: 'local_gateway',
        status: 'coming_soon',
        translation_key: 'russia_gateway_coming_soon',
        currencies: ['RUB'],
      },
    ],
  });
});

export default router;
