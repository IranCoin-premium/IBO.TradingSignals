# IBO Primary AI Coding Agent Integration & Worker Layer
**Document Version:** 1.0.0 — Phase 5  
**Scope:** Master Prompt Part 5 Implementation Architecture & Operational Manual  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Worker Principle

Phase 5 establishes a secure, auditable, and controlled bridge between the IBO orchestration layer (n8n & Control Center) and AI coding workers.

### Core Invariants:
1. **Coding Agents are Workers, Not Orchestrators:**
   Workers do not manage state, do not deploy directly to production, and do not execute live trading.
2. **Deterministic Sequence:**
   `Task -> OpenCode (Primary) -> Local Tests -> Cline (Secondary Reviewer) -> QA Gate`.
   Simultaneous uncontrolled writes by multiple agents on the same files are blocked by workspace locks.
3. **Evidence-Based Acceptance (Verification Separation):**
   Natural language claims like `"Tests passed"` are systematically rejected unless backed by exit code 0, test output logs, and independent inspection.

---

## 2. Autonomy Classes & Permissions

| Class | Level | Permitted Actions | Approval Requirement | Guardrail |
|:---:|:---:|:---|:---:|:---|
| **CLASS A** | Safe Autonomous | Read repo, inspect files, grep, run approved tests, lint, format | None | Read-only / Non-destructive |
| **CLASS B** | Controlled Autonomous | Edit project code, add dev dependencies, create local branches | Automated verification | Workspace lock & test pass |
| **CLASS C** | Approval Required | Push protected branch, production config change, staging deploy | Human Admin Approval Token | Scoped time-bound token |
| **CLASS D** | Hard Deny | Exfiltrate secrets, bypass auth/MFA, live trading changes, drop DB | HARD DENIED (Fail Closed) | Security audit alert |

---

## 3. Worker Architecture & Fallback Flow

```
   [ IBO Control Plane / Admin ]
                 |
                 v
         [ n8n Workflow ]
                 |
                 v
     [ CodingAgentAdapter ]
       /                \
 (Primary)          (Fallback / Review)
      v                     v
[ OpenCode Headless ]  [ Cline CLI ]
      \                     /
       v                   v
   [ Workspace Lock & Local Tests ]
                 |
                 v
   [ Independent Verifier Gate ]
                 |
                 v
   [ Structured Result & Audit ]
```

---

## 4. Work Package Verification Summary (Part 5)

- **WP 5.1 (Version Audit):** Detected Node v22.23.2, npm 10.9.8; OpenCode headless 2.1.0 and Cline 3.4.0 registered in control plane.
- **WP 5.2 & 5.3 (Safe Write & Verify):** Executed development task with verified exit code 0 and structured artifacts.
- **WP 5.4 (n8n Adapter):** `AutomationPlatformAdapter.dispatchCodingTaskFromN8n` correlates workflow, task ID, and execution traces.
- **WP 5.5 (Persistent Tasks & Idempotency):** Duplicate dispatches with identical `idempotencyKey` link back to original task.
- **WP 5.6 (Reason-Aware Retry):** Transient network errors trigger `RETRY_NEEDED` and increment `retryCount` without infinite looping.
- **WP 5.7 (Fallback Worker):** When `OPENCODE` is offline, adapter automatically routes to `CLINE` and records audit event.
- **WP 5.8 (Review Handoff):** Generates structured review payload containing risks, changed files, and requested scope for the reviewer.
- **WP 5.9 & 5.18 (Security Gates):** Class C requires admin approval token; Class D is hard-blocked and logged.
- **WP 5.12 (Workspace Concurrency Lock):** Concurrent write requests to the same path are blocked (`BLOCKED_LOCKED`).
- **WP 5.13 (Verification Separation):** Verifier catches failing evidence even if model claims text success.
- **WP 5.14 & 5.15 (Admin Observability):** Traceability from task to worker session with Admin-Safe explanations (no raw chain-of-thought leaks).
