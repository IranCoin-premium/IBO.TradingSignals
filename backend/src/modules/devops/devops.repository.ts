import { query } from '../../config/database';

export interface ReleaseAuditEntity {
  release_id: string;
  version: string;
  environment: string;
  commit_sha: string;
  status: string;
  deployed_by: string;
  metadata: any;
  created_at: Date;
}

export class DevopsRepository {
  static async recordRelease(
    releaseId: string,
    version: string,
    environment: string,
    commitSha: string,
    status: string,
    deployedBy: string,
    metadata: any
  ): Promise<ReleaseAuditEntity> {
    const res = await query(
      `INSERT INTO release_audit_logs (release_id, version, environment, commit_sha, status, deployed_by, metadata, created_at)
       VALUES ($1, $2, $3, $4, $5, $6, $7, CURRENT_TIMESTAMP)
       RETURNING *`,
      [releaseId, version, environment, commitSha, status || 'SUCCESS', deployedBy, JSON.stringify(metadata)]
    );
    return res.rows[0];
  }

  static async getReleases(): Promise<ReleaseAuditEntity[]> {
    const res = await query('SELECT * FROM release_audit_logs ORDER BY created_at DESC LIMIT 50');
    return res.rows;
  }
}
