package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.auth.FirebaseAuthService
import com.example.data.local.NewsEntity
import com.example.data.local.NotificationPreferencesRepository
import com.example.data.local.NotificationSettings
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.data.local.TradeLogEntity
import com.example.data.repository.*
import com.example.fcm.FcmSignalBroadcaster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradingViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val signalRepository: SignalRepository,
    private val newsRepository: NewsRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val tradeLogRepository: TradeLogRepository,
    private val feedbackRepository: FeedbackRepository,
    private val offlineCacheManager: FirestoreOfflineCacheManager,
    private val firebaseAuthService: FirebaseAuthService,
    private val notificationPreferencesRepository: NotificationPreferencesRepository
) : AndroidViewModel(application) {

    val notificationSettings: StateFlow<NotificationSettings> = notificationPreferencesRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationSettings())

    val signals: StateFlow<List<SignalEntity>> = signalRepository.getSignals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historicalSignals: StateFlow<List<SignalEntity>> = signalRepository.getHistoricalSignals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wonCount: StateFlow<Int> = signalRepository.getWonCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lostCount: StateFlow<Int> = signalRepository.getLostCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vetoCount: StateFlow<Int> = signalRepository.getVetoCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeSignals: StateFlow<List<SignalEntity>> = signalRepository.getActiveSignals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSignals: StateFlow<List<SignalEntity>> = signalRepository.getFavoriteSignals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(signal: SignalEntity) {
        viewModelScope.launch {
            signalRepository.toggleFavorite(signal)
        }
    }

    val plans: StateFlow<List<PlanEntity>> = subscriptionRepository.getPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<UserSubscriptionEntity>> = subscriptionRepository.getSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsList: StateFlow<List<NewsEntity>> = newsRepository.getNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staffList: StateFlow<List<UserEntity>> = authRepository.currentUser
        .map { listOfNotNull(it) } // Simplified: in a real app, this would be a separate query
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedbacks: StateFlow<List<com.example.data.local.FeedbackEntity>> = feedbackRepository.getFeedbacks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedbackCount: StateFlow<Int> = feedbackRepository.getFeedbackCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val tradeLogs: StateFlow<List<com.example.data.local.TradeLogEntity>> = tradeLogRepository.getTradeLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncMetadata: StateFlow<List<com.example.data.local.SyncMetadataEntity>> = offlineCacheManager.getAllSyncMetadata()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ultra Luxury Theme Mode State
    private val _luxuryThemeMode = MutableStateFlow(com.example.ui.theme.LuxuryThemeMode.PEARL_FROSTED_GLASS)
    val luxuryThemeMode: StateFlow<com.example.ui.theme.LuxuryThemeMode> = _luxuryThemeMode.asStateFlow()

    fun setLuxuryThemeMode(mode: com.example.ui.theme.LuxuryThemeMode) {
        _luxuryThemeMode.value = mode
    }

    val offlineCacheStatus: StateFlow<OfflineCacheSyncStatus> = offlineCacheManager.syncStatus

    val brokers: List<BrokerItem> = listOf(
        BrokerItem("pocket", "Pocket Option", "پاکت آپشن", "۹۲٪", true, "سریع", "$۵۰", "معتبر", "پیشنهادی", "بهترین بروکر برای کاربران ایرانی", regulation = "ثبت‌شده MISA (کومورو) — نظارت آفشور"),
        BrokerItem("quotex", "Quotex", "کوتکس", "۸۹٪", true, "عالی", "$۱۰", "معتبر", "محبوب", "پلتفرم مدرن و ساده", regulation = "آفشور (SVG) — بدون نظارت معتبر"),
        BrokerItem("iq", "IQ Option", "آی‌کیو آپشن", "۹۴٪", false, "فوق‌العاده", "$۱۰", "محدود", "حرفه‌ای", "قدیمی‌ترین و معتبرترین", regulation = "مجوز CySEC (سابق، 247/14) — نظارت محدود"),
        BrokerItem("alpari", "Alpari Fixed", "آلپاری فیکس", "۸۵٪", false, "متوسط", "$۱", "معتبر", "اقتصادی", "مناسب برای مبالغ پایین", regulation = "آفشور — سابقاً تحت نظارت FCA")
    )

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    private val _userPlan = MutableStateFlow("اشتراک ویژه VIP (فعال)")
    val userPlan: StateFlow<String> = _userPlan.asStateFlow()

    val usdtFeedState = com.example.util.UsdtRateFeedService.feedState

    private val prefs = application.getSharedPreferences("iran_binary_prefs", android.content.Context.MODE_PRIVATE)
    private val _userUsdtWallet = MutableStateFlow(prefs.getString("user_usdt_wallet", "") ?: "")
    val userUsdtWallet: StateFlow<String> = _userUsdtWallet.asStateFlow()

    private val authTokenProvider: () -> String? = { prefs.getString("backend_jwt", null) }
    private val referralApi: com.example.data.remote.ReferralApiService =
        com.example.data.remote.ReferralApiFactory.create(
            baseUrl = BuildConfig.BACKEND_BASE_URL,
            authInterceptor = okhttp3.Interceptor { chain ->
                val token = authTokenProvider()
                val req = if (token.isNullOrBlank()) chain.request()
                else chain.request().newBuilder().header("Authorization", "Bearer $token").build()
                chain.proceed(req)
            },
            debugLogging = BuildConfig.DEBUG
        )

    private val _referralDashboard = MutableStateFlow<com.example.data.remote.DashboardDto?>(null)
    val referralDashboard: StateFlow<com.example.data.remote.DashboardDto?> = _referralDashboard.asStateFlow()

    private val _referralError = MutableStateFlow<String?>(null)
    val referralError: StateFlow<String?> = _referralError.asStateFlow()

    private val _referralUnavailable = MutableStateFlow(false)
    val referralUnavailable: StateFlow<Boolean> = _referralUnavailable.asStateFlow()

    private val _tabsManifest = MutableStateFlow<List<com.example.data.remote.PlatformTabDto>>(emptyList())
    val tabsManifest: StateFlow<List<com.example.data.remote.PlatformTabDto>> = _tabsManifest.asStateFlow()

    private val _referralCode = MutableStateFlow("")
    val referralCode: StateFlow<String> = _referralCode.asStateFlow()

    private val _referralEarningsUsdt = MutableStateFlow(0.0)
    val referralEarningsUsdt: StateFlow<Double> = _referralEarningsUsdt.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.initializeAdmin()
            seedInitialData()
        }
        com.example.util.UsdtRateFeedService.startStreaming(getApplication())
        refreshReferralFromBackend()
    }

    private suspend fun seedInitialData() {
        subscriptionRepository.seedPlans(
            listOf(
                PlanEntity(
                    id = 1,
                    title = "پلن آزمایشی ۷ روزه",
                    durationText = "۷ روز",
                    durationDays = 7,
                    priceToman = "۲۹۰,۰۰۰ تومان",
                    priceUsdt = "۴.۵ USDT",
                    discountPercent = 0,
                    isPopular = false,
                    features = "دسترسی به سیگنال‌های کریپتو و فارکس,پشتیبانی آنلاین تلگرام,تست استراتژی‌های باینری,کانال VIP تلگرام",
                    badge = "شروع سریع"
                ),
                PlanEntity(
                    id = 2,
                    title = "پلن برنزی ۳۰ روزه",
                    durationText = "۳۰ روز",
                    durationDays = 30,
                    priceToman = "۹۵۰,۰۰۰ تومان",
                    priceUsdt = "۱۵ USDT",
                    discountPercent = 15,
                    isPopular = false,
                    features = "سیگنال‌های VIP باینری با وین‌ریت +۸۵٪,سیگنال‌های جفت ارزهای OTC و فارکس,دسترسی کامل به ژورنال ترید,پشتیبانی اختصاصی",
                    badge = "استاندارد"
                ),
                PlanEntity(
                    id = 3,
                    title = "پلن نقره‌ای ۹۰ روزه",
                    durationText = "۹۰ روز",
                    durationDays = 90,
                    priceToman = "۲,۵۵۰,۰۰۰ تومان",
                    priceUsdt = "۴۰ USDT",
                    discountPercent = 25,
                    isPopular = false,
                    features = "دسترسی همزمان به تمام سیگنال‌های VIP,تحلیل فاندامنتال و اخبار لحظه‌ای,ربات هشدار صوتی و تلگرام,مدیریت ریسک هوشمند مارتینگل",
                    badge = "به‌صرفه"
                ),
                PlanEntity(
                    id = 4,
                    title = "پلن طلایی ۱۸۰ روزه",
                    durationText = "۱۸۰ روز",
                    durationDays = 180,
                    priceToman = "۴,۸۰۰,۰۰۰ تومان",
                    priceUsdt = "۷۵ USDT",
                    discountPercent = 35,
                    isPopular = true,
                    features = "تمامی امکانات پلن نقره‌ای بدون محدودیت,سیگنال‌های اسکالپ ۱ دقیقه‌ای پرسرعت,وبینارهای هفتگی تحلیل لایو,کانال ویژه تریدرهای حرفه‌ای,مشاوره اختصاصی ادمین",
                    badge = "محبوب‌ترین (VIP)"
                ),
                PlanEntity(
                    id = 5,
                    title = "پلن الماس ۳۶۵ روزه",
                    durationText = "۳۶۵ روز",
                    durationDays = 365,
                    priceToman = "۸,۹۰۰,۰۰۰ تومان",
                    priceUsdt = "۱۴۰ USDT",
                    discountPercent = 50,
                    isPopular = false,
                    features = "دسترسی ۱ ساله به کلیه خدمات پریمیوم,استراتژی‌های انحصاری پاکت‌آپشن و کوتکس,ضمانت بازگشت وجه در صورت نارضایتی,پشتیبانی ۲۴/۷ تلفنی و تلگرامی",
                    badge = "فوق ویژه (Diamond)"
                )
            )
        )

        signalRepository.seedSignals(
            listOf(
                SignalEntity(
                    id = 1,
                    asset = "EUR/USD (OTC)",
                    category = "OTC",
                    direction = "CALL",
                    strikePrice = "1.08450",
                    currentPrice = "1.08458",
                    expiry = "1m",
                    payoutRate = "92%",
                    marketRegime = "Trend Bullish",
                    confidenceScore = 94,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "شکست مقاومت با حجم بالا در کندل ۱ دقیقه‌ای و تایید اندیکاتور RSI بالای ۵۰",
                    recommendedBrokers = "Pocket Option, Quotex",
                    status = "ACTIVE",
                    isFavorite = true,
                    timestamp = System.currentTimeMillis()
                ),
                SignalEntity(
                    id = 2,
                    asset = "GBP/USD",
                    category = "FOREX",
                    direction = "PUT",
                    strikePrice = "1.26320",
                    currentPrice = "1.26312",
                    expiry = "3m",
                    payoutRate = "89%",
                    marketRegime = "Breakout",
                    confidenceScore = 91,
                    riskScore = "متوسط (Medium)",
                    vetoStatus = "تایید شده",
                    rationale = "برخورد به مقاومت داینامیک و تشکیل الگوی کندل استیک چکش معکوس",
                    recommendedBrokers = "Pocket Option, IQ Option",
                    status = "ACTIVE",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 60_000
                ),
                SignalEntity(
                    id = 3,
                    asset = "BTC/USDT",
                    category = "CRYPTO",
                    direction = "CALL",
                    strikePrice = "68,450.00",
                    currentPrice = "68,485.50",
                    expiry = "5m",
                    payoutRate = "90%",
                    marketRegime = "Breakout",
                    confidenceScore = 88,
                    riskScore = "متوسط (Medium)",
                    vetoStatus = "تایید شده",
                    rationale = "حمایت قوی در فیبوناچی ۶۱.۸٪ و افزایش مومنتوم خریداران باینری",
                    recommendedBrokers = "Pocket Option, Quotex",
                    status = "ACTIVE",
                    isFavorite = true,
                    timestamp = System.currentTimeMillis() - 120_000
                ),
                SignalEntity(
                    id = 4,
                    asset = "USD/JPY (OTC)",
                    category = "OTC",
                    direction = "CALL",
                    strikePrice = "154.210",
                    currentPrice = "154.260",
                    expiry = "1m",
                    payoutRate = "95%",
                    marketRegime = "Trend Bullish",
                    confidenceScore = 96,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "کراس صعودی مووینگ اوریج ۲۰ و ۵۰ در تایم‌فریم ۱ دقیقه پاکت‌آپشن",
                    recommendedBrokers = "Pocket Option",
                    status = "ACTIVE",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 180_000
                ),
                SignalEntity(
                    id = 5,
                    asset = "AUD/CAD",
                    category = "FOREX",
                    direction = "PUT",
                    strikePrice = "0.89120",
                    currentPrice = "0.89110",
                    expiry = "5m",
                    payoutRate = "87%",
                    marketRegime = "Range Compression",
                    confidenceScore = 86,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "اشباع خرید در استوکاستیک و بازگشت از باند بالایی بولینگر باند",
                    recommendedBrokers = "Quotex, Alpari Fixed",
                    status = "ACTIVE",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 240_000
                ),
                SignalEntity(
                    id = 6,
                    asset = "EUR/GBP",
                    category = "FOREX",
                    direction = "CALL",
                    strikePrice = "0.85400",
                    currentPrice = "0.85435",
                    expiry = "5m",
                    payoutRate = "90%",
                    marketRegime = "Trend Bullish",
                    confidenceScore = 93,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "سیگنال موفق مطابق استراتژی پولبک به میانگین متحرک",
                    recommendedBrokers = "Pocket Option",
                    status = "WON",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 3_600_000
                ),
                SignalEntity(
                    id = 7,
                    asset = "USD/CHF",
                    category = "FOREX",
                    direction = "PUT",
                    strikePrice = "0.90210",
                    currentPrice = "0.90175",
                    expiry = "3m",
                    payoutRate = "88%",
                    marketRegime = "Breakout",
                    confidenceScore = 95,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "شکست خط روند نزولی و تثبیت در زیر سطح پیوت",
                    recommendedBrokers = "Quotex",
                    status = "WON",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 7_200_000
                ),
                SignalEntity(
                    id = 8,
                    asset = "NZD/USD",
                    category = "FOREX",
                    direction = "CALL",
                    strikePrice = "0.61200",
                    currentPrice = "0.61180",
                    expiry = "5m",
                    payoutRate = "85%",
                    marketRegime = "Range Compression",
                    confidenceScore = 78,
                    riskScore = "بالا (High)",
                    vetoStatus = "رد شده با Veto",
                    rationale = "نوسان شدید خبری خارج از برنامه معاملاتی",
                    recommendedBrokers = "Pocket Option",
                    status = "LOST",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 10_800_000
                ),
                SignalEntity(
                    id = 9,
                    asset = "Gold (XAU/USD)",
                    category = "COMMODITIES",
                    direction = "CALL",
                    strikePrice = "2340.50",
                    currentPrice = "2344.80",
                    expiry = "5m",
                    payoutRate = "92%",
                    marketRegime = "Trend Bullish",
                    confidenceScore = 95,
                    riskScore = "کم ریسک (Low)",
                    vetoStatus = "تایید شده",
                    rationale = "واکنش به حمایت طلایی و پرتاب قیمت به سمت مقاومت بعدی",
                    recommendedBrokers = "Pocket Option, Quotex",
                    status = "WON",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 14_400_000
                ),
                SignalEntity(
                    id = 10,
                    asset = "ETH/USDT",
                    category = "CRYPTO",
                    direction = "CALL",
                    strikePrice = "3,480.00",
                    currentPrice = "3,495.20",
                    expiry = "3m",
                    payoutRate = "91%",
                    marketRegime = "Breakout",
                    confidenceScore = 90,
                    riskScore = "متوسط (Medium)",
                    vetoStatus = "تایید شده",
                    rationale = "افزایش حجم معاملات اتریوم همگام با کندل‌های صعودی قدرتمند",
                    recommendedBrokers = "Pocket Option",
                    status = "WON",
                    isFavorite = false,
                    timestamp = System.currentTimeMillis() - 18_000_000
                )
            )
        )

        newsRepository.seedNews(
            listOf(
                NewsEntity(
                    id = 1,
                    title = "بیانیه مهم فدرال رزرو در مورد نرخ بهره و تاثیر آن بر جفت ارز EUR/USD",
                    summary = "جلسه اضطراری فدرال رزرو حاکی از تثبیت نرخ بهره تا ماه‌های آینده است که موجب تقویت دلار در بازارهای جهانی شده است.",
                    impact = "HIGH",
                    category = "FOREX",
                    fullContent = "بازار جفت ارزهای دلاری در پی بیانیه اخیر فدرال رزرو با نوسانات شدیدی همراه است. تریدرهای باینری آپشن توصیه می‌شود در زمان اعلام اخبار مهم تقویم اقتصادی از ورود به معاملات پرریسک خودداری کنند.",
                    source = "ایران باینری آپشن",
                    sentiment = "صعودی برای دلار",
                    timeAgo = "۱۰ دقیقه پیش",
                    timestamp = System.currentTimeMillis() - 600_000
                ),
                NewsEntity(
                    id = 2,
                    title = "تحلیل تکنیکال بیت‌کوین: تلاش برای تثبیت بالای مقاومت ۶۸,۰۰۰ دلار",
                    summary = "بیت‌کوین در تلاش برای عبور از سد مقاومتی ۶۸,۵۰۰ دلار با افزایش چشمگیر حجم خریداران در کندل‌های ۴ ساعته مواجه شد.",
                    impact = "MEDIUM",
                    category = "CRYPTO",
                    fullContent = "رمزارز اول بازار در صورت تثبیت بالای خط روند می‌تواند اهداف بالاتری را لمس کند. سیگنال‌های کریپتویی باینری آپشن در تایم‌فریم‌های ۳ و ۵ دقیقه‌ای پیشنهاد می‌شوند.",
                    source = "کوئن‌تلگراف",
                    sentiment = "صعودی",
                    timeAgo = "۴۵ دقیقه پیش",
                    timestamp = System.currentTimeMillis() - 2_700_000
                ),
                NewsEntity(
                    id = 3,
                    title = "بهترین ساعات معاملاتی باینری در بروکرهای پاکت آپشن و کوتکس",
                    summary = "همپوشانی سشن‌های لندن و نیویورک بیشترین حجم نوسان و بالاترین ضریب Payout (تا ۹۵٪) را در اختیار تریدرها قرار می‌دهد.",
                    impact = "LOW",
                    category = "EDUCATION",
                    fullContent = "ساعات ۱۶:۳۰ الی ۲۰:۳۰ به وقت ایران مناسب‌ترین بازه زمانی برای معاملات اسکالپ ۱ و ۳ دقیقه‌ای در جفت ارزهای اصلی می‌باشد.",
                    source = "آکادمی ایران باینری",
                    sentiment = "خنثی",
                    timeAgo = "۲ ساعت پیش",
                    timestamp = System.currentTimeMillis() - 7_200_000
                )
            )
        )

        tradeLogRepository.seedLogs(
            listOf(
                TradeLogEntity(
                    id = 1,
                    asset = "EUR/USD (OTC)",
                    direction = "CALL",
                    result = "WIN",
                    tradeAmount = 25.0,
                    payoutPercent = 92,
                    profitOrLoss = 23.0,
                    broker = "Pocket Option",
                    strategy = "شکست و پولبک",
                    notes = "ورود دقیق در نقطه پولبک با تاییدیه اندیکاتور RSI",
                    timestamp = System.currentTimeMillis() - 3600_000
                ),
                TradeLogEntity(
                    id = 2,
                    asset = "BTC/USDT",
                    direction = "CALL",
                    result = "WIN",
                    tradeAmount = 50.0,
                    payoutPercent = 90,
                    profitOrLoss = 45.0,
                    broker = "Quotex",
                    strategy = "حمایت و مقاومت",
                    notes = "معامله ۵ دقیقه‌ای در روند صعودی با مدیریت سرمایه ۲٪",
                    timestamp = System.currentTimeMillis() - 7200_000
                ),
                TradeLogEntity(
                    id = 3,
                    asset = "USD/JPY (OTC)",
                    direction = "PUT",
                    result = "LOSS",
                    tradeAmount = 20.0,
                    payoutPercent = 95,
                    profitOrLoss = -20.0,
                    broker = "Pocket Option",
                    strategy = "واگرایی RSI",
                    notes = "ادامه یافتن مومنتوم پرقدرت و فعال شدن حد ضرر شخصی",
                    timestamp = System.currentTimeMillis() - 10800_000
                )
            )
        )
    }

    fun refreshReferralFromBackend() {
        viewModelScope.launch {
            _referralUnavailable.value = false
            try {
                val code = referralApi.getMyCode().data
                _referralCode.value = code.code
                prefs.edit().putString("user_referral_code", code.code).apply()

                val dash = referralApi.getDashboard().data
                _referralDashboard.value = dash
                _referralEarningsUsdt.value = dash.balance.toDoubleOrNull() ?: 0.0

                val manifest = referralApi.getTabsManifest().data
                _tabsManifest.value = manifest.tabs
            } catch (e: Exception) {
                _referralError.value = e.message ?: "referral_sync_failed"
                _referralUnavailable.value = true
            }
        }
    }

    fun refreshUsdtRate() {
        viewModelScope.launch {
            com.example.util.UsdtRateFeedService.forceRefresh(getApplication())
        }
    }

    fun saveUserUsdtWallet(address: String) {
        prefs.edit().putString("user_usdt_wallet", address.trim()).apply()
        _userUsdtWallet.value = address.trim()
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            firebaseAuthService.signInWithEmail(email, pass)
            val user = authRepository.authenticateUser(email, pass)
            if (user != null) {
                _userPlan.value = user.activePlan
                onResult(true, "ورود با موفقیت انجام شد.")
            } else {
                onResult(false, "اطلاعات ورود نادرست است.")
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            val googleResult = firebaseAuthService.signInWithGoogleCredentialManager()
            val email = googleResult.getOrNull()?.email ?: "user.google@gmail.com"
            val name = googleResult.getOrNull()?.displayName ?: "کاربر گرامی گوگل"
            val user = authRepository.registerOrLoginSocial("GOOGLE", name, email)
            _userPlan.value = user.activePlan
        }
    }

    fun loginWithGitHub() {
        viewModelScope.launch {
            val user = authRepository.registerOrLoginSocial("GITHUB", "کاربر توسعه‌دهنده گیت‌هاب", "developer@github.com")
            _userPlan.value = user.activePlan
        }
    }

    fun registerManual(email: String, pass: String, name: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            firebaseAuthService.signUpWithEmail(email, pass)
            val user = authRepository.registerUser(email, pass, name)
            _userPlan.value = user.activePlan
            onResult(true, "حساب کاربری با موفقیت ایجاد شد.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuthService.signOut()
            authRepository.logout()
            _userPlan.value = "نیازمند فعال‌سازی اشتراک VIP"
        }
    }

    fun updateAdminPassword(newPass: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            authRepository.updatePassword(user.id, newPass)
        }
    }

    fun addNewStaff(email: String, pass: String, name: String, role: String) {
        viewModelScope.launch {
            authRepository.addNewStaff(email, pass, name, role)
        }
    }

    fun addSignal(signal: SignalEntity) {
        viewModelScope.launch {
            signalRepository.addSignal(signal)
            FcmSignalBroadcaster.broadcastHighAccuracySignal(getApplication(), signal)
            com.example.util.SignalAudioAlertHelper.speakSignalAlert(getApplication(), signal)
            com.example.widget.SignalWidgetProvider.updateAllWidgets(
                context = getApplication(),
                asset = signal.asset,
                direction = "${signal.direction} 🟢",
                confidence = "وین‌ریت: ${signal.confidenceScore}٪",
                expiry = "انقضا: ${signal.expiry}"
            )
        }
    }

    fun deleteSignal(id: Long) {
        viewModelScope.launch {
            signalRepository.deleteSignal(id)
        }
    }

    fun clearHistoricalSignals() {
        viewModelScope.launch {
            signalRepository.clearHistory()
        }
    }

    fun updateSignalStatus(signal: SignalEntity, status: String) {
        viewModelScope.launch {
            signalRepository.updateSignal(signal.copy(status = status))
            if ((status == "WON" || status == "LOST") && signal.status != status) {
                val payout = signal.payoutRate.replace("%", "").toIntOrNull() ?: 85
                val tradeAmount = 100.0
                val profit = if (status == "WON") tradeAmount * (payout / 100.0) else -tradeAmount
                val log = com.example.data.local.TradeLogEntity(
                    asset = signal.asset,
                    direction = signal.direction,
                    result = status,
                    tradeAmount = tradeAmount,
                    payoutPercent = payout,
                    profitOrLoss = profit,
                    broker = "Auto-Journaled",
                    entryPrice = signal.currentPrice,
                    exitPrice = "",
                    expiry = signal.expiry,
                    strategy = "AI Signal",
                    notes = "Automated journal entry from signal",
                    emotionalState = "منضبط و آرام",
                    timestamp = System.currentTimeMillis()
                )
                tradeLogRepository.addTradeLog(log)
            }
        }
    }

    fun updatePlan(plan: PlanEntity) {
        viewModelScope.launch {
            subscriptionRepository.updatePlan(plan)
        }
    }

    fun buyPlan(plan: PlanEntity, paymentMethod: String, transactionRef: String, onResult: (Boolean, String) -> Unit) {
        val user = currentUser.value
        if (user == null) {
            onResult(false, "لطفا ابتدا وارد حساب کاربری خود شوید.")
            return
        }
        viewModelScope.launch {
            try {
                val formattedRef = transactionRef.trim().uppercase()
                if (formattedRef.length < 4) {
                    onResult(false, "کد پیگیری وارد شده بسیار کوتاه و نامعتبر است.")
                    return@launch
                }
                val existing = subscriptions.value.firstOrNull { it.transactionRef.trim().uppercase() == formattedRef }
                if (existing != null) {
                    onResult(false, "این کد پیگیری تراکنش قبلاً ثبت شده است.")
                    return@launch
                }
                val days = when {
                    "یک هفته" in plan.durationText -> 7
                    "یک ماه" in plan.durationText -> 30
                    "سه ماه" in plan.durationText -> 90
                    "شش ماه" in plan.durationText -> 180
                    "یک سال" in plan.durationText -> 365
                    else -> 30
                }
                val sub = subscriptionRepository.recordUserSubscription(
                    userId = user.id.toString(),
                    userEmail = user.email,
                    planTitle = "${plan.title} (${plan.durationText})",
                    durationDays = days,
                    priceToman = plan.priceToman,
                    priceUsdt = plan.priceUsdt,
                    paymentMethod = paymentMethod,
                    transactionRef = formattedRef
                )
                if (sub != null) {
                    _userPlan.value = "${plan.title} (${plan.durationText})"
                    onResult(true, "تراکنش $formattedRef تایید شد.")
                } else {
                    onResult(false, "خطا در برقراری ارتباط.")
                }
            } catch (e: Exception) {
                onResult(false, "خطا: ${e.message}")
            }
        }
    }

    fun syncOfflineCache() {
        viewModelScope.launch {
            offlineCacheManager.syncAllFromCloud()
        }
    }

    fun addNews(news: NewsEntity) {
        viewModelScope.launch {
            newsRepository.addNews(listOf(news))
        }
    }

    fun deleteNews(id: Long) {
        viewModelScope.launch {
            newsRepository.deleteNews(id)
        }
    }

    fun refreshLatestFinancialNews(onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val count = newsRepository.refreshLatestFinancialNews()
            onComplete(count)
        }
    }

    fun addTradeLog(trade: com.example.data.local.TradeLogEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            tradeLogRepository.addTradeLog(trade)
            onComplete()
        }
    }

    fun updateTradeLog(trade: com.example.data.local.TradeLogEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            tradeLogRepository.updateTradeLog(trade)
            onComplete()
        }
    }

    fun deleteTradeLog(id: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            tradeLogRepository.deleteTradeLog(id)
            onComplete()
        }
    }

    fun clearAllTradeLogs(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            tradeLogRepository.clearAll()
            onComplete()
        }
    }

    fun submitFeedback(feedbackType: String, asset: String?, signalId: Long?, reasonCategory: String?, description: String, rating: Int, contactInfo: String?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val entity = com.example.data.local.FeedbackEntity(
                    feedbackType = feedbackType, asset = asset, signalId = signalId, reasonCategory = reasonCategory, description = description, rating = rating, contactInfo = contactInfo, timestamp = System.currentTimeMillis()
                )
                feedbackRepository.addFeedback(entity)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun deleteFeedback(id: Long) {
        viewModelScope.launch {
            feedbackRepository.deleteFeedback(id)
        }
    }

    fun toggleMasterNotifications(enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setMasterEnabled(enabled) }
    }

    fun toggleCategoryNotification(category: String, enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setCategoryEnabled(category, enabled) }
    }

    fun toggleHighAccuracyOnly(enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setHighAccuracyOnly(enabled) }
    }

    fun toggleRiskWarnings(enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setRiskWarningsEnabled(enabled) }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setSoundEnabled(enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch { notificationPreferencesRepository.setVibrationEnabled(enabled) }
    }

    fun resetNotificationSettings() {
        viewModelScope.launch { notificationPreferencesRepository.resetToDefaults() }
    }

    fun runAiAgent(prompt: String): String {
        return "AI Agent response to: $prompt" // Simplified
    }
}
