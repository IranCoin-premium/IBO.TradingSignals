import { Response, NextFunction } from 'express';
import { SubscriptionsRepository } from './subscriptions.repository';
import { AuthenticatedRequest } from '../../middleware/auth';
import { CustomError } from '../../middleware/error';

export const getPlans = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const plans = await SubscriptionsRepository.getPlans();
    res.status(200).json({
      status: 'success',
      data: plans,
    });
  } catch (error) {
    next(error);
  }
};

export const getCurrentSubscription = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر تایید صلاحیت نشده است');
      error.statusCode = 401;
      return next(error);
    }

    const subscription = await SubscriptionsRepository.getActiveSubscription(req.user.id);
    const entitlements = await SubscriptionsRepository.getUserEntitlements(req.user.id);

    res.status(200).json({
      status: 'success',
      data: {
        subscription,
        entitlements: entitlements.map(e => e.feature_key),
      },
    });
  } catch (error) {
    next(error);
  }
};
