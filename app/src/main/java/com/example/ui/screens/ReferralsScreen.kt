package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.UsdtFeedData

@Composable
fun ReferralsScreen(
    referralCode: String,
    referralEarningsUsdt: Double,
    userUsdtWallet: String,
    usdtFeed: UsdtFeedData,
    referralDashboard: com.example.data.remote.DashboardDto? = null,
    tabsManifest: List<com.example.data.remote.PlatformTabDto> = emptyList(),
    onOpenAffiliateDetails: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // PART 12 — server-authoritative: code, balance and payouts come ONLY from the backend.
    // No device-side mock data: an honest empty state is shown until the server responds.
    val payoutItems = remember(referralDashboard) {
        referralDashboard?.payouts?.map { p ->
            ReferredFriendItem(
                name = "پرداخت #${p.id.take(8)}",
                planPurchased = p.method,
                rewardUsdt = "${p.amount} ${p.currency}",
                statusText = when (p.status) {
                    "paid" -> "پرداخت شده به کیف پول"
                    "approved" -> "تأیید شده — در صف پرداخت"
                    else -> "در انتظار بررسی ادمین"
                },
                isPaid = p.status == "paid"
            )
        } ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftUiBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Referral Network Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftCardPurpleAccent.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SoftCardPurpleGradient[0]),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GroupAdd,
                                    contentDescription = "Invite Friends",
                                    tint = SoftCardPurpleAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "شبکه زیرمجموعه‌ها و دعوت از دوستان",
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "کسب پاداش دلاری نامحدود با شبکه سازی",
                                    color = TextDarkSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "با ساخت شبکه تریدرها، دوستانتان را با ابزارهای نوین سیگنال‌دهی هوش مصنوعی آشنا کنید. با هر خریدی که اعضای زیرمجموعه شما انجام دهند، واریز آنی تتر به کیف پول شما ثبت می‌شود.",
                        color = TextDarkSecondary,
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenAffiliateDetails,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مشاهده جدول دقیق پورسانت‌ها و ثبت کیف پول", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Quick Invite Actions (Telegram, WhatsApp, SMS)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "میان‌برهای دعوت سریع به شبکه ترید",
                        color = TextDarkPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val refLink = "https://iranbinaryoption.com/ref/$referralCode"
                    val inviteMessage = "سلام! برای دریافت سیگنال‌های باینری آپشن با وین‌ریت بالای ۸۵٪ هوش مصنوعی، وارد اپلیکیشن ایران باینری آپشن شو. لینک ثبت‌نام با پاداش: $refLink (کد معرفی: $referralCode)"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy Link Button
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(refLink))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftCardCyanAccent)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SoftCardCyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کپی لینک", color = SoftCardCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Share Intent Button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, inviteMessage)
                                }
                                context.startActivity(Intent.createChooser(intent, "ارسال پیام دعوت"))
                            },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftCardMintAccent)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ارسال پیام دعوت", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Referred Friends List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لیست دوستان و پورسانت‌های واریزی",
                    color = TextDarkPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "${payoutItems.size} پورسانت ثبت‌شده",
                    color = SoftCardPurpleAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }
        }

        // 4. Referred Friends Items
        items(payoutItems) { friend ->
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
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftCardPurpleGradient[0]),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = friend.name.take(1),
                                color = SoftCardPurpleAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(friend.name, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(friend.planPurchased, color = TextDarkSecondary, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = friend.rewardUsdt,
                            color = SoftCardMintAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (friend.isPaid) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = if (friend.isPaid) SoftCardMintAccent else SoftCardPeachAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = friend.statusText,
                                color = if (friend.isPaid) SoftCardMintAccent else SoftCardPeachAccent,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Empty / server-pending state (PART 12 — honest, no mock data)
        if (payoutItems.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = SoftCardPeachAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (referralCode.isBlank()) "در حال دریافت اطلاعات از سرور…"
                            else "هنوز پورسانتی ثبت نشده — کد خود را به اشتراک بگذارید",
                            color = TextDarkSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 6. Platform Tab Registry (GET /api/v1/tabs/manifest) — tabs rendered from server
        if (tabsManifest.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تب‌های فعال پلتفرم (از رجیستری سرور)",
                            color = TextDarkPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        tabsManifest.forEach { tab ->
                            val titleRes = context.resources.getIdentifier(tab.title_i18n_key, "string", context.packageName)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• " + (if (titleRes != 0) stringResource(titleRes) else tab.title_i18n_key),
                                    color = TextDarkSecondary,
                                    fontSize = 12.sp
                                )
                                Text(text = tab.status, color = SoftCardMintAccent, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 7. Legal risk disclosure (mandatory per PART12_CORRECTION_DIRECTIVE.md)
        item {
            Text(
                text = stringResource(id = R.string.referral_risk_disclosure),
                color = TextDarkMuted,
                fontSize = 10.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private data class ReferredFriendItem(
    val name: String,
    val planPurchased: String,
    val rewardUsdt: String,
    val statusText: String,
    val isPaid: Boolean
)
