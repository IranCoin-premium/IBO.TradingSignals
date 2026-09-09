================================================================
IBO — MASTER INSTRUCTION PACK v2 | 11 PARTS + PUBLISH MANDATE
Target: Gemini agent inside the AI Studio app («ایران باینری آپشن»)
Owner: ali.khani0916@gmail.com | Repo: IranCoin-premium/IBO.TradingSignals
================================================================

[CONSTANT CONTEXT — READ FIRST, APPLY TO EVERY PART]

PROJECT = IBO Binary Option Trading Signals platform. Canonical repo
documents to honor: ARCHITECTURE.md, DATABASE_SCHEMA.md,
PURCHASE_AND_SIGNAL_FLOW.md, PAYMENT_GATEWAYS.md,
PAYMENT_GATEWAYS_MAPPING.md, N8N_AND_AUTOMATION_STRATEGY.md,
SECURITY.md, SEO_CHECKLIST.md, MULTILINGUAL_AND_LOCALIZATION.md,
AUTONOMOUS_AGENTS_SPEC.md, ADMIN_SECRETS_MANAGEMENT.md,
brand-assets.json (protected brand paths — never regenerate).

PERMANENT RISK LINE (must render on every page/dialog/schema):
«این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

STANDING RULES (zero tolerance, apply to every PART):
R1. Inspect current repo structure BEFORE changing anything; never
    delete or rewrite existing files/paths without a stated reason.
R2. Work ONLY on the PART you are given; never guess future parts.
R3. Layer separation: frontend holds NO secrets; backend is the SINGLE
    SOURCE OF TRUTH for payments/subscriptions/commissions; n8n
    orchestrates only non-sensitive tasks; no raw keys in code/DB/git.
R4. Permanent risk line present on every surface, in all 6 languages.
R5. No misleading claims, no guaranteed profit, no fake success rates.
R6. Every automated agent action MUST pass the escalating 10-level
    test chain and be recorded in audit_logs; any level failure =
    instant rollback and restart from level 1.
R7. For unknown external services use official docs + TODO markers;
    never fabricate integrations.
R8. Finish the PART, report format below, then STOP and await the
    next PART.

DEFINITION OF DONE (every PART): compile green + all tests pass +
risk line verified + audit_logs written + no secrets committed.

REPORT CONTRACT (end of every PART, exactly this format):
STATUS: PART XX — PASSED 10/10 LEVELS | or | FAILED AT LEVEL X
EVIDENCE: (build cmd + exit code, test counts, file list touched)
CLAIMS: numbered list of verifiable statements made by you
STOP-AND-WAIT: confirm you have halted for next PART.

10-LEVEL ESCALATING TEST CHAIN (L1→L10: Easy → Hard → Extra Hard →
Extra-Extra Hard → Extra-Extra-EXTRA Hard):
L1 Happy-path manual walk | L2 Input validation/fuzz | L3 AuthZ/RBAC
negative tests | L4 Persistence/migration idempotency | L5 Concurrency
& race conditions | L6 Failure injection (network/db/gateway down,
rollback integrity) | L7 Security (secrets hygiene, injection,
IDOR/broken access) | L8 Perf & scale (load, N+1, caching) |
L9 I18n/RTL + 6-language risk-line presence + layout budgets |
L10 Chaos end-to-end: full user journey under injected failures +
regression of all prior parts + audit completeness check.

================================================================
PART 00 — MANDATORY: EXIT DEMO/TRIAL MODE → PRODUCTION PUBLISH MODE
================================================================
OBJECTIVE (do this FIRST, before any feature work):
Convert the project from experimental/demo state to
production-publish state. Concretely:
1) Introduce explicit environment profile: APP_MODE=demo|production
   (backend config), with production defaults: strict logging of
   financial events, no mock/stub payment adapters, no seed/demo
   accounts, no verbose debug endpoints.
2) Add /docs/RELEASE_CHECKLIST.md (production go-live checklist:
   migrations applied, secrets via ADMIN_SECRETS_MANAGEMENT.md flow,
   webhook endpoints live, monitoring, rollback plan) and wire the
   app so publish-readiness is a checkable state, not a slogan.
3) Replace any demo/placeholder content paths with production
   settings; keep brand assets from brand-assets.json untouched.
4) Legal gate: risk line in all 6 languages verified on every route
   before publish flag can be enabled.
ACCEPTANCE: APP_MODE switch works; demo-only behavior provably
disabled in production; release checklist exists and is referenced
by CI or admin panel. Report evidence per 10-level chain.

================================================================
PART 01 — BACKEND CORE, DATABASE & INPUT VALIDATION (hardened)
================================================================
Rebuild/verify to production grade, per ARCHITECTURE.md and
DATABASE_SCHEMA.md: Express+TS layered backend; PostgreSQL with
versioned migrations (next free number, idempotent, transactional);
JWT auth + Argon2/bcrypt hashing; RBAC Admin/Analyst/User; strict
input validation (zod) on every route; request-id + structured
logging; health endpoint. TESTS: extend the existing Jest+Supertest
suite (currently 44 passing) — every new route gets negative tests.
ACCEPTANCE: suite green, zero `any` leaks in payment paths, risk
line intact.

================================================================
PART 02 — SUBSCRIPTIONS & PAYMENTS: CRYPTO (NowPayments) + RIALS
================================================================
Implement exactly per PURCHASE_AND_SIGNAL_FLOW.md,
PAYMENT_GATEWAYS.md, PAYMENT_GATEWAYS_MAPPING.md: separate webhook
endpoints per gateway with HMAC signature verification and
idempotency keys; server-side price/plan computation only (client
claims NEVER trusted — anti-tamper rule); manual receipt review flow
for rial payments kept intact; atomic subscription activation only
after gateway-confirmed state; refund/cancel logic with full audit
trail. ACCEPTANCE: signature-forgery tests fail-closed; double-webhook
replay test passes; price-tamper test (client sends manipulated
price) fails closed.

================================================================
PART 03 — SIGNAL ENGINE + AI CONFIRMATION + DELIVERY
================================================================
Analyst submits signal (pair, direction, entry, expiry,
confidence_level) → AI review (gemini-3.6-flash via server-side API,
key from secrets flow) → ai_review_status approved/rejected/pending
→ publish only to ACTIVE subscribers with published_at/expires_at
(DB fields already spec'd). Delivery tracking in signal_deliveries.
Risk line on signal cards in all languages. ACCEPTANCE: RBAC tests
(user cannot see unpublished), AI-confirmation gate cannot be
bypassed by any role, audit entries written.

================================================================
PART 04 — FINANCIAL SECURITY: ANTI-TAMPER, CANCEL/REFUND INTEGRITY
================================================================
Harden: server-side only calculations; signed webhook replay
protection (timestamp+nonce); DB constraints against negative/float
amounts; per-gateway unique constraints to block duplicate
activation; cancel/refund state machine with no-loss-of-audit;
suspicious activity counters. ACCEPTANCE: adversarial test list you
must actually run and show results for (forged signature, replay,
price tamper, race on activation, refund-after-expiry).

================================================================
PART 05 — EVENT ORCHESTRATION, NON-SENSITIVE n8n AUTOMATION, AUDIT LOGGING
================================================================
Per N8N_AND_AUTOMATION_STRATEGY.md: GitHub Actions for scheduled,
non-sensitive jobs only (ui-ux check every 6h; media generation
daily; RSS news hourly — existing workflows under .github/workflows/
staying); n8n (docker-compose.n8n.yml) only for non-sensitive event
orchestration; ALL financial decisions stay in backend; append-only
audit_logs with actor, action, target, meta, request-id. ACCEPTANCE:
audit coverage test (every sensitive route writes audit row), n8n
cannot reach financial endpoints (network/auth proof).

================================================================
PART 06 — SUPERVISOR AGENT + 10-LEVEL TEST RUNNER (systemize)
================================================================
Generalize the 10-level chain (defined above) into a reusable
backend runner used by every agent (see AUTONOMOUS_AGENTS_SPEC.md):
UIUXAgent, TranslatorAgent, ImageVideoAgent, SupportAgent. Kill-
switch per agent (admin panel), per-run audit rows with level-by-
level results, auto-rollback on any level failure. ACCEPTANCE:
forced-failure drill — you must demonstrate a rollback actually
triggering at a chosen level.

================================================================
PART 07 — DEEP I18N (6 LANGUAGES), TRANSLATOR AGENT, GEO-IP, 24/7 SUPPORT
================================================================
Per MULTILINGUAL_AND_LOCALIZATION.md: 6 languages per spec,
RTL/LTR correctness, TranslatorAgent pre-publish quality gate (length
budgets, {var} preservation, no TODO/[UNTRANSLATED] leaks, risk-line
presence in target language), Geo-IP routing, SupportAgent 24/7 with
escalation-to-human path. ACCEPTANCE: L9 suite passes for all 6
languages on every new page including referral (when built).

================================================================
PART 08 — MEDIA/ASSET GENERATION (GOOGLE FLOW MODELS: NANO BANANA / VEO)
================================================================
Asset generation pipeline for brand visuals and teasers per
brand-assets.json (never regenerate protected paths):
ImageVideoAgent produces via Google generative models (image:
nano-banana; video: Veo 3.1 — the engine behind Google Flow) through
server-side API with keys from the secrets flow; watermark/brand
colors from brand-assets.json; outputs stored as new files only
(new paths, no overwrites of protected assets). ACCEPTANCE: pipeline
runs in demo mode offline (mock provider) and production mode via
real API; protected-path immutability test passes.

================================================================
PART 09 — ADMIN API SECRETS PANEL + CLOUD SECURITY BRIDGE (per ADMIN_SECRETS_MANAGEMENT.md)
================================================================
Admin panel section for managing API secrets: write-only UI (never
echo raw values), server-side encryption at rest, audit every
read/write/rotate, masked previews only; GitHub/Render bridge: sync
only secret NAMES/metadata, values injected at deploy time, never
committed. ACCEPTANCE: rotate-and-verify drill; dump tests prove raw
secrets unrecoverable from UI/DB/exports; audit trail complete.

================================================================
PART 10 — SEO/GEO MULTILINGUAL + DYNAMIC SITEMAP + LEGAL/SECURITY CHECKLIST
================================================================
Per SEO_CHECKLIST.md: dynamic sitemap (routes × 6 languages), JSON-LD
(FinancialService/NewsArticle), hreflang alternates, canonical rules,
OG/Twitter cards with IBO branding, robots rules; legal: risk line,
no-profit-claims policy, privacy/data-retention notes. ACCEPTANCE:
sitemap builds green, structured data validates, L9 i18n pass.

================================================================
PART 11 — AGENT AUTONOMY: SELF-TUNING, CIRCUIT BREAKER + NEW:
REFERRAL & SALES-PARTNERSHIP SYSTEM (BUILD FROM ZERO)
================================================================
A) Finish agent autonomy per AUTONOMOUS_AGENTS_SPEC.md:
   agent_instruction_versions (draft→staging→production, admin manual
   rollback), agent_autonomy_settings, agent_autonomy_logs,
   AutonomousAgentEngine, circuit breaker (N consecutive failures →
   halt + admin review), immutable guardrails (agents NEVER write
   financial tables, NEVER touch risk line).
B) NEW MODULE — REFERRAL & SALES PARTNERSHIP (does not exist yet in
   repo; build fully, server-authoritative):
   - referral_codes (unique per user/partner), referral_clicks,
     referral_conversions (attributed paid subscription events),
     partner_profiles (payout info, tier), commission_rules
     (tiered % per plan, server-computed ONLY), commission_ledger
     (append-only), payouts (state machine: pending→approved→paid,
     admin-approved only).
   - Pages: public «همکاری در فروش» landing (localized, risk line,
     no income guarantees — R5), partner dashboard (clicks,
     conversions, commission balance, payout history), admin review
     screen for payouts.
   - Attribution: ref code → click → signup → first paid conversion;
     cookie+code binding server-side; anti-fraud (self-referral,
     duplicate-account, click-spam detection hooks).
   - Anti-tamper: commissions computed and stored ONLY in backend;
     client may display read-only values from API.
ACCEPTANCE (both A and B): full 10-level chain incl. L5 race tests
on conversion attribution, L7 anti-fraud/negative tests, L9 i18n on
all new pages; migration ids continue existing numbering; zero
regression on prior parts.

================================================================
VERIFICATION LOOP (how your claims will be checked — expect it)
================================================================
After you commit via the app's GitHub tab, every CLAIM in your
REPORT CONTRACT will be independently re-verified in the repo
(builds re-run, tests re-run, file diffs inspected, audit schema
checked). Any claim that does not reproduce = you will receive a
CORRECTION DIRECTIVE for the same PART, must fix and re-report, and
the loop repeats until all levels genuinely pass. False PASS
reports are treated as FAILED reports. Plan accordingly: report
only what you can evidence.

END OF MASTER INSTRUCTION PACK — await PART assignments one by one.
================================================================