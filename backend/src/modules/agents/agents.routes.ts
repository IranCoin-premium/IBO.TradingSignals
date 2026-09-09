import { Router } from 'express';
import { AgentsController } from './agents.controller';
import {
  getAgentAutonomyDetails,
  updateAgentAutonomySettings,
  rollbackAgentInstruction,
  triggerSelfTuningRun
} from './autonomous.controller';
import { authenticateToken, requireRoles } from '../../middleware/auth';

const router = Router();

// Agent Job Dispatch (Authenticated service/admin)
router.post('/jobs', authenticateToken as any, AgentsController.dispatchJob);

// Agent Registration & Kill Switch Management (Admin protected)
router.post('/register', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, AgentsController.registerAgent);
router.post('/kill-switch', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, AgentsController.setKillSwitch);

// Part 11: Dedicated Autonomous Agent & Self-Tuning Endpoints (Admin protected)
router.get('/autonomy/:agent_name', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, getAgentAutonomyDetails);
router.put('/autonomy/:agent_name/settings', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, updateAgentAutonomySettings);
router.post('/autonomy/:agent_name/rollback/:version_number', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, rollbackAgentInstruction);
router.post('/autonomy/:agent_name/self-tune', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, triggerSelfTuningRun);

export default router;
