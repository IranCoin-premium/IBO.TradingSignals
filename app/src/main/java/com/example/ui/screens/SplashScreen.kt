package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandLogomotion
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
        )
        delay(200)
        onFinished()
    }

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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-tech Full Brand Logomotion
            BrandLogomotion(
                compact = false,
                showMotto = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated loading bar
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "در حال برقراری اتصال زنده به ۱۵ سرور بروکر و تحلیل‌گر هوش مصنوعی...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldNeon,
                    trackColor = SlateDark800
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(progress.value * 100).toInt()}٪ تکمیل بارگذاری لایوموشن",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = CyanGlow
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "ورود مستقیم به پلتفرم ⬅️",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Iran Binary Option Trading Signals v2.6.0\nمبتنی بر استانداردهای پیشرفته SEO & GEO و بازارهای OTC",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                ),
                color = TextMuted
            )
        }
    }
}
