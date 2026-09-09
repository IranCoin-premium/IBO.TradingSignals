# IBO Task Orchestration, Queue, Workers & 24/7 Autonomous Architecture
**Document Version:** 1.0.0 — Phase 7  
**Scope:** Master Prompt Part 7 Architecture, State Machine, Recovery & Governance  

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Core Principle

Phase 7 establishes the foundation for a 24/7 reliable Task Orchestration system for the IBO ecosystem.  
The core principle is:
$$\text{SYSTEM READY} \to \text{EVENT} \to \text{TASK} \to \text{ROUTE} \to \text{WORKER} \to \text{EXECUTE} \to \text{VERIFY} \to \text{RESULT} \to \text{NEXT}$$

The infrastructure and queue remain permanently alive, while AI Agents/Workers execute **on-demand** with strict concurrency caps, budgets, and timeout policies (availability $\ne$ runaway token consumption).

---

## 2. Responsibilities Separation

| Component | Role & Scope | Boundary |
|:---|:---|:---|
| **Orchestrator** | Schedules, routes, tracks state transitions, manages dependencies. | Does not execute tasks directly. |
| **Task Queue** | Persists pending, running, retryable, and dead-letter tasks. | Survives restarts; supports idempotency. |
| **Workers** | Performs concrete actions (OpenCode, Cline, QA, Research). | Scoped tools and MCP permissions. |
| **Scheduler** | Triggers time-based maintenance with misfire protection. | Prevents storming/catch-up duplicate jobs. |
| **Circuit Breaker** | Isolates failing external MCP/API services. | CLOSED $\to$ OPEN $\to$ HALF_OPEN. |
| **Human Action Gate** | Stops at KYC, Banking, and production safety limits. | Generates Persian Human Action Package. |

---

## 3. State Machine & Transitions

Valid lifecycle states:
- `NEW` $\to$ `QUEUED`
- `QUEUED` $\to$ `CLAIMED`
- `CLAIMED` $\to$ `RUNNING`
- `RUNNING` $\to$ `VERIFYING` $\mid$ `RETRY` $\mid$ `BLOCKED` $\mid$ `FAILED` $\mid$ `DEAD_LETTER`
- `VERIFYING` $\to$ `SUCCESS` $\mid$ `PARTIAL_SUCCESS` $\mid$ `RETRY` $\mid$ `FAILED`
- `RETRY` $\to$ `QUEUED` $\mid$ `ESCALATED`
- `FAILED` $\to$ `ESCALATED` $\mid$ `DEAD_LETTER`
- `ESCALATED` $\to$ `QUEUED` (Requires human decision)
