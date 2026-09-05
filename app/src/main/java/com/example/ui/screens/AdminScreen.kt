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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NewsEntity
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserSubscriptionEntity
import com.example.data.repository.OfflineCacheSyncStatus
import com.example.fcm.FcmNotificationHelper
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
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminScreen(
    currentUser: UserEntity?,
    signals: List<SignalEntity>,
    plans: List<PlanEntity>,
    newsList: List<NewsEntity>,
    staffList: List<UserEntity>,
    subscriptions: List<UserSubscriptionEntity> = emptyList(),
    offlineCacheStatus: OfflineCacheSyncStatus? = null,
    currentLuxuryTheme: com.example.ui.theme.LuxuryThemeMode = com.example.ui.theme.LuxuryThemeMode.PHOSPHOR_CANARY,
    onSelectLuxuryTheme: (com.example.ui.theme.LuxuryThemeMode) -> Unit = {},
    onLogin: (email: String, pass: String) -> Unit,
    onLogout: () -> Unit,
    onUpdatePassword: (newPass: String) -> Unit,
    onAddNewStaff: (email: String, pass: String, name: String, role: String) -> Unit,
    onAddSignal: (SignalEntity) -> Unit,
    onDeleteSignal: (Long) -> Unit,
    onUpdateSignalStatus: (SignalEntity, String) -> Unit,
    onUpdatePlan: (PlanEntity) -> Unit,
    onAddNews: (NewsEntity) -> Unit,
    onDeleteNews: (Long) -> Unit,
    onSyncCloud: () -> Unit = {},
    onRunAiAgent: (String) -> String
) {

    var adminEmailInput by remember { mutableStateOf("admin@iranbinary.ir") }
    var adminPassInput by remember { mutableStateOf("IranBinaryAdmin2026!") }
    var showPassword by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("AI_PROMPT_BOX") } // AI_PROMPT_BOX, SIGNAL_DELIVERY, SUBSCRIPTION_TIERS, NEWS_CMS, SECURITY

    // Shared State for AI pre-populating Form Fields
    var prefilledAsset by remember { mutableStateOf("EUR/USD (OTC)") }
    var prefilledCategory by remember { mutableStateOf("OTC") }
    var prefilledDirection by remember { mutableStateOf("CALL") }
    var prefilledStrike by remember { mutableStateOf("1.08510") }
    var prefilledExpiry by remember { mutableStateOf("1m") }
    var prefilledPayout by remember { mutableStateOf("۹۶٪") }
    var prefilledRegime by remember { mutableStateOf("شکست صعودی تثبیت‌شده") }
    var prefilledConfidence by remember { mutableStateOf("90") }
    var prefilledBrokers by remember { mutableStateOf("Quotex, Pocket Option") }
    var prefilledDeliveryTier by remember { mutableStateOf("کاربران اشتراکی (۱ هفته‌ای به بالا)") }
    var prefilledRationale by remember { mutableStateOf("افزایش مومنتوم خرید پس از تست موفقیت‌آمیز حمایت M5 و تایید AI1") }

    val isAdminLoggedIn = currentUser != null && (currentUser.role == "ADMIN" || currentUser.role == "STAFF")

    if (!isAdminLoggedIn) {
        // Protected Admin Login Gate
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDark950),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark)
                                .border(1.5.dp, EmeraldNeon, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = EmeraldGlow,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "سامانه ورود مدیران و کارمندان",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = TextPrimary
                        )

                        Text(
                            text = "منطقه محافظت‌شده مدیریت ارشد | Iran Binary Portal",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanGlow
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Banner with pre-configured default credentials for testing
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SlateDark900)
                                .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشخصات ورود پیش‌فرض مدیر سامانه:", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("نام کاربری / ایمیل: admin@iranbinary.ir (یا admin)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("کلمه عبور اختصاصی: IranBinaryAdmin2026!", color = EmeraldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("💡 پس از ورود، می‌توانید به فرم‌های تحویل سیگنال، مدیریت پلن‌های اشتراک و پرامپت‌باکس هوش مصنوعی دسترسی داشته باشید.", color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = adminEmailInput,
                            onValueChange = { adminEmailInput = it },
                            label = { Text("ایمیل یا نام کاربری مدیریت") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldNeon,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = EmeraldGlow,
                                unfocusedLabelColor = TextSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = adminPassInput,
                            onValueChange = { adminPassInput = it },
                            label = { Text("کلمه عبور") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextSecondary
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldNeon,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = EmeraldGlow,
                                unfocusedLabelColor = TextSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLogin(adminEmailInput, adminPassInput) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ورود امن به پنل مدیریت",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Authenticated Protected Admin Dashboard
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDark950),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Protected Security Header & Status Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDark)
                                        .border(1.dp, EmeraldNeon, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGlow, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentUser?.fullName ?: "مدیریت ارشد سامانه",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EmeraldDark)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = currentUser?.role ?: "ADMIN",
                                                color = EmeraldGlow,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                    Text(
                                        text = "نشست امن AES-256 فعال | دسترسی به فرم‌ها و دستیار AI",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CyanGlow,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SlateDark800)
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = "خروج", tint = CrimsonGlow, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Real-time Metrics Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill(title = "سیگنال‌های فعال", value = "${signals.size}", icon = Icons.Default.SignalCellularAlt, modifier = Modifier.weight(1f))
                            MetricPill(title = "پلن‌های اشتراک", value = "${plans.size} سطح", icon = Icons.Default.Layers, modifier = Modifier.weight(1f))
                            MetricPill(title = "شبکه تحویل", value = "زیر ۲۵۰ms", icon = Icons.Default.Speed, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Navigation Tabs
            item {
                val tabs = listOf(
                    "AI_PROMPT_BOX" to "دستیار AI و پرامپت‌باکس",
                    "THEME_PALETTES" to "دیزاین لوکس و پالت رنگ‌ها 🎨",
                    "SIGNAL_DELIVERY" to "فرم تحویل سیگنال",
                    "SUBSCRIPTION_TIERS" to "فرم پلن‌های اشتراک",
                    "NEWS_CMS" to "انتشار اخبار فاندامنتال",
                    "OFFLINE_CACHE_CLOUD" to "کش آفلاین Room و ابری",
                    "SECURITY" to "امنیت و کارمندان"
                )


                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tabs) { (code, label) ->
                        val isSelected = activeTab == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldDark else SlateDark900)
                                .border(1.dp, if (isSelected) EmeraldNeon else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { activeTab = code }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
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
            }

            // Tab Content
            when (activeTab) {
                "AI_PROMPT_BOX" -> {
                    item {
                        AiPromptBoxSection(
                            onRunAiAgent = onRunAiAgent,
                            onLoadSignalToForm = { asset, category, direction, strike, expiry, payout, regime, confidence, brokers, rationale ->
                                prefilledAsset = asset
                                prefilledCategory = category
                                prefilledDirection = direction
                                prefilledStrike = strike
                                prefilledExpiry = expiry
                                prefilledPayout = payout
                                prefilledRegime = regime
                                prefilledConfidence = confidence
                                prefilledBrokers = brokers
                                prefilledRationale = rationale
                                activeTab = "SIGNAL_DELIVERY"
                            },
                            onLoadTierToForm = {
                                activeTab = "SUBSCRIPTION_TIERS"
                            }
                        )
                    }
                }
                "THEME_PALETTES" -> {
                    item {
                        LuxuryThemeConfigSection(
                            currentTheme = currentLuxuryTheme,
                            onSelectTheme = onSelectLuxuryTheme
                        )
                    }
                }
                "SIGNAL_DELIVERY" -> {

                    item {
                        SignalDeliveryFormSection(
                            signals = signals,
                            initialAsset = prefilledAsset,
                            initialCategory = prefilledCategory,
                            initialDirection = prefilledDirection,
                            initialStrike = prefilledStrike,
                            initialExpiry = prefilledExpiry,
                            initialPayout = prefilledPayout,
                            initialRegime = prefilledRegime,
                            initialConfidence = prefilledConfidence,
                            initialBrokers = prefilledBrokers,
                            initialDeliveryTier = prefilledDeliveryTier,
                            initialRationale = prefilledRationale,
                            onAddSignal = onAddSignal,
                            onDeleteSignal = onDeleteSignal,
                            onUpdateSignalStatus = onUpdateSignalStatus
                        )
                    }
                }
                "SUBSCRIPTION_TIERS" -> {
                    item {
                        SubscriptionTiersFormSection(
                            plans = plans,
                            onUpdatePlan = onUpdatePlan
                        )
                    }
                }
                "NEWS_CMS" -> {
                    item {
                        NewsManagementSection(
                            newsList = newsList,
                            onAddNews = onAddNews,
                            onDeleteNews = onDeleteNews
                        )
                    }
                }
                "SECURITY" -> {
                    item {
                        SecurityAndStaffSection(
                            staffList = staffList,
                            onUpdatePassword = onUpdatePassword,
                            onAddNewStaff = onAddNewStaff
                        )
                    }
                }
                "OFFLINE_CACHE_CLOUD" -> {
                    item {
                        OfflineCacheAndCloudSection(
                            offlineCacheStatus = offlineCacheStatus,
                            subscriptions = subscriptions,
                            signals = signals,
                            newsList = newsList,
                            onSyncCloud = onSyncCloud
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

// Sub-Component: Metric Pill
@Composable
fun MetricPill(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateDark900)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(title, color = TextSecondary, fontSize = 9.5.sp)
                Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

// Sub-Section: Luxury Design System & Color Palette Admin Management
@Composable
fun LuxuryThemeConfigSection(
    currentTheme: com.example.ui.theme.LuxuryThemeMode,
    onSelectTheme: (com.example.ui.theme.LuxuryThemeMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.CanaryYellow.copy(alpha = 0.2f))
                        .border(1.dp, com.example.ui.theme.CanaryYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = com.example.ui.theme.CanaryYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "مدیریت استایل و پالت‌های رنگی دیزاین سیستم لوکس",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "تغییر لحظه‌ای تم رنگی کل اپلیکیشن (زرد، سبز فسفری، آبی، قرمز و نارنجی)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "پالت‌های رنگی فوق‌العاده طراحی‌شده (با یک لمس فعال می‌شود):",
                color = com.example.ui.theme.CanaryYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            com.example.ui.theme.LuxuryThemeMode.values().forEach { mode ->
                val isSelected = currentTheme == mode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) SlateDark800 else SlateDark900)
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) mode.accentPrimary else CardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectTheme(mode) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Color swatches preview dots
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(mode.accentPrimary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(mode.accentSecondary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(mode.bgPrimary)
                                        .border(0.5.dp, Color.White, CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) mode.accentPrimary else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isSelected) "● تم در حال استفاده در کل اپ" else "جهت اعمال روی اپ لمس کنید",
                                    color = if (isSelected) com.example.ui.theme.PhosphorGreen else TextMuted,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(mode.accentPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, mode.accentPrimary, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "فعال است",
                                    color = mode.accentPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Design metrics & typography note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateDark900)
                    .border(0.8.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        "💎 مشخصات متریال دیزاین ۳ و تایپوگرافی آپ‌اسکیل:",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• فونت‌های سنس‌سریف و هدلاین‌های لبه‌تیز با لتر اسپیسینگ استاندارد طلاکاری‌شده\n" +
                        "• لوگوموشن ۳ بعدی با امواج پالسی هولوگرافیک و نشان رسمی TRADING SIGNALS\n" +
                        "• نشان‌های کریستالی ۳D انحصاری دکمه‌های CALL (سبز فسفری/زرد) و PUT (قرمز/نارنجی)\n" +
                        "• خط افشای ریسک قانونی و بدون ادعای دروغین، منطبق بر تمام قوانین",
                        color = TextSecondary,
                        fontSize = 10.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// Sub-Section: AI Assistant Prompt Box for Managing Signal Delivery & Subscription Tiers

@Composable
fun AiPromptBoxSection(
    onRunAiAgent: (String) -> String,
    onLoadSignalToForm: (
        asset: String, category: String, direction: String, strike: String,
        expiry: String, payout: String, regime: String, confidence: String,
        brokers: String, rationale: String
    ) -> Unit,
    onLoadTierToForm: () -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    var aiResponse by remember {
        mutableStateOf(
            "سلام ادمین گرامی! 👋 دستیار هوش مصنوعی پلتفرم ایران باینری آماده نظارت بر تحویل سیگنال‌ها (Signal Delivery Queue) و بهینه‌سازی اقتصاد پلن‌های اشتراک (Subscription Tiers) است.\n\n" +
            "شما می‌توانید یکی از پرامپت‌های آماده زیر را انتخاب کرده یا دستور اختصاصی خود را وارد نمایید. نتایج پیشنهادی مستقیماً با یک لمس قابل انتقال به فرم‌های انتشار هستند."
        )
    }

    val quickPrompts = listOf(
        "تولید و بارگذاری سیگنال باینری EUR/USD OTC",
        "بهینه‌سازی نرخ و تخفیف پلن‌های اشتراک (Tier Economics)",
        "بررسی وضعیت و تاخیر شبکه تحویل سیگنال‌ها",
        "ممیزی ریسک و اعمال وتوی اضطراری تحویل"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldDark)
                        .border(1.dp, AmberGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("پرامپت‌باکس هوش مصنوعی مدیریت تحویل سیگنال و پلن‌ها", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Text("اتوماسیون هوشمند تولید سیگنال، صف تحویل و ممیزی اقتصادی اشتراک‌ها", color = TextSecondary, fontSize = 10.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Action Pills
            Text("پرامپت‌های سریع و عملیاتی:", color = CyanGlow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPrompts) { p ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateDark800)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                promptInput = p
                                aiResponse = onRunAiAgent(p)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(p, color = CyanGlow, fontSize = 10.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Response Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SlateDark900)
                    .border(1.dp, EmeraldDark, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldGlow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("پاسخ تحلیلگر هوش مصنوعی ایران باینری:", color = EmeraldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aiResponse,
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct Form Bridge Buttons (Transfer AI Output to Form Fields)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onLoadSignalToForm(
                            "EUR/USD (OTC)",
                            "OTC",
                            "CALL",
                            "1.08510",
                            "1m",
                            "۹۶٪",
                            "شکست صعودی تثبیت‌شده",
                            "90",
                            "Quotex, Pocket Option",
                            "افزایش مومنتوم خرید پس از تست موفقیت‌آمیز حمایت M5 و تایید AI1"
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("📥 بارگذاری در فرم سیگنال", color = EmeraldNeon, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onLoadTierToForm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("📥 انتقال به فرم پلن‌ها", color = AmberGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prompt Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("دستور خود را بنویسید (مثلاً: سیگنال جدید برای طلا بساز)...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            aiResponse = onRunAiAgent(promptInput)
                            promptInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(52.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "ارسال", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// Sub-Section: Dedicated Signal Delivery & Content Management Form
@Composable
fun SignalDeliveryFormSection(
    signals: List<SignalEntity>,
    initialAsset: String,
    initialCategory: String,
    initialDirection: String,
    initialStrike: String,
    initialExpiry: String,
    initialPayout: String,
    initialRegime: String,
    initialConfidence: String,
    initialBrokers: String,
    initialDeliveryTier: String,
    initialRationale: String,
    onAddSignal: (SignalEntity) -> Unit,
    onDeleteSignal: (Long) -> Unit,
    onUpdateSignalStatus: (SignalEntity, String) -> Unit
) {
    var assetInput by remember(initialAsset) { mutableStateOf(initialAsset) }
    var categoryInput by remember(initialCategory) { mutableStateOf(initialCategory) }
    var directionInput by remember(initialDirection) { mutableStateOf(initialDirection) }
    var strikeInput by remember(initialStrike) { mutableStateOf(initialStrike) }
    var expiryInput by remember(initialExpiry) { mutableStateOf(initialExpiry) }
    var payoutInput by remember(initialPayout) { mutableStateOf(initialPayout) }
    var regimeInput by remember(initialRegime) { mutableStateOf(initialRegime) }
    var confidenceInput by remember(initialConfidence) { mutableStateOf(initialConfidence) }
    var brokersInput by remember(initialBrokers) { mutableStateOf(initialBrokers) }
    var deliveryTierInput by remember(initialDeliveryTier) { mutableStateOf(initialDeliveryTier) }
    var rationaleInput by remember(initialRationale) { mutableStateOf(initialRationale) }

    var formSuccessMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Broadcast Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("فرم انتشار و تحویل سیگنال معاملاتی (Signal Delivery Form)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
                Text("تکمیل فیلدهای فرم زیر جهت ارسال آنی سیگنال به کاربران و صف وب‌سوکت بروکرها", color = TextSecondary, fontSize = 10.5.sp)

                Spacer(modifier = Modifier.height(2.dp))

                // Asset Quick Selection Chips
                Text("انتخاب سریع جفت‌ارز / دارایی:", color = CyanGlow, fontSize = 10.5.sp)
                val quickAssets = listOf("EUR/USD (OTC)", "GBP/USD (OTC)", "USD/JPY (OTC)", "BTC/USDT", "GOLD (OTC)")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickAssets) { a ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (assetInput == a) EmeraldDark else SlateDark900)
                                .border(1.dp, if (assetInput == a) EmeraldNeon else CardBorder, RoundedCornerShape(10.dp))
                                .clickable { assetInput = a }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(a, color = if (assetInput == a) EmeraldGlow else TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                // Row: Asset & Category
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assetInput,
                        onValueChange = { assetInput = it },
                        label = { Text("نام دارایی") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("دسته (OTC/FOREX)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Direction Selector (CALL, PUT, NO_TRADE)
                Text("جهت سیگنال معاملاتی:", color = TextSecondary, fontSize = 11.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CALL" to "خرید (CALL ⬆)", "PUT" to "فروش (PUT ⬇)", "NO_TRADE" to "فیلتر وتو (NO TRADE)").forEach { (dir, label) ->
                        val isSel = directionInput == dir
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSel) {
                                        when (dir) {
                                            "CALL" -> EmeraldDark
                                            "PUT" -> CrimsonRed.copy(alpha = 0.3f)
                                            else -> AmberGold.copy(alpha = 0.2f)
                                        }
                                    } else SlateDark900
                                )
                                .border(
                                    1.dp,
                                    if (isSel) {
                                        when (dir) {
                                            "CALL" -> EmeraldNeon
                                            "PUT" -> CrimsonGlow
                                            else -> AmberGold
                                        }
                                    } else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { directionInput = dir }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) TextPrimary else TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }

                // Row: Strike Price, Expiry, Payout
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = strikeInput,
                        onValueChange = { strikeInput = it },
                        label = { Text("قیمت استرایک") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = expiryInput,
                        onValueChange = { expiryInput = it },
                        label = { Text("انقضا (1m/5m)") },
                        modifier = Modifier.weight(0.9f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = payoutInput,
                        onValueChange = { payoutInput = it },
                        label = { Text("بازدهی (٪)") },
                        modifier = Modifier.weight(0.9f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Row: Confidence & Target Delivery Tier
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = confidenceInput,
                        onValueChange = { confidenceInput = it },
                        label = { Text("اعتماد هوش مصنوعی (٪)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = deliveryTierInput,
                        onValueChange = { deliveryTierInput = it },
                        label = { Text("تارگت پلن اشتراک") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Market Regime & Recommended Brokers
                OutlinedTextField(
                    value = regimeInput,
                    onValueChange = { regimeInput = it },
                    label = { Text("رژیم بازار (Market Regime)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = brokersInput,
                    onValueChange = { brokersInput = it },
                    label = { Text("بروکرهای بهینه برای اجرای معامله") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = rationaleInput,
                    onValueChange = { rationaleInput = it },
                    label = { Text("استدلال تحلیلی چندلایه AI") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                // FCM High-Accuracy Push Notification Notice Banner
                val parsedConfidence = confidenceInput.toIntOrNull() ?: 85
                val isHighAccuracy = parsedConfidence >= 80 && directionInput != "NO_TRADE"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateDark900)
                        .border(1.dp, if (isHighAccuracy) EmeraldNeon.copy(alpha = 0.5f) else CardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (isHighAccuracy) EmeraldNeon else CyanGlow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ارسال پوش نوتیفیکیشن FCM:",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHighAccuracy) "فعال (دقت بالا 🚀)" else "حالت استاندارد",
                                    color = if (isHighAccuracy) EmeraldNeon else AmberGold,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "همگام‌سازی ابری با Firebase Cloud Messaging برای تمام کاربران مشترک تاپیک high_accuracy_signals فعال است.",
                                color = TextMuted,
                                fontSize = 9.5.sp
                            )
                        }
                    }
                }

                // Test Push Notification Button
                OutlinedButton(
                    onClick = {
                        val testSignal = SignalEntity(
                            asset = assetInput,
                            category = categoryInput,
                            direction = directionInput,
                            strikePrice = strikeInput,
                            currentPrice = strikeInput,
                            expiry = expiryInput,
                            payoutRate = payoutInput,
                            marketRegime = regimeInput,
                            confidenceScore = parsedConfidence,
                            riskScore = "کم ریسک",
                            vetoStatus = "تایید شده",
                            rationale = "تست اعلان فوری FCM: سیگنال $assetInput با وین‌ریت $parsedConfidence٪ صادر شد.",
                            recommendedBrokers = brokersInput,
                            status = "ACTIVE"
                        )
                        FcmNotificationHelper.showSignalNotification(context, testSignal)
                        formSuccessMessage = "🔔 پوش نوتیفیکیشن تست FCM با موفقیت ارسال شد و در نوار اعلان‌های اندروید قرار گرفت."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تست فوری اعلان پوش نوتیفیکیشن FCM روی این دستگاه", color = CyanGlow, fontSize = 11.sp)
                }

                // Dispatch Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onAddSignal(
                                SignalEntity(
                                    asset = assetInput,
                                    category = categoryInput,
                                    direction = directionInput,
                                    strikePrice = strikeInput,
                                    currentPrice = strikeInput,
                                    expiry = expiryInput,
                                    payoutRate = payoutInput,
                                    marketRegime = regimeInput,
                                    confidenceScore = confidenceInput.toIntOrNull() ?: 85,
                                    riskScore = if (directionInput == "NO_TRADE") "وتو شده (Vetoed)" else "کنترل‌شده (Controlled)",
                                    vetoStatus = if (directionInput == "NO_TRADE") "غیرمجاز برای ترید" else "تایید شده",
                                    rationale = "$rationaleInput | تحویل به: $deliveryTierInput",
                                    recommendedBrokers = brokersInput,
                                    status = if (directionInput == "NO_TRADE") "VETOED" else "ACTIVE"
                                )
                            )
                            formSuccessMessage = "✅ سیگنال $assetInput با موفقیت در صف تحویل قرار گرفت و به کاربران ارسال شد."
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ارسال و تحویل فوری به کاربران", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            assetInput = "EUR/USD (OTC)"
                            directionInput = "CALL"
                            strikeInput = "1.08500"
                            expiryInput = "1m"
                            payoutInput = "۹۲٪"
                            formSuccessMessage = null
                        },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("پاکسازی", fontSize = 11.sp)
                    }
                }

                if (formSuccessMessage != null) {
                    Text(text = formSuccessMessage!!, color = EmeraldGlow, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Delivered Signals List
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("فهرست سیگنال‌های تحویل داده‌شده (${signals.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            Text("مدیریت وضعیت برد/باخت", color = TextSecondary, fontSize = 10.5.sp)
        }

        signals.forEach { signal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(signal.asset, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SlateDark900)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(signal.category, color = CyanGlow, fontSize = 9.sp)
                            }
                        }

                        Text(
                            text = signal.direction,
                            color = when (signal.direction) {
                                "CALL" -> EmeraldGlow
                                "PUT" -> CrimsonGlow
                                else -> AmberGold
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("استرایک: ${signal.strikePrice} | انقضا: ${signal.expiry} | بازدهی: ${signal.payoutRate} | اعتماد: ${signal.confidenceScore}٪", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onUpdateSignalStatus(signal, "WON") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("علامت برد (WON)", color = EmeraldGlow, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onUpdateSignalStatus(signal, "LOST") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("علامت باخت (LOST)", color = CrimsonGlow, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { onDeleteSignal(signal.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// Sub-Section: User Subscription Tiers Form & Management
@Composable
fun SubscriptionTiersFormSection(
    plans: List<PlanEntity>,
    onUpdatePlan: (PlanEntity) -> Unit
) {
    var selectedPlanId by remember { mutableStateOf(plans.firstOrNull()?.id ?: 1L) }
    val currentSelectedPlan = plans.find { it.id == selectedPlanId } ?: plans.firstOrNull()

    var titleEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.title ?: "") }
    var durationTextEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.durationText ?: "") }
    var durationDaysEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.durationDays?.toString() ?: "30") }
    var priceTomanEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.priceToman ?: "") }
    var priceUsdtEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.priceUsdt ?: "") }
    var discountEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.discountPercent?.toString() ?: "0") }
    var badgeEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.badge ?: "") }
    var featuresEdit by remember(currentSelectedPlan) { mutableStateOf(currentSelectedPlan?.features ?: "") }

    var saveToast by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Tiers Selector Row
        Text("انتخاب پلن اشتراک جهت ویرایش فیلدها:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(plans) { p ->
                val isSel = p.id == selectedPlanId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) EmeraldDark else SlateDark900)
                        .border(1.dp, if (isSel) EmeraldNeon else CardBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedPlanId = p.id }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(p.title, color = if (isSel) EmeraldGlow else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("${p.priceToman}", color = if (isSel) AmberGold else TextSecondary, fontSize = 9.5.sp)
                    }
                }
            }
        }

        // Tiers Form Card
        if (currentSelectedPlan != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("فرم مدیریت و قیمت‌گذاری ${currentSelectedPlan.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                    Text("ویرایش قیمت ریالی، دلاری، تخفیف، مدت زمان اعتبار و امتیازات تحویل", color = TextSecondary, fontSize = 10.5.sp)

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = titleEdit,
                            onValueChange = { titleEdit = it },
                            label = { Text("عنوان پلن") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = durationTextEdit,
                            onValueChange = { durationTextEdit = it },
                            label = { Text("متن مدت (مثلاً ۱ ماهه)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceTomanEdit,
                            onValueChange = { priceTomanEdit = it },
                            label = { Text("قیمت به تومان") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = priceUsdtEdit,
                            onValueChange = { priceUsdtEdit = it },
                            label = { Text("قیمت دلاری USDT") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = discountEdit,
                            onValueChange = { discountEdit = it },
                            label = { Text("درصد تخفیف (٪)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = badgeEdit,
                            onValueChange = { badgeEdit = it },
                            label = { Text("برچسب ویژه") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = featuresEdit,
                        onValueChange = { featuresEdit = it },
                        label = { Text("ویژگی‌ها و امتیازات تحویل (با | جدا کنید)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            onUpdatePlan(
                                currentSelectedPlan.copy(
                                    title = titleEdit,
                                    durationText = durationTextEdit,
                                    durationDays = durationDaysEdit.toIntOrNull() ?: currentSelectedPlan.durationDays,
                                    priceToman = priceTomanEdit,
                                    priceUsdt = priceUsdtEdit,
                                    discountPercent = discountEdit.toIntOrNull() ?: currentSelectedPlan.discountPercent,
                                    badge = badgeEdit,
                                    features = featuresEdit
                                )
                            )
                            saveToast = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ذخیره و اعمال تغییرات در دیتابیس پلن‌ها", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    if (saveToast) {
                        Text("✅ تغییرات پلن ${currentSelectedPlan.title} با موفقیت در سامانه ذخیره شد.", color = EmeraldGlow, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// Sub-Section: News Management
@Composable
fun NewsManagementSection(
    newsList: List<NewsEntity>,
    onAddNews: (NewsEntity) -> Unit,
    onDeleteNews: (Long) -> Unit
) {
    var showAddNewsDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("OTC") }
    var summaryInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var sourceInput by remember { mutableStateOf("پایگاه ممیزی ایران باینری") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("فهرست اخبار یک‌ساعته (${newsList.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)

            Button(
                onClick = { showAddNewsDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("انتشار خبر جدید", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
        }

        newsList.forEach { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(news.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, maxLines = 1)
                        Text("دسته: ${news.category} | ${news.timeAgo}", color = TextSecondary, fontSize = 10.5.sp)
                    }

                    IconButton(onClick = { onDeleteNews(news.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed)
                    }
                }
            }
        }
    }

    if (showAddNewsDialog) {
        AlertDialog(
            onDismissRequest = { showAddNewsDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            onAddNews(
                                NewsEntity(
                                    title = titleInput,
                                    category = categoryInput,
                                    summary = summaryInput,
                                    fullContent = contentInput,
                                    source = sourceInput,
                                    impact = "HIGH",
                                    sentiment = "صعودی (Bullish)",
                                    timeAgo = "هم‌اکنون"
                                )
                            )
                            showAddNewsDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("انتشار در فید زنده", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddNewsDialog = false }, shape = RoundedCornerShape(10.dp)) {
                    Text("انصراف")
                }
            },
            title = { Text("انتشار خبر فاندامنتال جدید", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("عنوان خبر") }, singleLine = true)
                    OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("دسته‌بندی (OTC/FOREX/CRYPTO)") }, singleLine = true)
                    OutlinedTextField(value = summaryInput, onValueChange = { summaryInput = it }, label = { Text("خلاصه کوتاه") }, maxLines = 2)
                    OutlinedTextField(value = contentInput, onValueChange = { contentInput = it }, label = { Text("متن کامل خبر و تحلیل باینری") }, maxLines = 4)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

// Sub-Section: Security, Password Change and New Staff Account Creation
@Composable
fun SecurityAndStaffSection(
    staffList: List<UserEntity>,
    onUpdatePassword: (newPass: String) -> Unit,
    onAddNewStaff: (email: String, pass: String, name: String, role: String) -> Unit
) {
    var newPasswordInput by remember { mutableStateOf("") }
    var passwordChangeSuccess by remember { mutableStateOf(false) }

    var newStaffEmail by remember { mutableStateOf("") }
    var newStaffPass by remember { mutableStateOf("") }
    var newStaffName by remember { mutableStateOf("") }
    var newStaffRole by remember { mutableStateOf("STAFF") }
    var staffAddSuccess by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Change Current Admin Password
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = EmeraldNeon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تغییر کلمه عبور مدیریت", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPasswordInput,
                    onValueChange = { newPasswordInput = it },
                    label = { Text("کلمه عبور جدید مدیریت") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (newPasswordInput.isNotBlank()) {
                            onUpdatePassword(newPasswordInput)
                            passwordChangeSuccess = true
                            newPasswordInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ثبت و به‌روزرسانی رمز عبور", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (passwordChangeSuccess) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✅ رمز عبور با موفقیت تغییر یافت و در دیتابیس ذخیره شد.", color = EmeraldGlow, fontSize = 11.sp)
                }
            }
        }

        // Add New Admin or Staff
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = CyanNeon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("افزودن کارمند یا مدیر جدید به سیستم", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newStaffName,
                    onValueChange = { newStaffName = it },
                    label = { Text("نام و نام خانوادگی") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newStaffEmail,
                    onValueChange = { newStaffEmail = it },
                    label = { Text("ایمیل کارمند") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newStaffPass,
                    onValueChange = { newStaffPass = it },
                    label = { Text("کلمه عبور اولیه") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (newStaffEmail.isNotBlank() && newStaffPass.isNotBlank()) {
                            onAddNewStaff(newStaffEmail, newStaffPass, newStaffName, newStaffRole)
                            staffAddSuccess = true
                            newStaffEmail = ""
                            newStaffPass = ""
                            newStaffName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ایجاد حساب کاربری اداری", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (staffAddSuccess) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✅ حساب پرسنلی جدید با موفقیت ایجاد گردید.", color = EmeraldGlow, fontSize = 11.sp)
                }
            }
        }
    }
}

// Sub-Component: Offline Cache & Firestore Cloud Section
@Composable
fun OfflineCacheAndCloudSection(
    offlineCacheStatus: OfflineCacheSyncStatus?,
    subscriptions: List<UserSubscriptionEntity>,
    signals: List<SignalEntity>,
    newsList: List<NewsEntity>,
    onSyncCloud: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("SUBSCRIPTIONS") } // SUBSCRIPTIONS, SIGNALS, NEWS

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldDark)
                            .border(1.dp, EmeraldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = EmeraldGlow, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "لایه کش آفلاین Room و همگام‌سازی ابری Firestore",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Single Source of Truth (SSOT) - دسترسی ۱۰۰٪ آفلاین به داده‌ها",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanGlow,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status Banner
            val isOnline = offlineCacheStatus?.isOnline ?: true
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isOnline) EmeraldDark.copy(alpha = 0.4f) else SlateDark900)
                    .border(1.dp, if (isOnline) EmeraldNeon.copy(alpha = 0.5f) else AmberGold, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) EmeraldNeon else AmberGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) "پایگاه داده آنلاین و کش محلی Room متصل است" else "حالت آفلاین (استفاده ایمن از کش Room)",
                                color = if (isOnline) EmeraldGlow else AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = offlineCacheStatus?.syncMessage ?: "داده‌ها در حافظه محلی SQLite به‌صورت آنی ذخیره می‌شوند.",
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onSyncCloud,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "همگام‌سازی", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    title = "اشتراک‌های Room",
                    value = "${subscriptions.size} رکورد",
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    title = "سیگنال‌های Room",
                    value = "${signals.size} سیگنال",
                    icon = Icons.Default.SignalCellularAlt,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    title = "اخبار فاندامنتال",
                    value = "${newsList.size} خبر",
                    icon = Icons.Default.Newspaper,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category selector tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "SUBSCRIPTIONS" to "اشتراک‌های کاربران (${subscriptions.size})",
                    "SIGNALS" to "سیگنال‌های تاریخی (${signals.size})",
                    "NEWS" to "اخبار فاندامنتال (${newsList.size})"
                ).forEach { (code, title) ->
                    val isSel = selectedCategory == code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) SlateDark800 else SlateDark900)
                            .border(1.dp, if (isSel) EmeraldNeon else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = code }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSel) EmeraldGlow else TextMuted,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List based on selected category
            when (selectedCategory) {
                "SUBSCRIPTIONS" -> {
                    if (subscriptions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("هیچ اشتراکی در کش محلی ثبت نشده است.", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            subscriptions.forEach { sub ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = SlateDark900)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = sub.userEmail,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(EmeraldDark)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = sub.planTitle,
                                                    color = EmeraldGlow,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "مدت: ${sub.durationDays} روز | پرداخت: ${sub.paymentMethod}",
                                                color = TextSecondary,
                                                fontSize = 10.5.sp
                                            )
                                            Text(
                                                text = "شناسه کش Room: ${sub.id.take(16)}",
                                                color = CyanGlow,
                                                fontSize = 9.5.sp
                                            )
                                        }

                                        if (sub.transactionRef.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "کد رهگیری: ${sub.transactionRef}",
                                                color = TextMuted,
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "SIGNALS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        signals.take(8).forEach { sig ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SlateDark900)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${sig.asset} (${sig.direction})",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        )
                                        Text(
                                            text = "استرایک: ${sig.strikePrice} | زمان: ${sig.expiry} | سود: ${sig.payoutRate}",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (sig.status) {
                                                    "WON" -> EmeraldDark
                                                    "LOST" -> CrimsonRed.copy(alpha = 0.3f)
                                                    "NO_TRADE" -> SlateDark800
                                                    else -> CyanGlow.copy(alpha = 0.2f)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = when (sig.status) {
                                                "WON" -> "برد (WON)"
                                                "LOST" -> "باخت (LOST)"
                                                "NO_TRADE" -> "بدون ورود (VETO)"
                                                else -> "فعال (ACTIVE)"
                                            },
                                            color = when (sig.status) {
                                                "WON" -> EmeraldGlow
                                                "LOST" -> CrimsonGlow
                                                "NO_TRADE" -> AmberGold
                                                else -> CyanNeon
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "NEWS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        newsList.take(6).forEach { news ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SlateDark900)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = news.title,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EmeraldDark)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(news.category, color = EmeraldGlow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = news.summary,
                                        color = TextSecondary,
                                        fontSize = 10.5.sp,
                                        maxLines = 2
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

