/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 28 TEST SUITE
 * VIP PUSH NOTIFICATIONS & REAL-TIME ALERT ORCHESTRATOR
 */

import { notificationOrchestrator } from '../modules/mobile/notification-orchestrator.service';
import { MobileNativeDeliveryEngine } from '../modules/mobile/mobile-delivery.service';

describe('Part 28 — VIP Push Notifications & Real-Time Alert Orchestrator', () => {
  
  beforeAll(() => {
    // Seed some tokens for testing
    MobileNativeDeliveryEngine.registerPushToken({
      deviceId: 'dev-1',
      userId: 'user-vip-123',
      platform: 'ANDROID_NATIVE',
      pushToken: 'fcm-token-1'
    });
    MobileNativeDeliveryEngine.registerPushToken({
      deviceId: 'dev-2',
      userId: 'user-regular-456',
      platform: 'ANDROID_NATIVE',
      pushToken: 'fcm-token-2'
    });
  });

  test('Work Package 28.01: Should dispatch notifications to all active devices', async () => {
    const payload = {
      title: 'Global Update',
      body: 'Check the new features!',
      priority: 'NORMAL' as const
    };

    const result = await notificationOrchestrator.dispatchNotification('ALL', payload);
    
    expect(result.messageId).toBeDefined();
    expect(result.successCount + result.failureCount).toBe(2);
  });

  test('Work Package 28.02: Should filter VIP users correctly in VIP_ONLY segment', async () => {
    const payload = {
      title: 'VIP Alpha Signal',
      body: 'Exclusive signal for VIPs',
      priority: 'HIGH' as const
    };

    const result = await notificationOrchestrator.dispatchNotification('VIP_ONLY', payload);
    
    // Only 'user-vip-123' should be targeted
    expect(result.successCount + result.failureCount).toBe(1);
    expect(result.deliveredTo[0] || result.failedDevices[0]).toBe('dev-1');
  });

  test('Work Package 28.05: Should format and dispatch automated signal alerts', async () => {
    const mockSignal = {
      id: 'sig-999',
      symbol: 'EUR/USD',
      direction: 'CALL',
      strikePrice: '1.0850',
      duration: '5m'
    };

    const result = await notificationOrchestrator.sendSignalAlert(mockSignal);
    
    expect(result.successCount + result.failureCount).toBe(2);
    expect(notificationOrchestrator.getDeliveryHistory().length).toBeGreaterThan(0);
  });
});
