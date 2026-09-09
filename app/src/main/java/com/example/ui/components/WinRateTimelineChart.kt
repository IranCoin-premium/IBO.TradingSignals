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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

data class WinRatePoint(
    val index: Int,
    val winRate: Float,
    val asset: String,
    val status: String,
    val timestamp: Long
)

@Composable
fun WinRateTimelineChart(
    signals: List<SignalEntity>,
    modifier: Modifier = Modifier
) {
    var selectedRangeLimit by remember { mutableIntStateOf(20) }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(signals.size, selectedRangeLimit) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(20.dp))
            .background(SoftUiSurface)
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
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
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoftCardMintGradient[0])
                            .border(1.dp, SoftCardMintAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = SoftCardMintAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نمودار روند وین‌ریت زمانی (Win Rate Timeline)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextDarkPrimary
                        )
                        Text(
                            text = "تحلیل پویای درصد موفقیت سیگنال‌ها در دیتابیس Room",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextDarkSecondary
                        )
                    }
                }

                // Current Win Rate Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftCardMintGradient[0])
                        .border(1.dp, SoftCardMintAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "میانگین: ${String.format(Locale.US, "%.1f", currentWinRate)}٪",
                        fontWeight = FontWeight.Bold,
                        color = SoftCardMintAccent,
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
                            .background(if (isSelected) SoftCardPurpleGradient[0] else SoftUiBg)
                            .border(1.dp, if (isSelected) SoftCardPurpleAccent else SoftUiCardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedRangeLimit = limit }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) SoftCardPurpleAccent else TextDarkSecondary
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftUiBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = TextDarkMuted,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سیگنال خاتمه‌یافته (برد/باخت) کافی جهت ترسیم نمودار یافت نشد.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkSecondary
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftUiBg)
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                        val width = size.width
                        val height = size.height
                        val progress = animatedProgress.value
                        val yMax = 100f

                        val gridLevels = listOf(25f, 50f, 75f, 100f)
                        val gridPathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                        gridLevels.forEach { level ->
                            val y = height - (level / yMax) * height
                            drawLine(
                                color = SoftUiCardBorder,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f,
                                pathEffect = gridPathEffect
                            )
                        }

                        // Target 70% benchmark line
                        val benchmarkY = height - (70f / yMax) * height
                        drawLine(
                            color = SoftCardMintAccent.copy(alpha = 0.4f),
                            start = Offset(0f, benchmarkY),
                            end = Offset(width, benchmarkY),
                            strokeWidth = 1.5f,
                            pathEffect = gridPathEffect
                        )

                        if (dataPoints.size == 1) {
                            val pt = dataPoints.first()
                            val y = height - (pt.winRate / yMax) * height
                            drawCircle(
                                color = SoftCardMintAccent,
                                radius = 6f * progress,
                                center = Offset(width / 2, y)
                            )
                        } else {
                            val stepX = width / (dataPoints.size - 1)
                            val path = Path()
                            val fillPath = Path()

                            val firstPoint = dataPoints.first()
                            val firstY = height - (firstPoint.winRate / yMax) * height
                            path.moveTo(0f, firstY)
                            fillPath.moveTo(0f, height)
                            fillPath.lineTo(0f, firstY)

                            for (i in 1 until dataPoints.size) {
                                val currentX = i * stepX
                                val ptY = height - (dataPoints[i].winRate / yMax) * height
                                val prevX = (i - 1) * stepX
                                val prevY = height - (dataPoints[i - 1].winRate / yMax) * height

                                val cx = (prevX + currentX) / 2f
                                path.cubicTo(cx, prevY, cx, ptY, currentX, ptY)
                                fillPath.cubicTo(cx, prevY, cx, ptY, currentX, ptY)
                            }

                            fillPath.lineTo((dataPoints.size - 1) * stepX, height)
                            fillPath.close()

                            // Fill under curve
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SoftCardMintAccent.copy(alpha = 0.25f * progress),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Stroke curve
                            drawPath(
                                path = path,
                                color = SoftCardMintAccent,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Draw last point
                            val lastIdx = dataPoints.size - 1
                            val lastX = lastIdx * stepX
                            val lastY = height - (dataPoints.last().winRate / yMax) * height
                            drawCircle(
                                color = SoftUiSurface,
                                radius = 7f * progress,
                                center = Offset(lastX, lastY)
                            )
                            drawCircle(
                                color = SoftCardMintAccent,
                                radius = 4f * progress,
                                center = Offset(lastX, lastY)
                            )
                        }
                    }
                }
            }
        }
    }
}
