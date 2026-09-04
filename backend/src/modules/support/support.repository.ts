import { query } from '../../config/database';

export interface SupportTicketEntity {
  ticket_id: string;
  user_id: string;
  locale: string;
  subject: string;
  message: string;
  status: string;
  response: string | null;
  created_at: Date;
  updated_at: Date;
}

export class SupportRepository {
  static async createTicket(
    ticketId: string,
    userId: string,
    locale: string,
    subject: string,
    message: string
  ): Promise<SupportTicketEntity> {
    const res = await query(
      `INSERT INTO support_tickets (ticket_id, user_id, locale, subject, message, status, created_at, updated_at)
       VALUES ($1, $2, $3, $4, $5, 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
       RETURNING *`,
      [ticketId, userId, locale || 'fa', subject, message]
    );
    return res.rows[0];
  }

  static async getTicketsByUserId(userId: string): Promise<SupportTicketEntity[]> {
    const res = await query(
      `SELECT * FROM support_tickets WHERE user_id = $1 ORDER BY created_at DESC`,
      [userId]
    );
    return res.rows;
  }

  static async updateTicketResponse(
    ticketId: string,
    response: string,
    status: string
  ): Promise<SupportTicketEntity | null> {
    const res = await query(
      `UPDATE support_tickets 
       SET response = $2, status = $3, updated_at = CURRENT_TIMESTAMP 
       WHERE ticket_id = $1 
       RETURNING *`,
      [ticketId, response, status]
    );
    return res.rows.length ? res.rows[0] : null;
  }
}
