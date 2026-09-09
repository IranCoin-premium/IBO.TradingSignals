/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 22 TEST SUITE
 * BINARY OPTIONS EXPIRY & HIGH-PRECISION PAYOUT ALGORITHM
 */

import { PayoutEngineService } from '../modules/market/payout-engine.service';

describe('Part 22 — Binary Options Expiry & High-Precision Payout Algorithm', () => {
  let service: PayoutEngineService;

  beforeEach(() => {
    service = new PayoutEngineService();
  });

  test('Work Package 22.01: Should calculate payout profit with 8-decimal precision', () => {
    const profit = service.calculatePayout("100.00", "0.925");
    expect(profit).toBe("92.50000000");
  });

  test('Work Package 22.05: Should handle expiry states correctly (OPEN -> SETTLED)', () => {
    const trade = service.createTrade({
      userId: 'u1',
      brokerId: 'pocket',
      symbol: 'EUR/USD',
      direction: 'CALL',
      amount: "50.00",
      payoutRate: "0.90",
      entryPrice: "1.08500",
      entryTime: Date.now(),
      expiryTime: Date.now() + 60000
    });

    expect(trade.status).toBe('OPEN');
    service.updateTradeStatus(trade.tradeId, 'EXPIRING');
    expect(service.getTrade(trade.tradeId)?.status).toBe('EXPIRING');
  });

  test('Work Package 22.12: Should verify WIN outcome and exact payout amount', () => {
    const trade = service.createTrade({
      userId: 'u2',
      brokerId: 'quotex',
      symbol: 'BTC/USD',
      direction: 'CALL',
      amount: "10.00",
      payoutRate: "0.85",
      entryPrice: "65000.00",
      entryTime: Date.now(),
      expiryTime: Date.now() + 300000
    });

    const settled = service.settleTrade(trade.tradeId, "65010.00");
    expect(settled.outcome).toBe('WIN');
    expect(settled.profit).toBe("8.50000000");
    expect(settled.payoutAmount).toBe("18.50000000");
  });

  test('Work Package 22.12: Should verify LOSS outcome and zero payout', () => {
    const trade = service.createTrade({
      userId: 'u3',
      brokerId: 'iq',
      symbol: 'GBP/USD',
      direction: 'PUT',
      amount: "100.00",
      payoutRate: "0.80",
      entryPrice: "1.25000",
      entryTime: Date.now(),
      expiryTime: Date.now() + 120000
    });

    const settled = service.settleTrade(trade.tradeId, "1.25100"); // Price went up, should lose on PUT
    expect(settled.outcome).toBe('LOSS');
    expect(settled.profit).toBe("-100.00000000");
    expect(settled.payoutAmount).toBe("0.00000000");
  });

  test('Work Package 22.12: Should handle DRAW (Refund) outcome', () => {
    const trade = service.createTrade({
      userId: 'u4',
      brokerId: 'binomo',
      symbol: 'EUR/GBP',
      direction: 'CALL',
      amount: "25.00",
      payoutRate: "0.82",
      entryPrice: "0.85000",
      entryTime: Date.now(),
      expiryTime: Date.now() + 60000
    });

    const settled = service.settleTrade(trade.tradeId, "0.85000");
    expect(settled.outcome).toBe('DRAW');
    expect(settled.profit).toBe("0.00000000");
    expect(settled.payoutAmount).toBe("25.00000000");
  });

  test('Work Package 22.20: Should maintain complete immutable audit trail', () => {
    const trade = service.createTrade({
      userId: 'u5',
      brokerId: 'deriv',
      symbol: 'EUR/USD',
      direction: 'CALL',
      amount: "1.00",
      payoutRate: "0.95",
      entryPrice: "1.0000",
      entryTime: Date.now(),
      expiryTime: Date.now() + 60000
    });
    service.settleTrade(trade.tradeId, "1.0001");

    const audit = service.getAuditLog();
    expect(audit.length).toBeGreaterThanOrEqual(2);
    expect(audit[0].action).toBe('TRADE_CREATED');
    expect(audit[audit.length - 1].action).toBe('TRADE_SETTLED');
  });
});
