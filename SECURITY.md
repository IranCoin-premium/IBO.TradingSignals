# Security Architecture and Threat Model Blueprint

This document establishes the security architecture and threat modeling for **Iran Binary Option Trading Signals (IranBinaryOption.TradingSignals)**. It outlines our core defense-in-depth principles, security controls, and a comprehensive 20-point threat model, alongside the implementation details of our **Autonomous Security Guardian**.

---

## 1. Security Architecture Principles

To transform the project from a security-aware application to a **security-governed platform**, we enforce a backend-authoritative pattern. The client application acts as a presentation and local caching layer, while all high-privilege operations, authentication, session validation, subscription management, and payment reconciliation are strictly centralized and verified on the secure backend.

Our security response flow operates on the following lifecycle:
```
DETECT ──> CLASSIFY ──> BLOCK ──> CONTAIN ──> REMEDIATE ──> VERIFY ──> AUDIT
```

---

## 2. Comprehensive 20-Point Threat Model

This threat model outlines the attack surfaces, vectors, impact, and remediation strategies for twenty distinct threat actors and scenarios.

| ID | Title / Threat Actor | Description | Severity | Impact | Remediation Strategy |
| :--- | :--- | :--- | :---: | :---: | :--- |
| **THR-01** | **Public Anonymous User** | Unauthenticated user attempts to access premium trading signals, admin endpoints, or payment checkouts. | **P1** | High | Enforce route-level JWT authentication on all endpoints except public authentication and health checks. |
| **THR-02** | **Authenticated Normal User** | Authenticated free tier user tries to craft API requests to bypass plan limitations or edit resources. | **P1** | High | Implement strict Role-Based Access Control (RBAC) and verify the user's active subscription status directly in the database for each premium request. |
| **THR-03** | **Compromised Authenticated User** | An attacker gains access to a normal user's account via password reuse or session hijacking. | **P2** | Medium | Utilize short-lived JWT access tokens (e.g., 24h), mandate strong password hashes (bcrypt), and implement logout endpoints that invalidate client storage. |
| **THR-04** | **Staff User Escalation** | A standard Staff user (who can create signals) attempts to perform super-admin operations (e.g., modifying users or system configs). | **P1** | High | Enforce granular, declarative RBAC middlewares (`requireRoles(['ADMIN', 'SUPER_ADMIN'])`) on high-privileged admin routes. |
| **THR-05** | **Administrator Privilege Abuse** | An internal admin account abuses privileges to alter payment histories, grant unauthorized subscriptions, or leak user lists. | **P1** | Critical | Record all administrative actions into an immutable, append-only `audit_logs` table. Enforce multi-sign or peer-review protocols for master ledger overrides. |
| **THR-06** | **Compromised Administrator Account** | An attacker steals an administrator's credentials and gains administrative access. | **P0** | Critical | Mandate Multi-Factor Authentication (MFA) for administrative accounts, enforce IP-address whitelisting for admin endpoints, and monitor for anomalous behavior. |
| **THR-07** | **Malicious API Client / Bot** | Automated bots hammer authentication, checkout, or signal APIs to scrape premium data or crash the server. | **P1** | High | Implement strict API rate limiting, robust CORS origin policies, and web application firewalls (WAF) to detect and block scraping behaviors. |
| **THR-08** | **Malicious/Compromised Automation** | Compromised internal scripting or cron jobs execute unauthorized database queries or trigger signals. | **P1** | High | Standardize service accounts with least-privilege tokens. Separate automation credentials from user accounts, and audit all automation access. |
| **THR-09** | **Compromised AI Agent** | A future autonomous AI agent develops unintended access paths or attempts to override system configurations. | **P1** | Critical | Implement an **Agent Permission boundary** limiting what tools and parameters the agent can invoke. Validate all agent outputs against schema constraints. |
| **THR-10** | **Compromised n8n Workflow** | An attacker hijacks an external n8n or Zapier workflow to trigger payment approvals or signal creations. | **P1** | Critical | Protect all webhook/automation endpoints with secure signature validation and cryptographic secrets. Restrict allowed IP ranges for webhook callers. |
| **THR-11** | **Supply-Chain Compromise** | Malicious packages are introduced during dependency updates in `package.json` or Gradle build catalogs. | **P1** | High | Lock down package versions using lockfiles (`package-lock.json`, Gradle checksums). Run automated dependency vulnerability scanners continuously. |
| **THR-12** | **Stolen JWT Access Token** | An attacker intercepts or extracts a user's JWT from client-side storage or HTTP transport. | **P2** | Medium | Transport all API payloads strictly over TLS (HTTPS). Use short JWT expiry durations, and implement token revoking using an active blacklist. |
| **THR-13** | **Leaked Cryptographic Secret** | Git commits expose `JWT_SECRET` or database passwords on public repositories. | **P0** | Critical | Use the Secrets Gradle Plugin for mobile builds, retrieve server secrets strictly from secure environment variables, and run `SecurityGuardian` pre-commit scans. |
| **THR-14** | **Malicious Dependency Injection** | An attacker registers a package with the same name on a public registry (typosquatting) to execute arbitrary code. | **P1** | High | Use scoped packages for proprietary modules, explicitly configure custom private registries, and audit npm/Gradle dependencies before building. |
| **THR-15** | **Database Credential Compromise** | An attacker steals PostgreSQL database credentials and directly manipulates tables. | **P0** | Critical | Run the database within a private VPC, disable public IP routing, enforce strict SSL connections, and limit access to the Express backend container's IP. |
| **THR-16** | **Replay of Webhook/Payment Events** | An attacker intercepts a legitimate payment webhook request and replays it multiple times to grant free credits. | **P1** | High | Enforce payment transaction idempotency. Store reference transaction IDs in the database and audit logs to prevent double-spending or duplicate activations. |
| **THR-17** | **Reverse Engineering of APK** | An attacker decompiles the Android APK to extract private API endpoints, encryption keys, or hardcoded credentials. | **P2** | High | Enforce ProGuard/R8 obfuscation. **NEVER** store production secrets on the mobile client; retrieve configurations dynamically and proxy requests through the backend. |
| **THR-18** | **Tampered Mobile Requests** | An attacker uses proxy tools (e.g., OWASP ZAP, Charles) to alter outgoing Android requests (e.g., bypassing client-side validation). | **P1** | High | Assume **all** incoming requests are hostile. Perform 100% of data validation, business logic checks, and role permissions on the backend. |
| **THR-19** | **Prompt Injection against AI Agents** | Attackers craft inputs to manipulate future LLM integration prompts, leading to unauthorized actions. | **P2** | Medium | Enforce strict input sanitization, separate instruction templates from user content dynamically, and wrap AI model outputs with verification filters. |
| **THR-20** | **Unauthorized Production Deployment** | An attacker pushes a malicious branch directly to production through compromised CI/CD pipelines. | **P1** | Critical | Enforce branch protection rules on Git, require peer reviews (PR approvals), and run automated security scan gates on all builds. |

---

## 3. The Autonomous Security Guardian

To continuously enforce our security posture, we have established the **Autonomous Security Guardian**. This modular security engine analyzes code configurations, environment setups, dependency catalogs, and mobile manifest files to automatically detect and remediate vulnerabilities.

### 3.1 Implemented Scan Modules
The scanner evaluates the workspace across four primary categories:
1. **SecretScanner (`SEC`)**: Scans code files using precise regex boundaries to identify hardcoded passwords, tokens, API keys, or plaintext cryptographic keys.
2. **AndroidSecurityScanner (`AND`)**: Analyzes the `/app/src/main/AndroidManifest.xml` configuration to verify application backup policies (`allowBackup`), cleartext traffic permissions, and insecurely exported components.
3. **BackendConfigScanner (`BCK`)**: Inspects `.env` files to ensure that permissive CORS overrides (`*`) are disabled and that default placeholder cryptographic secrets are changed.
4. **DependencyScanner (`DEP`)**: Reviews dependency manifests to flag outdated, unpinned, or vulnerable library versions.

### 3.2 Secure API Integration
The Security Guardian scanner is wired directly into the Express backend Monolith as a secure administrative endpoint:
- **Endpoint**: `/api/v1/admin/security/scan`
- **Method**: `POST`
- **Access Control**: Strictly restricted to users with `ADMIN` or `SUPER_ADMIN` roles.
- **Audit Logging**: Successful and failed execution requests are automatically appended to the centralized `audit_logs` database ledger.

### 3.3 Safe Auto-Remediation
The Guardian includes an automated self-healing mechanism (`runAutoRemediation`). When run, it safely generates strong, cryptographically secure 48-character random keys to replace default JWT signatures or database credentials, preventing misconfigured development setups from reaching production environments.
