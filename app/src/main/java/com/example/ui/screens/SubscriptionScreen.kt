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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlanEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.data.repository.OfflineCacheSyncStatus
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
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
fun SubscriptionScreen(
    plans: List<PlanEntity>,
    currentUserPlan: String,
    subscriptions: List<UserSubscriptionEntity> = emptyList(),
    offlineCacheStatus: OfflineCacheSyncStatus? = null,
    onBuyPlan: (PlanEntity) -> Unit
) {
    var selectedPlanToBuy by remember { mutableStateOf<PlanEntity?>(null) }
    var showSuccessDialog by remember { mutableStateOf<String?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("TETHER") } // TETHER, SHETAB, ZARINPAL

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950),
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
                        .background(EmeraldDark.copy(alpha = 0.6f))
                        .border(1.dp, EmeraldNeon.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فروشگاه رسمی اشتراک‌های Iran Binary Option",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGlow
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
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "دسترسی نامحدود به سیگنال‌های لایو ۱۵ بروکر، الگوریتم‌های هوش مصنوعی AI1/AI2/AI3 و فیلترهای ضدضرر No Trade",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Active user plan banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
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
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("پلن فعال شما در سامانه:", color = TextSecondary, fontSize = 11.5.sp)
                            Text(currentUserPlan, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldNeon)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("فعال", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }

        // Room Offline Storage & Cloud Sync Status Badge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateDark900)
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
                            .background(EmeraldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "پشتیبانی آفلاین Room و همگام‌سازی ابری Firestore",
                            color = EmeraldGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "اشتراک‌های شما و سیگنال‌های لایو به صورت آفلاین در دیتابیس دستگاه ذخیره می‌شوند.",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
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
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        if (plan.isPopular) 1.5.dp else 1.dp,
                        if (plan.isPopular) EmeraldNeon else CardBorder,
                        RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (plan.isPopular) SlateDark900 else CardSurface
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
                                    color = TextPrimary
                                )
                                if (plan.discountPercent > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AmberGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${plan.discountPercent}٪ تخفیف",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "دوره زمانی: ${plan.durationText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanGlow
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (plan.isPopular) EmeraldDark else SlateDark800)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = plan.badge,
                                color = if (plan.isPopular) EmeraldGlow else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pricing block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateDark950)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("قیمت به تومان:", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = plan.priceToman,
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("قیمت دلاری (تتر USDT):", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = plan.priceUsdt,
                                color = EmeraldNeon,
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
                                        .background(EmeraldDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGlow, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature.trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buy button
                    Button(
                        onClick = { selectedPlanToBuy = plan },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (plan.isPopular) EmeraldNeon else SlateDark800
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isCurrent) "تمدید این اشتراک" else "انتخاب و خرید ${plan.durationText}",
                            color = if (plan.isPopular) Color.Black else TextPrimary,
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
            onDismissRequest = { selectedPlanToBuy = null },
            confirmButton = {
                Button(
                    onClick = {
                        onBuyPlan(plan)
                        showSuccessDialog = "اشتراک ${plan.title} به مدت ${plan.durationText} با موفقیت فعال شد!"
                        selectedPlanToBuy = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تایید پرداخت و فعال‌سازی آنی", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedPlanToBuy = null },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            title = {
                Text(
                    text = "پیش‌فاکتور خرید ${plan.title}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "مبلغ قابل پرداخت: ${plan.priceToman} معادل ${plan.priceUsdt}",
                        color = AmberGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("روش پرداخت مورد نظر را انتخاب کنید:", color = TextSecondary, fontSize = 12.sp)

                    // Method 1: Tether USDT
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedPaymentMethod == "TETHER") EmeraldDark else SlateDark900)
                            .border(1.dp, if (selectedPaymentMethod == "TETHER") EmeraldNeon else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedPaymentMethod = "TETHER" }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CurrencyBitcoin, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("درگاه تتر کریپتو (USDT TRC20 / BEP20)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("واریز خودکار و بدون کارمزد بانکی", color = TextSecondary, fontSize = 10.sp)
                        }
                    }

                    // Method 2: Shetab Card to Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedPaymentMethod == "SHETAB") EmeraldDark else SlateDark900)
                            .border(1.dp, if (selectedPaymentMethod == "SHETAB") EmeraldNeon else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedPaymentMethod = "SHETAB" }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("کارت به کارت شتابی با رسید آنی", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("اتصال به سامانه شاپرک و پایا", color = TextSecondary, fontSize = 10.sp)
                        }
                    }

                    // Method 3: ZarinPal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedPaymentMethod == "ZARINPAL") EmeraldDark else SlateDark900)
                            .border(1.dp, if (selectedPaymentMethod == "ZARINPAL") EmeraldNeon else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedPaymentMethod = "ZARINPAL" }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("درگاه پرداخت مستقیم اینترنتی", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("اتصال امن رمز پویا", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Success Purchase Dialog
    if (showSuccessDialog != null) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = null },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("مشاهده سیگنال‌های باز شده", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldNeon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("پرداخت موفق و تایید شد", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = showSuccessDialog!!,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
