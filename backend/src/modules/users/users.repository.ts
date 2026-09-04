import { query } from '../../config/database';

export interface UserDbEntity {
  id: string;
  email: string;
  display_name: string | null;
  account_status: string;
  locale: string;
  created_at: Date;
  updated_at: Date;
}

export interface UserWithHash extends UserDbEntity {
  password_hash: string;
}

export class UsersRepository {
  static async findByEmail(email: string): Promise<UserDbEntity | null> {
    const result = await query(
      'SELECT id, email, display_name, account_status, locale, created_at, updated_at FROM users WHERE email = $1',
      [email.toLowerCase()]
    );
    return result.rowCount && result.rowCount > 0 ? result.rows[0] : null;
  }

  static async findByEmailWithHash(email: string): Promise<UserWithHash | null> {
    const result = await query(
      `SELECT u.id, u.email, u.display_name, u.account_status, u.locale, u.created_at, u.updated_at, p.password_hash 
       FROM users u
       JOIN password_hashes p ON u.id = p.user_id
       WHERE u.email = $1`,
      [email.toLowerCase()]
    );
    return result.rowCount && result.rowCount > 0 ? result.rows[0] : null;
  }

  static async findById(id: string): Promise<UserDbEntity | null> {
    const result = await query(
      'SELECT id, email, display_name, account_status, locale, created_at, updated_at FROM users WHERE id = $1',
      [id]
    );
    return result.rowCount && result.rowCount > 0 ? result.rows[0] : null;
  }

  static async createUser(email: string, displayName: string, passwordHash: string, locale: string = 'fa'): Promise<UserDbEntity> {
    const userResult = await query(
      `INSERT INTO users (email, display_name, locale) 
       VALUES ($1, $2, $3) 
       RETURNING id, email, display_name, account_status, locale, created_at, updated_at`,
      [email.toLowerCase(), displayName, locale]
    );
    
    const user = userResult.rows[0];

    // Separate hashes table entry
    await query(
      'INSERT INTO password_hashes (user_id, password_hash) VALUES ($1, $2)',
      [user.id, passwordHash]
    );

    // Assign default Regular USER role
    await query(
      'INSERT INTO user_roles (user_id, role) VALUES ($1, $2)',
      [user.id, 'USER']
    );

    return user;
  }

  static async getUserRoles(userId: string): Promise<string[]> {
    const result = await query(
      'SELECT role FROM user_roles WHERE user_id = $1',
      [userId]
    );
    return result.rows.map(r => r.role);
  }

  static async assignRole(userId: string, role: string): Promise<void> {
    await query(
      'INSERT INTO user_roles (user_id, role) VALUES ($1, $2) ON CONFLICT DO NOTHING',
      [userId, role]
    );
  }
}
