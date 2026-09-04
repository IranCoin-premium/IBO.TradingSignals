package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.TradeLogEntity
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeJournalScreen(
    tradeLogs: List<TradeLogEntity>,
    onAddTradeLog: (TradeLogEntity) -> Unit,
    onUpdateTradeLog: (TradeLogEntity) -> Unit,
    onDeleteTradeLog: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Trades List, 1: Analytics
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTrade by remember { mutableStateOf<TradeLogEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedPhotoFullscreen by remember { mutableStateOf<String?>(null) }

    // Search and Filter State
    var searchQuery by remember { mutableStateOf("") }
    var filterResult by remember { mutableStateOf("ALL") } // "ALL", "WIN", "LOSS"
    var filterBroker by remember { mutableStateOf("ALL") }

    // Performance Calculations
    val totalTrades = tradeLogs.size
    val winTrades = tradeLogs.filter { it.result == "WIN" }
    val lossTrades = tradeLogs.filter { it.result == "LOSS" }
    val tieTrades = tradeLogs.filter { it.result == "TIE" }

    val winCount = winTrades.size
    val lossCount = lossTrades.size
    val totalClosed = winCount + lossCount
    val winRate = if (totalClosed > 0) (winCount.toDouble() / totalClosed.toDouble()) * 100.0 else 0.0

    val totalProfit = winTrades.sumOf { it.profitOrLoss }
    val totalLoss = lossTrades.sumOf { kotlin.math.abs(it.profitOrLoss) }
    val netProfitLoss = tradeLogs.sumOf { it.profitOrLoss }
    val profitFactor = if (totalLoss > 0.0) totalProfit / totalLoss else if (totalProfit > 0.0) 99.9 else 0.0

    val filteredLogs = tradeLogs.filter { log ->
        val matchesResult = filterResult == "ALL" || log.result == filterResult
        val matchesBroker = filterBroker == "ALL" || log.broker == filterBroker
        val matchesSearch = searchQuery.isBlank() ||
                log.asset.contains(searchQuery, ignoreCase = true) ||
                log.notes.contains(searchQuery, ignoreCase = true) ||
                log.strategy.contains(searchQuery, ignoreCase = true) ||
                log.broker.contains(searchQuery, ignoreCase = true)
        matchesResult && matchesBroker && matchesSearch
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateDark950,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldNeon,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_trade_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "ثبت ترید")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ثبت معامله جدید", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ژورنال و دفترچه معاملات (Trade Journal)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "ثبت دستی تریدها، الصاق اسکرین‌شات چارت و تحلیل عملکرد",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                if (tradeLogs.isNotEmpty()) {
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark900)
                            .testTag("clear_journal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "پاک کردن ژورنال",
                            tint = CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Tabs: Trades List vs Performance Analytics
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateDark900,
                contentColor = EmeraldNeon,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldNeon,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("لیست معاملات (${tradeLogs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = EmeraldGlow,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تحلیل و آمار عملکرد", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = CyanGlow,
                    unselectedContentColor = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                // Trades List Tab
                TradesListContent(
                    tradeLogs = filteredLogs,
                    totalLogsCount = tradeLogs.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    filterResult = filterResult,
                    onFilterResultChange = { filterResult = it },
                    filterBroker = filterBroker,
                    onFilterBrokerChange = { filterBroker = it },
                    onEditTrade = { editingTrade = it },
                    onDeleteTrade = { id ->
                        onDeleteTradeLog(id)
                        Toast.makeText(context, "معامله با موفقیت حذف شد.", Toast.LENGTH_SHORT).show()
                    },
                    onPhotoClick = { uri -> selectedPhotoFullscreen = uri },
                    onOpenAddDialog = { showAddDialog = true }
                )
            } else {
                // Performance Analytics Tab
                PerformanceAnalyticsContent(
                    totalTrades = totalTrades,
                    winCount = winCount,
                    lossCount = lossCount,
                    tieCount = tieTrades.size,
                    winRate = winRate,
                    totalProfit = totalProfit,
                    totalLoss = totalLoss,
                    netProfitLoss = netProfitLoss,
                    profitFactor = profitFactor,
                    tradeLogs = tradeLogs
                )
            }
        }
    }

    // Add / Edit Trade Dialog
    if (showAddDialog || editingTrade != null) {
        TradeEntryDialog(
            initialTrade = editingTrade,
            onDismiss = {
                showAddDialog = false
                editingTrade = null
            },
            onSave = { trade ->
                if (editingTrade != null) {
                    onUpdateTradeLog(trade)
                    Toast.makeText(context, "معامله با موفقیت ویرایش شد.", Toast.LENGTH_SHORT).show()
                } else {
                    onAddTradeLog(trade)
                    Toast.makeText(context, "معامله جدید در ژورنال ثبت شد.", Toast.LENGTH_SHORT).show()
                }
                showAddDialog = false
                editingTrade = null
            }
        )
    }

    // Clear Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "پاکسازی کامل ژورنال معاملات",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "آیا از حذف تمام تاریخچه و لاگ‌های ثبت‌شده در ژورنال اطمینان دارید؟ این عمل غیرقابل بازگشت است.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "تمام لاگ‌های ژورنال حذف شدند.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text("بله، همه را پاک کن", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Fullscreen Photo Viewer Dialog
    if (selectedPhotoFullscreen != null) {
        Dialog(onDismissRequest = { selectedPhotoFullscreen = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateDark950)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسکرین‌شات و تصویر چارت پیوست",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        IconButton(onClick = { selectedPhotoFullscreen = null }) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                        }
                    }

                    AsyncImage(
                        model = selectedPhotoFullscreen,
                        contentDescription = "چارت معامله",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun TradesListContent(
    tradeLogs: List<TradeLogEntity>,
    totalLogsCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterResult: String,
    onFilterResultChange: (String) -> Unit,
    filterBroker: String,
    onFilterBrokerChange: (String) -> Unit,
    onEditTrade: (TradeLogEntity) -> Unit,
    onDeleteTrade: (Long) -> Unit,
    onPhotoClick: (String) -> Unit,
    onOpenAddDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Filter
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_search_field"),
                    placeholder = {
                        Text(
                            text = "جستجو در دارایی، یادداشت‌ها، استراتژی و بروکر...",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = SlateDark900,
                        unfocusedContainerColor = SlateDark900,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                // Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ResultFilterChip(
                            label = "همه (${totalLogsCount})",
                            isSelected = filterResult == "ALL",
                            activeColor = CyanNeon,
                            onClick = { onFilterResultChange("ALL") }
                        )
                        ResultFilterChip(
                            label = "🟢 برنده (WIN)",
                            isSelected = filterResult == "WIN",
                            activeColor = EmeraldNeon,
                            onClick = { onFilterResultChange("WIN") }
                        )
                        ResultFilterChip(
                            label = "🔴 بازنده (LOSS)",
                            isSelected = filterResult == "LOSS",
                            activeColor = CrimsonRed,
                            onClick = { onFilterResultChange("LOSS") }
                        )
                    }

                    Text(
                        text = "${tradeLogs.size} مورد",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Empty state
        if (tradeLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateDark900)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "هنوز معامله‌ای در این بخش ثبت نشده است",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "با ثبت معاملات روزانه و پیوست تصویر چارت، اشتباهات تحلیلی را کاهش داده و وین‌ریت خود را ارتقا دهید.",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Button(
                            onClick = onOpenAddDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ ثبت اولین معامله", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Trade Cards
        items(tradeLogs, key = { it.id }) { log ->
            TradeLogCard(
                trade = log,
                onEdit = { onEditTrade(log) },
                onDelete = { onDeleteTrade(log.id) },
                onPhotoClick = { log.photoUri?.let(onPhotoClick) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}

@Composable
private fun TradeLogCard(
    trade: TradeLogEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPhotoClick: () -> Unit
) {
    val isWin = trade.result == "WIN"
    val isLoss = trade.result == "LOSS"
    val resultColor = when {
        isWin -> EmeraldNeon
        isLoss -> CrimsonRed
        else -> AmberGold
    }

    val isCall = trade.direction == "CALL"
    val directionColor = if (isCall) EmeraldNeon else CrimsonRed

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault()) }
    val formattedDate = remember(trade.timestamp) { dateFormat.format(Date(trade.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .testTag("trade_card_${trade.id}"),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Asset + Direction + Result Badge + P/L
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(directionColor.copy(alpha = 0.18f))
                            .border(1.dp, directionColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCall) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = directionColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isCall) "CALL (خرید)" else "PUT (فروش)",
                                color = directionColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = trade.asset,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.5.sp
                    )
                }

                // Profit / Loss Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(resultColor.copy(alpha = 0.15f))
                        .border(1.dp, resultColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (trade.result) {
                                "WIN" -> "+$${String.format(Locale.US, "%.2f", trade.profitOrLoss)}"
                                "LOSS" -> "-$${String.format(Locale.US, "%.2f", kotlin.math.abs(trade.profitOrLoss))}"
                                else -> "$0.00 (سربه‌سر)"
                            },
                            color = resultColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Broker, Amount, Payout %, Expiry, Prices
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SlateDark900)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "بروکر: ${trade.broker}", color = TextSecondary, fontSize = 10.5.sp)
                    Text(text = "حجم: $${trade.tradeAmount}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "سود بروکر: ${trade.payoutPercent}%", color = TextSecondary, fontSize = 10.5.sp)
                    Text(text = "انقضا: ${trade.expiry}", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                if (trade.entryPrice.isNotBlank() || trade.exitPrice.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (trade.entryPrice.isNotBlank()) {
                            Text(text = "ورود: ${trade.entryPrice}", color = TextSecondary, fontSize = 10.sp)
                        }
                        if (trade.exitPrice.isNotBlank()) {
                            Text(text = "خروج: ${trade.exitPrice}", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Strategy & Psychology Tag
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateDark800)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberGold, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = trade.strategy, color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateDark800)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = trade.emotionalState, color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Attached Photo & Notes
            if (trade.photoUri != null || trade.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    if (trade.photoUri != null) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                .clickable(onClick = onPhotoClick)
                        ) {
                            AsyncImage(
                                model = trade.photoUri,
                                contentDescription = "چارت پیوست",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(2.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                    }

                    if (trade.notes.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateDark900)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = trade.notes,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Footer: Timestamp & Action Buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = CyanNeon, modifier = Modifier.size(15.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CrimsonRed, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceAnalyticsContent(
    totalTrades: Int,
    winCount: Int,
    lossCount: Int,
    tieCount: Int,
    winRate: Double,
    totalProfit: Double,
    totalLoss: Double,
    netProfitLoss: Double,
    profitFactor: Double,
    tradeLogs: List<TradeLogEntity>
) {
    val isNetProfitable = netProfitLoss >= 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // KPI Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateDark900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "خلاصه بازدهی و سودآوری کل",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isNetProfitable) "+$${String.format(Locale.US, "%.2f", netProfitLoss)}"
                            else "-$${String.format(Locale.US, "%.2f", kotlin.math.abs(netProfitLoss))}",
                            color = if (isNetProfitable) EmeraldNeon else CrimsonRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isNetProfitable) EmeraldNeon.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isNetProfitable) "سوددهی مثبت" else "نیاز به بازنگری مدیریت ریسک",
                                color = if (isNetProfitable) EmeraldNeon else CrimsonRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Grid Metrics
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricBox(
                            title = "وین‌ریت (Win Rate)",
                            value = "${String.format(Locale.US, "%.1f", winRate)}%",
                            subtext = "$winCount برد / $lossCount باخت",
                            highlightColor = if (winRate >= 60.0) EmeraldNeon else AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "فاکتور سود (PF)",
                            value = String.format(Locale.US, "%.2f", profitFactor),
                            subtext = if (profitFactor >= 1.5) "عالی" else "متوسط",
                            highlightColor = CyanNeon,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricBox(
                            title = "کل سود ناخالص",
                            value = "+$${String.format(Locale.US, "%.1f", totalProfit)}",
                            subtext = "از $winCount معامله موفق",
                            highlightColor = EmeraldGlow,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "کل زیان ناخالص",
                            value = "-$${String.format(Locale.US, "%.1f", totalLoss)}",
                            subtext = "از $lossCount معامله ناموفق",
                            highlightColor = CrimsonGlow,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Win / Loss Visual Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "نسبت بردهای باینری به کل معاملات",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    val winFraction = if (totalTrades > 0) (winCount.toFloat() / totalTrades.toFloat()) else 0f
                    val lossFraction = if (totalTrades > 0) (lossCount.toFloat() / totalTrades.toFloat()) else 0f
                    val tieFraction = if (totalTrades > 0) (tieCount.toFloat() / totalTrades.toFloat()) else 0f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(SlateDark800)
                    ) {
                        if (winFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .weight(winFraction)
                                    .fillMaxSize()
                                    .background(EmeraldNeon)
                            )
                        }
                        if (tieFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .weight(tieFraction)
                                    .fillMaxSize()
                                    .background(AmberGold)
                            )
                        }
                        if (lossFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .weight(lossFraction)
                                    .fillMaxSize()
                                    .background(CrimsonRed)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldNeon))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("برد: $winCount ($${String.format(Locale.US, "%.1f", totalProfit)})", color = TextSecondary, fontSize = 10.5.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CrimsonRed))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("باخت: $lossCount ($${String.format(Locale.US, "%.1f", totalLoss)})", color = TextSecondary, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }

        // Breakdown by Asset
        item {
            val assetGroups = tradeLogs.groupBy { it.asset }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "عملکرد و بازدهی به تفکیک دارایی‌ها",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    assetGroups.forEach { (asset, logs) ->
                        val assetWins = logs.count { it.result == "WIN" }
                        val assetTotal = logs.size
                        val assetWinRate = if (assetTotal > 0) (assetWins.toDouble() / assetTotal.toDouble()) * 100.0 else 0.0
                        val assetPnL = logs.sumOf { it.profitOrLoss }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = asset, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$assetTotal ترید (وین‌ریت ${String.format(Locale.US, "%.0f", assetWinRate)}%)", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (assetPnL >= 0.0) "+$${String.format(Locale.US, "%.1f", assetPnL)}"
                                else "-$${String.format(Locale.US, "%.1f", kotlin.math.abs(assetPnL))}",
                                color = if (assetPnL >= 0.0) EmeraldNeon else CrimsonRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Breakdown by Strategy & Psychology
        item {
            val strategyGroups = tradeLogs.groupBy { it.strategy }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "کارآمدی استراتژی‌های معاملاتی",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    strategyGroups.forEach { (strategy, logs) ->
                        val stratWins = logs.count { it.result == "WIN" }
                        val stratTotal = logs.size
                        val stratWinRate = if (stratTotal > 0) (stratWins.toDouble() / stratTotal.toDouble()) * 100.0 else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = strategy, color = TextPrimary, fontSize = 11.5.sp)
                            Text(text = "${stratWins}/${stratTotal} موفق", color = CyanGlow, fontSize = 11.sp)
                            Text(
                                text = "${String.format(Locale.US, "%.0f", stratWinRate)}%",
                                color = if (stratWinRate >= 70.0) EmeraldNeon else AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtext: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateDark950)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(text = title, color = TextMuted, fontSize = 10.sp)
            Text(text = value, color = highlightColor, fontWeight = FontWeight.Black, fontSize = 14.5.sp)
            Text(text = subtext, color = TextSecondary, fontSize = 9.5.sp)
        }
    }
}

@Composable
private fun ResultFilterChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.18f) else SlateDark900)
            .border(1.dp, if (isSelected) activeColor else CardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) activeColor else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun TradeEntryDialog(
    initialTrade: TradeLogEntity?,
    onDismiss: () -> Unit,
    onSave: (TradeLogEntity) -> Unit
) {
    var asset by remember { mutableStateOf(initialTrade?.asset ?: "EUR/USD (OTC)") }
    var direction by remember { mutableStateOf(initialTrade?.direction ?: "CALL") }
    var result by remember { mutableStateOf(initialTrade?.result ?: "WIN") }
    var amountText by remember { mutableStateOf(initialTrade?.tradeAmount?.toString() ?: "20") }
    var payoutText by remember { mutableStateOf(initialTrade?.payoutPercent?.toString() ?: "88") }
    var broker by remember { mutableStateOf(initialTrade?.broker ?: "Pocket Option") }
    var expiry by remember { mutableStateOf(initialTrade?.expiry ?: "1m") }
    var entryPrice by remember { mutableStateOf(initialTrade?.entryPrice ?: "") }
    var exitPrice by remember { mutableStateOf(initialTrade?.exitPrice ?: "") }
    var strategy by remember { mutableStateOf(initialTrade?.strategy ?: "شکست سطح و پولبک") }
    var emotionalState by remember { mutableStateOf(initialTrade?.emotionalState ?: "منضبط و آرام") }
    var notes by remember { mutableStateOf(initialTrade?.notes ?: "") }
    var photoUri by remember { mutableStateOf<String?>(initialTrade?.photoUri) }

    // Android Photo Picker Launcher (Zero-permission Google Play compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri.toString()
        }
    }

    val quickAssets = listOf("EUR/USD (OTC)", "GBP/USD", "USD/JPY (OTC)", "GOLD", "BTC/USDT", "ETH/USDT")
    val quickBrokers = listOf("Pocket Option", "Quotex", "Deriv", "Olymp Trade", "Nadex", "دیگر")
    val quickExpiries = listOf("1m", "2m", "3m", "5m", "15m")
    val quickStrategies = listOf("شکست سطح و پولبک", "پرایس اکشن پین‌بار", "واگرایی RSI", "سیگنال هوش مصنوعی", "حمایت و مقاومت")
    val quickEmotions = listOf("منضبط و آرام", "طمع / هیجان‌زده", "انتقام‌جویانه (Revenge)", "تردید و اضطراب")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateDark950),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialTrade != null) "ویرایش معامله" else "ثبت معامله جدید در ژورنال",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                    }
                }

                // 1. Asset Input & Quick Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "دارایی معامله‌شده:", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = asset,
                        onValueChange = { asset = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = SlateDark900,
                            unfocusedContainerColor = SlateDark900,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickAssets) { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (asset == item) CyanNeon.copy(alpha = 0.2f) else SlateDark900)
                                    .border(1.dp, if (asset == item) CyanNeon else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { asset = item }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(item, color = if (asset == item) CyanGlow else TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // 2. Direction & Result (Two Column Row)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Direction
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "جهت ترید:", color = TextSecondary, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (direction == "CALL") EmeraldNeon.copy(alpha = 0.25f) else SlateDark900)
                                    .border(1.dp, if (direction == "CALL") EmeraldNeon else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { direction = "CALL" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🟢 CALL (خرید)", color = if (direction == "CALL") EmeraldNeon else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (direction == "PUT") CrimsonRed.copy(alpha = 0.25f) else SlateDark900)
                                    .border(1.dp, if (direction == "PUT") CrimsonRed else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { direction = "PUT" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔴 PUT (فروش)", color = if (direction == "PUT") CrimsonRed else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Result
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "نتیجه ترید:", color = TextSecondary, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (result == "WIN") EmeraldNeon.copy(alpha = 0.25f) else SlateDark900)
                                    .border(1.dp, if (result == "WIN") EmeraldNeon else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { result = "WIN" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("WIN (برد)", color = if (result == "WIN") EmeraldNeon else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (result == "LOSS") CrimsonRed.copy(alpha = 0.25f) else SlateDark900)
                                    .border(1.dp, if (result == "LOSS") CrimsonRed else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { result = "LOSS" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LOSS (باخت)", color = if (result == "LOSS") CrimsonRed else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 3. Amount & Payout Percent
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("مبلغ معامله ($)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = SlateDark900,
                            unfocusedContainerColor = SlateDark900,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = payoutText,
                        onValueChange = { payoutText = it },
                        label = { Text("درصد سود بروکر (%)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = SlateDark900,
                            unfocusedContainerColor = SlateDark900,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // 4. Broker & Expiry Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "بروکر انتخابی:", color = TextSecondary, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickBrokers) { b ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (broker == b) EmeraldDark else SlateDark900)
                                    .border(1.dp, if (broker == b) EmeraldNeon else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { broker = b }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(b, color = if (broker == b) EmeraldGlow else TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // 5. Strategy & Emotional State
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "استراتژی یا دلیل ورود:", color = TextSecondary, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickStrategies) { s ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (strategy == s) AmberGold.copy(alpha = 0.2f) else SlateDark900)
                                    .border(1.dp, if (strategy == s) AmberGold else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { strategy = s }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(s, color = if (strategy == s) AmberGold else TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "وضعیت روانی / احساسی تریدر:", color = TextSecondary, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickEmotions) { e ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (emotionalState == e) CyanNeon.copy(alpha = 0.2f) else SlateDark900)
                                    .border(1.dp, if (emotionalState == e) CyanNeon else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { emotionalState = e }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(e, color = if (emotionalState == e) CyanGlow else TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // 6. Photo Attachment
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "تصویر یا اسکرین‌شات چارت (اختیاری):", color = TextSecondary, fontSize = 11.sp)

                    if (photoUri != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateDark900)
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "چارت پیوست",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "تصویر چارت پیوست شد", color = EmeraldNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Row {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("تغییر", fontSize = 10.sp, color = CyanNeon)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { photoUri = null }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف عکس", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("انتخاب و الصاق عکس چارت از گالری", fontSize = 11.5.sp)
                        }
                    }
                }

                // 7. Free-form Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("یادداشت و نکات تحلیلی معامله") },
                    placeholder = { Text("علت ورود، اشتباهات یا مدیریت سرمایه را بنویسید...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = SlateDark900,
                        unfocusedContainerColor = SlateDark900,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Save Button
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 20.0
                        val payout = payoutText.toIntOrNull() ?: 88
                        val pnl = when (result) {
                            "WIN" -> amount * (payout.toDouble() / 100.0)
                            "LOSS" -> -amount
                            else -> 0.0
                        }

                        val newLog = (initialTrade ?: TradeLogEntity(
                            asset = asset,
                            direction = direction,
                            result = result,
                            tradeAmount = amount,
                            payoutPercent = payout,
                            profitOrLoss = pnl,
                            broker = broker
                        )).copy(
                            asset = asset.trim(),
                            direction = direction,
                            result = result,
                            tradeAmount = amount,
                            payoutPercent = payout,
                            profitOrLoss = pnl,
                            broker = broker,
                            expiry = expiry,
                            entryPrice = entryPrice.trim(),
                            exitPrice = exitPrice.trim(),
                            strategy = strategy,
                            emotionalState = emotionalState,
                            notes = notes.trim(),
                            photoUri = photoUri,
                            timestamp = initialTrade?.timestamp ?: System.currentTimeMillis()
                        )

                        onSave(newLog)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_trade_log_button")
                ) {
                    Text(
                        text = if (initialTrade != null) "ذخیره تغییرات" else "ثبت معامله در ژورنال",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}
