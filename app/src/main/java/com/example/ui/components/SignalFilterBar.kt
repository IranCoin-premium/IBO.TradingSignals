package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

/**
 * Filter Component at the top of the signal list with unified Soft UI design.
 */
@Composable
fun SignalFilterBar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    minConfidence: Int,
    onMinConfidenceSelected: (Int) -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    totalResultsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with search bar and counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SoftCardPurpleGradient[0])
                            .border(1.dp, SoftCardPurpleAccent.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SoftCardPurpleAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "فیلتر پیشرفته سیگنال‌ها",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftCardCyanGradient[0])
                        .border(0.8.dp, SoftCardCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$totalResultsCount سیگنال",
                        color = SoftCardCyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "جستجوی جفت‌ارز (مثلاً EUR/USD، BTC، GOLD)...",
                        fontSize = 11.5.sp,
                        color = TextDarkMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SoftCardPurpleAccent,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChanged("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "پاک کردن",
                                tint = TextDarkMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("signal_search_field"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SoftUiBg,
                    unfocusedContainerColor = SoftUiBg,
                    focusedBorderColor = SoftCardPurpleAccent,
                    unfocusedBorderColor = SoftUiCardBorder,
                    focusedTextColor = TextDarkPrimary,
                    unfocusedTextColor = TextDarkPrimary
                )
            )

            // Section 1: Asset Class
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "کلاس دارایی (Asset Class):",
                    fontSize = 11.sp,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val assetClasses = listOf(
                        "ALL" to "همه بازارها",
                        "FOREX" to "جفت‌ارزهای فارکس",
                        "CRYPTO" to "ارزهای دیجیتال",
                        "OTC" to "بازارهای OTC (۲۴/۷)",
                        "COMMODITIES" to "طلا و نفت"
                    )

                    items(assetClasses) { (code, label) ->
                        val isSelected = selectedCategory == code
                        FontAwesomeCategoryPill(
                            categoryCode = code,
                            label = label,
                            isSelected = isSelected,
                            onClick = { onCategorySelected(code) },
                            modifier = Modifier.testTag("asset_class_chip_$code")
                        )
                    }
                }
            }

            // Section 2: Success Probability Level
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "حداقل ضریب احتمال برد هوش مصنوعی:",
                    fontSize = 11.sp,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val confidenceLevels = listOf(
                        0 to "همه ضریب‌ها",
                        80 to "احتمال +۸۰٪",
                        85 to "احتمال +۸۵٪",
                        90 to "فوق‌العاده (+۹۰٪)"
                    )

                    confidenceLevels.forEach { (conf, label) ->
                        val isSelected = minConfidence == conf
                        val (bg, accent) = when (conf) {
                            90 -> Pair(SoftCardPeachAccent.copy(alpha = 0.15f), SoftCardPeachAccent)
                            85 -> Pair(SoftCardMintAccent.copy(alpha = 0.15f), SoftCardMintAccent)
                            80 -> Pair(SoftCardCyanAccent.copy(alpha = 0.15f), SoftCardCyanAccent)
                            else -> Pair(SoftUiBg, SoftCardPurpleAccent)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) bg else SoftUiBg)
                                .border(
                                    0.8.dp,
                                    if (isSelected) accent else SoftUiCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onMinConfidenceSelected(conf) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) accent else TextDarkSecondary
                            )
                        }
                    }
                }
            }

            // Section 3: Status Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "وضعیت سیگنال:",
                    fontSize = 11.sp,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statusList = listOf(
                        "ALL" to "همه",
                        "FAVORITES" to "نشان‌شده‌ها ⭐",
                        "ACTIVE" to "در حال اجرا",
                        "NO_TRADE" to "No Trade 🛡️",
                        "WON" to "موفق 🟢"
                    )

                    statusList.forEach { (code, label) ->
                        val isSelected = selectedFilter == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SoftCardPurpleGradient[0] else SoftUiBg)
                                .border(
                                    0.8.dp,
                                    if (isSelected) SoftCardPurpleAccent else SoftUiCardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onFilterSelected(code) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSelected) SoftCardPurpleAccent else TextDarkSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
