import { Response, NextFunction } from 'express';
import { z } from 'zod';
import { SignalsRepository } from './signals.repository';
import { AuthenticatedRequest } from '../../middleware/auth';
import { CustomError } from '../../middleware/error';
import { query } from '../../config/database';

// Schema validation for signal creations
const createSignalSchema = z.object({
  title: z.string().min(2, 'عنوان سیگنال باید حداقل ۲ کاراکتر باشد'),
  asset: z.string().min(2, 'نماد معاملاتی الزامی است'),
  entryPrice: z.number().positive('قیمت ورود باید یک عدد مثبت باشد'),
  direction: z.enum(['CALL', 'PUT'], { message: 'جهت معامله باید CALL یا PUT باشد' }),
  rationale: z.string().optional(),
});

export const getSignals = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const signals = await SignalsRepository.getActiveSignals();
    res.status(200).json({
      status: 'success',
      data: signals,
    });
  } catch (error) {
    next(error);
  }
};

export const createSignal = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر تایید صلاحیت نشده است');
      error.statusCode = 401;
      return next(error);
    }

    const parseResult = createSignalSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات سیگنال نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { title, asset, entryPrice, direction, rationale } = parseResult.data;

    const newSignal = await SignalsRepository.createSignal(
      title,
      asset,
      entryPrice,
      direction,
      rationale || null
    );

    // Audit Log Entry
    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
       VALUES ($1, 'AI_AGENT', 'SIGNAL_PUBLISHED', 'signals', $2, 'SUCCESS')`,
      [req.user.id, newSignal.id]
    );

    res.status(201).json({
      status: 'success',
      data: newSignal,
    });
  } catch (error) {
    next(error);
  }
};
