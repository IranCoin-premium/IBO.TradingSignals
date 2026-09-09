# IBO Project — Agent & Worker Permission Matrix
**Document Version:** 1.0.0 — Phase 1  
**Scope:** Master Prompt Part 1 — Work Package 9  

---

## 1. Permission Levels Defined

| Level | Identifier | Description & Bounds |
|---|---|---|
| **L0** | `DENY` | **Strictly Forbidden:** Actions that are hard-blocked by immutable policy (e.g. deleting production databases, hardcoding secrets, removing legal risk disclosures). |
| **L1** | `READ` | **Read-Only Access:** Reading documentation, source code files, logs, and database read-replicas. Zero mutation capability. |
| **L2** | `DEVELOPMENT_WRITE` | **Local Workspace Mutations:** Editing source files, creating test scripts, updating documentation inside active development branches. |
| **L3** | `EXECUTE_SAFE` | **Read-Only Execution:** Running unit tests, linters, static analyzers, and read-only diagnostics (`tsc --noEmit`, `jest`, `compile_applet`). |
| **L4** | `EXECUTE_RESTRICTED` | **Guarded Execution:** Running database migrations in staging, updating dependencies, modifying build configs. Requires pre-flight validation. |
| **L5** | `SENSITIVE` | **Elevated Authorization:** Production deployments, modifying payment gateway webhook routes, updating admin secrets, managing release keystores. Requires explicit Human Admin approval. |

---

## 2. Role-to-Permission Matrix

| Entity / Actor | READ | DEV_WRITE | EXEC_SAFE | EXEC_RESTRICTED | SENSITIVE | DENY Enforcement |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Anonymous / Public User** | Public only | ❌ | ❌ | ❌ | ❌ | All mutations denied |
| **Subscribed User** | Signals/Profile | ❌ | ❌ | ❌ | ❌ | Admin/Internal routes |
| **n8n Orchestrator** | Logs/Workflows | ❌ | Webhook calls | Webhook dispatch | ❌ | Direct DB ledger edits |
| **AI Coding Agent (Autonomous)**| Full repo | Target files | Tests & builds | Staging scripts | ❌ (Blocked) | Direct prod deploy, raw secrets |
| **Specialized Agent (UI/UX)** | Frontend/Assets| UI files only | UI tests only | ❌ | ❌ | Backend/Financial code |
| **System Administrator (Human)**| Full system | Full system | Full system | Full system | Full system | Removing mandatory risk lines |

---

## 3. Immutable Security Policies (Zero-Tolerance Guardrails)

1. **Mandatory Risk Disclosure:** Any action or pull request attempting to alter or remove the mandatory risk disclosure string will be automatically aborted and flagged:
   > «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»
2. **Never Commit Secrets:** No raw API keys, private keys, keystore passwords, or database credentials may ever be committed or logged.
3. **Fail-Closed Principle:** If a permission check cannot be verified or an authorization token is malformed, access is denied by default.
