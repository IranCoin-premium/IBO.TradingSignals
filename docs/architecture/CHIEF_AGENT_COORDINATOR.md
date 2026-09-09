# IBO Project — Chief Agent Coordinator (CAC) Architecture
**Document Version:** 1.0.0 — Phase 2  
**Scope:** Master Prompt Part 2 — Work Package 2.7  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Philosophy

The **Chief Agent Coordinator (CAC)** is an architectural role responsible for high-level coordination, cross-agent conflict resolution, and priority sequencing across the IBO autonomous ecosystem.

### Critical Safety Invariants:
- ❌ **CAC is NOT an unrestricted super-AI.**
- ❌ **CAC is NOT the owner of the production system.**
- ❌ **CAC cannot override security policies or bypass 10-level verification gates.**
- ❌ **CAC cannot execute direct deployments to production.**
- ❌ **CAC cannot directly execute trades or transfer user funds.**
- ❌ **CAC does not have raw write access to the PostgreSQL database or private KMS vaults.**

---

## 2. Authority Hierarchy

```
+-----------------------------------------------------------------------------+
|                       HUMAN OWNER / CEO (Supreme Authority)                 |
| (Protected rights: release approval, kill-switch control, financial signs)  |
+-----------------------------------------------------------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                     CHIEF AGENT COORDINATOR (CAC) Layer                     |
| (Coordinates tasks, reads summaries, detects bottlenecks, orders sequence) |
+-----------------------------------------------------------------------------+
                                      |
         +----------------------------+----------------------------+
         |                                                         |
         v                                                         v
+-----------------------------+                           +-----------------------------+
|     DEVELOPMENT MANAGER     |                           |       QUALITY MANAGER       |
| (Coding Agent, Refactoring) |                           | (VTQI System, Audit Gates)  |
+-----------------------------+                           +-----------------------------+
         |                                                         |
         +----------------------------+----------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                   CONTROLLED MCP TOOLS & RUNTIME WORKERS                    |
| (Bounded tools: compile_applet, git diff, static analyzers, test runners)   |
+-----------------------------------------------------------------------------+
```

---

## 3. Allowed Inputs vs. Forbidden Inputs for CAC

### Allowed Inputs (Least Privilege):
- **Structured Worker Summaries:** High-level JSON outcomes from VTQI, Coding Agent, and CI/CD pipelines.
- **System Health Metrics:** CPU/Memory utilization, HTTP error rates (5xx counters), queue depths.
- **Task Status Registers:** Current counts of `QUEUED`, `RUNNING`, `BLOCKED`, and `FAILED` tasks.
- **Verification Gate Reports:** Pass/fail logs from `verify-full-10level.sh`, `jest`, `compile_applet`.

### Forbidden Inputs (Access Denied):
- **Raw User PII & Passwords:** Zero access to user email lists, password hashes, or identity documents.
- **Private Financial Credentials:** Zero access to wallet private keys, merchant secrets, or banking tokens.
- **Raw Keystores:** Zero access to `release.keystore` or code signing certificates.

---

## 4. Core Responsibilities of CAC

1. **Sequencing & Prioritization:** If a visual defect is flagged by VTQI while a core build failure exists, CAC prioritizes resolving the build failure before requesting re-renders.
2. **Conflict & Duplication Detection:** Prevents two agents from concurrently modifying the same file or dispatching conflicting PRs.
3. **Graceful Escalation:** When a task exhausts its 3 automatic retry attempts, CAC bundles the failure context and escalates to the Human Administrator.
