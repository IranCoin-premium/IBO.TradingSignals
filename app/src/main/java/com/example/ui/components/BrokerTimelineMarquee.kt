package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BrokerItem
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun BrokerTimelineMarquee(
    brokers: List<BrokerItem>,
    modifier: Modifier = Modifier
) {
    var selectedBroker by remember { mutableStateOf<BrokerItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val filteredBrokers = remember(brokers, searchQuery) {
        if (searchQuery.isBlank()) brokers
        else brokers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.faName.contains(searchQuery, ignoreCase = true) ||
                    it.badge.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    // Smooth auto-scroll hint effect
    LaunchedEffect(filteredBrokers) {
        if (filteredBrokers.isNotEmpty()) {
            while (true) {
                delay(4000)
                val nextIndex = (listState.firstVisibleItemIndex + 1) % filteredBrokers.size
                listState.animateScrollToItem(nextIndex)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "نوار تایم‌لاین بروکرها و صرافی‌های باینری (${filteredBrokers.size} بروکر)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SlateDark800)
                        .clickable { isSearchExpanded = !isSearchExpanded }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجوی صرافی/بروکر",
                        tint = CyanGlow,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDark.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "لایوموشن متصل",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = EmeraldGlow
                    )
                }
            }
        }

        AnimatedVisibility(visible = isSearchExpanded) {
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("جستجوی صرافی یا بروکر (Pocket, Quotex, Deriv, ...)", fontSize = 11.sp, color = TextSecondary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SlateDark900,
                    unfocusedContainerColor = SlateDark900,
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredBrokers, key = { it.id }) { broker ->
                BrokerChipItem(
                    broker = broker,
                    onClick = { selectedBroker = broker }
                )
            }
        }
    }

    // Broker Details Dialog
    if (selectedBroker != null) {
        val broker = selectedBroker!!
        AlertDialog(
            onDismissRequest = { selectedBroker = null },
            confirmButton = {
                Button(
                    onClick = { selectedBroker = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("بستن مشخصات بروکر", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = broker.faName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = broker.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanGlow
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = broker.payoutRate,
                            color = EmeraldGlow,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = broker.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("پشتیبانی OTC بیست‌وچهار ساعته:", color = TextSecondary, fontSize = 12.sp)
                        Text(if (broker.otc247) "بله (۲۴/۷ فعال)" else "خیر (ساعات اداری)", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سرعت اجرای معاملات:", color = TextSecondary, fontSize = 12.sp)
                        Text(broker.executionSpeed, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("حداقل واریز اولیه:", color = TextSecondary, fontSize = 12.sp)
                        Text(broker.minDeposit, color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("وضعیت اتصال به سیگنال‌های پلتفرم:", color = TextSecondary, fontSize = 12.sp)
                        Text(broker.status, color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun BrokerChipItem(
    broker: BrokerItem,
    onClick: () -> Unit
) {
    val (brandBg, brandAccent, brandText) = when {
        broker.name.contains("Pocket", ignoreCase = true) -> Triple(Color(0xFF1E3A8A), Color(0xFFF59E0B), "PO")
        broker.name.contains("Quotex", ignoreCase = true) -> Triple(Color(0xFF991B1B), Color(0xFFFFFFFF), "QX")
        broker.name.contains("IQ", ignoreCase = true) -> Triple(Color(0xFF9A3412), Color(0xFFFDE047), "IQ")
        broker.name.contains("Olymp", ignoreCase = true) -> Triple(Color(0xFF115E59), Color(0xFF22D3EE), "OT")
        broker.name.contains("Deriv", ignoreCase = true) -> Triple(Color(0xFF7F1D1D), Color(0xFFF87171), "DV")
        broker.name.contains("Expert", ignoreCase = true) -> Triple(Color(0xFF581C87), Color(0xFFFBBF24), "EX")
        broker.name.contains("Binomo", ignoreCase = true) -> Triple(Color(0xFF78350F), Color(0xFFFEF08A), "BM")
        broker.name.contains("Spectre", ignoreCase = true) -> Triple(Color(0xFF312E81), Color(0xFF38BDF8), "SP")
        broker.name.contains("Intrade", ignoreCase = true) -> Triple(Color(0xFF064E3B), Color(0xFF6EE7B7), "IB")
        broker.name.contains("Close", ignoreCase = true) -> Triple(Color(0xFF075985), Color(0xFF7DD3FC), "CO")
        broker.name.contains("Finmax", ignoreCase = true) -> Triple(Color(0xFF881337), Color(0xFFFDA4AF), "FM")
        else -> Triple(EmeraldDark, EmeraldGlow, broker.name.take(2).uppercase())
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        SlateDark900,
                        SlateDark800
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(listOf(brandBg, CardBorder)),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stylized Monogram Brand Logo Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(brandBg)
                    .border(1.dp, brandAccent.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = brandText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 11.5.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = brandAccent
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = broker.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldDark.copy(alpha = 0.6f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = broker.payoutRate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            ),
                            color = EmeraldGlow
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = broker.faName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextSecondary
                    )
                    if (broker.otc247) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanNeon.copy(alpha = 0.25f))
                                .border(0.5.dp, CyanNeon.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "⚡ 24/7 OTC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                color = CyanGlow
                            )
                        }
                    }
                }
            }
        }
    }
}
