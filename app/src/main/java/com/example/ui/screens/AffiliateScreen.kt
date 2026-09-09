package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.UsdtFeedData
import com.example.util.UsdtRateFeedService

@Composable
fun AffiliateScreen(
    userUsdtWallet: String,
    referralCode: String,
    referralEarningsUsdt: Double,
    usdtFeed: UsdtFeedData,
    onSaveWallet: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var walletInput by remember(userUsdtWallet) { mutableStateOf(userUsdtWallet) }
    var walletSavedMessage by remember { mutableStateOf("") }
    var isEditingWallet by remember { mutableStateOf(userUsdtWallet.isBlank()) }

    val totalTomanEarnings = (referralEarningsUsdt * usdtFeed.rateToman).toLong()

    val commissionRules = remember {
        listOf(
            CommissionRule("اشتراک ۱ هفته‌ای", "1 USDT", "۶۲,۵۰۰ تومان", "واریز آنی به کیف پول TRC20", SoftCardCyanAccent),
            CommissionRule("اشتراک ۱ ماهه", "2 USDT", "۱۲۵,۰۰۰ تومان", "واریز آنی به کیف پول TRC20", SoftCardMintAccent),
            CommissionRule("اشتراک ۳ ماهه VIP (محبوب)", "5 USDT", "۳۱۲,۵۰۰ تومان", "پاداش ویژه + واریز آنی", SoftCardPurpleAccent),
            CommissionRule("اشتراک ۶ ماهه VIP", "7 USDT", "۴۳۷,۵۰۰ تومان", "پاداش ویژه + واریز آنی", SoftCardPeachAccent),
            CommissionRule("اشتراک ۱ ساله طلایی", "10 USDT", "۶۲۵,۰۰ تومان", "بالاترین سطح پاداش افیلیت", SoftCardMintAccent)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftUiBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live USDT Ticker & Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(22.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SoftCardMintGradient[0]),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "USDT Rate",
                                    tint = SoftCardMintAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "کسب درآمد دلاری و افیلیت (USDT)",
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "پاداش آنی به ازای هر معرفی موفق",
                                    color = TextDarkSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Live Rate Chip with Cache / API Status
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SoftCardMintGradient[0])
                                    .border(1.dp, SoftCardMintAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (usdtFeed.isFromCache) Icons.Default.Storage else Icons.Default.Sync,
                                        contentDescription = "Live",
                                        tint = SoftCardMintAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تتر: ${UsdtRateFeedService.formatToman(usdtFeed.rateToman)}",
                                        color = SoftCardMintAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${usdtFeed.sourceName} (${usdtFeed.lastUpdatedText})",
                                color = TextDarkMuted,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "با دعوت دوستان و تریدرها به پلتفرم ایران باینری آپشن، به صورت کاملاً غیرفعال درآمد دلاری کسب کنید! پاداش هر خرید بلافاصله بر اساس تتر (USDT) محاسبه و مستقیم به کیف پول شما واریز می‌شود.",
                        color = TextDarkSecondary,
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 2. Earnings Overview Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // USDT Earnings Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, SoftCardMintAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("کل درآمد دلاری", color = TextDarkSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${referralEarningsUsdt.toInt()} USDT",
                            color = SoftCardMintAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "تسویه آنی TRC20",
                            color = TextDarkMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Toman Equivalent Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, SoftCardPurpleAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("معادل تومانی روز", color = TextDarkSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = UsdtRateFeedService.formatToman(totalTomanEarnings),
                            color = SoftCardPurpleAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "بر اساس نرخ لحظه‌ای تتر",
                            color = TextDarkMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 3. Referral Code & Referral Link Sharing Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftCardCyanAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "لینک و کد اختصاصی دعوت شما",
                        color = TextDarkPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val refLink = "https://iranbinaryoption.com/ref/$referralCode"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftUiBg)
                            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("کد معرفی اختصاصی:", color = TextDarkMuted, fontSize = 10.sp)
                            Text(referralCode, color = SoftCardCyanAccent, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(referralCode))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SoftCardCyanAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val shareText = "سلام! با سیگنال‌های هوش مصنوعی و تحلیلگر انسانی ایران باینری آپشن سود دلاری بگیرید. لینک عضویت VIP با پاداش ویژه: $refLink"

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری کد و لینک دعوت"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCardCyanAccent)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اشتراک‌گذاری سریع لینک در تلگرام و واتساپ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 4. USDT TRC20 Wallet Address Setting Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftCardPeachAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = SoftCardPeachAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آدرس کیف پول تتر (USDT - TRC20 / BEP20)",
                                color = TextDarkPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (userUsdtWallet.isNotBlank() && !isEditingWallet) {
                            TextButton(onClick = { isEditingWallet = true }) {
                                Text("ویرایش آدرس", color = SoftCardPeachAccent, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "جهت واریز آنی و مستقیم پورسانت‌های دلاری، آدرس کیف پول تتر خود را ثبت و تایید کنید:",
                        color = TextDarkSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditingWallet) {
                        OutlinedTextField(
                            value = walletInput,
                            onValueChange = { walletInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("نمونه: TYdge1S8p3v5X9M8gK1ZfB8rJ4yA7vD9eC", fontSize = 11.sp, color = TextDarkMuted) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftCardPeachAccent,
                                unfocusedBorderColor = SoftUiCardBorder,
                                focusedContainerColor = SoftUiBg,
                                unfocusedContainerColor = SoftUiBg,
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (walletInput.isNotBlank()) {
                                    onSaveWallet(walletInput)
                                    isEditingWallet = false
                                    walletSavedMessage = "آدرس کیف پول تتر شما با موفقیت ثبت و تایید گردید."
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftCardPeachAccent)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تایید و ذخیره آدرس کیف پول جهت واریز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoftUiBg)
                                .border(1.dp, SoftCardMintAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("آدرس کیف پول تایید شده:", color = SoftCardMintAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(userUsdtWallet, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                IconButton(onClick = { clipboardManager.setText(AnnotatedString(userUsdtWallet)) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextDarkPrimary)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = walletSavedMessage.isNotBlank()) {
                        Text(
                            text = walletSavedMessage,
                            color = SoftCardMintAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        // 5. Commission Table & Payout Rates Section
        item {
            Text(
                text = "جدول پورسانت و پاداش آنی دلاری (به ازای هر مشتری)",
                color = TextDarkPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        items(commissionRules) { rule ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rule.planTitle, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(rule.note, color = TextDarkSecondary, fontSize = 10.5.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(rule.accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = rule.usdtReward,
                                color = rule.accentColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "(${rule.tomanReward})",
                            color = TextDarkMuted,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}

private data class CommissionRule(
    val planTitle: String,
    val usdtReward: String,
    val tomanReward: String,
    val note: String,
    val accentColor: Color
)
