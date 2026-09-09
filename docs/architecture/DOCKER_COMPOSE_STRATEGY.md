# IBO Project — Docker & Compose Infrastructure Strategy
**Document Version:** 1.0.0 — Phase 2  
**Scope:** Master Prompt Part 2 — Work Package 2.3  

---

## 1. Executive Summary & Verification

In Phase 2, the Docker Compose configuration serves as the declarative standard for local developer environments and standalone containerized hosting (e.g. Render, Railway, Fly.io).

### Infrastructure Status in AI Studio Container Sandbox:
- **Docker Engine Binary:** `NOT AVAILABLE` (gVisor virtualization container sandbox does not support nested Docker daemon execution).
- **Docker Compose Binary:** `NOT AVAILABLE`.
- **Compose Specification Files:** `VERIFIED & VALIDATED` (`backend/docker-compose.yml`, `docker-compose.n8n.yml`).

---

## 2. Service Architecture in Docker Compose

```
+-----------------------------------------------------------------------------+
| ibo_network (Bridge Network)                                                |
|                                                                             |
|  +-----------------------+     +--------------------+     +---------------+  |
|  | backend               | --> | postgres           | <-- | n8n           |  |
|  | (Node.js/TS Monolith) |     | (PostgreSQL 15)    |     | (Orchestrator)|  |
|  | Port: 3000            |     | Port: 5432 (int)   |     | Port: 5678    |  |
|  | Health: /health       |     | Health: pg_isready |     | Storage: Vol  |  |
|  +-----------------------+     +--------------------+     +---------------+  |
+-----------------------------------------------------------------------------+
```

### Services Specification:

| Service | Image / Build Context | Port Mapping | Healthcheck Definition | Persistence Strategy |
|---|---|---|---|---|
| **backend** | Build `backend/Dockerfile` (Node 20 Alpine) | `3000:3000` | HTTP GET `/health` interval 15s | Stateless container; logs via stdout |
| **postgres** | `postgres:15-alpine` | `5432:5432` | `pg_isready -U ibo_admin -d ibo_signals_db` | Named volume `pgdata` at `/var/lib/postgresql/data` |
| **n8n** | `docker.n8n.io/n8nio/n8n:latest` | `5678:5678` | Native HTTP health check | Named volume `n8n_data` at `/home/node/.n8n` |

---

## 3. Minimal Infrastructure Invariants

1. **No Speculative Complex Services:** Part 2 strictly rejects introducing Redis, Kafka, RabbitMQ, or Kubernetes clusters. The current workload (relational financial ledgers, scheduled RSS news ingest, on-demand AI tasks) is fully handled by PostgreSQL and Node.js.
2. **Controlled Exposure:** Database ports in production/staging are internal to the private bridge network and never exposed to the public internet.
3. **Graceful Degradation:** When running outside Docker (such as in AI Studio or CI runners), all services support direct host execution (`npm test`, `npm start`, mock memory DB fallback).
