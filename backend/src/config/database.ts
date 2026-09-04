import { Pool } from 'pg';
import dotenv from 'dotenv';

dotenv.config();

const dbConfig = {
  host: process.env.DB_HOST || '127.0.0.1',
  port: parseInt(process.env.DB_PORT || '5432', 10),
  user: process.env.DB_USER || 'ibo_admin',
  password: process.env.DB_PASSWORD || 'ibo_secure_password_placeholder',
  database: process.env.DB_NAME || 'ibo_signals_db',
  ssl: process.env.DB_SSL === 'true' ? { rejectUnauthorized: false } : undefined,
  max: 10, // Maximum pool connections
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 5000,
};

// Singleton database connection pool
export const pool = new Pool(dbConfig);

// Helper to query with parameters
export const query = async (text: string, params?: any[]) => {
  return pool.query(text, params);
};

// Test-and-verify connectivity check
export const checkConnection = async (): Promise<boolean> => {
  try {
    const client = await pool.connect();
    client.release();
    return true;
  } catch (error: any) {
    console.error("DATABASE CONNECTION ERROR IN checkConnection:", error);
    return false;
  }
};
