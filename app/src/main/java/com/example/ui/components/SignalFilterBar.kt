package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
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

/**
 * Filter Component at the top of the signal list.
 * Allows multi-criteria filtering:
 * 1. Asset Class (همه بازارها، OTC، فارکس، ارز دیجیتال، طلا و نفت)
 * 2. Probability / Success Confidence Level (همه، +۸۰٪، +۸۵٪، +۹۰٪)
 * 3. Signal Status (همه، در حال معامله، No Trade، موفق)
 * 4. Quick Asset Search
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
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with search bar
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
                            .background(EmeraldDark)
                            .border(1.dp, EmeraldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "فیلتر پیشرفته سیگنال‌ها",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDark900)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$totalResultsCount سیگنال",
                        color = CyanGlow,
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
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = CyanGlow,
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
                                tint = TextMuted,
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
                    focusedContainerColor = SlateDark900,
                    unfocusedContainerColor = SlateDark900,
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Section 1: Asset Class (کلاس دارایی و بازارها)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "کلاس دارایی (Asset Class):",
                    fontSize = 11.sp,
                    color = TextSecondary,
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

            // Section 2: Success Probability Level (ضریب احتمال برد / Win-Rate Confidence)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "حداقل ضریب احتمال برد هوش مصنوعی (Success Probability):",
                    fontSize = 11.sp,
                    color = TextSecondary,
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
                        val activeColor = when (conf) {
                            90 -> GoldGlow
                            85 -> EmeraldNeon
                            80 -> CyanGlow
                            else -> TextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SlateDark800 else SlateDark900)
                                .border(
                                    0.8.dp,
                                    if (isSelected) activeColor else CardBorder,
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
                                color = if (isSelected) activeColor else TextMuted
                            )
                        }
                    }
                }
            }

            // Section 3: Status Filter (وضعیت اجرا)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "وضعیت سیگنال:",
                    fontSize = 11.sp,
                    color = TextSecondary,
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
                                .background(if (isSelected) SlateDark800 else Color.Transparent)
                                .border(
                                    0.8.dp,
                                    if (isSelected) CyanNeon else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onFilterSelected(code) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSelected) CyanGlow else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
