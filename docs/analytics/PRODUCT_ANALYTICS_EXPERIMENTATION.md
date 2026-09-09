# IBO Product Analytics, Experimentation & KPI Governance Architecture
**Document Version:** 1.0.0 — Phase 16  
**Scope:** Master Prompt Part 16: Privacy-Aware Event Registry, Canonical KPI Dictionary, Controlled Experimentation, Guardrail Fail-Safes & Insight Intelligence Generator  

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Product Intelligence Principles

Phase 16 establishes a privacy-first, controlled product experimentation and analytics layer. The architecture decouples telemetry ingestion from raw user identifiers and protects user privacy by enforcing strict opt-in consent controls. Experiments are dynamically bound by automatic error rate and quality guardrails ($R0-R4$).

Controlled Product Promotion Formula:
$$\text{PRODUCT STABILITY} = \text{METRICS} + \text{USER CONSENT} + \text{GUARDRAIL ERROR ENFORCEMENT}$$

---

## 2. Invariants & Guardrails

1. **Strict User Privacy Consent Guard:** Analytics events are automatically rejected if `userConsentGranted` is false (`accepted: false`).
2. **Automated Experiment Guardrail Rollback:** If a running experiment variant experiences an error rate higher than its maximum threshold (`guardrailMaxErrorRate`), the engine triggers an automatic rollback transition (`status: ROLLED_BACK`).
3. **Rigorous KPI Dictionary:** A centralized KPI dictionary governs conversion, retention, revenue, and quality metrics with target thresholds.
4. **Insights Intelligence Generator:** Product insights must be backed by evidence data, structured hypotheses, and acknowledged uncertainties instead of speculative claims.
