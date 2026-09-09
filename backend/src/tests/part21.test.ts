/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 21 TEST SUITE
 * Real-Time WebSocket Market Feeds, OTC Price Engine & Payout Calculator
 */

import { marketFeedService, MarketFeedService } from '../modules/market/market-feed.service';

describe('Part 21 — Real-Time WebSocket Market Feeds & OTC Price Engine', () => {
  let service: MarketFeedService;

  beforeEach(() => {
    service = new MarketFeedService();
  });

  test('Work Package 21.01: Should initialize exactly 16 binary option broker feeds', () => {
    expect(service.getBrokerCount()).toBe(16);
    const pocket = service.getBroker('pocket_option');
    expect(pocket).toBeDefined();
    expect(pocket?.brokerName).toBe('Pocket Option');
    expect(pocket?.payoutRate).toBe(0.92);
  });

  test('Work Package 21.04 & 21.05: Should handle jitter buffer fallback when broker WebSocket disconnects', () => {
    const symbol = 'EUR/USD';
    const brokerId = 'pocket_option';

    // Disconnect primary broker WebSocket feed
    service.setBrokerConnection(brokerId, false);

    const tick = service.ingestTick({
      symbol,
      brokerId,
      bid: 1.08500,
      ask: 1.08502,
      timestamp: Date.now(),
      isOTC: false,
      source: 'WEBSOCKET'
    });

    expect(tick.source).toBe('JITTER_BUFFER_FALLBACK');
  });

  test('Work Package 21.08: Should generate valid OTC fallback price ticks for weekend markets', () => {
    const otcTick = service.generateOtcFallbackTick('EUR/USD-OTC', 'pocket_option_otc', 1.08500);
    expect(otcTick.isOTC).toBe(true);
    expect(otcTick.source).toBe('OTC_GENERATOR');
    expect(otcTick.bid).toBeLessThan(otcTick.ask);
  });

  test('Work Package 21.12 & 21.15: Should execute trade and settle payout accurately (WIN)', () => {
    const trade = service.executeTrade({
      userId: 'user-trader-01',
      symbol: 'EUR/USD',
      brokerId: 'pocket_option',
      direction: 'CALL',
      amount: 100,
      entryPrice: 1.08500,
      strikeTime: Date.now(),
      expirySeconds: 60
    });

    expect(trade.status).toBe('ACTIVE');
    expect(trade.payoutRate).toBe(0.92);

    const settled = service.settleTrade(trade.orderId, 1.08550); // Higher exit price -> CALL WIN
    expect(settled.status).toBe('WON');
    expect(settled.profit).toBe(92); // 100 * 0.92
  });

  test('Work Package 21.16: Should settle trade accurately (LOST)', () => {
    const trade = service.executeTrade({
      userId: 'user-trader-02',
      symbol: 'EUR/USD',
      brokerId: 'quotex',
      direction: 'PUT',
      amount: 200,
      entryPrice: 1.08500,
      strikeTime: Date.now(),
      expirySeconds: 60
    });

    const settled = service.settleTrade(trade.orderId, 1.08580); // Higher exit price -> PUT LOST
    expect(settled.status).toBe('LOST');
    expect(settled.profit).toBe(-200);
  });

  test('Work Package 21.20: Should maintain complete immutable audit trail of feeds and trades', () => {
    service.setBrokerConnection('deriv', false);
    const trade = service.executeTrade({
      userId: 'user-trader-03',
      symbol: 'R_100',
      brokerId: 'deriv',
      direction: 'CALL',
      amount: 50,
      entryPrice: 5000.0,
      strikeTime: Date.now(),
      expirySeconds: 300
    });
    service.settleTrade(trade.orderId, 5010.0);

    const audit = service.getAuditLog();
    expect(audit.length).toBeGreaterThanOrEqual(3);
    expect(audit.some(a => a.action === 'BROKER_CONNECTION_CHANGE')).toBe(true);
    expect(audit.some(a => a.action === 'TRADE_EXECUTED')).toBe(true);
    expect(audit.some(a => a.action === 'TRADE_SETTLED')).toBe(true);
  });
});
