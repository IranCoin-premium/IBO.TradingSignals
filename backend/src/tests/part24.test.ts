/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 24 TEST SUITE
 * AI-Powered Signal Recommendation & Risk Analysis Engine
 */

import { aiRiskEngine, AiRiskEngineService } from '../modules/signals/ai-risk-engine.service';

describe('Part 24 — AI-Powered Signal Recommendation & Risk Analysis Engine', () => {
  let service: AiRiskEngineService;

  beforeEach(() => {
    service = new AiRiskEngineService();
  });

  test('Work Package 24.01: Should generate AI recommendation and risk score', () => {
    const result = service.analyzeSignal('EUR/USD', 1.0850, 0.2, 0.75, 1000);
    expect(result.riskScore).toBeGreaterThanOrEqual(0);
    expect(result.riskScore).toBeLessThanOrEqual(100);
    expect(['STRONG_BUY', 'BUY', 'NEUTRAL', 'SELL', 'STRONG_SELL']).toContain(result.recommendation);
    expect(result.recommendation).toBe('STRONG_BUY'); // High win rate, low volatility
  });

  test('Work Package 24.05: Should calculate Kelly Criterion bet size accurately', () => {
    // p = 0.6, q = 0.4, b = 0.85
    // Kelly = (0.85 * 0.6 - 0.4) / 0.85 = (0.51 - 0.4) / 0.85 = 0.11 / 0.85 = 0.129
    // But we cap it at 0.05
    const result = service.analyzeSignal('BTC/USD', 65000, 0.5, 0.6, 5000);
    expect(result.kellyBetSizePercent).toBe(0.05);
  });

  test('Work Package 24.08: Should detect trade hazards for volatile/low-accuracy assets', () => {
    const result = service.analyzeSignal('ETH/USD', 3500, 0.9, 0.4, 2000);
    expect(result.hazards).toContain('EXTREME_VOLATILITY');
    expect(result.hazards).toContain('LOW_HISTORICAL_ACCURACY');
    expect(result.recommendation).toBe('STRONG_SELL');
  });

  test('Work Package 24.15: Should maintain audit trail of AI risk assessments', () => {
    service.analyzeSignal('GBP/USD', 1.2500, 0.1, 0.55, 1000);
    const audit = service.getAuditLog();
    expect(audit.length).toBe(1);
    expect(audit[0].action).toBe('SIGNAL_ANALYZED');
  });
});
