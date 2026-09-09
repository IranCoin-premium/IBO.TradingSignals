package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.util.IranianMarket
import com.example.util.IranianMarketManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IranianMarketsSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val markets = remember { IranianMarketManager.getAllMarkets() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SoftUiSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SoftUiCardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Row
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
                            .background(SoftCardMintGradient[0]),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = SoftCardMintAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "استورهای رسمی ایرانی",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextDarkPrimary
                        )
                        Text(
                            text = "کافه بازار • مایکت • ایران اپس",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = SoftCardPurpleAccent
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
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

            Spacer(modifier = Modifier.height(14.dp))

            // Trust badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoftCardMintGradient[0].copy(alpha = 0.5f))
                    .border(1.dp, SoftCardMintAccent.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SoftCardMintAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تمامی نسخه‌ها با امضای دیجیتال معتبر، آنتی‌ویروس بازار شیلد و بررسی امنیتی مایکت و ایران اپس منتشر می‌شوند.",
                        color = TextDarkSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Markets List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(markets, key = { it.id }) { market ->
                    val isInstalled = remember(market) {
                        IranianMarketManager.isMarketInstalled(context, market)
                    }

                    MarketItemCard(
                        market = market,
                        isInstalled = isInstalled,
                        onOpen = {
                            val openedInApp = IranianMarketManager.openAppPage(context, market)
                            if (openedInApp) {
                                Toast.makeText(
                                    context,
                                    "در حال بازگشایی در ${market.titleFa}...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onRate = {
                            val openedInApp = IranianMarketManager.openRatingPage(context, market)
                            if (openedInApp) {
                                Toast.makeText(
                                    context,
                                    "با ثبت امتیاز ۵ ستاره در ${market.titleFa} از ما حمایت کنید ⭐",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }

                // Share Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = SoftCardPurpleAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "معرفی به دوستان و تریدرها",
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "لینک‌های دانلود از کافه بازار، مایکت و ایران اپس را با یک لمس برای دیگران بفرستید.",
                                color = TextDarkMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    IranianMarketManager.shareDownloadLinks(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftCardPurpleAccent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("share_markets_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ارسال لینک ۳ مارکت",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MarketItemCard(
    market: IranianMarket,
    isInstalled: Boolean,
    onOpen: () -> Unit,
    onRate: () -> Unit
) {
    val brandColor = Color(market.brandColorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = SoftUiShadowDark, ambientColor = SoftUiShadowDark)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SoftUiSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Store Title & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brandColor.copy(alpha = 0.15f))
                            .border(1.dp, brandColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = market.titleFa.take(1),
                            color = brandColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = market.titleFa,
                                color = TextDarkPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${market.titleEn})",
                                color = TextDarkMuted,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = market.tagline,
                            color = brandColor,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Installed or Web badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isInstalled) SoftCardMintGradient[0] else SoftUiBg)
                        .border(
                            1.dp,
                            if (isInstalled) SoftCardMintAccent.copy(alpha = 0.3f) else SoftUiCardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isInstalled) Icons.Default.PhoneAndroid else Icons.Default.Language,
                            contentDescription = null,
                            tint = if (isInstalled) SoftCardMintAccent else TextDarkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isInstalled) "نصب است" else "بازگشایی وب",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isInstalled) SoftCardMintAccent else TextDarkMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = market.description,
                color = TextDarkSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dual CTA Buttons: Open/Update and Rate 5-Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("open_${market.id}_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاهده در ${market.titleFa}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRate,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SoftCardPeachAccent
                    ),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                        .testTag("rate_${market.id}_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SoftCardPeachAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ثبت ۵ ستاره ★",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
