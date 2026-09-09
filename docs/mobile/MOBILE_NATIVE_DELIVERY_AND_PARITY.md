# IBO Mobile, Native App Delivery, PWA/Native Parity & Store Readiness
**Document Version:** 1.0.0 — Phase 19  
**Scope:** Master Prompt Part 19: Web / PWA / Android Native / iOS Native Parity Matrix, Deep Link Routing, Push Notification Token Governance, Multi-Store Compliance & Production Release Signing Attestation.

---

> **Mandatory Legal Risk Disclosure (Immutable Constitution):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»  
> *(These signals are not financial advice; binary options trading carries a high risk of losing capital.)*

---

## 1. Feature Parity Matrix

| Feature / Capability | Web | PWA | Android Native | iOS Native |
|---|---|---|---|---|
| Live Signals Streaming | ✅ | ✅ | ✅ | ✅ |
| Multilingual & RTL Layouts | ✅ | ✅ | ✅ | ✅ |
| In-App Billing (IAB) / Crypto | ✅ | ✅ | ✅ (Bazaar / Myket / Play) | ✅ |
| VIP Fast Push Notifications | ❌ | ✅ | ✅ (FCM) | ✅ (APNs) |
| Offline Cache & Sync | ❌ | ✅ | ✅ (RoomDB) | ✅ (CoreData / SQLite) |
| Deep Link Routing (`ibo://`) | ❌ | ✅ | ✅ | ✅ |
| Biometric Auth (Fingerprint/Face) | ❌ | ❌ | ✅ | ✅ |

---

## 2. Deep Link Scheme Routing
- `ibo://signals?id=:signalId` -> Routes to signal detail screen (`/signals/:signalId`).
- `ibo://subscription` -> Routes to plans and subscription management (`/subscription`).
- `bazaar://details?id=...` -> Direct link to Cafe Bazaar store page.
- `myket://details?id=...` -> Direct link to Myket store page.

---

## 3. Play Protect & App Store Submission Invariants
1. **Target SDK:** Android 16 (API 36).
2. **Keystore Consistency:** Immutable `release.keystore` key for update continuity.
3. **Debug Flag:** Mandatory `debuggable = false` in release builds.
4. **Mandatory Risk Warning:** Binary options risk warning disclosure required across all store descriptions and interfaces.
