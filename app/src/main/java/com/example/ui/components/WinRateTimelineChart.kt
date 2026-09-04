package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
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

data class WinRatePoint(
    val index: Int,
    val winRate: Float, // 0..100
    val asset: String,
    val status: String,
    val timestamp: Long
)

@Composable
fun WinRateTimelineChart(
    signals: List<SignalEntity>,
    modifier: Modifier = Modifier
) {
    var selectedRangeLimit by remember { mutableIntStateOf(20) } // 10, 20, 50, or 0 (All)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(signals.size, selectedRangeLimit) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    // Filter finished signals sorted chronologically (oldest to newest)
    val finishedSignals = remember(signals, selectedRangeLimit) {
        val resolved = signals
            .filter { it.status == "WON" || it.status == "LOST" }
            .sortedBy { it.timestamp }
        if (selectedRangeLimit > 0 && resolved.size > selectedRangeLimit) {
            resolved.takeLast(selectedRangeLimit)
        } else {
            resolved
        }
    }

    // Compute cumulative win rate progression
    val dataPoints = remember(finishedSignals) {
        var runningWins = 0
        var total = 0
        val list = mutableListOf<WinRatePoint>()
        finishedSignals.forEachIndexed { idx, sig ->
            total++
            if (sig.status == "WON") runningWins++
            val rate = (runningWins.toFloat() / total) * 100f
            list.add(
                WinRatePoint(
                    index = idx,
                    winRate = rate,
                    asset = sig.asset,
                    status = sig.status,
                    timestamp = sig.timestamp
                )
            )
        }
        list
    }

    val currentWinRate = dataPoints.lastOrNull()?.winRate ?: 0f
    val maxWinRate = dataPoints.maxOfOrNull { it.winRate } ?: 0f
    val minWinRate = dataPoints.minOfOrNull { it.winRate } ?: 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldGlow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نمودار روند وین‌ریت زمانی (Win Rate Timeline)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "تحلیل پویای درصد موفقیت سیگنال‌ها در دیتابیس Room",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }

                // Current Win Rate Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldNeon.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "میانگین: ${String.format(Locale.US, "%.1f", currentWinRate)}٪",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldNeon,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time range chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val chips = listOf(
                    10 to "۱۰ سیگنال اخیر",
                    20 to "۲۰ سیگنال اخیر",
                    50 to "۵۰ سیگنال اخیر",
                    0 to "کل تاریخچه"
                )
                chips.forEach { (limit, title) ->
                    val isSelected = selectedRangeLimit == limit
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SlateDark700 else SlateDark900)
                            .border(1.dp, if (isSelected) CyanNeon else SlateDark800, RoundedCornerShape(8.dp))
                            .clickable { selectedRangeLimit = limit }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) CyanNeon else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateDark950),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سیگنال خاتمه‌یافته (برد/باخت) کافی جهت ترسیم نمودار یافت نشد.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // Interactive Compose Canvas Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateDark950)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                        val width = size.width
                        val height = size.height
                        val progress = animatedProgress.value

                        val yMin = 0f
                        val yMax = 100f

                        // Draw Grid Horizontal Lines (25%, 50%, 75%, 100%)
                        val gridLevels = listOf(25f, 50f, 75f, 100f)
                        val gridPathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                        gridLevels.forEach { level ->
                            val y = height - (level / yMax) * height
                            drawLine(
                                color = SlateDark800,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f,
                                pathEffect = gridPathEffect
                            )
                        }

                        // Target 70% benchmark line (Green dotted)
                        val benchmarkY = height - (70f / yMax) * height
                        drawLine(
                            color = EmeraldGlow.copy(alpha = 0.4f),
                            start = Offset(0f, benchmarkY),
                            end = Offset(width, benchmarkY),
                            strokeWidth = 1.5f,
                            pathEffect = gridPathEffect
                        )

                        if (dataPoints.size == 1) {
                            // Single point
                            val pt = dataPoints.first()
                            val y = height - (pt.winRate / yMax) * height
                            drawCircle(
                                color = EmeraldNeon,
                                radius = 6f * progress,
                                center = Offset(width / 2, y)
                            )
                        } else {
                            val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

                            val points = dataPoints.mapIndexed { idx, pt ->
                                val x = idx * stepX
                                val targetY = height - (pt.winRate / yMax) * height
                                val animatedY = height - (height - targetY) * progress
                                Offset(x, animatedY)
                            }

                            // Build smooth path
                            val strokePath = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 0 until points.size - 1) {
                                    val current = points[i]
                                    val next = points[i + 1]
                                    val controlX = (current.x + next.x) / 2f
                                    cubicTo(
                                        controlX, current.y,
                                        controlX, next.y,
                                        next.x, next.y
                                    )
                                }
                            }

                            // Fill Path
                            val fillPath = Path().apply {
                                addPath(strokePath)
                                lineTo(points.last().x, height)
                                lineTo(points.first().x, height)
                                close()
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldNeon.copy(alpha = 0.35f * progress),
                                        EmeraldGlow.copy(alpha = 0.15f * progress),
                                        Color.Transparent
                                    ),
                                    startY = 0f,
                                    endY = height
                                )
                            )

                            // Glow Line
                            drawPath(
                                path = strokePath,
                                color = EmeraldGlow.copy(alpha = 0.5f),
                                style = Stroke(width = 5f * progress, cap = StrokeCap.Round)
                            )

                            // Core Line
                            drawPath(
                                path = strokePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(CyanNeon, EmeraldNeon, AmberGold)
                                ),
                                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                            )

                            // Draw peak and latest markers
                            val lastPoint = points.last()
                            drawCircle(
                                color = EmeraldNeon,
                                radius = 5f * progress,
                                center = lastPoint
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 2.5f * progress,
                                center = lastPoint
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chart Legend / Key metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldNeon)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "حداکثر وین‌ریت: ${String.format(Locale.US, "%.1f", maxWinRate)}٪",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AmberGold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تارگت سوددهی: ۷۰٪+",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyanNeon)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${dataPoints.size} معامله ارزیابی‌شده",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = CyanGlow
                        )
                    }
                }
            }
        }
    }
}
