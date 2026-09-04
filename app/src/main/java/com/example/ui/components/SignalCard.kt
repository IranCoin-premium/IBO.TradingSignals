package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SignalCard(
    signal: SignalEntity,
    modifier: Modifier = Modifier,
    onBrokerClick: ((String) -> Unit)? = null,
    onReportClick: ((SignalEntity) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val isCall = signal.direction == "CALL"
    val isPut = signal.direction == "PUT"
    val isNoTrade = signal.direction == "NO_TRADE"

    val directionColor = when {
        isCall -> EmeraldNeon
        isPut -> CrimsonRed
        else -> AmberGold
    }

    val directionBg = when {
        isCall -> EmeraldDark.copy(alpha = 0.5f)
        isPut -> CrimsonRed.copy(alpha = 0.15f)
        else -> AmberGold.copy(alpha = 0.15f)
    }

    val directionText = when {
        isCall -> "CALL (خرید / بالا)"
        isPut -> "PUT (فروش / پایین)"
        else -> "NO TRADE (عدم ورود)"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isNoTrade) AmberGold.copy(alpha = 0.4f) else CardBorder,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Asset, Category, Direction Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = signal.asset,
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

                    Text(
                        text = "رژیم بازار: ${signal.marketRegime}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Direction Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(directionBg)
                        .border(1.dp, directionColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isCall -> Icons.Default.ArrowUpward
                                isPut -> Icons.Default.ArrowDownward
                                else -> Icons.Default.Block
                            },
                            contentDescription = null,
                            tint = directionColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = directionText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = directionColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trading Metrics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateDark900.copy(alpha = 0.8f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("قیمت استرایک", color = TextMuted, fontSize = 10.5.sp)
                    Text(signal.strikePrice, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("مدت انقضا (Expiry)", color = TextMuted, fontSize = 10.5.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(signal.expiry, color = CyanGlow, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("بازدهی برآوردی (Payout)", color = TextMuted, fontSize = 10.5.sp)
                    Text(signal.payoutRate, color = EmeraldGlow, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confidence meter & Risk rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ضریب اعتماد الگوریتم:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${signal.confidenceScore}٪", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDark800)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ریسک: ${signal.riskScore}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = if ("بالا" in signal.riskScore) CrimsonGlow else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
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

            // Recommended brokers tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("بروکرهای بهینه: ", color = TextMuted, fontSize = 11.sp)
                Text(
                    text = signal.recommendedBrokers,
                    color = CyanGlow,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Multi-Agent Rationale Accordion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تحلیل چندلایه هوش مصنوعی (AI Strategist & Risk Architect)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = AmberGold
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateDark900)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "وضعیت وتو (Veto Status):",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = signal.vetoStatus,
                            color = if ("رد" in signal.vetoStatus || "وتو" in signal.vetoStatus) CrimsonGlow else EmeraldGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = signal.rationale,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            fontSize = 11.5.sp
                        ),
                        color = TextPrimary
                    )

                    if (onReportClick != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateDark800)
                                .clickable { onReportClick(signal) }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "گزارش عدم انطباق یا ارسال بازخورد برای این سیگنال",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = AmberGold
                            )
                        }
                    }
                }
            }
        }
    }
}
