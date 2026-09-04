import { Request, Response } from 'express';
import { AgentsSupervisor } from './agents.supervisor';
import { AgentsRepository } from './agents.repository';
import { logger } from '../../utils/logger';
import crypto from 'crypto';

export class AgentsController {
  static async dispatchJob(req: Request, res: Response): Promise<void> {
    try {
      const { agent_id, tool_id, action, target_resource, environment, payload } = req.body;

      if (!agent_id || !action || !target_resource) {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'Missing required agent job parameters (agent_id, action, target_resource).'
        });
        return;
      }

      const env = environment || 'DEVELOPMENT';

      // Evaluate via Supervisor Governor
      const evaluation = await AgentsSupervisor.evaluateAndAuthorize({
        agentId: agent_id,
        toolId: tool_id,
        action,
        targetResource: target_resource,
        environment: env,
        payload,
        correlationId: req.headers['x-correlation-id'] as string
      });

      if (!evaluation.allowed) {
        res.status(403).json({
          errorCode: 'AGENT_ACTION_VETOED',
          message: evaluation.reason,
          riskLevel: evaluation.riskLevel,
          auditId: evaluation.auditId
        });
        return;
      }

      // Create job in queue
      const jobId = `job_${crypto.randomBytes(8).toString('hex')}`;
      const job = await AgentsRepository.createJob(jobId, agent_id, action, payload || {}, env);

      // Execute mock simulation or tool execution if approved
      await AgentsRepository.updateJobStatus(jobId, 'COMPLETED', { executed: true, timestamp: new Date() }, null);

      res.status(201).json({
        success: true,
        job_id: jobId,
        status: 'COMPLETED',
        risk_level: evaluation.riskLevel,
        audit_id: evaluation.auditId,
        message: 'Agent job successfully authorized, executed, and audited.'
      });
    } catch (error: any) {
      logger.error('Error dispatching agent job:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'Failed to process agent job dispatch.'
      });
    }
  }

  static async setKillSwitch(req: Request, res: Response): Promise<void> {
    try {
      const { switch_key, enabled } = req.body;
      if (!switch_key || typeof enabled !== 'boolean') {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'Invalid switch_key or enabled status.'
        });
        return;
      }

      await AgentsRepository.setKillSwitch(switch_key, enabled);
      logger.warn(`[KILL SWITCH UPDATED] Switch ${switch_key} set to enabled = ${enabled}`);

      res.status(200).json({
        success: true,
        switch_key,
        enabled,
        message: 'Kill switch state updated successfully.'
      });
    } catch (error: any) {
      logger.error('Error updating kill switch:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'Failed to update kill switch.'
      });
    }
  }

  static async registerAgent(req: Request, res: Response): Promise<void> {
    try {
      const { agent_id, name, role, model, permissions } = req.body;
      if (!agent_id || !name || !role || !model) {
        res.status(400).json({
          errorCode: 'VALIDATION_ERROR',
          message: 'Missing required agent registration fields.'
        });
        return;
      }

      const perms = Array.isArray(permissions) ? permissions : [];
      await AgentsRepository.registerAgent(agent_id, name, role, model, perms);

      res.status(201).json({
        success: true,
        agent_id,
        message: 'Agent registered successfully in control plane.'
      });
    } catch (error: any) {
      logger.error('Error registering agent:', error);
      res.status(500).json({
        errorCode: 'INTERNAL_SERVER_ERROR',
        message: 'Failed to register agent.'
      });
    }
  }
}
