# IBO Billing, Subscriptions, Entitlements & Financial Controls Architecture
**Document Version:** 1.0.0 — Phase 18  
**Scope:** Master Prompt Part 18: Provider-Independent Billing Core, Subscription Durations (Weekly, Monthly, 3M, 6M, Yearly), State Separation, Cryptographic Webhook Security, Idempotency & Financial Reconciliation.

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Billing Principles

Phase 18 implements a provider-independent billing, subscription, and entitlement engine. The core decouples:
- **Payment Transaction State** (`PENDING`, `SETTLED`, `FAILED`, `REFUNDED`, `DISPUTED`, `CHARGEBACK`)
- **Subscription Lifecycle State** (`TRIAL`, `ACTIVE`, `GRACE_PERIOD`, `EXPIRED`, `CANCELLED`)
- **Entitlement Access Grants** (`SIGNALS_BINARY_CORE`, `SIGNALS_VIP_FAST`, `TECHNICAL_INDICATORS`, `AI_INSIGHTS`)

$$\text{FINANCIAL INTEGRITY} = \text{IDEMPOTENCY} + \text{HMAC VERIFICATION} + \text{RECONCILIATION AUDIT}$$

---

## 2. Core Invariants & Governance Controls

1. **Equal Core Capabilities:** All subscription durations (Weekly, Monthly, 3-Month, 6-Month, Yearly) grant equal access to core features (`SIGNALS_BINARY_CORE`, `SIGNALS_VIP_FAST`, `TECHNICAL_INDICATORS`, `AI_INSIGHTS`).
2. **Strict Mutation Idempotency:** Payments initiated with an already-processed `idempotencyKey` return the existing transaction record without creating duplicates or double charges.
3. **Cryptographic Webhook Verification:** Incoming payment status updates are validated using HMAC SHA-256 signatures against registered provider webhook secrets. Invalid signatures fail closed immediately.
4. **Dispute & Chargeback Revocation:** If a payment transitions to `DISPUTED` or `CHARGEBACK`, granted entitlements are automatically revoked.
5. **Periodic Financial Reconciliation:** The reconciliation engine continuously audits settled sums and transaction volume to guarantee zero-discrepancy financial balance.
6. **Human-Only Action Boundary:** Banking, merchant registration, KYC, and official credential handling are strictly human-owned actions.
