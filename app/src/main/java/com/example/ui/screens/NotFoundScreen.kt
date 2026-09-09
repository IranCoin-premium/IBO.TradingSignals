package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NotFoundScreen(
    onGoHome: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SlateDark950,
                        SlateDark900
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardSurface)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo motion
            BrandLogomotion(compact = true, showMotto = false)

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CrimsonRed.copy(alpha = 0.15f))
                    .border(1.dp, CrimsonRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "خطای ۴۰۴ - صفحه یا سرور یافت نشد",
                    color = CrimsonGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "داده‌های درخواستی در دسترس نیست",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ممکن است اتصال سرور باینری بروکر دچار وقفه شده باشد، یا نشانی تایم‌فریم مورد نظر نیاز به رفرش مجدد داشته باشد. لایوموشن پلتفرم در وضعیت ایمن (Safe Mode) قرار دارد.",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGoHome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("بازگشت به خانه", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تلاش مجدد", color = CyanGlow, fontSize = 12.sp)
                }
            }
        }
    }
}
