/**
 * IBO Ecosystem — Mobile, Native App Delivery, PWA/Native Parity & Store Readiness Test Suite
 * Master Prompt — Part 19: Work Packages 19.01 - 19.20
 */

import { MobileNativeDeliveryEngine } from '../modules/mobile/mobile-delivery.service';

describe('Part 19 — Mobile, Native App Delivery, PWA/Native Parity & Store Readiness', () => {

  describe('Work Package 19.01 & 19.02: Platform & Feature Parity Matrix', () => {
    it('should maintain comprehensive feature parity specifications across Web, PWA, Android and iOS', () => {
      const matrix = MobileNativeDeliveryEngine.getFeatureParityMatrix();
      expect(matrix.WEB).toBeDefined();
      expect(matrix.PWA).toBeDefined();
      expect(matrix.ANDROID_NATIVE).toBeDefined();
      expect(matrix.IOS_NATIVE).toBeDefined();

      // Invariants: Live stream signals and multilingual RTL support across all platforms
      expect(matrix.WEB.supportedFeatures).toContain('SIGNALS_LIVE_STREAM');
      expect(matrix.PWA.supportedFeatures).toContain('SIGNALS_LIVE_STREAM');
      expect(matrix.ANDROID_NATIVE.supportedFeatures).toContain('SIGNALS_LIVE_STREAM');
      expect(matrix.IOS_NATIVE.supportedFeatures).toContain('SIGNALS_LIVE_STREAM');

      // Native specific capabilities
      expect(matrix.ANDROID_NATIVE.supportedFeatures).toContain('BIOMETRIC_AUTH');
      expect(matrix.IOS_NATIVE.supportedFeatures).toContain('BIOMETRIC_AUTH');
    });
  });

  describe('Work Package 19.04 & 19.05: Deep Link Router & Universal URI Scheme', () => {
    it('should resolve internal signals deep link correctly', () => {
      const res = MobileNativeDeliveryEngine.resolveDeepLink('ibo://signals?id=sig-eurusd-99');
      expect(res.valid).toBe(true);
      expect(res.targetPath).toBe('/signals/sig-eurusd-99');
      expect(res.params.signalId).toBe('sig-eurusd-99');
      expect(res.canonicalWebUrl).toBe('https://ibo.app/signals/sig-eurusd-99');
    });

    it('should resolve subscription deep link correctly', () => {
      const res = MobileNativeDeliveryEngine.resolveDeepLink('ibo://subscription');
      expect(res.valid).toBe(true);
      expect(res.targetPath).toBe('/subscription');
      expect(res.canonicalWebUrl).toBe('https://ibo.app/subscription');
    });

    it('should resolve regional app store deep links for Android markets', () => {
      const bazaarLink = MobileNativeDeliveryEngine.resolveDeepLink('bazaar://details?id=com.aistudio.iranbinaryoption.trdsig');
      expect(bazaarLink.valid).toBe(true);
      expect(bazaarLink.params.store).toBe('bazaar');

      const myketLink = MobileNativeDeliveryEngine.resolveDeepLink('myket://details?id=com.aistudio.iranbinaryoption.trdsig');
      expect(myketLink.valid).toBe(true);
      expect(myketLink.params.store).toBe('myket');
    });

    it('should reject malformed or unknown deep link protocols safely', () => {
      const invalid = MobileNativeDeliveryEngine.resolveDeepLink('unknown-protocol://malicious-payload');
      expect(invalid.valid).toBe(false);
      expect(invalid.error).toBeDefined();
    });
  });

  describe('Work Package 19.06: Mobile Push Notification Token Lifecycle', () => {
    it('should register, list, and revoke mobile device push tokens', () => {
      const reg = MobileNativeDeliveryEngine.registerPushToken({
        deviceId: 'device-samsung-s24-001',
        userId: 'usr-vip-trader-01',
        platform: 'ANDROID_NATIVE',
        pushToken: 'fcm-push-token-secret-alpha-99'
      });

      expect(reg.status).toBe('ACTIVE');
      expect(reg.deviceId).toBe('device-samsung-s24-001');

      const activeTokens = MobileNativeDeliveryEngine.listActiveTokens('usr-vip-trader-01');
      expect(activeTokens.length).toBeGreaterThanOrEqual(1);

      // Revoke token
      const revoked = MobileNativeDeliveryEngine.revokePushToken('device-samsung-s24-001');
      expect(revoked).toBe(true);

      const activeAfterRevoke = MobileNativeDeliveryEngine.listActiveTokens('usr-vip-trader-01');
      expect(activeAfterRevoke.some(t => t.deviceId === 'device-samsung-s24-001')).toBe(false);
    });
  });

  describe('Work Package 19.14 & 19.15: App Store Readiness & Compliance Targets', () => {
    it('should maintain verified store submission configurations with mandatory legal risk warnings', () => {
      const stores = MobileNativeDeliveryEngine.listStoreTargets();
      expect(stores.length).toBe(4);

      const storeNames = stores.map(s => s.storeName);
      expect(storeNames).toContain('Google Play Store');
      expect(storeNames).toContain('Cafe Bazaar');
      expect(storeNames).toContain('Myket');
      expect(storeNames).toContain('RuStore');

      // Invariant: Mandatory risk warnings across all stores for financial signal apps
      for (const store of stores) {
        expect(store.riskWarningMandatory).toBe(true);
        expect(store.complianceStatus).toBe('READY');
      }
    });
  });

  describe('Work Package 19.19: Secure Signing Attestation & Build Artifacts', () => {
    it('should verify production build artifact integrity, targetSdk 36, and non-debuggable release configuration', () => {
      const build = MobileNativeDeliveryEngine.getBuildArtifact('build-android-v1.1.0-release');
      expect(build).toBeDefined();
      expect(build?.platform).toBe('ANDROID_NATIVE');
      expect(build?.versionName).toBe('1.1.0');
      expect(build?.targetSdk).toBe(36);
      expect(build?.debuggable).toBe(false); // Play Protect zero-warning compliance
      expect(build?.keystoreFingerprint).toMatch(/^SHA256:/);
      expect(build?.sha256Checksum).toBeDefined();
    });
  });
});
