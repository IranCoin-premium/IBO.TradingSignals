package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BrokerItem
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
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
    LaunchedEffect(filteredBrokers.size) {
        if (filteredBrokers.size > 1) {
            while (true) {
                delay(6000)
                try {
                    val nextIndex = (listState.firstVisibleItemIndex + 1) % filteredBrokers.size
                    listState.animateScrollToItem(nextIndex)
                } catch (_: Exception) {
                    // Safe catch
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(20.dp))
            .background(SoftUiSurface)
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = SoftCardPurpleAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "نوار تایم‌لاین بروکرها (${filteredBrokers.size} بروکر فعال)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = TextDarkPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftCardCyanGradient[0])
                        .clickable { isSearchExpanded = !isSearchExpanded }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجوی صرافی/بروکر",
                        tint = SoftCardCyanAccent,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftCardMintGradient[0])
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "لایوموشن فعال",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = SoftCardMintAccent
                    )
                }
            }
        }

        AnimatedVisibility(visible = isSearchExpanded) {
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("جستجوی صرافی یا بروکر (Pocket, Quotex, Deriv, ...)", fontSize = 11.sp, color = TextDarkMuted)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SoftUiBg,
                    unfocusedContainerColor = SoftUiBg,
                    focusedBorderColor = SoftCardPurpleAccent,
                    unfocusedBorderColor = SoftUiCardBorder,
                    focusedTextColor = TextDarkPrimary,
                    unfocusedTextColor = TextDarkPrimary
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
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("بستن مشخصات بروکر", color = Color.White, fontWeight = FontWeight.Bold)
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
                            color = TextDarkPrimary
                        )
                        Text(
                            text = broker.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftCardPurpleAccent
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoftCardMintGradient[0])
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = broker.payoutRate,
                            color = SoftCardMintAccent,
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
                        color = TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("پشتیبانی OTC بیست‌وچهار ساعته:", color = TextDarkSecondary, fontSize = 12.sp)
                        Text(if (broker.otc247) "بله (۲۴/۷ فعال)" else "خیر (ساعات اداری)", color = SoftCardMintAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سرعت اجرای معاملات:", color = TextDarkSecondary, fontSize = 12.sp)
                        Text(broker.executionSpeed, color = TextDarkPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("حداقل واریز اولیه:", color = TextDarkSecondary, fontSize = 12.sp)
                        Text(broker.minDeposit, color = SoftCardPeachAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("وضعیت اتصال به سیگنال‌ها:", color = TextDarkSecondary, fontSize = 12.sp)
                        Text(broker.status, color = SoftCardCyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // P1.10 — بج شفافیت رگولاتوری: وضعیت نظارت رسمی هر بروکر
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoftCardPeachAccent.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = SoftCardPeachAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "وضعیت رگولاتوری (نظارت رسمی):",
                                color = TextDarkSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = broker.regulation,
                            color = SoftCardPeachAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            containerColor = SoftUiSurface,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
fun BrokerChipItem(
    broker: BrokerItem,
    onClick: () -> Unit
) {
    val (brandBg, brandAccent, brandText) = when {
        broker.name.contains("Pocket", ignoreCase = true) -> Triple(Color(0xFFE8F1FD), Color(0xFF3B82F6), "PO")
        broker.name.contains("Quotex", ignoreCase = true) -> Triple(Color(0xFFFFECF3), Color(0xFFE83E8C), "QX")
        broker.name.contains("IQ", ignoreCase = true) -> Triple(Color(0xFFFFF0E7), Color(0xFFFF7251), "IQ")
        broker.name.contains("Olymp", ignoreCase = true) -> Triple(Color(0xFFE4F7FA), Color(0xFF1CB4C8), "OT")
        broker.name.contains("Deriv", ignoreCase = true) -> Triple(Color(0xFFFFE5E5), Color(0xFFEF4444), "DV")
        broker.name.contains("Expert", ignoreCase = true) -> Triple(Color(0xFFECEBFC), Color(0xFF7A6CF0), "EX")
        broker.name.contains("Binomo", ignoreCase = true) -> Triple(Color(0xFFFEF9C3), Color(0xFFCA8A04), "BM")
        broker.name.contains("Spectre", ignoreCase = true) -> Triple(Color(0xFFE0E7FF), Color(0xFF4F46E5), "SP")
        broker.name.contains("Intrade", ignoreCase = true) -> Triple(Color(0xFFE3FAF4), Color(0xFF1BBFA1), "IB")
        broker.name.contains("Close", ignoreCase = true) -> Triple(Color(0xFFE0F2FE), Color(0xFF0284C7), "CO")
        broker.name.contains("Finmax", ignoreCase = true) -> Triple(Color(0xFFFCE7F3), Color(0xFFDB2777), "FM")
        else -> Triple(Color(0xFFE3FAF4), Color(0xFF1BBFA1), broker.name.take(2).uppercase())
    }

    Box(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(16.dp))
            .background(SoftUiSurface)
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
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
                    .background(brandBg),
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftCardMintGradient[0])
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = broker.payoutRate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            ),
                            color = SoftCardMintAccent
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
                        color = TextDarkSecondary
                    )
                    if (broker.otc247) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SoftCardCyanGradient[0])
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "⚡ OTC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                color = SoftCardCyanAccent
                            )
                        }
                    }
                }
            }
        }
    }
}
