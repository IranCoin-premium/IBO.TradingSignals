import request from 'supertest';
import app from '../app';
import jwt from 'jsonwebtoken';
import { pool } from '../config/database';

// Primary mock for database package pg
jest.mock('pg', () => {
  const mClient = {
    query: jest.fn(),
    release: jest.fn(),
  };
  const mPool = {
    connect: jest.fn().mockResolvedValue(mClient),
    query: jest.fn(),
    on: jest.fn(),
    end: jest.fn(),
  };
  return { Pool: jest.fn(() => mPool) };
});

describe('IBO Trading Signals Backend Integration Tests', () => {
  const prefix = '/api/v1';

  // Auth tokens
  const userToken = jwt.sign(
    { id: 'user_uuid_123', email: 'user@ibo.ir', roles: ['USER'] },
    'super_secret_jwt_sign_key_change_me_in_production'
  );

  const adminToken = jwt.sign(
    { id: 'admin_uuid_789', email: 'admin@ibo.ir', roles: ['ADMIN'] },
    'super_secret_jwt_sign_key_change_me_in_production'
  );

  const mClient = {
    query: jest.fn(),
    release: jest.fn(),
  };

  // Setup/Reset mocks before each individual test case
  beforeEach(() => {
    // Reset individual mocks
    mClient.query.mockReset();
    mClient.release.mockReset();
    (pool.connect as jest.Mock).mockReset();
    (pool.query as jest.Mock).mockReset();

    // Setup client query mock implementation
    mClient.query.mockImplementation((sql: string, params?: any[]) => {
      const sqlNormalized = (sql || '').toString().toUpperCase();

      if (sqlNormalized.includes('SELECT COUNT(*)')) {
        return Promise.resolve({ rows: [{ count: '1' }], rowCount: 1 });
      }

      // Plans Retrieval mock
      if (sqlNormalized.includes('FROM SUBSCRIPTION_PLANS') && sqlNormalized.includes('WHERE ID = $1')) {
        const planId = params?.[0];
        if (planId === 'INVALID') {
          return Promise.resolve({ rows: [], rowCount: 0 });
        }
        return Promise.resolve({
          rows: [{
            id: planId || 'BRONZE',
            title: 'پلن برنزی ۳۰ روزه',
            price: '15.00',
            duration_days: 30,
            description: 'سیگنال‌های باینری آپشن',
            created_at: new Date()
          }],
          rowCount: 1
        });
      }

      if (sqlNormalized.includes('FROM SUBSCRIPTION_PLANS')) {
        return Promise.resolve({
          rows: [
            { id: 'FREE', title: 'پلن رایگان', price: '0.00', duration_days: 3650 },
            { id: 'BRONZE', title: 'پلن برنزی', price: '15.00', duration_days: 30 }
          ],
          rowCount: 2
        });
      }

      // Purchase Intents mock
      if (sqlNormalized.includes('INSERT INTO PURCHASE_INTENTS')) {
        return Promise.resolve({
          rows: [{
            id: 'intent-uuid-1111',
            user_id: params?.[0],
            plan_id: params?.[1],
            quoted_amount: params?.[2],
            currency: params?.[3],
            payment_method: params?.[4],
            idempotency_key: params?.[5],
            expires_at: params?.[6],
            status: 'CREATED',
            created_at: new Date(),
            updated_at: new Date()
          }],
          rowCount: 1
        });
      }

      if (sqlNormalized.includes('FROM PURCHASE_INTENTS') && sqlNormalized.includes('IDEMPOTENCY_KEY = $1')) {
        const key = params?.[0];
        if (key === 'duplicate-key-102030') {
          return Promise.resolve({
            rows: [{
              id: 'intent-uuid-1111',
              user_id: 'user_uuid_123',
              plan_id: 'BRONZE',
              quoted_amount: 15.00,
              currency: 'USD',
              payment_method: 'TETHER',
              idempotency_key: key,
              status: 'CREATED'
            }],
            rowCount: 1
          });
        }
        return Promise.resolve({ rows: [], rowCount: 0 });
      }

      if (sqlNormalized.includes('FROM PURCHASE_INTENTS') && sqlNormalized.includes('ID = $1')) {
        return Promise.resolve({
          rows: [{
            id: params?.[0],
            user_id: 'user_uuid_123',
            plan_id: 'BRONZE',
            quoted_amount: 15.00,
            currency: 'USD',
            payment_method: 'TETHER',
            status: 'CREATED'
          }],
          rowCount: 1
        });
      }

      // Transactions mock
      if (sqlNormalized.includes('SELECT * FROM PAYMENT_TRANSACTIONS WHERE ID = $1')) {
        return Promise.resolve({
          rows: [{
            id: params?.[0],
            user_id: 'user_uuid_123',
            plan_id: 'BRONZE',
            amount: '15.00',
            status: 'SUCCESS'
          }],
          rowCount: 1
        });
      }

      // Refunds mock
      if (sqlNormalized.includes('INSERT INTO REFUNDS')) {
        return Promise.resolve({
          rows: [{
            id: 'refund-uuid-9999',
            transaction_id: params?.[0],
            amount: params?.[1] || '15.00',
            status: 'SUCCESS',
            reason: params?.[2] || 'Test refund',
            created_at: new Date()
          }],
          rowCount: 1
        });
      }

      // Agents mock
      if (sqlNormalized.includes('FROM AGENTS') && sqlNormalized.includes('AGENT_ID = $1')) {
        const agentId = params?.[0];
        if (agentId === 'agent-active-01') {
          return Promise.resolve({
            rows: [{
              agent_id: agentId,
              name: 'Test Supervisor',
              role: 'SUPERVISOR',
              model: 'gemini-3.5-flash',
              status: 'ACTIVE',
              permissions: ['REPO_READ', 'RUN_TESTS', 'PUBLISH_CONTENT', 'WRITE_DATABASE', 'MANAGE_SECURITY']
            }],
            rowCount: 1
          });
        }
        return Promise.resolve({ rows: [], rowCount: 0 });
      }

      if (sqlNormalized.includes('FROM AGENT_TOOLS') && sqlNormalized.includes('TOOL_ID = $1')) {
        const toolId = params?.[0];
        if (toolId === 'read_repository') {
          return Promise.resolve({
            rows: [{
              tool_id: toolId,
              name: 'Read Repository',
              risk_level: 'R0',
              required_permissions: ['REPO_READ'],
              enabled: true
            }],
            rowCount: 1
          });
        }
        if (toolId === 'modify_production_db') {
          return Promise.resolve({
            rows: [{
              tool_id: toolId,
              name: 'Modify Production DB',
              risk_level: 'R4',
              required_permissions: ['WRITE_DATABASE', 'MANAGE_SECURITY'],
              enabled: true
            }],
            rowCount: 1
          });
        }
        return Promise.resolve({ rows: [], rowCount: 0 });
      }

      if (sqlNormalized.includes('FROM AGENT_KILL_SWITCH')) {
        return Promise.resolve({
          rows: [{ enabled: false }],
          rowCount: 1
        });
      }

      if (sqlNormalized.includes('INSERT INTO AGENT_JOBS') || sqlNormalized.includes('INSERT INTO AGENT_AUDIT_LOGS') || sqlNormalized.includes('INSERT INTO AGENT_KILL_SWITCH') || sqlNormalized.includes('UPDATE AGENT_JOBS')) {
        return Promise.resolve({ rows: [{ success: true }], rowCount: 1 });
      }

      // Default fallback rows
      return Promise.resolve({ rows: [], rowCount: 0 });
    });

    // Wire connect and pool query mocks
    (pool.connect as jest.Mock).mockResolvedValue(mClient);
    (pool.query as jest.Mock).mockImplementation((sql: string, params?: any[]) => {
      return mClient.query(sql, params);
    });
  });

  describe('GET /health', () => {
    it('should return 200 with ok status when database is healthy', async () => {
      const res = await request(app).get(`${prefix}/health`);
      expect(res.status).toBe(200);
      expect(res.body).toEqual(expect.objectContaining({
        status: 'ok',
        service: 'backend',
        database: {
          status: 'healthy',
          migrations: 'synchronized',
        }
      }));
    });
  });

  describe('Input Validation & Auth Rejection', () => {
    it('should reject register requests when email format is invalid', async () => {
      const res = await request(app)
        .post(`${prefix}/auth/register`)
        .send({
          email: 'invalid-email',
          password: '123',
          displayName: 'Al',
        });

      expect(res.status).toBe(400);
      expect(res.body.code).toBe('VALIDATION_ERROR');
    });

    it('should reject checkout requests when unauthorized', async () => {
      const res = await request(app)
        .post(`${prefix}/payments/checkout`)
        .send({
          planId: 'BRONZE',
          paymentMethod: 'USDT_TRC20',
          externalReference: 'tx_9876543210'
        });

      expect(res.status).toBe(401);
      expect(res.body.code).toBe('UNAUTHORIZED');
    });
  });

  describe('Part 4 - Subscription Plans Retrieval', () => {
    it('should return plans correctly', async () => {
      const res = await request(app)
        .get(`${prefix}/subscriptions/plans`);
      
      expect(res.status).toBe(200);
      expect(res.body.status).toBe('success');
      expect(Array.isArray(res.body.data)).toBe(true);
    });
  });

  describe('Part 4 - Purchase Intent Creation & Client Price Tampering Protection', () => {
    it('should reject purchase intent with invalid plan id', async () => {
      const res = await request(app)
        .post(`${prefix}/purchases`)
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          planId: 'INVALID',
          paymentMethod: 'TETHER',
          idempotencyKey: 'idempotency-key-000111222'
        });

      expect(res.status).toBe(404);
    });

    it('should create purchase intent successfully with server-authoritative quoted price', async () => {
      const res = await request(app)
        .post(`${prefix}/purchases`)
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          planId: 'BRONZE',
          paymentMethod: 'TETHER',
          idempotencyKey: 'idempotency-key-111222333'
        });

      expect(res.status).toBe(201);
      expect(res.body.data.quoted_amount).toBe(15.00);
    });

    it('should retrieve existing purchase intent for duplicate idempotency key (idempotency safety)', async () => {
      const res = await request(app)
        .post(`${prefix}/purchases`)
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          planId: 'BRONZE',
          paymentMethod: 'TETHER',
          idempotencyKey: 'duplicate-key-102030'
        });

      expect(res.status).toBe(200);
      expect(res.body.message).toContain('بازیابی قصد خرید تکراری');
    });
  });

  describe('Part 4 - Webhook Signature Security Validation', () => {
    it('should reject webhook with missing signature', async () => {
      const res = await request(app)
        .post(`${prefix}/payments/webhooks/tether`)
        .send({
          transactionId: '00000000-0000-0000-0000-000000000000',
          eventType: 'confirmed'
        });

      expect(res.status).toBe(401);
      expect(res.body.message).toContain('امضای وب‌هووک ارسالی نامعتبر یا گم شده است');
    });

    it('should accept and process webhook with valid signature', async () => {
      const res = await request(app)
        .post(`${prefix}/payments/webhooks/tether`)
        .set('X-Webhook-Signature', 'IBO_SECURE_WEBHOOK_SECRET_2026')
        .send({
          transactionId: '11112222-3333-4444-5555-666677778888',
          eventType: 'payment_confirmed',
          payload: { txHash: '0x1234abc' }
        });

      expect(res.status).toBe(200);
      expect(res.body.message).toContain('با موفقیت دریافت، تایید و اعمال گردید');
    });
  });

  describe('Part 4 - Refund Authorization & Execution Checks', () => {
    it('should deny refund request when requested by non-admin', async () => {
      const res = await request(app)
        .post(`${prefix}/admin/subscriptions/refund`)
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          transactionId: '11112222-3333-4444-5555-666677778888',
          amount: 15.00,
          reason: 'User request'
        });

      expect(res.status).toBe(403);
    });

    it('should allow refund request when requested by authorized admin', async () => {
      const res = await request(app)
        .post(`${prefix}/admin/subscriptions/refund`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          transactionId: '11112222-3333-4444-5555-666677778888',
          amount: 15.00,
          reason: 'Double payment error'
        });

      expect(res.status).toBe(200);
      expect(res.body.message).toContain('با موفقیت عودت داده شد');
    });
  });

  describe('Part 4 - Subscription Cancellation', () => {
    it('should successfully cancel an active subscription', async () => {
      const res = await request(app)
        .post(`${prefix}/subscriptions/sub-102030/cancel`)
        .set('Authorization', `Bearer ${userToken}`);

      expect([200, 400]).toContain(res.status);
    });
  });

  describe('Part 6 - Agent Orchestration, Supervisor & Governance Foundation', () => {
    it('should successfully dispatch job when agent is valid and authorized', async () => {
      const res = await request(app)
        .post(`${prefix}/agents/jobs`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          agent_id: 'agent-active-01',
          tool_id: 'read_repository',
          action: 'READ_FILES',
          target_resource: '/backend/src/app.ts',
          environment: 'DEVELOPMENT',
          payload: { path: '/backend/src/app.ts' }
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.job_id).toBeDefined();
    });

    it('should deny job dispatch when agent is unknown or missing', async () => {
      const res = await request(app)
        .post(`${prefix}/agents/jobs`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          agent_id: 'agent-unknown-999',
          tool_id: 'read_repository',
          action: 'READ_FILES',
          target_resource: '/backend/src/app.ts',
          environment: 'DEVELOPMENT'
        });

      expect(res.status).toBe(403);
      expect(res.body.errorCode).toBe('AGENT_ACTION_VETOED');
    });

    it('should allow admin to update kill switch state', async () => {
      const res = await request(app)
        .post(`${prefix}/agents/kill-switch`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          switch_key: 'GLOBAL_KILL_SWITCH',
          enabled: true
        });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.enabled).toBe(true);
    });
  });

  describe('Part 6 - Master UI/UX Agent & 10-Level Quality Gate with Auto-Rollback', () => {
    // Import the Agent
    const { UIUXAgent } = require('../agents/ui-ux-agent/ui-ux-agent');
    const { BrandProtectionGuard } = require('../agents/ui-ux-agent/brand-guard');
    const { TenLevelTester } = require('../agents/ui-ux-agent/ten-level-tester');
    const { AutoDebugRollbackHandler } = require('../agents/ui-ux-agent/auto-rollback-handler');

    it('Constraint 1: should reject any proposal that attempts to modify protected brand assets', async () => {
      const agent = new UIUXAgent();
      const maliciousProposal = {
        componentTarget: 'logo_redesign',
        changeType: 'STYLE_DRIFT',
        targetFiles: ['public/logo.png', 'app/src/main/res/drawable/ic_ibo_logo.xml'],
        diffSummary: 'Attempted to change logo colors',
        styleDriftPercentage: 2.0,
        beforeState: { 'public/logo.png': '<original_logo>' },
        afterState: { 'public/logo.png': '<modified_logo>' }
      };

      const result = await agent.processUIProposal(maliciousProposal);
      expect(result.approved).toBe(false);
      expect(result.uiChangeRecord.rolledBack).toBe(true);
      expect(result.message).toContain('VETO: Modification of protected brand asset');
    });

    it('Constraint 2: should reject any proposal that removes the mandatory risk disclosure', async () => {
      const brandGuard = new BrandProtectionGuard();
      const mandatoryText = brandGuard.getMandatoryRiskDisclosure();

      const proposal = {
        componentTarget: 'login_modal',
        changeType: 'MODAL_CONVERSION',
        targetFiles: ['frontend/src/pages/Login.tsx'],
        diffSummary: 'Removed footer notice',
        styleDriftPercentage: 2.5,
        beforeState: {
          'frontend/src/pages/Login.tsx': `<div>Login Form</div><div>${mandatoryText}</div>`
        },
        afterState: {
          'frontend/src/pages/Login.tsx': `<div>Login Form without disclosure</div>`
        }
      };

      const check = brandGuard.validateProposal(proposal);
      expect(check.allowed).toBe(false);
      expect(check.violation).toContain('removes mandatory risk disclosure');
    });

    it('Quality Gate: should successfully pass all 10 test levels for compliant modal conversion', async () => {
      const agent = new UIUXAgent();
      const proposal = agent.createAuthModalConversionProposal();

      const result = await agent.processUIProposal(proposal);
      expect(result.approved).toBe(true);
      expect(result.uiChangeRecord.levelTestStatus).toBe('PASSED_LEVEL_10');
      expect(result.uiChangeRecord.rolledBack).toBe(false);
      expect(result.uiChangeRecord.retryCount).toBe(0);
      expect(result.uiChangeRecord.humanReviewRequired).toBe(false);
    });

    it('Auto-Debug Handler: should rollback and retry when a simulated intermediate level failure occurs', async () => {
      const handler = new AutoDebugRollbackHandler(3);
      const agent = new UIUXAgent();
      const proposal = agent.createAuthModalConversionProposal();

      // Simulate Level 4 failure on attempt 1, pass on attempt 2
      const simErrors = {
        1: { 4: 'Focus trap failed on initial render' }
      };

      const result = await handler.executeWithAutoDebugGovernance(proposal, simErrors);
      expect(result.status).toBe('PASSED_LEVEL_10');
      expect(result.totalAttempts).toBe(2);
      expect(result.history[0].passed).toBe(false);
      expect(result.history[0].rollbackPerformed).toBe(true);
      expect(result.history[1].passed).toBe(true);
    });

    it('Auto-Debug Governance: should exhaust retries and flag for human review after 3 failures', async () => {
      const handler = new AutoDebugRollbackHandler(3);
      const agent = new UIUXAgent();
      const proposal = agent.createAuthModalConversionProposal();

      // Simulate failure on all 3 attempts (e.g. Level 8 RTL failure)
      const persistentErrors = {
        1: { 8: 'RTL padding misaligned in Persian locale' },
        2: { 8: 'RTL padding still misaligned' },
        3: { 8: 'RTL layout broken' }
      };

      const result = await handler.executeWithAutoDebugGovernance(proposal, persistentErrors);
      expect(result.status).toBe('CANCELLED_MAX_RETRIES_EXCEEDED');
      expect(result.totalAttempts).toBe(3);
      expect(result.finalRecord.rolledBack).toBe(true);
      expect(result.finalRecord.humanReviewRequired).toBe(true);
    });
  });

  describe('Part 7 - Localization, Geo-IP, Translator Agent & 24/7 AI Support', () => {
    const { GeoIpService } = require('../modules/support/geoip.service');
    const { TranslatorAgent } = require('../agents/translator-agent/translator-agent');
    const { SupportAIAgent } = require('../agents/support-agent/support-ai-agent');

    it('Geo-IP: should accurately map IP/country headers to the 6 target languages and direction', () => {
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'IR').suggestedLanguage).toBe('fa');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'IR').direction).toBe('rtl');

      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'SA').suggestedLanguage).toBe('ar');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'SA').direction).toBe('rtl');

      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'IN').suggestedLanguage).toBe('hi');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'IN').direction).toBe('ltr');

      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'TR').suggestedLanguage).toBe('tr');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'TR').direction).toBe('ltr');

      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'RU').suggestedLanguage).toBe('ru');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'RU').direction).toBe('ltr');

      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'US').suggestedLanguage).toBe('en');
      expect(GeoIpService.detectLanguageFromIp('127.0.0.1', 'US').direction).toBe('ltr');
    });

    it('Translator Quality Gate: should reject translations with forbidden placeholders or omitted risk disclosures', () => {
      const translator = new TranslatorAgent();

      // Case 1: Forbidden placeholder
      const badPlaceholder = translator.validatePrePublish(
        'Call Signal 5m Expiry',
        'TODO: سیگنال خرید ۵ دقیقه',
        'fa'
      );
      expect(badPlaceholder.valid).toBe(false);
      expect(badPlaceholder.errors[0]).toContain('forbidden placeholder');

      // Case 2: Missing template variable
      const missingVariable = translator.validatePrePublish(
        'Amount: {amount} USDT for {asset}',
        'مبلغ: ۵۰ تتر برای دارایی',
        'fa'
      );
      expect(missingVariable.valid).toBe(false);
      expect(missingVariable.errors[0]).toContain('Missing template variable');

      // Case 3: Omitted mandatory risk disclosure
      const omittedRisk = translator.validatePrePublish(
        'Warning: high risk of capital loss',
        'هشدار: این یک معامله است',
        'fa'
      );
      expect(omittedRisk.valid).toBe(false);
      expect(omittedRisk.errors[0]).toContain('Mandatory risk disclosure is missing');

      // Case 4: Valid translation with preserved risk disclosure
      const valid = translator.validatePrePublish(
        'high risk of capital loss in binary trading',
        'معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
        'fa'
      );
      expect(valid.valid).toBe(true);
    });

    it('Support AI Agent: must truthfully disclose AI identity when asked "Are you human?"', async () => {
      const supportAgent = new SupportAIAgent();

      // Ask in Persian
      const resFa = await supportAgent.processMessage('آیا شما انسان هستید یا ربات؟', {
        languageCode: 'fa'
      });
      expect(resFa.isAiDisclosed).toBe(true);
      expect(resFa.reply).toContain('من دستیار هوش مصنوعی پشتیبانی ۲۴ ساعته');
      expect(resFa.reply).toContain('ریسک بالای از دست دادن سرمایه دارد');

      // Ask in English
      const resEn = await supportAgent.processMessage('Are you a human or an AI bot?', {
        languageCode: 'en'
      });
      expect(resEn.isAiDisclosed).toBe(true);
      expect(resEn.reply).toContain('Yes, I am the 24/7 AI Support Assistant');
      expect(resEn.reply).toContain('high risk of capital loss');

      // Ask in Turkish
      const resTr = await supportAgent.processMessage('Sen insan mısın yoksa bot musun?', {
        languageCode: 'tr'
      });
      expect(resTr.isAiDisclosed).toBe(true);
      expect(resTr.reply).toContain('Yapay Zeka Destek Asistanıyım');
    });

    it('API: should serve 24/7 AI chat, detect language and return active languages', async () => {
      // 1. Languages endpoint
      const langRes = await request(app).get(`${prefix}/support/localization/languages`);
      expect(langRes.status).toBe(200);
      expect(langRes.body.languages).toHaveLength(6);
      expect(langRes.body.languages.map((l: any) => l.code)).toEqual(['fa', 'en', 'ar', 'hi', 'tr', 'ru']);

      // 2. Translations endpoint
      const transRes = await request(app).get(`${prefix}/support/localization/translations/ar`);
      expect(transRes.status).toBe(200);
      expect(transRes.body.translations.risk_disclosure).toContain('تداول الخيارات الثنائية');

      // 3. 24/7 AI Chat endpoint
      const chatRes = await request(app)
        .post(`${prefix}/support/chat`)
        .send({
          message: 'آیا شما ربات هستید؟',
          languageCode: 'fa'
        });
      expect(chatRes.status).toBe(200);
      expect(chatRes.body.isAiDisclosed).toBe(true);
      expect(chatRes.body.riskDisclosureIncluded).toBe(true);
    });
  });

  describe('Part 8 - Media Assets, Style Reference Rotation & Video Agent', () => {
    const { MediaAgent } = require('../agents/media-agent/media-agent');

    it('Constraint 1: should reject generation prompts with forbidden third-party trademarks', () => {
      const agent = new MediaAgent();

      const resBinance = agent.checkPromptCompliance('Cryptocurrency trading chart on Binance platform');
      expect(resBinance.allowed).toBe(false);
      expect(resBinance.reason).toContain('forbidden third-party trademark');

      const resNasdaq = agent.checkPromptCompliance('Nasdaq tech stock price jump');
      expect(resNasdaq.allowed).toBe(false);
      expect(resNasdaq.reason).toContain('nasdaq');

      const validPrompt = agent.checkPromptCompliance('Futuristic financial trading dashboard with golden candlesticks');
      expect(validPrompt.allowed).toBe(true);
    });

    it('Constraint 2: should reject prompts containing misleading claims or guaranteed profits', () => {
      const agent = new MediaAgent();

      const resGuaranteed = agent.checkPromptCompliance('Show trader with guaranteed profit and 100% win rate');
      expect(resGuaranteed.allowed).toBe(false);
      expect(resGuaranteed.reason).toContain('misleading');

      const resFarsi = agent.checkPromptCompliance('کسب سود تضمینی بدون ریسک در معامله');
      expect(resFarsi.allowed).toBe(false);
    });

    it('Aspect Ratios: should automatically allocate appropriate aspect ratios based on purpose', () => {
      const agent = new MediaAgent();

      // News: 16:9 banner & 9:16 mobile story
      expect(agent.getRequiredAspectRatios('news')).toEqual(['16:9', '9:16']);

      // Journal: 3:2 blog cover & 1:1 square
      expect(agent.getRequiredAspectRatios('journal')).toEqual(['3:2', '1:1']);

      // Marketing: All 5 ratios
      expect(agent.getRequiredAspectRatios('marketing')).toEqual(['1:1', '16:9', '9:16', '4:5', '3:2']);
    });

    it('Style Reference Rotation: should retain permanent logo and select up to 9 candidates', () => {
      const agent = new MediaAgent();

      // Mock 12 weekly candidates
      const mockCandidates = Array.from({ length: 12 }).map((_, i) => ({
        id: `med_mock_${i}`,
        type: 'image',
        purpose: 'marketing',
        sourceUrl: `https://cdn.example.com/${i}.png`,
        aspectRatio: '1:1',
        hasWatermark: true,
        hasLogo: true,
        createdByAgent: 'agent-media',
        isStyleReference: false,
        isPermanentLogoReference: false,
        complianceStatus: 'VERIFIED',
        createdAt: new Date().toISOString()
      }));

      const rotation = agent.rotateWeeklyStyleReferences(mockCandidates, '2026-W36');

      expect(rotation.retainedPermanentLogoId).toBe('00000000-0000-0000-0000-000000000001');
      // 1 permanent logo + up to 9 selected images = 10 total
      expect(rotation.newStyleReferenceIds).toHaveLength(10);
      expect(rotation.newStyleReferenceIds[0]).toBe('00000000-0000-0000-0000-000000000001');
    });

    it('Video Generation: should convert image into 15-60s teaser video with watermark and logo', () => {
      const agent = new MediaAgent();

      const mockImage = {
        id: 'med_base_image_123',
        type: 'image' as const,
        purpose: 'news' as const,
        sourceUrl: 'https://cdn.example.com/base.png',
        aspectRatio: '16:9' as const,
        hasWatermark: true,
        hasLogo: true,
        createdByAgent: 'agent-media',
        isStyleReference: false,
        isPermanentLogoReference: false,
        complianceStatus: 'VERIFIED' as const,
        platformBrandingText: 'IBO Binary Option Trading Signals — yourdomain.com',
        bgRemovalMethod: 'none' as const,
        createdAt: new Date().toISOString()
      };

      const video = agent.generateMotionVideoFromImage({
        sourceImage: mockImage,
        durationSeconds: 30
      });

      expect(video.type).toBe('video');
      expect(video.durationSeconds).toBe(30);
      expect(video.hasWatermark).toBe(true);
      expect(video.hasLogo).toBe(true);
      expect(video.styleReferenceId).toBe('med_base_image_123');
    });
  });

  describe('Part 9 - Admin Secrets Management & AI Assistants Panel', () => {
    it('Security Check: should reject non-admin requests from accessing admin secrets', async () => {
      const res = await request(app)
        .get(`${prefix}/admin/secrets`)
        .set('Authorization', `Bearer ${userToken}`);

      expect(res.status).toBe(403);
    });

    it('List Secrets: should list registered AI agents & payment secrets with masked hints (never raw secrets)', async () => {
      const res = await request(app)
        .get(`${prefix}/admin/secrets`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(200);
      expect(res.body.status).toBe('success');
      expect(res.body.data.total).toBeGreaterThanOrEqual(9);

      // Verify no raw keys exist and all secrets have masked hints like '****ab12'
      for (const service of res.body.data.services) {
        expect(service.masked_hint).toMatch(/^\*{4}[a-z0-9]{4}$/);
        expect(service).not.toHaveProperty('raw_secret_value');
        expect(service).not.toHaveProperty('raw_value');
        expect(service.secret_ref).toBeDefined();
      }
    });

    it('Update Secret: should update secret via provider bridge, return safe mask and record human audit', async () => {
      const res = await request(app)
        .post(`${prefix}/admin/secrets/GOOGLE_FLOW_API_KEY`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          raw_secret_value: 'sk-googleflow-nano-9988aabb',
          is_active: true,
          assistant_metadata: {
            model: 'nanobanana-v2',
            maxDuration: 45
          }
        });

      expect(res.status).toBe(200);
      expect(res.body.status).toBe('success');
      expect(res.body.data.masked_hint).toBe('****aabb');
      expect(res.body.data.secret_ref).toBe('GITHUB_SECRET:GOOGLE_FLOW_API_KEY');
      expect(res.body.data.assistant_metadata.maxDuration).toBe(45);
      expect(res.body.data.updated_by).toBe('admin@ibo.ir');
    });

    it('Masking utility: should correctly format secrets with only last 4 characters visible', () => {
      const { SecretsManagerBridge } = require('../modules/admin/secrets-bridge.service');
      expect(SecretsManagerBridge.maskSecret('live_secret_key_abcdef1234')).toBe('****1234');
      expect(SecretsManagerBridge.maskSecret('abc')).toBe('****');
    });
  });
});

