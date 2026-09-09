import winston from 'winston';

const logFormat = winston.format.combine(
  winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
  winston.format.errors({ stack: true }),
  winston.format.json()
);

const sensitiveKeys = ['password', 'token', 'jwt', 'secret', 'key', 'auth'];

// Helper to sanitize logging payloads
const sanitizePayload = (info: any) => {
  const sanitized = { ...info };
  if (sanitized.message && typeof sanitized.message === 'object') {
    sanitized.message = sanitizeObject(sanitized.message);
  }
  return sanitized;
};

const sanitizeObject = (obj: any): any => {
  if (!obj || typeof obj !== 'object') return obj;
  const result = { ...obj };
  for (const key of Object.keys(result)) {
    if (sensitiveKeys.some(sk => key.toLowerCase().includes(sk))) {
      result[key] = '[REDACTED]';
    } else if (typeof result[key] === 'object') {
      result[key] = sanitizeObject(result[key]);
    }
  }
  return result;
};

export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format((info) => sanitizePayload(info))(),
    logFormat
  ),
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      ),
    }),
  ],
});
