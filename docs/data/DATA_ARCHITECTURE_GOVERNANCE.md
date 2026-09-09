# IBO Data Architecture, Data Governance, Quality, Backup, Restore & Disaster Recovery
**Document Version:** 1.0.0 — Phase 15  
**Scope:** Master Prompt Part 15: Data Inventory, Source-of-Truth Ownership, PII/Financial Isolation, Backup Restoration Verification & Disaster Recovery Targets  

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Data Governance Formula

Phase 15 establishes a governed data foundation across all storage domains (Task Graph, Audit Logs, User KYC/Billing, Knowledge Base). Access controls strictly isolate personal/financial data from AI context windows while proving recoverability via actual restoration tests.

Data Governance & Recovery Formula:
$$\text{DATA COHERENCE} = \text{SOURCE OF TRUTH} + \text{ISOLATION (PII/Financial)} + \text{RECOVERY (Actual Restore Evidence)} + \text{RPO/RTO TARGETS}$$

---

## 2. Invariants & Data Architecture Guardrails

1. **Explicit Source of Truth:** Every data domain defines its primary authoritative store, derived views, retention period, and backup frequency.
2. **Strict PII/Financial Isolation:** Restricted user data (KYC, billing, passwords) is strictly blocked from AI context or LLM prompt ingestion (`piiFinancialExposedToAi: false`).
3. **Restoration Evidence Over Job Logs:** Backup reliability is validated exclusively by executing actual restoration tests and verifying checksum hashes (`dataIntegrityCheck: INTACT`).
4. **RPO/RTO Service Level Targets:**
   - **Task State & Queue:** RPO 5 mins, RTO 15 mins
   - **Audit Logs:** RPO 0 mins (Realtime), RTO 10 mins
   - **User KYC & Billing:** RPO 1 min (Realtime), RTO 30 mins
   - **Knowledge Base:** RPO 60 mins, RTO 60 mins
