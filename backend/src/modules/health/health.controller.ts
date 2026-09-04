import { Request, Response, NextFunction } from 'express';
import { checkConnection, query } from '../../config/database';
import { logger } from '../../utils/logger';

export const getHealth = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const dbConnected = await checkConnection();
    let migrationsApplied = false;

    if (dbConnected) {
      try {
        const result = await query('SELECT COUNT(*) FROM schema_migrations');
        migrationsApplied = parseInt(result.rows[0].count, 10) > 0;
      } catch (err) {
        logger.warn('Failed to query migrations count during health check', err);
      }
    }

    const healthStatus = {
      status: dbConnected ? 'ok' : 'error',
      service: 'backend',
      timestamp: new Date().toISOString(),
      database: {
        status: dbConnected ? 'healthy' : 'unhealthy',
        migrations: migrationsApplied ? 'synchronized' : 'pending_or_missing',
      },
    };

    if (!dbConnected) {
      return res.status(503).json(healthStatus);
    }

    res.status(200).json(healthStatus);
  } catch (error) {
    next(error);
  }
};
