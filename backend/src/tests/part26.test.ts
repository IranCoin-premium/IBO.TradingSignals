/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 26 TEST SUITE
 * ADVANCED TECHNICAL INDICATORS ENGINE
 */

import { indicatorsService } from '../modules/market/indicators.service';

describe('Part 26 — Advanced Technical Indicators Engine', () => {
  
  test('Work Package 26.01: Should calculate RSI accurately', () => {
    const data = [44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10, 45.42, 45.84, 46.08, 45.89, 46.03, 45.61, 46.28, 46.28, 46.00];
    const rsi = indicatorsService.calculateRSI(data, 14);
    expect(rsi.length).toBe(data.length - 14);
    expect(rsi[0]).toBeGreaterThan(0);
    expect(rsi[0]).toBeLessThan(100);
  });

  test('Work Package 26.05: Should calculate Bollinger Bands with middle, upper and lower bands', () => {
    const data = Array(30).fill(100).map((v, i) => v + i);
    const bands = indicatorsService.calculateBollingerBands(data, 20, 2);
    expect(bands.length).toBe(data.length - 20 + 1);
    expect(bands[0].upper).toBeGreaterThan(bands[0].middle);
    expect(bands[0].middle).toBeGreaterThan(bands[0].lower);
  });

  test('Work Package 26.10: Should calculate MACD lines and histogram', () => {
    const data = Array(50).fill(100).map((v, i) => v + Math.sin(i) * 10);
    const macd = indicatorsService.calculateMACD(data);
    expect(macd.macdLine.length).toBeGreaterThan(0);
    expect(macd.signalLine.length).toBeGreaterThan(0);
    expect(macd.histogram.length).toBeGreaterThan(0);
  });
});
