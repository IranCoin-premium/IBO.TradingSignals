package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.SignalEntity
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPeachGradient
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiShadowDark
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

enum class FeedbackCategory(
    val id: String,
    val titleFa: String,
    val icon: ImageVector,
    val accentColor: Color,
    val bgGradient: List<Color>
) {
    SIGNAL_INACCURACY("SIGNAL_INACCURACY", "گزارش عدم انطباق سیگنال", Icons.Default.ReportProblem, SoftCardPeachAccent, SoftCardPeachGradient),
    IMPROVEMENT_SUGGESTION("IMPROVEMENT_SUGGESTION", "پیشنهاد بهبود امکانات", Icons.Default.Lightbulb, SoftCardPurpleAccent, SoftCardPurpleGradient),
    BROKER_ISSUE("BROKER_ISSUE", "مشکل در بروکر / پلتفرم", Icons.Default.Storefront, SoftCardCyanAccent, SoftCardCyanGradient),
    GENERAL("GENERAL", "سایر نظرات و بازخوردها", Icons.Default.Feedback, SoftCardMintAccent, SoftCardMintGradient)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubmitFeedbackDialog(
    initialSignal: SignalEntity? = null,
    userEmail: String? = null,
    availableSignals: List<SignalEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSubmit: (
        feedbackType: String,
        asset: String?,
        signalId: Long?,
        reasonCategory: String?,
        description: String,
        rating: Int,
        contactInfo: String?
    ) -> Unit
) {
    var selectedCategory by remember {
        mutableStateOf(
            if (initialSignal != null) FeedbackCategory.SIGNAL_INACCURACY
            else FeedbackCategory.SIGNAL_INACCURACY
        )
    }

    var selectedSignal by remember { mutableStateOf(initialSignal) }
    var selectedReasonTag by remember { mutableStateOf<String?>(null) }
    var descriptionText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var contactInfo by remember { mutableStateOf(userEmail ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val inaccuracyTags = listOf(
        "تفاوت قیمت ورود با بروکر (Slippage)",
        "نتیجه معامله OTM شد (جهت نادرست)",
        "تاخیر در دریافت نوتیفیکیشن",
        "عدم وتوی سیگنال در زمان خبر پرخطر",
        "کاهش Payout در بروکر",
        "عدم تطابق زمان انقضا (Expiry)"
    )

    val improvementTags = listOf(
        "افزودن بروکرهای ایرانی جدید",
        "سیگنال‌های باینری بازارهای کریپتو",
        "فیلتر بر اساس وین‌ریت بالای ۹۰٪",
        "امکان تست سیگنال در پلتفرم‌های معاملاتی",
        "هشدارهای صوتی اختصاصی برای CALL/PUT"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(22.dp)),
                color = SoftUiSurface,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(selectedCategory.bgGradient[0])
                                    .border(1.dp, selectedCategory.accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = selectedCategory.icon,
                                    contentDescription = null,
                                    tint = selectedCategory.accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ثبت بازخورد و گزارش خطا",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = "مستقیم به تیم تحلیل و توسعه ایران باینری",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SoftUiBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = TextDarkSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable Form Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Category Selection Chips
                        Column {
                            Text(
                                text = "موضوع بازخورد:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextDarkSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FeedbackCategory.values().forEach { category ->
                                    val isSelected = selectedCategory == category
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) category.bgGradient[0]
                                                else SoftUiBg
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) category.accentColor else SoftUiCardBorder,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                selectedCategory = category
                                                selectedReasonTag = null
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = category.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) category.accentColor else TextDarkSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = category.titleFa,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isSelected) category.accentColor else TextDarkSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Target Signal Banner / Selector
                        if (selectedCategory == FeedbackCategory.SIGNAL_INACCURACY) {
                            Column {
                                Text(
                                    text = "نماد / سیگنال مورد نظر:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextDarkSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                if (selectedSignal != null) {
                                    val sig = selectedSignal!!
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SoftUiBg)
                                            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${sig.asset} (${sig.category})",
                                                    fontWeight = FontWeight.Bold,
                                                    color = SoftCardPurpleAccent,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = "جهت: ${sig.direction} | استرایک: ${sig.strikePrice} | انقضا: ${sig.expiry}",
                                                    color = TextDarkSecondary,
                                                    fontSize = 11.5.sp
                                                )
                                            }
                                            Text(
                                                text = "تغییر",
                                                color = SoftCardPeachAccent,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable { selectedSignal = null }
                                                    .padding(4.dp)
                                            )
                                        }
                                    }
                                } else {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        availableSignals.take(6).forEach { sig ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(SoftUiBg)
                                                    .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(8.dp))
                                                    .clickable { selectedSignal = sig }
                                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    text = sig.asset,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextDarkPrimary
                                                )
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(SoftUiBg)
                                                .border(0.8.dp, SoftUiCardBorder, RoundedCornerShape(8.dp))
                                                .clickable { selectedSignal = null }
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "سایر نمادها / نامشخص",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextDarkSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Quick Reason Tags
                        val currentTags = when (selectedCategory) {
                            FeedbackCategory.SIGNAL_INACCURACY -> inaccuracyTags
                            FeedbackCategory.IMPROVEMENT_SUGGESTION -> improvementTags
                            else -> emptyList()
                        }

                        if (currentTags.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "علت یا برچسب سریع:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextDarkSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    currentTags.forEach { tag ->
                                        val isTagSelected = selectedReasonTag == tag
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isTagSelected) selectedCategory.bgGradient[0]
                                                    else SoftUiBg
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isTagSelected) selectedCategory.accentColor else SoftUiCardBorder,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedReasonTag = if (isTagSelected) null else tag
                                                    if (descriptionText.isBlank() && !isTagSelected) {
                                                        descriptionText = tag
                                                    }
                                                }
                                                .padding(horizontal = 9.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isTagSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isTagSelected) selectedCategory.accentColor else TextDarkSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Rating Stars
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedCategory == FeedbackCategory.SIGNAL_INACCURACY)
                                        "میزان رضایت از دقت کلی:"
                                    else "امتیاز کلی به عملکرد سامانه:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextDarkSecondary
                                )
                                Row {
                                    (1..5).forEach { starIndex ->
                                        Icon(
                                            imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "ستاره $starIndex",
                                            tint = if (starIndex <= rating) SoftCardPeachAccent else TextDarkMuted,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clickable { rating = starIndex }
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Description Input
                        Column {
                            Text(
                                text = "توضیحات و جزئیات:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextDarkSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = descriptionText,
                                onValueChange = {
                                    descriptionText = it
                                    if (it.length >= 3) validationError = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = when (selectedCategory) {
                                            FeedbackCategory.SIGNAL_INACCURACY -> "مثلاً: در کندل 10:35 بروکر پوکت آپشن قیمت ورود ۱.۰۸۵۵ بود در حالی که استرایک سیگنال ۱.۰۸۴۵ اعلام شد..."
                                            FeedbackCategory.IMPROVEMENT_SUGGESTION -> "مثلاً: اگر امکان مشخص کردن حد ضرر مجازی برای هر سشن به برنامه اضافه شود بسیار مفید خواهد بود..."
                                            else -> "توضیحات کامل خود را اینجا وارد فرمایید..."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextDarkMuted,
                                        lineHeight = 18.sp
                                    )
                                },
                                minLines = 3,
                                maxLines = 5,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = selectedCategory.accentColor,
                                    unfocusedBorderColor = SoftUiCardBorder,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary,
                                    focusedContainerColor = SoftUiBg,
                                    unfocusedContainerColor = SoftUiBg
                                )
                            )

                            if (validationError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = validationError!!,
                                    color = SoftCardPeachAccent,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // 6. Contact Info (Optional)
                        Column {
                            Text(
                                text = "اطلاعات تماس جهت پیگیری (اختیاری):",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextDarkSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = contactInfo,
                                onValueChange = { contactInfo = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "ایمیل یا شناسه تلگرام جهت اطلاع از نتیجه بررسی...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextDarkMuted
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SoftCardPurpleAccent,
                                    unfocusedBorderColor = SoftUiCardBorder,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary,
                                    focusedContainerColor = SoftUiBg,
                                    unfocusedContainerColor = SoftUiBg
                                )
                            )
                        }

                        // Persistence notice
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftUiBg)
                                .border(1.dp, SoftUiCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SoftCardMintAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "بازخوردها مستقیماً در دیتابیس محلی ذخیره شده و به تیم هوش مصنوعی ارجاع می‌گردند.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dialog Actions (Cancel & Submit)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextDarkSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftUiCardBorder)
                        ) {
                            Text("انصراف", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                if (descriptionText.trim().length < 3 && selectedReasonTag == null) {
                                    validationError = "لطفاً توضیحی کوتاه بنویسید یا یکی از برچسب‌های سریع را انتخاب کنید."
                                    return@Button
                                }
                                isSubmitting = true
                                val finalMessage = if (descriptionText.isNotBlank()) descriptionText.trim()
                                else (selectedReasonTag ?: "بدون توضیحات")

                                onSubmit(
                                    selectedCategory.id,
                                    selectedSignal?.asset,
                                    selectedSignal?.id,
                                    selectedReasonTag,
                                    finalMessage,
                                    rating,
                                    contactInfo.ifBlank { null }
                                )
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = selectedCategory.accentColor,
                                contentColor = Color.White
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Feedback,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ارسال بازخورد",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
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
