import { query } from '../../config/database';

export interface SignalDbEntity {
  id: string;
  title: string;
  asset: string;
  entry_price: number;
  current_price: number | null;
  direction: string;
  status: string;
  rationale: string | null;
  created_at: Date;
  updated_at: Date;
}

export class SignalsRepository {
  static async getActiveSignals(): Promise<SignalDbEntity[]> {
    const result = await query(
      'SELECT id, title, asset, entry_price, current_price, direction, status, rationale, created_at, updated_at FROM signals WHERE status = \'ACTIVE\' ORDER BY created_at DESC'
    );
    return result.rows.map(row => ({
      ...row,
      entry_price: parseFloat(row.entry_price),
      current_price: row.current_price ? parseFloat(row.current_price) : null,
    }));
  }

  static async createSignal(
    title: string,
    asset: string,
    entryPrice: number,
    direction: string,
    rationale: string | null = null
  ): Promise<SignalDbEntity> {
    const result = await query(
      `INSERT INTO signals (title, asset, entry_price, direction, rationale, status) 
       VALUES ($1, $2, $3, $4, $5, 'ACTIVE') 
       RETURNING id, title, asset, entry_price, current_price, direction, status, rationale, created_at, updated_at`,
      [title, asset, entryPrice, direction, rationale]
    );
    return {
      ...result.rows[0],
      entry_price: parseFloat(result.rows[0].entry_price),
      current_price: result.rows[0].current_price ? parseFloat(result.rows[0].current_price) : null,
    };
  }

  static async updateSignalStatus(id: string, status: string, currentPrice: number): Promise<void> {
    await query(
      'UPDATE signals SET status = $1, current_price = $2, updated_at = CURRENT_TIMESTAMP WHERE id = $3',
      [status, currentPrice, id]
    );
  }
}
