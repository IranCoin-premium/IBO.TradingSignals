package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NewsEntity
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonRed
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
fun NewsScreen(
    newsList: List<NewsEntity>,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedImpactFilter by remember { mutableStateOf("ALL") }
    var viewingNews by remember { mutableStateOf<NewsEntity?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    val categories = listOf(
        "ALL" to "همه بازارها",
        "OTC" to "باینری و OTC",
        "COMMODITIES" to "طلا و فلزات",
        "FOREX" to "جفت‌ارزهای فارکس",
        "CRYPTO" to "رمزارزها",
        "MACRO" to "اقتصاد کلان و بهره"
    )

    // Filter news based on category, search query, and impact
    val filteredNews = newsList.filter { news ->
        val matchesCategory = selectedCategory == "ALL" || news.category == selectedCategory
        val matchesImpact = selectedImpactFilter == "ALL" || news.impact == selectedImpactFilter
        val matchesQuery = searchQuery.isBlank() ||
                news.title.contains(searchQuery, ignoreCase = true) ||
                news.summary.contains(searchQuery, ignoreCase = true) ||
                news.source.contains(searchQuery, ignoreCase = true) ||
                news.category.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesImpact && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Live Indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Feed,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اخبار مالی و تحلیل بازارهای ۲۰۲۶",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.5.sp
                            ),
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "رصد اخبار اقتصادی تأثیرگذار بر معاملات باینری آپشن و نوسانات کوتاه‌مدت",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        isRefreshing = true
                        onRefresh()
                        Toast.makeText(context, "فید اخبار و تحلیل‌های مالی به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                        isRefreshing = false
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateDark800)
                        .testTag("refresh_news_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh News",
                        tint = EmeraldNeon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Market Pulse Banners (2026 Key Highlights)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    MarketPulseCard(
                        icon = Icons.Default.AutoGraph,
                        iconTint = AmberGold,
                        title = "انس جهانی طلا (XAU)",
                        value = "$4,900 هدف گلدمن ساکس",
                        subtext = "تقاضای ۳۸۰ میلیارد دلاری خرید بانک‌های مرکزی",
                        tag = "مومنتوم صعودی"
                    )
                }
                item {
                    MarketPulseCard(
                        icon = Icons.Default.Security,
                        iconTint = CyanNeon,
                        title = "باینری آپشن Cboe و Nasdaq",
                        value = "درخواست نظارت SEC",
                        subtext = "رگولاسیون رسمی با ریسک/پاداش معین",
                        tag = "رگولاتوری ۲۰۲۶"
                    )
                }
                item {
                    MarketPulseCard(
                        icon = Icons.Default.CurrencyBitcoin,
                        iconTint = EmeraldNeon,
                        title = "کریپتو و بیت‌کوین",
                        value = "مارکت کپ $2.82T",
                        subtext = "بیت‌کوین در مرز ۸۱,۰۰۰$ و رشد تقاضا",
                        tag = "نقدینگی بالا"
                    )
                }
                item {
                    MarketPulseCard(
                        icon = Icons.Default.CurrencyExchange,
                        iconTint = CrimsonGlow,
                        title = "جفت‌ارزهای فارکس",
                        value = "چشم‌انداز کاهش نرخ بهره",
                        subtext = "تقویت ین ژاپن و تعادل EUR/USD",
                        tag = "نوسانات سشن‌ها"
                    )
                }
            }
        }

        // 3. Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_search_field"),
                placeholder = {
                    Text(
                        text = "جستجو در اخبار (مثلاً طلا، بیت‌کوین، Cboe، فدرال رزرو، OTC...)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = CyanNeon,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "پاک کردن",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = SlateDark900,
                    unfocusedContainerColor = SlateDark900,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        // 4. Categories & Impact Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (code, label) ->
                        val isSelected = selectedCategory == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldDark else SlateDark900)
                                .border(1.dp, if (isSelected) EmeraldNeon else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = code }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("category_chip_$code")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) EmeraldGlow else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                // Impact quick filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "درجه اهمیت:",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        ImpactFilterPill(
                            label = "همه",
                            isSelected = selectedImpactFilter == "ALL",
                            activeColor = CyanNeon,
                            onClick = { selectedImpactFilter = "ALL" }
                        )

                        ImpactFilterPill(
                            label = "🔴 پرریسک (HIGH)",
                            isSelected = selectedImpactFilter == "HIGH",
                            activeColor = CrimsonRed,
                            onClick = { selectedImpactFilter = "HIGH" }
                        )

                        ImpactFilterPill(
                            label = "🟡 متوسط",
                            isSelected = selectedImpactFilter == "MEDIUM",
                            activeColor = AmberGold,
                            onClick = { selectedImpactFilter = "MEDIUM" }
                        )
                    }

                    Text(
                        text = "${filteredNews.size} خبر",
                        color = CyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 5. Empty State
        if (filteredNews.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateDark900)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "خبری با مشخصات جستجو شده یافت نشد.",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "می‌توانید فیلتر دسته‌بندی یا عبارت جستجو را پاک کنید.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "ALL"
                                selectedImpactFilter = "ALL"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("نمایش همه اخبار", color = CyanNeon, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 6. News List Items
        items(filteredNews, key = { it.id }) { news ->
            val isBullish = "صعودی" in news.sentiment || "Bullish" in news.sentiment
            val isBearish = "نزولی" in news.sentiment || "Bearish" in news.sentiment
            val sentimentColor = when {
                isBullish -> EmeraldNeon
                isBearish -> CrimsonRed
                else -> CyanNeon
            }

            val impactColor = when (news.impact) {
                "HIGH" -> CrimsonRed
                "MEDIUM" -> AmberGold
                else -> CyanNeon
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .clickable { viewingNews = news }
                    .testTag("news_item_card_${news.id}"),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top Bar (Category + Impact + Sentiment)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SlateDark800)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = news.category,
                                    color = CyanGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(impactColor.copy(alpha = 0.15f))
                                    .border(1.dp, impactColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (news.impact == "HIGH") "اهمیت بالا (تأثیر فوری)" else "اهمیت متوسط",
                                    color = impactColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                            }
                        }

                        // Sentiment Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when {
                                    isBullish -> Icons.Default.TrendingUp
                                    isBearish -> Icons.Default.TrendingDown
                                    else -> Icons.Default.TrendingFlat
                                },
                                contentDescription = null,
                                tint = sentimentColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = news.sentiment,
                                color = sentimentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Summary
                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.5.sp,
                            fontSize = 11.5.sp
                        ),
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Binary Trading Strategy Hint
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateDark900)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (news.category) {
                                    "COMMODITIES" -> "استراتژی باینری: مناسب قراردادهای ۱ و ۳ دقیقه CALL در پولبک‌های حمایتی طلا."
                                    "OTC" -> "استراتژی باینری: استفاده از فیلتر اسلیپیج در بروکرهای پاکت آپشن و کوتکس."
                                    "CRYPTO" -> "استراتژی باینری: شکست‌های M5 در بیت‌کوین و اتریوم با پاداش بالای ۸۸٪."
                                    "FOREX" -> "استراتژی باینری: توقف ورود ۱۰ دقیقه قبل و بعد از بیانیه‌های فدرال رزرو."
                                    else -> "استراتژی باینری: رعایت مدیریت سرمایه حداکثر ۲٪ بالانس در هر پوزیشن."
                                },
                                color = AmberGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Source & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "منبع: ${news.source}",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = news.timeAgo,
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Full News Details Dialog
    if (viewingNews != null) {
        val item = viewingNews!!
        val isBullish = "صعودی" in item.sentiment || "Bullish" in item.sentiment
        val isBearish = "نزولی" in item.sentiment || "Bearish" in item.sentiment
        val sentimentColor = when {
            isBullish -> EmeraldNeon
            isBearish -> CrimsonRed
            else -> CyanNeon
        }

        AlertDialog(
            onDismissRequest = { viewingNews = null },
            confirmButton = {
                Button(
                    onClick = { viewingNews = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("close_news_dialog_button")
                ) {
                    Text("متوجه شدم و بستن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "دسته‌بندی: ${item.category}",
                            color = CyanGlow,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "سنتیمنت: ${item.sentiment}",
                            color = sentimentColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = item.fullContent,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 23.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // AI Trading Governor Recommendation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateDark900)
                            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "توصیه سیستم هوش مصنوعی ایران باینری (AI Governor):",
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "در زمان نوسانات این خبر، از ورود به تایم‌فریم‌های زیر ۳۰ ثانیه پرهیز نموده و صرفاً با تایید پترن‌های تاییدشده در بروکرهای معتبر (Quotex, Pocket Option, Nadex) ترید نمایید.",
                                color = TextSecondary,
                                fontSize = 10.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "منبع موثق: ${item.source}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = item.timeAgo,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ImpactFilterPill(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.18f) else SlateDark900)
            .border(1.dp, if (isSelected) activeColor else CardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) activeColor else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun MarketPulseCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    subtext: String,
    tag: String
) {
    Box(
        modifier = Modifier
            .width(210.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SlateDark900, SlateDark950)
                )
            )
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(iconTint.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tag,
                        color = iconTint,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp
            )

            Text(
                text = subtext,
                color = TextMuted,
                fontSize = 9.5.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }
}
