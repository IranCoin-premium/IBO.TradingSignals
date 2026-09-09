# IBO Project — Visual & Text Quality Intelligence (VTQI) System
**Document Version:** 1.0.0 — Phase 2  
**Scope:** Master Prompt Part 2 — Work Packages 2.8 & 2.9  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Purpose

The **Visual & Text Quality Intelligence (VTQI)** system is a specialized agentic system whose responsibility is **strictly limited to visual and textual quality control**.

### Strict Negative Boundaries (What VTQI MUST NEVER DO):
- ❌ **NO Trading Decisions:** Does not evaluate signals, calculate win rates, or recommend financial entries.
- ❌ **NO Financial / Pricing Logic:** Does not modify subscription prices, gateway routes, or referral math.
- ❌ **NO Backend / Infrastructure Mutations:** Does not touch backend routes, PostgreSQL schemas, or Docker configs.
- ❌ **NO Production Deployments:** Cannot publish APKs or deploy server bundles.
- ❌ **NO Arbitrary Code Modification:** Modifications are strictly localized to UI presentation files (Compose styling, strings, layout resources).

---

## 2. VTQI Architecture & Sub-Agent Specialization

```
                          [ Visual / Text Event ]
                                     |
                                     v
                       [ VTQI System Coordinator ]
                                     |
    +--------------------------------+--------------------------------+
    |                                |                                |
    v                                v                                v
[ Visual Inspector ]       [ Typography Inspector ]        [ RTL / LTR Inspector ]
(Layout, alignment,        (Fonts, readability,            (Persian RTL, mixed
 spacing, responsiveness)   line-height, sizing)            numerals, punctuation)
    |                                |                                |
    +--------------------------------+--------------------------------+
    |                                                                 |
    v                                                                 v
[ Text Presentation Inspector ]                           [ Brand Consistency Guard ]
(Spelling, labels, placeholders,                          (Soft-UI palette, logo protection,
 accidental developer strings)                            approved typography pairings)
                                     |
                                     v
                       [ VTQI Findings Engine ]
                                     |
                                     v
                      [ Rule & Boundary Validation ]
                                     |
                                     v
                        [ Structured Worker Result ]
```

---

## 3. Five Specialized Sub-Agent Roles

| Sub-Agent | Scope & Focus Areas | Forbidden Actions |
|---|---|---|
| **1. Visual Inspector** | Layout bounds, container clipping, responsive grid scaling, button touch-targets (min 48dp), padding alignment. | Altering backend data structures or view models. |
| **2. Typography Inspector** | Font pairing consistency, line height ratios, contrast readability on light backgrounds. | Changing text meaning or financial disclaimers. |
| **3. RTL / LTR Inspector** | Correct Persian RTL layout direction, bidirectional text (Persian text with English tickers like "EUR/USD"), Persian vs. Latin numeral placement. | Inverting navigation logic or route destinations. |
| **4. Text Presentation Inspector** | Persian spelling accuracy, grammar, punctuation (نشانه‌های نگارشی), eliminating placeholder text ("Lorem ipsum", "TODO", "test"). | Modifying legal disclaimers or user agreement clauses. |
| **5. Brand Consistency Guard** | Preserving the Soft-UI design tokens, pastel accents, brand colors, protecting original SVG/PNG logos. | Redesigning brand assets or inventing unapproved color palettes. |

---

## 4. Immutable VTQI Operating Rules

- **RULE-VTQI-001 (Logo Protection):** Never modify, overwrite, or delete the official IBO logo assets (`app/src/main/res/drawable/` brand marks).
- **RULE-VTQI-002 (Brand Color Policy):** Never introduce unapproved dark themes or arbitrary hex codes. Adhere strictly to the approved Soft-UI light palette (`SoftUiBg`, `SoftUiCard`, `SoftCardCyanAccent`, etc.).
- **RULE-VTQI-003 (Minimal Localized Fixes):** Never redesign an entire screen or rewrite unrelated composables to resolve a localized spacing or typography defect.
- **RULE-VTQI-004 (Evidence-Based Reporting):** Every high or critical finding must provide verifiable evidence (screenshot coordinate, component tag, line number, or script error).
- **RULE-VTQI-005 (Mandatory Risk Line Invariant):** The mandatory trading risk disclosure must remain visible, legible, and unclipped on all landing, signal, and subscription screens.
- **RULE-VTQI-006 (Zero Financial Authority):** Any task submitted to VTQI attempting to modify signal formulas, payouts, or subscription prices must be rejected immediately with an out-of-scope error.
- **RULE-VTQI-007 (Verification Before Approval):** A visual or text defect is never marked resolved until `verify-ui-uniformity.sh` and `compile_applet` re-run and pass cleanly.
