package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.SignalEntity
import com.example.data.repository.BrokerItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.ui.components.BrandLogomotion
import com.example.ui.components.BrokerTimelineMarquee
import com.example.ui.components.QuickJournalLogDialog
import com.example.ui.components.SignalCard
import com.example.ui.components.SignalDetailBottomSheet
import com.example.ui.components.SignalFilterBar
import com.example.ui.components.SmartRiskCalculatorModal
import com.example.ui.components.SubmitFeedbackDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalsHomeScreen(
    signals: List<SignalEntity>,
    brokers: List<BrokerItem>,
    userPlan: String,
    onOpenSubscriptions: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenMarkets: () -> Unit,
    onOpenTutorial: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenNotFoundTest: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenTradeJournal: () -> Unit = {},
    onOpenArticles: () -> Unit = {},
    onAddTradeLog: ((com.example.data.local.TradeLogEntity) -> Unit)? = null,
    onToggleFavorite: ((SignalEntity) -> Unit)? = null,
    onSubmitFeedback: ((feedbackType: String, asset: String?, signalId: Long?, reasonCategory: String?, description: String, rating: Int, contactInfo: String?) -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var minConfidenceFilter by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var targetFeedbackSignal by remember { mutableStateOf<SignalEntity?>(null) }
    var selectedSignalForSheet by remember { mutableStateOf<SignalEntity?>(null) }
    var quickJournalSignal by remember { mutableStateOf<SignalEntity?>(null) }
    var showRiskCalculatorModal by remember { mutableStateOf(false) }
    val signalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val riskCalculatorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val categories = listOf(
        "ALL" to "همه بازارها",
        "OTC" to "بازارهای OTC (۲۴/۷)",
        "FOREX" to "جفت‌ارزهای فارکس",
        "CRYPTO" to "ارزهای دیجیتال",
        "COMMODITIES" to "طلا و نفت"
    )

    val filteredSignals = signals.filter { signal ->
        val catMatch = if (selectedCategory == "ALL") true else signal.category == selectedCategory
        val filterMatch = when (selectedFilter) {
            "FAVORITES" -> signal.isFavorite
            "ACTIVE" -> signal.status == "ACTIVE"
            "NO_TRADE" -> signal.direction == "NO_TRADE"
            "WON" -> signal.status == "WON"
            else -> true
        }
        val confMatch = signal.confidenceScore >= minConfidenceFilter
        val searchMatch = searchQuery.isBlank() ||
                signal.asset.contains(searchQuery, ignoreCase = true) ||
                signal.category.contains(searchQuery, ignoreCase = true)

        catMatch && filterMatch && confMatch && searchMatch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Iran Binary Option",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = CyanGlow
                    )
                    Text(
                        text = "داشبورد سیگنال‌های معاملاتی هوشمند",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Iranian Markets Hub Button
                    IconButton(
                        onClick = onOpenMarkets,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "مارکت‌های ایرانی (بازار، مایکت، ایران اپس)",
                            tint = CyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EmeraldDark)
                            .clickable(onClick = onOpenSubscriptions)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = AmberGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "پلن: $userPlan",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Onboarding Tutorial Action Button
                    IconButton(
                        onClick = onOpenTutorial,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "راهنمای تصویری ترید و خواندن سیگنال‌ها",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notification Settings (DataStore) & FCM Live Status
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                            .testTag("open_settings_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = EmeraldNeon,
                                    modifier = Modifier.size(6.dp)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "تنظیمات نوتیفیکیشن‌ها و دسته‌بندی‌ها (DataStore)",
                                tint = EmeraldNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Encyclopedia / 3 Educational Sections Header Button
                    IconButton(
                        onClick = onOpenArticles,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                            .testTag("open_encyclopedia_header_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AmberGold,
                                    modifier = Modifier.size(6.dp)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "دانشنامه جامع ۳ گانه (بخش‌های ۶، ۳۶ و ۶۷ تایی)",
                                tint = CyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Trade Journal (Personal Logs & Analytics) Header Button
                    IconButton(
                        onClick = onOpenTradeJournal,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                            .testTag("open_trade_journal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "ژورنال و دفترچه معاملات شخصی",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Signal History (Room Database) Header Button
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "تاریخچه سیگنال‌های دیتابیس Room",
                            tint = CyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Submit Feedback / Inaccuracy Dialog Button
                    IconButton(
                        onClick = {
                            targetFeedbackSignal = null
                            showFeedbackDialog = true
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                            .testTag("open_feedback_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = "ثبت بازخورد یا گزارش خطای سیگنال",
                            tint = AmberGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenSupport,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "پشتیبانی ۲۴ ساعته",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Brand Logomotion Card with official slogans
        item {
            BrandLogomotion(
                compact = false,
                showMotto = true,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 2. Featured AI Trading Visual Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                EmeraldNeon.copy(alpha = 0.8f),
                                CyanNeon.copy(alpha = 0.8f),
                                AmberGold.copy(alpha = 0.6f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_trading_banner),
                        contentDescription = "Iran Binary Option AI Terminal Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SlateDark950.copy(alpha = 0.45f),
                                        SlateDark950.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // AI Live Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SlateDark900.copy(alpha = 0.85f))
                                    .border(1.dp, EmeraldNeon, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldNeon)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "موتور پردازش هوش مصنوعی آنلاین",
                                        color = EmeraldGlow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AmberGold.copy(alpha = 0.25f))
                                    .border(0.8.dp, AmberGold, RoundedCornerShape(12.dp))
                                    .clickable(onClick = onOpenSubscriptions)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ارتقاء اشتراک ⭐️",
                                    color = AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Hero Slogan & Call-to-action
                        Column {
                            Text(
                                text = "ایران باینری آپشن • AI Trading Signals",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "اولین پلتفرم تخصصی سیگنال‌دهی نوسان‌گیری باینری آپشن در ایران",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. 15+ Binary Option Brokers Timeline Marquee Ribbon
        item {
            BrokerTimelineMarquee(
                brokers = brokers,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        // 3.5. Quick Action Shortcuts Row (4 Primary Trading Utilities)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action 1: Risk Calculator
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateDark900)
                        .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { showRiskCalculatorModal = true }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("محاسبه ریسک", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("مدیریت سرمایه", color = TextMuted, fontSize = 9.sp)
                    }
                }

                // Action 2: Trade Journal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateDark900)
                        .border(1.dp, EmeraldNeon.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onOpenTradeJournal() }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(EmeraldNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ژورنال ترید", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("ثبت و آنالیز", color = TextMuted, fontSize = 9.sp)
                    }
                }

                // Action 3: Signal History
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateDark900)
                        .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onOpenHistory() }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("تاریخچه Room", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("آرشیو آفلاین", color = TextMuted, fontSize = 9.sp)
                    }
                }

                // Action 4: Encyclopedia
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateDark900)
                        .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onOpenArticles() }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanGlow.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("دانشنامه", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("۱۰۹ سرفصل", color = TextMuted, fontSize = 9.sp)
                    }
                }
            }
        }

        // 4. Performance & Safety Analytics Ribbon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Win Rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("وین‌ریت میانگین", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("۸۷.۴٪", color = EmeraldGlow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("در تایم‌فریم‌های ۱ و ۵ دقیقه", color = TextSecondary, fontSize = 9.sp)
                    }
                }

                // Metric 2: Live signals
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سیگنال‌های لایو", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${signals.size} سیگنال", color = CyanGlow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("پایش همزمان ۱۵ اکسچنج", color = TextSecondary, fontSize = 9.sp)
                    }
                }

                // Metric 3: No Trade Veto Protection
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فیلتر No Trade", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("فعال و ایمن", color = AmberGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text("جلوگیری از اسپرد سنگین", color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }

        // 4.5 Iranian Markets Fast Hub Banner (Cafe Bazaar, Myket, IranApps)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenMarkets),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateDark900)
                                .border(1.dp, EmeraldNeon.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = EmeraldGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "استورهای ایرانی",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "بازار • مایکت • ایران اپس",
                                    color = CyanGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "به‌روزرسانی سریع، اصالت بسته و ثبت نظر ۵ ستاره",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldDark)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "مشاهده استورها ⬅️",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 5. Advanced Signal Multi-Filter Bar (Asset Class, Win Probability %, Status, Search)
        item {
            SignalFilterBar(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                minConfidence = minConfidenceFilter,
                onMinConfidenceSelected = { minConfidenceFilter = it },
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                totalResultsCount = filteredSignals.size,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 7. Signals List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "سیگنال‌های هوشمند باینری آپشن (${filteredSignals.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                Text(
                    text = "تست صفحه ۴۰۴",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted,
                    modifier = Modifier.clickable(onClick = onOpenNotFoundTest)
                )
            }
        }

        // 8. Live Signal Items
        if (filteredSignals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 30.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedFilter == "FAVORITES") Icons.Default.Bookmark else Icons.Default.FilterList,
                            contentDescription = null,
                            tint = if (selectedFilter == "FAVORITES") AmberGold else TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedFilter == "FAVORITES")
                                "هیچ سیگنالی نشان نشده است. برای ذخیره سیگنال‌های با احتمال بالا جهت بررسی سریع بعدی، آیکون نشان کردن 🔖 روی کارت‌ها را لمس کنید."
                            else "سیگنالی در این فیلتر یافت نشد",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredSignals, key = { it.id }) { signal ->
                SignalCard(
                    signal = signal,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { sig ->
                        selectedSignalForSheet = sig
                    },
                    onReportClick = { sig ->
                        targetFeedbackSignal = sig
                        showFeedbackDialog = true
                    },
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }

    if (selectedSignalForSheet != null) {
        SignalDetailBottomSheet(
            signal = selectedSignalForSheet,
            sheetState = signalSheetState,
            onDismiss = {
                selectedSignalForSheet = null
            },
            onLogToJournal = { sig ->
                selectedSignalForSheet = null
                quickJournalSignal = sig
            },
            onReportFeedback = { sig ->
                selectedSignalForSheet = null
                targetFeedbackSignal = sig
                showFeedbackDialog = true
            }
        )
    }

    if (quickJournalSignal != null) {
        QuickJournalLogDialog(
            signal = quickJournalSignal!!,
            onDismiss = { quickJournalSignal = null },
            onConfirmLog = { tradeLog ->
                onAddTradeLog?.invoke(tradeLog)
                quickJournalSignal = null
                Toast.makeText(context, "معامله با موفقیت در ژورنال ذخیره شد.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showRiskCalculatorModal) {
        SmartRiskCalculatorModal(
            sheetState = riskCalculatorSheetState,
            onDismiss = { showRiskCalculatorModal = false }
        )
    }

    if (showFeedbackDialog) {
        SubmitFeedbackDialog(
            initialSignal = targetFeedbackSignal,
            userEmail = null,
            availableSignals = signals,
            onDismiss = {
                showFeedbackDialog = false
                targetFeedbackSignal = null
            },
            onSubmit = { type, asset, signalId, reason, desc, rating, contact ->
                showFeedbackDialog = false
                targetFeedbackSignal = null
                onSubmitFeedback?.invoke(type, asset, signalId, reason, desc, rating, contact)
            }
        )
    }
}


