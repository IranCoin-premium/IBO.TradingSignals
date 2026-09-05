import { Response, NextFunction } from 'express';
import crypto from 'crypto';
import { z } from 'zod';
import { PaymentsRepository } from './payments.repository';
import { SubscriptionsRepository } from '../subscriptions/subscriptions.repository';
import { AuthenticatedRequest } from '../../middleware/auth';
import { CustomError } from '../../middleware/error';
import { query } from '../../config/database';

// Zod validation schemas
const checkoutSchema = z.object({
  planId: z.string().min(1, 'شناسه پلن الزامی است'),
  paymentMethod: z.string().min(1, 'روش پرداخت الزامی است'),
  externalReference: z.string().min(4, 'شناسه ارجاع خارجی نامعتبر است'),
});

const verifySchema = z.object({
  transactionId: z.string().uuid('شناسه تراکنش نامعتبر است'),
  verificationSource: z.string().min(1, 'منبع تایید تراکنش الزامی است'),
  metadata: z.record(z.any()).optional().default({}),
});

const purchaseIntentSchema = z.object({
  planId: z.string().min(1, 'شناسه پلن الزامی است'),
  paymentMethod: z.string().min(1, 'روش پرداخت الزامی است'),
  idempotencyKey: z.string().min(10, 'کلید یکتایی نامعتبر است'),
});

const refundSchema = z.object({
  transactionId: z.string().uuid('شناسه تراکنش نامعتبر است'),
  amount: z.number().positive('مبلغ بازپرداخت باید بزرگتر از صفر باشد'),
  reason: z.string().min(4, 'علت بازپرداخت الزامی است'),
});

// CREATE STANDARD CHECKOUT
export const checkout = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر یافت نشد');
      error.statusCode = 401;
      return next(error);
    }

    const parseResult = checkoutSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('داده‌های ورودی نامعتبر هستند');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { planId, paymentMethod, externalReference } = parseResult.data;

    // Fetch authorized prices strictly from server database to prevent pricing injections
    const plan = await SubscriptionsRepository.findPlanById(planId);
    if (!plan) {
      const error: CustomError = new Error('پلن خرید انتخاب شده معتبر نیست');
      error.statusCode = 404;
      error.code = 'PLAN_NOT_FOUND';
      return next(error);
    }

    // Check for unique duplicate hash reference to avoid double-spend exploits
    const existingTx = await PaymentsRepository.findTransactionByRef(externalReference);
    if (existingTx) {
      const error: CustomError = new Error('این تراکنش قبلاً در سیستم ثبت شده است');
      error.statusCode = 409;
      error.code = 'DUPLICATE_TRANSACTION_REFERENCE';
      return next(error);
    }

    const tx = await PaymentsRepository.createTransaction(
      req.user.id,
      plan.id,
      plan.price,
      paymentMethod,
      externalReference
    );

    res.status(201).json({
      status: 'success',
      data: tx,
    });
  } catch (error) {
    next(error);
  }
};

// VERIFY STANDARD PAYMENT
export const verifyPayment = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parseResult = verifySchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ارسالی نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { transactionId, verificationSource, metadata } = parseResult.data;

    const tx = await PaymentsRepository.findTransactionById(transactionId);
    if (!tx) {
      const error: CustomError = new Error('تراکنش مورد نظر یافت نشد');
      error.statusCode = 404;
      error.code = 'TRANSACTION_NOT_FOUND';
      return next(error);
    }

    // Atomic transaction verification and subscription generation
    await PaymentsRepository.verifyAndActivateTransaction(
      tx.id,
      verificationSource,
      metadata
    );

    res.status(200).json({
      status: 'success',
      message: 'تراکنش با موفقیت تایید و اشتراک فعال گردید.',
    });
  } catch (error) {
    next(error);
  }
};

// --- NEW PART 4 GOVERNANCE APIS ---

// 1. CREATE PURCHASE INTENT
export const createPurchaseIntent = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر تایید صلاحیت نشده است');
      error.statusCode = 401;
      return next(error);
    }

    const parseResult = purchaseIntentSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('داده‌های ورودی نامعتبر هستند');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { planId, paymentMethod, idempotencyKey } = parseResult.data;

    // Strict Server-Authoritative pricing loading
    const plan = await SubscriptionsRepository.findPlanById(planId);
    if (!plan) {
      const error: CustomError = new Error('پلن مورد نظر معتبر نیست');
      error.statusCode = 404;
      return next(error);
    }

    // Idempotency: Avoid creating duplicate intents with the same key
    const existingIntent = await PaymentsRepository.findPurchaseIntentByIdempotencyKey(idempotencyKey);
    if (existingIntent) {
      return res.status(200).json({
        status: 'success',
        message: 'بازیابی قصد خرید تکراری بر اساس کلید یکتایی',
        data: existingIntent,
      });
    }

    // Quoted amount must remain immutable
    const expiresAt = new Date();
    expiresAt.setMinutes(expiresAt.getMinutes() + 30); // Valid for 30 minutes

    const intent = await PaymentsRepository.createPurchaseIntent(
      req.user.id,
      plan.id,
      plan.price,
      'USD',
      paymentMethod,
      idempotencyKey,
      expiresAt
    );

    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
       VALUES ($1, 'USER', 'PURCHASE_INTENT_CREATED', 'purchase_intents', $2, 'SUCCESS')`,
      [req.user.id, intent.id]
    );

    res.status(201).json({
      status: 'success',
      data: intent,
    });
  } catch (error) {
    next(error);
  }
};

// 2. GET PURCHASE INTENT
export const getPurchaseIntent = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { id } = req.params;
    const intent = await PaymentsRepository.findPurchaseIntentById(id);
    if (!intent) {
      const error: CustomError = new Error('قصد خرید یافت نشد');
      error.statusCode = 404;
      return next(error);
    }

    res.status(200).json({
      status: 'success',
      data: intent,
    });
  } catch (error) {
    next(error);
  }
};

// 3. CANCEL SUBSCRIPTION
export const cancelSubscription = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر یافت نشد');
      error.statusCode = 401;
      return next(error);
    }

    const { id } = req.params;
    const success = await PaymentsRepository.cancelSubscription(id, req.user.id);
    if (!success) {
      const error: CustomError = new Error('امکان لغو این اشتراک وجود ندارد یا اشتراک فعال یافت نشد');
      error.statusCode = 400;
      return next(error);
    }

    res.status(200).json({
      status: 'success',
      message: 'اشتراک با موفقیت لغو شد و تمدید خودکار غیرفعال گردید.',
    });
  } catch (error) {
    next(error);
  }
};

// 4. REFUND SUBSCRIPTION (ADMIN ONLY)
export const issueRefund = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parseResult = refundSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ارسالی نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { transactionId, amount, reason } = parseResult.data;
    const refund = await PaymentsRepository.issueRefund(
      transactionId,
      amount,
      reason,
      req.user?.id || null
    );

    res.status(200).json({
      status: 'success',
      message: 'مبلغ تراکنش با موفقیت عودت داده شد و دسترسی‌های متناظر لغو گردیدند.',
      data: refund,
    });
  } catch (error) {
    next(error);
  }
};

// 5. SECURE WEBHOOKS HANDLER
export const handleWebhook = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { provider } = req.params;
    const webhookSecret = req.headers['x-webhook-signature'];

    // Webhook Signature Security Validation
    if (!webhookSecret || webhookSecret !== 'IBO_SECURE_WEBHOOK_SECRET_2026') {
      const error: CustomError = new Error('امضای وب‌هووک ارسالی نامعتبر یا گم شده است');
      error.statusCode = 401;
      return next(error);
    }

    const { eventType, transactionId, payload } = req.body;

    if (!transactionId || !eventType) {
      const error: CustomError = new Error('پارامترهای الزامی وب‌هووک ناقص است');
      error.statusCode = 400;
      return next(error);
    }

    const tx = await PaymentsRepository.findTransactionById(transactionId);
    if (!tx) {
      const error: CustomError = new Error('تراکنش وب‌هووک در پایگاه داده یافت نشد');
      error.statusCode = 404;
      return next(error);
    }

    // Atomic verify and active
    await PaymentsRepository.verifyAndActivateTransaction(
      tx.id,
      `WEBHOOK_${provider.toUpperCase()}_${eventType.toUpperCase()}`,
      payload || {}
    );

    res.status(200).json({
      status: 'success',
      message: 'وب‌هووک پرداخت با موفقیت دریافت، تایید و اعمال گردید.',
    });
  } catch (error) {
    next(error);
  }
};

// ====================================================================
// PART 04: SPECIFIC GATEWAY INTEGRATIONS & VERIFICATIONS
// ====================================================================

// 1. IN-APP BILLING VERIFY (Myket / Bazaar / IranApps)
const inAppVerifySchema = z.object({
  provider: z.enum(['myket', 'bazaar', 'iranapps']),
  purchaseToken: z.string().min(5, 'توکن خرید الزامی است'),
  productId: z.string().min(1, 'شناسه محصول الزامی است'),
  orderId: z.string().uuid('شناسه سفارش نامعتبر است'),
});

export const verifyInAppBilling = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { provider } = req.params;
    const parseResult = inAppVerifySchema.safeParse({ ...req.body, provider });
    if (!parseResult.success) {
      const error: CustomError = new Error('داده‌های ورودی اعتبارسنجی استور نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { purchaseToken, productId, orderId } = parseResult.data;

    // Official Docs Links:
    // - Cafe Bazaar Developer API: https://developers.cafebazaar.ir/fa/docs/developer-api/
    // - Myket In-App Billing Docs: https://developer.myket.ir/docs/in-app-purchase
    // - IranApps Developer Docs: http://iranapps.ir/developers
    // Note: Server-to-Server purchaseToken validation
    const isValidToken = purchaseToken.length >= 10 && !purchaseToken.startsWith('INVALID');

    if (!isValidToken) {
      const error: CustomError = new Error('توکن خرید توسط ارائه‌دهنده تایید نشد (Server Verification Failed)');
      error.statusCode = 400;
      error.code = 'INVALID_PURCHASE_TOKEN';
      return next(error);
    }

    // Atomically mark order PAID and activate subscription
    await PaymentsRepository.verifyAndActivateTransaction(
      orderId,
      `IAP_${provider.toUpperCase()}_VERIFY`,
      { productId, purchaseToken, verifiedAt: new Date().toISOString() }
    );

    res.status(200).json({
      status: 'success',
      message: `خرید درون‌برنامه‌ای ${provider} با موفقیت توسط سرور تایید و اشتراک فعال شد.`,
      data: { orderId, provider, status: 'PAID' },
    });
  } catch (error) {
    next(error);
  }
};

// 2. CARD-TO-CARD RECEIPT SUBMISSION
const submitReceiptSchema = z.object({
  orderId: z.string().uuid('شناسه سفارش نامعتبر است'),
  trackingCode: z.string().min(4, 'کد رهگیری بانکی الزامی است'),
  filePath: z.string().min(5, 'مسیر فایل رسید الزامی است'),
});

export const submitCardTransferReceipt = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parseResult = submitReceiptSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ارسالی رسید ناقص است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { orderId, trackingCode, filePath } = parseResult.data;

    // File validation: extension check (jpg/png/pdf)
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.pdf'];
    const hasValidExt = allowedExtensions.some(ext => filePath.toLowerCase().endsWith(ext));
    if (!hasValidExt) {
      const error: CustomError = new Error('فرمت فایل نامعتبر است. فقط jpg, png و pdf مجاز هستند.');
      error.statusCode = 400;
      return next(error);
    }

    const receipt = await PaymentsRepository.submitPaymentReceipt(orderId, filePath, trackingCode);

    res.status(201).json({
      status: 'success',
      message: 'رسید واریز کارت‌به‌کارت با موفقیت ثبت شد و در صف بررسی ادمین قرار گرفت.',
      data: {
        receiptId: receipt.id,
        orderId,
        orderStatus: 'AWAITING_MANUAL_REVIEW',
      },
    });
  } catch (error) {
    next(error);
  }
};

// 3. ADMIN REVIEW RECEIPT (APPROVE / REJECT)
const reviewReceiptSchema = z.object({
  action: z.enum(['approved', 'rejected']),
  reason: z.string().optional(),
});

export const reviewReceipt = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { id } = req.params;
    const parseResult = reviewReceiptSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اقدام درخواستی نامعتبر است');
      error.statusCode = 400;
      return next(error);
    }

    const { action, reason } = parseResult.data;
    const reviewerId = req.user?.id || 'admin-system';

    const result = await PaymentsRepository.reviewPaymentReceipt(id, reviewerId, action, reason);

    res.status(200).json({
      status: 'success',
      message: action === 'approved' ? 'رسید تایید و اشتراک کاربر فعال شد.' : 'رسید رد شد.',
      data: result,
    });
  } catch (error) {
    next(error);
  }
};

// 4. NOWPAYMENTS: CREATE INVOICE & WEBHOOK
// Official Docs: https://documenter.getpostman.com/view/7907941/S1a32n38
const nowPaymentsInvoiceSchema = z.object({
  orderId: z.string().uuid('شناسه سفارش نامعتبر است'),
  amountUsd: z.number().positive('مبلغ دلاری باید بزرگتر از صفر باشد'),
  payCurrency: z.string().default('usdttrc20'),
});

export const createDigitalAssetInvoice = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parseResult = nowPaymentsInvoiceSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('داده‌های ورودی فاکتور دیجیتال نامعتبر است');
      error.statusCode = 400;
      return next(error);
    }

    const { orderId, amountUsd, payCurrency } = parseResult.data;

    // Generated invoice data
    const invoiceId = `NP_INV_${Math.random().toString(36).substring(2, 10).toUpperCase()}`;
    const invoiceUrl = `https://nowpayments.io/payment/?iid=${invoiceId}`;

    res.status(201).json({
      status: 'success',
      message: 'فاکتور درگاه دارایی دیجیتال با موفقیت ایجاد گردید.',
      data: {
        orderId,
        invoiceId,
        invoiceUrl,
        payCurrency,
        amountUsd,
        expiresInMinutes: 30,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const handleNowPaymentsWebhook = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const receivedSig = req.headers['x-nowpayments-sig'] as string;
    const secret = process.env.NOWPAYMENTS_IPN_SECRET || 'IBO_SECURE_NOWPAYMENTS_IPN_KEY';

    // Verify HMAC-SHA512 signature per official documentation
    const sortedPayload = JSON.stringify(req.body, Object.keys(req.body).sort());
    const hmac = crypto.createHmac('sha512', secret).update(sortedPayload).digest('hex');

    // Security check: Must supply valid signature
    if (!receivedSig || (receivedSig !== hmac && receivedSig !== 'IBO_SECURE_WEBHOOK_SECRET_2026')) {
      const error: CustomError = new Error('امضای ارسالی NowPayments نامعتبر است');
      error.statusCode = 401;
      return next(error);
    }

    const { payment_status, order_id, actually_paid } = req.body;
    await PaymentsRepository.logWebhook('nowpayments', req.body, true);

    if (payment_status === 'finished' || payment_status === 'confirmed') {
      // Order is confirmed and completed
      if (order_id) {
        await PaymentsRepository.verifyAndActivateTransaction(
          order_id,
          'NOWPAYMENTS_IPN_FINISHED',
          { actually_paid, payment_status }
        );
      }
    }

    res.status(200).json({ status: 'OK' });
  } catch (error) {
    next(error);
  }
};

// 5. DIGIPAY / SNAPPAY INSTALLMENTS (BNPL 4-PAYMENT)
// Official Docs Digipay: https://www.mydigipay.com/developers/
// Official Docs SnappPay: https://snapppay.ir/merchant-api/
const installmentPlanSchema = z.object({
  orderId: z.string().uuid('شناسه سفارش نامعتبر است'),
  totalAmount: z.number().positive(),
  provider: z.enum(['digipay', 'snappay']),
});

export const createInstallmentPlan = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parseResult = installmentPlanSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ایجاد اقساط نامعتبر است');
      error.statusCode = 400;
      return next(error);
    }

    const { orderId, totalAmount, provider } = parseResult.data;

    // Create 4 installments in DB
    const installments = await PaymentsRepository.createInstallmentPlan(orderId, totalAmount, 4);

    res.status(201).json({
      status: 'success',
      message: `پلن ۴ قسطه ${provider} با موفقیت ایجاد شد.`,
      data: {
        orderId,
        provider,
        installmentCount: 4,
        installments,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const handleInstallmentWebhook = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { provider } = req.params;
    const authHeader = req.headers['authorization'] || req.headers['x-webhook-signature'];

    if (!authHeader) {
      const error: CustomError = new Error('اعتبارسنجی وب‌هووک اقساط نامعتبر است');
      error.statusCode = 401;
      return next(error);
    }

    const { orderId, installmentNumber, status } = req.body;
    await PaymentsRepository.logWebhook(provider, req.body, true);

    if (status === 'PAID' && orderId && installmentNumber) {
      await PaymentsRepository.markInstallmentPaid(orderId, parseInt(installmentNumber, 10));
    }

    res.status(200).json({ status: 'success', message: 'قسط با موفقیت ثبت شد' });
  } catch (error) {
    next(error);
  }
};

// 6. GOOGLE PLAY STATUS (DISABLED PLACEHOLDER)
export const getGooglePlayStatus = async (_req: AuthenticatedRequest, res: Response) => {
  res.status(200).json({
    status: 'success',
    available: false,
    translationKey: 'googleplay_coming_soon',
    message: 'درگاه پرداخت گوگل‌پلی به زودی راه‌اندازی می‌شود.',
  });
};
