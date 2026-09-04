import { Response, NextFunction } from 'express';
import { AuthenticatedRequest } from '../../middleware/auth';
import { SecurityGuardian } from '../../security/guardian/guardian';
import { query } from '../../config/database';

export const scanSecurity = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const guardian = new SecurityGuardian();
    const findings = guardian.runScan();

    const remediated = req.query.remediate === 'true' || req.body.remediate === true
      ? guardian.runAutoRemediation()
      : 0;

    // Log the security audit event securely
    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result, metadata) 
       VALUES ($1, 'USER', 'SECURITY_SCAN_EXECUTED', 'system', 'security_guardian', 'SUCCESS', $2)`,
      [
        req.user?.id || null,
        JSON.stringify({
          findings_count: findings.length,
          auto_remediated_count: remediated,
          request_ip: req.ip,
        }),
      ]
    );

    res.status(200).json({
      status: 'success',
      data: {
        timestamp: new Date().toISOString(),
        findings_count: findings.length,
        remediated_count: remediated,
        findings: findings.map(f => ({
          id: f.id,
          severity: f.severity,
          category: f.category,
          title: f.title,
          description: f.description,
          filePath: f.filePath,
          line: f.line,
          remediation: f.remediation,
          status: f.status,
        })),
      },
    });
  } catch (error) {
    next(error);
  }
};
