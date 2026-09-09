/**
 * IBO TRADING SIGNALS ECOSYSTEM — PART 21
 * Real-Time WebSocket Market Feeds, OTC Price Engine & Payout Calculator
 */

export interface BrokerFeedConfig {
  brokerId: string;
  brokerName: string;
  supportedPairs: string[];
  isOTC: boolean;
  baseLatencyMs: number;
  payoutRate: number; // e.g., 0.92 for 92%
}

export interface PriceTick {
  symbol: string;
  brokerId: string;
  bid: number;
  ask: number;
  timestamp: number;
  isOTC: boolean;
  source: 'WEBSOCKET' | 'JITTER_BUFFER_FALLBACK' | 'OTC_GENERATOR';
}

export interface BinaryOptionTradeOrder {
  orderId: string;
  userId: string;
  symbol: string;
  brokerId: string;
  direction: 'CALL' | 'PUT';
  amount: number;
  entryPrice: number;
  strikeTime: number;
  expirySeconds: number; // 60, 120, 300
  payoutRate: number;
  status: 'PENDING' | 'ACTIVE' | 'WON' | 'LOST' | 'DRAW';
  exitPrice?: number;
  profit?: number;
}

export class MarketFeedService {
  private brokers: Map<string, BrokerFeedConfig> = new Map();
  private activeConnections: Map<string, boolean> = new Map();
  private priceBuffers: Map<string, PriceTick[]> = new Map();
  private activeOrders: Map<string, BinaryOptionTradeOrder> = new Map();
  private auditLog: Array<{ timestamp: string; action: string; meta: any }> = [];

  constructor() {
    this.initializeBrokers();
  }

  private initializeBrokers() {
    const defaultBrokers: BrokerFeedConfig[] = [
      { brokerId: 'pocket_option', brokerName: 'Pocket Option', supportedPairs: ['EUR/USD', 'GBP/USD', 'BTC/USD', 'ETH/USD'], isOTC: false, baseLatencyMs: 45, payoutRate: 0.92 },
      { brokerId: 'pocket_option_otc', brokerName: 'Pocket Option OTC', supportedPairs: ['EUR/USD-OTC', 'GBP/USD-OTC'], isOTC: true, baseLatencyMs: 30, payoutRate: 0.88 },
      { brokerId: 'quotex', brokerName: 'Quotex', supportedPairs: ['EUR/USD', 'AUD/CAD', 'USD/JPY'], isOTC: false, baseLatencyMs: 50, payoutRate: 0.94 },
      { brokerId: 'quotex_otc', brokerName: 'Quotex OTC', supportedPairs: ['EUR/USD-OTC'], isOTC: true, baseLatencyMs: 35, payoutRate: 0.90 },
      { brokerId: 'iq_option', brokerName: 'IQ Option', supportedPairs: ['EUR/USD', 'GBP/JPY', 'EUR/GBP'], isOTC: false, baseLatencyMs: 40, payoutRate: 0.89 },
      { brokerId: 'deriv', brokerName: 'Deriv (Binary.com)', supportedPairs: ['R_100', 'R_75', 'EUR/USD'], isOTC: false, baseLatencyMs: 55, payoutRate: 0.95 },
      { brokerId: 'expert_option', brokerName: 'ExpertOption', supportedPairs: ['EUR/USD', 'USD/CHF'], isOTC: false, baseLatencyMs: 60, payoutRate: 0.85 },
      { brokerId: 'binomo', brokerName: 'Binomo', supportedPairs: ['EUR/USD', 'NZD/USD'], isOTC: false, baseLatencyMs: 48, payoutRate: 0.87 },
      { brokerId: 'olymp_trade', brokerName: 'Olymp Trade', supportedPairs: ['EUR/USD', 'GBP/USD'], isOTC: false, baseLatencyMs: 42, payoutRate: 0.91 },
      { brokerId: 'pocket_broker', brokerName: 'Pocket Broker', supportedPairs: ['EUR/USD'], isOTC: false, baseLatencyMs: 50, payoutRate: 0.88 },
      { brokerId: 'broker_11', brokerName: 'Prime Binary', supportedPairs: ['EUR/USD'], isOTC: false, baseLatencyMs: 65, payoutRate: 0.86 },
      { brokerId: 'broker_12', brokerName: 'Apex Trader', supportedPairs: ['GBP/USD'], isOTC: false, baseLatencyMs: 60, payoutRate: 0.89 },
      { brokerId: 'broker_13', brokerName: 'Alpha Option', supportedPairs: ['USD/JPY'], isOTC: false, baseLatencyMs: 50, payoutRate: 0.90 },
      { brokerId: 'broker_14', brokerName: 'Zenith FX', supportedPairs: ['EUR/JPY'], isOTC: false, baseLatencyMs: 70, payoutRate: 0.85 },
      { brokerId: 'broker_15', brokerName: 'Vanguard Options', supportedPairs: ['AUD/USD'], isOTC: false, baseLatencyMs: 55, payoutRate: 0.88 },
      { brokerId: 'broker_16', brokerName: 'Titan Binary', supportedPairs: ['EUR/CAD'], isOTC: false, baseLatencyMs: 45, payoutRate: 0.92 }
    ];

    for (const b of defaultBrokers) {
      this.brokers.set(b.brokerId, b);
      this.activeConnections.set(b.brokerId, true);
    }
  }

  public getBrokerCount(): number {
    return this.brokers.size;
  }

  public getBroker(brokerId: string): BrokerFeedConfig | undefined {
    return this.brokers.get(brokerId);
  }

  public setBrokerConnection(brokerId: string, connected: boolean): void {
    if (!this.brokers.has(brokerId)) {
      throw new Error(`Unknown broker ID: ${brokerId}`);
    }
    this.activeConnections.set(brokerId, connected);
    this.auditLog.push({
      timestamp: new Date().toISOString(),
      action: 'BROKER_CONNECTION_CHANGE',
      meta: { brokerId, connected }
    });
  }

  public ingestTick(tick: PriceTick): PriceTick {
    const isConnected = this.activeConnections.get(tick.brokerId) ?? false;
    const finalTick: PriceTick = {
      ...tick,
      source: isConnected ? 'WEBSOCKET' : 'JITTER_BUFFER_FALLBACK'
    };

    if (!this.priceBuffers.has(tick.symbol)) {
      this.priceBuffers.set(tick.symbol, []);
    }
    const buf = this.priceBuffers.get(tick.symbol)!;
    buf.push(finalTick);
    if (buf.length > 100) buf.shift(); // keep sliding window

    return finalTick;
  }

  public generateOtcFallbackTick(symbol: string, brokerId: string, basePrice: number): PriceTick {
    const randomDelta = (Math.random() - 0.5) * 0.0005;
    const price = Number((basePrice + randomDelta).toFixed(5));
    const tick: PriceTick = {
      symbol,
      brokerId,
      bid: price - 0.00002,
      ask: price + 0.00002,
      timestamp: Date.now(),
      isOTC: true,
      source: 'OTC_GENERATOR'
    };

    if (!this.priceBuffers.has(symbol)) {
      this.priceBuffers.set(symbol, []);
    }
    const buf = this.priceBuffers.get(symbol)!;
    buf.push(tick);
    if (buf.length > 100) buf.shift();

    return tick;
  }

  public executeTrade(order: Omit<BinaryOptionTradeOrder, 'status' | 'orderId' | 'payoutRate'> & { payoutRate?: number }): BinaryOptionTradeOrder {
    const broker = this.brokers.get(order.brokerId);
    if (!broker) {
      throw new Error(`Invalid broker for trade execution: ${order.brokerId}`);
    }
    if (!broker.supportedPairs.includes(order.symbol)) {
      throw new Error(`Symbol ${order.symbol} not supported by broker ${order.brokerId}`);
    }

    const orderId = `ord-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
    const fullOrder: BinaryOptionTradeOrder = {
      ...order,
      orderId,
      payoutRate: broker.payoutRate,
      status: 'ACTIVE'
    };

    this.activeOrders.set(orderId, fullOrder);
    this.auditLog.push({
      timestamp: new Date().toISOString(),
      action: 'TRADE_EXECUTED',
      meta: { orderId, symbol: order.symbol, brokerId: order.brokerId, amount: order.amount, direction: order.direction }
    });

    return fullOrder;
  }

  public settleTrade(orderId: string, exitPrice: number): BinaryOptionTradeOrder {
    const order = this.activeOrders.get(orderId);
    if (!order) {
      throw new Error(`Order not found: ${orderId}`);
    }
    if (order.status !== 'ACTIVE') {
      return order;
    }

    order.exitPrice = exitPrice;
    let won = false;
    if (order.direction === 'CALL') {
      won = exitPrice > order.entryPrice;
    } else {
      won = exitPrice < order.entryPrice;
    }

    if (exitPrice === order.entryPrice) {
      order.status = 'DRAW';
      order.profit = 0;
    } else if (won) {
      order.status = 'WON';
      order.profit = Number((order.amount * order.payoutRate).toFixed(2));
    } else {
      order.status = 'LOST';
      order.profit = -order.amount;
    }

    this.auditLog.push({
      timestamp: new Date().toISOString(),
      action: 'TRADE_SETTLED',
      meta: { orderId, status: order.status, profit: order.profit, exitPrice }
    });

    return order;
  }

  public getAuditLog() {
    return this.auditLog;
  }
}

export const marketFeedService = new MarketFeedService();
