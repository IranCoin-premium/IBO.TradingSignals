package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

data class TechnicalDataPoint(
    val timestamp: Long,
    val close: Float,
    val rsi: Float,
    val bbUpper: Float,
    val bbLower: Float,
    val bbMiddle: Float
)

@Composable
fun TechnicalChartingCanvas(
    data: List<TechnicalDataPoint>,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data.size) {
        animatedProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(20.dp))
            .background(SoftUiSurface)
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftCardPurpleAccent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Insights, null, tint = SoftCardPurpleAccent, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("تحلیل تکنیکال هوشمند", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDarkPrimary)
                    Text("اندیکاتورهای RSI و Bollinger Bands", fontSize = 10.sp, color = TextDarkSecondary)
                }
            }
            
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SoftCardMintAccent.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("LIVE ENGINE", fontWeight = FontWeight.Black, fontSize = 8.sp, color = SoftCardMintAccent)
            }
        }

        // Price Chart with Bollinger Bands
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SoftUiBg)
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                if (data.isEmpty()) return@Canvas
                
                val width = size.width
                val height = size.height
                val progress = animatedProgress.value

                val minPrice = data.minOf { it.bbLower } * 0.999f
                val maxPrice = data.maxOf { it.bbUpper } * 1.001f
                val priceRange = maxPrice - minPrice

                val stepX = width / (data.size - 1)

                // Draw Bollinger Band Area
                val bbPath = Path()
                bbPath.moveTo(0f, height - ((data[0].bbUpper - minPrice) / priceRange) * height)
                for (i in 1 until data.size) {
                    bbPath.lineTo(i * stepX, height - ((data[i].bbUpper - minPrice) / priceRange) * height)
                }
                for (i in data.size - 1 downTo 0) {
                    bbPath.lineTo(i * stepX, height - ((data[i].bbLower - minPrice) / priceRange) * height)
                }
                bbPath.close()
                drawPath(bbPath, color = SoftCardPurpleAccent.copy(alpha = 0.05f * progress))

                // Draw Close Price Line
                val pricePath = Path()
                pricePath.moveTo(0f, height - ((data[0].close - minPrice) / priceRange) * height)
                for (i in 1 until data.size) {
                    pricePath.lineTo(i * stepX, height - ((data[i].close - minPrice) / priceRange) * height)
                }
                drawPath(
                    path = pricePath,
                    color = SoftCardCyanAccent,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Text("Price & BB (20,2)", modifier = Modifier.align(Alignment.TopStart), fontSize = 9.sp, color = TextDarkMuted)
        }

        // RSI Indicator
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("RSI (14)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextDarkSecondary)
                Text("${data.lastOrNull()?.rsi?.toInt() ?: 0}", fontWeight = FontWeight.Black, fontSize = 11.sp, color = SoftCardPeachAccent)
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftUiBg)
                    .padding(vertical = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    if (data.isEmpty()) return@Canvas
                    val width = size.width
                    val height = size.height
                    val progress = animatedProgress.value
                    val stepX = width / (data.size - 1)

                    // Oversold/Overbought zones
                    val y30 = height - (30f / 100f) * height
                    val y70 = height - (70f / 100f) * height
                    
                    drawLine(SoftCardPeachAccent.copy(alpha = 0.2f), Offset(0f, y30), Offset(width, y30), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
                    drawLine(SoftCardPeachAccent.copy(alpha = 0.2f), Offset(0f, y70), Offset(width, y70), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))

                    val rsiPath = Path()
                    rsiPath.moveTo(0f, height - (data[0].rsi / 100f) * height)
                    for (i in 1 until data.size) {
                        rsiPath.lineTo(i * stepX, height - (data[i].rsi / 100f) * height)
                    }
                    drawPath(
                        path = rsiPath,
                        color = SoftCardPeachAccent,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Technical Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(color = SoftCardCyanAccent, label = "Price")
            LegendItem(color = SoftCardPurpleAccent.copy(alpha = 0.3f), label = "BB Bands")
            LegendItem(color = SoftCardPeachAccent, label = "RSI")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 9.sp, color = TextDarkMuted)
    }
}
