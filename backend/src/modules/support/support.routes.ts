import { Router } from 'express';
import { SupportController } from './support.controller';
import { authenticateToken } from '../../middleware/auth';

const router = Router();

router.post('/tickets', authenticateToken as any, SupportController.createTicket);
router.get('/tickets', authenticateToken as any, SupportController.getTickets);

export default router;
