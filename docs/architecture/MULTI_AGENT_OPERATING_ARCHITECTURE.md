# IBO Multi-Agent Operating Architecture & Governance
**Document Version:** 1.0.0 — Phase 8  
**Scope:** Master Prompt Part 8: Chief Agent, Specialist Agents, Delegation, Execution Gate, Conflict Resolution & Governance  

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Core Principle

Phase 8 defines and implements the **Governed Multi-Agent Operating Architecture** for the IBO ecosystem.  
The core principle is:
$$\text{INTELLIGENCE} \to \text{GOVERNANCE} \to \text{ORCHESTRATION} \to \text{EXECUTION} \to \text{VERIFICATION} \to \text{EVIDENCE} \to \text{AUDIT}$$

The system operates as a governed organization of specialized AI agents under central coordination (Chief Agent Coordinator) and human owner authority.

---

## 2. Agent Registry & Specialist Roles

| Agent Name | Role & Type | Scope & Boundaries |
|:---|:---|:---|
| **Chief Agent Coordinator** | Strategic Coordinator | Plan generation, task graph construction, risk analysis, specialist consultation. No direct trading or secret extraction. |
| **Engineering Specialist Agent** | Software Development | Code edits, unit testing, sandboxed workspace implementation. No direct production deploy. |
| **Visual QA Specialist** | Visual & Viewport QA | Multi-viewport responsiveness (320px–2560px), Soft-UI layout, typography, contrast. No backend edits. |
| **Security Guardian** | Security Policy Enforcement | Secret scan, permission check, fail-closed policy evaluation. Zero-trust invariant enforcement. |

---

## 3. Governance Invariants & Hard Boundaries

1. **Explicit Execution Mode Gate:** Every task requires a canonical mode (`READ_ONLY`, `DRY_RUN`, `STAGING`, `PRODUCTION`). Child tasks cannot elevate execution modes above their parent authority.
2. **Unified Task Status Vocabulary:** Strict compliance with canonical statuses (`NEW`, `QUEUED`, `CLAIMED`, `RUNNING`, `WAITING`, `VERIFYING`, `RETRYING`, `SUCCESS`, `PARTIAL_SUCCESS`, `BLOCKED`, `FAILED`, `ESCALATED`, `CANCEL_REQUESTED`, `CANCELLED`, `EXPIRED`).
3. **Conflict Resolution & Preservation of Dissent:** If Security reports `FAIL`, the system immediately fails closed (`BLOCK`). Dissenting findings are preserved in the Decision Journal.
4. **Approval Objects:** Consequential actions ($R3/R4$ risks and `PRODUCTION` modes) require explicit scoped approval objects.
5. **Admin-Safe Explanations:** Raw private chain-of-thought is never stored or exposed; structured operational summaries are recorded.
