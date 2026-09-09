/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 27 TEST SUITE
 * COMMUNITY SOCIAL TRADING & LEADERBOARDS
 */

import { socialTradingService } from '../modules/users/social-trading.service';

describe('Part 27 — Community Social Trading & Leaderboards', () => {
  
  test('Work Package 27.01: Should retrieve ranked leaderboard', () => {
    const leaderboard = socialTradingService.getLeaderboard();
    expect(leaderboard.length).toBeGreaterThan(0);
    expect(leaderboard[0].rank).toBe(1);
    expect(leaderboard[0].winRate).toBeGreaterThan(leaderboard[1].winRate);
  });

  test('Work Package 27.05: Should retrieve social trading feed', () => {
    const feed = socialTradingService.getSocialFeed();
    expect(feed.length).toBeGreaterThan(0);
    expect(feed[0].creatorName).toBeDefined();
  });

  test('Work Package 27.10: Should increment copy count on action', () => {
    const feed = socialTradingService.getSocialFeed();
    const initialCount = feed[0].copyCount;
    
    socialTradingService.recordCopyAction(feed[0].id);
    
    expect(feed[0].copyCount).toBe(initialCount + 1);
  });
});
