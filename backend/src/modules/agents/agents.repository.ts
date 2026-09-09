import { query } from '../../config/database';

export interface AgentEntity {
  agent_id: string;
  name: string;
  role: string;
  model: string;
  status: string;
  permissions: string[];
  created_at: Date;
}

export interface AgentToolEntity {
  tool_id: string;
  name: string;
  risk_level: 'R0' | 'R1' | 'R2' | 'R3' | 'R4';
  required_permissions: string[];
  enabled: boolean;
  created_at: Date;
}

export interface AgentJobEntity {
  job_id: string;
  agent_id: string;
  task_type: string;
  payload: any;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'REJECTED';
  environment: 'DEVELOPMENT' | 'STAGING' | 'PRODUCTION';
  result: any;
  error: string | null;
  created_at: Date;
  completed_at: Date | null;
}

export interface AgentAuditEntity {
  audit_id: string;
  agent_id: string | null;
  job_id: string | null;
  action: string;
  target_resource: string;
  risk_level: string;
  outcome: 'APPROVED' | 'DENIED' | 'VETOED' | 'EXECUTED' | 'ROLLED_BACK';
  correlation_id: string | null;
  timestamp: Date;
}

export class AgentsRepository {
  static async findAgentById(agentId: string): Promise<AgentEntity | null> {
    const res = await query('SELECT * FROM agents WHERE agent_id = $1', [agentId]);
    return res.rows.length ? res.rows[0] : null;
  }

  static async findToolById(toolId: string): Promise<AgentToolEntity | null> {
    const res = await query('SELECT * FROM agent_tools WHERE tool_id = $1', [toolId]);
    return res.rows.length ? res.rows[0] : null;
  }

  static async isKillSwitchEnabled(switchKey: string): Promise<boolean> {
    const res = await query('SELECT enabled FROM agent_kill_switch WHERE switch_key = $1', [switchKey]);
    if (res.rows.length === 0) return false;
    return res.rows[0].enabled;
  }

  static async setKillSwitch(switchKey: string, enabled: boolean): Promise<void> {
    await query(
      `INSERT INTO agent_kill_switch (switch_key, enabled, updated_at) 
       VALUES ($1, $2, CURRENT_TIMESTAMP) 
       ON CONFLICT (switch_key) DO UPDATE SET enabled = $2, updated_at = CURRENT_TIMESTAMP`,
      [switchKey, enabled]
    );
  }

  static async registerAgent(
    agentId: string,
    name: string,
    role: string,
    model: string,
    permissions: string[]
  ): Promise<void> {
    await query(
      `INSERT INTO agents (agent_id, name, role, model, permissions, status) 
       VALUES ($1, $2, $3, $4, $5, 'ACTIVE') 
       ON CONFLICT (agent_id) DO UPDATE SET name = $2, role = $3, model = $4, permissions = $5`,
      [agentId, name, role, model, permissions]
    );
  }

  static async createJob(
    jobId: string,
    agentId: string,
    taskType: string,
    payload: any,
    environment: string
  ): Promise<AgentJobEntity> {
    const res = await query(
      `INSERT INTO agent_jobs (job_id, agent_id, task_type, payload, status, environment, created_at)
       VALUES ($1, $2, $3, $4, 'PENDING', $5, CURRENT_TIMESTAMP)
       RETURNING *`,
      [jobId, agentId, taskType, JSON.stringify(payload), environment]
    );
    return res.rows[0];
  }

  static async updateJobStatus(
    jobId: string,
    status: string,
    result: any = null,
    error: string | null = null
  ): Promise<void> {
    await query(
      `UPDATE agent_jobs 
       SET status = $2, result = $3, error = $4, completed_at = CURRENT_TIMESTAMP 
       WHERE job_id = $1`,
      [jobId, status, result ? JSON.stringify(result) : null, error]
    );
  }

  static async logAudit(
    auditId: string,
    agentId: string | null,
    jobId: string | null,
    action: string,
    targetResource: string,
    riskLevel: string,
    outcome: string,
    correlationId: string | null
  ): Promise<AgentAuditEntity> {
    const res = await query(
      `INSERT INTO agent_audit_logs (audit_id, agent_id, job_id, action, target_resource, risk_level, outcome, correlation_id, timestamp)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, CURRENT_TIMESTAMP)
       RETURNING *`,
      [auditId, agentId, jobId, action, targetResource, riskLevel, outcome, correlationId]
    );
    return res.rows[0];
  }
}
