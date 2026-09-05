package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CanaryYellow
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.PhosphorGreenGlow
import com.example.ui.theme.RoyalCyberBlue
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class BrandLogoStyle(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val coreIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    PHOSPHOR_CANARY(
        "فسفری و زرد قناری سوپرلوکس ⚡",
        "تم دیجیتالی مدرن با ترکیب رنگ‌های سبز فسفری و زرد قناری تابان",
        PhosphorGreen,
        CanaryYellow,
        CyanNeon,
        Icons.Default.AutoGraph
    ),
    GOLD_LUXURY(
        "طلایی لاکچری 👑",
        "تم سلطنتی و باشکوه با ترکیب طلا و پلاتین درخشان",
        AmberGold,
        GoldGlow,
        CanaryYellow,
        Icons.Default.Shield
    ),
    CYBER_ORANGE_CRIMSON(
        "نارنجی پرحرارت & زرشکی 🔥",
        "تم پرهیجان معامله‌گری با انفجار رنگ‌های نارنجی و قرمز نئون",
        ElectricOrange,
        CrimsonRed,
        CrimsonGlow,
        Icons.Default.TrendingUp
    ),
    ROYAL_SAPPHIRE(
        "یاقوت سلطنتی & آبی رویال 💎",
        "تم حرفه‌ای تریدینگ مدرن با نورهای آبی رویال و فیروزه‌ای عمیق",
        RoyalCyberBlue,
        CyanNeon,
        CanaryYellow,
        Icons.Default.CheckCircle
    )
}

@Composable
fun BrandLogomotion(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showMotto: Boolean = true,
    style: BrandLogoStyle = BrandLogoStyle.PHOSPHOR_CANARY,
    speedFactor: Float = 1.0f,
    strokeWidthFactor: Float = 1.0f,
    coreScaleFactor: Float = 1.0f,
    glowIntensity: Float = 1.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logomotion")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween((9000 / speedFactor.coerceAtLeast(0.1f)).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween((14000 / speedFactor.coerceAtLeast(0.1f)).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val waveRingRadius by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_ring"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f * glowIntensity,
        targetValue = 0.95f * glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SlateDark900.copy(alpha = 0.96f),
                        CardSurface
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        style.primaryColor.copy(alpha = 0.5f * glowIntensity),
                        style.secondaryColor.copy(alpha = 0.7f * glowIntensity),
                        style.accentColor.copy(alpha = 0.5f * glowIntensity)
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(if (compact) 12.dp else 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logomotion visual centerpiece with luxury animated badge
        Box(
            modifier = Modifier.size(if (compact) 82.dp else 116.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outermost pulsing holographic wave ring
            Canvas(
                modifier = Modifier
                    .size(if (compact) 82.dp else 116.dp)
                    .scale(waveRingRadius)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            style.primaryColor.copy(alpha = 0.25f * (1.3f - waveRingRadius)),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2
                )
            }

            // Rotating cyber radar circles with multi-color neon sweep
            Canvas(
                modifier = Modifier
                    .size(if (compact) 82.dp else 116.dp)
                    .rotate(rotation)
            ) {
                val strokeWidth = 2.dp.toPx() * strokeWidthFactor
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            style.primaryColor.copy(alpha = glowAlpha),
                            style.secondaryColor.copy(alpha = 0.2f),
                            style.accentColor.copy(alpha = glowAlpha),
                            style.primaryColor.copy(alpha = glowAlpha)
                        )
                    ),
                    style = Stroke(width = strokeWidth)
                )

                // Nodes of Canary Yellow & Phosphor Green
                drawCircle(
                    color = CanaryYellow,
                    radius = 4.5.dp.toPx() * strokeWidthFactor,
                    center = Offset(size.width / 2, 2.dp.toPx())
                )
                drawCircle(
                    color = PhosphorGreen,
                    radius = 4.5.dp.toPx() * strokeWidthFactor,
                    center = Offset(size.width - 2.dp.toPx(), size.height / 2)
                )
                drawCircle(
                    color = ElectricOrange,
                    radius = 3.5.dp.toPx() * strokeWidthFactor,
                    center = Offset(2.dp.toPx(), size.height / 2)
                )
            }

            // Counter-rotating inner ring
            Canvas(
                modifier = Modifier
                    .size(if (compact) 64.dp else 92.dp)
                    .rotate(counterRotation)
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            style.secondaryColor.copy(alpha = 0.8f * glowIntensity),
                            Color.Transparent,
                            style.primaryColor.copy(alpha = 0.8f * glowIntensity)
                        )
                    ),
                    style = Stroke(width = 1.6.dp.toPx() * strokeWidthFactor)
                )
            }

            // Core 3D Luxury Custom Brand Badge
            Box(
                modifier = Modifier
                    .size(if (compact) 54.dp else 76.dp)
                    .scale(pulseScale * coreScaleFactor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                style.primaryColor.copy(alpha = 0.35f),
                                SlateDark800
                            )
                        )
                    )
                    .border(2.dp, Brush.linearGradient(listOf(style.secondaryColor, style.primaryColor)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ibo_luxury_logo_1788608740521),
                    contentDescription = "IBO Binary Option Trading Luxury Brand Logo",
                    modifier = Modifier
                        .size(if (compact) 48.dp else 68.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Brand Name (Persian)
        Text(
            text = "ایران باینری آپشن",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = if (compact) 18.sp else 22.sp,
                letterSpacing = 0.5.sp
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        // Sub-Brand with capitalized initials: "Trading Signals"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                style.primaryColor.copy(alpha = 0.2f),
                                style.secondaryColor.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(style.primaryColor, style.secondaryColor)),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "TRADING SIGNALS VIP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontSize = if (compact) 11.sp else 13.sp
                    ),
                    color = CanaryYellow
                )
            }
        }

        if (showMotto) {
            Spacer(modifier = Modifier.height(10.dp))

            // Primary Official Slogan
            Text(
                text = "اولین و تنها پلتفرم رسمی ارائه‌دهنده سیگنال‌های معاملاتی به سبک ترید باینری آپشن",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 11.sp else 12.sp,
                    lineHeight = 18.sp
                ),
                color = PhosphorGreenGlow,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Secondary Philosophy Motto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(SlateDark800.copy(alpha = 0.85f))
                    .border(0.8.dp, style.primaryColor.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CanaryYellow,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تحلیل قبل از تصمیم، ریسک قبل از معامله",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = TextPrimary
                )
            }
        }
    }
}

