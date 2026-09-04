import { Request, Response, NextFunction } from 'express';
import { logger } from '../utils/logger';

export interface CustomError extends Error {
  statusCode?: number;
  code?: string;
  details?: any;
}

export const errorHandler = (
  err: CustomError,
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const statusCode = err.statusCode || 500;
  const errorCode = err.code || 'INTERNAL_SERVER_ERROR';
  const message = err.message || 'خطای غیرمنتظره‌ای در سرور رخ داده است';

  // Structured Error Logging
  logger.error('API Error Response:', {
    route: req.originalUrl,
    method: req.method,
    statusCode,
    errorCode,
    message,
    stack: process.env.APP_ENV === 'development' ? err.stack : undefined,
    details: err.details,
  });

  // Client payload
  res.status(statusCode).json({
    status: 'error',
    code: errorCode,
    message,
    details: process.env.APP_ENV === 'development' ? err.details : undefined,
  });
};
