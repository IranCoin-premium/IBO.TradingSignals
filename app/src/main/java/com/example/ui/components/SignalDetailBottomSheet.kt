package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.ui.theme.GoldGlow
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

/**
 * Detailed Bottom Sheet Modal appearing when tapping any signal in the list.
 * Provides granular details:
 * - Exact entry timing (ثانیه ۰۰ کندل، ساعت صدور، مدت انقضا، ساعت تقریبی بسته شدن)
 * - Price metrics (استرایک پرایس، پی‌آوت بروکر، محاسبه‌گر سود فرضی)
 * - AI confidence, Veto status, and Multi-Agent rationale
 * - Recommended broker compatibility and execution speeds
 * - Pre-trade safety checklist (مدیریت ریسک ۲٪، عدم مارتینگل)
 * - Quick actions: Copy signal, Log to Trade Journal, Report/Feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalDetailBottomSheet(
    signal: SignalEntity?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onLogToJournal: ((SignalEntity) -> Unit)? = null,
    onReportFeedback: ((SignalEntity) -> Unit)? = null
) {
    if (signal == null) return
    val context = LocalContext.current

    val isCall = signal.direction == "CALL"
    val isPut = signal.direction == "PUT"
    val isNoTrade = signal.direction == "NO_TRADE"

    val directionColor = when {
        isCall -> EmeraldNeon
        isPut -> CrimsonRed
        else -> AmberGold
    }

    val directionBg = when {
        isCall -> EmeraldDark.copy(alpha = 0.6f)
        isPut -> CrimsonRed.copy(alpha = 0.2f)
        else -> AmberGold.copy(alpha = 0.2f)
    }

    val directionFaTitle = when {
        isCall -> "CALL (خرید / حرکت رو به بالا 🟢)"
        isPut -> "PUT (فروش / حرکت رو به پایین 🔴)"
        else -> "NO TRADE (وتو شده / عدم ورود 🛡️)"
    }

    val statusFaText = when (signal.status) {
        "WON" -> "موفق (ITM - In The Money) 🟢"
        "LOST" -> "ناموفق (OTM - Out Of The Money) 🔴"
        "NO_TRADE" -> "فیلتر وتو هوش مصنوعی 🛡️"
        else -> "در حال معامله (ACTIVE) ⚡"
    }

    val statusColor = when (signal.status) {
        "WON" -> EmeraldNeon
        "LOST" -> CrimsonRed
        "NO_TRADE" -> AmberGold
        else -> CyanGlow
    }

    // Format timestamps
    val timeSdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val dateSdf = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
    val entryTimeFormatted = remember(signal.timestamp) { timeSdf.format(Date(signal.timestamp)) }
    val entryDateFormatted = remember(signal.timestamp) { dateSdf.format(Date(signal.timestamp)) }

    // Parse expiry minutes for estimated expiry time
    val expiryMinutes = remember(signal.expiry) {
        when {
            signal.expiry.contains("15") -> 15
            signal.expiry.contains("5") -> 5
            signal.expiry.contains("3") -> 3
            signal.expiry.contains("2") -> 2
            else -> 1
        }
    }
    val expiryTimeFormatted = remember(signal.timestamp, expiryMinutes) {
        timeSdf.format(Date(signal.timestamp + expiryMinutes * 60 * 1000L))
    }

    // Parse payout percentage (e.g. "۹۳٪" or "93%" -> 93)
    val payoutNumber = remember(signal.payoutRate) {
        val clean = signal.payoutRate.replace("%", "").replace("٪", "")
            .replace("۰", "0").replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4")
            .replace("۵", "5").replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9")
            .trim()
        clean.toIntOrNull() ?: 90
    }

    // Interactive potential trade size for simulation
    var selectedTradeAmount by remember { mutableDoubleStateOf(10.0) }
    val potentialProfit = (selectedTradeAmount * payoutNumber / 100.0)
    val totalReturn = selectedTradeAmount + potentialProfit

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateDark950,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SlateDark700)
            )
        },
        modifier = Modifier.testTag("signal_detail_bottom_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header with Asset, Direction Pill and Close Action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(directionBg)
                                .border(1.5.dp, directionColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isCall -> Icons.Default.ArrowUpward
                                    isPut -> Icons.Default.ArrowDownward
                                    else -> Icons.Default.Block
                                },
                                contentDescription = null,
                                tint = directionColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = signal.asset,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp
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
                                            fontSize = 10.5.sp
                                        ),
                                        color = CyanGlow
                                    )
                                }
                            }

                            Text(
                                text = "شناسه سیگنال: #${signal.id} • ${signal.marketRegime}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                            .testTag("signal_detail_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Status & Direction Overview Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, directionColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "جهت و وضعیت معامله:",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = statusFaText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = directionFaTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 14.5.sp
                            ),
                            color = directionColor
                        )
                    }
                }
            }

            // 3. Detailed Entry Time & Candle Timing Card (بخش اختصاصی زمان دقیق ورود و ثانیه‌شمار)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark900),
                    border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "زمان‌بندی و زمان دقیق ورود (Entry & Expiration Timing)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Timings in a 2x2 Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. Issue time
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark950)
                                    .border(0.8.dp, CardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ساعت صدور سیگنال", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$entryTimeFormatted ($entryDateFormatted)",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // 2. Expiry duration
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark950)
                                    .border(0.8.dp, CardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = AmberGold, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مدت زمان انقضا", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${signal.expiry} ($expiryMinutes دقیقه)",
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 3. Exact Entry Second recommendation
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark950)
                                    .border(0.8.dp, CardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ثانیه طلایی ورود", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ثانیه ۰۰ تا ۰۲ (Candle Open)",
                                    color = EmeraldGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            // 4. Estimated Close Time
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark950)
                                    .border(0.8.dp, CardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ساعت انقضای تخمینی", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = expiryTimeFormatted,
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Timing Rule Tip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateDark800.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "💡 نکته حرفه‌ای: برای بیشترین دقت، پوزیشن را دقیقا در ثانیه ۰۰ (شروع کندل جدید) یا پس از یک پولبک ۳۰ درصدی به قیمت استرایک ثبت نمایید.",
                                color = TextSecondary,
                                fontSize = 10.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 4. Price Parameters & Potential Profit Estimator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مشخصات قیمتی و نرخ بازدهی",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldDark)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Payout: ${signal.payoutRate}",
                                    color = EmeraldNeon,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Strike Price & Current Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(10.dp)
                            ) {
                                Text("قیمت استرایک (Strike)", color = TextMuted, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = signal.strikePrice,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateDark900)
                                    .padding(10.dp)
                            ) {
                                Text("قیمت لحظه‌ای (Live)", color = TextMuted, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = signal.currentPrice,
                                    color = CyanGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Profit Estimator
                        Text(
                            text = "محاسبه‌گر سریع سود فرضی معامله:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick amount chips ($5, $10, $25, $50, $100)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val amounts = listOf(5.0, 10.0, 25.0, 50.0, 100.0)
                            items(amounts) { amt ->
                                val isSelected = selectedTradeAmount == amt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmeraldDark else SlateDark900)
                                        .border(
                                            0.8.dp,
                                            if (isSelected) EmeraldNeon else CardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedTradeAmount = amt }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "$${amt.toInt()}",
                                        color = if (isSelected) EmeraldNeon else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calculation Result Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateDark900)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("سود خالص برآوردی (Net Profit):", color = TextMuted, fontSize = 10.5.sp)
                                Text(
                                    text = "+$${String.format(Locale.US, "%.2f", potentialProfit)}",
                                    color = EmeraldNeon,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("مجموع برگشتی (Return):", color = TextMuted, fontSize = 10.5.sp)
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", totalReturn)}",
                                    color = CyanGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. AI Confidence, Risk Score, and Multi-Agent Rationale
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark900),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ارزیابی ۳ لایه هوش مصنوعی (AI Multi-Agent)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AmberGold
                                )
                            }

                            Text(
                                text = "اعتماد: ${signal.confidenceScore}٪",
                                color = EmeraldNeon,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Confidence bar
                        LinearProgressIndicator(
                            progress = { signal.confidenceScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isNoTrade) AmberGold else EmeraldNeon,
                            trackColor = SlateDark800
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سطح ریسک معامله:", color = TextSecondary, fontSize = 11.5.sp)
                            Text(
                                text = signal.riskScore,
                                color = if ("بالا" in signal.riskScore) CrimsonGlow else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("وضعیت وتو (AI Veto):", color = TextSecondary, fontSize = 11.5.sp)
                            Text(
                                text = signal.vetoStatus,
                                color = if ("رد" in signal.vetoStatus || "وتو" in signal.vetoStatus) CrimsonGlow else EmeraldGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = SlateDark800, thickness = 0.8.dp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "تحلیل تکنیکال و رشنال استراتژیست:",
                            color = AmberGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = signal.rationale,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 18.sp,
                                fontSize = 11.5.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }
            }

            // 6. Recommended Brokers & Execution Speed
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بروکرهای پیشنهادی و هماهنگ",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = signal.recommendedBrokers,
                            color = CyanGlow,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "این سیگنال با الگوریتم‌های پاکت‌آپشن، کوتکس و دریو تطبیق داده شده و دارای کمترین میزان لغزش قیمت (Slippage) است.",
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 7. Safety & Position Sizing Checklist
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateDark900)
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "چک‌لیست ایمنی و مدیریت سرمایه (Pre-Trade Checklist):",
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("• ورود حداکثر با ۱ تا ۲ درصد از کل بالانس حساب", color = TextSecondary, fontSize = 10.5.sp)
                        Text("• اکیداً از روش مارتینگل و دوبرابر کردن حجم پس از باخت خودداری کنید", color = TextSecondary, fontSize = 10.5.sp)
                        Text("• در صورت مشاهده اسپرد غیرعادی، از ورود منصرف شوید", color = TextSecondary, fontSize = 10.5.sp)
                    }
                }
            }

            // 8. Action Buttons (Copy, Journal, Report Feedback)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Copy signal text button
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val text = """
                                🚀 سیگنال ایران باینری آپشن
                                دارایی: ${signal.asset}
                                جهت: ${signal.direction}
                                ساعت صدور: $entryTimeFormatted
                                استرایک پرایس: ${signal.strikePrice}
                                انقضا: ${signal.expiry} (خروج تقریبی: $expiryTimeFormatted)
                                بازدهی بروکر: ${signal.payoutRate}
                                ضریب اعتماد: ${signal.confidenceScore}%
                                بروکرهای سازگار: ${signal.recommendedBrokers}
                                تحلیل: ${signal.rationale}
                            """.trimIndent()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Signal Detail", text))
                            Toast.makeText(context, "مشخصات و زمان‌بندی سیگنال کپی شد.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("signal_detail_copy_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "کپی کامل جزئیات سیگنال و زمان ورود",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onLogToJournal != null) {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onLogToJournal(signal)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("signal_detail_journal_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldNeon)
                            ) {
                                Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ثبت در ژورنال", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (onReportFeedback != null) {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onReportFeedback(signal)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("signal_detail_report_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ثبت بازخورد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
