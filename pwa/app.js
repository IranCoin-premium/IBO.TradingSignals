/* ============================================================
   IBO Trading Signals PWA — Main Application Logic
   Synced with Android app data models & navigation
   Version: 1.0.0 — PWA Initial Release
   ============================================================ */

// --- Service Worker ---
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('sw.js').catch(() => {});
}

// --- State ---
let currentTab = 'home';
let currentCategory = 'ALL';
let currentDirection = 'ALL';
let riskAccepted = localStorage.getItem('ibo_risk_accepted') === 'true';

// --- Mock Data (mirrors Android SignalEntity) ---
const SIGNALS = [
  { id: 1, asset: 'EUR/USD', category: 'FOREX', direction: 'CALL', strikePrice: '1.0842', currentPrice: '1.0845', expiry: '3m', payoutRate: '92%', marketRegime: 'Trend Bullish', confidenceScore: 87, riskScore: 'کم ریسک', vetoStatus: 'تایید شده', rationale: 'حمایت قوی در سطح 1.0840 با واگرایی مثبت RSI', recommendedBrokers: 'Pocket Option, Quotex', status: 'ACTIVE' },
  { id: 2, asset: 'GBP/JPY', category: 'FOREX', direction: 'PUT', strikePrice: '191.450', currentPrice: '191.480', expiry: '5m', payoutRate: '88%', marketRegime: 'Range Compression', confidenceScore: 72, riskScore: 'متوسط', vetoStatus: 'تایید شده', rationale: 'مقاومت در 191.500 با الگوی سقف دوقلو', recommendedBrokers: 'Pocket Option', status: 'ACTIVE' },
  { id: 3, asset: 'BTC/USD', category: 'CRYPTO', direction: 'CALL', strikePrice: '67,240', currentPrice: '67,180', expiry: '15m', payoutRate: '85%', marketRegime: 'Breakout', confidenceScore: 91, riskScore: 'کم ریسک', vetoStatus: 'تایید شده', rationale: 'شکست مقاومت 67,000 با حجم بالا', recommendedBrokers: 'Pocket Option, Quotex, IQ Option', status: 'ACTIVE' },
  { id: 4, asset: 'XAU/USD', category: 'COMMODITIES', direction: 'CALL', strikePrice: '2,648.50', currentPrice: '2,647.20', expiry: '3m', payoutRate: '90%', marketRegime: 'Trend Bullish', confidenceScore: 83, riskScore: 'کم ریسک', vetoStatus: 'تایید شده', rationale: 'افزایش تقاضا در بازار طلا', recommendedBrokers: 'Quotex, Pocket Option', status: 'ACTIVE' },
  { id: 5, asset: 'USD/TRY', category: 'OTC', direction: 'PUT', strikePrice: '34.280', currentPrice: '34.250', expiry: '1m', payoutRate: '94%', marketRegime: 'Range Compression', confidenceScore: 65, riskScore: 'بالا', vetoStatus: 'تایید شده', rationale: 'نوسانات شدید در بازار OTC', recommendedBrokers: 'Pocket Option', status: 'ACTIVE' },
  { id: 6, asset: 'ETH/USD', category: 'CRYPTO', direction: 'NO_TRADE', strikePrice: '3,520', currentPrice: '3,518', expiry: '5m', payoutRate: '82%', marketRegime: 'Uncertain', confidenceScore: 35, riskScore: 'بالا', vetoStatus: 'رد شده با Veto', rationale: 'عدم قطعیت بالا در بازار', recommendedBrokers: '-', status: 'NO_TRADE' },
  { id: 7, asset: 'EUR/GBP', category: 'FOREX', direction: 'CALL', strikePrice: '0.8562', currentPrice: '0.8558', expiry: '3m', payoutRate: '91%', marketRegime: 'Trend Bullish', confidenceScore: 78, riskScore: 'متوسط', vetoStatus: 'تایید شده', rationale: 'ترند صعودی کوتاه‌مدت', recommendedBrokers: 'Pocket Option, Quotex', status: 'WON' },
  { id: 8, asset: 'AUD/USD', category: 'FOREX', direction: 'PUT', strikePrice: '0.6534', currentPrice: '0.6538', expiry: '5m', payoutRate: '89%', marketRegime: 'Bearish', confidenceScore: 74, riskScore: 'متوسط', vetoStatus: 'تایید شده', rationale: 'مقاومت در سطح کلیدی', recommendedBrokers: 'Quotex', status: 'LOST' },
  { id: 9, asset: 'SOL/USD', category: 'CRYPTO', direction: 'CALL', strikePrice: '172.40', currentPrice: '172.10', expiry: '15m', payoutRate: '86%', marketRegime: 'Breakout', confidenceScore: 88, riskScore: 'کم ریسک', vetoStatus: 'تایید شده', rationale: 'شکست مقاومت با حجم تاییدی', recommendedBrokers: 'Pocket Option, IQ Option', status: 'ACTIVE' },
  { id: 10, asset: 'NZD/USD', category: 'OTC', direction: 'CALL', strikePrice: '0.5912', currentPrice: '0.5908', expiry: '1m', payoutRate: '93%', marketRegime: 'Range Compression', confidenceScore: 68, riskScore: 'متوسط', vetoStatus: 'تایید شده', rationale: 'بازگشت از حمایت', recommendedBrokers: 'Pocket Option', status: 'ACTIVE' },
  { id: 11, asset: 'USD/CAD', category: 'FOREX', direction: 'PUT', strikePrice: '1.3645', currentPrice: '1.3648', expiry: '3m', payoutRate: '90%', marketRegime: 'Bearish', confidenceScore: 76, riskScore: 'متوسط', vetoStatus: 'تایید شده', rationale: 'مقاومت در باند بالایی بولینگر', recommendedBrokers: 'Quotex, Pocket Option', status: 'ACTIVE' },
  { id: 12, asset: 'CRUDE/OIL', category: 'COMMODITIES', direction: 'CALL', strikePrice: '78.40', currentPrice: '78.25', expiry: '5m', payoutRate: '87%', marketRegime: 'Trend Bullish', confidenceScore: 81, riskScore: 'کم ریسک', vetoStatus: 'تایید شده', rationale: 'افزایش تقاضای فصلی', recommendedBrokers: 'Pocket Option', status: 'ACTIVE' },
];

const BROKERS = [
  { name: 'Pocket Option', online: true },
  { name: 'Quotex', online: true },
  { name: 'IQ Option', online: false },
];

const MARKET_DATA = [
  { symbol: 'EUR/USD', price: '1.0845', change: '+0.12%', up: true },
  { symbol: 'GBP/JPY', price: '191.48', change: '-0.08%', up: false },
  { symbol: 'BTC/USD', price: '67,180', change: '+2.34%', up: true },
  { symbol: 'XAU/USD', price: '2,647', change: '+0.45%', up: true },
  { symbol: 'ETH/USD', price: '3,518', change: '-0.22%', up: false },
  { symbol: 'SOL/USD', price: '172.10', change: '+1.88%', up: true },
  { symbol: 'USD/TRY', price: '34.25', change: '+0.31%', up: true },
  { symbol: 'AUD/USD', price: '0.6538', change: '-0.15%', up: false },
];

// --- Init ---
document.addEventListener('DOMContentLoaded', () => {
  setTimeout(() => {
    document.getElementById('splash').style.display = 'none';
    document.getElementById('main-app').classList.remove('hidden');
  }, 2000);

  if (riskAccepted) {
    document.getElementById('risk-banner').classList.add('hidden');
  }

  renderMarquee();
  renderSignals();
  renderHomeSignals();
  calcRisk();
  drawChart();
});

// --- Navigation ---
function switchTab(tab) {
  currentTab = tab;
  document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById('tab-' + tab).classList.add('active');
  document.querySelector(`.nav-item[data-tab="${tab}"]`).classList.add('active');
  if (tab === 'signals') renderSignals();
  if (tab === 'performance') renderHistory();
}

// --- Risk Banner ---
function acceptRisk() {
  riskAccepted = true;
  localStorage.setItem('ibo_risk_accepted', 'true');
  document.getElementById('risk-banner').classList.add('hidden');
}

// --- Marquee ---
function renderMarquee() {
  const el = document.getElementById('market-marquee');
  const items = [...MARKET_DATA, ...MARKET_DATA].map(m =>
    `<span class="marquee-item ${m.up ? 'up' : 'down'}">${m.symbol} ${m.price} ${m.change}</span>`
  ).join('');
  el.innerHTML = items;
}

// --- Signals ---
function getFilteredSignals() {
  return SIGNALS.filter(s => {
    if (currentCategory !== 'ALL' && s.category !== currentCategory) return false;
    if (currentDirection !== 'ALL' && s.direction !== currentDirection) return false;
    return true;
  });
}

function renderSignals() {
  const list = document.getElementById('signals-list');
  list.innerHTML = getFilteredSignals().map(s => signalCardHTML(s)).join('');
}

function renderHomeSignals() {
  const el = document.getElementById('home-signals');
  el.innerHTML = SIGNALS.slice(0, 3).map(s => signalCardHTML(s)).join('');
}

function signalCardHTML(s) {
  const dirClass = s.direction === 'CALL' ? 'call' : s.direction === 'PUT' ? 'put' : 'no-trade';
  const dirLabel = s.direction === 'CALL' ? '📈 CALL' : s.direction === 'PUT' ? '📉 PUT' : '⏸ NO TRADE';
  const timeAgo = getTimeAgo(s.id);
  const statusColor = s.status === 'WON' ? 'var(--green)' : s.status === 'LOST' ? 'var(--red)' : s.status === 'NO_TRADE' ? 'var(--text-muted)' : 'var(--cyan)';
  const progressWidth = s.confidenceScore;
  const progressColor = s.confidenceScore > 75 ? 'var(--green)' : s.confidenceScore > 50 ? 'var(--amber)' : 'var(--red)';
  return `
    <div class="signal-card" onclick="openSignalDetail(${s.id})">
      <div class="signal-header">
        <span class="signal-asset">${s.asset}</span>
        <span class="signal-badge ${dirClass}">${dirLabel}</span>
      </div>
      <div class="signal-meta">
        <span class="signal-meta-item">Strike: <strong>${s.strikePrice}</strong></span>
        <span class="signal-meta-item">Expiry: <strong>${s.expiry}</strong></span>
        <span class="signal-meta-item">Payout: <strong>${s.payoutRate}</strong></span>
      </div>
      <div class="signal-progress">
        <div class="signal-progress-bar">
          <div class="signal-progress-fill" style="width:${progressWidth}%;background:${progressColor}"></div>
        </div>
      </div>
      <div class="signal-footer">
        <span class="signal-time">${timeAgo} — ${s.status === 'WON' ? '✅ برد' : s.status === 'LOST' ? '❌ باخت' : s.status}</span>
        <span class="signal-action" style="color:${statusColor}">Confidence: ${s.confidenceScore}%</span>
      </div>
    </div>`;
}

function getTimeAgo(id) {
  const mins = [2, 5, 8, 12, 18, 25, 35, 48, 60, 75, 90, 120];
  const m = mins[id % mins.length];
  return m < 60 ? `${m} دقیقه پیش` : `${Math.floor(m / 60)} ساعت پیش`;
}

// --- Filter ---
function filterCategory(cat) {
  currentCategory = cat;
  document.querySelectorAll('.filter-bar:first-of-type .filter-chip').forEach(c => {
    c.classList.toggle('active', c.dataset.cat === cat);
  });
  renderSignals();
}

function filterDirection(dir) {
  currentDirection = dir;
  document.querySelectorAll('.filter-bar.secondary .filter-chip').forEach(c => {
    c.classList.toggle('active', c.dataset.dir === dir);
  });
  renderSignals();
}

// --- Signal Detail ---
function openSignalDetail(id) {
  const s = SIGNALS.find(x => x.id === id);
  if (!s) return;
  document.getElementById('modal-asset').textContent = s.asset;
  document.getElementById('modal-body').innerHTML = `
    <div style="margin-bottom:12px"><strong>جهت:</strong> ${s.direction}</div>
    <div style="margin-bottom:12px"><strong>Strike:</strong> ${s.strikePrice}</div>
    <div style="margin-bottom:12px"><strong>قیمت فعلی:</strong> ${s.currentPrice}</div>
    <div style="margin-bottom:12px"><strong>انقضا:</strong> ${s.expiry}</div>
    <div style="margin-bottom:12px"><strong>نرخ پرداخت:</strong> ${s.payoutRate}</div>
    <div style="margin-bottom:12px"><strong>رژیم بازار:</strong> ${s.marketRegime}</div>
    <div style="margin-bottom:12px"><strong>اطمینان:</strong> ${s.confidenceScore}%</div>
    <div style="margin-bottom:12px"><strong>ریسک:</strong> ${s.riskScore}</div>
    <div style="margin-bottom:12px"><strong>دلیل:</strong> ${s.rationale}</div>
    <div style="margin-bottom:12px"><strong>بروکرها:</strong> ${s.recommendedBrokers}</div>
    <div style="padding:10px;border-radius:12px;background:var(--bg);margin-top:12px;font-size:12px;color:var(--text-secondary)">
      ⚠️ سیگنال‌ها صرفاً جنبه پیشنهادی دارند و توصیه مالی نیستند.
    </div>`;
  document.getElementById('signal-modal').classList.remove('hidden');
}

function closeSignalModal(e) {
  if (!e || e.target === document.getElementById('signal-modal')) {
    document.getElementById('signal-modal').classList.add('hidden');
  }
}

// --- Settings ---
function openSettings() { document.getElementById('settings-modal').classList.remove('hidden'); }
function closeSettings(e) {
  if (!e || e.target === document.getElementById('settings-modal')) {
    document.getElementById('settings-modal').classList.add('hidden');
  }
}

function changeLang(lang) {
  document.documentElement.lang = lang;
  document.documentElement.dir = (lang === 'ar' || lang === 'fa') ? 'rtl' : 'ltr';
}

// --- Tools ---
function openRiskCalculator() {
  switchTab('tools');
  document.getElementById('risk-calc').classList.toggle('hidden');
}

function calcRisk() {
  const balance = parseFloat(document.getElementById('calc-balance')?.value || 1000);
  const percent = parseFloat(document.getElementById('calc-percent')?.value || 2);
  const payout = parseFloat(document.getElementById('calc-payout')?.value || 92);
  const tradeAmount = (balance * percent / 100).toFixed(2);
  const profit = (tradeAmount * payout / 100).toFixed(2);
  const el = document.getElementById('calc-result');
  if (el) el.innerHTML = `
    <div>مبلغ هر معامله: <strong>$${tradeAmount}</strong></div>
    <div>سود بالقوه: <strong>$${profit}</strong></div>
    <div>ضرر بالocomplete: <strong>$${tradeAmount}</strong></div>`;
}

function openJournal() { alert('دفتر ترید — به‌زودی'); }
function openSupport() { alert('پشتیبانی — به‌زودی'); }
function openSubscriptions() { alert('اشتراک — به‌زودی'); }
function openMarkets() { alert('بازارها — به‌زودی'); }
function openTutorial() { alert('آموزش — به‌زودی'); }

// --- Performance Chart ---
function drawChart() {
  const canvas = document.getElementById('perf-chart');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const w = canvas.width, h = canvas.height;
  const data = [5, 8, 6, 9, 7, 10, 8];
  const labels = ['شنبه', 'یکشنبه', 'دوشنبه', 'سه‌شنبه', 'چهارشنبه', 'پنجشنبه', 'جمعه'];
  const max = Math.max(...data) + 2;
  const barW = (w - 60) / data.length;

  ctx.clearRect(0, 0, w, h);
  ctx.strokeStyle = '#E2E8F0';
  ctx.lineWidth = 1;

  // Grid lines
  for (let i = 0; i <= 4; i++) {
    const y = 20 + (h - 50) * i / 4;
    ctx.beginPath(); ctx.moveTo(40, y); ctx.lineTo(w - 10, y); ctx.stroke();
  }

  // Bars
  data.forEach((v, i) => {
    const barH = (v / max) * (h - 50);
    const x = 50 + i * barW;
    const y = h - 30 - barH;
    const grad = ctx.createLinearGradient(x, y, x, h - 30);
    grad.addColorStop(0, '#1CB4C8');
    grad.addColorStop(1, '#7A6CF0');
    ctx.fillStyle = grad;
    ctx.beginPath();
    ctx.roundRect(x, y, barW - 12, barH, 6);
    ctx.fill();
    // Label
    ctx.fillStyle = '#64748B';
    ctx.font = '10px sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(labels[i], x + (barW - 12) / 2, h - 12);
  });
}

// --- Render History ---
function renderHistory() {
  const el = document.getElementById('history-list');
  const history = SIGNALS.filter(s => s.status === 'WON' || s.status === 'LOST');
  el.innerHTML = history.map(s => signalCardHTML(s)).join('');
}
