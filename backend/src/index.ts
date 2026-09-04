import app from './app';
import { runMigrations } from './database/migrator';
import { logger } from './utils/logger';

const port = process.env.APP_PORT || 3000;

const startServer = async () => {
  try {
    // Perform database migrations sequentially on startup
    const migrationsSucceeded = await runMigrations();
    if (!migrationsSucceeded && process.env.APP_ENV !== 'test') {
      logger.error('Failed to run database migrations. Shutting down application...');
      process.exit(1);
    }

    app.listen(port, () => {
      logger.info(`IBO Trading Signals backend monolith is running on port ${port} in [${process.env.APP_ENV || 'production'}] mode.`);
    });
  } catch (error) {
    logger.error('Unhandled Server Bootstrap Exception:', error);
    process.exit(1);
  }
};

startServer();
