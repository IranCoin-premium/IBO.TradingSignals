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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationSettings
import com.example.data.local.SignalEntity
import com.example.fcm.FcmNotificationHelper
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.SlateDark950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: NotificationSettings,
    onToggleMaster: (Boolean) -> Unit,
    onToggleCategory: (String, Boolean) -> Unit,
    onToggleHighAccuracyOnly: (Boolean) -> Unit,
    onToggleRiskWarnings: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onResetDefaults: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark950)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SlateDark900)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "تنظیمات نوتیفیکیشن‌ها",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "مدیریت دریافت هشدار دسته‌بندی‌های سیگنال با DataStore",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }

                // Reset to default button
                IconButton(
                    onClick = { showResetConfirmDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SlateDark900)
                        .border(1.dp, CardBorder, CircleShape)
                        .testTag("settings_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "بازنشانی تنظیمات پیش‌فرض",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Master Notifications Toggle Hero Card
        item {
            MasterNotificationCard(
                masterEnabled = settings.masterEnabled,
                onToggle = onToggleMaster
            )
        }

        // 3. Category Preferences Section
        item {
            SectionHeader(
                title = "دسته‌بندی‌های ترجیحی سیگنال (Signal Categories)",
                subtitle = "انتخاب کنید کدام بازارها برای شما نوتیفیکیشن آنی ارسال کنند",
                icon = Icons.Default.Tune,
                iconTint = CyanNeon
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // OTC Category
                    SettingToggleRow(
                        title = "بازارهای OTC (۲۴ ساعته و آخر هفته)",
                        subtitle = "سیگنال‌های Over-The-Counter مناسب ترید پیوسته در تمام ایام هفته",
                        icon = Icons.Default.ShowChart,
                        iconTint = EmeraldNeon,
                        enabled = settings.masterEnabled,
                        isChecked = settings.otcEnabled,
                        onCheckedChange = { onToggleCategory("OTC", it) },
                        testTag = "toggle_category_otc"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = SlateDark800,
                        thickness = 0.8.dp
                    )

                    // Forex Category
                    SettingToggleRow(
                        title = "جفت‌ارزهای فارکس (Forex Majors & Minors)",
                        subtitle = "سیگنال‌های جفت‌ارزهای جهانی مانند EUR/USD, GBP/USD, USD/JPY",
                        icon = Icons.Default.CurrencyExchange,
                        iconTint = CyanNeon,
                        enabled = settings.masterEnabled,
                        isChecked = settings.forexEnabled,
                        onCheckedChange = { onToggleCategory("FOREX", it) },
                        testTag = "toggle_category_forex"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = SlateDark800,
                        thickness = 0.8.dp
                    )

                    // Crypto Category
                    SettingToggleRow(
                        title = "ارزهای دیجیتال (Crypto Trading)",
                        subtitle = "سیگنال‌های لحظه‌ای کریپتوکارنسی BTC, ETH, SOL و آلت‌کوین‌ها",
                        icon = Icons.Default.CurrencyBitcoin,
                        iconTint = AmberGold,
                        enabled = settings.masterEnabled,
                        isChecked = settings.cryptoEnabled,
                        onCheckedChange = { onToggleCategory("CRYPTO", it) },
                        testTag = "toggle_category_crypto"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = SlateDark800,
                        thickness = 0.8.dp
                    )

                    // Commodities Category
                    SettingToggleRow(
                        title = "طلا و نفت و کالاها (Commodities)",
                        subtitle = "سیگنال‌های انس طلا (XAU/USD)، نفت خام (USOIL) و نقره",
                        icon = Icons.Default.WorkspacePremium,
                        iconTint = Color(0xFFFBBF24),
                        enabled = settings.masterEnabled,
                        isChecked = settings.commoditiesEnabled,
                        onCheckedChange = { onToggleCategory("COMMODITIES", it) },
                        testTag = "toggle_category_commodities"
                    )
                }
            }
        }

        // 4. Accuracy & Risk Strategy Filters
        item {
            SectionHeader(
                title = "فیلترهای کیفیت و مدیریت ریسک",
                subtitle = "شخصی‌سازی حداقل دقت و هشدارهای شرایط پرریسک بازار",
                icon = Icons.Default.Shield,
                iconTint = AmberGold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // High Accuracy Only (85%+)
                    SettingToggleRow(
                        title = "فقط سیگنال‌های وین‌ریت بالا (۸۵٪+)",
                        subtitle = "فیلتر خودکار و عدم ارسال نوتیفیکیشن برای سیگنال‌های با ضریب اطمینان معمولی",
                        icon = Icons.Default.Verified,
                        iconTint = CyanNeon,
                        enabled = settings.masterEnabled,
                        isChecked = settings.highAccuracyOnly,
                        onCheckedChange = onToggleHighAccuracyOnly,
                        testTag = "toggle_high_accuracy_only"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = SlateDark800,
                        thickness = 0.8.dp
                    )

                    // Risk & NO_TRADE Warnings
                    SettingToggleRow(
                        title = "هشدارهای عدم معامله (NO_TRADE)",
                        subtitle = "اطلاع‌رسانی فوری زمان‌های انتشار اخبار سنگین اقتصادی و نوسانات غیرعادی",
                        icon = Icons.Default.Warning,
                        iconTint = CrimsonRed,
                        enabled = settings.masterEnabled,
                        isChecked = settings.riskWarningsEnabled,
                        onCheckedChange = onToggleRiskWarnings,
                        testTag = "toggle_risk_warnings"
                    )
                }
            }
        }

        // 5. Sound & Alert Delivery
        item {
            SectionHeader(
                title = "حالت‌های صوتی و بازخورد لمسی",
                subtitle = "تنظیم زنگ هشدار و ویبره هنگام دریافت سیگنال باینری آپشن",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                iconTint = EmeraldNeon
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow(
                        title = "صدای زنگ نوتیفیکیشن (Sound)",
                        subtitle = "پخش آلارم صوتی اختصاصی هنگام صدور سیگنال معتبر",
                        icon = Icons.Default.Notifications,
                        iconTint = EmeraldNeon,
                        enabled = settings.masterEnabled,
                        isChecked = settings.soundEnabled,
                        onCheckedChange = onToggleSound,
                        testTag = "toggle_sound"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = SlateDark800,
                        thickness = 0.8.dp
                    )

                    SettingToggleRow(
                        title = "لرزش دستگاه (Vibration)",
                        subtitle = "الگوی ویبره ۲ مرحله‌ای برای جلب توجه در لحظه ورود به معامله",
                        icon = Icons.Default.Vibration,
                        iconTint = CyanGlow,
                        enabled = settings.masterEnabled,
                        isChecked = settings.vibrationEnabled,
                        onCheckedChange = onToggleVibration,
                        testTag = "toggle_vibration"
                    )
                }
            }
        }

        // 6. Test Notification Trigger Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SlateDark800, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SlateDark900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(EmeraldGlow.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = EmeraldNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "تست و بررسی عملکرد نوتیفیکیشن",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "تست شبیه‌سازی دریافت اعلان طبق فیلترهای بالا",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "تنظیمات شما به صورت واکنشی (Reactive) در Jetpack DataStore ذخیره می‌شوند و تغییرات فوراً بدون نیاز به راه‌اندازی مجدد برنامه اعمال می‌گردند.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 18.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!settings.masterEnabled) {
                                Toast.makeText(
                                    context,
                                    "کلید اصلی نوتیفیکیشن‌ها خاموش است. ابتدا آن را فعال کنید.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val testSignal = SignalEntity(
                                    asset = "EUR/USD (OTC)",
                                    category = "OTC",
                                    direction = "CALL",
                                    strikePrice = "1.08450",
                                    currentPrice = "1.08445",
                                    expiry = "1M (یک‌دقیقه)",
                                    payoutRate = "92%",
                                    marketRegime = "Trend Bullish",
                                    confidenceScore = 92,
                                    riskScore = "کم ریسک (Low)",
                                    vetoStatus = "تایید شده",
                                    rationale = "بررسی هماهنگی تنظیمات نوتیفیکیشن در DataStore",
                                    recommendedBrokers = "Pocket Option, Quotex",
                                    status = "ACTIVE"
                                )
                                FcmNotificationHelper.showSignalNotification(
                                    context = context,
                                    signal = testSignal,
                                    customTitle = "🔔 تست نوتیفیکیشن سیگنال EUR/USD (OTC)",
                                    customBody = "تنظیمات DataStore با موفقیت فعال و هماهنگ شد."
                                )
                                Toast.makeText(
                                    context,
                                    "نوتیفیکیشن تستی طبق تنظیمات شما ارسال شد.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("test_notification_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldNeon,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ارسال نوتیفیکیشن تستی به گوشی",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }
        }

        // Bottom space
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            containerColor = CardSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بازنشانی تنظیمات اعلان‌ها",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
            },
            text = {
                Text(
                    text = "آیا می‌خواهید تمام تنظیمات نوتیفیکیشن‌های DataStore به حالت اولیه (روشن بودن همه دسته‌بندی‌ها) بازگردانده شوند؟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDefaults()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "تنظیمات به حالت پیش‌فرض بازگشتند.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("بازنشانی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MasterNotificationCard(
    masterEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.5.dp,
                if (masterEnabled) EmeraldNeon.copy(alpha = 0.5f) else SlateDark800,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (masterEnabled) SlateDark900 else CardSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (masterEnabled) EmeraldNeon.copy(alpha = 0.15f) else SlateDark800
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (masterEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (masterEnabled) EmeraldNeon else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "کلید اصلی اعلان‌ها",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (masterEnabled) EmeraldNeon.copy(alpha = 0.15f) else SlateDark800
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (masterEnabled) "فعال" else "خاموش",
                                color = if (masterEnabled) EmeraldNeon else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (masterEnabled) "دریافت سیگنال‌های معاملاتی و هشدارهای مهم فعال است" else "تمام اعلان‌های برنامه موقتاً غیرفعال شده‌اند",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = masterEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.testTag("master_notification_switch"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = EmeraldNeon,
                    uncheckedThumbColor = SlateDark700,
                    uncheckedTrackColor = SlateDark800,
                    uncheckedBorderColor = SlateDark700
                )
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    enabled: Boolean,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (enabled) iconTint.copy(alpha = 0.12f) else SlateDark800.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) iconTint else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (enabled) TextPrimary else TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                    color = if (enabled) TextSecondary else TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EmeraldNeon,
                uncheckedThumbColor = SlateDark700,
                uncheckedTrackColor = SlateDark800,
                uncheckedBorderColor = SlateDark700
            )
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = TextSecondary
            )
        }
    }
}
