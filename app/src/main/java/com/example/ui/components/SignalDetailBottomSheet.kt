package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.R
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPeachGradient
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Detailed Bottom Sheet Modal appearing when tapping any signal in the list.
 * Styled with complete Soft UI design tokens, pastel accents and soft shadows.
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
        isCall -> SoftCardMintAccent
        isPut -> SoftCardPeachAccent
        else -> SoftCardPurpleAccent
    }

    val directionBg = when {
        isCall -> SoftCardMintGradient[0]
        isPut -> SoftCardPeachGradient[0]
        else -> SoftCardPurpleGradient[0]
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
        "WON" -> SoftCardMintAccent
        "LOST" -> SoftCardPeachAccent
        "NO_TRADE" -> SoftCardPeachAccent
        else -> SoftCardCyanAccent
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

    val payoutNumber = remember(signal.payoutRate) {
        val clean = signal.payoutRate.replace("%", "").replace("٪", "")
            .replace("۰", "0").replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4")
            .replace("۵", "5").replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9")
            .trim()
        clean.toIntOrNull() ?: 90
    }

    var selectedTradeAmount by remember { mutableDoubleStateOf(10.0) }
    val potentialProfit = (selectedTradeAmount * payoutNumber / 100.0)
    val totalReturn = selectedTradeAmount + potentialProfit

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SoftUiSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SoftUiCardBorder)
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
                                .border(1.5.dp, directionColor.copy(alpha = 0.4f), CircleShape),
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
                                    color = TextDarkPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SoftCardCyanGradient[0])
                                        .border(0.8.dp, SoftCardCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = signal.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        ),
                                        color = SoftCardCyanAccent
                                    )
                                }
                            }

                            Text(
                                text = "شناسه سیگنال: #${signal.id} • ${signal.marketRegime}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextDarkSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftUiBg)
                            .testTag("signal_detail_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextDarkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Status & Direction Overview Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, directionColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = directionBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "جهت و وضعیت معامله:",
                                color = TextDarkSecondary,
                                fontSize = 11.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiSurface)
                                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
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

            // 3. Detailed Entry Time & Candle Timing Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SoftCardPurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = SoftCardPurpleAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "زمان‌بندی و زمان دقیق ورود (Entry & Timing)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextDarkPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Timings in a 2x2 Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ساعت صدور", color = TextDarkMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$entryTimeFormatted ($entryDateFormatted)",
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مدت انقضا", color = TextDarkMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${signal.expiry} ($expiryMinutes دقیقه)",
                                    color = SoftCardPeachAccent,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ثانیه طلایی ورود", color = TextDarkMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ثانیه ۰۰ تا ۰۲",
                                    color = SoftCardMintAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ساعت انقضا", color = TextDarkMuted, fontSize = 10.5.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = expiryTimeFormatted,
                                    color = SoftCardCyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftUiBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "💡 نکته: پوزیشن را دقیقا در ثانیه ۰۰ (شروع کندل جدید) یا پس از یک پولبک کوتاه ثبت نمایید.",
                                color = TextDarkSecondary,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مشخصات قیمتی و نرخ بازدهی",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextDarkPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SoftCardMintGradient[0])
                                    .border(0.8.dp, SoftCardMintAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Payout: ${signal.payoutRate}",
                                    color = SoftCardMintAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .padding(10.dp)
                            ) {
                                Text("قیمت استرایک (Strike)", color = TextDarkMuted, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = signal.strikePrice,
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftUiBg)
                                    .padding(10.dp)
                            ) {
                                Text("قیمت لحظه‌ای (Live)", color = TextDarkMuted, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = signal.currentPrice,
                                    color = SoftCardCyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "محاسبه‌گر سریع سود فرضی معامله:",
                            color = TextDarkSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

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
                                        .background(if (isSelected) SoftCardMintGradient[0] else SoftUiBg)
                                        .border(
                                            0.8.dp,
                                            if (isSelected) SoftCardMintAccent else SoftUiCardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedTradeAmount = amt }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "$${amt.toInt()}",
                                        color = if (isSelected) SoftCardMintAccent else TextDarkSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftUiBg)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("سود خالص برآوردی (Net Profit):", color = TextDarkMuted, fontSize = 10.5.sp)
                                Text(
                                    text = "+$${String.format(Locale.US, "%.2f", potentialProfit)}",
                                    color = SoftCardMintAccent,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("مجموع برگشتی (Return):", color = TextDarkMuted, fontSize = 10.5.sp)
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", totalReturn)}",
                                    color = SoftCardCyanAccent,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ارزیابی ۳ لایه هوش مصنوعی (AI Multi-Agent)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SoftCardPurpleAccent
                                )
                            }

                            Text(
                                text = "اعتماد: ${signal.confidenceScore}٪",
                                color = SoftCardMintAccent,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { signal.confidenceScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isNoTrade) SoftCardPeachAccent else SoftCardMintAccent,
                            trackColor = SoftUiBg
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سطح ریسک معامله:", color = TextDarkSecondary, fontSize = 11.5.sp)
                            Text(
                                text = signal.riskScore,
                                color = if ("بالا" in signal.riskScore) SoftCardPeachAccent else TextDarkPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("وضعیت وتو (AI Veto):", color = TextDarkSecondary, fontSize = 11.5.sp)
                            Text(
                                text = signal.vetoStatus,
                                color = if ("رد" in signal.vetoStatus || "وتو" in signal.vetoStatus) SoftCardPeachAccent else SoftCardMintAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = SoftUiCardBorder, thickness = 0.8.dp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "تحلیل تکنیکال و رشنال استراتژیست:",
                            color = SoftCardPurpleAccent,
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
                            color = TextDarkPrimary
                        )
                    }
                }
            }

            // 5.5 Technical Analysis Chart (Added in Part 26)
            item {
                val mockTechData = remember(signal.id) {
                    val strike = signal.strikePrice.replace(",", "").toFloatOrNull() ?: 1.0f
                    List(20) { i ->
                        val close = strike + (Math.sin(i.toDouble() / 3.0) * 0.001).toFloat()
                        TechnicalDataPoint(
                            timestamp = System.currentTimeMillis() - (20 - i) * 60000,
                            close = close,
                            rsi = (50 + Math.sin(i.toDouble() / 2.0) * 20).toFloat(),
                            bbUpper = close + 0.002f,
                            bbLower = close - 0.002f,
                            bbMiddle = close
                        )
                    }
                }
                TechnicalChartingCanvas(data = mockTechData)
            }

            // 6. Recommended Brokers
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بروکرهای پیشنهادی و هماهنگ",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextDarkPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = signal.recommendedBrokers,
                            color = SoftCardCyanAccent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "این سیگنال با الگوریتم‌های پاکت‌آپشن، کوتکس و دریو تطبیق داده شده و دارای کمترین میزان لغزش قیمت (Slippage) است.",
                            color = TextDarkSecondary,
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
                        .background(SoftCardPeachGradient[0])
                        .border(1.dp, SoftCardPeachAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SoftCardPeachAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "چک‌لیست ایمنی و مدیریت سرمایه:",
                                color = SoftCardPeachAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("• ورود حداکثر با ۱ تا ۲ درصد از کل بالانس حساب", color = TextDarkSecondary, fontSize = 10.5.sp)
                        Text("• اکیداً از روش مارتینگل و دوبرابر کردن حجم پس از باخت خودداری کنید", color = TextDarkSecondary, fontSize = 10.5.sp)
                        Text("• در صورت مشاهده اسپرد غیرعادی، از ورود منصرف شوید", color = TextDarkSecondary, fontSize = 10.5.sp)
                    }
                }
            }

            // 8. Action Buttons (Copy, Journal, Report Feedback)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "کپی کامل جزئیات سیگنال و زمان ورود",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

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
                                border = BorderStroke(1.dp, SoftCardPeachAccent.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftCardPeachAccent)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ثبت بازخورد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 9. Mandatory Risk Disclosure (P1.10) — تک‌منبع حقیقت از strings.xml
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SoftCardPeachAccent.copy(alpha = 0.08f))
                        .border(
                            1.dp,
                            SoftCardPeachAccent.copy(alpha = 0.35f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SoftCardPeachAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.risk_disclosure_title),
                            color = SoftCardPeachAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = stringResource(R.string.risk_disclosure_content),
                        color = TextDarkSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.testTag("signal_detail_risk_disclosure")
                    )
                }
            }
        }
    }
}
