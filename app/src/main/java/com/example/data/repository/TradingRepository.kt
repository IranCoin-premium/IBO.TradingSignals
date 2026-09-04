package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.NewsEntity
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserSubscriptionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrokerItem(
    val id: String,
    val name: String,
    val faName: String,
    val payoutRate: String,
    val otc247: Boolean,
    val executionSpeed: String,
    val minDeposit: String,
    val status: String,
    val badge: String,
    val description: String
)

class TradingRepository(
    private val db: AppDatabase,
    private val context: Context
) {
    val offlineCacheManager = FirestoreOfflineCacheManager(db, context.applicationContext)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allSignals: Flow<List<SignalEntity>> = db.signalDao().getAllSignals()
    val historicalSignals: Flow<List<SignalEntity>> = db.signalDao().getHistoricalSignals()
    val activeSignals: Flow<List<SignalEntity>> = db.signalDao().getActiveSignals()
    val favoriteSignals: Flow<List<SignalEntity>> = db.signalDao().getFavoriteSignals()

    suspend fun toggleFavoriteSignal(signal: SignalEntity) {
        val newStatus = !signal.isFavorite
        db.signalDao().updateFavoriteStatus(signal.id, newStatus)
    }
    val wonCount: Flow<Int> = db.signalDao().getWonCountFlow()
    val lostCount: Flow<Int> = db.signalDao().getLostCountFlow()
    val vetoCount: Flow<Int> = db.signalDao().getVetoCountFlow()
    val allNews: Flow<List<NewsEntity>> = db.newsDao().getAllNews()
    val highImpactNews: Flow<List<NewsEntity>> = db.newsDao().getHighImpactNews()
    val allPlans: Flow<List<PlanEntity>> = db.planDao().getAllPlans()
    val allAdmins: Flow<List<UserEntity>> = db.userDao().getAllAdminsAndStaff()
    val allSubscriptions: Flow<List<UserSubscriptionEntity>> = db.userSubscriptionDao().getAllSubscriptions()
    val allFeedback: Flow<List<com.example.data.local.FeedbackEntity>> = db.feedbackDao().getAllFeedback()
    val feedbackCount: Flow<Int> = db.feedbackDao().getFeedbackCount()
    val allTradeLogs: Flow<List<com.example.data.local.TradeLogEntity>> = db.tradeLogDao().getAllTradeLogs()
    val offlineCacheStatus: StateFlow<OfflineCacheSyncStatus> = offlineCacheManager.syncStatus

    suspend fun submitFeedback(feedback: com.example.data.local.FeedbackEntity): Long {
        return db.feedbackDao().insertFeedback(feedback)
    }

    suspend fun deleteFeedback(id: Long) {
        db.feedbackDao().deleteFeedback(id)
    }

    // 15+ Binary Option Brokers & Exchanges
    val binaryBrokers: List<BrokerItem> = listOf(
        BrokerItem("pocket_option", "Pocket Option", "پوکت آپشن", "تا ۹۶٪", true, "سریع (زیر ۱ ثانیه)", "$5", "فعال و متصل", "پرطرفدارترین", "پشتیبانی کامل از بازارهای OTC و واریز ریالی و کریپتو"),
        BrokerItem("quotex", "Quotex", "کوتکس", "تا ۹۵٪", true, "فوری (Ultra-Fast)", "$10", "فعال و متصل", "محبوب معامله‌گران", "پلتفرم پیشرفته با ابزارهای تحلیل تکنیکال داخلی"),
        BrokerItem("iq_option", "IQ Option", "آی‌کیو آپشن", "تا ۹۵٪", false, "استاندارد", "$10", "فعال", "بین‌المللی", "قدیمی‌ترین و خوش‌نام‌ترین پلتفرم باینری در سطح جهان"),
        BrokerItem("deriv", "Deriv (Binary.com)", "دریو (باینری دات‌کام)", "تا ۹۸٪", true, "الگوریتمی", "$5", "فعال و متصل", "تنظیم‌شده و معتبر", "پیشرو در انواع قراردادهای Digit و Rise/Fall"),
        BrokerItem("olymp_trade", "Olymp Trade", "الیمپ ترید", "تا ۹۲٪", false, "سریع", "$10", "فعال", "ساده و روان", "معاملات زمان ثابت (FTT) با رابط کاربری کاربرپسند"),
        BrokerItem("nadex", "Nadex", "نیدکس آمریکا", "تا ۸۵٪", false, "بورس رسمی", "$0", "نظارت CFTC", "رسمی و قانونی", "بورس رسمی ارائه‌دهنده Event Contracts در ایالات متحده"),
        BrokerItem("expert_option", "ExpertOption", "اکسپرت آپشن", "تا ۹۰٪", true, "سریع", "$10", "فعال", "معاملات اجتماعی", "دارای اتاق گفتگو و قابلیت کپی تریدینگ اختصاصی"),
        BrokerItem("binomo", "Binomo", "بینومو", "تا ۸۹٪", true, "فوری", "$10", "فعال", "مسابقات روزانه", "پلتفرم معتبر با مسابقات ترید و بونوس‌های دوره‌ای"),
        BrokerItem("spectre_ai", "Spectre.ai", "اسپکتر دیفای", "تا ۹۰٪", true, "بلاکچینی", "$1", "غیرمتمرکز (DeFi)", "بدون بروکر واسط", "قراردادهای هوشمند بدون نیاز به واریز به حساب بروکر"),
        BrokerItem("race_option", "RaceOption", "ریس آپشن", "تا ۹۵٪", true, "برداشت ۱ ساعته", "$250", "پشتیبانی VIP", "برداشت سریع", "پشتیبانی زنده ویدیویی و بانس‌های خوش‌آمدگویی"),
        BrokerItem("close_option", "CloseOption", "کلوز آپشن", "تا ۸۵٪", true, "استاندارد", "$5", "سازگار با ایران", "پشتیبانی ریالی", "پذیرش کاربران ایرانی با احراز هویت آسان"),
        BrokerItem("videforex", "Videforex", "ویدفارکس", "تا ۹۵٪", true, "پخش زنده", "$250", "لایو ۲۴/۷", "معاملات همزمان", "ترکیب معاملات باینری آپشن و فارکس آنلاین"),
        BrokerItem("binary_cent", "BinaryCent", "باینری سنت", "تا ۹۵٪", true, "میکرو سنت", "$250", "حساب سنتی", "ریسک بسیار پایین", "امکان معامله از ۱۰ سنت در هر ترید"),
        BrokerItem("bullex", "Bullex", "بولکس", "تا ۹۲٪", true, "فوری", "$10", "فعال", "جدید و چابک", "پلتفرم بهینه‌سازی‌شده برای بازارهای پرنوسان OTC"),
        BrokerItem("cme_events", "CME Event Contracts", "قراردادهای بورس شیکاگو CME", "تا ۸۴٪", false, "تنظیم‌شده CFTC", "$100", "بورس نیویورک/شیکاگو", "ابزار نهادی", "قراردادهای باینری شاخص‌های S&P500 و طلا و نفت"),
        BrokerItem("dukascopy", "Dukascopy Binary", "دوکاسکوپی بانک سوئیس", "تا ۸۸٪", false, "حفاظت بانکی", "$1000", "نظارت FINMA سوئیس", "امنیت بانکی", "ارائه آپشن‌های باینری تحت نظارت عالی بانکی اروپا")
    )

    fun initializeSeedData(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            // Seed admin if not present
            val adminUser = db.userDao().getUserByEmail("admin@iranbinary.ir")
            if (adminUser == null) {
                db.userDao().insertUser(
                    UserEntity(
                        email = "admin@iranbinary.ir",
                        passwordHash = "IranBinaryAdmin2026!",
                        fullName = "مدیریت ارشد ایران باینری آپشن",
                        role = "ADMIN",
                        activePlan = "یک ساله",
                        planExpiryTimestamp = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000,
                        loginProvider = "MANUAL"
                    )
                )
            }

            // Seed 5 Plans if empty
            if (db.planDao().getCount() == 0) {
                db.planDao().insertAll(
                    listOf(
                        PlanEntity(
                            title = "اشتراک آزمایشی هفتگی",
                            durationText = "۱ هفته‌ای (۷ روز)",
                            durationDays = 7,
                            priceToman = "۳۹۰,۰۰۰ تومان",
                            priceUsdt = "7.5 USDT",
                            discountPercent = 0,
                            isPopular = false,
                            features = "دسترسی به تمام سیگنال‌های لایو OTC و فارکس,تایم‌فریم‌های ۱ و ۵ دقیقه,اعلان فوری در اپلیکیشن,پشتیبانی ۲۴ ساعته",
                            badge = "تست و ارزیابی"
                        ),
                        PlanEntity(
                            title = "اشتراک استاندارد ماهانه",
                            durationText = "۱ ماهه (۳۰ روز)",
                            durationDays = 30,
                            priceToman = "۱,۱۹۰,۰۰۰ تومان",
                            priceUsdt = "22.0 USDT",
                            discountPercent = 15,
                            isPopular = true,
                            features = "دسترسی نامحدود به سیگنال‌های باینری آپشن,پوشش ۱۵ بروکر اختصاصی,تحلیل چندلایه AI1 Strategist,فیلتر هوشمند NO TRADE,دسترسی به کانال تلگرام VIP",
                            badge = "محبوب‌ترین انتخاب"
                        ),
                        PlanEntity(
                            title = "اشتراک الیت سه ماهه",
                            durationText = "۳ ماهه (۹۰ روز)",
                            durationDays = 90,
                            priceToman = "۲,۸۹۰,۰۰۰ تومان",
                            priceUsdt = "55.0 USDT",
                            discountPercent = 25,
                            isPopular = false,
                            features = "تمامی امکانات پلن ماهانه,تخفیف ۲۵ درصدی فصلی,وبینارهای اختصاصی هفتگی مدیریت ریسک,ژورنال هوشمند ثبت معاملات,پشتیبانی تلفنی مستقیم",
                            badge = "ارزش اقتصادی بالا"
                        ),
                        PlanEntity(
                            title = "اشتراک نیم‌ساله VIP",
                            durationText = "۶ ماهه (۱۸۰ روز)",
                            durationDays = 180,
                            priceToman = "۴,۹۹۰,۰۰۰ تومان",
                            priceUsdt = "95.0 USDT",
                            discountPercent = 35,
                            isPopular = false,
                            features = "دسترسی ۶ ماهه به سیگنال‌های هوش مصنوعی,مشاوره اختصاصی تخصیص سرمایه (Position Sizing),تنظیم استراتژی شخصی بدون مارتینگل,اولویت پاسخگویی تیکت‌های VIP",
                            badge = "ویژه حرفه‌ای‌ها"
                        ),
                        PlanEntity(
                            title = "اشتراک سالانه آلتیمیت",
                            durationText = "۱ ساله (۳۶۵ روز)",
                            durationDays = 365,
                            priceToman = "۸,۴۹۰,۰۰۰ تومان",
                            priceUsdt = "160.0 USDT",
                            discountPercent = 50,
                            isPopular = false,
                            features = "دسترسی کامل ۳۶۵ روزه با ۵۰٪ تخفیف طلایی,کلید دسترسی مستقیم API سیگنال‌ها,پکیج کامل دوره آموزشی صفر تا صد باینری آپشن,ممیزی هفتگی ژورنال معاملات با AI3 Governor",
                            badge = "تخفیف استثنایی ۵۰٪"
                        )
                    )
                )
            }

            // Seed Signals if empty
            if (db.signalDao().getCount() == 0) {
                db.signalDao().insertAll(
                    listOf(
                        SignalEntity(
                            asset = "EUR/USD (OTC)",
                            category = "OTC",
                            direction = "CALL",
                            strikePrice = "1.08420",
                            currentPrice = "1.08425",
                            expiry = "1m",
                            payoutRate = "۹۳٪",
                            marketRegime = "روند صعودی پرقدرت (Bullish Trend)",
                            confidenceScore = 88,
                            riskScore = "کم ریسک (Low)",
                            vetoStatus = "تایید شده",
                            rationale = "شکست پول‌بک در حمایت معتبر M1 به همراه واگرایی مثبت استوکاستیک و افزایش حجم سفارش‌های خرید در بروکر Quotex و Pocket Option.",
                            recommendedBrokers = "Quotex, Pocket Option, Deriv",
                            status = "ACTIVE"
                        ),
                        SignalEntity(
                            asset = "BTC/USDT",
                            category = "CRYPTO",
                            direction = "PUT",
                            strikePrice = "64,320.00",
                            currentPrice = "64,310.50",
                            expiry = "5m",
                            payoutRate = "۸۹٪",
                            marketRegime = "برگشت از سقف رنج (Range Reversal)",
                            confidenceScore = 84,
                            riskScore = "متوسط (Medium)",
                            vetoStatus = "تایید شده",
                            rationale = "برخورد به باند بالایی بولینگر و اشباع خرید شدید در RSI(14) با تاییده کندل چکش معکوس در تایم‌فریم ۵ دقیقه‌ای.",
                            recommendedBrokers = "Pocket Option, IQ Option, Spectre.ai",
                            status = "ACTIVE"
                        ),
                        SignalEntity(
                            asset = "GBP/JPY (OTC)",
                            category = "OTC",
                            direction = "NO_TRADE",
                            strikePrice = "196.450",
                            currentPrice = "196.448",
                            expiry = "3m",
                            payoutRate = "۹۲٪",
                            marketRegime = "فشردگی نوسان بدون جهت (Compression)",
                            confidenceScore = 42,
                            riskScore = "بالا (High Risk)",
                            vetoStatus = "وتو شده توسط AI2 Risk Architect",
                            rationale = "طبق اصل No Trade پلتفرم ایران باینری آپشن: اسپرد مشکوک و نبود مومنتوم کافی در آستانه انتشار گزارش بانکی ژاپن.",
                            recommendedBrokers = "تمامی بروکرها متوقف شود",
                            status = "NO_TRADE"
                        ),
                        SignalEntity(
                            asset = "GOLD (XAU/USD)",
                            category = "COMMODITIES",
                            direction = "CALL",
                            strikePrice = "2,518.30",
                            currentPrice = "2,519.10",
                            expiry = "3m",
                            payoutRate = "۸۸٪",
                            marketRegime = "شکست صعودی مقاومت (Breakout)",
                            confidenceScore = 91,
                            riskScore = "کنترل‌شده (Controlled)",
                            vetoStatus = "تایید شده",
                            rationale = "تثبیت کندل ۳ دقیقه‌ای بالای ناحیه مقاومتی ۲۵۱۸ با حمایت متحرک EMA 20 و تایید شاخص ATR.",
                            recommendedBrokers = "Quotex, Nadex, ExpertOption",
                            status = "WON"
                        ),
                        SignalEntity(
                            asset = "USD/CAD",
                            category = "FOREX",
                            direction = "PUT",
                            strikePrice = "1.34890",
                            currentPrice = "1.34860",
                            expiry = "1m",
                            payoutRate = "۹۴٪",
                            marketRegime = "مومنتوم نزولی شتاب‌دار (Momentum Expansion)",
                            confidenceScore = 86,
                            riskScore = "کم ریسک (Low)",
                            vetoStatus = "تایید شده",
                            rationale = "شکست کف ماژور همراه با عدم تعادل سفارشات فروش نهادی و افزایش نرخ دلار کانادا.",
                            recommendedBrokers = "Pocket Option, Deriv, Olymp Trade",
                            status = "WON",
                            timestamp = System.currentTimeMillis() - 25 * 60 * 1000L
                        ),
                        SignalEntity(
                            asset = "AUD/USD (OTC)",
                            category = "OTC",
                            direction = "CALL",
                            strikePrice = "0.66240",
                            currentPrice = "0.66258",
                            expiry = "1m",
                            payoutRate = "۹۵٪",
                            marketRegime = "واگرایی صعودی RSI در کف",
                            confidenceScore = 90,
                            riskScore = "کم ریسک",
                            vetoStatus = "تایید شده",
                            rationale = "برخورد به حمایت تکنیکال در کف روزانه بروکر پاکت آپشن با تایید پرایس اکشن پین‌بار صعودی.",
                            recommendedBrokers = "Pocket Option, Quotex",
                            status = "WON",
                            timestamp = System.currentTimeMillis() - 75 * 60 * 1000L
                        ),
                        SignalEntity(
                            asset = "ETH/USDT",
                            category = "CRYPTO",
                            direction = "CALL",
                            strikePrice = "3,480.20",
                            currentPrice = "3,481.90",
                            expiry = "5m",
                            payoutRate = "۸۷٪",
                            marketRegime = "شکست پرقدرت مقاومت M5",
                            confidenceScore = 85,
                            riskScore = "متوسط",
                            vetoStatus = "تایید شده",
                            rationale = "کندل ماروبوزو سبز روی میانگین متحرک نمایی ۲۰ روزه همراه با افزایش حجم قراردادهای دریو.",
                            recommendedBrokers = "Deriv, Spectre.ai, Pocket Option",
                            status = "WON",
                            timestamp = System.currentTimeMillis() - 140 * 60 * 1000L
                        ),
                        SignalEntity(
                            asset = "EUR/GBP",
                            category = "FOREX",
                            direction = "PUT",
                            strikePrice = "0.85430",
                            currentPrice = "0.85445",
                            expiry = "1m",
                            payoutRate = "۹۰٪",
                            marketRegime = "فشار فروش مقاومت ماژور",
                            confidenceScore = 79,
                            riskScore = "متوسط",
                            vetoStatus = "تایید شده",
                            rationale = "برخورد به سقف کانال رنج و تشکیل سایه بالایی بلند در کندل قبلی؛ اما نوسان اسپرد باعث بسته شدن با اختلاف ناچیز شد.",
                            recommendedBrokers = "Quotex, Olymp Trade",
                            status = "LOST",
                            timestamp = System.currentTimeMillis() - 210 * 60 * 1000L
                        ),
                        SignalEntity(
                            asset = "US CRUDE OIL (نفت WTI)",
                            category = "COMMODITIES",
                            direction = "CALL",
                            strikePrice = "74.85",
                            currentPrice = "75.12",
                            expiry = "5m",
                            payoutRate = "۸۶٪",
                            marketRegime = "روند شتاب‌دار صعودی سشن لندن",
                            confidenceScore = 93,
                            riskScore = "کم ریسک",
                            vetoStatus = "تایید شده",
                            rationale = "جهش تقاضا پس از اعلام گزارش ذخایر انرژی با تشکیل الگوی ادامه‌دهنده پرچم در M5.",
                            recommendedBrokers = "Nadex, Quotex, ExpertOption",
                            status = "WON",
                            timestamp = System.currentTimeMillis() - 320 * 60 * 1000L
                        ),
                        SignalEntity(
                            asset = "USD/JPY",
                            category = "FOREX",
                            direction = "NO_TRADE",
                            strikePrice = "154.200",
                            currentPrice = "154.180",
                            expiry = "1m",
                            payoutRate = "۹۱٪",
                            marketRegime = "نوسانات شدید در آستانه سخنرانی BoJ",
                            confidenceScore = 38,
                            riskScore = "فوق‌العاده پرریسک",
                            vetoStatus = "وتو شده قطعی",
                            rationale = "عدم تعادل و احتمال مداخله ارزی بانک مرکزی ژاپن، دستور توقف ترید برای جلوگیری از ضرر صادر شد.",
                            recommendedBrokers = "هیچ بروکری مجاز نیست",
                            status = "NO_TRADE",
                            timestamp = System.currentTimeMillis() - 440 * 60 * 1000L
                        )
                    )
                )
            }

            // Seed News if empty
            if (db.newsDao().getCount() == 0) {
                db.newsDao().insertAll(
                    listOf(
                        NewsEntity(
                            title = "درخواست رسمی Cboe و Nasdaq به SEC در سال ۲۰۲۶ برای بازگشت باینری آپشن‌های نظارت‌شده",
                            category = "OTC",
                            summary = "بورس Cboe برای راه‌اندازی باینری آپشن‌های KPI و نزدک برای قراردادهای Outcome Related با ضمانت اتاق پایاپای به SEC درخواست دادند.",
                            fullContent = "در سال ۲۰۲۶، صرافی‌های بزرگ جهانی مانند Cboe Global Markets و Nasdaq گام‌های بلندی برای ارائه باینری آپشن‌های شفاف و رگوله‌شده برداشته‌اند. این قراردادها با ریسک و پاداش ثابت (Fixed-Risk/Fixed-Reward) و نظارت درلحظه، فرصت‌های قانونی جدیدی را برای تریدرهای مشتقات کوتاه‌مدت ایجاد می‌کنند.",
                            source = "Cboe Global Markets / Nasdaq SEC Filings",
                            impact = "HIGH",
                            sentiment = "صعودی (Bullish)",
                            timeAgo = "۱۰ دقیقه پیش",
                            timestamp = System.currentTimeMillis() - 10 * 60 * 1000L
                        ),
                        NewsEntity(
                            title = "پیش‌بینی گلدمن ساکس برای جهش طلا تا ۴,۹۰۰ دلار در سال ۲۰۲۶ با تقاضای رکورد ۳۸۰ میلیارد دلاری",
                            category = "COMMODITIES",
                            summary = "گلدمن ساکس هدف انس طلا را تا ۴,۹۰۰ دلار ارتقا داد؛ تقاضای خرید بانک‌های مرکزی رکورد تاریخی ۳۸۰ میلیارد دلار را در نیمه نخست ثبت کرد.",
                            fullContent = "تحلیلگران پژوهشی گلدمن ساکس اعلام کردند که روند چندساله خرید شمش توسط بانک‌های مرکزی و تثبیت نرخ بهره، تقاضای قدرتمندی برای طلا ایجاد کرده است. در معاملات کوتاه‌مدت باینری آپشن، طلا (XAU/USD) دارای مومنتوم بالای صعودی در سشن‌های نیویورک و لندن ارزیابی می‌شود.",
                            source = "Goldman Sachs Research & Kitco",
                            impact = "HIGH",
                            sentiment = "صعودی (Bullish)",
                            timeAgo = "۳۰ دقیقه پیش",
                            timestamp = System.currentTimeMillis() - 30 * 60 * 1000L
                        ),
                        NewsEntity(
                            title = "جهش بازار رمزارزها در سپتامبر ۲۰۲۶: بیت‌کوین در مرز ۸۱,۰۰۰ دلار و ارزش کل بازار ۲.۸۲ تریلیون دلار",
                            category = "CRYPTO",
                            summary = "ورود مجدد نقدینگی نهادی و توکنیزاسیون دارایی‌ها ارزش بازار کریپتو را بالا برد؛ اتریوم در آستانه جهش به سمت اهداف ۱۰ هزار دلاری.",
                            fullContent = "بازار ارزهای دیجیتال با عبور ارزش کل از ۲.۸۲ تریلیون دلار و تثبیت بیت‌کوین در کانال ۸۰ تا ۸۱ هزار دلار، نوسانات فوق‌العاده‌ای برای قراردادهای ۵ و ۱۵ دقیقه‌ای باینری آپشن در رمزارزهای BTC, ETH و SOL فراهم کرده است.",
                            source = "بلومبرگ کریپتو و The Block",
                            impact = "HIGH",
                            sentiment = "صعودی (Bullish)",
                            timeAgo = "۴۵ دقیقه پیش",
                            timestamp = System.currentTimeMillis() - 45 * 60 * 1000L
                        ),
                        NewsEntity(
                            title = "پیش‌بینی قدرت‌گیری ین ژاپن و چشم‌انداز کاهش نرخ بهره فدرال رزرو بر جفت‌ارزهای فارکس",
                            category = "FOREX",
                            summary = "انتظارات کاهش نرخ بهره توسط فدرال رزرو و تغییر سیاست‌های بانک مرکزی ژاپن (BoJ) باعث نوسانات پرقدرت در USD/JPY و EUR/USD شده است.",
                            fullContent = "گزارش‌های اقتصادی حاکی از واگرایی در سیاست‌های بانک‌های مرکزی است. دلار آمریکا در برابر ین با فشار اصلاحی مواجه بوده در حالی که یورو نوسانات پایداری را ثبت می‌کند. تریدرهای باینری آپشن باید در زمان انتشار داده‌های تورم و اشتغال آمریکا فیلتر No-Trade را رعایت نمایند.",
                            source = "رویترز فارکس و Convera",
                            impact = "HIGH",
                            sentiment = "خنثی (Neutral)",
                            timeAgo = "۱ ساعت پیش",
                            timestamp = System.currentTimeMillis() - 60 * 60 * 1000L
                        ),
                        NewsEntity(
                            title = "تاب‌آوری اقتصاد جهانی و افزایش کسری تجاری آمریکا به ۸۸.۶ میلیارد دلار در سال ۲۰۲۶",
                            category = "MACRO",
                            summary = "داده‌های وزارت بازرگانی آمریکا رشد پایدار تولید ناخالص داخلی همراه با افزایش کسری تجاری را نشان می‌دهد که بر تایم‌فریم‌های شاخص‌ها اثرگذار است.",
                            fullContent = "گزارش‌های کلان حاکی از تاب‌آوری اقتصاد آمریکا با وجود افزایش کسری تجاری کالا و خدمات به ۸۸.۶ میلیارد دلار است. در حوزه قراردادهای باینری آپشن، شاخص‌های سهام S&P 500 و Nasdaq 100 در وضعیت رنج نوسانی قرار گرفته‌اند.",
                            source = "US Bureau of Economic Analysis (BEA)",
                            impact = "MEDIUM",
                            sentiment = "خنثی (Neutral)",
                            timeAgo = "۲ ساعت پیش",
                            timestamp = System.currentTimeMillis() - 120 * 60 * 1000L
                        ),
                        NewsEntity(
                            title = "بررسی عملکرد الگوریتم‌های OTC در تعطیلات آخر هفته بروکرها",
                            category = "OTC",
                            summary = "تحلیلگران نوسانات اسپرد و نحوه تولید قیمت در سرورهای بروکر کوتکس و پوکت آپشن را در ساعات OTC بررسی کردند.",
                            fullContent = "بازارهای OTC یا Over The Counter در باینری آپشن بر اساس الگوریتم‌های داخلی و فیدهای تجمیعی قیمت‌گذاری می‌شوند. در پلتفرم ایران باینری آپشن، سیستم هوش مصنوعی ما به صورت ۲۴ ساعته جریان قیمت را از طریق مقایسه میکروثانیه‌ای ممیزی می‌کند تا از ورود در شرایط با نوسان غیرطبیعی جلوگیری کند.",
                            source = "پایگاه ممیزی OTC ایران باینری",
                            impact = "MEDIUM",
                            sentiment = "صعودی (Bullish)",
                            timeAgo = "۳ ساعت پیش",
                            timestamp = System.currentTimeMillis() - 180 * 60 * 1000L
                        )
                    )
                )
            }

            // Seed initial User Subscriptions for offline cache demonstration if empty
            if (db.userSubscriptionDao().getCount() == 0) {
                db.userSubscriptionDao().insertAll(
                    listOf(
                        UserSubscriptionEntity(
                            id = "sub_seed_admin",
                            userId = "1",
                            userEmail = "admin@iranbinary.ir",
                            planTitle = "یک ساله سازمانی VIP",
                            durationDays = 365,
                            priceToman = "۱۶,۵۰۰,۰۰۰ تومان",
                            priceUsdt = "$349",
                            status = "ACTIVE",
                            startDate = System.currentTimeMillis() - 7L * 86400000L,
                            expiryDate = System.currentTimeMillis() + 358L * 86400000L,
                            paymentMethod = "CRYPTO_USDT",
                            transactionRef = "TRC20-IB-992147",
                            isCachedLocally = true
                        ),
                        UserSubscriptionEntity(
                            id = "sub_seed_demo",
                            userId = "2",
                            userEmail = "trader@iranbinary.ir",
                            planTitle = "سه ماهه طلایی",
                            durationDays = 90,
                            priceToman = "۵,۸۵۰,۰۰۰ تومان",
                            priceUsdt = "$119",
                            status = "ACTIVE",
                            startDate = System.currentTimeMillis() - 2L * 86400000L,
                            expiryDate = System.currentTimeMillis() + 88L * 86400000L,
                            paymentMethod = "TOMAN_CARD",
                            transactionRef = "SHETAB-84210",
                            isCachedLocally = true
                        )
                    )
                )
            }

            // Seed initial Trade Journal Logs if empty
            if (db.tradeLogDao().getCount() == 0) {
                db.tradeLogDao().insertAll(
                    listOf(
                        com.example.data.local.TradeLogEntity(
                            asset = "EUR/USD (OTC)",
                            direction = "CALL",
                            result = "WIN",
                            tradeAmount = 50.0,
                            payoutPercent = 92,
                            profitOrLoss = 46.0,
                            broker = "Pocket Option",
                            entryPrice = "1.08450",
                            exitPrice = "1.08480",
                            expiry = "1m",
                            strategy = "شکست سطح و پولبک",
                            notes = "ورود دقیق پس از شکست مقاومت محلی در تایم‌فریم ۱ دقیقه و تایید کندل پین‌بار صعودی.",
                            emotionalState = "منضبط و آرام",
                            timestamp = System.currentTimeMillis() - 25 * 60 * 1000L
                        ),
                        com.example.data.local.TradeLogEntity(
                            asset = "USD/CAD",
                            direction = "PUT",
                            result = "WIN",
                            tradeAmount = 40.0,
                            payoutPercent = 89,
                            profitOrLoss = 35.6,
                            broker = "Quotex",
                            entryPrice = "1.34890",
                            exitPrice = "1.34860",
                            expiry = "1m",
                            strategy = "سیگنال هوش مصنوعی پلتفرم",
                            notes = "همگام با سیگنال باینری هوش مصنوعی با شاخص اطمینان ۸۶٪.",
                            emotionalState = "منضبط و آرام",
                            timestamp = System.currentTimeMillis() - 95 * 60 * 1000L
                        ),
                        com.example.data.local.TradeLogEntity(
                            asset = "GOLD (XAU/USD)",
                            direction = "CALL",
                            result = "WIN",
                            tradeAmount = 100.0,
                            payoutPercent = 88,
                            profitOrLoss = 88.0,
                            broker = "Quotex",
                            entryPrice = "2,680.10",
                            exitPrice = "2,682.40",
                            expiry = "5m",
                            strategy = "واگرایی RSI و تقاضای سشن",
                            notes = "ورود عالی روی واگرایی مثبت کف در M5 همزمان با افزایش تقاضای شمش طلا.",
                            emotionalState = "منضبط و آرام",
                            timestamp = System.currentTimeMillis() - 180 * 60 * 1000L
                        ),
                        com.example.data.local.TradeLogEntity(
                            asset = "EUR/GBP",
                            direction = "PUT",
                            result = "LOSS",
                            tradeAmount = 30.0,
                            payoutPercent = 90,
                            profitOrLoss = -30.0,
                            broker = "Deriv",
                            entryPrice = "0.85430",
                            exitPrice = "0.85445",
                            expiry = "1m",
                            strategy = "برخورد به سقف کانال",
                            notes = "اسپرد بروکر کمی افزایش یافت و در ثانیه پایانی با اختلاف ۱ پیپ ضرر شد. رعایت حد ضرر روزانه و خروج.",
                            emotionalState = "منضبط و آرام",
                            timestamp = System.currentTimeMillis() - 320 * 60 * 1000L
                        ),
                        com.example.data.local.TradeLogEntity(
                            asset = "BTC/USDT",
                            direction = "CALL",
                            result = "WIN",
                            tradeAmount = 60.0,
                            payoutPercent = 86,
                            profitOrLoss = 51.6,
                            broker = "Pocket Option",
                            entryPrice = "80,450",
                            exitPrice = "80,590",
                            expiry = "3m",
                            strategy = "حمایت و مقاومت",
                            notes = "تثبیت بالای سطح روانی ۸۰ هزار دلار با حجم کندل ماروبوزو سبز.",
                            emotionalState = "منضبط و آرام",
                            timestamp = System.currentTimeMillis() - 500 * 60 * 1000L
                        )
                    )
                )
            }

            // Start Firestore background real-time sync with Room offline cache layer
            offlineCacheManager.startRealtimeSync(scope)
        }
    }

    suspend fun authenticateUser(email: String, pass: String): UserEntity? {
        val cleanEmail = email.trim().lowercase()
        // Support either exact email or 'admin'
        val user = if (cleanEmail == "admin") {
            db.userDao().getUserByEmail("admin@iranbinary.ir")
        } else {
            db.userDao().getUserByEmail(cleanEmail)
        }

        return if (user != null && user.passwordHash == pass) {
            _currentUser.value = user
            user
        } else {
            null
        }
    }

    suspend fun registerUser(email: String, pass: String, name: String): UserEntity {
        val cleanEmail = email.trim().lowercase()
        val existing = db.userDao().getUserByEmail(cleanEmail)
        if (existing != null) return existing

        val newUser = UserEntity(
            email = cleanEmail,
            passwordHash = pass,
            fullName = name,
            role = "USER",
            activePlan = "رایگان",
            loginProvider = "MANUAL"
        )
        val id = db.userDao().insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created
        return created
    }

    fun quickSocialLogin(provider: String, name: String, email: String, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val cleanEmail = email.trim().lowercase()
            var user = db.userDao().getUserByEmail(cleanEmail)
            if (user == null) {
                val newUser = UserEntity(
                    email = cleanEmail,
                    passwordHash = "SOCIAL_OAUTH",
                    fullName = name,
                    role = "USER",
                    activePlan = "یک هفته‌ای (هدیه ورود)",
                    planExpiryTimestamp = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000,
                    loginProvider = provider
                )
                val id = db.userDao().insertUser(newUser)
                user = newUser.copy(id = id)
            }
            _currentUser.value = user
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun updatePassword(userId: Long, newPass: String): Boolean {
        val user = _currentUser.value ?: return false
        val updated = user.copy(passwordHash = newPass)
        db.userDao().updateUser(updated)
        _currentUser.value = updated
        return true
    }

    suspend fun addNewStaff(email: String, pass: String, name: String, role: String): Boolean {
        val newUser = UserEntity(
            email = email.trim().lowercase(),
            passwordHash = pass,
            fullName = name,
            role = role,
            activePlan = "یک ساله سازمانی",
            loginProvider = "MANUAL"
        )
        db.userDao().insertUser(newUser)
        return true
    }

    suspend fun addSignal(signal: SignalEntity): Long {
        return offlineCacheManager.cacheAndUploadSignal(signal)
    }

    suspend fun updateSignal(signal: SignalEntity) {
        offlineCacheManager.cacheAndUploadSignal(signal)
    }

    suspend fun deleteSignal(id: Long) {
        offlineCacheManager.deleteCachedSignal(id)
    }

    suspend fun clearHistoricalSignals() {
        db.signalDao().clearHistory()
    }

    fun searchHistoricalSignals(query: String): Flow<List<SignalEntity>> {
        return db.signalDao().searchHistoricalSignals(query)
    }

    suspend fun addNews(news: NewsEntity): Long {
        return offlineCacheManager.cacheAndUploadNews(news)
    }

    suspend fun refreshLatestFinancialNews(): Int {
        val freshItems = listOf(
            NewsEntity(
                title = "درخواست رسمی Cboe و Nasdaq به SEC در سال ۲۰۲۶ برای بازگشت باینری آپشن‌های نظارت‌شده",
                category = "OTC",
                summary = "بورس Cboe برای راه‌اندازی باینری آپشن‌های KPI و نزدک برای قراردادهای Outcome Related با ضمانت اتاق پایاپای به SEC درخواست دادند.",
                fullContent = "در سال ۲۰۲۶، صرافی‌های بزرگ جهانی مانند Cboe Global Markets و Nasdaq گام‌های بلندی برای ارائه باینری آپشن‌های شفاف و رگوله‌شده برداشته‌اند. این قراردادها با ریسک و پاداش ثابت (Fixed-Risk/Fixed-Reward) و نظارت درلحظه، فرصت‌های قانونی جدیدی را برای تریدرهای مشتقات کوتاه‌مدت ایجاد می‌کنند.",
                source = "Cboe Global Markets / Nasdaq SEC Filings",
                impact = "HIGH",
                sentiment = "صعودی (Bullish)",
                timeAgo = "۱۰ دقیقه پیش",
                timestamp = System.currentTimeMillis() - 10 * 60 * 1000L
            ),
            NewsEntity(
                title = "پیش‌بینی گلدمن ساکس برای جهش طلا تا ۴,۹۰۰ دلار در سال ۲۰۲۶ با تقاضای رکورد ۳۸۰ میلیارد دلاری",
                category = "COMMODITIES",
                summary = "گلدمن ساکس هدف انس طلا را تا ۴,۹۰۰ دلار ارتقا داد؛ تقاضای خرید بانک‌های مرکزی رکورد تاریخی ۳۸۰ میلیارد دلار را در نیمه نخست ثبت کرد.",
                fullContent = "تحلیلگران پژوهشی گلدمن ساکس اعلام کردند که روند چندساله خرید شمش توسط بانک‌های مرکزی و تثبیت نرخ بهره، تقاضای قدرتمندی برای طلا ایجاد کرده است. در معاملات کوتاه‌مدت باینری آپشن، طلا (XAU/USD) دارای مومنتوم بالای صعودی در سشن‌های نیویورک و لندن ارزیابی می‌شود.",
                source = "Goldman Sachs Research & Kitco",
                impact = "HIGH",
                sentiment = "صعودی (Bullish)",
                timeAgo = "۳۰ دقیقه پیش",
                timestamp = System.currentTimeMillis() - 30 * 60 * 1000L
            ),
            NewsEntity(
                title = "جهش بازار رمزارزها در سپتامبر ۲۰۲۶: بیت‌کوین در مرز ۸۱,۰۰۰ دلار و ارزش کل بازار ۲.۸۲ تریلیون دلار",
                category = "CRYPTO",
                summary = "ورود مجدد نقدینگی نهادی و توکنیزاسیون دارایی‌ها ارزش بازار کریپتو را بالا برد؛ اتریوم در آستانه جهش به سمت اهداف ۱۰ هزار دلاری.",
                fullContent = "بازار ارزهای دیجیتال با عبور ارزش کل از ۲.۸۲ تریلیون دلار و تثبیت بیت‌کوین در کانال ۸۰ تا ۸۱ هزار دلار، نوسانات فوق‌العاده‌ای برای قراردادهای ۵ و ۱۵ دقیقه‌ای باینری آپشن در رمزارزهای BTC, ETH و SOL فراهم کرده است.",
                source = "بلومبرگ کریپتو و The Block",
                impact = "HIGH",
                sentiment = "صعودی (Bullish)",
                timeAgo = "۴۵ دقیقه پیش",
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000L
            ),
            NewsEntity(
                title = "پیش‌بینی قدرت‌گیری ین ژاپن و چشم‌انداز کاهش نرخ بهره فدرال رزرو بر جفت‌ارزهای فارکس",
                category = "FOREX",
                summary = "انتظارات کاهش نرخ بهره توسط فدرال رزرو و تغییر سیاست‌های بانک مرکزی ژاپن (BoJ) باعث نوسانات پرقدرت در USD/JPY و EUR/USD شده است.",
                fullContent = "گزارش‌های اقتصادی حاکی از واگرایی در سیاست‌های بانک‌های مرکزی است. دلار آمریکا در برابر ین با فشار اصلاحی مواجه بوده در حالی که یورو نوسانات پایداری را ثبت می‌کند. تریدرهای باینری آپشن باید در زمان انتشار داده‌های تورم و اشتغال آمریکا فیلتر No-Trade را رعایت نمایند.",
                source = "رویترز فارکس و Convera",
                impact = "HIGH",
                sentiment = "خنثی (Neutral)",
                timeAgo = "۱ ساعت پیش",
                timestamp = System.currentTimeMillis() - 60 * 60 * 1000L
            ),
            NewsEntity(
                title = "تاب‌آوری اقتصاد جهانی و افزایش کسری تجاری آمریکا به ۸۸.۶ میلیارد دلار در سال ۲۰۲۶",
                category = "MACRO",
                summary = "داده‌های وزارت بازرگانی آمریکا رشد پایدار تولید ناخالص داخلی همراه با افزایش کسری تجاری را نشان می‌دهد که بر تایم‌فریم‌های شاخص‌ها اثرگذار است.",
                fullContent = "گزارش‌های کلان حاکی از تاب‌آوری اقتصاد آمریکا با وجود افزایش کسری تجاری کالا و خدمات به ۸۸.۶ میلیارد دلار است. در حوزه قراردادهای باینری آپشن، شاخص‌های سهام S&P 500 و Nasdaq 100 در وضعیت رنج نوسانی قرار گرفته‌اند.",
                source = "US Bureau of Economic Analysis (BEA)",
                impact = "MEDIUM",
                sentiment = "خنثی (Neutral)",
                timeAgo = "۲ ساعت پیش",
                timestamp = System.currentTimeMillis() - 120 * 60 * 1000L
            ),
            NewsEntity(
                title = "بررسی عملکرد الگوریتم‌های OTC در تعطیلات آخر هفته بروکرها",
                category = "OTC",
                summary = "تحلیلگران نوسانات اسپرد و نحوه تولید قیمت در سرورهای بروکر کوتکس و پوکت آپشن را در ساعات OTC بررسی کردند.",
                fullContent = "بازارهای OTC یا Over The Counter در باینری آپشن بر اساس الگوریتم‌های داخلی و فیدهای تجمیعی قیمت‌گذاری می‌شوند. در پلتفرم ایران باینری آپشن، سیستم هوش مصنوعی ما به صورت ۲۴ ساعته جریان قیمت را از طریق مقایسه میکروثانیه‌ای ممیزی می‌کند تا از ورود در شرایط با نوسان غیرطبیعی جلوگیری کند.",
                source = "پایگاه ممیزی OTC ایران باینری",
                impact = "MEDIUM",
                sentiment = "صعودی (Bullish)",
                timeAgo = "۳ ساعت پیش",
                timestamp = System.currentTimeMillis() - 180 * 60 * 1000L
            )
        )
        for (item in freshItems) {
            offlineCacheManager.cacheAndUploadNews(item)
        }
        return freshItems.size
    }

    suspend fun deleteNews(id: Long) {
        offlineCacheManager.deleteCachedNews(id)
    }

    suspend fun addTradeLog(trade: com.example.data.local.TradeLogEntity): Long {
        return db.tradeLogDao().insertTradeLog(trade)
    }

    suspend fun updateTradeLog(trade: com.example.data.local.TradeLogEntity) {
        db.tradeLogDao().updateTradeLog(trade)
    }

    suspend fun deleteTradeLog(id: Long) {
        db.tradeLogDao().deleteTradeLogById(id)
    }

    suspend fun clearAllTradeLogs() {
        db.tradeLogDao().clearAllTradeLogs()
    }

    suspend fun updatePlan(plan: PlanEntity) {
        db.planDao().updatePlan(plan)
    }

    suspend fun upgradeUserPlan(planTitle: String, days: Int) {
        val user = _currentUser.value ?: return
        val expiry = System.currentTimeMillis() + days.toLong() * 24 * 60 * 60 * 1000
        val updated = user.copy(activePlan = planTitle, planExpiryTimestamp = expiry)
        db.userDao().updateUser(updated)
        _currentUser.value = updated

        // Record in offline cache layer & sync to Firestore
        val sub = UserSubscriptionEntity(
            id = "sub_${user.id}_${System.currentTimeMillis()}",
            userId = user.id.toString(),
            userEmail = user.email,
            planTitle = planTitle,
            durationDays = days,
            priceToman = "",
            priceUsdt = "",
            status = "ACTIVE",
            startDate = System.currentTimeMillis(),
            expiryDate = expiry,
            paymentMethod = "PROMO_UPGRADE",
            transactionRef = "INTERNAL-${System.currentTimeMillis() % 100000}",
            isCachedLocally = true
        )
        offlineCacheManager.cacheAndUploadUserSubscription(sub)
    }

    suspend fun recordUserSubscription(
        planTitle: String,
        durationDays: Int,
        priceToman: String,
        priceUsdt: String,
        paymentMethod: String = "CRYPTO_USDT",
        transactionRef: String = ""
    ): UserSubscriptionEntity? {
        val user = _currentUser.value ?: return null
        val expiry = System.currentTimeMillis() + durationDays.toLong() * 24 * 60 * 60 * 1000
        val sub = UserSubscriptionEntity(
            id = "sub_${user.id}_${System.currentTimeMillis()}",
            userId = user.id.toString(),
            userEmail = user.email,
            planTitle = planTitle,
            durationDays = durationDays,
            priceToman = priceToman,
            priceUsdt = priceUsdt,
            status = "ACTIVE",
            startDate = System.currentTimeMillis(),
            expiryDate = expiry,
            paymentMethod = paymentMethod,
            transactionRef = transactionRef,
            isCachedLocally = true
        )
        offlineCacheManager.cacheAndUploadUserSubscription(sub)

        val updated = user.copy(activePlan = planTitle, planExpiryTimestamp = expiry)
        db.userDao().updateUser(updated)
        _currentUser.value = updated
        return sub
    }

    suspend fun syncOfflineCacheWithCloud() {
        offlineCacheManager.syncAllFromCloud()
    }

    suspend fun updateUser(user: UserEntity) {
        db.userDao().updateUser(user)
    }

    suspend fun registerOrLoginSocial(provider: String, name: String, email: String): UserEntity {
        val cleanEmail = email.trim().lowercase()
        var user = db.userDao().getUserByEmail(cleanEmail)
        if (user == null) {
            val newUser = UserEntity(
                email = cleanEmail,
                passwordHash = "SOCIAL_OAUTH",
                fullName = name,
                role = "USER",
                activePlan = "یک هفته‌ای (هدیه ورود)",
                planExpiryTimestamp = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000,
                loginProvider = provider
            )
            val id = db.userDao().insertUser(newUser)
            user = newUser.copy(id = id)
        }
        _currentUser.value = user
        return user
    }

    // AI Assistant helper for Editor / Staff in Admin Panel
    fun runAiEditorAgent(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "تحویل" in lower || "delivery" in lower || "ارسال" in lower -> {
                "🤖 دستیار هوش مصنوعی تحویل سیگنال (Signal Delivery Engine):\n" +
                "• وضعیت مانیتورینگ شبکه تحویل: فعال و پرسرعت (Ultra-Low Latency)\n" +
                "• کانال‌های فعال: اعلان پوش فوری درون‌برنامه‌ای، پیام‌رسان تلگرام VIP، داشبورد وب‌سوکت\n" +
                "• بهینه‌سازی تحویل: سیگنال‌های انقضای ۱ دقیقه‌ای با میانگین تاخیر ارسال زیر ۲۵۰ میلی‌ثانیه به کاربران پلن‌های ۳ ماهه، ۶ ماهه و ۱ ساله توزیع می‌گردد.\n" +
                "• توزیع بر اساس پینگ بروکرها: کاربران Quotex و Pocket Option در اولویت صف وب‌سوکت قرار دارند.\n" +
                "• پیشنهاد سیگنال تحویل فوری: برای انتقال مستقیم به فرم انتشار، دکمه «بارگذاری در فرم سیگنال» را لمس فرمایید."
            }
            "پلن" in lower || "اشتراک" in lower || "tier" in lower || "subscription" in lower || "قیمت" in lower || "تخفیف" in lower -> {
                "🤖 دستیار هوش مصنوعی اقتصاد اشتراک‌ها (Subscription Tiers Architect):\n" +
                "• ممیزی ۵ لایه اشتراک فعال پلتفرم:\n" +
                "  ۱. یک هفته‌ای: ۸۵۰,۰۰۰ تومان (۱۹$) - مناسب تست و اثبات بازدهی\n" +
                "  ۲. یک ماهه: ۲,۴۵۰,۰۰۰ تومان (۴۹$) - تخفیف ۱۰٪ (پلن استاندارد)\n" +
                "  ۳. سه ماهه: ۵,۸۵۰,۰۰۰ تومان (۱۱۹$) - تخفیف ۲۰٪ (محبوب‌ترین و بهینه‌ترین LTV)\n" +
                "  ۴. شش ماهه: ۹,۹۰۰,۰۰۰ تومان (۱۹۹$) - تخفیف ۳۵٪ (معامله‌گران نیمه‌حرفه‌ای)\n" +
                "  ۵. یک ساله سازمانی: ۱۶,۵۰۰,۰۰۰ تومان (۳۴۹$) - تخفیف ۵۰٪ (VIP نامحدود)\n" +
                "• تحلیل ارزش امیدریاضی کاربر (EV): با وین‌ریت میانگین ۷۸٪ و سود ۸۵٪ در هر معامله، هزینه پلن ۳ ماهه با ۱۰ معامله موفق پوشش داده می‌شود.\n" +
                "• توصیه بهینه‌سازی: اعمال کمپین تخفیف ویژه عیدانه ۳۰٪ روی پلن ۳ ماهه برای رشد ۴۲٪ نرخ ارتقای کاربران رایگان."
            }
            "سیگنال" in lower || "signal" in lower -> {
                "🤖 دستیار هوش مصنوعی ایران باینری:\nسیگنال جدید پیشنهادی بر اساس شرایط فعلی بازار:\n• دارایی: EUR/USD (OTC)\n• جهت: CALL (خرید / بالا)\n• زمان انقضا: 1 دقیقه\n• قیمت استرایک: 1.08510\n• رژیم بازار: شکست صعودی تثبیت‌شده\n• درصد اعتماد: 90%\n• رتبه ریسک: کم\n• بروکرهای بهینه: Quotex, Pocket Option\n• تحلیل: افزایش مومنتوم خرید پس از تست موفقیت‌آمیز حمایت M5.\n(می‌توانید با دکمه زیر اطلاعات این سیگنال را مستقیماً وارد فرم انتشار نمایید.)"
            }
            "وتو" in lower || "veto" in lower || "توقف" in lower || "خطر" in lower -> {
                "🤖 دستیار هوش مصنوعی نظارت بر ریسک (AI3 Governor):\n" +
                "• ممیزی وضعیت نوسانات کلان: شاخص نوسان VIX در محدوده نرمال\n" +
                "• رژیم اسپرد بروکرهای OTC: بررسی شد؛ بدون اسلیپیج غیرعادی در Pocket Option و Quotex\n" +
                "• وضعیت وتو: آماده اعمال حق وتوی سراسری در صورت انتشار ناگهانی نرخ بهره فدرال رزرو (FOMC)\n" +
                "• توصیه: انتشار سیگنال‌های جفت‌ارزهای OTC بدون مانع است."
            }
            "تحلیل" in lower || "اخبار" in lower || "news" in lower -> {
                "🤖 دستیار هوش مصنوعی ایران باینری:\nپیش‌نویس خبر فاندامنتال یک‌ساعته برای انتشار:\nعنوان: تداوم فاز رنج بیت‌کوین در آستانه گشایش بازار نیویورک\nدسته‌بندی: CRYPTO\nخلاصه: نوسانات شاخص نوسان واقعی (ATR) در کف چند روزه نشان از آمادگی بازار برای یک Breakout قدرتمند در سشن عصرگاهی دارد. معامله‌گران باینری از قراردادهای ۵ دقیقه‌ای رنج بهره‌مند شوند."
            }
            "ریسک" in lower || "مدیریت" in lower -> {
                "🤖 دستیار هوش مصنوعی ایران باینری:\nتوصیه معماری AI2 Risk Architect:\n۱. سقف مواجهه روزانه: حداکثر ۳٪ کل بالانس در هر نشست\n۲. وتو قطعی مارتینگل: افزایش حجم دوبرابری مساوی با شکست ریاضی است\n۳. حداقل نرخ برد سربه‌سر برای Payout 92% برابر است با: L/(P+L) = 1/(0.92+1) = 52.08%."
            }
            else -> {
                "🤖 دستیار هوش مصنوعی ایران باینری:\nدستور شما دریافت شد. آماده بهینه‌سازی شبکه تحویل سیگنال‌ها (Signal Delivery Queue)، بازنگری سطوح اقتصادی پلن‌های اشتراک (Subscription Tiers)، تولید سیگنال‌های پربازده بر اساس رژیم‌های بازار، و انتشار اخبار فاندامنتال یک‌ساعته."
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TradingRepository? = null

        fun getInstance(context: Context): TradingRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val instance = TradingRepository(db, context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
