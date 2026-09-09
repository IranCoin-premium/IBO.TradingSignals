/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 25 TEST SUITE
 * MULTI-BROKER API INTEGRATION & AUTOMATED ORDER EXECUTION
 */

import { brokerRegistry, executionMonitor, BaseBrokerAdapter } from '../modules/brokers/broker-adapters.service';

describe('Part 25 — Multi-Broker API Integration & Automated Order Execution', () => {
  
  test('Work Package 25.01: Should register and retrieve broker adapters', () => {
    const brokers = brokerRegistry.getAllBrokers();
    expect(brokers).toContain('POCKET_OPTION');
    expect(brokers).toContain('QUOTEX');
    expect(brokers).toContain('DERIV');
    
    const adapter = brokerRegistry.getAdapter('POCKET_OPTION');
    expect(adapter).toBeDefined();
    expect(adapter?.brokerId).toBe('POCKET_OPTION');
  });

  test('Work Package 25.05: Should execute automated order through adapter and log results', async () => {
    const order = {
      symbol: 'EUR/USD',
      direction: 'CALL' as const,
      amount: 10,
      durationSeconds: 60
    };

    const response = await executionMonitor.executeAutomatedOrder('QUOTEX', order);
    
    expect(response.success).toBe(true);
    expect(response.externalTradeId).toBeDefined();
    
    const logs = executionMonitor.getLogs();
    expect(logs.length).toBeGreaterThan(0);
    expect(logs[logs.length - 1].brokerId).toBe('QUOTEX');
    expect(logs[logs.length - 1].latencyMs).toBeDefined();
  });

  test('Work Package 25.10: Should throw error for non-existent broker', async () => {
    const order = {
      symbol: 'BTC/USD',
      direction: 'PUT' as const,
      amount: 5,
      durationSeconds: 300
    };

    await expect(executionMonitor.executeAutomatedOrder('NON_EXISTENT' as any, order))
      .rejects.toThrow('Broker NON_EXISTENT not found in registry');
  });

  test('Work Package 25.15: Should support multiple concurrent orders (Stress Test Simulation)', async () => {
    const orders = Array(10).fill(null).map((_, i) => 
      executionMonitor.executeAutomatedOrder('DERIV', {
        symbol: `SYM-${i}`,
        direction: i % 2 === 0 ? 'CALL' : 'PUT',
        amount: 1 + i,
        durationSeconds: 60
      })
    );

    const results = await Promise.all(orders);
    expect(results.every(r => r.success)).toBe(true);
    expect(executionMonitor.getLogs().length).toBeGreaterThanOrEqual(10);
  });
});
