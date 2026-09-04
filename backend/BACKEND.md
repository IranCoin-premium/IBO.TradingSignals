# IBO TRADING SIGNALS - BACKEND FOUNDATION & POSTGRESQL ARCHITECTURE

This folder contains the secure, production-grade Node.js + TypeScript + Express backend monolith foundation for **Iran Binary Option (IBO) Trading Signals**.

## 1. TARGET ARCHITECTURE MAP
The system moves security-sensitive logic from the untrusted client (Android APK) to a backend-authoritative architecture:

```
[Android Native Client] 
     │ (HTTPS REST)
     ▼
[Secure Express Backend Monolith] ─── (Winston JSON Structured Logs)
     │
     ├─► [PostgreSQL Database] (Identity, Subs, Payments, Signals, Audits)
     │
     └─► [Future Integrations (n8n Webhooks / AI Orchestrators)]
```

---

## 2. POSTGRESQL DATABASE SCHEMAS & ENTITIES

Database schema migrations are strictly managed sequentially in `/src/database/migrations` and applied via atomic transaction blocks.

### A. Initial Relational Table Entities
1. **`users`**: Master user profiles.
2. **`password_hashes`**: Isolated password digests (using Bcrypt).
3. **`roles`**: Standard authorization scopes (`USER`, `STAFF`, `ADMIN`, `SUPER_ADMIN`, `SERVICE_AGENT`).
4. **`user_roles`**: Join table matching users to their scopes.
5. **`subscription_plans`**: Available packages with pricing and durations.
6. **`subscriptions`**: Active user subscriptions.
7. **`entitlements`**: Feature keys (`premium_signals`, `premium_news`) synced to subscriptions.
8. **`payment_transactions`**: Secure payments tracking (`PENDING`, `SUCCESS`, `FAILED`).
9. **`payment_events`**: Detailed audit-ready metadata logs for payment activities.
10. **`signals`**: Relational signals records.
11. **`news_items`**: Platform news.
12. **`audit_logs`**: Complete tamper-evident trails capturing actor, action, and resource metadata.

---

## 3. API ENDPOINT MAP (`/api/v1`)

| Route | HTTP Method | Auth Scope | Description |
| :--- | :--- | :--- | :--- |
| `/health` | `GET` | Public | Real health check (checks process + PostgreSQL connectivity + migration status) |
| `/auth/register` | `POST` | Public | Registers a user and hashes passwords securely (Bcrypt) |
| `/auth/login` | `POST` | Public | Authenticates credentials and returns a secure JWT |
| `/auth/profile` | `GET` | User | Retrieves profiles |
| `/subscriptions/plans` | `GET` | Public | Lists active premium packages |
| `/subscriptions/current` | `GET` | User | Checks user active subscriptions and entitlements |
| `/payments/checkout` | `POST` | User | Starts secure checkout, querying prices strictly server-side |
| `/payments/verify` | `POST` | Admin/Service Agent | Verifies payment and activates subscriptions inside a database transaction |
| `/signals` | `GET` | User | Lists active trading signals |
| `/signals/create` | `POST` | Admin/Staff | Creates a signal |

---

## 4. LOCAL RUNTIME SETUP

### Requirements
* Node.js (v20+)
* PostgreSQL (v15+) or Docker

### Option A: Local Dev Execution (Manual)
1. Install dependencies:
   ```bash
   cd backend
   npm install
   ```
2. Configure `.env` from `.env.example`.
3. Synchronize database migrations:
   ```bash
   npm run migrate
   ```
4. Start dev server:
   ```bash
   npm run dev
   ```

### Option B: Zero-Config Orchestrated Execution (Docker)
Spins up both the backend and PostgreSQL:
```bash
cd backend
docker-compose up --build
```

---

## 5. REPRODUCIBLE TESTING RUNS
Unit and integration test suites run independently of a live database container using mock drivers:
```bash
cd backend
npm run test
```

---

## 6. TRANSITION & COEXISTENCE STRATEGY

### A. Room Integration Model
The Room database inside Android is redefined as a **Local Cache / Offline UX Layer** rather than an authority.
* Active UI streams observe Room.
* Android fetches updates from the backend REST endpoints, transforms DTOs into entities, and updates Room (SSOT pattern).
* Any security-sensitive checks (viewing premium signals) are authenticated against JWT entitlements.

### B. Firebase Coexistence Model
* Firebase Auth continues handling Google Social Sign-ins. When Google Authenticates on Android, the client submits the Google ID Token to `/auth/verify-social` to register/verify sessions backend-side.
* FCM remains active for push deliveries, triggered by server-side event handlers in future automated schedules.
