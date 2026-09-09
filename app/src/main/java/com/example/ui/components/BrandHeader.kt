package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.ui.theme.*

/**
 * Reusable BrandHeader component integrating the high-quality Lottie animation
 * for the app logo, app name, and official slogan.
 */
@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showMotto: Boolean = true
) {
    // Load Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))
    val lottieAnimState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.85f,
        isPlaying = true
    )

    // Gentle halo breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "brandHeaderHalo")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        SlateDark900,
                        SoftUiSurface,
                        SlateDark900
                    )
                )
            )
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(24.dp))
            .padding(if (compact) 12.dp else 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing ambient halo
        Box(
            modifier = Modifier
                .size(if (compact) 110.dp else 160.dp)
                .scale(haloScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldNeon.copy(alpha = 0.2f),
                            CyanNeon.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie Animation Container
            Box(
                modifier = Modifier
                    .size(if (compact) 84.dp else 118.dp)
                    .clip(CircleShape)
                    .background(SlateDark900.copy(alpha = 0.6f))
                    .border(2.dp, CyanNeon.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (composition != null) {
                    LottieAnimation(
                        composition = composition,
                        progress = { lottieAnimState.progress },
                        modifier = Modifier.size(if (compact) 76.dp else 108.dp)
                    )
                } else {
                    BrandLogomotion(
                        compact = true,
                        showMotto = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Name
            Text(
                text = "ایران باینری آپشن",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (compact) 17.sp else 21.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            // Subtitle Tag
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanNeon.copy(alpha = 0.15f))
                    .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "IBO TRADING SIGNALS VIP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = if (compact) 10.sp else 11.5.sp,
                        letterSpacing = 1.sp
                    ),
                    color = CyanGlow
                )
            }

            if (showMotto) {
                Spacer(modifier = Modifier.height(8.dp))
                // Official Slogan
                Text(
                    text = "سامانه هوشمند تحلیل تکنیکال و سیگنال‌های باینری آپشن",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 11.sp else 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    ),
                    color = EmeraldGlow,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}
