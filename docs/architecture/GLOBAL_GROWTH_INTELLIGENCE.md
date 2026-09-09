# IBO Global Growth Intelligence, International SEO & Market Readiness
**Document Version:** 1.0.0 — Phase 3  
**Scope:** Master Prompt Part 3 Implementation Architecture  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Philosophy

Phase 3 establishes the foundation for sustainable international discovery, multilingual support, locale intelligence, and payment readiness across the IBO ecosystem.

### Core Invariants:
1. **ONE PRODUCT CORE + MULTIPLE LANGUAGES + MULTIPLE LOCALES + MULTIPLE MARKET PROFILES:**
   The application is not cloned for each country.
2. **LANGUAGE IS NOT COUNTRY:**
   An English user is not automatically assumed to be in the United Kingdom (e.g. US, Canada, Australia). Arabic is not treated as a single uniform culture (neutral `ar-001` baseline vs. regional `ar-SA`, `ar-AE`, `ar-EG`).
3. **CONTEXT RESOLUTION PRIORITY:**
   Explicit user preference always overrides network-inferred or weak geographic signals.
4. **NO DECEPTIVE CLAIMS & ANTI-SPAM COMPLIANCE:**
   Zero mass generation of doorway pages; zero promises of "#1 ranking"; GEO is treated as clarity and accessibility enhancement, not generative search manipulation.
5. **90TH MINUTE HUMAN HANDOFF AT KYC/LEGAL BOUNDARY:**
   Automated agents handle all research and technical preparation, but immediately halt at the human identity, legal, banking, or KYC boundary, generating a complete Persian Human Action Package.

---

## 2. Multilingual & Locale Registry

The IBO ecosystem supports a shared product core with localized configurations:

| Language Code | Language Name | Script | Direction | Default Locale | Recognized Regional Locales | Date & Numeral Standards |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| `fa` | فارسی | Arab | **RTL** | `fa-IR` | `fa-IR`, `fa-AF` | Solar Hijri / Persian Numerals |
| `en` | English | Latn | **LTR** | `en-US` | `en-US`, `en-GB`, `en-CA`, `en-AU`, `en-NZ` | Gregorian / Latin Numerals |
| `ar` | العربية | Arab | **RTL** | `ar-001` (MSA) | `ar-001`, `ar-SA`, `ar-AE`, `ar-EG`, `ar-KW`, `ar-QA` | Gregorian / Arabic-Indic Numerals |
| `ru` | Русский | Cyrl | **LTR** | `ru-RU` | `ru-RU`, `ru-BY`, `ru-KZ` | Gregorian / Latin Numerals |
| `tr` | Türkçe | Latn | **LTR** | `tr-TR` | `tr-TR`, `tr-CY` | Gregorian / Latin Numerals |
| `es` | Español | Latn | **LTR** | `es-ES` | `es-ES`, `es-MX`, `es-AR`, `es-CO` | Gregorian / Latin Numerals |

---

## 3. Context Resolution Architecture

Resolution strictly prioritizes user consent and explicit agency:

```
[ Explicit User Language / Country Selection ] (Confidence: 1.0)
                       |
                       v (if absent)
[ Registered Account Preference Profile ] (Confidence: 0.95)
                       |
                       v (if absent)
[ Client Device / Browser Accept-Language Header ] (Confidence: 0.85)
                       |
                       v (if absent)
[ Consented Coarse Regional Signal ] (Confidence: 0.75)
                       |
                       v (if absent)
[ Network / IP Inference (e.g. CF-IPCountry) ] (Confidence: 0.60)
                       |
                       v (if absent)
[ Baseline Default: Persian (fa-IR) ] (Confidence: 0.50)
```

---

## 4. Market Profile Standard & Evidence Guards

A market is never labeled `VERIFIED_SUPPORTED` without verifiable evidence.

| Market Code | Market Name | Status | Primary Locale | Verified Settlement Methods | Regulatory & Operational Safeguards |
|:---:|:---:|:---:|:---:|:---:|:---|
| **IR** | Iran | `VERIFIED_SUPPORTED` | `fa-IR` | USDT (TRC20, ERC20, TON), NowPayments, Card | Informational signals only. Zero custodial broker executions. |
| **TR** | Turkey | `VERIFIED_SUPPORTED` | `tr-TR` | USDT (TRC20, TON), Crypto | High crypto and derivative adoption; Turkish risk line mandatory. |
| **AE** | UAE | `VERIFIED_SUPPORTED` | `ar-AE`, `en-US` | USDT (TRC20, TON), Crypto | Islamic swap-free option analysis; Dubai VARA guidelines. |
| **SA** | Saudi Arabia | `VERIFIED_SUPPORTED` | `ar-SA` | USDT (TRC20), Crypto | Strict consumer risk disclosure; Saudi CMA terminology. |
| **AU** | Australia | `RESEARCH_REQUIRED` | `en-AU` | Under Investigation | **Signals feed disabled.** ASIC compliance review pending. |

---

## 5. SEO Intelligence & Generative Search (GEO) Invariants

The SEO Intelligence System maintains strict separation between:
1. `VERIFIED_TECHNICAL_ISSUE` (e.g., broken canonical mapping, missing hreflang).
2. `SEO_HYPOTHESIS` (ideas to test via controlled experiments).
3. `CONTENT_OPPORTUNITY` (high-value informational gap with usefulness score $\ge 0.70$).
4. `GEO_FACTUAL_CLARITY_GAP` (improving structure and citations for generative engines).

### Enforced Quality Rules:
- **RULE-SEO-001 to 005:** Prohibits doorway pages, deceptive search terms, and ranking promises.
- **RULE-GEO-001 to 009:** Rejects mass AI content generation. Generative Search Optimization focuses on technical clarity, verified structured schemas, and trustworthy domain citations.

---

## 6. Payment Readiness Intelligence & 90th Minute Handoff

The Payment Readiness Intelligence Agent (PRIA) prepares technical plumbing, webhooks, and currency conversions, but halts immediately at the identity, legal, or banking boundary:

### The Human Action Package (Persian):
When a provider requires merchant verification or corporate KYC:
1. System produces an immutable `HumanActionPackage` in Persian.
2. Contains step-by-step instructions, official HTTPS portal link, and required documentation checklist.
3. Explicit security warnings: **Strictly forbids sharing passwords, 2FA codes, or wallet seed phrases with AI agents or code repositories.**
4. Notifications are rate-limited and deduplicated to prevent alert spam.

---

## 7. MCP Tool Permissions & Collaboration Pipeline

Role-based access ensures agents operate strictly within their designated boundaries:

```
[SEO Agent] (Search research, Site audit)
      |
      v (content_opportunity_detected)
[Market Intelligence Agent] (Official docs, Market demand)
      |
      v (market_research_completed)
[Locale Intelligence Agent] (Locale translation review)
      |
      v (localization_gap_analyzed)
[Payment Readiness Agent] (Payment provider doc audit)
      |
      v (human_action_required)
[90th Minute Human Handoff] -> Persian Package delivered to Project Owner
```
