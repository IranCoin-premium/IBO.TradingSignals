import { Response, NextFunction } from 'express';
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
