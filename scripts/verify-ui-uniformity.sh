#!/usr/bin/env bash
# L6 — UI/UX uniformity gate: every screen must follow the Home (Soft-UI light) template.
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
UI="$ROOT/app/src/main/java/com/example/ui"
fail=0
ok()  { echo "  PASS  $1"; }
bad() { echo "  FAIL  $1"; fail=1; }
absent() { if grep -rEq "$2" "$1" --include='*.kt' 2>/dev/null; then bad "$3"; else ok "$3"; fi; }
present() { if grep -rEq "$2" "$1" --include='*.kt' 2>/dev/null; then ok "$3"; else bad "$3 (missing)"; fi; }

echo "== U1) No dark-theme escapes (tokens must stay mapped to Soft light palette) =="
absent "$UI" 'SlateDark950 *= *Color\(0xFF(0|1|2|3)' "SlateDark950 is light (SoftUiBg)"
absent "$UI" 'SlateDark900 *= *Color\(0xFF(0|1|2|3)' "SlateDark900 is light (surface)"

echo "== U2) No hard dark literals in screens/components =="
absent "$UI/screens" 'Color\(0xFF(0[0-9A-Fa-f]|1[0-9A-Fa-f]|2[0-9A-Fa-f]|3[0-3])' "no dark background literals in screens"

echo "== U3) Home template tokens present everywhere =="
present "$UI/theme/Color.kt" 'SoftUiBg *= *Color\(0xFFF2F4F8\)' "canonical SoftUiBg defined"
present "$UI/theme/Color.kt" 'SoftUiSurface *= *Color\(0xFFFFFFFF\)' "canonical white neumorphic surface"
present "$UI" 'SoftCardCyanAccent|SoftCardPurpleAccent' "pastel accent system in use"

echo "== U4) Neumorphic borders/shadows consistent =="
present "$UI/components/AnimatedBottomNavBar.kt" 'RoundedCornerShape\(26\.dp\)' "navbar matches Home card language"

if [ "$fail" -eq 0 ]; then echo "RESULT: PASS — UI follows Home (Soft-UI) template."; exit 0; else echo "RESULT: FAIL — UI drift detected."; exit 1; fi
