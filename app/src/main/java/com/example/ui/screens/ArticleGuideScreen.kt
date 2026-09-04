package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EncyclopediaItem
import com.example.data.model.EncyclopediaRepository
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArticleGuideScreen(
    onBack: (() -> Unit)? = null
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    val tabTitles = listOf(
        "همه (۱۰۹ سرفصل)" to Icons.Default.Bookmark,
        "بخش ۶ تایی (قوانین طلایی)" to Icons.Default.Shield,
        "بخش ۳۶ تایی (کندل‌استیک)" to Icons.Default.ShowChart,
        "بخش ۶۷ تایی (اصطلاحات و استراتژی)" to Icons.Default.AutoGraph
    )

    // Filter items based on active tab
    val baseList = remember(selectedTabIndex) {
        when (selectedTabIndex) {
            1 -> EncyclopediaRepository.sectionSixItems
            2 -> EncyclopediaRepository.sectionThirtySixItems
            3 -> EncyclopediaRepository.sectionSixtySevenItems
            else -> EncyclopediaRepository.allItems
        }
    }

    // Dynamic categories extracted from base list
    val availableCategories = remember(baseList) {
        listOf("ALL") + baseList.map { it.category }.distinct()
    }

    // Filtered list based on search and category
    val filteredList by remember(baseList, searchQuery, selectedCategoryFilter) {
        derivedStateOf {
            baseList.filter { item ->
                val matchesCategory = selectedCategoryFilter == "ALL" || item.category == selectedCategoryFilter
                val matchesSearch = if (searchQuery.isBlank()) true else {
                    val query = searchQuery.trim().lowercase()
                    item.title.lowercase().contains(query) ||
                            item.titleEn.lowercase().contains(query) ||
                            item.summary.lowercase().contains(query) ||
                            item.fullContent.lowercase().contains(query) ||
                            item.practicalTip.lowercase().contains(query) ||
                            item.tags.any { it.lowercase().contains(query) }
                }
                matchesCategory && matchesSearch
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950)
            .testTag("article_guide_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SlateDark900, SlateDark950)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (onBack != null) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SlateDark800)
                                        .testTag("article_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "بازگشت",
                                        tint = CyanGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = CyanGlow,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "دانشنامه جامع Iran Binary Option",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "مرجع کامل ۳ بخش طلایی: ۶ قانون بقا • ۳۶ الگوی کندل‌استیک • ۶۷ اصطلاح تخصصی",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Stats Ribbon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickStatCard(
                            modifier = Modifier.weight(1f),
                            title = "بخش ۶ تایی",
                            count = "۶ اصل",
                            subtitle = "قوانین مدیریت ریسک",
                            accentColor = AmberGold,
                            isSelected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 }
                        )
                        QuickStatCard(
                            modifier = Modifier.weight(1f),
                            title = "بخش ۳۶ تایی",
                            count = "۳۶ الگو",
                            subtitle = "کندل‌استیک و پرایس‌اکشن",
                            accentColor = EmeraldNeon,
                            isSelected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 }
                        )
                        QuickStatCard(
                            modifier = Modifier.weight(1f),
                            title = "بخش ۶۷ تایی",
                            count = "۶۷ اصطلاح",
                            subtitle = "فرمول، اندیکاتور و OTC",
                            accentColor = CyanGlow,
                            isSelected = selectedTabIndex == 3,
                            onClick = { selectedTabIndex = 3 }
                        )
                    }
                }
            }
        }

        // 2. Tabs Bar
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SlateDark900,
                contentColor = CyanGlow,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = CyanGlow,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                tabTitles.forEachIndexed { index, pair ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            selectedCategoryFilter = "ALL"
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = pair.second,
                                    contentDescription = null,
                                    tint = if (selectedTabIndex == index) CyanGlow else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = pair.first,
                                    color = if (selectedTabIndex == index) TextPrimary else TextMuted,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }
            }
        }

        // 3. Search & Filter Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("article_search_field"),
                    placeholder = {
                        Text(
                            "جستجو در عنوان، فرمول، الگو، اندیکاتور یا تگ...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = CyanGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاک کردن",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateDark900,
                        unfocusedContainerColor = SlateDark900,
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                if (availableCategories.size > 2) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableCategories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            val label = if (cat == "ALL") "همه دسته‌ها" else cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) CyanGlow.copy(alpha = 0.2f) else SlateDark900)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanGlow else CardBorder,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedCategoryFilter = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) CyanNeon else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Section Count Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نمایش ${filteredList.size} سرفصل آموزشی معتبر",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                if (searchQuery.isNotEmpty() || selectedCategoryFilter != "ALL") {
                    Text(
                        text = "پاک‌سازی فیلترها",
                        color = AmberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedCategoryFilter = "ALL"
                        }
                    )
                }
            }
        }

        // 5. Items List
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سرفصلی با این مشخصات یافت نشد.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                val isExpanded = expandedItemId == item.id
                EncyclopediaCard(
                    item = item,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedItemId = if (isExpanded) null else item.id
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier,
    title: String,
    count: String,
    subtitle: String,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.15f) else CardSurface)
            .border(
                1.dp,
                if (isSelected) accentColor else CardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = TextMuted, fontSize = 9.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(count, color = accentColor, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(subtitle, color = TextSecondary, fontSize = 8.5.sp, maxLines = 1)
        }
    }
}

@Composable
private fun EncyclopediaCard(
    item: EncyclopediaItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val accentColor = when (item.sectionType) {
        "SECTION_6" -> AmberGold
        "SECTION_36" -> EmeraldNeon
        else -> CyanGlow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isExpanded) accentColor.copy(alpha = 0.6f) else CardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggleExpand() }
            .testTag("encyclopedia_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Badges & Section Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Section badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.sectionTitleBadge,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Direction Badge (CALL / PUT / NEUTRAL)
                    if (item.direction == "CALL") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDark.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("سیگنال CALL", color = EmeraldNeon, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (item.direction == "PUT") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CrimsonGlow.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = CrimsonGlow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("سیگنال PUT", color = CrimsonGlow, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (item.winRate.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldGlow.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "وین‌ریت: ${item.winRate}",
                                color = GoldGlow,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                fontSize = 13.5.sp
            )

            if (item.titleEn.isNotBlank()) {
                Text(
                    text = item.titleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanNeon.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp,
                fontSize = 11.5.sp
            )

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = CardBorder
                    )

                    // Full Content Section
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "تحلیل جامع و سازوکار:",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.fullContent,
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    if (item.practicalTip.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateDark900)
                                .border(1.dp, AmberGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "نکته طلایی و استراتژی اجرایی در بروکر:",
                                        color = AmberGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.practicalTip,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    // Tags
                    if (item.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(item.tags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SlateDark800)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Expand / Collapse Footer Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlateDark800)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "دسته: ${item.category}",
                        color = TextMuted,
                        fontSize = 9.5.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleExpand() }
                ) {
                    Text(
                        text = if (isExpanded) "بستن جزئیات" else "مشاهده تحلیل کامل و استراتژی",
                        color = accentColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
