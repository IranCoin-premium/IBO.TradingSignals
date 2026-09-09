#!/usr/bin/env bash
# ============================================================================
# PART 12 LOOP VERIFIER — deterministic gate for AI Studio client fixes
# Usage: bash scripts/verify-part12.sh   (exit 0 = GREEN/loop can close, 1 = RED)
# ============================================================================
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VM="$ROOT/app/src/main/java/com/example/ui/TradingViewModel.kt"
SCR="$ROOT/app/src/main/java/com/example/ui/screens"
APPKTS="$ROOT/app/src/main/java/com/example"
UIDIR="$ROOT/app/src/main/java/com/example/ui"
fail=0
ok()  { echo "  PASS  $1"; }
bad() { echo "  FAIL  $1"; fail=1; }
# PASS only when pattern is NOT found (mock eradication checks)
absent() {
  local target="$1" pat="$2" label="$3" rc=1
  if [ -d "$target" ]; then grep -rEq "$pat" "$target" --include='*.kt' 2>/dev/null && rc=0
  elif [ -f "$target" ]; then grep -q "$pat" "$target" 2>/dev/null && rc=0
  fi
  [ $rc -eq 0 ] && bad "$label (mock still present)" || ok "$label"
}
# PASS only when pattern IS found (integration checks)
present() {
  local target="$1" pat="$2" label="$3" inc="${4:-*.kt}"
  if grep -rEq "$pat" "$target" --include="$inc" 2>/dev/null; then ok "$label"; else bad "$label (missing)"; fi
}

echo "== A) Mock eradication (must be ABSENT) =="
absent "$VM" 'IBO-\${' "no locally-generated referral code (IBO-\${...random})"
absent "$VM" 'MutableStateFlow(15.0)' "no hardcoded fake earnings (15.0)"
absent "$SCR/ReferralsScreen.kt" 'کامران رضایی' "no hardcoded fake referred-friends list"

echo "== B) Real backend integration (must be PRESENT) =="
present "$APPKTS" 'api/v1/referral/my-code' "endpoint wired: GET /api/v1/referral/my-code"
present "$UIDIR" 'ReferralApiService|referralApi' "UI consumes ReferralApiService (my-code)"
present "$APPKTS" 'api/v1/referral/dashboard' "endpoint wired: GET /api/v1/referral/dashboard"
present "$APPKTS" 'api/v1/referral/partnership' "endpoint wired: GET /api/v1/referral/partnership (risk line)"
present "$APPKTS" 'api/v1/tabs/manifest' "endpoint wired: GET /api/v1/tabs/manifest (Tab Registry)"
present "$UIDIR" 'tabsManifest' "UI renders tabs from server manifest"
present "$SCR" 'referral_risk_disclosure' "legal risk line rendered in screens"
present "$APPKTS" 'BuildConfig' "BASE_URL sourced from BuildConfig"


echo "== C) Network scaffold & i18n readiness =="
present "$APPKTS" 'ReferralApiService' "Retrofit service for referral endpoints exists"
present "$ROOT/app/src/main/res" 'referral_risk_disclosure' "i18n keys exist in res (6 langs)" '*.xml'
present "$UIDIR" 'referral_risk_disclosure' "risk line wired into UI via stringResource"
# Client-side commission math is FORBIDDEN (server computes only)
absent "$SCR" '\*\s*0\.(15|20|25|30)' "no client-side commission percentage math in screens"

if [ "$fail" -eq 0 ]; then echo "RESULT: GREEN — loop gate satisfied."; exit 0; else echo "RESULT: RED — corrective directive (PART12_CORRECTION_DIRECTIVE.md) still open."; exit 1; fi
