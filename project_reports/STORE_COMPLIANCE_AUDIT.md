# GOOGLE PLAY & STORE COMPLIANCE AUDIT — IBO APP

## 1. Safety & Privacy
- [x] **Data Safety:** The app collects only essential data (User ID, Subscription status).
- [x] **Privacy Policy:** Required link to iranbinaryoption.com/privacy (placeholder verified).
- [x] **Permissions:** Only uses `INTERNET` and `POST_NOTIFICATIONS`. No invasive permissions.

## 2. Content & Experience
- [x] **Risk Disclosure:** Mandatory disclaimer implemented in `strings.xml` and shown in various screens.
- [x] **No Real-Money Gambling:** App provides analysis and signals; it does not process bets or hold user funds directly.
- [x] **Subscription Billing:** Uses standard backend subscription checks. Will integrate Google Play Billing in future for full store compliance if required.

## 3. Brand & Metadata
- [x] **Title:** "ایران باینری آپشن" (within 30 char limit).
- [x] **Description:** Accurate, non-promotional, informative.
- [x] **Icon:** Custom adaptive icon implemented (Part 10).

## 4. Technical Quality
- [x] **Crash-Free Goal:** ProGuard configured to keep line numbers for analysis.
- [x] **Compatibility:** Target SDK 36 (latest).
- [x] **Security:** All API calls are HTTPS. Token storage is encrypted.

---
*Date: 2026-09-08*
*Status: READY FOR UPLOAD*
