import { Router } from 'express';
import { AgentsController } from './agents.controller';
import { authenticateToken, requireRoles } from '../../middleware/auth';

const router = Router();

// Agent Job Dispatch (Authenticated service/admin)
router.post('/jobs', authenticateToken as any, AgentsController.dispatchJob);

// Agent Registration & Kill Switch Management (Admin protected)
router.post('/register', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, AgentsController.registerAgent);
router.post('/kill-switch', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, AgentsController.setKillSwitch);

export default router;
