package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class ArticleSection(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val content: String,
    val tags: List<String>
)

@Composable
fun ArticleGuideScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var expandedSectionId by remember { mutableStateOf<String?>("1") }

    val sections = remember {
        listOf(
            ArticleSection(
                id = "1",
                title = "۱. باینری آپشن چیست و چگونه کار می‌کند؟",
                category = "مبانی",
                summary = "قراردادهای نتیجه‌محور دوحالته (Binary) بر اساس پیش‌بینی جهت قیمت تا زمان مشخص (Expiry).",
                content = "قراردادهای باینری آپشن (Binary Options) خانواده‌ای از مشتقات مالی هستند که در آن‌ها نتیجه معامله صرفاً به دو حالت صفر یا یک (برد یا باخت) وابسته است.\n\nدر ساده‌ترین تعریف، معامله‌گر پیش‌بینی می‌کند که آیا دارایی مورد نظر (مانند EUR/USD یا BTC) در زمان سررسید (مثلاً ۶۰ ثانیه بعد) بالاتر از قیمت ورود خواهد بود یا پایین‌تر.\n\nدر صورت صحت پیش‌بینی، مبلغی بین ۷۰ تا ۹۸ درصد سود خالص (Payout) پرداخت می‌شود؛ در صورت عدم تحقق، مبلغ در معرض ریسک از دست می‌رود. در ایران باینری آپشن، سیگنال‌ها نه بر اساس حدس و گمان، بلکه پس از پردازش رژیم بازار و مدیریت ریاضی ریسک منتشر می‌شوند.",
                tags = listOf("Binary Options", "Call & Put", "Expiry", "Payout")
            ),
            ArticleSection(
                id = "2",
                title = "۲. فرمول‌های ریاضی و نرخ برد سربه‌سر (Break-even Win Rate)",
                category = "ریاضیات",
                summary = "محاسبه حداقل نرخ برد لازم L/(P+L) و ارزش امیدریاضی Expected Value (EV).",
                content = "یکی از خطاهای مرگبار معامله‌گران خرد، عدم درک ریاضیات بازدهی باینری آپشن است. اگر پرداخت بروکر P و زیان L باشد، حداقل نرخ برد سربه‌سر طبق رابطه زیر محاسبه می‌شود:\n\nBreak-even Win Rate = L / (P + L)\n\nبه عنوان مثال اگر بروکر کوتکس یا پوکت آپشن بازدهی 92% (P=0.92) بدهد، حداقل نرخ برد شما برای صفر شدن زیان برابر 52.08% خواهد بود.\n\nهمچنین فرمول ارزش امیدریاضی معامله:\nEV = (p × W) - ((1 - p) × L)\n\nدر پلتفرم Iran Binary Option Trading Signals، تنها سیگنال‌هایی صادر می‌شوند که ارزش امیدریاضی خالص آن‌ها (Net Edge) پس از کسر اسپرد و تاخیر مثبت باشد.",
                tags = listOf("Break-even", "EV Formula", "Expected Value", "Net Edge")
            ),
            ArticleSection(
                id = "3",
                title = "۳. رژیم‌های بازار (Market Regimes) و شاخص‌های ATR و بولینگر",
                category = "تحلیل تکنیکال",
                summary = "تشخیص فازهای روند، رنج، شکست‌های فیک (Fake Breakout) و فشردگی قیمت.",
                content = "بازار همیشه رونددار نیست! معامله در فاز رنج با استراتژی روندی فاجعه‌بار است. پلتفرم ایران باینری ۵ رژیم اصلی را تفکیک می‌کند:\n\n۱. Trend (روند پرقدرت صعودی/نزولی): مناسب برای قراردادهای ادامه‌دهنده Call/Put.\n۲. Range (کانال نوسانی): مناسب برای معکوس شدن از کف و سقف باندها.\n۳. Breakout (شکست واقعی سطح): تایید حجم و تثبیت بالای مقاومت.\n۴. Fake Breakout (شکست جعلی): خروج مقطعی و بازگشت سریع به داخل کانال.\n۵. Compression (فشردگی نوسان): هشدار آماده‌باش برای انفجار قیمت.\n\nشاخص دامنه واقعی میانگین (ATR) برای تشخیص نویز در تایم‌فریم ۱ تا ۵ دقیقه به کار گرفته می‌شود.",
                tags = listOf("Trend", "Range", "Breakout", "Bollinger Bands", "ATR")
            ),
            ArticleSection(
                id = "4",
                title = "۴. معماری سه‌گانه هوش مصنوعی پلتفرم (AI1, AI2, AI3)",
                category = "هوش مصنوعی",
                summary = "تقسیم وظایف میان استراتژیست، مدیر ریسک و ناظر حاکمیتی معاملات.",
                content = "سامانه تصمیم‌یار ما مبتنی بر ۳ عامل مستقل هوش مصنوعی است تا از خطای انسانی و توهم مدل‌ها جلوگیری کند:\n\n🤖 عامل اول (AI1 Strategist & Regime Detector): وظیفه دارد داده‌های لحظه‌ای بازار را بخواند، ساختار سطوح و الگوها را بسنجد و در صورت مساعد بودن، سناریو ورود تدوین کند.\n\n🛡️ عامل دوم (AI2 Risk Architect): نقش محاسبه ریسک، اسپرد، اندازه معامله (Position Size) و ارزیابی اسلیپیج بروکرهای مختلف را بر عهده دارد.\n\n⚖️ عامل سوم (AI3 Trade Governor & Auditor): قدرت وتو (Veto) را در دست دارد. اگر کیفیت داده پایین باشد، نقدشوندگی مشکوک باشد یا بازار در آستانه خبر سنگین باشد، دستور قطعی NO TRADE را صادر می‌کند.",
                tags = listOf("AI1 Strategist", "AI2 Risk Architect", "AI3 Governor", "Local LLM")
            ),
            ArticleSection(
                id = "5",
                title = "۵. فلسفه عدم معامله (NO TRADE) و خطرات مارتینگل",
                category = "مدیریت ریسک",
                summary = "چرا معامله نکردن در شرایط بد بهترین سود است؟ رد قطعی روش تخریبی Martingale.",
                content = "بزرگ‌ترین دشمن معامله‌گران باینری، روش افزایش تصاعدی حجم پس از باخت یا به اصطلاح مارتینگل (Martingale) است. این روش در یک زنجیره چند باخت متوالی، حساب کاربر را به طور کامل صفر می‌کند.\n\nپلتفرم ایران باینری آپشن بر اصل «سرمایه محدود و قابل حفاظت» بنا شده است. یکی از مهم‌ترین خروجی‌های سیستم ما سیگنال NO TRADE است؛ یعنی بازاری که داده معتبر ندارد یا ریسک آن بالاست نباید ترید شود.",
                tags = listOf("No Trade", "Martingale Danger", "Risk Management", "Capital Protection")
            ),
            ArticleSection(
                id = "6",
                title = "۶. بازارهای OTC و تفاوت با بورس‌های رسمی (Nadex و CME)",
                category = "بروکری",
                summary = "سازوکار قیمت‌گذاری Over The Counter در ایام تعطیلات و هشدارهای CFTC/SEC.",
                content = "بازارهای فرابورس یا OTC در باینری آپشن امکان معامله در روزهای شنبه، یکشنبه و ساعات تعطیلی بانک‌ها را فراهم می‌سازند. این نرخ‌ها توسط الگوریتم‌های بروکرها تولید می‌شوند.\n\nدر مقابل، بورس‌های رسمی مانند Nadex در آمریکا و CME قراردادهای رویدادی (Event Contracts) را تحت نظارت CFTC ارائه می‌دهند. در اروپا نیز ESMA محدودیت‌های مشخصی برای حمایت از سرمایه‌گذاران خرد اعمال کرده است. پلتفرم ایران باینری آپشن بر شفافیت کامل و عدم اتکا به وعده‌های فریبنده تاکید دارد.",
                tags = listOf("OTC Markets", "Nadex", "CME", "ESMA", "CFTC")
            ),
            ArticleSection(
                id = "7",
                title = "۷. راهنمای سئو (SEO) و موقعیت‌یابی جغرافیایی (GEO)",
                category = "سئو و متادیتا",
                summary = "کلمات کلیدی رسمی، ساختار اسکیما و بهینه‌سازی موتورهای جستجو.",
                content = "کلیدواژه‌های اصلی پلتفرم:\n• باینری آپشن\n• Binary Options Trading Signals\n• ایران باینری آپشن\n• سیگنال فارکس و OTC\n• ربات تحلیلگر باینری آپشن\n• Pocket Option & Quotex Signals Iran\n\nتگ‌های ژئو: Iran, Persian Gulf, Middle East, Tehran, Global OTC.\nاین پلتفرم با رعایت کامل اصول استانداردهای سئو مدرن، معماری اطلاعات غنی و تولید محتوای تخصصی به معامله‌گران سراسر منطقه خدمات‌رسانی می‌کند.",
                tags = listOf("SEO", "GEO Metadata", "Keywords", "Schema Org")
            )
        )
    }

    val categories = listOf(
        "ALL" to "همه سرفصل‌ها",
        "مبانی" to "مبانی باینری آپشن",
        "ریاضیات" to "ریاضیات و فرمول‌ها",
        "تحلیل تکنیکال" to "تحلیل و رژیم بازار",
        "هوش مصنوعی" to "معماری هوش مصنوعی",
        "مدیریت ریسک" to "مدیریت ریسک و No Trade",
        "بروکری" to "بازارهای OTC و بروکرها",
        "سئو و متادیتا" to "سئو و GEO"
    )

    val filtered = sections.filter { s ->
        val catMatch = if (selectedCategory == "ALL") true else s.category == selectedCategory
        val queryMatch = searchQuery.isBlank() || s.title.contains(searchQuery) || s.content.contains(searchQuery)
        catMatch && queryMatch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دانشنامه جامع Iran Binary Option",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        ),
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "مرجع ۱۶۰ سرفصلی آموزش باینری آپشن، بازارهای جهانی، OTC، هوش مصنوعی و سئو",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = TextSecondary
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("جستجو در بین مقالات و سرفصل‌ها...", fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanGlow) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface,
                    focusedBorderColor = EmeraldNeon,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        // Category Filter Ribbon
        item {
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
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) EmeraldGlow else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Legal & Regulatory Notice Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateDark900)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "یادداشت قانونی و سلب مسئولیت ریسک:",
                            color = AmberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "باینری آپشن محصولاتی با ریسک بالا هستند. هیچ سیستم تحلیلی نباید سود قطعی را تضمین نماید. Iran Binary Option صرفاً ابزار تصمیم‌یار چندلایه و آموزشی است.",
                            color = TextSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Sections
        items(filtered, key = { it.id }) { section ->
            val isExpanded = expandedSectionId == section.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .clickable {
                        expandedSectionId = if (isExpanded) null else section.id
                    },
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateDark800)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(section.category, color = CyanGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = section.summary,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = TextSecondary
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SlateDark900)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = section.content,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 22.sp
                                    ),
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tags
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                section.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldDark.copy(alpha = 0.5f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            color = EmeraldGlow,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
