package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
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
import com.example.ui.theme.SlateDark600
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalHistoryScreen(
    signals: List<SignalEntity>,
    wonCount: Int,
    lostCount: Int,
    vetoCount: Int,
    onBack: () -> Unit,
    onDeleteSignal: (Long) -> Unit,
    onClearHistory: () -> Unit,
    onAddSampleSignal: (SignalEntity) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedOutcomeFilter by remember { mutableStateOf("ALL") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var selectedSignalDetail by remember { mutableStateOf<SignalEntity?>(null) }

    // Filter logic
    val filteredSignals = remember(signals, searchQuery, selectedOutcomeFilter, selectedCategoryFilter) {
        signals.filter { signal ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                signal.asset.lowercase().contains(q) ||
                        signal.category.lowercase().contains(q) ||
                        signal.recommendedBrokers.lowercase().contains(q) ||
                        signal.rationale.lowercase().contains(q) ||
                        signal.direction.lowercase().contains(q)
            }

            val matchesOutcome = when (selectedOutcomeFilter) {
                "WON" -> signal.status == "WON"
                "LOST" -> signal.status == "LOST"
                "NO_TRADE" -> signal.direction == "NO_TRADE" || signal.status == "NO_TRADE"
                "ACTIVE" -> signal.status == "ACTIVE"
                else -> true
            }

            val matchesCategory = if (selectedCategoryFilter == "ALL") {
                true
            } else {
                signal.category == selectedCategoryFilter
            }

            matchesSearch && matchesOutcome && matchesCategory
        }
    }

    // Win Rate Calculation
    val totalFinished = wonCount + lostCount
    val winRatePercent = if (totalFinished > 0) {
        (wonCount.toFloat() / totalFinished * 100f).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950)
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark900)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateDark800)
                        .testTag("history_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "تاریخچه سیگنال‌های دریافتی",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Room DB",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = EmeraldNeon
                            )
                        }
                    }
                    Text(
                        text = "پایگاه داده محلی SQLite / Room در حافظه دستگاه",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }

            // Header Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        // Quick add a simulated historical signal into Room to test reactive persistence
                        val testSignal = SignalEntity(
                            asset = "EUR/USD (OTC)",
                            category = "OTC",
                            direction = if (System.currentTimeMillis() % 2 == 0L) "CALL" else "PUT",
                            strikePrice = "1.08${(400..490).random()}",
                            currentPrice = "1.08${(400..490).random()}",
                            expiry = "1m",
                            payoutRate = "۹۳٪",
                            marketRegime = "تحلیل هوشمند لحظه‌ای M1",
                            confidenceScore = (82..94).random(),
                            riskScore = "کم ریسک",
                            vetoStatus = "تایید شده",
                            rationale = "ثبت در دیتابیس Room با موفقیت انجام شد و از طریق Flow واکنش‌گرا به نمایش درآمد.",
                            recommendedBrokers = "Pocket Option, Quotex",
                            status = if (System.currentTimeMillis() % 3 == 0L) "WON" else "ACTIVE",
                            timestamp = System.currentTimeMillis()
                        )
                        onAddSampleSignal(testSignal)
                        Toast.makeText(context, "سیگنال تستی در دیتابیس Room ذخیره شد.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SlateDark800)
                        .testTag("add_test_signal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "ثبت سیگنال جدید در دیتابیس Room",
                        tint = CyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SlateDark800)
                        .testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "پاک‌سازی تاریخچه",
                        tint = CrimsonGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Room Database Performance Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "آمار انباشته دیتابیس محلی (Room Aggregation)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "کل رکوردها: ${signals.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanGlow
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats 4-columns
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Win Rate
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("وین‌ریت", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = "$winRatePercent٪",
                                    color = EmeraldGlow,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Won trades
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("برد (ITM)", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = "$wonCount",
                                    color = EmeraldNeon,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Lost trades
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("باخت (OTM)", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = "$lostCount",
                                    color = CrimsonRed,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Veto Filtered
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("وتو (No Trade)", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = "$vetoCount",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar for Win Rate
                        LinearProgressIndicator(
                            progress = { if (totalFinished > 0) wonCount.toFloat() / totalFinished else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EmeraldNeon,
                            trackColor = SlateDark800
                        )
                    }
                }
            }

            // 3. Search and Quick Filters
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signal_history_search_input"),
                        placeholder = {
                            Text(
                                text = "جستجو در دارایی، بروکر یا تحلیل (مثلاً EUR/USD، OTC، طلا)...",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "پاک کردن", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateDark900,
                            unfocusedContainerColor = SlateDark900,
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Outcome Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val outcomeFilters = listOf(
                            "ALL" to "همه وضعیت‌ها (${signals.size})",
                            "WON" to "بردها ($wonCount)",
                            "LOST" to "باخت‌ها ($lostCount)",
                            "NO_TRADE" to "فیلتر وتو ($vetoCount)",
                            "ACTIVE" to "در جریان"
                        )
                        items(outcomeFilters) { (key, label) ->
                            val isSelected = selectedOutcomeFilter == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedOutcomeFilter = key },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (key) {
                                        "WON" -> EmeraldDark
                                        "LOST" -> CrimsonRed.copy(alpha = 0.25f)
                                        "NO_TRADE" -> AmberGold.copy(alpha = 0.25f)
                                        else -> CyanNeon.copy(alpha = 0.2f)
                                    },
                                    selectedLabelColor = when (key) {
                                        "WON" -> EmeraldNeon
                                        "LOST" -> CrimsonGlow
                                        "NO_TRADE" -> AmberGold
                                        else -> CyanGlow
                                    },
                                    containerColor = SlateDark900,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CyanNeon else CardBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categoryFilters = listOf(
                            "ALL" to "تمامی بازارها",
                            "OTC" to "OTC (۲۴/۷)",
                            "FOREX" to "جفت‌ارزهای فارکس",
                            "CRYPTO" to "ارزهای دیجیتال",
                            "COMMODITIES" to "طلا و نفت"
                        )
                        items(categoryFilters) { (key, label) ->
                            val isSelected = selectedCategoryFilter == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = key },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SlateDark700,
                                    selectedLabelColor = TextPrimary,
                                    containerColor = SlateDark900,
                                    labelColor = TextMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CyanGlow.copy(alpha = 0.5f) else CardBorder
                                )
                            )
                        }
                    }
                }
            }

            // 4. Persistence Info Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateDark900.copy(alpha = 0.7f))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سیگنال‌های باینری آپشن در جدول signals دیتابیس Room با شناسه یکتا ذخیره می‌شوند و بدون نیاز به اینترنت در دسترس هستند.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 17.sp
                            ),
                            color = TextSecondary
                        )
                    }
                }
            }

            // 5. Signals History List or Empty State
            if (filteredSignals.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "هیچ سیگنالی با این مشخصات در دیتابیس محلی یافت نشد",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "می‌توانید فیلترها را تغییر داده یا از دکمه افزودن سیگنال تستی استفاده کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredSignals, key = { it.id }) { signal ->
                    SignalHistoryItemCard(
                        signal = signal,
                        onDetailClick = { selectedSignalDetail = signal },
                        onDeleteClick = {
                            onDeleteSignal(signal.id)
                            Toast.makeText(context, "سیگنال #${signal.id} از دیتابیس حذف شد.", Toast.LENGTH_SHORT).show()
                        },
                        onCopyClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val text = """
                                سیگنال ایران باینری آپشن
                                دارایی: ${signal.asset}
                                جهت: ${signal.direction}
                                استرایک: ${signal.strikePrice}
                                انقضا: ${signal.expiry}
                                بازدهی: ${signal.payoutRate}
                                وضعیت: ${signal.status}
                                بروکرهای بهینه: ${signal.recommendedBrokers}
                            """.trimIndent()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Signal", text))
                            Toast.makeText(context, "مشخصات سیگنال کپی شد.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Confirmation dialog for clearing history
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = SlateDark900,
            title = {
                Text(
                    text = "پاک‌سازی تاریخچه سیگنال‌ها؟",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "آیا اطمینان دارید که می‌خواهید تمام سیگنال‌های ثبت شده در جدول signals دیتابیس محلی Room را حذف نمایید؟ این عملیات غیرقابل بازگشت است.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "تاریخچه دیتابیس Room با موفقیت پاک شد.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text("بله، پاک شود", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }

    // Full detail modal for selected historical signal
    if (selectedSignalDetail != null) {
        val signal = selectedSignalDetail!!
        AlertDialog(
            onDismissRequest = { selectedSignalDetail = null },
            containerColor = SlateDark900,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = signal.asset,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateDark800)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Room-ID: #${signal.id}",
                            color = CyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جهت معامله:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = when (signal.direction) {
                                "CALL" -> "CALL (خرید / بالا 🟢)"
                                "PUT" -> "PUT (فروش / پایین 🔴)"
                                else -> "NO TRADE (وتو شده 🛡️)"
                            },
                            color = when (signal.direction) {
                                "CALL" -> EmeraldNeon
                                "PUT" -> CrimsonRed
                                else -> AmberGold
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("قیمت استرایک:", color = TextSecondary, fontSize = 12.sp)
                        Text(signal.strikePrice, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مدت زمان انقضا:", color = TextSecondary, fontSize = 12.sp)
                        Text(signal.expiry, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("بازدهی بروکر (Payout):", color = TextSecondary, fontSize = 12.sp)
                        Text(signal.payoutRate, color = EmeraldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ضریب اعتماد الگوریتم:", color = TextSecondary, fontSize = 12.sp)
                        Text("${signal.confidenceScore}٪", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("بروکرهای بهینه:", color = TextSecondary, fontSize = 12.sp)
                        Text(signal.recommendedBrokers, color = CyanGlow, fontSize = 11.5.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "تحلیل و رشنال هوش مصنوعی:",
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Text(
                        text = signal.rationale,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            fontSize = 11.5.sp
                        ),
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedSignalDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                ) {
                    Text("بستن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SignalHistoryItemCard(
    signal: SignalEntity,
    onDetailClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    val isCall = signal.direction == "CALL"
    val isPut = signal.direction == "PUT"
    val isNoTrade = signal.direction == "NO_TRADE"

    val directionColor = when {
        isCall -> EmeraldNeon
        isPut -> CrimsonRed
        else -> AmberGold
    }

    val statusBadgeColor = when (signal.status) {
        "WON" -> EmeraldNeon
        "LOST" -> CrimsonRed
        "NO_TRADE" -> AmberGold
        else -> CyanGlow
    }

    val statusBadgeText = when (signal.status) {
        "WON" -> "برد (ITM) 🟢"
        "LOST" -> "باخت (OTM) 🔴"
        "NO_TRADE" -> "فیلتر وتو 🛡️"
        else -> "در جریان ⚡"
    }

    val formattedTime = remember(signal.timestamp) {
        val diffMinutes = (System.currentTimeMillis() - signal.timestamp) / (1000 * 60)
        when {
            diffMinutes < 1 -> "همین الان"
            diffMinutes < 60 -> "$diffMinutes دقیقه پیش"
            diffMinutes < 24 * 60 -> "${diffMinutes / 60} ساعت پیش"
            else -> {
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                sdf.format(Date(signal.timestamp))
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onDetailClick)
            .testTag("signal_history_card_${signal.id}"),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Asset, Category, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.asset,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateDark800)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = signal.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = CyanGlow
                        )
                    }
                }

                // Outcome Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBadgeColor.copy(alpha = 0.15f))
                        .border(1.dp, statusBadgeColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusBadgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusBadgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Metrics Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SlateDark900)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Direction
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isCall -> Icons.Default.ArrowUpward
                            isPut -> Icons.Default.ArrowDownward
                            else -> Icons.Default.Block
                        },
                        contentDescription = null,
                        tint = directionColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = signal.direction,
                        color = directionColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Strike
                Text(
                    text = "استرایک: ${signal.strikePrice}",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                // Expiry
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(signal.expiry, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }

                // Payout
                Text(
                    text = signal.payoutRate,
                    color = EmeraldGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Rationale snippet
            Text(
                text = signal.rationale,
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 16.sp,
                    fontSize = 11.sp
                ),
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Row 4: Timestamp, Room ID, Quick Actions (Copy & Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "زمان دریافت: $formattedTime",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Room #ID: ${signal.id}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SlateDark600
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "کپی مشخصات",
                            tint = TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف از دیتابیس",
                            tint = CrimsonGlow.copy(alpha = 0.8f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
