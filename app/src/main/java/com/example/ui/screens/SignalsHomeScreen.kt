package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
import com.example.data.repository.BrokerItem
import com.example.ui.components.BrandHeader
import com.example.ui.components.BrokerTimelineMarquee
import com.example.ui.components.SignalCard
import com.example.ui.components.SignalDetailsModalSheet
import com.example.ui.components.SmartRiskCalculatorModal
import com.example.ui.components.SubmitFeedbackDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalsHomeScreen(
    signals: List<SignalEntity>,
    brokers: List<BrokerItem>,
    userPlan: String,
    tradeLogs: List<com.example.data.local.TradeLogEntity> = emptyList(),
    // Part 3 B8: offline-cache clarity — when false, cached (possibly outdated)
    // signals are explicitly labeled in the feed instead of looking live.
    isOnline: Boolean = true,
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
    // 0: خانه (Home Dashboard), 1: سیگنال‌ها (Signals Feed), 2: عملکرد (Performance Analytics), 3: ابزارها (Tools)
    var activeBottomTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var targetFeedbackSignal by remember { mutableStateOf<SignalEntity?>(null) }
    var selectedSignalForDetail by remember { mutableStateOf<SignalEntity?>(null) }
    var showRiskCalculatorModal by remember { mutableStateOf(false) }
    val riskCalculatorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val categories = listOf(
        "ALL" to "همه",
        "FOREX" to "فارکس",
        "OTC" to "OTC",
        "CRYPTO" to "کریپتو",
        "COMMODITIES" to "طلا"
    )

    val filteredSignals = signals.filter { signal ->
        val catMatch = if (selectedCategory == "ALL") true else signal.category == selectedCategory
        val favMatch = if (showOnlyFavorites) signal.isFavorite else true
        val filterMatch = when (selectedFilter) {
            "FAVORITES" -> signal.isFavorite
            "ACTIVE" -> signal.status == "ACTIVE"
            "NO_TRADE" -> signal.direction == "NO_TRADE"
            "WON" -> signal.status == "WON"
            else -> true
        }
        catMatch && favMatch && filterMatch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftUiBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // =========================================================================
            // 1. هدر مشترک و زیبای نئومورفیک نرم
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftUiBg)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // دکمه منوی همبرگری
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SoftUiSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "منو",
                        tint = TextDarkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // عنوان وسط بر اساس تب فعال
                Text(
                    text = when (activeBottomTab) {
                        0 -> "داشبورد معاملات هوشمند"
                        1 -> "آپشن‌های معاملاتی"
                        2 -> "عملکرد من"
                        else -> "ابزارهای تحلیلی"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    ),
                    color = TextDarkPrimary
                )

                // دکمه اعلان یا ستاره علاقه‌مندی
                IconButton(
                    onClick = {
                        if (activeBottomTab == 1) {
                            showOnlyFavorites = !showOnlyFavorites
                        } else {
                            onOpenSubscriptions()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SoftUiSurface)
                ) {
                    Icon(
                        imageVector = if (activeBottomTab == 1 && showOnlyFavorites) Icons.Default.Star
                        else if (activeBottomTab == 1) Icons.Default.StarBorder
                        else Icons.Default.Notifications,
                        contentDescription = "اعلان و علاقه‌مندی",
                        tint = if (activeBottomTab == 1 && showOnlyFavorites) CanaryYellowDark else TextDarkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // نوار انتخاب بخش‌های داخلی صفحه اصلی (داشبورد، سیگنال‌ها، عملکرد، ابزارها)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftUiSurface)
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val subTabs = listOf(
                    0 to "داشبورد",
                    1 to "سیگنال‌ها",
                    2 to "عملکرد",
                    3 to "ابزارها"
                )
                subTabs.forEach { (index, title) ->
                    val isSelected = activeBottomTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SoftCardPurpleAccent else Color.Transparent)
                            .clickable { activeBottomTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextDarkSecondary
                        )
                    }
                }
            }

            // =========================================================================
            // 2. بدنه صفحه (۴ تب تفکیک‌شده و هماهنگ)
            // =========================================================================
            when (activeBottomTab) {
                // ------------------ تب 0: صفحه اصلی (خانه - Home Dashboard) ------------------
                0 -> {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            BrandHeader(compact = true, showMotto = true)
                        }
                        // ۱. کارت هیرو و موجودی / سود با گرادیان نرم یاسی
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(24.dp))
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Brush.horizontalGradient(SoftCardPurpleGradient))
                                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(24.dp))
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("خوش آمدید، تریدر محترم", fontSize = 12.sp, color = TextDarkSecondary)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("IBO Trading AI", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextDarkPrimary)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .clickable { onOpenSubscriptions() }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Diamond, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(userPlan, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextDarkPrimary)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // خلاصه آمار هیرو
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("سود خالص هفتگی", fontSize = 10.5.sp, color = TextDarkSecondary)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("+۲,۴۵۰,۰۰۰ تومان", fontWeight = FontWeight.Black, fontSize = 14.sp, color = SoftCardBlueAccent)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("ضریب موفقیت AI", fontSize = 10.5.sp, color = TextDarkSecondary)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("۸۲.۴٪", fontWeight = FontWeight.Black, fontSize = 14.sp, color = SoftCardMintAccent)
                                        }
                                    }
                                }
                            }
                        }

                        // ۲. سیگنال ویژه و طلایی روز (Hot Signal Feature)
                        if (signals.isNotEmpty()) {
                            val hotSignal = signals.maxByOrNull { it.confidenceScore } ?: signals.first()
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("سیگنال برگزیده هوش مصنوعی (VIP)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                        Text("مشاهده همه >", fontSize = 11.sp, color = SoftCardPurpleAccent, modifier = Modifier.clickable { activeBottomTab = 1 })
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SignalCard(
                                        signal = hotSignal,
                                        onClick = { selectedSignalForDetail = it },
                                        onToggleFavorite = onToggleFavorite,
                                        onReportClick = {
                                            targetFeedbackSignal = it
                                            showFeedbackDialog = true
                                        }
                                    )
                                }
                            }
                        }

                        // ۳. میانبرهای سریع ۴ گانه نئومورفیک
                        item {
                            Text("دسترسی سریع و امکانات", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // دکمه ۱: سیگنال‌های لایو
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SoftUiSurface)
                                        .clickable { activeBottomTab = 1 }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(SoftCardCyanGradient[0]),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("سیگنال‌ها", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextDarkPrimary)
                                    }
                                }

                                // دکمه ۲: ماشین حساب ریسک
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SoftUiSurface)
                                        .clickable { showRiskCalculatorModal = true }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(SoftCardMintGradient[0]),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Calculate, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("مدیریت ریسک", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextDarkPrimary)
                                    }
                                }

                                // دکمه ۳: ژورنال ترید
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SoftUiSurface)
                                        .clickable { onOpenTradeJournal() }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(SoftCardPurpleGradient[0]),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Assessment, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("ژورنال ترید", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextDarkPrimary)
                                    }
                                }

                                // دکمه ۴: آکادمی
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SoftUiSurface)
                                        .clickable { onOpenArticles() }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(SoftCardPeachGradient[0]),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("آموزش‌ها", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextDarkPrimary)
                                    }
                                }
                            }
                        }

                        // ۴. نوار اسکرول بروکرهای باینری آپشن
                        item {
                            Text("بروکرهای متصل و پشتیبانی‌شده", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            BrokerTimelineMarquee(brokers = brokers)
                        }

                        // ۵. آخرین سیگنال‌های دریافتی
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("جدیدترین سیگنال‌های لحظه‌ای", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                if (!isOnline) {
                                    // Part 3 B8: stale-signal warning — cached data must never
                                    // look like fresh market signals (financial-loss risk).
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AmberGold.copy(alpha = 0.15f))
                                            .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("signals_cached_banner")
                                    ) {
                                        Text(
                                            text = "⚠️ آفلاین — داده‌های کش‌شده، ممکن است قدیمی باشند",
                                            color = AmberGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(signals.take(4), key = { it.id }) { sig ->
                            SignalCard(
                                signal = sig,
                                onClick = { selectedSignalForDetail = it },
                                onToggleFavorite = onToggleFavorite,
                                onReportClick = {
                                    targetFeedbackSignal = it
                                    showFeedbackDialog = true
                                }
                            )
                        }
                    }
                }

                // ------------------ تب 1: لیست کامل سیگنال‌ها (طرح ستون ۱ عکس) ------------------
                1 -> {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            BrandHeader(compact = true, showMotto = false)
                        }
                        // ردیف وضعیت اشتراک VIP و پلن
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shadow(3.dp, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SoftUiSurface)
                                        .clickable { onOpenSubscriptions() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Diamond, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("اشتراک VIP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .shadow(3.dp, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SoftUiSurface)
                                        .clickable { onOpenSubscriptions() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(SoftCardMintAccent)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("پلن فعال: $userPlan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                    }
                                }
                            }
                        }

                        // ردیف فیلتر کپسولی دسته‌بندی‌ها
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { (catKey, catTitle) ->
                                    val isSelected = selectedCategory == catKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp))
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) SoftCardPurpleAccent else SoftUiSurface)
                                            .clickable { selectedCategory = catKey }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = catTitle,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextDarkSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // آیتم‌های سیگنال‌ها (کارت‌های گرادیانتی نئومورفیک پاستلی)
                        if (filteredSignals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 30.dp)
                                        .shadow(4.dp, RoundedCornerShape(20.dp))
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SoftUiSurface)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextDarkMuted, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("سیگنالی در این دسته یافت نشد", color = TextDarkSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            items(filteredSignals, key = { it.id }) { signal ->
                                SignalCard(
                                    signal = signal,
                                    onClick = { sig ->
                                        selectedSignalForDetail = sig
                                    },
                                    onToggleFavorite = onToggleFavorite,
                                    onReportClick = { sig ->
                                        targetFeedbackSignal = sig
                                        showFeedbackDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                // ------------------ تب 2: عملکرد من و آمار (طرح دقیق ستون ۳ عکس) ------------------
                2 -> {
                    val totalTrades = tradeLogs.size
                    val winCount = tradeLogs.count { it.result.equals("WIN", true) || it.result.equals("WON", true) }
                    val lossCount = tradeLogs.count { it.result.equals("LOSS", true) || it.result.equals("LOST", true) }
                    val totalClosed = winCount + lossCount
                    val winRateFloat = if (totalClosed > 0) (winCount.toFloat() / totalClosed.toFloat()) else 0f
                    val sweepAngle = 360f * winRateFloat
                    val winRateText = "${(winRateFloat * 100).toInt()}%"
                    val netProfitLoss = tradeLogs.sumOf { it.profitOrLoss }
                    val formattedPnL = (if (netProfitLoss >= 0) "+" else "") + String.format(java.util.Locale.US, "$%.2f", netProfitLoss)
                    val pnlColor = if (netProfitLoss >= 0) SoftCardBlueAccent else SoftCardPeachAccent

                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            BrandHeader(compact = true, showMotto = false)
                        }
                        // ۱. چارت دونات واقعی بر اساس وین ریت
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(26.dp))
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(SoftUiSurface)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier.size(170.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeWidth = 18.dp.toPx()
                                        // حلقه پس‌زمینه خاکستری لطیف
                                        drawCircle(
                                            color = SoftUiBg,
                                            style = Stroke(width = strokeWidth)
                                        )
                                        // کمان گرادیانتی دونات
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                listOf(
                                                    SoftChartDonutBlue,
                                                    SoftChartDonutPink,
                                                    SoftChartDonutPurple,
                                                    SoftChartDonutBlue
                                                )
                                            ),
                                            startAngle = -90f,
                                            sweepAngle = sweepAngle,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = winRateText,
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 32.sp
                                            ),
                                            color = TextDarkPrimary
                                        )
                                        Text(
                                            text = "وین‌ریت عملکرد",
                                            fontSize = 11.sp,
                                            color = TextDarkSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // ۲. ردیف‌های آماری نئومورفیک نرم (کل معاملات، حق‌العمل، ناموفق، موفق)
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    // سطر ۱: کل معاملات
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("کل معاملات", color = TextDarkSecondary, fontSize = 13.5.sp)
                                        Text(totalTrades.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDarkPrimary)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SoftUiBg)

                                    // سطر ۲: حق‌العمل / سود خالص
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("سود/زیان خالص", color = TextDarkSecondary, fontSize = 13.5.sp)
                                        Text(formattedPnL, fontWeight = FontWeight.Black, fontSize = 16.sp, color = pnlColor)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SoftUiBg)

                                    // سطر ۳: معاملات موفق
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("معاملات موفق (Win)", color = TextDarkSecondary, fontSize = 13.5.sp)
                                        Text(winCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SoftCardMintAccent)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SoftUiBg)

                                    // سطر ۴: معاملات ناموفق
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("معاملات ناموفق (Loss)", color = TextDarkSecondary, fontSize = 13.5.sp)
                                        Text(lossCount.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SoftCardPeachAccent)
                                    }
                                }
                            }
                        }

                        // ۳. نمودار میله‌ای هفتگی (Weekly Bar Chart)
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp)
                                ) {
                                    Text(
                                        text = "روند معاملاتی هفته جاری",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDarkPrimary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // میله‌های چارت
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        val days = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
                                        
                                        val activityCounts = IntArray(7) { 0 }
                                        val calendar = java.util.Calendar.getInstance()
                                        tradeLogs.forEach { log ->
                                            calendar.timeInMillis = log.timestamp
                                            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                                            val mappedIdx = when (dayOfWeek) {
                                                java.util.Calendar.SATURDAY -> 0
                                                java.util.Calendar.SUNDAY -> 1
                                                java.util.Calendar.MONDAY -> 2
                                                java.util.Calendar.TUESDAY -> 3
                                                java.util.Calendar.WEDNESDAY -> 4
                                                java.util.Calendar.THURSDAY -> 5
                                                java.util.Calendar.FRIDAY -> 6
                                                else -> 0
                                            }
                                            activityCounts[mappedIdx]++
                                        }
                                        
                                        val maxActivity = activityCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
                                        val heights = activityCounts.map { (it.toFloat() / maxActivity.toFloat()).coerceAtLeast(0.15f) }
                                        
                                        val colors = listOf(
                                            SoftCardPurpleAccent,
                                            SoftCardRoseAccent,
                                            SoftCardBlueAccent,
                                            SoftCardMintAccent,
                                            SoftCardPeachAccent,
                                            SoftCardPurpleAccent,
                                            SoftCardBlueAccent
                                        )

                                        days.forEachIndexed { idx, day ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Bottom,
                                                modifier = Modifier.fillMaxHeight()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(16.dp)
                                                        .fillMaxHeight(heights[idx])
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(colors[idx])
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(day, fontSize = 10.sp, color = TextDarkSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ------------------ تب 3: ابزارهای معاملاتی و هوشمند ------------------
                3 -> {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            BrandHeader(compact = true, showMotto = false)
                        }
                        item {
                            Text(
                                text = "ابزارهای تخصصی ترید باینری آپشن",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextDarkPrimary
                            )
                        }

                        // ابزار ۱: ماشین حساب هوشمند ریسک
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.horizontalGradient(SoftCardMintGradient))
                                    .clickable { showRiskCalculatorModal = true }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Calculate, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("ماشین‌حساب هوشمند مدیریت سرمایه و مارتینگل", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                        Text("محاسبه اندازه پوزیشن، پله‌های مارتینگل و ضریب ریسک", fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                }
                            }
                        }

                        // ابزار ۲: ژورنال شخصی معاملات
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.horizontalGradient(SoftCardPurpleGradient))
                                    .clickable { onOpenTradeJournal() }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Assessment, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("دفترچه ژورنال و تحلیل عملکرد تریدها", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                        Text("ثبت و بررسی علت بردها و باخت‌ها برای ارتقای استراتژی", fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                }
                            }
                        }

                        // ابزار ۳: آکادمی و دانشنامه
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.horizontalGradient(SoftCardBlueGradient))
                                    .clickable { onOpenArticles() }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = SoftCardBlueAccent, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("دانشنامه و استراتژی‌های باینری آپشن", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                        Text("آموزش کندل‌استیک، سطوح حمایت و مقاومت و پرایس اکشن", fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                }
                            }
                        }

                        // ابزار ۴: پشتیبانی ۲۴/۷
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.horizontalGradient(SoftCardPeachGradient))
                                    .clickable { onOpenSupport() }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text("چت آنلاین پشتیبانی ۲۴/۷ تریدرها", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDarkPrimary)
                                        Text("پاسخگویی سریع کارشناسان به سوالات و مشکلات فعال‌سازی", fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    // =========================================================================
    // 4. مودال جزئیات سیگنال (طرح دقیق ستون وسط تصویر ارسالی کاربر)
    // =========================================================================
    if (selectedSignalForDetail != null) {
        SignalDetailsModalSheet(
            signal = selectedSignalForDetail!!,
            onDismiss = { selectedSignalForDetail = null },
            onToggleFavorite = {
                onToggleFavorite?.invoke(selectedSignalForDetail!!)
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

@Composable
fun BottomTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) accentColor else TextDarkMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) accentColor else TextDarkMuted
        )
    }
}
