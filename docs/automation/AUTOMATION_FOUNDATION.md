# IBO Project — Automation Foundation & AI Agent Adapter Architecture
**Document Version:** 1.0.0 — Phase 1  
**Scope:** Master Prompt Part 1 — Work Packages 6 & 7  

---

## Part A: Work Package 6 — n8n Role Definition & Security Boundary

### 1. Architectural Boundaries for n8n

n8n is the **Primary Workflow Orchestrator** for non-critical, event-driven, and scheduled operations.

#### What n8n IS Authorized to Do:
- Ingest scheduled cron triggers (e.g. hourly financial news feed fetch).
- Ingest asynchronous webhooks from third-party services and forward them to the Backend Core with HMAC signatures.
- Orchestrate notification dispatches (Telegram, email, push notifications).
- Trigger on-demand worker tasks via authenticated API endpoints.
- Manage workflow-level retries and operational failure escalations.

#### What n8n IS STRICTLY FORBIDDEN from Doing:
- **No Direct Financial Ledger Manipulation:** n8n must never directly alter user wallet balances, subscription statuses, or payout ledgers. The Backend Core API is the sole source of truth.
- **No Unrestricted Host Shell Access:** n8n containers must run as non-root with `Execute Command` nodes restricted or audited.
- **No Hardcoded API Keys or Credentials:** All integration credentials must use environment variable references or encrypted credential stores.

---

### 2. Five Standard n8n Workflow Categories

```
+-------------------------------------------------------------------------------+
| Category 1: Incident Workflow                                                 |
|  - Trigger: Critical service health failure (/health 503) or crash alert.    |
|  - Action: Capture error context, notify Admin Telegram, trigger safe freeze. |
+-------------------------------------------------------------------------------+
| Category 2: Code-Change / CI/CD Workflow                                      |
|  - Trigger: Repository tag creation (vX.Y.Z) or manual dispatch webhook.       |
|  - Action: Trigger 10-level verification pipeline, verify gates, alert team.  |
+-------------------------------------------------------------------------------+
| Category 3: Scheduled Audit Workflow                                          |
|  - Trigger: Daily cron (e.g. 03:00 UTC).                                      |
|  - Action: Run security scan, check stale subscriptions, verify DB integrity. |
+-------------------------------------------------------------------------------+
| Category 4: Monitoring Alert Workflow                                         |
|  - Trigger: Unhandled 500 error spikes, rate limit breaches, anomaly events. |
|  - Action: Filter noise, route severity-graded alerts to ops channels.        |
+-------------------------------------------------------------------------------+
| Category 5: Manual CEO / Admin Command Workflow                               |
|  - Trigger: Authenticated admin action (e.g., manual emergency kill-switch).   |
|  - Action: Issue signed instruction to Backend API with audit log generation. |
+-------------------------------------------------------------------------------+
```

---

## Part B: Work Package 7 — Primary AI Coding Agent Adapter Design

### 1. Decoupled Adapter Interface

To prevent vendor lock-in or fragile coupling to a specific AI tool, the automation and orchestration layers interact with coding agents through a normalized **Coding Agent Adapter Interface**.

```
[ Automation / Orchestration Layer (n8n / CI / CLI) ]
                        |
                        v
         [ CodingAgentAdapter (Interface) ]
                        |
         +--------------+--------------+
         |                             |
         v                             v
[ OpenCodeAdapter ]           [ AIStudio/HeadlessAdapter ]
```

### 2. Conceptual Contract (TypeScript Specification)

```typescript
export interface AgentTaskRequest {
  taskId: string;
  category: 'BUG_FIX' | 'FEATURE' | 'REFACTOR' | 'DOCS' | 'SECURITY_PATCH';
  title: string;
  description: string;
  targetFiles: string[];
  testCommand: string;
  constraints: {
    maxFileModifications: number;
    forbiddenPatterns: string[];
    mandatoryRiskDisclaimer: boolean;
  };
}

export interface AgentTaskResponse {
  taskId: string;
  status: 'PROPOSED' | 'VERIFYING' | 'SUCCESS' | 'BLOCKED' | 'FAILED';
  modifiedFiles: string[];
  verificationOutput: string;
  patchSummary: string;
  failureReason?: string;
  recommendedNextAction?: string;
}

export interface CodingAgentAdapter {
  name: string;
  version: string;
  analyzeTask(request: AgentTaskRequest): Promise<{ feasible: boolean; affectedFiles: string[] }>;
  executeTask(request: AgentTaskRequest): Promise<AgentTaskResponse>;
  runVerification(testCommand: string): Promise<{ passed: boolean; output: string }>;
}
```

### 3. Safety Guarantees in Adapter Execution
- **Strict Diff Boundary:** The agent cannot touch files outside `targetFiles` without explicit escalation.
- **Fail-Closed on Test Failures:** If `runVerification` fails, the task state transitions to `FAILED` or `RETRY` (up to 3 attempts), never to `SUCCESS`.
- **Audit Logging:** Every proposed patch and test log must be saved for human oversight.
