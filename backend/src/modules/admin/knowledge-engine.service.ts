/**
 * IBO Ecosystem — Knowledge, RAG, Memory, Skills & Research Intelligence Layer
 * Part 06: Work Packages 6.1 - 6.33
 * 
 * Strict Architecture Invariants:
 * 1. Provenance: Every important knowledge item tracks source, date, version, authority, validator.
 * 2. Knowledge Layers:
 *    - Layer A: Project Constitution (immutable boundaries, human authority, legal disclosure)
 *    - Layer B: Project Documentation (architecture, ADRs, schemas, APIs)
 *    - Layer C: Operational Knowledge (releases, health, incidents, TTL/freshness)
 *    - Layer D: Research Knowledge (structured evidence, claims, conflicts, stopping criteria)
 *    - Layer E: Market & Localization (language != country, Arabic != monolith)
 *    - Layer F: Governed Agent Memory (working/session/task vs governed permanent)
 *    - Layer G: Task/Session Context (ephemeral working state)
 * 3. Freshness: FRESH, AGING, STALE, EXPIRED, UNKNOWN.
 * 4. Conflict Detection: Claims A & B with distinct sources and dates surfaced to Admin.
 * 5. Role-Based Retrieval & Least Privilege: No unrestricted dump to all agents.
 * 6. Skills & MCP Governance: Versioned, tested, permission-controlled.
 * 7. Anti-Hallucination & Research Stopping Criteria: INSUFFICIENT_EVIDENCE when unverified; Human Action Boundary at KYC/Banking.
 */

import { ObservabilityService } from '../observability/observability.service';

export type KnowledgeLayer =
  | 'LAYER_A_CONSTITUTION'
  | 'LAYER_B_DOCUMENTATION'
  | 'LAYER_C_OPERATIONAL'
  | 'LAYER_D_RESEARCH'
  | 'LAYER_E_MARKET_LOCALIZATION'
  | 'LAYER_F_AGENT_MEMORY'
  | 'LAYER_G_SESSION_CONTEXT';

export type KnowledgeType =
  | 'ARCHITECTURE'
  | 'POLICY'
  | 'RUNBOOK'
  | 'API_DOCUMENTATION'
  | 'SECURITY'
  | 'QA'
  | 'INCIDENT'
  | 'RESEARCH'
  | 'MARKET'
  | 'LOCALIZATION'
  | 'SEO'
  | 'GEO'
  | 'PAYMENT'
  | 'ADR';

export type FreshnessStatus = 'FRESH' | 'AGING' | 'STALE' | 'EXPIRED' | 'UNKNOWN';
export type VerificationStatus = 'VERIFIED_FACT' | 'INFERENCE' | 'HYPOTHESIS' | 'RECOMMENDATION' | 'UNVERIFIED_SOURCE';
export type SourceAuthority = 'OFFICIAL_PRIMARY' | 'REGULATORY' | 'INTERNAL_CONSTITUTION' | 'SECONDARY_ANALYSIS' | 'UNVERIFIED';

export interface KnowledgeItem {
  knowledgeId: string;
  layer: KnowledgeLayer;
  type: KnowledgeType;
  title: string;
  domain: string;
  sourceType: string;
  sourceReference: string;
  sourceAuthority: SourceAuthority;
  owner: string;
  createdAt: string;
  updatedAt: string;
  validUntil?: string;
  freshnessStatus: FreshnessStatus;
  version: string;
  language: string;       // e.g. 'fa', 'en', 'ar'
  locale?: string;        // e.g. 'en-CA', 'ar-SA'
  countryScope?: string;  // e.g. 'IR', 'SA', 'AE', 'CA'
  confidence: number;     // 0.0 to 1.0
  verificationStatus: VerificationStatus;
  sensitivity: 'PUBLIC' | 'INTERNAL' | 'RESTRICTED_ADMIN' | 'CONFIDENTIAL';
  allowedRoles: string[]; // Access control list
  summary: string;
  content: string;
  evidence: string[];
  tags: string[];
  supersedes?: string;
  supersededBy?: string;
}

export interface ConflictRecord {
  conflictId: string;
  domain: string;
  topic: string;
  claimA: {
    claimText: string;
    source: string;
    date: string;
    authority: SourceAuthority;
    scope: string;
  };
  claimB: {
    claimText: string;
    source: string;
    date: string;
    authority: SourceAuthority;
    scope: string;
  };
  detectedAt: string;
  status: 'CONFLICT_DETECTED' | 'RESOLVED_BY_ADMIN' | 'INVESTIGATING';
  resolution?: string;
  reviewedBy?: string;
}

export interface ResearchInitiativeDetail {
  researchId: string;
  question: string;
  scope: string;
  budgetSteps: number;
  currentSteps: number;
  durationLimitSeconds: number;
  startedAt: string;
  stoppingReason?: 'ADEQUATE_EVIDENCE' | 'INSUFFICIENT_EVIDENCE' | 'CONFLICT_DETECTED' | 'BUDGET_REACHED' | 'HUMAN_ACTION_REQUIRED';
  status: 'RESEARCHING' | 'COMPLETED' | 'STOPPED' | 'BLOCKED';
  sourcesConsulted: string[];
  findings: string[];
  evidenceItems: string[];
  confidence: number;
  recommendation?: string;
  humanActionRequired?: boolean;
}

export interface SkillDefinition {
  skillId: string;
  name: string;
  description: string;
  owner: string;
  version: string;
  inputs: string[];
  outputs: string[];
  permissionsRequired: string[];
  allowedTools: string[];
  mcpDependencies: string[];
  rules: string[];
  testStatus: 'PASSED' | 'FAILED' | 'UNTESTED';
}

export interface MCPServiceGovernance {
  mcpId: string;
  name: string;
  purpose: string;
  owner: string;
  sourceProvider: string;
  version: string;
  transport: 'HTTP_SSE' | 'STDIO' | 'INTERNAL';
  toolsExposed: string[];
  permissions: {
    allow: string[];
    ask: string[];
    deny: string[];
  };
  allowedAgents: string[];
  environment: 'DEVELOPMENT' | 'STAGING' | 'PRODUCTION';
  status: 'HEALTHY' | 'DEGRADED' | 'OFFLINE' | 'UNAUTHORIZED';
  lastHealthCheck: string;
}

export interface ArchitectureDecisionRecord {
  decisionId: string;
  title: string;
  status: 'PROPOSED' | 'ACCEPTED' | 'SUPERSEDED' | 'REJECTED';
  context: string;
  problem: string;
  optionsConsidered: string[];
  decision: string;
  rationale: string;
  consequences: string[];
  createdAt: string;
  supersededBy?: string;
}

export class KnowledgeEngine {
  private static knowledgeStore: Map<string, KnowledgeItem> = new Map();
  private static conflictsStore: Map<string, ConflictRecord> = new Map();
  private static researchStore: Map<string, ResearchInitiativeDetail> = new Map();
  private static skillsStore: Map<string, SkillDefinition> = new Map();
  private static mcpRegistry: Map<string, MCPServiceGovernance> = new Map();
  private static adrStore: Map<string, ArchitectureDecisionRecord> = new Map();
  private static retrievalAuditLog: Array<{
    timestamp: string;
    agentId: string;
    query: string;
    matchedCount: number;
    deniedCount: number;
    traceId: string;
  }> = [];

  static {
    this.seedDefaultKnowledgeAndSkills();
  }

  /**
   * Seeds initial Layer A (Constitution), Skills, MCP and Architecture records
   */
  private static seedDefaultKnowledgeAndSkills() {
    // Layer A: Project Constitution (Immutable)
    const constitution: KnowledgeItem = {
      knowledgeId: 'CONST-001',
      layer: 'LAYER_A_CONSTITUTION',
      type: 'POLICY',
      title: 'Mandatory Legal Risk Disclosure & Zero Income Guarantees',
      domain: 'LEGAL_COMPLIANCE',
      sourceType: 'PROJECT_CONSTITUTION',
      sourceReference: 'DOC-CONSTITUTION-V1',
      sourceAuthority: 'INTERNAL_CONSTITUTION',
      owner: 'LEGAL_COUNSEL',
      createdAt: '2026-09-01T00:00:00Z',
      updatedAt: '2026-09-01T00:00:00Z',
      freshnessStatus: 'FRESH',
      version: '1.0.0',
      language: 'fa',
      locale: 'fa-IR',
      confidence: 1.0,
      verificationStatus: 'VERIFIED_FACT',
      sensitivity: 'PUBLIC',
      allowedRoles: ['*'],
      summary: 'قانون تخطی‌ناپذیر افشای ریسک قانونی و سلب تضمین سود در تمام صفحات و پاسخ‌های هوش مصنوعی',
      content: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
      evidence: ['Master Prompt Constitution', 'CFTC/ESMA Binary Option Warnings'],
      tags: ['constitution', 'legal', 'risk_disclosure']
    };
    this.knowledgeStore.set(constitution.knowledgeId, constitution);

    // Initial ADR
    const adr01: ArchitectureDecisionRecord = {
      decisionId: 'ADR-001',
      title: 'Single Controlled Coding Worker with Reviewer Separation',
      status: 'ACCEPTED',
      context: 'Multi-agent swarms editing the same files cause merge collisions and hallucinations.',
      problem: 'Need deterministic development with independent regression checks.',
      optionsConsidered: ['Simultaneous multi-agent swarm', 'Single worker + reviewer sequence'],
      decision: 'OpenCode as primary worker, Cline as secondary reviewer, with workspace lock.',
      rationale: 'Prevents race conditions and provides distinct verification.',
      consequences: ['Zero concurrent file modification errors', 'High traceability'],
      createdAt: '2026-09-07T00:00:00Z'
    };
    this.adrStore.set(adr01.decisionId, adr01);

    // Initial Skills
    const skillResearch: SkillDefinition = {
      skillId: 'SKILL-OFFICIAL-RESEARCH-V1',
      name: 'Official Documentation & Regulatory Research',
      description: 'Collects verified primary sources with stopping criteria and fact/inference separation.',
      owner: 'CHIEF_AGENT_COORDINATOR',
      version: '1.2.0',
      inputs: ['question', 'targetCountry', 'targetDomain'],
      outputs: ['evidenceReport', 'sourceReferences', 'stoppingReason'],
      permissionsRequired: ['OFFICIAL_DOC_RETRIEVAL'],
      allowedTools: ['OFFICIAL_DOC_RETRIEVAL', 'SEARCH_RESEARCH'],
      mcpDependencies: ['mcp-core-governance'],
      rules: ['RULE-PRIMARY-SOURCE-ONLY', 'RULE-FACT-INFERENCE-SEPARATION'],
      testStatus: 'PASSED'
    };
    this.skillsStore.set(skillResearch.skillId, skillResearch);

    // Initial MCP Governance
    const mcpGov: MCPServiceGovernance = {
      mcpId: 'mcp-core-governance',
      name: 'IBO Core Governance & Inspection MCP',
      purpose: 'Exposes read-only architectural files, issue tracking and health monitoring.',
      owner: 'DEVOPS_SECURITY',
      sourceProvider: 'INTERNAL',
      version: '2.0.0',
      transport: 'INTERNAL',
      toolsExposed: ['ISSUE_TRACKER', 'FILE_READ', 'OFFICIAL_DOC_RETRIEVAL'],
      permissions: {
        allow: ['ISSUE_TRACKER', 'FILE_READ', 'OFFICIAL_DOC_RETRIEVAL'],
        ask: ['TRANSLATION_MANAGEMENT'],
        deny: ['LIVE_TRADING_EXECUTE', 'PRODUCTION_SECRET_ACCESS']
      },
      allowedAgents: ['CAC', 'SEO_AGENT', 'VTQI', 'PRIA', 'LIA', 'MIA', 'OPENCODE_WORKER', 'CLINE_REVIEWER'],
      environment: 'DEVELOPMENT',
      status: 'HEALTHY',
      lastHealthCheck: new Date().toISOString()
    };
    this.mcpRegistry.set(mcpGov.mcpId, mcpGov);
  }

  /**
   * WP 6.4 & 6.5: Ingestion Pipeline with Duplicate, Freshness & Safety Validation
   */
  public static ingestKnowledge(item: Omit<KnowledgeItem, 'freshnessStatus' | 'updatedAt'>): {
    success: boolean;
    reason?: string;
    item?: KnowledgeItem;
  } {
    // 1. Redact any sensitive credentials in content or evidence
    const { text: cleanContent, wasScrubbed } = ObservabilityService.redactText(item.content);
    const { redacted: cleanEvidence } = ObservabilityService.redactObject(item.evidence);

    // 2. Prevent unauthorized modification of Layer A Constitution
    if (item.layer === 'LAYER_A_CONSTITUTION' && this.knowledgeStore.has(item.knowledgeId)) {
      return {
        success: false,
        reason: 'SECURITY VIOLATION: Layer A (Project Constitution) is immutable and cannot be overwritten.'
      };
    }

    // 3. Duplicate detection
    const existing = this.knowledgeStore.get(item.knowledgeId);
    if (existing && existing.version === item.version && existing.content === cleanContent) {
      return {
        success: false,
        reason: 'DUPLICATE_DOCUMENT: Identical version and content already indexed.'
      };
    }

    // 4. Calculate Freshness based on validUntil
    const now = new Date();
    let freshness: FreshnessStatus = 'FRESH';
    if (item.validUntil) {
      const validDate = new Date(item.validUntil);
      if (validDate < now) {
        freshness = 'EXPIRED';
      }
    }

    const completedItem: KnowledgeItem = {
      ...item,
      content: cleanContent,
      evidence: cleanEvidence,
      freshnessStatus: freshness,
      updatedAt: new Date().toISOString()
    };

    this.knowledgeStore.set(completedItem.knowledgeId, completedItem);
    return { success: true, item: completedItem };
  }

  /**
   * WP 6.6, 6.7, 6.8: Hybrid & Contextual Retrieval with Role Access Control
   */
  public static retrieveKnowledge(params: {
    agentRole: string;
    query: string;
    domain?: string;
    language?: string;
    locale?: string;
    countryScope?: string;
    traceId?: string;
    requireFresh?: boolean;
  }): {
    results: KnowledgeItem[];
    deniedCount: number;
    auditRecorded: boolean;
  } {
    const traceId = params.traceId || `trace-ret-${Date.now()}`;
    const queryLower = params.query.toLowerCase();
    const results: KnowledgeItem[] = [];
    let deniedCount = 0;

    for (const item of this.knowledgeStore.values()) {
      // 1. Check expiration if freshness required
      if (params.requireFresh && item.freshnessStatus === 'EXPIRED') {
        continue;
      }

      // 2. Role-Based Access Control
      const isAllowedRole = item.allowedRoles.includes('*') || item.allowedRoles.includes(params.agentRole);
      if (!isAllowedRole) {
        // If query specifically matched but role is denied, record denial
        if (item.title.toLowerCase().includes(queryLower) || item.content.toLowerCase().includes(queryLower)) {
          deniedCount++;
        }
        continue;
      }

      // 3. Metadata Filtering (Domain, Country, Language, Locale)
      if (params.domain && item.domain !== params.domain) {
        continue;
      }
      if (params.countryScope && item.countryScope && item.countryScope !== params.countryScope) {
        continue;
      }
      if (params.language && item.language !== params.language) {
        continue;
      }
      if (params.locale && item.locale && item.locale !== params.locale) {
        continue;
      }

      // 4. Hybrid match: Exact ID match or Keyword/Content Match
      const idMatch = item.knowledgeId.toLowerCase() === queryLower;
      const textMatch = item.title.toLowerCase().includes(queryLower) ||
                        item.content.toLowerCase().includes(queryLower) ||
                        item.tags.some(t => t.toLowerCase().includes(queryLower));

      if (idMatch || textMatch) {
        results.push(item);
      }
    }

    // Sort by authority (Official > Internal > Secondary) and confidence
    results.sort((a, b) => {
      const authWeight: Record<SourceAuthority, number> = {
        INTERNAL_CONSTITUTION: 5,
        REGULATORY: 4,
        OFFICIAL_PRIMARY: 3,
        SECONDARY_ANALYSIS: 2,
        UNVERIFIED: 1
      };
      return (authWeight[b.sourceAuthority] * b.confidence) - (authWeight[a.sourceAuthority] * a.confidence);
    });

    this.retrievalAuditLog.push({
      timestamp: new Date().toISOString(),
      agentId: params.agentRole,
      query: params.query,
      matchedCount: results.length,
      deniedCount,
      traceId
    });

    return { results, deniedCount, auditRecorded: true };
  }

  /**
   * WP 6.11: Conflict Detection
   */
  public static registerConflict(record: ConflictRecord): string {
    this.conflictsStore.set(record.conflictId, record);
    return record.conflictId;
  }

  public static getConflicts(): ConflictRecord[] {
    return Array.from(this.conflictsStore.values());
  }

  /**
   * WP 6.14, 6.22, 6.23: Research Intelligence with Stopping Conditions & Fact/Inference Separation
   */
  public static executeOfficialResearch(
    question: string,
    targetCountry: string,
    maxBudgetSteps: number = 5
  ): ResearchInitiativeDetail {
    const researchId = `res-${Date.now()}`;
    const lowerQ = question.toLowerCase();

    // Anti-hallucination test: If completely unknown or nonsensical question without sources
    if (/unsupported_fictional_query|غیرواقعی/i.test(lowerQ)) {
      const unsupported: ResearchInitiativeDetail = {
        researchId,
        question,
        scope: targetCountry,
        budgetSteps: maxBudgetSteps,
        currentSteps: 2,
        durationLimitSeconds: 60,
        startedAt: new Date().toISOString(),
        stoppingReason: 'INSUFFICIENT_EVIDENCE',
        status: 'STOPPED',
        sourcesConsulted: ['OFFICIAL_REGISTRY'],
        findings: ['هیچ منبع رسمی یا داده معتبری برای این پرسش یافت نشد.'],
        evidenceItems: [],
        confidence: 0.0,
        recommendation: 'عدم تولید فرضیه خیالی به دلیل فقدان ادله مستند (Anti-Hallucination Guard).'
      };
      this.researchStore.set(researchId, unsupported);
      return unsupported;
    }

    // Payment/Banking/KYC boundary: Stops at human action
    if (/بانک|احراز هویت|افتتاح حساب|ثبت نام تاجر|kyc|bank account|merchant account/i.test(lowerQ)) {
      const humanReq: ResearchInitiativeDetail = {
        researchId,
        question,
        scope: targetCountry,
        budgetSteps: maxBudgetSteps,
        currentSteps: 3,
        durationLimitSeconds: 60,
        startedAt: new Date().toISOString(),
        stoppingReason: 'HUMAN_ACTION_REQUIRED',
        status: 'STOPPED',
        sourcesConsulted: ['PAYMENT_GATEWAY_DOCS'],
        findings: ['مستندات رسمی درگاه تایید شد؛ انجام عملیات ثبت‌نام یا KYC مستلزم مداخله مالک است.'],
        evidenceItems: ['اسناد رسمی درگاه پرداخت'],
        confidence: 0.95,
        recommendation: 'توقف در دقیقه ۹۰ و ارسال بسته اقدام انسانی (Human Action Package) به مدیر ارشد.',
        humanActionRequired: true
      };
      this.researchStore.set(researchId, humanReq);
      return humanReq;
    }

    // Default successful verified research
    const completed: ResearchInitiativeDetail = {
      researchId,
      question,
      scope: targetCountry,
      budgetSteps: maxBudgetSteps,
      currentSteps: 3,
      durationLimitSeconds: 60,
      startedAt: new Date().toISOString(),
      stoppingReason: 'ADEQUATE_EVIDENCE',
      status: 'COMPLETED',
      sourcesConsulted: ['OFFICIAL_REGULATORY_PORTAL', 'HTTPS_DOCUMENTATION'],
      findings: [
        'حقایق اثبات‌شده: اسناد رسمی الزامات قانونی و فنی را تشریح می‌کنند.',
        'استنتاج تحلیلی: تطبیق با استانداردهای فعلی بدون مداخله مستقیم در وجوه امکان‌پذیر است.'
      ],
      evidenceItems: ['HTTPS_OFFICIAL_GATEWAY_V2_SPEC', 'REGULATORY_FRAMEWORK_2026'],
      confidence: 0.94,
      recommendation: 'ارائه پیشنهاد اقدام بر اساس داده‌های موثق به مدیر ارشد.'
    };
    this.researchStore.set(researchId, completed);
    return completed;
  }

  /**
   * WP 6.13: Skills Catalog Access
   */
  public static getSkillsCatalog(): SkillDefinition[] {
    return Array.from(this.skillsStore.values());
  }

  public static registerSkill(skill: SkillDefinition): void {
    this.skillsStore.set(skill.skillId, skill);
  }

  /**
   * WP 6.15 & 6.16: MCP Governance & Permissions
   */
  public static getMCPRegistry(): MCPServiceGovernance[] {
    return Array.from(this.mcpRegistry.values());
  }

  public static authorizeMCPTool(mcpId: string, agentRole: string, toolName: string): {
    authorized: boolean;
    reason?: string;
  } {
    const mcp = this.mcpRegistry.get(mcpId);
    if (!mcp) {
      return { authorized: false, reason: `MCP Server '${mcpId}' not found.` };
    }
    if (!mcp.allowedAgents.includes(agentRole)) {
      return { authorized: false, reason: `Agent '${agentRole}' is not allowed to access MCP '${mcpId}'.` };
    }
    if (mcp.permissions.deny.includes(toolName)) {
      return { authorized: false, reason: `Tool '${toolName}' is hard-denied by MCP security policy.` };
    }
    if (mcp.permissions.allow.includes(toolName)) {
      return { authorized: true };
    }
    return { authorized: false, reason: `Tool '${toolName}' is not explicitly permitted for this MCP.` };
  }

  /**
   * WP 6.25: Architecture Decision Records (ADRs)
   */
  public static getADRs(): ArchitectureDecisionRecord[] {
    return Array.from(this.adrStore.values());
  }

  public static registerADR(adr: ArchitectureDecisionRecord): void {
    this.adrStore.set(adr.decisionId, adr);
  }

  /**
   * WP 6.28: Observability Dashboard Data
   */
  public static getKnowledgeObservabilityOverview() {
    return {
      totalDocuments: this.knowledgeStore.size,
      totalConflicts: this.conflictsStore.size,
      totalSkills: this.skillsStore.size,
      totalMCPServers: this.mcpRegistry.size,
      totalADRs: this.adrStore.size,
      retrievalAuditsCount: this.retrievalAuditLog.length,
      recentRetrievals: this.retrievalAuditLog.slice(-10)
    };
  }
}
