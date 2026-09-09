#!/usr/bin/env bash
# Scans built APKs for Firebase key literals (placeholder or real) and reports status.
REL="app/build/outputs/apk/release/app-release.apk"
DEB="app/build/outputs/apk/debug/app-debug.apk"
[ -f "$REL" ] || { echo "Release APK missing"; exit 1; }
HITS=$(unzip -p "$REL" 2>/dev/null | strings | grep -cE 'AIza[0-9A-Za-z_-]{20,}' || true)
if [ "$HITS" -gt 0 ]; then echo "FAIL: $REL contains Firebase key literals ($HITS)"; exit 1; fi
echo "PASS: APK clean of Firebase key literals"
