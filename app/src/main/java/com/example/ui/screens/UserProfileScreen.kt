package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import android.widget.Toast
import com.example.ui.components.BrandLogomotion
import com.example.ui.components.BrandLogoStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.ui.components.SignalCard
import com.example.ui.theme.*

@Composable
fun UserProfileScreen(
    currentUser: UserEntity?,
    userPlan: String,
    favoriteSignals: List<SignalEntity>,
    subscriptions: List<UserSubscriptionEntity>,
    tradeLogsCount: Int,
    wonCount: Int,
    lostCount: Int,
    onBack: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAdmin: () -> Unit,
    onToggleFavoriteSignal: (SignalEntity) -> Unit,
    onLogout: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var autoRenewNotifications by remember { mutableStateOf(true) }

    var logoStyle: BrandLogoStyle by remember { mutableStateOf<BrandLogoStyle>(BrandLogoStyle.PHOSPHOR_CANARY) }
    var speedFactor by remember { mutableStateOf(1.0f) }


    var strokeWidthFactor by remember { mutableStateOf(1.0f) }
    var coreScaleFactor by remember { mutableStateOf(1.0f) }
    var glowIntensity by remember { mutableStateOf(1.0f) }
    var showMotto by remember { mutableStateOf(true) }

    val totalTrades = wonCount + lostCount
    val winRatePercent = if (totalTrades > 0) ((wonCount.toDouble() / totalTrades) * 100).toInt() else 88

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftUiBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SoftUiSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = TextDarkPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "پروفایل تریدر و اشتراک‌ها",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextDarkPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PhosphorGreen.copy(alpha = 0.15f))
                    .border(1.dp, PhosphorGreenDark, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = userPlan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PhosphorGreenDark
                )
            }
        }

        // Navigation Tabs (Profile Overview, Subscriptions, Saved Signals)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SoftUiBg,
            contentColor = TextDarkPrimary,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RoyalCyberBlue,
                        height = 3.dp
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTab == 0) RoyalCyberBlue else TextDarkSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "اطلاعات حساب",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) TextDarkPrimary else TextDarkSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTab == 1) RoyalCyberBlue else TextDarkSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "مدیریت اشتراک",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) TextDarkPrimary else TextDarkSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTab == 2) RoyalCyberBlue else TextDarkSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "سیگنال‌های نشان‌شده (${favoriteSignals.size})",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) TextDarkPrimary else TextDarkSecondary
                        )
                    }
                }
            )

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Account Overview
                    item {
                        TraderHeaderCard(
                            currentUser = currentUser,
                            userPlan = userPlan,
                            onLoginClick = onLoginClick,
                            onLogout = onLogout
                        )
                    }

                    item {
                        TraderStatsRow(
                            winRatePercent = winRatePercent,
                            favoriteCount = favoriteSignals.size,
                            tradeLogsCount = tradeLogsCount,
                            wonCount = wonCount,
                            lostCount = lostCount
                        )
                    }

                    item {
                        AccountSettingsQuickActions(
                            currentUser = currentUser,
                            onOpenSettings = onOpenSettings,
                            onOpenSupport = onOpenSupport,
                            onOpenSubscriptions = onOpenSubscriptions,
                            onOpenAdmin = onOpenAdmin
                        )
                    }
                }

                1 -> {
                    // TAB 1: Subscriptions Management
                    item {
                        SubscriptionStatusCard(
                            userPlan = userPlan,
                            autoRenewNotifications = autoRenewNotifications,
                            onToggleAutoRenew = { autoRenewNotifications = it },
                            onUpgradeClick = onOpenSubscriptions
                        )
                    }

                    item {
                        Text(
                            text = "سوابق تراکنش‌ها و لایسنس‌های فعال",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (subscriptions.isEmpty()) {
                        item {
                            EmptySubscriptionCard(onOpenSubscriptions = onOpenSubscriptions)
                        }
                    } else {
                        items(subscriptions) { sub ->
                            SubscriptionItemRow(subscription = sub)
                        }
                    }
                }

                2 -> {
                    // TAB 2: Saved Signals History
                    if (favoriteSignals.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkRemove,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "هنوز هیچ سیگنالی نشان نشده است!",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "می‌توانید با زدن روی آیکون ستاره یا نشان روی هر کارت سیگنال، آن را برای بررسی‌های بعدی در این لیست قرار دهید.",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(favoriteSignals, key = { it.id }) { signal ->
                            SignalCard(
                                signal = signal,
                                onClick = { },
                                onToggleFavorite = { onToggleFavoriteSignal(signal) }
                            )
                        }
                    }
                }

                3 -> {
                    // TAB 3: Dynamic Branding Designer
                    item {
                        Text(
                            text = "پیش‌نمایش آنلاین برندینگ شما",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    item {
                        BrandLogomotion(
                            modifier = Modifier.fillMaxWidth(),
                            compact = false,
                            showMotto = showMotto,
                            style = logoStyle,
                            speedFactor = speedFactor,
                            strokeWidthFactor = strokeWidthFactor,
                            coreScaleFactor = coreScaleFactor,
                            glowIntensity = glowIntensity
                        )
                    }

                    item {
                        Text(
                            text = "انتخاب استایل و تم اصلی لوگو",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BrandLogoStyle.values().forEach { styleOption ->
                                val isSelected = logoStyle == styleOption
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { logoStyle = styleOption }
                                        .border(
                                            1.5.dp,
                                            if (isSelected) styleOption.primaryColor else SoftUiCardBorder,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) styleOption.primaryColor.copy(alpha = 0.1f) else SoftUiSurface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(styleOption.primaryColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = styleOption.coreIcon,
                                                contentDescription = null,
                                                tint = styleOption.primaryColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = styleOption.title.split(" ")[0], // Get name part
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SoftUiCardBorder, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "تنظیمات فوق تخصصی استایل‌ساز",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Speed Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("سرعت چرخش رادار ⚡", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    Text(text = speedFactor.toString() + "x", fontSize = 11.sp, color = logoStyle.primaryColor, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = speedFactor,
                                    onValueChange = { speedFactor = it },
                                    valueRange = 0.2f..3.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = logoStyle.primaryColor,
                                        activeTrackColor = logoStyle.primaryColor,
                                        inactiveTrackColor = SlateDark800
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stroke Width Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("ضخامت خطوط رادار 🎨", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    Text(text = strokeWidthFactor.toString() + "x", fontSize = 11.sp, color = logoStyle.primaryColor, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = strokeWidthFactor,
                                    onValueChange = { strokeWidthFactor = it },
                                    valueRange = 0.5f..2.5f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = logoStyle.primaryColor,
                                        activeTrackColor = logoStyle.primaryColor,
                                        inactiveTrackColor = SlateDark800
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Core Scale Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("اندازه نشان مرکزی 🎯", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    Text(text = coreScaleFactor.toString() + "x", fontSize = 11.sp, color = logoStyle.primaryColor, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = coreScaleFactor,
                                    onValueChange = { coreScaleFactor = it },
                                    valueRange = 0.5f..1.5f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = logoStyle.primaryColor,
                                        activeTrackColor = logoStyle.primaryColor,
                                        inactiveTrackColor = SlateDark800
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Glow Intensity Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("شدت هاله درخشان ✨", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    Text(text = glowIntensity.toString() + "x", fontSize = 11.sp, color = logoStyle.primaryColor, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = glowIntensity,
                                    onValueChange = { glowIntensity = it },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = logoStyle.primaryColor,
                                        activeTrackColor = logoStyle.primaryColor,
                                        inactiveTrackColor = SlateDark800
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = SoftUiCardBorder, thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Motto Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "نمایش شعار ایران باینری آپشن 📜",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "متن توصیفی و خط فکری برند در زیر لوگو",
                                            fontSize = 10.5.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Switch(
                                        checked = showMotto,
                                        onCheckedChange = { showMotto = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.Black,
                                            checkedTrackColor = logoStyle.primaryColor
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Developer Code Box to Copy the Customized Style
                    item {
                        val clipboardManager = LocalClipboardManager.current
                        val context = LocalContext.current
                        val configCode = """
                            // تنظیمات استایل اختصاصی ایران باینری آپشن
                            val customStyle = BrandLogoStyle.${logoStyle.name}
                            val speedFactor = ${speedFactor}f
                            val strokeWidthFactor = ${strokeWidthFactor}f
                            val coreScaleFactor = ${coreScaleFactor}f
                            val glowIntensity = ${glowIntensity}f
                            val showMotto = $showMotto
                        """.trimIndent()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "کد استایل تولیدشده ⚙️",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(logoStyle.primaryColor.copy(alpha = 0.15f))
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(configCode))
                                                Toast.makeText(context, "کد استایل لوگو با موفقیت کپی شد! 📋", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "کپی کد",
                                            tint = logoStyle.primaryColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "کپی استایل",
                                            fontSize = 10.sp,
                                            color = logoStyle.primaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = configCode,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TraderHeaderCard(
    currentUser: UserEntity?,
    userPlan: String,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(SoftCardBlueGradient))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(RoyalCyberBlue, PhosphorGreenDark)
                                )
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(SoftUiSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = RoyalCyberBlue,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = currentUser?.fullName ?: "تریدر باینری آپشن",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = TextDarkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentUser?.email ?: "کاربر مهمان (تأییدنشده)",
                            fontSize = 12.sp,
                            color = TextDarkSecondary
                        )
                    }
                }

                if (currentUser != null) {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "خروج",
                            tint = CrimsonRed
                        )
                    }
                } else {
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalCyberBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ورود / ثبت‌نام", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SoftUiCardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سطح دسترسی: $userPlan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }

                Text(
                    text = "ID: #${(currentUser?.id ?: 1084) * 31}",
                    fontSize = 11.sp,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun TraderStatsRow(
    winRatePercent: Int,
    favoriteCount: Int,
    tradeLogsCount: Int,
    wonCount: Int,
    lostCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, PhosphorGreenDark.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("وین‌ریت کلی", fontSize = 11.sp, color = TextDarkSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$winRatePercent٪",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = PhosphorGreenDark
                )
                Text(
                    text = "$wonCount برد / $lostCount باخت",
                    fontSize = 10.sp,
                    color = TextDarkSecondary
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, RoyalCyberBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ژورنال معامله", fontSize = 11.sp, color = TextDarkSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$tradeLogsCount ثبت",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = RoyalCyberBlue
                )
                Text(
                    text = "ترید شخصی",
                    fontSize = 10.sp,
                    color = TextDarkSecondary
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, AmberGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("سیگنال ذخیره", fontSize = 11.sp, color = TextDarkSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$favoriteCount عدد",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AmberGold
                )
                Text(
                    text = "لیست نشان‌شده",
                    fontSize = 10.sp,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun AccountSettingsQuickActions(
    currentUser: UserEntity?,
    onOpenSettings: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "تنظیمات سریع و پشتیبانی",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextDarkPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenSettings() }
                    .background(SoftUiBg)
                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = RoyalCyberBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("تنظیمات اعلان‌ها و هشدارهای صوتی 🔔", fontSize = 12.5.sp, color = TextDarkPrimary, fontWeight = FontWeight.Medium)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDarkSecondary, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenSupport() }
                    .background(SoftUiBg)
                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = PhosphorGreenDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("چت آنلاین پشتیبانی ۲۴/۷ 🎧", fontSize = 12.5.sp, color = TextDarkPrimary, fontWeight = FontWeight.Medium)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDarkSecondary, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenSubscriptions() }
                    .background(SoftUiBg)
                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ارتقا به اشتراک الماس یا VIP 💎", fontSize = 12.5.sp, color = TextDarkPrimary, fontWeight = FontWeight.Medium)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDarkSecondary, modifier = Modifier.size(16.dp))
            }

            if (currentUser?.role == "ADMIN") {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenAdmin() }
                        .background(SoftUiBg)
                        .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("پنل مدیریت ادمین 🔐", fontSize = 12.5.sp, color = TextDarkPrimary, fontWeight = FontWeight.Bold)
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDarkSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SubscriptionStatusCard(
    userPlan: String,
    autoRenewNotifications: Boolean,
    onToggleAutoRenew: (Boolean) -> Unit,
    onUpgradeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(SoftCardPeachGradient))
            .border(1.dp, GoldGlow.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "پلن فعال: $userPlan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "اعتبار لایسنس: ۳۰ روز باقی‌مانده",
                            fontSize = 11.5.sp,
                            color = EmeraldGlow
                        )
                    }
                }

                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("تمدید / تغییر", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SoftUiCardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "هشدار پیامکی و نوتیفیکیشن قبل از پایان لایسنس",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "ارسال هشدار ۲۴ ساعت قبل از منقضی شدن لایسنس",
                        fontSize = 10.5.sp,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = autoRenewNotifications,
                    onCheckedChange = onToggleAutoRenew,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = EmeraldNeon
                    )
                )
            }
        }
    }
}

@Composable
private fun EmptySubscriptionCard(
    onOpenSubscriptions: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(SoftCardMintGradient))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(14.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = TextDarkSecondary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("هنوز هیچ لایسنسی خریده نشده است.", fontSize = 12.sp, color = TextDarkSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onOpenSubscriptions,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("مشاهده ۵ پلن VIP", color = RoyalCyberBlue, fontSize = 11.5.sp)
            }
        }
    }
}

@Composable
private fun SubscriptionItemRow(
    subscription: UserSubscriptionEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PhosphorGreenDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = subscription.planTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                    val formattedDate = try {
                        java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.ENGLISH)
                            .format(java.util.Date(subscription.startDate))
                    } catch (e: Exception) {
                        subscription.startDate.toString()
                    }
                    Text(text = "تاریخ شروع: $formattedDate", fontSize = 11.sp, color = TextDarkSecondary)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PhosphorGreen.copy(alpha = 0.15f))
                    .border(1.dp, PhosphorGreenDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (subscription.status == "ACTIVE") "فعال 🟢" else "منقضی 🔴",
                    fontSize = 10.5.sp,
                    color = PhosphorGreenDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
