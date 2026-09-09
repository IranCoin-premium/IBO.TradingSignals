/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 29
 * AUTOMATED QUALITY ASSURANCE & SYSTEM INTEGRITY CHECKS
 */

import { indicatorsService } from '../modules/market/indicators.service';
import { brokerRegistry } from '../modules/brokers/broker-adapters.service';
import { socialTradingService } from '../modules/users/social-trading.service';

describe('Part 29 — Automated Quality Assurance & System Integrity', () => {
  
  test('Integrity Check: Technical Indicators Accuracy', () => {
    const data = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25];
    const rsi = indicatorsService.calculateRSI(data, 14);
    
    // RSI should be high for uptrend
    expect(rsi[0]).toBeGreaterThan(70);
    expect(rsi[rsi.length - 1]).toBeGreaterThan(70);
  });

  test('Integrity Check: Broker Registry Completeness', () => {
    const brokers = brokerRegistry.getAllBrokers();
    const mandatoryBrokers = ['POCKET_OPTION', 'QUOTEX', 'IQ_OPTION', 'DERIV'];
    
    mandatoryBrokers.forEach(id => {
      expect(brokers).toContain(id);
      expect(brokerRegistry.getAdapter(id as any)).toBeDefined();
    });
  });

  test('Integrity Check: Social Trading Rank Logic', () => {
    const leaderboard = socialTradingService.getLeaderboard();
    
    // Ensure ranks are sequential and logical
    for (let i = 0; i < leaderboard.length - 1; i++) {
      expect(leaderboard[i].rank).toBe(i + 1);
      // Top ranks should have better win rates in mock data
      expect(leaderboard[i].winRate).toBeGreaterThanOrEqual(leaderboard[i+1].winRate);
    }
  });

  test('System Stress Test: High-Frequency Calculation', () => {
    const start = Date.now();
    const iterations = 1000;
    const data = Array(100).fill(100).map(() => Math.random() * 100);
    
    for (let i = 0; i < iterations; i++) {
      indicatorsService.calculateBollingerBands(data);
    }
    
    const duration = Date.now() - start;
    // Calculation should be fast (< 1000ms for 1000 iterations in container)
    expect(duration).toBeLessThan(1000);
  });
});
