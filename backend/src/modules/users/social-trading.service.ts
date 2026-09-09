/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 27
 * COMMUNITY SOCIAL TRADING & LEADERBOARD ENGINE
 */

import { logger } from '../../utils/logger';

export interface LeaderboardEntry {
  userId: string;
  username: string;
  avatarUrl?: string;
  totalTrades: number;
  winRate: number;
  totalProfit: number;
  rank: number;
  isVip: boolean;
}

export interface SocialTradeSignal {
  id: string;
  creatorId: string;
  creatorName: string;
  asset: string;
  direction: 'CALL' | 'PUT';
  timestamp: string;
  copyCount: number;
}

export class SocialTradingService {
  private leaderboard: LeaderboardEntry[] = [];
  private socialFeed: SocialTradeSignal[] = [];

  constructor() {
    this.seedMockData();
  }

  private seedMockData() {
    this.leaderboard = [
      { userId: 'u1', username: 'CryptoKing', totalTrades: 1240, winRate: 88.5, totalProfit: 15400, rank: 1, isVip: true },
      { userId: 'u2', username: 'AlphaTrader', totalTrades: 850, winRate: 82.1, totalProfit: 9200, rank: 2, isVip: true },
      { userId: 'u3', username: 'BinaryPro', totalTrades: 620, winRate: 79.4, totalProfit: 4800, rank: 3, isVip: false },
      { userId: 'u4', username: 'MarketWizard', totalTrades: 450, winRate: 75.0, totalProfit: 3200, rank: 4, isVip: true },
      { userId: 'u5', username: 'ScalperX', totalTrades: 310, winRate: 72.8, totalProfit: 2100, rank: 5, isVip: false },
    ];

    this.socialFeed = [
      { id: 's1', creatorId: 'u1', creatorName: 'CryptoKing', asset: 'BTC/USD', direction: 'CALL', timestamp: new Date().toISOString(), copyCount: 42 },
      { id: 's2', creatorId: 'u2', creatorName: 'AlphaTrader', asset: 'EUR/USD', direction: 'PUT', timestamp: new Date().toISOString(), copyCount: 18 },
    ];
  }

  /**
   * Work Package 27.01: Global Leaderboard
   */
  public getLeaderboard(limit: number = 10): LeaderboardEntry[] {
    return this.leaderboard.slice(0, limit);
  }

  /**
   * Work Package 27.05: Social Copy Trading Feed
   */
  public getSocialFeed(): SocialTradeSignal[] {
    return this.socialFeed;
  }

  /**
   * Work Package 27.10: Record Copy Action
   */
  public recordCopyAction(signalId: string) {
    const signal = this.socialFeed.find(s => s.id === signalId);
    if (signal) {
      signal.copyCount++;
      logger.info(`[SOCIAL_TRADING] Signal ${signalId} copied. New count: ${signal.copyCount}`);
    }
  }
}

export const socialTradingService = new SocialTradingService();
