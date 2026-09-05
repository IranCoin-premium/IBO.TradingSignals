import { Router } from 'express';
import { SupportController } from './support.controller';
import { authenticateToken } from '../../middleware/auth';

const router = Router();

// Standard Tickets
router.post('/tickets', authenticateToken as any, SupportController.createTicket);
router.get('/tickets', authenticateToken as any, SupportController.getTickets);

// Part 07: 24/7 AI Multilingual Support Chat
router.post('/chat', SupportController.chatWithAi);

// Part 07: Geo-IP Language Detection and Localization APIs
router.get('/localization/detect', SupportController.detectGeoLanguage);
router.get('/localization/languages', SupportController.getLanguages);
router.get('/localization/translations/:lang', SupportController.getTranslations);

export default router;
