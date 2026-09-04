package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Feed
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
import com.example.ui.TradingViewModel
import com.example.ui.components.SupportChatModal
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ArticleGuideScreen
import com.example.ui.screens.AuthModal
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.NotFoundScreen
import com.example.ui.screens.SignalsHomeScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubscriptionScreen
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
    object Home : Screen("home", "سیگنال‌ها", Icons.Default.TrendingUp)
    object Subscriptions : Screen("subscriptions", "اشتراک ۵ گانه", Icons.Default.Stars)
    object News : Screen("news", "فید اخبار", Icons.Default.Feed)
    object Article : Screen("article", "دانشنامه سئو", Icons.Default.Book)
    object Admin : Screen("admin", "پنل ادمین", Icons.Default.AdminPanelSettings)
    object NotFound : Screen("not_found", "خطای ۴۰۴", Icons.Default.TrendingUp)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IranBinaryTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    val viewModel: TradingViewModel = viewModel()
                    val context = LocalContext.current

                    val signals by viewModel.signals.collectAsState()
                    val plans by viewModel.plans.collectAsState()
                    val newsList by viewModel.newsList.collectAsState()
                    val staffList by viewModel.staffList.collectAsState()
                    val currentUser by viewModel.currentUser.collectAsState()
                    val userPlan by viewModel.userPlan.collectAsState()
                    val subscriptions by viewModel.subscriptions.collectAsState()
                    val offlineCacheStatus by viewModel.offlineCacheStatus.collectAsState()

                    var showSupportSheet by remember { mutableStateOf(false) }
                    var showAuthSheet by remember { mutableStateOf(false) }
                    val supportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val authSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val bottomNavItems = listOf(
                        Screen.Home,
                        Screen.Subscriptions,
                        Screen.News,
                        Screen.Article,
                        Screen.Admin
                    )

                    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SlateDark950),
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar(
                                    containerColor = SlateDark900,
                                    tonalElevation = androidx.compose.ui.unit.Dp(0f)
                                ) {
                                    bottomNavItems.forEach { screen ->
                                        val selected = currentRoute == screen.route
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    imageVector = screen.icon,
                                                    contentDescription = screen.title
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = screen.title,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            selected = selected,
                                            onClick = {
                                                if (currentRoute != screen.route) {
                                                    navController.navigate(screen.route) {
                                                        popUpTo(Screen.Home.route) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = Color.Black,
                                                selectedTextColor = EmeraldGlow,
                                                indicatorColor = EmeraldNeon,
                                                unselectedIconColor = TextSecondary,
                                                unselectedTextColor = TextSecondary
                                            )
                                        )
                                    }
                                }
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
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Splash.route) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable(Screen.Home.route) {
                                    SignalsHomeScreen(
                                        signals = signals,
                                        brokers = viewModel.brokers,
                                        userPlan = userPlan,
                                        onOpenSubscriptions = {
                                            navController.navigate(Screen.Subscriptions.route)
                                        },
                                        onOpenSupport = {
                                            showSupportSheet = true
                                        },
                                        onOpenNotFoundTest = {
                                            navController.navigate(Screen.NotFound.route)
                                        }
                                    )
                                }

                                composable(Screen.Subscriptions.route) {
                                    SubscriptionScreen(
                                        plans = plans,
                                        currentUserPlan = userPlan,
                                        subscriptions = subscriptions,
                                        offlineCacheStatus = offlineCacheStatus,
                                        onBuyPlan = { plan ->
                                            viewModel.buyPlan(plan)
                                        }
                                    )
                                }

                                composable(Screen.News.route) {
                                    NewsScreen(
                                        newsList = newsList,
                                        onRefresh = {
                                            Toast.makeText(context, "فید اخبار فاندامنتال با موفقیت به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }

                                composable(Screen.Article.route) {
                                    ArticleGuideScreen()
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
