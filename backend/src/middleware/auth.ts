import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { CustomError } from './error';

export interface AuthenticatedRequest extends Request {
  user?: {
    id: string;
    email: string;
    roles: string[];
  };
}

export const authenticateToken = (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1]; // Bearer <token>

  if (!token) {
    const error: CustomError = new Error('دسترسی مجاز نیست. لطفا وارد حساب کاربری خود شوید.');
    error.statusCode = 401;
    error.code = 'UNAUTHORIZED';
    return next(error);
  }

  jwt.verify(token, process.env.JWT_SECRET || 'super_secret_jwt_sign_key_change_me_in_production', (err, decoded: any) => {
    if (err) {
      const error: CustomError = new Error('توکن ورود نامعتبر یا منقضی شده است.');
      error.statusCode = 403;
      error.code = 'FORBIDDEN';
      return next(error);
    }

    req.user = {
      id: decoded.id,
      email: decoded.email,
      roles: decoded.roles || [],
    };
    next();
  });
};

export const requireRoles = (allowedRoles: string[]) => {
  return (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
    if (!req.user) {
      const error: CustomError = new Error('احراز هویت انجام نشده است.');
      error.statusCode = 401;
      error.code = 'UNAUTHORIZED';
      return next(error);
    }

    const hasRole = req.user.roles.some(r => allowedRoles.includes(r));
    if (!hasRole) {
      const error: CustomError = new Error('شما دسترسی لازم برای انجام این عملیات را ندارید.');
      error.statusCode = 403;
      error.code = 'INSUFFICIENT_PERMISSIONS';
      return next(error);
    }

    next();
  };
};
