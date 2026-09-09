/* ============================================================
   IBO PWA — Complete Web Version
   Exact data from Android TradingViewModel.kt
   All screens: Dashboard, Signals, Performance, Tools,
   Subscriptions, Trade Journal, Articles, Settings, Profile
   ============================================================ */
if('serviceWorker' in navigator){navigator.serviceWorker.register('sw.js').catch(()=>{})}

let curTab=0,curCat='ALL',curDir='ALL',favMode=false,activeScreen='home';
let signalData=[], brokerData=[], planData=[], tradeData=[], articleData=[];
let currentScreen='home';

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
  {id:"pocket",name:"Pocket Option",faName:"پاکت آپشن",payout:"۹۲٪",speed:"سریع",min:"$۵۰",status:"پیشنهادی",online:true,reg:"ثبت‌شده MISA (کومورو)",desc:"محبوب‌ترین پلتفرم باینری آپشن با پشتیبانی ۲۴/۷"},
  {id:"quotex",name:"Quotex",faName:"کوتکس",payout:"۸۹٪",speed:"عالی",min:"$۱۰",status:"محبوب",online:true,reg:"آفشور (SVG)",desc:"پلتفرم مدرن با رابط کاربری ساده و سریع"},
  {id:"iq",name:"IQ Option",faName:"آی‌کیو آپشن",payout:"۹۴٪",speed:"فوق‌العاده",min:"$۱۰",status:"حرفه‌ای",online:false,reg:"مجوز CySEC (سابق)",desc:"پلتفرم حرفه‌ای با ابزارهای تحلیلی پیشرفته"},
  {id:"alpari",name:"Alpari Fixed",faName:"آلپاری فیکس",payout:"۸۵٪",speed:"متوسط",min:"$۱",status:"اقتصادی",online:false,reg:"آفشور",desc:"بروکر اقتصادی مناسب برای شروع با سرمایه کم"}
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

const ARTICLES=[
  {id:1,cat:"strateji",title:"استراتژی پولبک به میانگین متحرک",summary:"آموزش کامل استراتژی بازگشت به مووینگ اوریج با مثال‌های عملی",content:"استراتژی پولبک یکی از محبوب‌ترین استراتژی‌ها در باینری آپشن است. وقتی قیمت به مووینگ اوریج می‌رسد و واکنش نشان می‌دهد، سیگنال ورود صادر می‌شود.\n\nمراحل اجرا:\n۱. انتخاب تایم‌فریم ۱ یا ۵ دقیقه\n۲. رسم مووینگ اوریج ساده با دوره ۲۰\n۳. صبر برای لمس قیمت با MO\n۴. تایید با کندل بازگشتی\n۵. ورود با پوزیشن مناسب"},
  {id:2,cat:"kandele",title:"الگوهای کندل استیک پرکاربرد",summary:"شناخت کندل‌های چکش، دوجی، اینگالفینگ و ستاره صبحگاهی",content:"کندل استیک یکی از مهم‌ترین ابزارهای تحلیل تکنیکال است. هر کندل اطلاعات قیمت باز، بسته، بالاترین و پایین‌ترین را نشان می‌دهد.\n\nکندل‌های مهم:\n• چکش (Hammer): سیگنال بازگشت صعودی\n• دوجی (Doji): عدم تصمیم بازار\n• اینگالفینگ: تغییر قوی روند\n• ستاره صبحگاهی: شروع روند صعودی"},
  {id:3,cat:"risk",title:"مدیریت ریسک در باینری آپشن",summary:"قوانین طلایی حفظ سرمایه و مدیریت صحیح ریسک",content:"مدیریت ریسک مهم‌ترین مهارت یک تریدر موفق است.\n\nقوانین اصلی:\n۱. هرگز بیش از ۲٪ سرمایه در هر معامله ریسک نکنید\n۲. حداکثر ضرر روزانه ۶٪ کل سرمایه\n۳. استفاده از استاپ لاس\n۴. ثبت تمام معاملات در ژورنال\n۵. بررسی هفتگی عملکرد"},
  {id:4,cat:"price",title:"پرایس اکشن در تایم‌فریم کوتاه",summary:"تحلیل حرکات قیمت در تایم‌فریم ۱ تا ۵ دقیقه",content:"پرایس اکشن به بررسی حرکات خالص قیمت بدون استفاده از اندیکاتورها می‌پردازد.\n\nنکات کلیدی:\n• سطوح حمایت و مقاومت افقی\n• خطوط روند\n• الگوهای قیمتی (مثلث، پرچم)\n• حجم معاملات در تایید سیگنال"},
  {id:5,cat:"indicators",title:"اندیکاتور RSI و استوکاستیک",summary:"آموزش استفاده صحیح از RSI و استوکاستیک در ترید",content:"RSI (شاخص قدرت نسبی):\n• بالای ۷۰: اشباع خرید\n• زیر ۳۰: اشباع فروش\n• تقاطم ۵۰: تایید روند\n\nاستوکاستیک:\n• تقاطم K و D در نواحی اشباع\n• سیگنال بازگشت از ۲۰ و ۸۰\n• همراه با ترند لاین"},
  {id:6,cat:"strateji",title:"استراتژی بریک‌اوت",summary:"شناسایی و معامله شکست‌های قیمتی مهم",content:"بریک‌اوت وقتی رخ می‌دهد که قیمت سطح حمایت یا مقاومت مهمی را بشکند.\n\nمراحل شناسایی:\n۱. شناسایی سطوح کلیدی\n۲. انتظار برای شکست با حجم بالا\n۳. تایید پولبک به سطح شکسته شده\n۴. ورود بعد از تثبیت"}
];

const TRADE_LOGS=[
  {id:1,asset:"EUR/USD",direction:"CALL",result:"WIN",pnl:46.00,timestamp:Date.now()-3600000*2,strategy:"پولبک"},
  {id:2,asset:"GBP/USD",direction:"PUT",result:"WIN",pnl:35.60,timestamp:Date.now()-3600000*5,strategy:"بریک‌اوت"},
  {id:3,asset:"BTC/USDT",direction:"CALL",result:"LOSS",pnl:-50.00,timestamp:Date.now()-3600000*8,strategy:"RSI"},
  {id:4,asset:"USD/JPY",direction:"CALL",result:"WIN",pnl:47.50,timestamp:Date.now()-3600000*12,strategy:"کراس MO"},
  {id:5,asset:"AUD/CAD",direction:"PUT",result:"WIN",pnl:43.50,timestamp:Date.now()-3600000*18,strategy:"بولینگر"},
  {id:6,asset:"EUR/GBP",direction:"CALL",result:"WIN",pnl:45.00,timestamp:Date.now()-3600000*24,strategy:"پولبک"},
  {id:7,asset:"USD/CHF",direction:"PUT",result:"WIN",pnl:44.00,timestamp:Date.now()-3600000*30,strategy:"پیوت"},
  {id:8,asset:"NZD/USD",direction:"CALL",result:"LOSS",pnl:-42.50,timestamp:Date.now()-3600000*36,strategy:"بریک‌اوت"}
];

// ===== INIT =====
document.addEventListener('DOMContentLoaded',()=>{
  setTimeout(()=>{
    document.getElementById('splash').style.display='none';
    document.getElementById('main-app').classList.remove('hidden');
    signalData=SIGNALS;
    brokerData=BROKERS;
    planData=PLANS;
    tradeData=TRADE_LOGS;
    renderDashboard();
    renderSignals();
    renderPerformance();
    renderTools();
    startMarquee();
  },2000);
});

// ===== MARQUEE =====
let marqueePos=0;
function startMarquee(){
  const el=document.getElementById('dashboard-content');
  const mEl=document.getElementById('marquee');
  if(!mEl)return;
  setInterval(()=>{
    marqueePos-=1;
    if(Math.abs(marqueePos)>200)marqueePos=0;
    mEl.style.transform=`translateX(${marqueePos}px)`;
  },30);
}

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
  document.getElementById('sub-tabs').classList.remove('hidden');
  document.getElementById('bottom-nav').classList.remove('hidden');
}

function openScreen(screen){
  currentScreen=screen;
  document.querySelectorAll('.tab-content').forEach(t=>t.classList.remove('active'));
  document.querySelectorAll('.screen').forEach(s=>s.classList.add('hidden'));
  document.getElementById('sub-tabs').classList.add('hidden');

  if(screen==='home'){
    document.getElementById('bottom-nav').classList.remove('hidden');
    document.getElementById('header-title').textContent='داشبورد معاملات هوشمند';
    document.getElementById('tab-'+curTab).classList.add('active');
    document.getElementById('sub-tabs').classList.remove('hidden');
  }else if(screen==='subscriptions'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='فروشگاه اشتراک';
    renderSubscriptions();
  }else if(screen==='journal'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='ژورنال ترید';
    renderJournal();
  }else if(screen==='articles'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='دانشنامه باینری آپشن';
    renderArticles();
  }else if(screen==='settings'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='تنظیمات';
    renderSettings();
  }else if(screen==='profile'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='پروفایل من';
    renderProfile();
  }else if(screen==='history'){
    document.getElementById('bottom-nav').classList.add('hidden');
    document.getElementById('header-title').textContent='تاریخچه سیگنال‌ها';
    renderHistory();
  }

  // Update nav
  document.querySelectorAll('.nav-item').forEach(n=>n.classList.remove('active'));
  const navIdx={home:0,subscriptions:1,journal:2,articles:3,profile:4}[screen];
  if(navIdx!==undefined)document.querySelectorAll('.nav-item')[navIdx].classList.add('active');
}

function headerRightAction(){
  if(curTab===1){favMode=!favMode;renderSignals()}
}

// ===== RENDER DASHBOARD =====
function renderDashboard(){
  const el=document.getElementById('dashboard-content');
  const hot=SIGNALS.find(s=>s.confidenceScore>=94)||SIGNALS[0];
  el.innerHTML=`
    <!-- Brand -->
    <div class="brand-header">
      <div class="brand-logo-sm">IBO</div>
      <div><div class="brand-motto">اولین و تنها پلتفرم رسمی سیگنال باینری آپشن ایران</div></div>
    </div>
    <!-- Hero -->
    <div class="hero">
      <div class="hero-greeting">خوش آمدید، تریدر محترم 👋</div>
      <div class="hero-sub">آخرین بروزرسانی: ${new Date().toLocaleTimeString('fa-IR')}</div>
      <div class="hero-stats">
        <div class="hero-stat"><div class="hero-stat-val">+۲۱۴٪</div><div class="hero-stat-lbl">سود هفتگی</div></div>
        <div class="hero-stat"><div class="hero-stat-val">۸۷٪</div><div class="hero-stat-lbl">وین‌ریت</div></div>
        <div class="hero-stat"><div class="hero-stat-val">۲۳</div><div class="hero-stat-lbl">سیگنال فعال</div></div>
      </div>
    </div>
    <!-- Hot Signal -->
    <div class="hot-signal">
      <div class="hot-label">🔥 سیگنال داغ لحظه‌ای</div>
      <div class="hot-asset">${hot.asset} → ${hot.direction}</div>
      <div class="hot-detail">Confidence: ${hot.confidenceScore}% | Expiry: ${hot.expiry} | Payout: ${hot.payoutRate}</div>
    </div>
    <!-- Market Marquee -->
    <div class="marquee-wrap">
      <div class="marquee-title">📊 بازارهای لحظه‌ای</div>
      <div class="marquee-inner" id="marquee">
        ${[...MARKET,...MARKET].map(m=>`<span class="${m.u?'up':'dn'}">${m.s} ${m.p} ${m.c}</span>`).join('')}
      </div>
    </div>
    <!-- Quick Actions -->
    <div class="qa-row">
      <div class="qa-item" onclick="switchTab(1)"><div class="qa-icon" style="background:linear-gradient(135deg,#E4F7FA,#D1F5F0)">📡</div><div class="qa-label">سیگنال‌ها</div></div>
      <div class="qa-item" onclick="openRiskCalc()"><div class="qa-icon" style="background:linear-gradient(135deg,#ECEBFC,#DDD8FE)">🧮</div><div class="qa-label">مدیریت ریسک</div></div>
      <div class="qa-item" onclick="openScreen('journal')"><div class="qa-icon" style="background:linear-gradient(135deg,#E0F2FE,#C7E6FD)">📋</div><div class="qa-label">ژورنال ترید</div></div>
      <div class="qa-item" onclick="openScreen('articles')"><div class="qa-icon" style="background:linear-gradient(135deg,#FFF0E7,#FFE0D0)">🎓</div><div class="qa-label">آموزش‌ها</div></div>
    </div>
    <!-- Brokers -->
    <div class="section-title">بروکرهای متصل و پشتیبانی‌شده</div>
    <div class="broker-wrap">
      <div class="broker-header">
        <div class="broker-header-left">
          <span style="color:var(--primary);font-size:14px">📈</span>
          <span style="font-size:12.5px;font-weight:700">نوار تایم‌لاین بروکرها (${BROKERS.length} بروکر فعال)</span>
        </div>
        <div class="broker-live-badge">لایوموشن فعال</div>
      </div>
      <div class="broker-list">${BROKERS.map(b=>`
        <div class="broker-chip" onclick="openBrokerDetail('${b.id}')">
          <div class="broker-chip-top">
            <div class="broker-mono" style="background:${getBrokerBg(b.name)};color:${getBrokerAccent(b.name)}">${b.name.substring(0,2).toUpperCase()}</div>
            <div>
              <div style="display:flex;align-items:center;gap:5px">
                <span class="broker-chip-name">${b.name}</span>
                <span class="broker-chip-payout">${b.payout}</span>
              </div>
            </div>
          </div>
          <div class="broker-chip-fa">${b.faName}${b.online?'<span class="broker-chip-otc">⚡ OTC</span>':''}</div>
        </div>
      `).join('')}</div>
    </div>
    <!-- Recent Signals -->
    <div class="section-title">جدیدترین سیگنال‌های لحظه‌ای</div>
    <div id="dashboard-signals">${SIGNALS.slice(0,4).map((s,i)=>signalCardHTML(s,i)).join('')}</div>
  `;
}

// ===== RENDER SIGNALS =====
function renderSignals(){
  const cats=['ALL','OTC','FOREX','CRYPTO','COMMODITIES'];
  const catLabels={ALL:'همه',OTC:'OTC',FOREX:'فارکس',CRYPTO:'کریپتو',COMMODITIES:'کالاها'};
  const filtered=getFiltered();
  document.getElementById('signals-content').innerHTML=`
    <div class="brand-header">
      <div class="brand-logo-sm">IBO</div>
      <div><div class="brand-motto">سیگنال‌های معاملاتی هوشمند</div></div>
    </div>
    <div class="vip-row">
      <div class="vip-badge">⭐ VIP — پلن طلایی فعال</div>
      <div style="font-size:11px;color:var(--text2)">مانده: ۴۵ روز</div>
    </div>
    <div class="cat-filters">${cats.map(c=>`<button class="cat-chip ${curCat===c?'active':''}" onclick="curCat='${c}';renderSignals()">${catLabels[c]}</button>`).join('')}</div>
    <div id="signals-list">${filtered.length?filtered.map((s,i)=>signalCardHTML(s,i)).join(''):'<div class="empty-state"><div class="empty-icon">🔍</div><div class="empty-text">سیگنالی در این دسته یافت نشد</div></div>'}</div>
  `;
}

function getFiltered(){
  return SIGNALS.filter(s=>{
    if(curCat!=='ALL'&&s.category!==curCat)return false;
    if(favMode&&!s.isFavorite)return false;
    return true;
  });
}

function signalCardHTML(s,i){
  const g=i%5;
  const isCall=s.direction==='CALL';
  const dirCls=isCall?'call':'put';
  const sCls=s.status==='WON'?'s-status-won':s.status==='LOST'?'s-status-lost':s.status==='ACTIVE'?'s-status-active':'s-status-notrade';
  const sLbl=s.status==='WON'?'WIN':s.status==='LOST'?'LOSS':s.status==='ACTIVE'?'PENDING':s.status;
  const confCls=s.confidenceScore>=90?'high':s.confidenceScore>=80?'med':'low';
  return `<div class="s-card g${g}" onclick="openSignalDetail(${s.id})">
    <div class="s-top">
      <div class="s-info">
        <div class="s-asset">${s.asset}</div>
        <div class="s-badges">
          <span class="s-badge s-cat">${s.category}</span>
          <span class="s-badge ${sCls}">${sLbl}</span>
        </div>
      </div>
      <button class="s-star ${s.isFavorite?'fav':''}" onclick="event.stopPropagation();toggleFav(${s.id})">${s.isFavorite?'⭐':'☆'}</button>
    </div>
    <div class="s-dir ${dirCls}">${s.direction} → ${s.expiry}</div>
    <div class="s-row"><span>Strike: <strong>${s.strikePrice}</strong></span><span>Current: <strong>${s.currentPrice}</strong></span></div>
    <div class="s-row"><span>Payout: <strong>${s.payoutRate}</strong></span><span>Risk: <strong>${s.riskScore}</strong></span></div>
    <div class="s-conf-bar"><div class="s-conf-fill ${confCls}" style="width:${s.confidenceScore}%"></div></div>
    <div class="s-actions">
      <button class="s-action-btn primary" onclick="event.stopPropagation();openSignalDetail(${s.id})">جزئیات</button>
      <button class="s-action-btn secondary" onclick="event.stopPropagation();openFeedback(${s.id})">گزارش</button>
    </div>
  </div>`;
}

// ===== RENDER PERFORMANCE =====
function renderPerformance(){
  const wins=tradeData.filter(t=>t.result==='WIN').length;
  const losses=tradeData.filter(t=>t.result==='LOSS').length;
  const total=tradeData.length;
  const winRate=total>0?Math.round((wins/total)*100):0;
  const pnl=tradeData.reduce((a,t)=>a+t.pnl,0);
  const pnlStr=(pnl>=0?'+':'')+pnl.toFixed(2);
  const days=['ش','ی','د','س','چ','پ','ج'];
  const dayCounts=[0,0,0,0,0,0,0];
  tradeData.forEach(t=>{
    const d=new Date(t.timestamp).getDay();
    const idx=d===6?0:d===0?1:d===1?2:d===2?3:d===3?4:d===4?5:6;
    dayCounts[idx]++;
  });
  const maxDay=Math.max(...dayCounts,1);

  document.getElementById('performance-content').innerHTML=`
    <div class="brand-header">
      <div class="brand-logo-sm">IBO</div>
      <div><div class="brand-motto">آمار و عملکرد معاملاتی</div></div>
    </div>
    <!-- Donut -->
    <div class="donut-card">
      <div class="donut-wrap">
        <canvas id="donutCanvas" width="170" height="170"></canvas>
        <div class="donut-center">
          <div class="donut-pct">${winRate}%</div>
          <div class="donut-label">وین‌ریت عملکرد</div>
        </div>
      </div>
    </div>
    <!-- Stats -->
    <div class="stats-card">
      <div class="stat-row"><span class="stat-lbl">کل معاملات</span><span class="stat-val primary">${total}</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">سود/زیان خالص</span><span class="stat-val ${pnl>=0?'mint':'peach'}">${pnlStr}$</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">معاملات موفق (Win)</span><span class="stat-val mint">${wins}</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">معاملات ناموفق (Loss)</span><span class="stat-val peach">${losses}</span></div>
    </div>
    <!-- Weekly Bar -->
    <div class="stats-card">
      <div style="font-size:13px;font-weight:700;color:var(--text1)">روند معاملاتی هفته جاری</div>
      <div class="bar-row">
        ${days.map((d,i)=>{
          const h=Math.round((dayCounts[i]/maxDay)*80)||4;
          const colors=['var(--primary)','var(--accent-rose)','var(--accent-blue)','var(--accent-mint)','var(--accent-peach)','var(--primary)','var(--accent-blue)'];
          return `<div class="bar-col"><div class="bar" style="height:${h}px;background:${colors[i]}"></div><div class="bar-label">${d}</div></div>`;
        }).join('')}
      </div>
    </div>
  `;
  // Draw donut
  setTimeout(()=>drawDonut(winRate),100);
}

function drawDonut(pct){
  const c=document.getElementById('donutCanvas');
  if(!c)return;
  const ctx=c.getContext('2d');
  const cx=85,cy=85,r=65,lw=18;
  ctx.clearRect(0,0,170,170);
  ctx.beginPath();ctx.arc(cx,cy,r,0,Math.PI*2);ctx.strokeStyle='#F2F4F8';ctx.lineWidth=lw;ctx.stroke();
  const grad=ctx.createLinearGradient(0,0,170,170);
  grad.addColorStop(0,'#3B82F6');grad.addColorStop(0.5,'#E83E8C');grad.addColorStop(1,'#7A6CF0');
  ctx.beginPath();ctx.arc(cx,cy,r,-Math.PI/2,-Math.PI/2+(Math.PI*2*pct/100));ctx.strokeStyle=grad;ctx.lineWidth=lw;ctx.lineCap='round';ctx.stroke();
}

// ===== RENDER TOOLS =====
function renderTools(){
  document.getElementById('tools-content').innerHTML=`
    <div class="brand-header">
      <div class="brand-logo-sm">IBO</div>
      <div><div class="brand-motto">ابزارهای تخصصی ترید باینری آپشن</div></div>
    </div>
    <div class="section-title">ابزارهای تخصصی ترید باینری آپشن</div>
    <div class="tool-card tool-mint" onclick="openRiskCalc()">
      <div class="tool-icon">🧮</div>
      <div><div class="tool-title">ماشین‌حساب هوشمند مدیریت سرمایه و مارتینگل</div><div class="tool-desc">محاسبه اندازه پوزیشن، پله‌های مارتینگل و ضریب ریسک</div></div>
    </div>
    <div class="tool-card tool-purple" onclick="openScreen('journal')">
      <div class="tool-icon">📊</div>
      <div><div class="tool-title">دفترچه ژورنال و تحلیل عملکرد تریدها</div><div class="tool-desc">ثبت و بررسی علت بردها و باخت‌ها برای ارتقای استراتژی</div></div>
    </div>
    <div class="tool-card tool-blue" onclick="openScreen('articles')">
      <div class="tool-icon">🎓</div>
      <div><div class="tool-title">دانشنامه و استراتژی‌های باینری آپشن</div><div class="tool-desc">آموزش کندل‌استیک، سطوح حمایت و مقاومت و پرایس اکشن</div></div>
    </div>
    <div class="tool-card tool-peach" onclick="openSupport()">
      <div class="tool-icon">💬</div>
      <div><div class="tool-title">چت آنلاین پشتیبانی ۲۴/۷ تریدرها</div><div class="tool-desc">پاسخگویی سریع کارشناسان به سوالات و مشکلات فعال‌سازی</div></div>
    </div>
  `;
}

// ===== RENDER SUBSCRIPTIONS =====
function renderSubscriptions(){
  document.getElementById('screen-subscriptions').classList.remove('hidden');
  document.getElementById('screen-subscriptions').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">فروشگاه رسمی اشتراک‌های Iran Binary Option</div>
    </div>
    <div style="text-align:center;margin-bottom:16px">
      <div style="background:rgba(122,108,240,.1);border-radius:20px;padding:6px 14px;display:inline-block;font-size:12px;font-weight:700;color:var(--primary)">💎 فروشگاه رسمی اشتراک‌های IBO</div>
    </div>
    ${PLANS.map(p=>`
      <div class="plan-card ${p.popular?'popular':''}">
        ${p.popular?'<div class="plan-badge">محبوب‌ترین</div>':''}
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div class="plan-title">${p.title}</div>
          <div style="background:rgba(122,108,240,.1);border-radius:10px;padding:3px 8px;font-size:10px;font-weight:700;color:var(--primary)">${p.badge}</div>
        </div>
        <div class="plan-days">${p.days}</div>
        <div class="plan-price">${p.price}</div>
        <div class="plan-usdt">${p.usdt}</div>
        ${p.discount?`<div class="plan-discount">🔥 ${p.discount}% تخفیف ویژه</div>`:''}
        <div class="plan-features">${p.features.map(f=>`<div class="plan-feature">${f}</div>`).join('')}</div>
        <button class="plan-buy-btn" onclick="alert('پردازش خرید... (نیاز به اتصال درگاه پرداخت)')">خرید اشتراک ${p.days}</button>
      </div>
    `).join('')}
    <div style="text-align:center;padding:20px;color:var(--text3);font-size:11px">
      پرداخت از طریق تتر (USDT) و درگاه بانکی • ضمانت بازگشت وجه
    </div>
  `;
}

// ===== RENDER JOURNAL =====
function renderJournal(){
  const wins=tradeData.filter(t=>t.result==='WIN').length;
  const losses=tradeData.filter(t=>t.result==='LOSS').length;
  document.getElementById('screen-journal').classList.remove('hidden');
  document.getElementById('screen-journal').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">ژورنال ترید</div>
    </div>
    <div class="stats-card" style="margin-bottom:14px">
      <div style="display:flex;gap:10px;margin-bottom:12px">
        <div style="flex:1;background:var(--bg);border-radius:12px;padding:10px;text-align:center">
          <div style="font-size:18px;font-weight:900;color:var(--primary)">${tradeData.length}</div>
          <div style="font-size:10px;color:var(--text3)">کل معاملات</div>
        </div>
        <div style="flex:1;background:var(--bg);border-radius:12px;padding:10px;text-align:center">
          <div style="font-size:18px;font-weight:900;color:var(--accent-mint)">${wins}</div>
          <div style="font-size:10px;color:var(--text3)">موفق</div>
        </div>
        <div style="flex:1;background:var(--bg);border-radius:12px;padding:10px;text-align:center">
          <div style="font-size:18px;font-weight:900;color:var(--accent-peach)">${losses}</div>
          <div style="font-size:10px;color:var(--text3)">ناموفق</div>
        </div>
      </div>
    </div>
    ${tradeData.map(t=>`
      <div class="journal-entry">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px">
          <div style="font-size:14px;font-weight:900;color:var(--text1)">${t.asset}</div>
          <div style="font-size:12px;font-weight:700;color:${t.result==='WIN'?'var(--accent-mint)':'var(--accent-peach)'}">${t.result} ${t.pnl>=0?'+':''}${t.pnl.toFixed(2)}$</div>
        </div>
        <div class="journal-row"><span class="lbl">جهت</span><span class="val" style="color:${t.direction==='CALL'?'var(--accent-mint)':'var(--accent-peach)'}">${t.direction}</span></div>
        <div class="journal-row"><span class="lbl">استراتژی</span><span class="val">${t.strategy}</span></div>
        <div class="journal-row"><span class="lbl">زمان</span><span class="val">${new Date(t.timestamp).toLocaleString('fa-IR')}</span></div>
      </div>
    `).join('')}
  `;
}

// ===== RENDER ARTICLES =====
function renderArticles(){
  const cats=[...new Set(ARTICLES.map(a=>a.cat))];
  const catLabels={strateji:'استراتژی',kandele:'کندل‌استیک',risk:'مدیریت ریسک',price:'پرایس اکشن',indicators:'اندیکاتور'};
  const catColors={strateji:'var(--primary)',kandele:'var(--accent-rose)',risk:'var(--accent-peach)',price:'var(--accent-mint)',indicators:'var(--accent-blue)'};
  document.getElementById('screen-articles').classList.remove('hidden');
  document.getElementById('screen-articles').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">دانشنامه باینری آپشن</div>
    </div>
    <div class="cat-filters" style="margin-bottom:12px">
      <button class="cat-chip active" onclick="this.parentElement.querySelectorAll('.cat-chip').forEach(c=>c.classList.remove('active'));this.classList.add('active')">همه</button>
      ${cats.map(c=>`<button class="cat-chip" onclick="this.parentElement.querySelectorAll('.cat-chip').forEach(c=>c.classList.remove('active'));this.classList.add('active');filterArticles('${c}')">${catLabels[c]||c}</button>`).join('')}
    </div>
    <div id="articles-list">${ARTICLES.map(a=>`
      <div class="article-card" onclick="toggleArticle(${a.id})">
        <div class="article-category" style="background:${catColors[a.cat]||'var(--primary)'}22;color:${catColors[a.cat]||'var(--primary)'}">${catLabels[a.cat]||a.cat}</div>
        <div class="article-title">${a.title}</div>
        <div class="article-summary">${a.summary}</div>
        <div class="article-content" id="article-${a.id}" style="display:none;white-space:pre-line">${a.content}</div>
      </div>
    `).join('')}</div>
  `;
}

function toggleArticle(id){
  const el=document.getElementById('article-'+id);
  el.style.display=el.style.display==='none'?'block':'none';
}

// ===== RENDER SETTINGS =====
function renderSettings(){
  document.getElementById('screen-settings').classList.remove('hidden');
  document.getElementById('screen-settings').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">تنظیمات</div>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">اعلان‌های سیگنال</div><div class="setting-desc">دریافت نوتیفیکیشن سیگنال‌های جدید</div></div>
      <button class="toggle on" onclick="this.classList.toggle('on')"></button>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">صدای هشدار</div><div class="setting-desc">پخش صدا هنگام دریافت سیگنال</div></div>
      <button class="toggle" onclick="this.classList.toggle('on')"></button>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">لرزش گوشی</div><div class="setting-desc">ویبره هنگام سیگنال مهم</div></div>
      <button class="toggle on" onclick="this.classList.toggle('on')"></button>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">حالت تاریک</div><div class="setting-desc">تغییر تم به حالت شب</div></div>
      <button class="toggle" onclick="this.classList.toggle('on')"></button>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">زبان برنامه</div><div class="setting-desc">فارسی (پیش‌فرض)</div></div>
      <div style="font-size:12px;color:var(--primary);font-weight:700">فارسی</div>
    </div>
    <div class="setting-item">
      <div><div class="setting-lbl">نسخه برنامه</div><div class="setting-desc">ورژن فعلی PWA</div></div>
      <div style="font-size:12px;color:var(--text3);font-weight:700">v2.1.0</div>
    </div>
  `;
}

// ===== RENDER PROFILE =====
function renderProfile(){
  document.getElementById('screen-profile').classList.remove('hidden');
  document.getElementById('screen-profile').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">پروفایل من</div>
    </div>
    <div class="profile-card">
      <div class="profile-avatar">👤</div>
      <div class="profile-name">تریدر VIP</div>
      <div class="profile-email">trader@iranbinary.ir</div>
      <div class="profile-stat-row">
        <div class="profile-stat"><div class="profile-stat-val">${tradeData.length}</div><div class="profile-stat-lbl">معامله</div></div>
        <div class="profile-stat"><div class="profile-stat-val">${tradeData.filter(t=>t.result==='WIN').length}</div><div class="profile-stat-lbl">برد</div></div>
        <div class="profile-stat"><div class="profile-stat-val">${Math.round(tradeData.filter(t=>t.result==='WIN').length/Math.max(tradeData.length,1)*100)}%</div><div class="profile-stat-lbl">وین‌ریت</div></div>
      </div>
    </div>
    <div class="stats-card">
      <div class="stat-row"><span class="stat-lbl">پلن فعلی</span><span class="stat-val primary">طلایی ۱۸۰ روزه</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">مانده اشتراک</span><span class="stat-val mint">۴۵ روز</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">تاریخ عضویت</span><span class="stat-val">۱۴۰۵/۰۶/۰۱</span></div>
      <hr>
      <div class="stat-row"><span class="stat-lbl">کد دعوت</span><span class="stat-val blue">IBO-2026-VIP</span></div>
    </div>
    <div class="tool-card tool-mint" onclick="openScreen('subscriptions')">
      <div class="tool-icon">⭐</div>
      <div><div class="tool-title">ارتقاء اشتراک</div><div class="tool-desc"> upgrade to higher plan</div></div>
    </div>
    <div class="tool-card tool-purple" onclick="alert('لینک دعوت دوستان کپی شد')">
      <div class="tool-icon">👥</div>
      <div><div class="tool-title">دعوت دوستان</div><div class="tool-desc">کسب درآمد از زیرمجموعه‌گیری</div></div>
    </div>
  `;
}

// ===== RENDER HISTORY =====
function renderHistory(){
  document.getElementById('screen-history').classList.remove('hidden');
  document.getElementById('screen-history').innerHTML=`
    <div class="screen-header">
      <button class="screen-back" onclick="openScreen('home')">→</button>
      <div class="screen-title">تاریخچه سیگنال‌ها</div>
    </div>
    ${SIGNALS.map((s,i)=>signalCardHTML(s,i)).join('')}
  `;
}

// ===== MODALS =====
function openSignalDetail(id){
  const s=SIGNALS.find(x=>x.id===id);
  if(!s)return;
  document.getElementById('modal-overlay').classList.remove('hidden');
  const modal=document.getElementById('signal-detail-modal');
  modal.classList.remove('hidden');
  modal.innerHTML=`
    <div class="modal-header">
      <div class="modal-title">${s.asset}</div>
      <button class="modal-close" onclick="closeModal()">✕</button>
    </div>
    <div style="display:flex;gap:8px;margin-bottom:16px">
      <span class="s-badge s-cat" style="font-size:12px;padding:4px 10px">${s.category}</span>
      <span class="s-badge ${s.status==='WON'?'s-status-won':s.status==='LOST'?'s-status-lost':'s-status-active'}" style="font-size:12px;padding:4px 10px">${s.status==='WON'?'WIN':s.status==='LOST'?'LOSS':'PENDING'}</span>
    </div>
    <div class="detail-row"><span class="detail-lbl">جهت معامله</span><span class="detail-val ${s.direction==='CALL'?'mint':'peach'}">${s.direction}</span></div>
    <div class="detail-row"><span class="detail-lbl">قیمت اعتصاب</span><span class="detail-val">${s.strikePrice}</span></div>
    <div class="detail-row"><span class="detail-lbl">قیمت فعلی</span><span class="detail-val">${s.currentPrice}</span></div>
    <div class="detail-row"><span class="detail-lbl">زمان انقضا</span><span class="detail-val">${s.expiry}</span></div>
    <div class="detail-row"><span class="detail-lbl">نرخ پرداخت</span><span class="detail-val mint">${s.payoutRate}</span></div>
    <div class="detail-row"><span class="detail-lbl">رژیم بازار</span><span class="detail-val primary">${s.marketRegime}</span></div>
    <div class="detail-row"><span class="detail-lbl">امتیاز اطمینان</span><span class="detail-val ${s.confidenceScore>=90?'mint':s.confidenceScore>=80?'blue':'peach'}">${s.confidenceScore}%</span></div>
    <div class="detail-row"><span class="detail-lbl">وضعیت ریسک</span><span class="detail-val">${s.riskScore}</span></div>
    <div class="detail-row"><span class="detail-lbl">وضعیت Veto</span><span class="detail-val ${s.vetoStatus.includes('تایید')?'mint':'peach'}">${s.vetoStatus}</span></div>
    <div class="detail-row"><span class="detail-lbl">تحلیل منطقی</span><span class="detail-val" style="font-size:11px;font-weight:600;line-height:1.6">${s.rationale}</span></div>
    <div class="detail-row"><span class="detail-lbl">بروکرهای پیشنهادی</span><span class="detail-val blue">${s.recommendedBrokers}</span></div>
    <div class="s-conf-bar" style="margin-top:12px"><div class="s-conf-fill ${s.confidenceScore>=90?'high':s.confidenceScore>=80?'med':'low'}" style="width:${s.confidenceScore}%"></div></div>
  `;
}

function openRiskCalc(){
  document.getElementById('modal-overlay').classList.remove('hidden');
  const modal=document.getElementById('risk-calc-modal');
  modal.classList.remove('hidden');
  modal.innerHTML=`
    <div class="modal-header">
      <div class="modal-title">ماشین‌حساب هوشمند مدیریت سرمایه</div>
      <button class="modal-close" onclick="closeModal()">✕</button>
    </div>
    <div class="calc-label">موجودی حساب (USDT)</div>
    <input class="calc-input" type="number" id="calcBalance" value="500" oninput="updateCalc()">
    <div class="calc-label">درصد ریسک هر معامله: <span id="calcRiskPct">2</span>%</div>
    <input class="calc-slider" type="range" min="0.5" max="10" step="0.5" value="2" id="calcRiskSlider" oninput="document.getElementById('calcRiskPct').textContent=this.value;updateCalc()">
    <div class="calc-label">نرخ پرداخت بروکر: <span id="calcPayout">92</span>%</div>
    <input class="calc-slider" type="range" min="70" max="95" step="1" value="92" id="calcPayoutSlider" oninput="document.getElementById('calcPayout').textContent=this.value;updateCalc()">
    <div class="calc-label">وین‌ریت تخمینی: <span id="calcWinRate">65</span>%</div>
    <input class="calc-slider" type="range" min="40" max="90" step="1" value="65" id="calcWinRateSlider" oninput="document.getElementById('calcWinRate').textContent=this.value;updateCalc()">
    <div class="calc-result" id="calcResults"></div>
    <button class="calc-btn" onclick="closeModal()">بستن</button>
  `;
  updateCalc();
}

function updateCalc(){
  const bal=parseFloat(document.getElementById('calcBalance')?.value)||500;
  const risk=parseFloat(document.getElementById('calcRiskSlider')?.value)||2;
  const payout=parseFloat(document.getElementById('calcPayoutSlider')?.value)||92;
  const wr=parseFloat(document.getElementById('calcWinRateSlider')?.value)||65;
  const tradeSize=(bal*risk/100).toFixed(2);
  const payoutAmt=(tradeSize*payout/100).toFixed(2);
  const maxLoss=(bal*0.06).toFixed(2);
  const p=wr/100,q=1-p,b=payout/100;
  const kelly=(((b*p-q)/b)*100).toFixed(1);
  const ev=((p*payout/100)-(q*1)).toFixed(4);
  const el=document.getElementById('calcResults');
  if(!el)return;
  el.innerHTML=`
    <div class="calc-result-row"><span class="calc-result-lbl">اندازه پوزیشن پیشنهادی</span><span class="calc-result-val" style="color:var(--primary)">${tradeSize} USDT</span></div>
    <div class="calc-result-row"><span class="calc-result-lbl">سود بالقوه</span><span class="calc-result-val" style="color:var(--accent-mint)">+${payoutAmt} USDT</span></div>
    <div class="calc-result-row"><span class="calc-result-lbl">حداکثر ضرر روزانه (۶٪)</span><span class="calc-result-val" style="color:var(--accent-peach)">-${maxLoss} USDT</span></div>
    <div class="calc-result-row"><span class="calc-result-lbl">Kelly Criterion</span><span class="calc-result-val" style="color:var(--accent-blue)">${kelly}%</span></div>
    <div class="calc-result-row"><span class="calc-result-lbl">ارزش انتظار (EV)</span><span class="calc-result-val" style="color:${parseFloat(ev)>=0?'var(--accent-mint)':'var(--accent-peach)'}">${parseFloat(ev)>=0?'+':''}${ev}</span></div>
  `;
}

function openBrokerDetail(id){
  const b=BROKERS.find(x=>x.id===id);
  if(!b)return;
  document.getElementById('modal-overlay').classList.remove('hidden');
  const modal=document.getElementById('broker-detail-modal');
  modal.classList.remove('hidden');
  modal.innerHTML=`
    <div class="broker-dialog">
      <div class="broker-dialog-header">
        <div>
          <div style="font-size:16px;font-weight:900;color:var(--text1)">${b.faName}</div>
          <div style="font-size:12px;color:var(--primary);font-weight:600">${b.name}</div>
        </div>
        <div class="broker-dialog-payout">${b.payout}</div>
      </div>
      <div style="font-size:13px;color:var(--text2);margin-top:12px;line-height:1.6">${b.desc}</div>
      <div class="broker-dialog-row"><span class="lbl">پشتیبانی OTC ۲۴/۷:</span><span class="val" style="color:${b.online?'var(--accent-mint)':'var(--text3)'}">${b.online?'بله (۲۴/۷ فعال)':'خیر (ساعات اداری)'}</span></div>
      <div class="broker-dialog-row"><span class="lbl">سرعت اجرا:</span><span class="val">${b.speed}</span></div>
      <div class="broker-dialog-row"><span class="lbl">حداقل واریز:</span><span class="val" style="color:var(--accent-peach)">${b.min}</span></div>
      <div class="broker-dialog-row"><span class="lbl">وضعیت اتصال:</span><span class="val" style="color:var(--accent-cyan)">${b.status}</span></div>
      <div class="broker-reg">
        <div class="broker-reg-title">🛡️ وضعیت رگولاتوری (نظارت رسمی):</div>
        <div class="broker-reg-text">${b.reg}</div>
      </div>
      <button class="broker-dialog-btn" onclick="closeModal()">بستن مشخصات بروکر</button>
    </div>
  `;
}

function openFeedback(signalId){
  document.getElementById('modal-overlay').classList.remove('hidden');
  const modal=document.getElementById('feedback-modal');
  modal.classList.remove('hidden');
  modal.innerHTML=`
    <div class="modal-header">
      <div class="modal-title">گزارش سیگنال</div>
      <button class="modal-close" onclick="closeModal()">✕</button>
    </div>
    <div class="calc-label">نوع گزارش</div>
    <select class="calc-input" id="fbType">
      <option>سیگنال نادرست</option>
      <option>تاخیر در ارسال</option>
      <option>اطلاعات ناقص</option>
      <option>پیشنهاد بهبود</option>
    </select>
    <div class="calc-label">توضیحات</div>
    <textarea class="calc-input" rows="4" placeholder="توضیحات خود را بنویسید..." style="resize:vertical"></textarea>
    <button class="calc-btn" onclick="alert('گزارش شما ثبت شد');closeModal()">ارسال گزارش</button>
  `;
}

function openSupport(){
  alert('چت پشتیبانی: @IranBinarySupport');
}

function closeModal(){
  document.getElementById('modal-overlay').classList.add('hidden');
  document.getElementById('signal-detail-modal').classList.add('hidden');
  document.getElementById('risk-calc-modal').classList.add('hidden');
  document.getElementById('broker-detail-modal').classList.add('hidden');
  document.getElementById('feedback-modal').classList.add('hidden');
}

// ===== HELPERS =====
function toggleFav(id){
  const s=SIGNALS.find(x=>x.id===id);
  if(s)s.isFavorite=!s.isFavorite;
  renderSignals();
  if(curTab===0)renderDashboard();
}

function getBrokerBg(name){
  if(name.includes('Pocket'))return '#E8F1FD';
  if(name.includes('Quotex'))return '#FFECF3';
  if(name.includes('IQ'))return '#FFF0E7';
  if(name.includes('Olymp'))return '#E4F7FA';
  if(name.includes('Deriv'))return '#FFE5E5';
  return '#E3FAF4';
}
function getBrokerAccent(name){
  if(name.includes('Pocket'))return '#3B82F6';
  if(name.includes('Quotex'))return '#E83E8C';
  if(name.includes('IQ'))return '#FF7251';
  if(name.includes('Olymp'))return '#1CB4C8';
  if(name.includes('Deriv'))return '#EF4444';
  return '#1BBFA1';
}
