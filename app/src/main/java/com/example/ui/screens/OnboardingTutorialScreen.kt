package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import kotlinx.coroutines.launch

data class TutorialSlide(
    val stepNumber: Int,
    val badge: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val keyPoints: List<Pair<String, String>>,
    val tips: String
)

@Composable
fun OnboardingTutorialScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val slides = listOf(
        TutorialSlide(
            stepNumber = 1,
            badge = "گام ۱ از ۴ • شناخت ساختار",
            title = "آناتومی یک سیگنال باینری آپشن",
            subtitle = "هر سیگنال شامل دارایی، جهت و نقطه ورود دقیق لحظه‌ای است.",
            icon = Icons.Default.TrendingUp,
            accentColor = EmeraldNeon,
            keyPoints = listOf(
                "جفت‌ارز و دارایی (Asset)" to "نام دارایی معامله مثل EUR/USD یا بازارهای ۲۴ ساعته OTC بروکرها.",
                "جهت معامله (CALL یا PUT)" to "CALL (خرید سبز 🟢) به معنی صعود قیمت و PUT (فروش قرمز 🔴) به معنی نزول قیمت در زمان انقضا.",
                "قیمت استرایک (Strike Price)" to "نقطه قیمت پیشنهادی لحظه صدور سیگنال؛ در قیمتی برابر یا بهتر از آن وارد پوزیشن شوید."
            ),
            tips = "نکته کلیدی: قبل از زدن دکمه ترید، نام جفت‌ارز را در بروکر با سیگنال مطابقت دهید."
        ),
        TutorialSlide(
            stepNumber = 2,
            badge = "گام ۲ از ۴ • زمان‌بندی دقیق",
            title = "تایم‌فریم و زمان انقضا (Expiry)",
            subtitle = "معاملات باینری آپشن دارای زمان خاتمه از پیش تعیین‌شده (Fixed-Time) هستند.",
            icon = Icons.Default.Timer,
            accentColor = CyanNeon,
            keyPoints = listOf(
                "زمان انقضا (Expiry Time)" to "مدت زمان فعال بودن معامله در بروکر (معمولاً ۱ دقیقه یا ۵ دقیقه).",
                "قانون طلایی ثانیه صفر (۰۰s)" to "برای به حداکثر رساندن سود، در ثانیه‌های آغازین کندل جدید پوزیشن را باز کنید.",
                "نرخ بازدهی بروکر (Payout)" to "همواره دارایی‌هایی با بازدهی ۸۵٪ تا ۹۶٪ را در بروکر برای اجرای معامله انتخاب نمایید."
            ),
            tips = "توصیه حرفه‌ای: تایمر بروکر خود را روی مود 'زمان ثابت' (Fixed Time) قرار دهید."
        ),
        TutorialSlide(
            stepNumber = 3,
            badge = "گام ۳ از ۴ • دقت و امنیت",
            title = "وین‌ریت تحلیلی و فیلتر وتو هوشمند",
            subtitle = "تمام سیگنال‌ها از فیلترهای چندگانه هوش مصنوعی و مدیریت ریسک عبور می‌کنند.",
            icon = Icons.Default.Psychology,
            accentColor = AmberGold,
            keyPoints = listOf(
                "وین‌ریت پیش‌بینی‌شده (Win-Rate)" to "امتیاز اطمینان تحلیل (بالای ۸۰٪ سیگنال طلایی و با اولویت بالا محسوب می‌شود).",
                "سیستم وتو و عدم معامله (NO_TRADE 🛡️)" to "در زمان سخنرانی‌های خبری پرریسک یا نوسانات نامتعارف، سیستم اخطار توقف صادر می‌کند.",
                "پوش نوتیفیکیشن فوری (FCM)" to "سیگنال‌های با دقت بالا به صورت لحظه‌ای با اعلان صوتی به گوشی شما ارسال می‌شوند."
            ),
            tips = "حفظ سرمایه: هر زمان وضعیت سیگنال 'وتو شده' بود، از ورود به ترید جداً خودداری کنید."
        ),
        TutorialSlide(
            stepNumber = 4,
            badge = "گام ۴ از ۴ • اجرا و سودآوری",
            title = "اجرای گام‌به‌گام در بروکر و مدیریت سرمایه",
            subtitle = "مراحل نهایی اجرای ترید در پاکت آپشن، کوتکس و سایر بروکرها.",
            icon = Icons.Default.RocketLaunch,
            accentColor = EmeraldNeon,
            keyPoints = listOf(
                "۱. باز کردن پلتفرم بروکر" to "بروکر خود (Pocket Option, Quotex, Olymp Trade) را باز کرده و جفت‌ارز را انتخاب کنید.",
                "۲. اعمال تایم‌فریم و انقضا" to "زمان معامله را مطابق سیگنال (مثلاً M1 یا M5) تنظیم کنید.",
                "۳. قانون طلایی ۱ تا ۲ درصد" to "هرگز بیش از ۱ الی ۲ درصد کل بالانس حسابتان را در یک معامله ریسک نکنید.",
                "۴. زدن دکمه HIGHER یا LOWER" to "بر اساس جهت CALL (سبز) یا PUT (قرمز) بلافاصله کلیک کنید."
            ),
            tips = "پرهیز از مارتینگل سنگین: انضباط معاملاتی رمز موفقیت و سودآوری مستمر شماست."
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    fun completeTutorial() {
        val prefs = context.getSharedPreferences("iran_binary_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_completed_onboarding_tutorial", true).apply()
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Logo / Step indicator & Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateDark900)
                            .border(1.dp, EmeraldNeon.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "راهنمای معاملات باینری آپشن",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = "آموزش تصویری خواندن و اجرای سیگنال‌ها",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                TextButton(
                    onClick = { completeTutorial() }
                ) {
                    Text(
                        text = "رد کردن",
                        color = CyanGlow,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.5.sp
                    )
                }
            }

            // Hero visual banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_trading_onboarding),
                    contentDescription = "تصویر آموزشی ترید باینری آپشن",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    SlateDark950.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = EmeraldNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سیستم سیگنال‌دهی فوق سریع ایران باینری با وین‌ریت تاییدشده",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Swipeable Content Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                val slide = slides[pageIndex]
                TutorialSlideContent(slide = slide)
            }

            // Bottom Navigation & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateDark900)
                    .border(1.dp, CardBorder)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Page Dots / Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.indices.forEach { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 8.dp,
                            animationSpec = spring(),
                            label = "dot_width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) EmeraldNeon else SlateDark800,
                            label = "dot_color"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Previous & Next / Finish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "صفحه قبلی",
                                modifier = Modifier.size(16.dp),
                                tint = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("گام قبلی", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    val isLastPage = pagerState.currentPage == slides.size - 1

                    Button(
                        onClick = {
                            if (isLastPage) {
                                completeTutorial()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastPage) EmeraldNeon else CyanNeon
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(if (pagerState.currentPage > 0) 1.5f else 1f)
                    ) {
                        Text(
                            text = if (isLastPage) "ورود به معاملات و شروع ترید" else "گام بعدی",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (isLastPage) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TutorialSlideContent(slide: TutorialSlide) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .verticalScroll(scrollState)
    ) {
        // Step Badge & Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(slide.accentColor.copy(alpha = 0.15f))
                    .border(1.dp, slide.accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = slide.badge,
                    color = slide.accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SlateDark900)
                    .border(1.dp, slide.accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = slide.icon,
                    contentDescription = null,
                    tint = slide.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title & Subtitle
        Text(
            text = slide.title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = slide.subtitle,
            color = TextSecondary,
            fontSize = 11.5.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Slide Specific Interactive Mockup
        when (slide.stepNumber) {
            1 -> MockSignalCardDemo()
            2 -> MockTimeframeDemo()
            3 -> MockVetoAndWinRateDemo()
            4 -> MockBrokerExecutionDemo()
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Points Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateDark900),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                slide.keyPoints.forEach { (boldTitle, desc) ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(slide.accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = boldTitle,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 10.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pro Tip Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EmeraldDark.copy(alpha = 0.4f))
                .border(1.dp, EmeraldNeon.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = slide.tips,
                    color = TextPrimary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------------------------------------------
// Interactive Visual Mockups for Each Tutorial Slide
// -------------------------------------------------------------------------------------------------

@Composable
fun MockSignalCardDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("EUR/USD (OTC)", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("انقضا: ۱ دقیقه", color = TextSecondary, fontSize = 10.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldNeon)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خرید (CALL)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("قیمت استرایک ورود:", color = TextMuted, fontSize = 9.5.sp)
                    Text("1.08500", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("وین‌ریت پیش‌بینی:", color = TextMuted, fontSize = 9.5.sp)
                    Text("۹۴٪ (تایید هوش مصنوعی)", color = EmeraldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MockTimeframeDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyanNeon.copy(alpha = 0.15f))
                        .border(1.dp, CyanNeon, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1M", color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("تایمر ۶۰ ثانیه", color = TextSecondary, fontSize = 9.5.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SlateDark800),
                    contentAlignment = Alignment.Center
                ) {
                    Text("5M", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("تایمر ۵ دقیقه", color = TextSecondary, fontSize = 9.5.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldDark)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("ورود در ثانیه ۰۰", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("بیشترین ضریب برد", color = TextMuted, fontSize = 9.5.sp)
            }
        }
    }
}

@Composable
fun MockVetoAndWinRateDemo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // High Win-rate Card
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("وین‌ریت بالای ۸۵٪", color = EmeraldNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("سیگنال طلایی مجاز برای ورود", color = TextSecondary, fontSize = 9.sp, textAlign = TextAlign.Center)
            }
        }

        // Veto Filter Card
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonGlow.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("فیلتر وتو (NO_TRADE)", color = CrimsonRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("هشدار خبر خطرناک / ورود ممنوع", color = TextSecondary, fontSize = 9.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun MockBrokerExecutionDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("۱. اعلان فوری FCM دریافت شد", color = CyanGlow, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(14.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("۲. بروکر باز شد: انتخاب EUR/USD و تایمر M1", color = TextPrimary, fontSize = 10.5.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("۳. حجم ترید: حداکثر ۱ تا ۲ درصد موجودی کل", color = AmberGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("۴. کلیک فوری روی دکمه سبز HIGHER یا قرمز LOWER", color = EmeraldNeon, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
