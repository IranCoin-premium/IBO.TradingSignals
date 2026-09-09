# IBO Knowledge, RAG, Memory, Skills, MCP Governance & Research Architecture
**Document Version:** 1.0.0 — Phase 6  
**Scope:** Master Prompt Part 6 Architecture, Principles, Governance & Verification  

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Executive Summary & Core Principle

Phase 6 implements the Governed Knowledge and Intelligence Layer of the IBO Ecosystem.  
Raw information is systematically processed, enriched, sanitized, and stored across distinct conceptual layers:

$$\text{Source} \to \text{Ingest} \to \text{Scrub/Redact} \to \text{Classify} \to \text{Metadata} \to \text{Index} \to \text{Role-Filter} \to \text{Retrieve} \to \text{Audit}$$

### Non-Negotiable Invariants:
1. **No Uncontrolled Memory Dump:** Knowledge is never dumped blindly to every agent. Least-privilege filtering is enforced per agent role and data sensitivity.
2. **Provenance & Attribution:** Every knowledge item and research claim preserves its source authority (`OFFICIAL_PRIMARY`, `REGULATORY`, `INTERNAL_CONSTITUTION`), date, scope, and validation status.
3. **Fact vs. Inference Separation:** Verified facts are strictly separated from hypotheses and recommendations.
4. **Anti-Hallucination & Research Stopping:** If evidence is lacking, the system terminates with `INSUFFICIENT_EVIDENCE`.
5. **90th-Minute Human Action Boundary:** Automated research stops immediately at KYC, identity verification, or merchant banking registration boundaries, emitting a structured Persian handoff package.

---

## 2. Seven Knowledge Layers

| Layer | Name | Content & Scope | Mutability & Governance |
|:---:|:---|:---|:---|
| **Layer A** | Project Constitution | Non-negotiable safety boundaries, risk disclosures, human authority rules. | **Immutable**: Attempts to overwrite are hard-rejected. |
| **Layer B** | Project Documentation | Architecture specifications, ADRs, schemas, APIs, runbooks. | Versioned with changelog and owner. |
| **Layer C** | Operational Knowledge | Active releases, health status, TTL-sensitive items. | Freshness-tracked (`FRESH`, `AGING`, `STALE`, `EXPIRED`). |
| **Layer D** | Research Knowledge | Questions, claims, evidence, confidence scores, stopping criteria. | Evidence-backed; stopping reasons enforced. |
| **Layer E** | Market & Localization | Languages, locales, cultural terms, payment profiles. | Multi-market; Language $\ne$ Country; Arabic $\ne$ Monolith. |
| **Layer F** | Agent Scoped Memory | Scoped memory for agent roles. | Promotion to permanent knowledge requires approval. |
| **Layer G** | Session / Task Context | Ephemeral working state. | Automatically expires; never promoted blindly. |

---

## 3. Skills & MCP Governance Matrix

- **Skills Catalog:** Reusable capabilities (`SKILL-OFFICIAL-RESEARCH-V1`, etc.) defining inputs, outputs, permissions, and validation status.
- **MCP Governance:** Integrated servers (`mcp-core-governance`) with granular `allow`, `ask`, and `deny` rules. Tool invocations attempting `LIVE_TRADING_EXECUTE` or `PRODUCTION_SECRET_ACCESS` are hard-denied.
- **Conflict Engine:** Competing claims are captured as `ConflictRecord` (Claim A vs. Claim B with respective sources, dates, and authorities) and surfaced to the Admin Control Center rather than hidden.
