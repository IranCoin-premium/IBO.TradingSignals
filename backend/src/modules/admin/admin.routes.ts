import { Router } from 'express';
import { authenticateToken, requireRoles } from '../../middleware/auth';
import { listAdminSecrets, updateAdminSecret } from './secrets.controller';
import { scanSecurity, getControlCenterOverview, chatWithChiefAgent, executeCodingWorkerTask, getKnowledgeOverview, getOrchestrationOverview, getMultiAgentOverview, getSecurityGovernanceOverview, getResilienceOverview, getQualityScorecardOverview, getCEOExecutiveOverview, getIntegrationReconciliationOverview, getProductionReleaseOverview, getDataGovernanceOverview, getProductAnalyticsOverview, getGlobalGrowthOverview, getBillingOverview, getMobileDeliveryOverview, getAutonomousMasterOverview } from './admin.controller';

const router = Router();

// Require authenticated human admin for all secret and admin operations
router.use(authenticateToken as any);
router.use(requireRoles(['ADMIN', 'SUPER_ADMIN']) as any);

// Secrets & AI Assistants Management Endpoints
router.get('/secrets', listAdminSecrets as any);
router.post('/secrets/:key_name', updateAdminSecret as any);

// Security scanning
router.post('/security/scan', scanSecurity as any);

// Control Center & Chief Agent Coordinator
router.get('/control-center/overview', getControlCenterOverview as any);
router.post('/control-center/chief-agent/chat', chatWithChiefAgent as any);

// Coding Worker Execution Endpoint
router.post('/control-center/coding-worker/execute', executeCodingWorkerTask as any);

// Knowledge Observability & RAG Overview Endpoint
router.get('/control-center/knowledge/overview', getKnowledgeOverview as any);

// Orchestration & 24/7 Worker Health Dashboard Endpoint
router.get('/control-center/orchestration/overview', getOrchestrationOverview as any);

// Multi-Agent Architecture & Specialist Registry Endpoint
router.get('/control-center/multi-agent/overview', getMultiAgentOverview as any);

// Zero-Trust Security & Policy Governance Overview Endpoint
router.get('/control-center/security/overview', getSecurityGovernanceOverview as any);

// Runtime Resilience, Incidents & System Health Overview Endpoint
router.get('/control-center/resilience/overview', getResilienceOverview as any);

// Quality Engineering, Test Registry & Release Gate Overview Endpoint
router.get('/control-center/quality/overview', getQualityScorecardOverview as any);

// CEO / Admin Control Plane & Governance Command Center Overview Endpoint
router.get('/control-center/ceo/overview', getCEOExecutiveOverview as any);

// End-to-End Integration, Contracts & State Reconciliation Overview Endpoint
router.get('/control-center/integration/reconciliation', getIntegrationReconciliationOverview as any);

// Production Readiness, CI/CD Pipelines & Deployment Governance Overview Endpoint
router.get('/control-center/release/overview', getProductionReleaseOverview as any);

// Data Architecture, Quality, Backup & Disaster Recovery Overview Endpoint
router.get('/control-center/data/overview', getDataGovernanceOverview as any);

// Product Analytics, Experimentation & KPI Governance Overview Endpoint
router.get('/control-center/analytics/overview', getProductAnalyticsOverview as any);

// Global Growth Engine, International SEO & Content Overview Endpoint
router.get('/control-center/growth/overview', getGlobalGrowthOverview as any);

// Billing, Subscriptions, Entitlements & Financial Reconciliation Overview Endpoint
router.get('/control-center/billing/overview', getBillingOverview as any);

// Mobile Delivery, PWA/Native Parity & Store Readiness Overview Endpoint
router.get('/control-center/mobile/overview', getMobileDeliveryOverview as any);

// Autonomous Operating System, Continuous Improvement & Master Readiness Overview Endpoint
router.get('/control-center/autonomous/overview', getAutonomousMasterOverview as any);

export default router;
