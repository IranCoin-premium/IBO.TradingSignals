import { Response, NextFunction } from 'express';
import { AuthenticatedRequest } from '../../middleware/auth';
import { SecurityGuardian } from '../../security/guardian/guardian';
import { query } from '../../config/database';
import { ControlCenterService } from './control-center.service';
import { CodingAgentAdapter, WorkerStartRequest } from '../automation/coding-agent.adapter';
import { TaskOrchestrationEngine } from '../orchestration/orchestration.service';
import { MultiAgentOperatingArchitecture } from '../agents/multi-agent.service';
import { ZeroTrustPolicyEngine } from '../../security/policy/policy-engine.service';
import { RuntimeResilienceObservabilityService } from '../observability/resilience.service';
import { QualityEngineeringReleaseService } from '../quality/quality-engineering.service';
import { CEOAdminControlPlaneService } from './control-plane.service';
import { EndToEndIntegrationReconciliationService } from '../integration/reconciliation.service';
import { ProductionReleaseEngineeringService } from '../release/production-release.service';
import { DataArchitectureGovernanceService } from '../data/data-architecture.service';
import { ProductAnalyticsEngine } from '../analytics/product-analytics.service';
import { GlobalGrowthEngineService } from '../growth/global-growth.service';
import { BillingEntitlementEngine } from '../billing/billing.service';
import { MobileNativeDeliveryEngine } from '../mobile/mobile-delivery.service';
import { AutonomousOperatingSystem } from '../autonomous/autonomous-os.service';

export const scanSecurity = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const guardian = new SecurityGuardian();
    const findings = guardian.runScan();

    const remediated = req.query.remediate === 'true' || req.body.remediate === true
      ? guardian.runAutoRemediation()
      : 0;

    // Log the security audit event securely
    await query(
      `INSERT INTO audit_logs (actor_id, actor_type, action, resource_type, resource_id, result, metadata) 
       VALUES ($1, 'USER', 'SECURITY_SCAN_EXECUTED', 'system', 'security_guardian', 'SUCCESS', $2)`,
      [
        req.user?.id || null,
        JSON.stringify({
          findings_count: findings.length,
          auto_remediated_count: remediated,
          request_ip: req.ip,
        }),
      ]
    );

    res.status(200).json({
      status: 'success',
      data: {
        timestamp: new Date().toISOString(),
        findings_count: findings.length,
        remediated_count: remediated,
        findings: findings.map(f => ({
          id: f.id,
          severity: f.severity,
          category: f.category,
          title: f.title,
          description: f.description,
          filePath: f.filePath,
          line: f.line,
          remediation: f.remediation,
          status: f.status,
        })),
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getControlCenterOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const overview = ControlCenterService.getDashboardOverview();
    res.status(200).json({
      status: 'success',
      data: overview,
    });
  } catch (error) {
    next(error);
  }
};

export const chatWithChiefAgent = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { message } = req.body;
    if (!message || typeof message !== 'string') {
      return res.status(400).json({
        status: 'error',
        message: 'پیام مدیر برای Chief Agent الزامی است.',
      });
    }

    const result = ControlCenterService.handleChiefAgentChat(message, req.user?.email || 'admin@ibo.com');
    res.status(200).json({
      status: 'success',
      data: result,
    });
  } catch (error) {
    next(error);
  }
};

export const executeCodingWorkerTask = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { instruction, autonomyClass, environment, workspacePath, approvalToken, idempotencyKey } = req.body;
    if (!instruction || !autonomyClass) {
      return res.status(400).json({
        status: 'error',
        message: 'دستورالعمل و کلاس خودمختاری الزامی است.',
      });
    }

    const taskId = `task-admin-${Date.now()}`;
    const startRequest: WorkerStartRequest = {
      taskId,
      instruction,
      autonomyClass,
      environment: environment || 'DEVELOPMENT',
      workspacePath: workspacePath || '/workspace',
      timeoutMs: 30000,
      retryPolicy: { maxRetries: 3, backoffMs: 1000 },
      permissions: {
        allowedTools: ['FILE_READ', 'FILE_WRITE', 'TEST_RUNNER'],
        deniedTools: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS'],
        mcpServers: ['mcp-core-governance'],
        autonomyClass,
      },
      approvalToken,
      idempotencyKey,
    };

    CodingAgentAdapter.createTask(startRequest);
    const result = await CodingAgentAdapter.executeTask(startRequest);

    res.status(200).json({
      status: 'success',
      data: result,
    });
  } catch (error) {
    next(error);
  }
};

export const getKnowledgeOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const { KnowledgeEngine } = await import('./knowledge-engine.service');
    const overview = KnowledgeEngine.getKnowledgeObservabilityOverview();
    res.status(200).json({
      status: 'success',
      data: overview,
    });
  } catch (error) {
    next(error);
  }
};

export const getOrchestrationOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const overview = TaskOrchestrationEngine.getObservabilityDashboard();
    const tasks = TaskOrchestrationEngine.listTasks();
    const workers = TaskOrchestrationEngine.listWorkers();
    const schedules = TaskOrchestrationEngine.listSchedules();

    res.status(200).json({
      status: 'success',
      data: {
        ...overview,
        tasks,
        workers,
        schedules,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getMultiAgentOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const agents = MultiAgentOperatingArchitecture.listAgents();
    const decisionJournal = MultiAgentOperatingArchitecture.getDecisionJournal();
    const messageLogs = MultiAgentOperatingArchitecture.getMessageLogs();

    res.status(200).json({
      status: 'success',
      data: {
        totalAgents: agents.length,
        agents,
        decisionJournal,
        messageLogsCount: messageLogs.length,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getSecurityGovernanceOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const overview = ZeroTrustPolicyEngine.getSecurityOverview();
    res.status(200).json({
      status: 'success',
      data: overview,
    });
  } catch (error) {
    next(error);
  }
};

export const getResilienceOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const overview = RuntimeResilienceObservabilityService.getSystemHealthOverview();
    const incidents = RuntimeResilienceObservabilityService.listIncidents();
    res.status(200).json({
      status: 'success',
      data: {
        ...overview,
        incidents,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getQualityScorecardOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const tests = QualityEngineeringReleaseService.listTests();
    const scorecards = QualityEngineeringReleaseService.listScorecards();
    res.status(200).json({
      status: 'success',
      data: {
        totalRegisteredTests: tests.length,
        tests,
        scorecards,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getCEOExecutiveOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const overview = CEOAdminControlPlaneService.getExecutiveOverview();
    const previews = CEOAdminControlPlaneService.listActionPreviews();
    const breakGlassSessions = CEOAdminControlPlaneService.listBreakGlassSessions();
    res.status(200).json({
      status: 'success',
      data: {
        overview,
        previews,
        breakGlassSessions,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getIntegrationReconciliationOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const report = EndToEndIntegrationReconciliationService.reconcileSystemState();
    const events = EndToEndIntegrationReconciliationService.listEvents();
    res.status(200).json({
      status: 'success',
      data: {
        report,
        totalEventsTracked: events.length,
        events,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getProductionReleaseOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const candidates = ProductionReleaseEngineeringService.listCandidates();
    const rehearsals = ProductionReleaseEngineeringService.listRollbackRehearsals();
    res.status(200).json({
      status: 'success',
      data: {
        totalReleaseCandidates: candidates.length,
        candidates,
        rehearsals,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getDataGovernanceOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const inventory = DataArchitectureGovernanceService.listInventory();
    const restoreTests = DataArchitectureGovernanceService.listRestoreTests();
    res.status(200).json({
      status: 'success',
      data: {
        totalDataDomains: inventory.length,
        inventory,
        restoreTests,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getProductAnalyticsOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const kpis = ProductAnalyticsEngine.listKPIs();
    const experiments = ProductAnalyticsEngine.listExperiments();
    const insights = ProductAnalyticsEngine.generateInsights();
    res.status(200).json({
      status: 'success',
      data: {
        kpis,
        experiments,
        insights,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getGlobalGrowthOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const opportunities = GlobalGrowthEngineService.listOpportunities();
    const campaigns = GlobalGrowthEngineService.listCampaigns();
    const insights = GlobalGrowthEngineService.listInsights();
    res.status(200).json({
      status: 'success',
      data: {
        opportunities,
        campaigns,
        insights,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getBillingOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const plans = BillingEntitlementEngine.listPlans();
    const reconciliation = BillingEntitlementEngine.runFinancialReconciliation();
    res.status(200).json({
      status: 'success',
      data: {
        plans,
        reconciliation,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getMobileDeliveryOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const parityMatrix = MobileNativeDeliveryEngine.getFeatureParityMatrix();
    const storeTargets = MobileNativeDeliveryEngine.listStoreTargets();
    const buildArtifacts = MobileNativeDeliveryEngine.listBuildArtifacts();
    res.status(200).json({
      status: 'success',
      data: {
        parityMatrix,
        storeTargets,
        buildArtifacts,
      },
    });
  } catch (error) {
    next(error);
  }
};

export const getAutonomousMasterOverview = async (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  try {
    const autonomyLevel = AutonomousOperatingSystem.getAutonomyLevel();
    const readiness = AutonomousOperatingSystem.getMasterReadinessMatrix();
    const chaosHistory = AutonomousOperatingSystem.getChaosExerciseHistory();
    const techDebt = AutonomousOperatingSystem.getTechnicalDebtRegistry();
    res.status(200).json({
      status: 'success',
      data: {
        autonomyLevel,
        readiness,
        chaosHistory,
        techDebt,
      },
    });
  } catch (error) {
    next(error);
  }
};




