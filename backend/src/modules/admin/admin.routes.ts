import { Router } from 'express';
import { authenticateToken, requireRoles } from '../../middleware/auth';
import { listAdminSecrets, updateAdminSecret } from './secrets.controller';
import { scanSecurity } from './admin.controller';

const router = Router();

// Require authenticated human admin for all secret and admin operations
router.use(authenticateToken as any);
router.use(requireRoles(['ADMIN', 'SUPER_ADMIN']) as any);

// Secrets & AI Assistants Management Endpoints
router.get('/secrets', listAdminSecrets as any);
router.post('/secrets/:key_name', updateAdminSecret as any);

// Security scanning
router.post('/security/scan', scanSecurity as any);

export default router;
