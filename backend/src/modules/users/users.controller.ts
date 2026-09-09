import { Request, Response, NextFunction } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { z } from 'zod';
import { UsersRepository } from './users.repository';
import { CustomError } from '../../middleware/error';
import { AuthenticatedRequest } from '../../middleware/auth';
import { query } from '../../config/database';

// Input DTO Validation Schemas
const registerSchema = z.object({
  email: z.string().email('آدرس ایمیل وارد شده نامعتبر است'),
  password: z.string().min(8, 'رمز عبور باید حداقل ۸ کاراکتر باشد'),
  displayName: z.string().min(2, 'نام نمایشی باید حداقل ۲ کاراکتر باشد'),
  locale: z.string().optional().default('fa'),
});

const loginSchema = z.object({
  email: z.string().email('آدرس ایمیل وارد شده نامعتبر است'),
  password: z.string().min(1, 'رمز عبور الزامی است'),
});

export const register = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const parseResult = registerSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ورودی نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { email, password, displayName, locale } = parseResult.data;

    // Check if user already exists
    const existingUser = await UsersRepository.findByEmail(email);
    if (existingUser) {
      const error: CustomError = new Error('کاربری با این آدرس ایمیل قبلاً ثبت‌نام کرده است');
      error.statusCode = 409;
      error.code = 'EMAIL_ALREADY_EXISTS';
      return next(error);
    }

    // Secure adaptive password hashing
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const newUser = await UsersRepository.createUser(email, displayName, passwordHash, locale);
    const roles = await UsersRepository.getUserRoles(newUser.id);

    // Audit Log Entry
    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
       VALUES ($1, 'USER', 'USER_REGISTERED', 'users', $2, 'SUCCESS')`,
      [newUser.id, newUser.id]
    );

    res.status(201).json({
      status: 'success',
      data: {
        id: newUser.id,
        email: newUser.email,
        displayName: newUser.display_name,
        accountStatus: newUser.account_status,
        roles,
        locale: newUser.locale,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const login = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const parseResult = loginSchema.safeParse(req.body);
    if (!parseResult.success) {
      const error: CustomError = new Error('اطلاعات ورود نامعتبر است');
      error.statusCode = 400;
      error.code = 'VALIDATION_ERROR';
      error.details = parseResult.error.flatten().fieldErrors;
      return next(error);
    }

    const { email, password } = parseResult.data;

    const user = await UsersRepository.findByEmailWithHash(email);
    if (!user) {
      const error: CustomError = new Error('ایمیل یا رمز عبور اشتباه است');
      error.statusCode = 401;
      error.code = 'INVALID_CREDENTIALS';
      return next(error);
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      const error: CustomError = new Error('ایمیل یا رمز عبور اشتباه است');
      error.statusCode = 401;
      error.code = 'INVALID_CREDENTIALS';
      return next(error);
    }

    const roles = await UsersRepository.getUserRoles(user.id);

    // Sign secure JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email, roles },
      process.env.JWT_SECRET || 'super_secret_jwt_sign_key_change_me_in_production',
      { expiresIn: process.env.JWT_EXPIRES_IN || '24h' } as any
    );

    // Audit Log Entry
    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result) 
       VALUES ($1, 'USER', 'USER_LOGIN', 'users', $2, 'SUCCESS')`,
      [user.id, user.id]
    );

    res.status(200).json({
      status: 'success',
      data: {
        token,
        user: {
          id: user.id,
          email: user.email,
          displayName: user.display_name,
          accountStatus: user.account_status,
          roles,
          locale: user.locale,
        },
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getProfile = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      const error: CustomError = new Error('کاربر یافت نشد');
      error.statusCode = 404;
      return next(error);
    }

    const user = await UsersRepository.findById(req.user.id);
    if (!user) {
      const error: CustomError = new Error('کاربر یافت نشد');
      error.statusCode = 404;
      return next(error);
    }

    const roles = await UsersRepository.getUserRoles(user.id);

    res.status(200).json({
      status: 'success',
      data: {
        id: user.id,
        email: user.email,
        displayName: user.display_name,
        accountStatus: user.account_status,
        roles,
        locale: user.locale,
      },
    });
  } catch (error) {
    next(error);
  }
};
