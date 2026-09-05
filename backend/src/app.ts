import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { errorHandler } from './middleware/error';
import { getHealth } from './modules/health/health.controller';
import { register, login, getProfile } from './modules/users/users.controller';
import { getPlans, getCurrentSubscription } from './modules/subscriptions/subscriptions.controller';
import { checkout, verifyPayment, createPurchaseIntent, getPurchaseIntent, cancelSubscription, issueRefund, handleWebhook, handleNowPaymentsWebhook } from './modules/payments/payments.controller';
import { getSignals, createSignal } from './modules/signals/signals.controller';
import { scanSecurity } from './modules/admin/admin.controller';
import agentsRouter from './modules/agents/agents.routes';
import adminRouter from './modules/admin/admin.routes';
import supportRouter from './modules/support/support.routes';
import devopsRouter from './modules/devops/devops.routes';
import paymentsRouter from './modules/payments/payments.routes';
import { authenticateToken, requireRoles } from './middleware/auth';
import { logger } from './utils/logger';

dotenv.config();

const app = express();

// Secure client origin constraints (strict defaults, no open permissive asterisks)
const allowedOrigins = (process.env.CORS_ALLOWED_ORIGINS || 'http://localhost:3000,http://127.0.0.1:3000').split(',');
app.use(cors({
  origin: (origin, callback) => {
    if (!origin || allowedOrigins.includes(origin) || process.env.APP_ENV === 'test') {
      callback(null, true);
    } else {
      callback(new Error('درخواست به دلیل محدودیت CORS رد شد.'));
    }
  },
  credentials: true,
}));

app.use(express.json({ limit: '10mb' }));

// Structured Request Logger Middleware
app.use((req, res, next) => {
  const start = Date.now();
  const requestId = req.headers['x-request-id'] || `req_${Math.random().toString(36).substring(2, 11)}`;
  res.setHeader('x-request-id', requestId);

  res.on('finish', () => {
    const duration = Date.now() - start;
    logger.info(`HTTP ${req.method} ${req.originalUrl}`, {
      method: req.method,
      url: req.originalUrl,
      status: res.statusCode,
      latency: `${duration}ms`,
      requestId,
    });
  });
  next();
});

const prefix = process.env.API_PREFIX || '/api/v1';

// HEALTH API
app.get(`${prefix}/health`, getHealth);

// AUTH / IDENTITY APIS
app.post(`${prefix}/auth/register`, register);
app.post(`${prefix}/auth/login`, login);
app.get(`${prefix}/auth/profile`, authenticateToken as any, getProfile as any);

// SUBSCRIPTIONS APIS
app.get(`${prefix}/subscriptions/plans`, getPlans);
app.get(`${prefix}/subscriptions/current`, authenticateToken as any, getCurrentSubscription as any);

// PAYMENTS APIS
app.post(`${prefix}/payments/checkout`, authenticateToken as any, checkout as any);
app.post(`${prefix}/payments/verify`, authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN', 'SERVICE_AGENT']) as any, verifyPayment as any);
app.post(`${prefix}/purchases`, authenticateToken as any, createPurchaseIntent as any);
app.get(`${prefix}/purchases/:id`, authenticateToken as any, getPurchaseIntent as any);
app.post(`${prefix}/subscriptions/:id/cancel`, authenticateToken as any, cancelSubscription as any);
app.post(`${prefix}/payments/webhooks/:provider`, handleWebhook as any);
app.post(`${prefix}/admin/subscriptions/refund`, authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, issueRefund as any);
app.use(`${prefix}/payments`, paymentsRouter);
app.use(`${prefix}`, paymentsRouter);

// SIGNALS APIS
app.get(`${prefix}/signals`, authenticateToken as any, getSignals as any);
app.post(`${prefix}/signals/create`, authenticateToken as any, requireRoles(['ADMIN', 'STAFF', 'SERVICE_AGENT']) as any, createSignal as any);

// ADMIN SECURITY & SECRETS APIS
app.use(`${prefix}/admin`, adminRouter);

// AGENT ORCHESTRATION & GOVERNANCE APIS
app.use(`${prefix}/agents`, agentsRouter);

// SUPPORT & LOCALIZATION APIS
app.use(`${prefix}/support`, supportRouter);

// DEVOPS & OBSERVABILITY APIS
app.use(`${prefix}/devops`, devopsRouter);

// 404 handler
app.use((req, res, next) => {
  res.status(404).json({
    status: 'error',
    code: 'NOT_FOUND',
    message: 'مسیر درخواستی یافت نشد',
  });
});

// Centralized error handler
app.use(errorHandler as any);

export default app;
