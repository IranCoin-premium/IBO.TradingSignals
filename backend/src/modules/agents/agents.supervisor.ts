import { AgentsRepository } from './agents.repository';
import { logger } from '../../utils/logger';
import crypto from 'crypto';

export interface SupervisorEvaluationContext {
  agentId: string;
  toolId?: string;
  action: string;
  targetResource: string;
  environment: 'DEVELOPMENT' | 'STAGING' | 'PRODUCTION';
  payload?: any;
  correlationId?: string;
}

export interface SupervisorResult {
  allowed: boolean;
  reason: string;
  riskLevel: string;
  auditId?: string;
}

export class AgentsSupervisor {
  static async evaluateAndAuthorize(context: SupervisorEvaluationContext): Promise<SupervisorResult> {
    const correlationId = context.correlationId || `req_${crypto.randomBytes(6).toString('hex')}`;
    const auditId = `audit_${crypto.randomBytes(8).toString('hex')}`;

    // 1. Check Global Kill Switch
    const globalKill = await AgentsRepository.isKillSwitchEnabled('GLOBAL_KILL_SWITCH');
    if (globalKill) {
      logger.warn(`[AGENT SUPERVISOR] VETOED: Global Kill Switch is active! Action: ${context.action}`);
      await AgentsRepository.logAudit(
        auditId,
        context.agentId,
        null,
        context.action,
        context.targetResource,
        'R4',
        'VETOED',
        correlationId
      );
      return { allowed: false, reason: 'Global Kill Switch is active.', riskLevel: 'R4', auditId };
    }

    // 2. Check Production Write Kill Switch if environment is PRODUCTION
    if (context.environment === 'PRODUCTION' && (context.action.includes('WRITE') || context.action.includes('MUTATE') || context.action.includes('DEPLOY'))) {
      const prodKill = await AgentsRepository.isKillSwitchEnabled('PRODUCTION_WRITE_KILL_SWITCH');
      if (prodKill) {
        logger.warn(`[AGENT SUPERVISOR] VETOED: Production Write Kill Switch is active! Action: ${context.action}`);
        await AgentsRepository.logAudit(
          auditId,
          context.agentId,
          null,
          context.action,
          context.targetResource,
          'R4',
          'VETOED',
          correlationId
        );
        return { allowed: false, reason: 'Production Write Kill Switch is active.', riskLevel: 'R4', auditId };
      }
    }

    // 3. Verify Agent existence & status
    const agent = await AgentsRepository.findAgentById(context.agentId);
    if (!agent) {
      logger.error(`[AGENT SUPERVISOR] DENIED: Agent not found: ${context.agentId}`);
      return { allowed: false, reason: 'Agent not registered or found.', riskLevel: 'R3' };
    }
    if (agent.status !== 'ACTIVE') {
      logger.warn(`[AGENT SUPERVISOR] DENIED: Agent is inactive: ${context.agentId}`);
      return { allowed: false, reason: 'Agent is inactive or suspended.', riskLevel: 'R2' };
    }

    // 4. Verify Tool permissions & risk level if toolId is provided
    let riskLevel = 'R0';
    if (context.toolId) {
      const tool = await AgentsRepository.findToolById(context.toolId);
      if (!tool) {
        return { allowed: false, reason: `Tool not registered: ${context.toolId}`, riskLevel: 'R3' };
      }
      if (!tool.enabled) {
        return { allowed: false, reason: `Tool is disabled: ${context.toolId}`, riskLevel: 'R2' };
      }
      riskLevel = tool.risk_level;

      // Check if agent holds required permissions for the tool
      const hasPermission = tool.required_permissions.every(perm => agent.permissions.includes(perm));
      if (!hasPermission) {
        logger.warn(`[AGENT SUPERVISOR] DENIED: Agent ${agent.agent_id} lacks permissions for tool ${tool.tool_id}`);
        await AgentsRepository.logAudit(
          auditId,
          agent.agent_id,
          null,
          context.action,
          context.targetResource,
          riskLevel,
          'DENIED',
          correlationId
        );
        return { allowed: false, reason: `Agent lacks required permissions for tool: ${tool.tool_id}`, riskLevel, auditId };
      }
    }

    // 5. Approve execution
    await AgentsRepository.logAudit(
      auditId,
      agent.agent_id,
      null,
      context.action,
      context.targetResource,
      riskLevel,
      'APPROVED',
      correlationId
    );

    logger.info(`[AGENT SUPERVISOR] APPROVED: Agent ${agent.agent_id} action ${context.action} on ${context.targetResource}`);
    return { allowed: true, reason: 'Action authorized by supervisor governor.', riskLevel, auditId };
  }
}
