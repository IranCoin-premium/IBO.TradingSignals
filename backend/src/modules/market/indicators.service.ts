/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 26
 * ADVANCED TECHNICAL INDICATORS ENGINE (RSI, MACD, BOLLINGER BANDS)
 */

import { logger } from '../../utils/logger';

export interface Candle {
  timestamp: number;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export class IndicatorsService {
  /**
   * Relative Strength Index (RSI)
   */
  public calculateRSI(closes: number[], period: number = 14): number[] {
    if (closes.length <= period) return [];

    let gains: number[] = [];
    let losses: number[] = [];

    for (let i = 1; i < closes.length; i++) {
      const diff = closes[i] - closes[i - 1];
      gains.push(diff > 0 ? diff : 0);
      losses.push(diff < 0 ? Math.abs(diff) : 0);
    }

    let avgGain = gains.slice(0, period).reduce((a, b) => a + b) / period;
    let avgLoss = losses.slice(0, period).reduce((a, b) => a + b) / period;

    let rsi: number[] = [100 - (100 / (1 + avgGain / avgLoss))];

    for (let i = period; i < gains.length; i++) {
      avgGain = (avgGain * (period - 1) + gains[i]) / period;
      avgLoss = (avgLoss * (period - 1) + losses[i]) / period;
      rsi.push(100 - (100 / (1 + avgGain / avgLoss)));
    }

    return rsi;
  }

  /**
   * Moving Average Convergence Divergence (MACD)
   */
  public calculateMACD(closes: number[], fast: number = 12, slow: number = 26, signal: number = 9) {
    const emaFast = this.calculateEMA(closes, fast);
    const emaSlow = this.calculateEMA(closes, slow);

    const macdLine = emaFast.map((val, i) => val - emaSlow[i + (fast - slow)]);
    const signalLine = this.calculateEMA(macdLine, signal);

    return {
      macdLine,
      signalLine,
      histogram: macdLine.slice(signal - 1).map((val, i) => val - signalLine[i])
    };
  }

  private calculateEMA(data: number[], period: number): number[] {
    const k = 2 / (period + 1);
    let ema = [data[0]];
    for (let i = 1; i < data.length; i++) {
      ema.push(data[i] * k + ema[i - 1] * (1 - k));
    }
    return ema;
  }

  /**
   * Bollinger Bands
   */
  public calculateBollingerBands(closes: number[], period: number = 20, stdDev: number = 2) {
    let bands = [];
    for (let i = period - 1; i < closes.length; i++) {
      const slice = closes.slice(i - period + 1, i + 1);
      const mean = slice.reduce((a, b) => a + b) / period;
      const variance = slice.reduce((a, b) => a + Math.pow(b - mean, 2), 0) / period;
      const sd = Math.sqrt(variance);

      bands.push({
        middle: mean,
        upper: mean + stdDev * sd,
        lower: mean - stdDev * sd
      });
    }
    return bands;
  }
}

export const indicatorsService = new IndicatorsService();
