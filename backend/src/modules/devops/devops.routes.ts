import { Router } from 'express';
import { DevopsController } from './devops.controller';
import { authenticateToken, requireRoles } from '../../middleware/auth';

const router = Router();

router.post('/releases', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, DevopsController.recordRelease);
router.get('/releases', authenticateToken as any, requireRoles(['ADMIN', 'SUPER_ADMIN']) as any, DevopsController.getReleases);
router.get('/metrics', DevopsController.getHealthMetrics);

export default router;
