# IBO Project — Architecture Baseline & System Foundation
**Document Version:** 1.0.0 — Phase 1 (Foundation & Architecture Freeze)  
**System:** IBO Binary Option Trading Signals  
**Classification:** Internal Architectural Standard  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Core Philosophy

The IBO system is designed to provide high-probability binary option trading signals combining **expert human analysts** with **AI verification (Human-in-the-loop)**.

### The 24/7 Architectural Principle:
**The system stays available 24/7. AI agents do not need to think 24/7.**

- **Infrastructure:** Always on (High Availability).
- **Monitoring & Health:** Always active.
- **Event Listeners & Webhooks:** Always responsive.
- **Schedulers (Cron/Timers):** Trigger periodic audits and maintenance.
- **AI Agents:** Invoked **on-demand** when there is concrete, actionable, authorized work.
- **Failure Isolation:** An AI agent failure or blocked task must never halt unrelated system components or interrupt critical user traffic.

---

## 2. Five-Tier Target Conceptual Architecture

```
+-----------------------------------------------------------------------------+
| Layer E: Applications                                                       |
|  - Android Native App (Jetpack Compose, Room, M3 Soft-UI)                   |
|  - PWA / Web Client (Planned)                                               |
|  - Admin / CEO Management Interface                                         |
+-----------------------------------------------------------------------------+
                                      | (REST / JWT / Server-Authoritative)
+-----------------------------------------------------------------------------+
| Layer A: Infrastructure & Core Backend                                      |
|  - Node.js/TypeScript Monolith (Express, Zod, Winston)                      |
|  - PostgreSQL Database (Single Source of Truth for Financials/Users/Signals)|
|  - Host Runtime / Linux Container Environment                               |
+-----------------------------------------------------------------------------+
          ^                                                   ^
          | (Webhooks / Events)                               | (API & Controlled Tools)
+------------------------------------+   +------------------------------------+
| Layer B: Automation (n8n)          |   | Layer D: Tools & Integrations      |
|  - Event Orchestration             |---|  - Model Context Protocol (MCP)    |
|  - Scheduled Jobs (News, Audits)   |   |  - External APIs (NowPayments, etc.)|
|  - Non-Sensitive Notifications     |   |  - Controlled CLI Diagnostic Tools |
+------------------------------------+   +------------------------------------+
                  |
                  v (Action Requests)
+-----------------------------------------------------------------------------+
| Layer C: AI Workers & Autonomous Agents                                     |
|  - Coding Agent Adapter (OpenCode / Headless Agent Interface)              |
|  - Specialized Workers (UI/UX Auditor, Translator, Brand Guardian)          |
|  - Bounded Autonomy with 10-Level Gate Validation                           |
+-----------------------------------------------------------------------------+
```

---

## 3. Current State vs. Target State (Gap Analysis)

| Dimension | Current State (Verified) | Target State (Future Roadmap) | Gap / Action |
|---|---|---|---|
| **Android App** | Functional Android app (Compose, Room, Soft-UI, Retrofit for referrals, 100% building). | Production signed release, app store & direct distribution. | In-place update signing verified (`release.keystore`). |
| **Backend API** | Node.js/TS backend with Auth, RBAC, Purchases, Referrals, 56/56 passing unit tests. | Production clustered deployment with persistent PostgreSQL DB. | Part 1 freeze; cluster orchestration deferred. |
| **Web / PWA** | Partial component scaffold (`frontend/src/components/LanguageSwitcher.tsx`). No app shell. | Complete responsive Web Client / PWA. | Web shell to be developed in future phase; no premature mock. |
| **Database** | SQL migration files (001-007), TypeScript migrator, pool config with mock fallback in tests. | Managed PostgreSQL 15/16 database on persistent PaaS/IaaS. | Migrations syntax verified; connection string injection ready. |
| **n8n Automation** | `docker-compose.n8n.yml` configured with security and PostgreSQL storage settings. | Live running instance handling news ingest, notifications, and incident triggers. | Role defined; container deployment deferred to execution phase. |
| **AI Coding Agent** | Direct AI Studio / environment agents. | Decoupled adapter architecture supporting interchangeable agent backends. | Adapter interface specified in Work Package 7. |

---

## 4. Phase Boundaries (Strict Phase 1 Freeze)

### Explicitly Excluded from Part 1:
1. **No Production Deployments:** No DNS changes, no live server modifications.
2. **No Live Trading Integration:** No broker API access, no real money transactions.
3. **No Container Orchestration Swarms:** No Kubernetes, no multi-node Nomad/Swarm.
4. **No Heavy Secondary Infrastructure:** No Redis, Kafka, or RabbitMQ introduced prematurely.
5. **No Infinite Agent Loops:** No polling loops querying LLMs continuously.
6. **No Speculative Complex Multi-Agent Swarms:** Agent roles defined conceptually with adapter contracts.
