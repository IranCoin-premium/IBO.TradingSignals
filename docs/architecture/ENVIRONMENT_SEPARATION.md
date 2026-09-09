# IBO Project — Development, Staging & Production Separation
**Document Version:** 1.0.0 — Phase 2  
**Scope:** Master Prompt Part 2 — Work Package 2.2  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Boundaries

To prevent catastrophic configuration leakage, credential compromise, or unverified automated modifications to live systems, the IBO system establishes strict architectural separation between three environments: **Development**, **Staging**, and **Production**.

Under no circumstances may credentials, database connection strings, or automated permissions cross these boundaries.

---

## 2. Environment Comparison Matrix

| Dimension | DEVELOPMENT (Local / AI Studio) | STAGING (Pre-Release / Integration) | PRODUCTION (Protected User-Facing) |
|---|---|---|---|
| **Primary Purpose** | Fast iteration, local unit testing, UI/UX polish, static analysis. | Automated 10-level gates, E2E validation, load tests, pre-release sign-off. | Serving live end-users, delivering real-time signals, processing payments. |
| **Hosting Environment** | AI Studio container / local developer workstation. | Isolated cloud container cluster (Render / Railway / Fly.io / GCP). | Clustered, multi-region secure cloud infrastructure with SLA guarantees. |
| **Database Instance** | In-memory mock / local Docker PostgreSQL (`postgres:15-alpine`). | Isolated staging PostgreSQL instance with synthetic test data. | Highly available, encrypted-at-rest managed PostgreSQL with automated point-in-time backups. |
| **Database Data Policy** | **Synthetic / mock data only.** Zero real user records. Zero real PII. | Synthetic load fixtures. Scrubbed schema only. Real data strictly forbidden. | Real user profiles, cryptographic subscription ledgers, audit logs. |
| **Authentication & Secrets** | Development placeholder keys (`super_secret_jwt_dev...`). Local mock signatures. | Staging-only credentials rotated every 30 days. Stored in CI secret vault. | Production KMS / Vault injected secrets. Multi-party admin authorization required. |
| **Payment Gateways** | Sandbox / mock webhook dispatcher (`test-mode`). Zero financial settlement. | Provider testnet sandbox (NowPayments Sandbox, test USDT). | Live production merchant accounts with HMAC signature verification. |
| **Live Trading Integration** | **STRICTLY BLOCKED (Zero Access).** | Paper trading / simulated market feed only. | Read-only market quote connections; manual or strictly audited human-in-the-loop signal triggers. |
| **AI Agent Autonomy Scope** | Propose diffs, run local linters & tests (`jest`, `compile_applet`), audit visual/text quality. | Autonomous pull-request branch testing under 10-level quality gate. | **ZERO DIRECT MUTATION RIGHTS.** AI agents cannot deploy or alter production code without human CEO approval. |
| **n8n Automation Role** | Local workflow debugging, webhook format mock. | Automated E2E testing flows, synthetic alert dispatch. | Production alert aggregation, RSS news ingestion, scheduled sanity audits. |

---

## 3. Strict Boundary Rules (Invariants)

1. **RULE-ENV-001 (Zero Production Credentials in Dev):**
   Development configuration files (`.env.example`, `backend/.env.example`) must only contain harmless placeholder strings. Production API keys (`AIza...`, `sk-...`, `ghp_...`, live payment secrets) must NEVER be committed or present in development.

2. **RULE-ENV-002 (Fail-Closed Firebase Initialization):**
   In development without `google-services.json`, FCM push notification initialization must fail-closed safely without crashing the app, and must NEVER instantiate with placeholder/fake keys that pretend to succeed.

3. **RULE-ENV-003 (No Real Money / No Broker Execution in Part 2):**
   Part 2 strictly prohibits any live broker API connections, real wallet transfers, or live trading execution. All trading signals remain informational and human-reviewed.

4. **RULE-ENV-004 (Data Minimization & Sanctity):**
   User transaction records, password hashes, and personal email addresses from production must NEVER be downloaded or exported into development sandboxes.
