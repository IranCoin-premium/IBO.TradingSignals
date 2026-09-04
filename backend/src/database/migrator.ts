import fs from 'fs';
import path from 'path';
import { pool, query } from '../config/database';
import { logger } from '../utils/logger';

export const runMigrations = async (): Promise<boolean> => {
  logger.info('Starting PostgreSQL database migrations...');
  let client;
  try {
    client = await pool.connect();

    // 1. Create meta-table to track migrations
    await client.query(`
      CREATE TABLE IF NOT EXISTS schema_migrations (
        version VARCHAR(255) PRIMARY KEY,
        applied_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
      );
    `);

    // 2. Read migration files
    const migrationsDir = path.join(__dirname, 'migrations');
    const files = fs.readdirSync(migrationsDir)
      .filter(f => f.endsWith('.sql'))
      .sort(); // Sort so files execute sequentially (e.g., 001, 002)

    logger.info(`Discovered ${files.length} migration files in directory.`);

    for (const file of files) {
      // Check if migration was already applied
      const result = await client.query('SELECT 1 FROM schema_migrations WHERE version = $1', [file]);
      if (result.rowCount && result.rowCount > 0) {
        logger.debug(`Migration ${file} is already applied. Skipping.`);
        continue;
      }

      logger.info(`Applying database migration: ${file}...`);
      const sql = fs.readFileSync(path.join(migrationsDir, file), 'utf8');

      // Execute migration inside an atomic transaction
      await client.query('BEGIN');
      try {
        await client.query(sql);
        await client.query('INSERT INTO schema_migrations (version) VALUES ($1)', [file]);
        await client.query('COMMIT');
        logger.info(`Migration ${file} applied successfully.`);
      } catch (err) {
        await client.query('ROLLBACK');
        logger.error(`Failed to apply migration ${file}. Rolled back transaction.`);
        throw err;
      }
    }

    logger.info('All database migrations have been successfully synchronized.');
    return true;
  } catch (error: any) {
    logger.error('Critical Database Migration Failure:', error);
    return false;
  } finally {
    if (client) client.release();
  }
};

// Allow standalone execution of migrator
if (require.main === module) {
  runMigrations()
    .then(success => {
      process.exit(success ? 0 : 1);
    })
    .catch(() => {
      process.exit(1);
    });
}
