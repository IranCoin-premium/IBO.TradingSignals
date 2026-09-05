package com.example

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fcm.FcmNotificationHelper
import com.example.ui.TradingViewModel
import com.example.ui.components.AnimatedBottomNavBar
import com.example.ui.components.IranianMarketsSheet
import com.example.ui.components.SupportChatModal
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ArticleGuideScreen
import com.example.ui.screens.AuthModal
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.NotFoundScreen
import com.example.ui.screens.OnboardingTutorialScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignalHistoryScreen
import com.example.ui.screens.SignalsHomeScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.TradeJournalScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.IranBinaryTheme
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Splash : Screen("splash", "شروع", Icons.Default.TrendingUp)
    object OnboardingTutorial : Screen("onboarding_tutorial", "آموزش سیگنال‌ها", Icons.Default.TrendingUp)
    object Home : Screen("home", "سیگنال‌ها", Icons.Default.TrendingUp)
    object TradeJournal : Screen("trade_journal", "ژورنال ترید", Icons.Default.Assessment)
    object SignalHistory : Screen("signal_history", "تاریخچه سیگنال‌ها", Icons.Default.History)
    object Subscriptions : Screen("subscriptions", "اشتراک ۵ گانه", Icons.Default.Stars)
    object News : Screen("news", "فید اخبار", Icons.Default.Feed)
    object Article : Screen("article", "دانشنامه سئو", Icons.Default.Book)
    object Admin : Screen("admin", "پنل ادمین", Icons.Default.AdminPanelSettings)
    object UserProfile : Screen("user_profile", "پروفایل من", Icons.Default.Person)
    object Settings : Screen("settings", "تنظیمات نوتیفیکیشن", Icons.Default.Settings)
    object NotFound : Screen("not_found", "خطای ۴۰۴", Icons.Default.TrendingUp)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.fcm.FirebaseAppInitializer.ensureInitialized(this)
        enableEdgeToEdge()
        setContent {
            val viewModel: TradingViewModel = viewModel()
            val luxuryThemeMode by viewModel.luxuryThemeMode.collectAsState()

            IranBinaryTheme(luxuryMode = luxuryThemeMode) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    val context = LocalContext.current


                    val signals by viewModel.signals.collectAsState()
                    val plans by viewModel.plans.collectAsState()
                    val newsList by viewModel.newsList.collectAsState()
                    val staffList by viewModel.staffList.collectAsState()
                    val currentUser by viewModel.currentUser.collectAsState()
                    val userPlan by viewModel.userPlan.collectAsState()
                    val subscriptions by viewModel.subscriptions.collectAsState()
                    val offlineCacheStatus by viewModel.offlineCacheStatus.collectAsState()
                    val wonCount by viewModel.wonCount.collectAsState()
                    val lostCount by viewModel.lostCount.collectAsState()
                    val vetoCount by viewModel.vetoCount.collectAsState()
                    val notificationSettings by viewModel.notificationSettings.collectAsState()
                    val tradeLogs by viewModel.tradeLogs.collectAsState()

                    var showSupportSheet by remember { mutableStateOf(false) }
                    var showAuthSheet by remember { mutableStateOf(false) }
                    var showMarketsSheet by remember { mutableStateOf(false) }
                    val supportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val authSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val marketsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    // 1. Initialize FCM, Audio Alerts, Live Ticker
                    LaunchedEffect(Unit) {
                        FcmNotificationHelper.initNotificationChannel(context)
                        FcmNotificationHelper.subscribeToTopics(context)
                        com.example.util.SignalAudioAlertHelper.init(context)
                        com.example.util.LiveMarketTickerService.startStreaming()
                    }

                    // 2. Android 13+ (API 33) Runtime Notification Permission
                    val notificationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            FcmNotificationHelper.subscribeToTopics(context)
                        }
                    }

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!FcmNotificationHelper.isNotificationPermissionGranted(context)) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    // 3. Handle push notification click navigation
                    LaunchedEffect(intent) {
                        val navigateTo = intent?.getStringExtra(FcmNotificationHelper.EXTRA_NAVIGATE_TO)
                        if (navigateTo == "signals") {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                            }
                        }
                    }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val bottomNavItems = listOf(
                        Screen.Home,
                        Screen.TradeJournal,
                        Screen.Subscriptions,
                        Screen.News,
                        Screen.UserProfile
                    )

                    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SlateDark950),
                        bottomBar = {
                            if (showBottomBar) {
                                AnimatedBottomNavBar(
                                    items = bottomNavItems,
                                    currentRoute = currentRoute,
                                    onItemSelected = { screen ->
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Splash.route
                            ) {
                                composable(Screen.Splash.route) {
                                    SplashScreen(
                                        onFinished = {
                                            val prefs = context.getSharedPreferences("iran_binary_prefs", Context.MODE_PRIVATE)
                                            val hasCompletedTutorial = prefs.getBoolean("has_completed_onboarding_tutorial", false)
                                            val targetRoute = if (hasCompletedTutorial) Screen.Home.route else Screen.OnboardingTutorial.route
                                            navController.navigate(targetRoute) {
                                                popUpTo(Screen.Splash.route) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable(Screen.OnboardingTutorial.route) {
                                    OnboardingTutorialScreen(
                                        onFinish = {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.OnboardingTutorial.route) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                 composable(Screen.Home.route) {
                                    SignalsHomeScreen(
                                        signals = signals,
                                        brokers = viewModel.brokers,
                                        userPlan = userPlan,
                                        onAddTradeLog = { viewModel.addTradeLog(it) },
                                        onOpenSubscriptions = {
                                            navController.navigate(Screen.Subscriptions.route)
                                        },
                                        onOpenSupport = {
                                            showSupportSheet = true
                                        },
                                        onOpenMarkets = {
                                            showMarketsSheet = true
                                        },
                                        onOpenTutorial = {
                                            navController.navigate(Screen.OnboardingTutorial.route)
                                        },
                                        onOpenHistory = {
                                            navController.navigate(Screen.SignalHistory.route)
                                        },
                                        onOpenNotFoundTest = {
                                            navController.navigate(Screen.NotFound.route)
                                        },
                                        onOpenSettings = {
                                            navController.navigate(Screen.Settings.route)
                                        },
                                        onOpenTradeJournal = {
                                            navController.navigate(Screen.TradeJournal.route)
                                        },
                                        onOpenArticles = {
                                            navController.navigate(Screen.Article.route)
                                         },
                                         onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onSubmitFeedback = { feedbackType, asset, signalId, reasonCategory, description, rating, contactInfo ->
                                            viewModel.submitFeedback(
                                                feedbackType = feedbackType,
                                                asset = asset,
                                                signalId = signalId,
                                                reasonCategory = reasonCategory,
                                                description = description,
                                                rating = rating,
                                                contactInfo = contactInfo,
                                                onComplete = { success ->
                                                    Toast.makeText(
                                                        context,
                                                        if (success) "بازخورد شما با موفقیت در دیتابیس ثبت شد." else "خطا در ثبت بازخورد.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    )
                                }

                                composable(Screen.SignalHistory.route) {
                                    SignalHistoryScreen(
                                        signals = signals,
                                        wonCount = wonCount,
                                        lostCount = lostCount,
                                        vetoCount = vetoCount,
                                        onBack = { navController.popBackStack() },
                                        onDeleteSignal = { id -> viewModel.deleteSignal(id) },
                                        onClearHistory = { viewModel.clearHistoricalSignals() },
                                        onAddSampleSignal = { signal -> viewModel.addSignal(signal) },
                                        onSubmitFeedback = { feedbackType, asset, signalId, reasonCategory, description, rating, contactInfo ->
                                            viewModel.submitFeedback(
                                                feedbackType = feedbackType,
                                                asset = asset,
                                                signalId = signalId,
                                                reasonCategory = reasonCategory,
                                                description = description,
                                                rating = rating,
                                                contactInfo = contactInfo,
                                                onComplete = { success ->
                                                    Toast.makeText(
                                                        context,
                                                        if (success) "بازخورد شما با موفقیت در دیتابیس ثبت شد." else "خطا در ثبت بازخورد.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    )
                                }

                                composable(Screen.TradeJournal.route) {
                                    TradeJournalScreen(
                                        tradeLogs = tradeLogs,
                                        onAddTradeLog = { trade -> viewModel.addTradeLog(trade) },
                                        onUpdateTradeLog = { trade -> viewModel.updateTradeLog(trade) },
                                        onDeleteTradeLog = { id -> viewModel.deleteTradeLog(id) },
                                        onClearAll = { viewModel.clearAllTradeLogs() }
                                    )
                                }

                                composable(Screen.Subscriptions.route) {
                                    SubscriptionScreen(
                                        plans = plans,
                                        currentUserPlan = userPlan,
                                        subscriptions = subscriptions,
                                        offlineCacheStatus = offlineCacheStatus,
                                        onBuyPlan = { plan, method, ref, callback ->
                                            viewModel.buyPlan(plan, method, ref, callback)
                                        }
                                    )
                                }

                                composable(Screen.News.route) {
                                    NewsScreen(
                                        newsList = newsList,
                                        onRefresh = {
                                            viewModel.refreshLatestFinancialNews {
                                                Toast.makeText(context, "فید اخبار و تحلیل‌های مالی بازارهای ۲۰۲۶ به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }

                                composable(Screen.Article.route) {
                                    ArticleGuideScreen(
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Screen.Admin.route) {
                                    AdminScreen(
                                        currentUser = currentUser,
                                        signals = signals,
                                        plans = plans,
                                        newsList = newsList,
                                        staffList = staffList,
                                        subscriptions = subscriptions,
                                        offlineCacheStatus = offlineCacheStatus,
                                        currentLuxuryTheme = luxuryThemeMode,
                                        onSelectLuxuryTheme = { selectedMode ->
                                            viewModel.setLuxuryThemeMode(selectedMode)
                                            Toast.makeText(context, "پالت دیزاین ${selectedMode.title} اعمال شد ✨", Toast.LENGTH_SHORT).show()
                                        },
                                        onLogin = { email, pass ->

                                            viewModel.login(email, pass) { success, msg ->
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onLogout = {
                                            viewModel.logout()
                                            Toast.makeText(context, "از حساب کاربری خارج شدید.", Toast.LENGTH_SHORT).show()
                                        },
                                        onUpdatePassword = { newPass ->
                                            viewModel.updateAdminPassword(newPass)
                                            Toast.makeText(context, "رمز عبور با موفقیت به‌روز شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onAddNewStaff = { email, pass, name, role ->
                                            viewModel.addNewStaff(email, pass, name, role)
                                            Toast.makeText(context, "کارمند جدید اضافه شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onAddSignal = { signal ->
                                            viewModel.addSignal(signal)
                                            Toast.makeText(context, "سیگنال با موفقیت منتشر شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onDeleteSignal = { id ->
                                            viewModel.deleteSignal(id)
                                            Toast.makeText(context, "سیگنال حذف شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onUpdateSignalStatus = { signal, status ->
                                            viewModel.updateSignalStatus(signal, status)
                                        },
                                        onUpdatePlan = { plan ->
                                            viewModel.updatePlan(plan)
                                            Toast.makeText(context, "قیمت و مشخصات پلن ذخیره شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onAddNews = { news ->
                                            viewModel.addNews(news)
                                            Toast.makeText(context, "خبر فاندامنتال در فید ثبت شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onDeleteNews = { id ->
                                            viewModel.deleteNews(id)
                                        },
                                        onSyncCloud = {
                                            viewModel.syncOfflineCache()
                                            Toast.makeText(context, "درخواست همگام‌سازی ابری ارسال شد.", Toast.LENGTH_SHORT).show()
                                        },
                                        onRunAiAgent = { prompt ->
                                            viewModel.runAiAgent(prompt)
                                        }
                                    )
                                }

                                composable(Screen.UserProfile.route) {
                                    val favoriteSignals = signals.filter { it.isFavorite }
                                    UserProfileScreen(
                                        currentUser = currentUser,
                                        userPlan = userPlan,
                                        favoriteSignals = favoriteSignals,
                                        subscriptions = subscriptions,
                                        tradeLogsCount = tradeLogs.size,
                                        wonCount = wonCount,
                                        lostCount = lostCount,
                                        onBack = { navController.popBackStack() },
                                        onOpenSubscriptions = {
                                            navController.navigate(Screen.Subscriptions.route) {
                                                popUpTo(Screen.Home.route)
                                                launchSingleTop = true
                                            }
                                        },
                                        onOpenSettings = {
                                            navController.navigate(Screen.Settings.route) {
                                                launchSingleTop = true
                                            }
                                        },
                                        onOpenSupport = {
                                            showSupportSheet = true
                                        },
                                        onOpenAdmin = {
                                            navController.navigate(Screen.Admin.route) {
                                                launchSingleTop = true
                                            }
                                        },
                                        onToggleFavoriteSignal = { signal ->
                                            viewModel.toggleFavorite(signal)
                                        },
                                        onLogout = {
                                            viewModel.logout()
                                            Toast.makeText(context, "از حساب کاربری خارج شدید.", Toast.LENGTH_SHORT).show()
                                        },
                                        onLoginClick = {
                                            showAuthSheet = true
                                        }
                                    )
                                }

                                composable(Screen.Settings.route) {
                                    SettingsScreen(
                                        settings = notificationSettings,
                                        onToggleMaster = { enabled -> viewModel.toggleMasterNotifications(enabled) },
                                        onToggleCategory = { cat, enabled -> viewModel.toggleCategoryNotification(cat, enabled) },
                                        onToggleHighAccuracyOnly = { enabled -> viewModel.toggleHighAccuracyOnly(enabled) },
                                        onToggleRiskWarnings = { enabled -> viewModel.toggleRiskWarnings(enabled) },
                                        onToggleSound = { enabled -> viewModel.toggleSound(enabled) },
                                        onToggleVibration = { enabled -> viewModel.toggleVibration(enabled) },
                                        onResetDefaults = { viewModel.resetNotificationSettings() },
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                composable(Screen.NotFound.route) {
                                    NotFoundScreen(
                                        onGoHome = {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Home.route) { inclusive = true }
                                            }
                                        },
                                        onRetry = {
                                            navController.navigate(Screen.Home.route)
                                        }
                                    )
                                }
                            }

                            // 24/7 Support Live Chat Modal
                            if (showSupportSheet) {
                                SupportChatModal(
                                    sheetState = supportSheetState,
                                    onDismiss = { showSupportSheet = false }
                                )
                            }

                            // Iranian Markets Hub Modal (Bazaar, Myket, IranApps)
                            if (showMarketsSheet) {
                                IranianMarketsSheet(
                                    sheetState = marketsSheetState,
                                    onDismiss = { showMarketsSheet = false }
                                )
                            }

                            // Client Auth Modal (Google/GitHub/Email)
                            if (showAuthSheet) {
                                AuthModal(
                                    sheetState = authSheetState,
                                    onDismiss = { showAuthSheet = false },
                                    onGoogleSignIn = {
                                        viewModel.loginWithGoogle()
                                        Toast.makeText(context, "ورود با گوگل انجام شد.", Toast.LENGTH_SHORT).show()
                                    },
                                    onGitHubSignIn = {
                                        viewModel.loginWithGitHub()
                                        Toast.makeText(context, "ورود با گیت‌هاب انجام شد.", Toast.LENGTH_SHORT).show()
                                    },
                                    onManualLogin = { email, pass ->
                                        viewModel.login(email, pass) { success, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onManualRegister = { email, pass, name ->
                                        viewModel.registerManual(email, pass, name) { success, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
