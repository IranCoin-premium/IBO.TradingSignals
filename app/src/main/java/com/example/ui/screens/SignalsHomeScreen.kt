package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.repository.BrokerItem
import com.example.ui.components.BrandLogomotion
import com.example.ui.components.BrokerTimelineMarquee
import com.example.ui.components.SignalCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SignalsHomeScreen(
    signals: List<SignalEntity>,
    brokers: List<BrokerItem>,
    userPlan: String,
    onOpenSubscriptions: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenNotFoundTest: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL" to "همه بازارها",
        "OTC" to "بازارهای OTC (۲۴/۷)",
        "FOREX" to "جفت‌ارزهای فارکس",
        "CRYPTO" to "ارزهای دیجیتال",
        "COMMODITIES" to "طلا و نفت"
    )

    val filteredSignals = signals.filter { signal ->
        val catMatch = if (selectedCategory == "ALL") true else signal.category == selectedCategory
        val filterMatch = when (selectedFilter) {
            "ACTIVE" -> signal.status == "ACTIVE"
            "NO_TRADE" -> signal.direction == "NO_TRADE"
            "WON" -> signal.status == "WON"
            else -> true
        }
        catMatch && filterMatch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Iran Binary Option",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = CyanGlow
                    )
                    Text(
                        text = "داشبورد سیگنال‌های معاملاتی هوشمند",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EmeraldDark)
                            .clickable(onClick = onOpenSubscriptions)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = AmberGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "پلن: $userPlan",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenSupport,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "پشتیبانی ۲۴ ساعته",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Brand Logomotion Card with official slogans
        item {
            BrandLogomotion(
                compact = false,
                showMotto = true,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 3. 15+ Binary Option Brokers Timeline Marquee Ribbon
        item {
            BrokerTimelineMarquee(
                brokers = brokers,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // 4. Performance & Safety Analytics Ribbon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Win Rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("وین‌ریت میانگین", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("۸۷.۴٪", color = EmeraldGlow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("در تایم‌فریم‌های ۱ و ۵ دقیقه", color = TextSecondary, fontSize = 9.sp)
                    }
                }

                // Metric 2: Live signals
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سیگنال‌های لایو", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${signals.size} سیگنال", color = CyanGlow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("پایش همزمان ۱۵ اکسچنج", color = TextSecondary, fontSize = 9.sp)
                    }
                }

                // Metric 3: No Trade Veto Protection
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فیلتر No Trade", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("فعال و ایمن", color = AmberGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text("جلوگیری از اسپرد سنگین", color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }

        // 5. Market Categories Selector
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (code, label) ->
                    val isSelected = selectedCategory == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldDark else SlateDark900)
                            .border(
                                1.dp,
                                if (isSelected) EmeraldNeon else CardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = code }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.5.sp
                            ),
                            color = if (isSelected) EmeraldGlow else TextSecondary
                        )
                    }
                }
            }
        }

        // 6. Signal Filter Chips (All, Active, No-Trade, Won)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فیلتر وضعیت:",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                listOf(
                    "ALL" to "همه",
                    "ACTIVE" to "در حال اجرا",
                    "NO_TRADE" to "فیلتر No Trade",
                    "WON" to "موفق"
                ).forEach { (code, label) ->
                    val isSelected = selectedFilter == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SlateDark800 else Color.Transparent)
                            .border(
                                0.8.dp,
                                if (isSelected) CyanNeon else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFilter = code }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) CyanGlow else TextSecondary,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // 7. Signals List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "سیگنال‌های هوشمند باینری آپشن (${filteredSignals.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                Text(
                    text = "تست صفحه ۴۰۴",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted,
                    modifier = Modifier.clickable(onClick = onOpenNotFoundTest)
                )
            }
        }

        // 8. Live Signal Items
        if (filteredSignals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 30.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سیگنالی در این فیلتر یافت نشد",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredSignals, key = { it.id }) { signal ->
                SignalCard(
                    signal = signal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
