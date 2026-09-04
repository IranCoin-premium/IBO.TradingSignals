package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import com.example.ui.components.BrandLogomotion
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Lottie-based animated Splash Screen orchestrating a seamless sequence:
 * 1. Phase 1 (0% - 45%): Brand Reveal - Official "Iran Binary Option" diamond crest & candlestick logo
 * 2. Phase 2 (45% - 85%): Transition & Cybernetic Radar Loader - Dynamic binary nodes, pulse waves & data sync
 * 3. Phase 3 (85% - 100%): Market Engine Ready - Handshake with Room offline database & broker feeds
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    // Load Lottie composition from raw resources
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))

    // Fallback animatable to ensure progress even if composition takes a moment
    val fallbackProgress = remember { Animatable(0f) }

    // Animate Lottie composition state
    val lottieAnimState = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 0.9f,
        isPlaying = true
    )

    // Run fallback timer alongside Lottie to guarantee smooth progress
    LaunchedEffect(Unit) {
        fallbackProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
        )
    }

    // Effective progress: prefer Lottie progress when available, otherwise fallback
    val effectiveProgress = if (composition != null) {
        lottieAnimState.progress
    } else {
        fallbackProgress.value
    }

    // Trigger onFinished when animation reaches end (or fallback completes)
    LaunchedEffect(lottieAnimState.isAtEnd, effectiveProgress) {
        if ((lottieAnimState.isAtEnd && composition != null) || effectiveProgress >= 1f) {
            delay(300)
            onFinished()
        }
    }

    // Gentle infinite breathing pulse for background radial halo
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        SlateDark900,
                        SlateDark950
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glowing ambient halo behind Lottie animation
        Box(
            modifier = Modifier
                .size(290.dp)
                .scale(haloScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldNeon.copy(alpha = 0.18f),
                            CyanNeon.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Version and security pill badge at the top
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SlateDark900)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EmeraldNeon)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "ایران باینری آپشن | اتصال امن نسخه ۳.۰",
                        color = EmeraldGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Main Lottie Animation Container: Logo transitioning to Loading Animation
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(SlateDark900.copy(alpha = 0.45f))
                    .border(1.dp, CardBorder, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (composition != null) {
                    LottieAnimation(
                        composition = composition,
                        progress = { lottieAnimState.progress },
                        modifier = Modifier.size(220.dp)
                    )
                } else {
                    // Resilient fallback while Lottie asset initializes
                    BrandLogomotion(
                        compact = false,
                        showMotto = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Typography
            Text(
                text = "ایران باینری آپشن",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "سامانه هوشمند تحلیل تکنیکال و سیگنال‌های باینری آپشن",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center
                ),
                color = CyanGlow
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Orchestrated Phase Sequence Descriptions
            val phaseTitle = when {
                effectiveProgress < 0.40f -> "۱. بارگذاری نماد رسمی و هویت برند ایران باینری"
                effectiveProgress < 0.75f -> "۲. اتصال به رادار تحلیلی بروکرها و وب‌سوکت OTC..."
                else -> "۳. همگام‌سازی کش محلی Room و راه‌اندازی هسته هوش مصنوعی"
            }

            val phaseSubtitle = when {
                effectiveProgress < 0.40f -> "الگوریتم‌های پیشرفته تحلیل نوسانات و تعیین انقضای ۶۰ ثانیه الی ۵ دقیقه"
                effectiveProgress < 0.75f -> "دریافت فید قیمت زنده جفت‌ارزها، ارزهای دیجیتال و طلا"
                else -> "Single Source of Truth محلی فعال است — پایانه آماده معاملات"
            }

            Text(
                text = phaseTitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
                color = if (effectiveProgress >= 0.85f) EmeraldGlow else GoldGlow
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = phaseSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                ),
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // High-Tech Cyber Progress Indicator
            Column(
                modifier = Modifier.fillMaxWidth(0.88f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { effectiveProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (effectiveProgress >= 0.85f) EmeraldNeon else CyanNeon,
                    trackColor = SlateDark800
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "وضعیت لایوموشن: ${if (effectiveProgress < 0.5f) "نمایش لوگو" else "رادار و لودر داده"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = TextMuted
                    )

                    Text(
                        text = "${(effectiveProgress * 100).toInt()}٪",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = CyanGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Telemetry Status Chips
            Row(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusPill(
                    icon = Icons.Default.Memory,
                    label = "کش Room",
                    isReady = true,
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    icon = Icons.Default.CloudDone,
                    label = "سینک ابری",
                    isReady = effectiveProgress >= 0.45f,
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    icon = Icons.Default.Shield,
                    label = "بروکرها",
                    isReady = effectiveProgress >= 0.70f,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Direct Entry Button (Skip) with accessible 48dp minimum touch target
            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateDark800,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(48.dp)
                    .testTag("splash_enter_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = EmeraldGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ورود مستقیم به پلتفرم ⬅️",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SlateDark900)
            .border(
                1.dp,
                if (isReady) EmeraldNeon.copy(alpha = 0.5f) else CardBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isReady) EmeraldGlow else TextMuted,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isReady) FontWeight.Bold else FontWeight.Normal,
                color = if (isReady) TextPrimary else TextMuted
            )
        }
    }
}
