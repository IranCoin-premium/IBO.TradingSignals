/**
 * IBO Ecosystem — MCP Tool Boundaries & Cross-Agent Collaboration
 * Part 03: Work Packages 3.12 & 3.13
 * 
 * Rules:
 * 1. Least-Privilege Role-Based Tool Invocation (RULE-MCP-001 to RULE-MCP-008).
 * 2. Auditable handoff pipeline: SEO -> Market -> Locale -> Payment -> Human Handoff.
 */

export type AgentRole =
  | 'SEO_AGENT'
  | 'MARKET_INTELLIGENCE_AGENT'
  | 'LOCALE_INTELLIGENCE_AGENT'
  | 'PAYMENT_READINESS_AGENT'
  | 'GLOBAL_GROWTH_MANAGER'
  | 'CHIEF_AGENT_COORDINATOR'
  | 'VTQI';

export type ToolCategory =
  | 'SEARCH_RESEARCH'
  | 'OFFICIAL_DOC_RETRIEVAL'
  | 'SITE_AUDIT'
  | 'TRANSLATION_MANAGEMENT'
  | 'PAYMENT_RESEARCH'
  | 'ISSUE_TRACKER'
  | 'DISPATCH_HUMAN_PACKAGE';

export const ROLE_TOOL_PERMISSIONS: Record<AgentRole, ToolCategory[]> = {
  SEO_AGENT: ['SEARCH_RESEARCH', 'SITE_AUDIT'],
  MARKET_INTELLIGENCE_AGENT: ['SEARCH_RESEARCH', 'OFFICIAL_DOC_RETRIEVAL', 'ISSUE_TRACKER'],
  LOCALE_INTELLIGENCE_AGENT: ['TRANSLATION_MANAGEMENT', 'OFFICIAL_DOC_RETRIEVAL'],
  PAYMENT_READINESS_AGENT: ['OFFICIAL_DOC_RETRIEVAL', 'PAYMENT_RESEARCH', 'DISPATCH_HUMAN_PACKAGE'],
  GLOBAL_GROWTH_MANAGER: ['ISSUE_TRACKER', 'OFFICIAL_DOC_RETRIEVAL'],
  CHIEF_AGENT_COORDINATOR: ['ISSUE_TRACKER'],
  VTQI: ['SITE_AUDIT']
};

export interface AgentCollaborationHandoff {
  traceId: string;
  initiatingAgent: AgentRole;
  receivingAgent: AgentRole;
  stage: 'SEO_DEMAND' | 'MARKET_EVALUATION' | 'LOCALE_ADAPTATION' | 'PAYMENT_VERIFICATION' | 'HUMAN_HANDOFF';
  payloadSummary: string;
  timestamp: string;
  verified: boolean;
}

export class MCPBoundaryGuard {
  /**
   * Validates whether an agent has authorization to invoke a tool in a given category
   */
  public static authorizeToolInvocation(role: AgentRole, toolCategory: ToolCategory): { authorized: boolean; reason?: string } {
    const allowed = ROLE_TOOL_PERMISSIONS[role] || [];
    if (!allowed.includes(toolCategory)) {
      return {
        authorized: false,
        reason: `RULE-MCP-008 VIOLATION: Role '${role}' is not authorized to invoke tool category '${toolCategory}'. Access denied.`
      };
    }
    return { authorized: true };
  }

  /**
   * Records an auditable pipeline transition
   */
  public static recordHandoff(handoff: AgentCollaborationHandoff): { success: boolean; logEntry: string } {
    const logEntry = `[COLLABORATION_TRACE][${handoff.traceId}] ${handoff.initiatingAgent} -> ${handoff.receivingAgent} (${handoff.stage}): ${handoff.payloadSummary}`;
    return {
      success: true,
      logEntry
    };
  }
}
