# IBO Trading Signals - n8n Automation & Webhook Governance (Part 5)

This document specifies the integration architecture between the **IBO Backend API** and the **n8n Automation and Orchestration Layer**.

---

## 1. Architectural Principles (Backend vs. n8n)

- **Backend (Node.js/Express + PostgreSQL)**: Sole system of record and authority for user identity, JWT authentication, RBAC authorization, pricing catalogs, purchase intents, transactional payment verification, and active subscription entitlements.
- **n8n (Orchestration Engine)**: Acts as an event-driven automation layer, handling notifications, payment reconciliation reporting, webhook delivery retries, automated support ticket escalations, and AI agent job dispatches.
- **Untrusted Clients**: The Android application communicates exclusively via authenticated HTTPS endpoints with the backend and never initiates direct mutations to n8n or PostgreSQL.

---

## 2. Secure Backend ↔ n8n Communication Contract

Every event dispatched from the backend to n8n or received from an external webhook adheres to strict schema validation and signature verification:

```json
{
  "event_id": "evt_uuid_998877",
  "event_type": "PAYMENT_VERIFIED",
  "schema_version": 1,
  "occurred_at": "2026-09-04T12:00:00Z",
  "source": "backend-authoritative",
  "entity_type": "payment_transaction",
  "entity_id": "tx_uuid_12345",
  "correlation_id": "req_xyz789"
}
```

- **Authentication**: All webhook callbacks originating from external payment providers must carry a valid cryptographic signature (`X-Webhook-Signature`).
- **Idempotency**: Duplicate events are filtered via unique event identifiers and transaction hashes in PostgreSQL.

---

## 3. Workflow Catalog

| ID | Category | Purpose | Trigger | Idempotency Strategy |
|----|----------|---------|---------|----------------------|
| WFL-01 | Notifications | Send subscription expiration reminder | Scheduled Cron (Daily) | User ID + Date Unique constraint |
| WFL-02 | Payments | Reconcile pending Tether transactions | Webhook / Cron | Transaction Hash Unique Constraint |
| WFL-03 | Subscriptions| Auto-revoke expired entitlements | Hourly Cron | Entitlement ID + State Check |
| WFL-04 | AI Dispatch | Dispatch asynchronous analysis tasks | API Event | Job ID Unique Constraint |

---

## 4. Deployment Model

n8n is deployed alongside the backend service via Docker Compose (`docker-compose.yml`), utilizing an isolated internal Docker bridge network (`ibo_network`) with persistent volume storage (`n8n_data`).

For standalone or VPS-less cloud deployment, refer to `docker-compose.n8n.yml` and `/N8N_AND_AUTOMATION_STRATEGY.md` for Render.com and Railway.app deployment recipes.

---

## 5. Summary on VPS-less Automation

> **"n8n دائمی نیاز به یک سرویس Always-On دارد (حتی رایگان‌ترین PaaS)؛ GitHub Actions/Codespace جایگزین کامل آن نیست اما برای بخش قابل‌توجهی از اتوماسیون‌های زمان‌بندی‌شده (نه Webhook زنده) می‌تواند رایگان و بدون n8n جایگزین شود."**

