/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 24
 * AI-Powered Signal Recommendation, Risk Analysis & Kelly Criterion Engine
 */

import { logger } from '../../utils/logger';

export interface RiskAnalysisResult {
  riskScore: number; // 0-100
  recommendation: 'STRONG_BUY' | 'BUY' | 'NEUTRAL' | 'SELL' | 'STRONG_SELL';
  confidence: number; // 0-1
  kellyBetSizePercent: number;
  hazards: string[];
  suggestedStopLoss?: number;
  suggestedTakeProfit?: number;
}

export class AiRiskEngineService {
  private auditLog: any[] = [];

  /**
   * Work Package 24.01: AI Signal Recommendation Model (Deterministic Simulation)
   * In a real world, this would call Gemini/Vertex AI.
   */
  public analyzeSignal(
    symbol: string,
    currentPrice: number,
    volatility: number,
    winRate: number,
    accountBalance: number
  ): RiskAnalysisResult {
    // Deterministic simulation of AI logic
    const riskScore = Math.min(100, Math.max(0, (volatility * 100) + (Math.random() * 20)));
    
    let recommendation: RiskAnalysisResult['recommendation'] = 'NEUTRAL';
    if (winRate > 0.7 && riskScore < 40) recommendation = 'STRONG_BUY';
    else if (winRate > 0.6 && riskScore < 60) recommendation = 'BUY';
    else if (winRate < 0.4 || riskScore > 80) recommendation = 'STRONG_SELL';
    else if (winRate < 0.5 || riskScore > 70) recommendation = 'SELL';

    const hazards: string[] = [];
    if (volatility > 0.8) hazards.push('EXTREME_VOLATILITY');
    if (riskScore > 75) hazards.push('HIGH_CAPITAL_EXPOSURE');
    if (winRate < 0.45) hazards.push('LOW_HISTORICAL_ACCURACY');

    // Kelly Criterion: f* = (bp - q) / b
    // b = odds (payout rate - 1, simplified as 1:1 for this logic or use typical 0.85)
    // p = probability of win
    // q = probability of loss (1-p)
    const p = winRate;
    const q = 1 - p;
    const b = 0.85; // Average binary payout
    const kelly = Math.max(0, (b * p - q) / b);
    
    // Safety cap at 5% of balance per trade regardless of Kelly
    const kellyBetSizePercent = Math.min(0.05, kelly);

    const result: RiskAnalysisResult = {
      riskScore,
      recommendation,
      confidence: winRate,
      kellyBetSizePercent,
      hazards,
      suggestedStopLoss: currentPrice * 0.995,
      suggestedTakeProfit: currentPrice * 1.01
    };

    this.logAction('SIGNAL_ANALYZED', { symbol, recommendation, riskScore });
    return result;
  }

  private logAction(action: string, meta: any): void {
    this.auditLog.push({
      timestamp: new Date().toISOString(),
      action,
      meta
    });
    logger.info(`[AI_RISK_ENGINE] ${action}`, meta);
  }

  public getAuditLog() {
    return this.auditLog;
  }
}

export const aiRiskEngine = new AiRiskEngineService();
