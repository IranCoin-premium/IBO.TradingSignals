package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.data.local.PlanEntity
import com.example.data.store.AppStore
import com.example.data.local.UserSubscriptionEntity
import com.example.data.repository.OfflineCacheSyncStatus
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

@Composable
fun SubscriptionScreen(
    plans: List<PlanEntity>,
    currentUserPlan: String,
    subscriptions: List<UserSubscriptionEntity> = emptyList(),
    offlineCacheStatus: OfflineCacheSyncStatus? = null,
    usdtFeed: com.example.util.UsdtFeedData = com.example.util.UsdtFeedData(),
    onBuyPlan: (PlanEntity, String, String, (Boolean, String) -> Unit) -> Unit
) {
    var selectedPlanToBuy by remember { mutableStateOf<PlanEntity?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("TETHER") } // TETHER, SHETAB, ZARINPAL
    
    // Multi-step Checkout state
    var checkoutStep by remember { mutableStateOf("SELECTION") } // SELECTION, PRE_FACTOR, VERIFYING, SUCCESS
    var transactionRefInput by remember { mutableStateOf("") }
    var loadingMessage by remember { mutableStateOf("") }
    var serverResponseMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    fun openStorePage(store: AppStore) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(store.deepLinkUri))
            store.packageName?.let { intent.setPackage(it) }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(store.webFallbackUrl)))
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftUiBg),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoftCardPurpleGradient[0])
                        .border(1.dp, SoftCardPurpleAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فروشگاه رسمی اشتراک‌های Iran Binary Option",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = SoftCardPurpleAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "پلن‌های ۵ گانه اشتراک تخصصی سیگنال‌های باینری آپشن",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    ),
                    color = TextDarkPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "دسترسی نامحدود به سیگنال‌های لایو ۱۵ بروکر، الگوریتم‌های هوش مصنوعی AI1/AI2/AI3 و فیلترهای ضدضرر No Trade",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                    color = TextDarkSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Active user plan banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SoftCardMintGradient[0]),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("پلن فعال شما در سامانه:", color = TextDarkSecondary, fontSize = 11.5.sp)
                            Text(currentUserPlan, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoftCardMintAccent)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("فعال", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }

        // Room Offline Storage & Cloud Sync Status Badge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SoftCardCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SoftCardCyanGradient[0])
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SoftCardCyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "پشتیبانی آفلاین Room و همگام‌سازی ابری",
                            color = SoftCardCyanAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "اشتراک‌های شما و سیگنال‌های لایو به صورت آفلاین در دیتابیس دستگاه ذخیره می‌شوند.",
                            color = TextDarkSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Mandatory Risk Disclosure Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftCardPeachAccent.copy(alpha = 0.1f))
                    .border(1.dp, SoftCardPeachAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.",
                    fontSize = 10.5.sp,
                    color = SoftCardPeachAccent,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Live USDT Rate Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SoftCardMintAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SoftCardMintGradient[0])
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SoftCardMintAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CurrencyBitcoin, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("نرخ لحظه‌ای تتر (USDT)", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "منبع: ${usdtFeed.sourceName} • ${usdtFeed.lastUpdatedText}",
                                color = TextDarkSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = com.example.util.UsdtRateFeedService.formatToman(usdtFeed.rateToman),
                            color = SoftCardMintAccent,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (usdtFeed.isUp) SoftCardMintAccent.copy(alpha = 0.15f) else SoftCardPeachAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${if (usdtFeed.isUp) "+" else ""}${usdtFeed.priceChange24hPercent}٪",
                                color = if (usdtFeed.isUp) SoftCardMintAccent else SoftCardPeachAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // The 5 Plans List
        items(plans, key = { it.id }) { plan ->
            val isCurrent = currentUserPlan.contains(plan.durationText) || currentUserPlan == plan.title

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (plan.isPopular) 4.dp else 2.dp, RoundedCornerShape(20.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        if (plan.isPopular) 1.5.dp else 1.dp,
                        if (plan.isPopular) SoftCardPurpleAccent else SoftUiCardBorder,
                        RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = SoftUiSurface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Plan Top Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = plan.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    ),
                                    color = TextDarkPrimary
                                )
                                if (plan.discountPercent > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SoftCardPeachAccent)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${plan.discountPercent}٪ تخفیف",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "دوره زمانی: ${plan.durationText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftCardPurpleAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (plan.isPopular) SoftCardPurpleGradient[0] else SoftUiBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = plan.badge,
                                color = if (plan.isPopular) SoftCardPurpleAccent else TextDarkSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val usdtVal = plan.priceUsdt.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 1.0
                    val calculatedTomanPrice = (usdtVal * usdtFeed.rateToman).toLong()
                    val liveTomanFormatted = com.example.util.UsdtRateFeedService.formatToman(calculatedTomanPrice)

                    // Pricing block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftUiBg)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("قیمت به تومان (محاسبه آنلاین):", color = TextDarkMuted, fontSize = 11.sp)
                            Text(
                                text = liveTomanFormatted,
                                color = TextDarkPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("قیمت دلاری (تتر USDT):", color = TextDarkMuted, fontSize = 11.sp)
                            Text(
                                text = plan.priceUsdt,
                                color = SoftCardMintAccent,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Features checklist
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        plan.features.split(",").forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(SoftCardMintGradient[0]),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature.trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buy button
                    Button(
                        onClick = {
                            selectedPlanToBuy = plan
                            checkoutStep = "SELECTION"
                            errorMessage = null
                            transactionRefInput = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (plan.isPopular) SoftCardPurpleAccent else SoftCardPurpleAccent.copy(alpha = 0.85f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isCurrent) "تمدید این اشتراک" else "انتخاب و خرید ${plan.durationText}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Checkout Modal Dialog
    if (selectedPlanToBuy != null) {
        val plan = selectedPlanToBuy!!
        AlertDialog(
            onDismissRequest = { 
                if (checkoutStep != "VERIFYING") {
                    selectedPlanToBuy = null 
                }
            },
            confirmButton = {
                if (checkoutStep == "SELECTION") {
                    Button(
                        onClick = {
                            checkoutStep = "PRE_FACTOR"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تایید روش پرداخت و دریافت فاکتور فنی", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (checkoutStep == "PRE_FACTOR") {
                    val storeMethod = AppStore.fromMethodId(selectedPaymentMethod)
                    if (storeMethod != null) {
                        Button(
                            onClick = { openStorePage(storeMethod) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(storeMethod.brandColor)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("باز کردن ${storeMethod.titleFa} و ادامه در فروشگاه", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = if (storeMethod.billingSupported)
                                "پرداخت درون‌برنامه‌ای (IAB v3) — فعال‌سازی لایسنس پس از تایید خرید توسط سرور به‌صورت خودکار انجام می‌شود."
                            else
                                "این فروشگاه کانال دریافت/توزیع امن اپ است؛ پرداخت درون‌برنامه‌ای ندارد.",
                            color = TextDarkSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 15.sp
                        )
                    } else {
                        Button(
                            onClick = {
                                if (transactionRefInput.trim().isEmpty()) {
                                    errorMessage = "لطفاً کد پیگیری تراکنش خود را وارد نمایید."
                                    return@Button
                                }
                                
                                checkoutStep = "VERIFYING"
                                errorMessage = null
                                loadingMessage = "در حال ارسال و استعلام اصالت فیش واریزی از درگاه سرور..."
                                
                                onBuyPlan(plan, selectedPaymentMethod, transactionRefInput) { success, msg ->
                                    if (success) {
                                        serverResponseMessage = msg
                                        checkoutStep = "SUCCESS"
                                    } else {
                                        errorMessage = msg
                                        checkoutStep = "PRE_FACTOR"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftCardMintAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ثبت و درخواست تایید فنی تراکنش", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            dismissButton = {
                if (checkoutStep != "VERIFYING" && checkoutStep != "SUCCESS") {
                    OutlinedButton(
                        onClick = { 
                            if (checkoutStep == "PRE_FACTOR") {
                                checkoutStep = "SELECTION"
                            } else {
                                selectedPlanToBuy = null 
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (checkoutStep == "PRE_FACTOR") "بازگشت" else "انصراف", color = TextDarkSecondary)
                    }
                }
            },
            title = {
                Text(
                    text = when (checkoutStep) {
                        "SELECTION" -> "انتخاب روش پرداخت ${plan.title}"
                        "PRE_FACTOR" -> "فاکتور فنی پرداخت و دستورالعمل واریز"
                        "VERIFYING" -> "در حال تایید تراکنش در سرور"
                        "SUCCESS" -> "پرداخت تایید و فعال شد"
                        else -> "پیش‌فاکتور خرید"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDarkPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (checkoutStep == "SELECTION") {
                        Text(
                            text = "مبلغ قابل پرداخت: ${plan.priceToman} معادل ${plan.priceUsdt}",
                            color = SoftCardPeachAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("روش پرداخت مورد نظر خود را انتخاب کنید:", color = TextDarkSecondary, fontSize = 12.sp)

                        // Method 1: Tether USDT
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedPaymentMethod == "TETHER") SoftCardMintGradient[0] else SoftUiBg)
                                .border(1.dp, if (selectedPaymentMethod == "TETHER") SoftCardMintAccent else SoftUiCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedPaymentMethod = "TETHER" }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CurrencyBitcoin, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("درگاه تتر کریپتو (USDT TRC20 / BEP20)", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("واریز خودکار سرور بدون کارمزد بانکی", color = TextDarkSecondary, fontSize = 10.sp)
                            }
                        }

                        // Method 2: Shetab Card to Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedPaymentMethod == "SHETAB") SoftCardPurpleGradient[0] else SoftUiBg)
                                .border(1.dp, if (selectedPaymentMethod == "SHETAB") SoftCardPurpleAccent else SoftUiCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedPaymentMethod = "SHETAB" }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = SoftCardPurpleAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("کارت به کارت شتابی با رسید فیش بانکی", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("اتصال به شبکه شتاب و پایا", color = TextDarkSecondary, fontSize = 10.sp)
                            }
                        }

                        // Method 3: ZarinPal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedPaymentMethod == "ZARINPAL") SoftCardCyanGradient[0] else SoftUiBg)
                                .border(1.dp, if (selectedPaymentMethod == "ZARINPAL") SoftCardCyanAccent else SoftUiCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedPaymentMethod = "ZARINPAL" }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("درگاه پرداخت مستقیم اینترنتی", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("امن‌ترین حالت با رمز دوم پویا", color = TextDarkSecondary, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "یا پرداخت / دریافت از فروشگاه‌های اپلیکیشن منطقه‌ای:",
                            color = TextDarkSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        AppStore.entries.forEach { store ->
                            val storeSelected = (selectedPaymentMethod == store.methodId)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (storeSelected) SoftCardCyanGradient[0] else SoftUiBg)
                                    .border(
                                        1.dp,
                                        if (storeSelected) Color(store.brandColor) else SoftUiCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPaymentMethod = store.methodId }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(store.brandColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = store.logoRes),
                                        contentDescription = store.titleFa,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(store.titleFa, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(
                                        text = if (store.billingSupported) "پرداخت درون‌برنامه‌ای IAB v3" else "کانال دریافت / توزیع",
                                        color = TextDarkSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                if (store.isIos) {
                                    Text("iOS", color = TextDarkMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (checkoutStep == "PRE_FACTOR") {
                        // Display specific instruction based on payment method
                        when (selectedPaymentMethod) {
                            "TETHER" -> {
                                val address = "TYdge1S8p3v5X9M8gK1ZfB8rJ4yA7vD9eC"
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SoftUiBg),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("آدرس کیف پول تتر شبکه TRC20 رسمی:", color = TextDarkSecondary, fontSize = 11.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(address, color = SoftCardMintAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(address)) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی آدرس تتر", tint = TextDarkPrimary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("مبلغ دقیق واریزی: ${plan.priceUsdt}", color = SoftCardPeachAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            "SHETAB" -> {
                                val cardNumber = "5022-2915-7055-0994"
                                val shebaNumber = "IR900190000000221341736005"
                                val accountOwner = "علی خانی"
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SoftUiBg),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("اطلاعات حساب بانکی (کارت به کارت / پایا / ساتنا / پل):", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                        Text("صاحب حساب: $accountOwner", color = SoftCardPurpleAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // Card Number Row
                                        Text("شماره کارت شتاب:", color = TextDarkSecondary, fontSize = 11.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(cardNumber, color = TextDarkPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { clipboardManager.setText(AnnotatedString("5022291570550994")) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی شماره کارت", tint = SoftCardPurpleAccent, modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Sheba Number Row
                                        Text("شماره شبا (پایا / ساتنا / پل):", color = TextDarkSecondary, fontSize = 11.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(shebaNumber, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(shebaNumber)) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی شماره شبا", tint = SoftCardPurpleAccent, modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("مبلغ دقیق به تومان: ${plan.priceToman}", color = SoftCardPeachAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            "ZARINPAL" -> {
                                val link = "https://zarinp.al/iranbinaryoption"
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SoftUiBg),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("لینک درگاه پرداخت الکترونیک زرین‌پال:", color = TextDarkSecondary, fontSize = 11.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(link, color = SoftCardCyanAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(link)) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی لینک درگاه", tint = TextDarkPrimary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("مبلغ تراکنش آنلاین: ${plan.priceToman}", color = SoftCardPeachAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            "STORE_CAFEBAZAAR", "STORE_MYKET", "STORE_APKPURE", "STORE_IRANAPPS" -> {
                                val store = AppStore.fromMethodId(selectedPaymentMethod)!!
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SoftUiBg),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(store.brandColor)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = store.logoRes),
                                                    contentDescription = store.titleFa,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("خرید از طریق ${store.titleFa}", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                                Text(
                                                    text = if (store.billingSupported) "پرداخت درون‌برنامه‌ای امن IAB v3" else if (store.isIos) "فروشگاه iOS" else "صفحه اپ در فروشگاه",
                                                    color = TextDarkSecondary,
                                                    fontSize = 9.5.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "۱. روی «باز کردن ${store.titleFa}» در پایین بزنید. ۲. صفحه این اپ در فروشگاه باز می‌شود. ۳. پس از فعال‌سازی درون‌برنامه‌ای، لایسنس اشتراک به‌صورت خودکار توسط سرور صادر می‌شود.",
                                            color = TextDarkSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (AppStore.fromMethodId(selectedPaymentMethod) == null) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "هشدار امنیتی: واریزی شما تنها با تایید دستی یا خودکار تراکنش در سرور معتبر است. پس از واریز، کد پیگیری و شناسه تراکنش را برای بررسی اصالت و صدور لایسنس تایید نهایی در کادر زیر وارد کنید.",
                                color = TextDarkSecondary,
                                fontSize = 11.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = transactionRefInput,
                                onValueChange = { transactionRefInput = it },
                                label = { Text("کد پیگیری تراکنش (مثلاً TX-102030)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SoftCardPurpleAccent,
                                    unfocusedBorderColor = SoftUiCardBorder,
                                    focusedLabelColor = SoftCardPurpleAccent,
                                    unfocusedLabelColor = TextDarkMuted,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        errorMessage?.let { err ->
                            Text(err, color = Color.Red, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (checkoutStep == "VERIFYING") {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = SoftCardPurpleAccent, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = loadingMessage,
                                color = TextDarkPrimary,
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (checkoutStep == "SUCCESS") {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SoftCardMintGradient[0]),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SoftCardMintAccent, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = serverResponseMessage,
                                color = SoftCardMintAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { selectedPlanToBuy = null },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("بستن و مشاهده سیگنال‌های لایو", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            containerColor = SoftUiSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
