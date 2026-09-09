/* ============================================================
   IBO PWA — Exact data from Android TradingViewModel.kt
   ============================================================ */
if('serviceWorker' in navigator){navigator.serviceWorker.register('sw.js').catch(()=>{})}

let curTab=0,curCat='ALL',curDir='ALL',favMode=false;

// ===== EXACT DATA FROM ANDROID ViewModel.kt =====
const SIGNALS=[
  {id:1,asset:"EUR/USD (OTC)",category:"OTC",direction:"CALL",strikePrice:"1.08450",currentPrice:"1.08458",expiry:"1m",payoutRate:"92%",marketRegime:"Trend Bullish",confidenceScore:94,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"شکست مقاومت با حجم بالا در کندل ۱ دقیقه‌ای و تایید اندیکاتور RSI بالای ۵۰",recommendedBrokers:"Pocket Option, Quotex",status:"ACTIVE",isFavorite:true,timestamp:Date.now()},
  {id:2,asset:"GBP/USD",category:"FOREX",direction:"PUT",strikePrice:"1.26320",currentPrice:"1.26312",expiry:"3m",payoutRate:"89%",marketRegime:"Breakout",confidenceScore:91,riskScore:"متوسط (Medium)",vetoStatus:"تایید شده",rationale:"برخورد به مقاومت داینامیک و تشکیل الگوی کندل استیک چکش معکوس",recommendedBrokers:"Pocket Option, IQ Option",status:"ACTIVE",isFavorite:false,timestamp:Date.now()-60000},
  {id:3,asset:"BTC/USDT",category:"CRYPTO",direction:"CALL",strikePrice:"68,450.00",currentPrice:"68,485.50",expiry:"5m",payoutRate:"90%",marketRegime:"Breakout",confidenceScore:88,riskScore:"متوسط (Medium)",vetoStatus:"تایید شده",rationale:"حمایت قوی در فیبوناچی ۶۱.۸٪ و افزایش مومنتوم خریداران باینری",recommendedBrokers:"Pocket Option, Quotex",status:"ACTIVE",isFavorite:true,timestamp:Date.now()-120000},
  {id:4,asset:"USD/JPY (OTC)",category:"OTC",direction:"CALL",strikePrice:"154.210",currentPrice:"154.260",expiry:"1m",payoutRate:"95%",marketRegime:"Trend Bullish",confidenceScore:96,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"کراس صعودی مووینگ اوریج ۲۰ و ۵۰ در تایم‌فریم ۱ دقیقه پاکت‌آپشن",recommendedBrokers:"Pocket Option",status:"ACTIVE",isFavorite:false,timestamp:Date.now()-180000},
  {id:5,asset:"AUD/CAD",category:"FOREX",direction:"PUT",strikePrice:"0.89120",currentPrice:"0.89110",expiry:"5m",payoutRate:"87%",marketRegime:"Range Compression",confidenceScore:86,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"اشباع خرید در استوکاستیک و بازگشت از باند بالایی بولینگر باند",recommendedBrokers:"Quotex, Alpari Fixed",status:"ACTIVE",isFavorite:false,timestamp:Date.now()-240000},
  {id:6,asset:"EUR/GBP",category:"FOREX",direction:"CALL",strikePrice:"0.85400",currentPrice:"0.85435",expiry:"5m",payoutRate:"90%",marketRegime:"Trend Bullish",confidenceScore:93,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"سیگنال موفق مطابق استراتژی پولبک به میانگین متحرک",recommendedBrokers:"Pocket Option",status:"WON",isFavorite:false,timestamp:Date.now()-3600000},
  {id:7,asset:"USD/CHF",category:"FOREX",direction:"PUT",strikePrice:"0.90210",currentPrice:"0.90175",expiry:"3m",payoutRate:"88%",marketRegime:"Breakout",confidenceScore:95,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"شکست خط روند نزولی و تثبیت در زیر سطح پیوت",recommendedBrokers:"Quotex",status:"WON",isFavorite:false,timestamp:Date.now()-7200000},
  {id:8,asset:"NZD/USD",category:"FOREX",direction:"CALL",strikePrice:"0.61200",currentPrice:"0.61180",expiry:"5m",payoutRate:"85%",marketRegime:"Range Compression",confidenceScore:78,riskScore:"بالا (High)",vetoStatus:"رد شده با Veto",rationale:"نوسان شدید خبری خارج از برنامه معاملاتی",recommendedBrokers:"Pocket Option",status:"LOST",isFavorite:false,timestamp:Date.now()-10800000},
  {id:9,asset:"Gold (XAU/USD)",category:"COMMODITIES",direction:"CALL",strikePrice:"2340.50",currentPrice:"2344.80",expiry:"5m",payoutRate:"92%",marketRegime:"Trend Bullish",confidenceScore:95,riskScore:"کم ریسک (Low)",vetoStatus:"تایید شده",rationale:"واکنش به حمایت طلایی و پرتاب قیمت به سمت مقاومت بعدی",recommendedBrokers:"Pocket Option, Quotex",status:"WON",isFavorite:false,timestamp:Date.now()-14400000},
  {id:10,asset:"ETH/USDT",category:"CRYPTO",direction:"CALL",strikePrice:"3,480.00",currentPrice:"3,495.20",expiry:"3m",payoutRate:"91%",marketRegime:"Breakout",confidenceScore:90,riskScore:"متوسط (Medium)",vetoStatus:"تایید شده",rationale:"افزایش حجم معاملات اتریوم همگام با کندل‌های صعودی قدرتمند",recommendedBrokers:"Pocket Option",status:"WON",isFavorite:false,timestamp:Date.now()-18000000}
];

const BROKERS=[
  {name:"Pocket Option",payout:"۹۲٪",speed:"سریع",min:"$۵۰",status:"پیشنهادی",online:true,reg:"ثبت‌شده MISA (کومورو)"},
  {name:"Quotex",payout:"۸۹٪",speed:"عالی",min:"$۱۰",status:"محبوب",online:true,reg:"آفشور (SVG)"},
  {name:"IQ Option",payout:"۹۴٪",speed:"فوق‌العاده",min:"$۱۰",status:"حرفه‌ای",online:false,reg:"مجوز CySEC (سابق)"},
  {name:"Alpari Fixed",payout:"۸۵٪",speed:"متوسط",min:"$۱",status:"اقتصادی",online:false,reg:"آفشور"}
];

const PLANS=[
  {id:1,title:"پلن آزمایشی ۷ روزه",days:"۷ روز",price:"۲۹۰,۰۰۰ تومان",usdt:"4.5 USDT",discount:0,popular:false,badge:"شروع سریع",features:["دسترسی به سیگنال‌های کریپتو و فارکس","پشتیبانی آنلاین تلگرام","تست استراتژی‌های باینری","کانال VIP تلگرام"]},
  {id:2,title:"پلن برنزی ۳۰ روزه",days:"۳۰ روز",price:"۹۵۰,۰۰۰ تومان",usdt:"15 USDT",discount:15,popular:false,badge:"استاندارد",features:["سیگنال‌های VIP باینری با وین‌ریت +۸۵٪","سیگنال‌های جفت ارزهای OTC و فارکس","دسترسی کامل به ژورنال ترید","پشتیبانی اختصاصی"]},
  {id:3,title:"پلن نقره‌ای ۹۰ روزه",days:"۹۰ روز",price:"۲,۵۵۰,۰۰۰ تومان",usdt:"40 USDT",discount:25,popular:false,badge:"به‌صرفه",features:["دسترسی همزمان به تمام سیگنال‌های VIP","تحلیل فاندامنتال و اخبار لحظه‌ای","ربات هشدار صوتی و تلگرام","مدیریت ریسک هوشمند مارتینگل"]},
  {id:4,title:"پلن طلایی ۱۸۰ روزه",days:"۱۸۰ روز",price:"۴,۸۰۰,۰۰۰ تومان",usdt:"75 USDT",discount:35,popular:true,badge:"محبوب‌ترین (VIP)",features:["تمامی امکانات پلن نقره‌ای بدون محدودیت","سیگنال‌های اسکالپ ۱ دقیقه‌ای پرسرعت","وبینارهای هفتگی تحلیل لایو","کانال ویژه تریدرهای حرفه‌ای","مشاوره اختصاصی ادمین"]},
  {id:5,title:"پلن الماس ۳۶۵ روزه",days:"۳۶۵ روز",price:"۸,۹۰۰,۰۰۰ تومان",usdt:"140 USDT",discount:50,popular:false,badge:"فوق ویژه (Diamond)",features:["دسترسی ۱ ساله به کلیه خدمات پریمیوم","استراتژی‌های انحصاری پاکت‌آپشن و کوتکس","ضمانت بازگشت وجه در صورت نارضایتی","پشتیبانی ۲۴/۷ تلفنی و تلگرامی"]}
];

const MARKET=[
  {s:"EUR/USD",p:"1.0845",c:"+0.12%",u:true},{s:"GBP/USD",p:"1.2631",c:"-0.05%",u:false},
  {s:"BTC/USDT",p:"68,485",c:"+1.82%",u:true},{s:"XAU/USD",p:"2,344",c:"+0.35%",u:true},
  {s:"ETH/USDT",p:"3,495",c:"+1.24%",u:true},{s:"USD/JPY",p:"154.26",c:"+0.18%",u:true}
];

// ===== INIT =====
document.addEventListener('DOMContentLoaded',()=>{
  setTimeout(()=>{document.getElementById('splash').style.display='none';document.getElementById('main-app').classList.remove('hidden')},2000);
  renderMarquee();renderAll();calcRisk();drawChart();
});

// ===== NAV =====
function switchTab(i){
  curTab=i;favMode=false;
  document.querySelectorAll('.tab-content').forEach(t=>t.classList.remove('active'));
  document.querySelectorAll('.sub-tab').forEach(t=>t.classList.remove('active'));
  document.getElementById('tab-'+i).classList.add('active');
  document.querySelector(`.sub-tab[data-tab="${i}"]`).classList.add('active');
  const titles=["داشبورد معاملات هوشمند","آپشن‌های معاملاتی","عملکرد من","ابزارهای تحلیلی"];
  document.getElementById('header-title').textContent=titles[i];
  document.getElementById('header-right-btn').textContent=i===1?'⭐':'🔔';
  if(i===1)renderSignals();
  if(i===2)renderHistory();
  if(i===3)renderPlans();
}
function headerRightAction(){
  if(curTab===1){favMode=!favMode;renderSignals()}
  else switchTab(3);
}

// ===== MARQUEE =====
function renderMarquee(){
  const el=document.getElementById('marquee');
  const items=[...MARKET,...MARKET].map(m=>`<span class="${m.u?'up':'dn'}">${m.s} ${m.p} ${m.c}</span>`).join('');
  el.innerHTML=items;
}

// ===== SIGNALS =====
function getFiltered(){
  return SIGNALS.filter(s=>{
    if(curCat!=='ALL'&&s.category!==curCat)return false;
    if(curDir!=='ALL'&&s.direction!==curDir)return false;
    if(favMode&&!s.isFavorite)return false;
    return true;
  });
}
function signalHTML(s,i){
  const g=i%5;
  const isCall=s.direction==='CALL';
  const isPut=s.direction==='PUT';
  const dirCls=isCall?'call':isPut?'put':'hold';
  const dirLbl=isCall?'CALL':isPut?'PUT':'HOLD';
  const sCls=s.status==='WON'?'s-status-won':s.status==='LOST'?'s-status-lost':s.status==='ACTIVE'?'s-status-active':'s-status-notrade';
  const sLbl=s.status==='WON'?'WIN':s.status==='LOST'?'LOSS':s.status==='ACTIVE'?'PENDING':s.status;
  return `<div class="s-card g${g}" onclick="openDetail(${s.id})">
    <div class="s-top">
      <button class="s-star" onclick="event.stopPropagation();toggleFav(${s.id})">${s.isFavorite?'⭐':'☆'}</button>
      <div class="s-info">
        <div class="s-asset">${s.asset}</div>
        <div class="s-badges">
          <span class="s-badge s-cat">${s.category}</span>
          <span class="s-badge ${sCls}">${sLbl}</span>
        </div>
        <div class="s-price">قیمت: ${s.currentPrice}</div>
      </div>
      <div class="s-right">
        <div class="s-dir-box ${dirCls}">${dirLbl} ${s.expiry}</div>
        <div class="s-conf" style="color:${isCall?'var(--cyan)':isPut?'var(--peach)':'var(--amber)'}">${s.confidenceScore}%</div>
      </div>
    </div>
  </div>`;
}
function renderSignals(){
  const list=document.getElementById('signals-list');
  list.innerHTML=getFiltered().map((s,i)=>signalHTML(s,i)).join('');
}
function renderHomeSignals(){
  document.getElementById('hot-signal').innerHTML=signalHTML(SIGNALS.reduce((a,b)=>a.confidenceScore>b.confidenceScore?a:b),0);
  document.getElementById('home-signals').innerHTML=SIGNALS.slice(0,4).map((s,i)=>signalHTML(s,i)).join('');
}
function renderAll(){renderHomeSignals()}

// ===== FILTER =====
function filterCat(c,el){
  curCat=c;document.querySelectorAll('.filter-row:first-of-type .filter-chip').forEach(e=>e.classList.remove('active'));el.classList.add('active');renderSignals();
}
function filterDir(d,el){
  curDir=d;document.querySelectorAll('.filter-row:last-of-type .filter-chip').forEach(e=>e.classList.remove('active'));el.classList.add('active');renderSignals();
}
function toggleFav(id){
  const s=SIGNALS.find(x=>x.id===id);if(s)s.isFavorite=!s.isFavorite;
  if(curTab===1)renderSignals();else renderHomeSignals();
}

// ===== DETAIL =====
function openDetail(id){
  const s=SIGNALS.find(x=>x.id===id);if(!s)return;
  document.getElementById('m-asset').textContent=s.asset;
  document.getElementById('m-body').innerHTML=`
    <div class="mb-row"><span class="mb-label">جهت:</span> <span class="mb-val">${s.direction}</span></div>
    <div class="mb-row"><span class="mb-label">Strike:</span> <span class="mb-val">${s.strikePrice}</span></div>
    <div class="mb-row"><span class="mb-label">قیمت فعلی:</span> <span class="mb-val">${s.currentPrice}</span></div>
    <div class="mb-row"><span class="mb-label">انقضا:</span> <span class="mb-val">${s.expiry}</span></div>
    <div class="mb-row"><span class="mb-label">نرخ پرداخت:</span> <span class="mb-val">${s.payoutRate}</span></div>
    <div class="mb-row"><span class="mb-label">رژیم بازار:</span> <span class="mb-val">${s.marketRegime}</span></div>
    <div class="mb-row"><span class="mb-label">اطمینان AI:</span> <span class="mb-val">${s.confidenceScore}%</span></div>
    <div class="mb-row"><span class="mb-label">ریسک:</span> <span class="mb-val">${s.riskScore}</span></div>
    <div class="mb-row"><span class="mb-label">Veto:</span> <span class="mb-val">${s.vetoStatus}</span></div>
    <div class="mb-row"><span class="mb-label">تحلیل:</span> <span class="mb-val">${s.rationale}</span></div>
    <div class="mb-row"><span class="mb-label">بروکرها:</span> <span class="mb-val">${s.recommendedBrokers}</span></div>
    <div class="modal-risk">⚠️ سیگنال‌ها صرفاً جنبه پیشنهادی دارند و توصیه مالی نیستند. ریسک از دست دادن سرمایه وجود دارد.</div>`;
  document.getElementById('signal-modal').classList.remove('hidden');
}

// ===== HISTORY =====
function renderHistory(){
  const won=SIGNALS.filter(s=>s.status==='WON').length;
  const lost=SIGNALS.filter(s=>s.status==='LOST').length;
  document.getElementById('p-won').textContent=won;
  document.getElementById('p-lost').textContent=lost;
  document.getElementById('p-total').textContent=SIGNALS.length;
  document.getElementById('history-list').innerHTML=SIGNALS.filter(s=>s.status==='WON'||s.status==='LOST').map((s,i)=>signalHTML(s,i)).join('');
}

// ===== PLANS =====
function renderPlans(){
  document.getElementById('plans-list').innerHTML=PLANS.map(p=>`
    <div class="plan-card${p.popular?' popular':''}">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div class="plan-title">${p.title}</div>
        <span class="plan-badge">${p.badge}</span>
      </div>
      <div class="plan-price">${p.price} | ${p.usdt}${p.discount?` (-${p.discount}%)`:''}</div>
      <div class="plan-features">${p.features.map(f=>'• '+f).join('<br>')}</div>
    </div>`).join('');
}

// ===== TOOLS =====
function showRiskCalc(){switchTab(3);document.getElementById('risk-calc').classList.toggle('hidden')}
function showJournal(){alert('دفتر ترید — به‌زودی')}
function openSupport(){alert('پشتیبانی ۲۴/۷ — به‌زودی')}
function openSettings(){document.getElementById('settings-modal').classList.remove('hidden')}
function closeModal(e,id){if(!e||e.target===document.getElementById(id))document.getElementById(id).classList.add('hidden')}

function calcRisk(){
  const b=parseFloat(document.getElementById('calc-bal')?.value||1000);
  const p=parseFloat(document.getElementById('calc-pct')?.value||2);
  const r=parseFloat(document.getElementById('calc-pay')?.value||92);
  const amt=(b*p/100).toFixed(2);
  const profit=(amt*r/100).toFixed(2);
  const el=document.getElementById('calc-result');
  if(el)el.innerHTML=`<div>مبلغ هر معامله: <strong>$${amt}</strong></div><div>سود بالقوه: <strong>$${profit}</strong></div><div>ضرر بالقوه: <strong>$${amt}</strong></div>`;
}

// ===== CHART =====
function drawChart(){
  const c=document.getElementById('chart');if(!c)return;
  const ctx=c.getContext('2d'),w=c.width,h=c.height;
  const data=[5,8,6,9,7,10,8],labels=['شنبه','یکشنبه','دوشنبه','سه‌شنبه','چهارشنبه','پنجشنبه','جمعه'];
  const max=Math.max(...data)+2,bw=(w-60)/data.length;
  ctx.clearRect(0,0,w,h);ctx.strokeStyle='#E2E8F0';ctx.lineWidth=1;
  for(let i=0;i<=4;i++){const y=20+(h-50)*i/4;ctx.beginPath();ctx.moveTo(40,y);ctx.lineTo(w-10,y);ctx.stroke()}
  data.forEach((v,i)=>{
    const bh=(v/max)*(h-50),x=50+i*bw,y=h-30-bh;
    const g=ctx.createLinearGradient(x,y,x,h-30);g.addColorStop(0,'#1CB4C8');g.addColorStop(1,'#7A6CF0');
    ctx.fillStyle=g;ctx.beginPath();ctx.roundRect(x,y,bw-12,bh,6);ctx.fill();
    ctx.fillStyle='#64748B';ctx.font='10px sans-serif';ctx.textAlign='center';ctx.fillText(labels[i],x+(bw-12)/2,h-12);
  });
}
