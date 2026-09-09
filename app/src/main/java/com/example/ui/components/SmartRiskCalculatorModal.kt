package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.SoftCardOrangeAccent
import com.example.ui.theme.SoftCardOrangeGradient
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartRiskCalculatorModal(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    initialBalance: Double = 500.0,
    initialPayout: Int = 92
) {
    var balanceInput by remember { mutableStateOf(initialBalance.toInt().toString()) }
    var riskPercent by remember { mutableDoubleStateOf(2.0) }
    var payoutRate by remember { mutableDoubleStateOf(initialPayout.toDouble()) }
    var winRateInput by remember { mutableDoubleStateOf(65.0) } // Default 65% win rate

    val currentBalance = balanceInput.toDoubleOrNull() ?: 500.0
    val recommendedTradeSize = (currentBalance * (riskPercent / 100.0)).coerceAtLeast(1.0)
    val projectedPayoutAmount = recommendedTradeSize * (payoutRate / 100.0)
    val maxLossAllowed = currentBalance * 0.06 // 6% max daily drawdown limit

    // Kelly Criterion: f* = (bp - q) / b
    // p = win prob, q = loss prob (1-p), b = odds (payout rate)
    val p = winRateInput / 100.0
    val q = 1.0 - p
    val b = payoutRate / 100.0
    val kellyF = if (b > 0) (b * p - q) / b else 0.0
    val kellyPercent = (kellyF * 100.0).coerceIn(0.0, 100.0)

    // Simulated AI Recommendation
    val aiConfidence = winRateInput / 100.0
    val aiRiskScore = (riskPercent * 15 + (100 - winRateInput) / 2).coerceIn(0.0, 100.0)
    val recommendation = when {
        winRateInput >= 75 && aiRiskScore < 40 -> "خرید بسیار قوی (STRONG BUY)"
        winRateInput >= 65 && aiRiskScore < 60 -> "خرید (BUY)"
        winRateInput <= 45 || aiRiskScore > 80 -> "فروش بسیار قوی (STRONG SELL)"
        winRateInput <= 55 || aiRiskScore > 70 -> "فروش (SELL)"
        else -> "خنثی (NEUTRAL)"
    }
    val recommendationColor = when {
        recommendation.contains("STRONG BUY") -> com.example.ui.theme.SoftCardMintAccent
        recommendation.contains("BUY") -> com.example.ui.theme.SoftCardCyanAccent
        recommendation.contains("STRONG SELL") -> com.example.ui.theme.SoftCardPeachAccent
        recommendation.contains("SELL") -> com.example.ui.theme.SoftCardOrangeAccent
        else -> com.example.ui.theme.TextDarkSecondary
    }

    val hazards = mutableListOf<String>()
    if (winRateInput < 55) hazards.add("نرخ برد پایین")
    if (riskPercent > 3.5) hazards.add("ریسک سرمایه بالا")
    if (payoutRate < 70) hazards.add("بازدهی ضعیف بروکر")
    if (aiRiskScore > 75) hazards.add("نوسانات شدید بازار")

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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SoftCardPurpleGradient[0]),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = SoftCardPurpleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "هوش مصنوعی ریسک و سرمایه (IBO AI)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDarkPrimary
                        )
                        Text(
                            text = "تحلیل هوشمند و محاسبه فرمول Kelly Criterion",
                            fontSize = 11.sp,
                            color = TextDarkMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftUiBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = TextDarkSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Input Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = balanceInput,
                    onValueChange = { balanceInput = it.filter { c -> c.isDigit() } },
                    label = { Text("موجودی ($)", fontSize = 11.sp, color = TextDarkSecondary) },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftUiBg,
                        unfocusedContainerColor = SoftUiBg,
                        focusedBorderColor = SoftCardPurpleAccent,
                        unfocusedBorderColor = SoftUiCardBorder
                    )
                )

                OutlinedTextField(
                    value = payoutRate.toInt().toString(),
                    onValueChange = { payoutRate = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text("پیروت (٪)", fontSize = 11.sp, color = TextDarkSecondary) },
                    singleLine = true,
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftUiBg,
                        unfocusedContainerColor = SoftUiBg,
                        focusedBorderColor = SoftCardCyanAccent,
                        unfocusedBorderColor = SoftUiCardBorder
                    )
                )
            }

            // Sliders Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Win Rate Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("نرخ برد (Win Rate):", fontSize = 11.5.sp, color = TextDarkSecondary)
                    Text("${winRateInput.toInt()}٪", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SoftCardMintAccent)
                }
                Slider(
                    value = winRateInput.toFloat(),
                    onValueChange = { winRateInput = it.toDouble() },
                    valueRange = 30f..95f,
                    colors = SliderDefaults.colors(thumbColor = SoftCardMintAccent, activeTrackColor = SoftCardMintAccent)
                )

                // Risk Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ریسک در هر معامله:", fontSize = 11.5.sp, color = TextDarkSecondary)
                    Text("${String.format(Locale.US, "%.1f", riskPercent)}٪", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SoftCardPurpleAccent)
                }
                Slider(
                    value = riskPercent.toFloat(),
                    onValueChange = { riskPercent = it.toDouble() },
                    valueRange = 0.5f..10.0f,
                    colors = SliderDefaults.colors(thumbColor = SoftCardPurpleAccent, activeTrackColor = SoftCardPurpleAccent)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SoftUiCardBorder)

            // AI ANALYSIS SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftUiBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftUiCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, null, tint = SoftCardPurpleAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تحلیل هوشمند IBO AI", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDarkPrimary)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(recommendation, color = recommendationColor, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("شاخص ریسک (Risk Score)", fontSize = 10.sp, color = TextDarkMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (aiRiskScore / 100f).toFloat() },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = if (aiRiskScore < 50) SoftCardMintAccent else SoftCardPeachAccent,
                                trackColor = SoftUiCardBorder,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("اعتماد (Confidence)", fontSize = 10.sp, color = TextDarkMuted)
                            Text("${(aiConfidence * 100).toInt()}٪", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SoftCardCyanAccent)
                        }
                    }

                    if (hazards.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = SoftCardPeachAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(hazards) { hazard ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(hazard, fontSize = 9.sp) },
                                        colors = AssistChipDefaults.assistChipColors(labelColor = SoftCardPeachAccent, containerColor = SoftCardPeachAccent.copy(0.1f))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // KELLY CRITERION & RESULTS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Kelly Sugestion
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SoftCardMintGradient[0]),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("فرمول Kelly", fontSize = 10.sp, color = TextDarkSecondary)
                        Text("${String.format(Locale.US, "%.1f", kellyPercent)}٪", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SoftCardMintAccent)
                        Text("حجم پیشنهادی ریاضی", fontSize = 8.sp, color = TextDarkMuted)
                    }
                }

                // Final Amount
                Card(
                    modifier = Modifier.weight(1.2f),
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftUiCardBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مبلغ نهایی معامله", fontSize = 10.sp, color = TextDarkMuted)
                        Text("$${String.format(Locale.US, "%.2f", recommendedTradeSize)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SoftCardPurpleAccent)
                        Text("بر اساس مدیریت ریسک", fontSize = 8.sp, color = TextDarkMuted)
                    }
                }
            }

            // Daily Limit Alert
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCardOrangeGradient[0])
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, null, tint = SoftCardOrangeAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "حد ضرر روزانه پیشنهادی: $${String.format(Locale.US, "%.1f", maxLossAllowed)} (۶٪ سرمایه کل)",
                        fontSize = 10.5.sp, color = TextDarkPrimary, fontWeight = FontWeight.Medium
                    )
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent)
            ) {
                Text("تایید و ثبت در ژورنال هوشمند", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
