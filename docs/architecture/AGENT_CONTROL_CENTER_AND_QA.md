# IBO Agent Control Center, Chief Agent Governance & Multi-Viewport QA
**Document Version:** 1.0.0 — Phase 4  
**Scope:** Master Prompt Part 4 Implementation Architecture  

---

> **Mandatory Legal Risk Disclosure (Immutable):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary

Phase 4 defines the comprehensive **Agent Control Center, Observability, Chief AI Coordinator (CAC), Web/PWA Experience Inspector (WPEI), Multi-Viewport QA Matrix, and Release Gates** for the IBO ecosystem.

### Core Architectural Pillars:
1. **Always-On Infrastructure with Event-Driven AI:**
   Infrastructure runs 24/7; agents execute tasks on-demand in response to events, avoiding costly infinite thinking loops.
2. **Admin-Safe Explanation Summary (No Hidden Reasoning Leaks):**
   Raw private chain-of-thought tokens are strictly isolated. Administrators are provided with structured, evidence-based reasoning summaries (Objective, Plan, Facts, Assumptions, Decision Rationale, Risks, Confidence Score).
3. **Strict Redaction & Structured Logging:**
   Universal sanitization for passwords, Bearer JWTs, private keys, wallet seed phrases, and API credentials. UTC timestamp storage with display timezone conversions (+03:30 for Tehran).
4. **Chief Agent Coordinator (CAC) Interactive Console & Persistent Deep Research:**
   High-level AI coordinator with interactive Persian chat capabilities, specialist agent consultation (VTQI, MIA, LIA, PRIA), and independent research initiatives that persist across CLI/browser sessions.
5. **Web/PWA Experience Inspector (WPEI) & Multi-Viewport Matrix:**
   Cross-platform evaluation spanning Extra Small Mobile (320px) to Ultrawide (2560px), portrait/landscape orientation, and square foldables.
6. **Dual QA Gate (Visual QA + Text QA + Responsive QA):**
   Independent visual and textual evaluations; a pass in Visual QA cannot bypass a failure in Text QA.

---

## 2. Observability & Log Retention Matrix

| Log Category | Retention Period | Redaction Policy | Storage Destination |
|:---:|:---:|:---:|:---|
| **Operational Logs** | 30 Days | Universal pattern scrubbing | Structured Console / Elastic Index |
| **Audit Logs** | 365 Days | Scoped Admin actions & Approvals | Append-only PostgreSQL Database |
| **Security Logs** | 730 Days | IP and access attempt hashes | Isolated Security Guardian Audit Table |
| **Error & Crash Logs** | 90 Days | Full stack trace with sanitized inputs | Error Tracking Service |
| **Visual Evidence & Screenshots**| 14 Days | Automated ephemeral cleanup | Object Storage |

---

## 3. Representative Viewport QA Profiles

| Profile ID | Device Classification | Resolution (W x H) | Orientation | Scale Factor | Core Invariant Verified |
|:---:|:---:|:---:|:---:|:---:|:---|
| `xs-mobile` | Extra Small Mobile (iPhone SE) | 320 x 568 | Portrait | 2.0x | Zero horizontal scroll, touch target $\ge 48$dp |
| `std-mobile`| Standard Mobile (Pixel / iPhone) | 390 x 844 | Portrait | 3.0x | Soft-UI cards fit, zero header clipping |
| `lg-mobile` | Large Mobile (Max / Plus) | 430 x 932 | Portrait | 3.0x | Typography hierarchy & banner aspect ratio |
| `tab-port`  | Tablet Portrait (iPad Air) | 768 x 1024 | Portrait | 2.0x | Multi-column grid reflow |
| `tab-land`  | Tablet Landscape | 1024 x 768 | Landscape | 2.0x | Navigation bar to rail adaptation |
| `laptop`    | Standard Laptop | 1366 x 768 | Landscape | 1.0x | Dashboard layout & table rendering |
| `desktop-fhd`| FHD Desktop Display | 1920 x 1080 | Landscape | 1.0x | Responsive center container max-width |
| `ultrawide` | 21:9 Ultrawide Monitor | 2560 x 1080 | Landscape | 1.0x | Aspect-ratio bounding, no wide distortion |
| `square-fold`| Foldable Inner Display | 884 x 1104 | Portrait | 2.5x | Dynamic layout re-balancing |

---

## 4. Brand Guardian Invariants (RULE-BRAND-001 to 005)

- **RULE-BRAND-001:** The official IBO logo cannot be replaced or altered by automated agents.
- **RULE-BRAND-002:** Brand aspect ratios must be strictly preserved; distortion or uneven scaling triggers immediate Visual QA failure.
- **RULE-BRAND-003:** Unauthorized third-party broker logos must never be substituted for core brand assets.
- **RULE-BRAND-004:** Primary and secondary brand color tokens must remain faithful to the approved Soft-UI design system.
- **RULE-BRAND-005:** Missing, clipped, or low-contrast logos are flagged as Critical Defects.

---

## 5. Dual QA Release Gate Flow

```
[ Code / Architecture Candidate ]
               |
               v
     [ Build & Unit Tests ]
               |
               v
    [ Web/PWA Inspector (WPEI) ]
         /           \
        v             v
 [ Visual QA (VQAA) ] [ Text QA (TQAA) ]
        \             /
         v           v
    [ Dual QA Release Gate ]
          |
    +-----+-----+
    |           |
 (PASS)      (FAIL)
    |           |
    v           v
 [ Release ] [ BLOCKED / Rollback / Remediation ]
```

- **Visual QA = PASS, Text QA = FAIL** $\rightarrow$ Release **BLOCKED**.
- **Visual QA = FAIL, Text QA = PASS** $\rightarrow$ Release **BLOCKED**.
- **Release Allowed** $\rightarrow$ Visual QA PASS + Text QA PASS + Responsive QA PASS.
