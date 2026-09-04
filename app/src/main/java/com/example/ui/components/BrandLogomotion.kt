package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GoldGlow
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
    CYBER_NEON(
        "سایبر نئون ⚡",
        "تم دیجیتالی مدرن با ترکیب رنگ‌های سبز نئون و آبی آسمانی تابان",
        EmeraldNeon,
        CyanNeon,
        AmberGold,
        Icons.Default.AutoGraph
    ),
    GOLD_LUXURY(
        "طلایی لاکچری 👑",
        "تم سلطنتی و باشکوه با ترکیب طلا و پلاتین درخشان",
        AmberGold,
        GoldGlow,
        Color(0xFFFFD700),
        Icons.Default.Shield
    ),
    ROYAL_SAPPHIRE(
        "یاقوت سلطنتی 💎",
        "تم حرفه‌ای تریدینگ مدرن با نورهای بنفش عمیق و آبی اقیانوسی",
        CyanNeon,
        Color(0xFF8A2BE2),
        Color(0xFFE0B0FF),
        Icons.Default.TrendingUp
    ),
    EMERALD_MINT(
        "نعنایی زمرد 🍀",
        "تم آرامش و رشد معامله‌گری با رنگ‌های نعنایی و سبز تیره جنگلی",
        Color(0xFF00FFCC),
        Color(0xFF1B4D3E),
        Color(0xFFCCFF00),
        Icons.Default.CheckCircle
    )
}

@Composable
fun BrandLogomotion(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showMotto: Boolean = true,
    style: BrandLogoStyle = BrandLogoStyle.CYBER_NEON,
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
            animation = tween((12000 / speedFactor.coerceAtLeast(0.1f)).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f * glowIntensity,
        targetValue = 0.9f * glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SlateDark900.copy(alpha = 0.95f),
                        CardSurface
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        style.primaryColor.copy(alpha = 0.4f * glowIntensity),
                        style.secondaryColor.copy(alpha = 0.6f * glowIntensity),
                        style.accentColor.copy(alpha = 0.4f * glowIntensity)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(if (compact) 12.dp else 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logomotion visual centerpiece
        Box(
            modifier = Modifier.size(if (compact) 72.dp else 104.dp),
            contentAlignment = Alignment.Center
        ) {
            // Rotating cyber radar circles
            Canvas(
                modifier = Modifier
                    .size(if (compact) 72.dp else 104.dp)
                    .rotate(rotation)
            ) {
                val strokeWidth = 2.dp.toPx() * strokeWidthFactor
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            style.primaryColor.copy(alpha = glowAlpha),
                            style.secondaryColor.copy(alpha = 0.1f),
                            style.accentColor.copy(alpha = glowAlpha),
                            style.primaryColor.copy(alpha = glowAlpha)
                        )
                    ),
                    style = Stroke(width = strokeWidth)
                )

                // Dashed nodes
                drawCircle(
                    color = style.primaryColor,
                    radius = 4.dp.toPx() * strokeWidthFactor,
                    center = Offset(size.width / 2, 2.dp.toPx())
                )
                drawCircle(
                    color = style.secondaryColor,
                    radius = 4.dp.toPx() * strokeWidthFactor,
                    center = Offset(size.width - 2.dp.toPx(), size.height / 2)
                )
            }

            // Counter-rotating inner ring
            Canvas(
                modifier = Modifier
                    .size(if (compact) 56.dp else 84.dp)
                    .rotate(-rotation * 1.5f)
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            style.secondaryColor.copy(alpha = 0.7f * glowIntensity),
                            Color.Transparent,
                            style.primaryColor.copy(alpha = 0.7f * glowIntensity)
                        )
                    ),
                    style = Stroke(width = 1.5.dp.toPx() * strokeWidthFactor)
                )
            }

            // Core hexagon badge
            Box(
                modifier = Modifier
                    .size(if (compact) 44.dp else 64.dp)
                    .scale(pulseScale * coreScaleFactor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                style.primaryColor.copy(alpha = 0.3f),
                                SlateDark800
                            )
                        )
                    )
                    .border(1.5.dp, style.accentColor.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.coreIcon,
                    contentDescription = "Iran Binary Option Logo",
                    tint = style.primaryColor,
                    modifier = Modifier.size(if (compact) 24.dp else 36.dp)
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
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(style.primaryColor.copy(alpha = 0.15f))
                    .border(0.8.dp, style.primaryColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Trading Signals",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        fontSize = if (compact) 11.sp else 13.sp
                    ),
                    color = style.primaryColor
                )
            }
        }

        if (showMotto) {
            Spacer(modifier = Modifier.height(8.dp))

            // Primary Official Slogan
            Text(
                text = "اولین و تنها پلتفرم رسمی ارائه‌دهنده سیگنال‌های معاملاتی به سبک ترید باینری آپشن",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 11.sp else 12.sp,
                    lineHeight = 18.sp
                ),
                color = style.accentColor,
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
                    .background(SlateDark800.copy(alpha = 0.8f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = style.primaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تحلیل قبل از تصمیم، ریسک قبل از معامله",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = TextSecondary
                )
            }
        }
    }
}
