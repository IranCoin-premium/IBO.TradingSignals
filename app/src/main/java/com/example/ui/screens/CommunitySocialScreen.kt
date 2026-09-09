package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandHeader
import com.example.ui.theme.*

data class SocialTrader(
    val id: String,
    val name: String,
    val winRate: Float,
    val profit: String,
    val trades: Int,
    val isVip: Boolean = false
)

data class FeedSignal(
    val id: String,
    val traderName: String,
    val asset: String,
    val direction: String,
    val time: String,
    val copyCount: Int
)

@Composable
fun CommunitySocialScreen(
    onNavigateBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("LEADERBOARD") } // LEADERBOARD, SOCIAL_FEED

    val topTraders = remember {
        listOf(
            SocialTrader("1", "CryptoKing", 88.5f, "+$15,400", 1240, true),
            SocialTrader("2", "AlphaTrader", 82.1f, "+$9,200", 850, true),
            SocialTrader("3", "BinaryPro", 79.4f, "+$4,800", 620, false),
            SocialTrader("4", "MarketWizard", 75.0f, "+$3,200", 450, true),
            SocialTrader("5", "ScalperX", 72.8f, "+$2,100", 310, false)
        )
    }

    val socialFeed = remember {
        listOf(
            FeedSignal("s1", "CryptoKing", "BTC/USD", "CALL", "۲ دقیقه پیش", 42),
            FeedSignal("s2", "AlphaTrader", "EUR/USD", "PUT", "۵ دقیقه پیش", 18),
            FeedSignal("s3", "BinaryPro", "GBP/JPY", "CALL", "۱۲ دقیقه پیش", 25)
        )
    }

    Scaffold(
        containerColor = SoftUiBg,
        topBar = {
            BrandHeader()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftUiSurface)
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                listOf("LEADERBOARD" to "لیگ برتر 🏆", "SOCIAL_FEED" to "فید کپی ترید ⚡").forEach { (code, label) ->
                    val isSelected = activeTab == code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldDark else Color.Transparent)
                            .clickable { activeTab = code }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (isSelected) EmeraldGlow else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (activeTab) {
                "LEADERBOARD" -> LeaderboardList(topTraders)
                "SOCIAL_FEED" -> SocialFeedList(socialFeed)
            }
        }
    }
}

@Composable
fun LeaderboardList(traders: List<SocialTrader>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(traders) { index, trader ->
            TraderRankCard(index + 1, trader)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun TraderRankCard(rank: Int, trader: SocialTrader) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> AmberGold.copy(0.2f)
                            2 -> Color(0xFF94A3B8).copy(0.2f) // Silver fallback
                            3 -> Color(0xFFCD7F32).copy(0.2f) // Bronze fallback
                            else -> SoftUiBg
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = when (rank) {
                        1 -> AmberGold
                        2 -> Color(0xFF94A3B8)
                        3 -> Color(0xFFCD7F32)
                        else -> TextMuted
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SoftCardCyanAccent.copy(0.1f))
                    .border(1.dp, SoftCardCyanAccent.copy(0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = SoftCardCyanAccent)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(trader.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    if (trader.isVip) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, null, tint = AmberGold, modifier = Modifier.size(14.dp))
                    }
                }
                Text("${trader.trades} معامله", fontSize = 11.sp, color = TextMuted)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(trader.profit, fontWeight = FontWeight.Black, color = EmeraldGlow, fontSize = 14.sp)
                Text("${trader.winRate}% Win", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun SocialFeedList(feed: List<FeedSignal>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(feed) { signal ->
            SocialSignalCard(signal)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SocialSignalCard(signal: FeedSignal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(SoftUiBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountCircle, null, tint = TextSecondary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(signal.traderName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(signal.time, fontSize = 10.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (signal.direction == "CALL") EmeraldDark else Color(0xFFEF4444).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (signal.direction == "CALL") "BUY / CALL" else "SELL / PUT",
                        color = if (signal.direction == "CALL") EmeraldGlow else CrimsonGlow,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftUiBg)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("نماد معاملاتی", fontSize = 10.sp, color = TextMuted)
                    Text(signal.asset, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                
                Button(
                    onClick = {},
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کپی ترید (${signal.copyCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
