package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.SignalEntity
import com.example.ui.theme.*

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SignalCard(
    signal: SignalEntity,
    modifier: Modifier = Modifier,
    onToggleFavorite: ((SignalEntity) -> Unit)? = null,
    onReportClick: ((SignalEntity) -> Unit)? = null,
    onClick: ((SignalEntity) -> Unit)? = null
) {
    val isCall = signal.direction.uppercase() == "CALL" || signal.direction.contains("خرید") || signal.direction.contains("بالا")
    val isPut = signal.direction.uppercase() == "PUT" || signal.direction.contains("فروش") || signal.direction.contains("پایین")

    // انتخاب گرادیان پاستلی بر اساس ایندکس یا نوع سیگنال (طرح دقیق تصویر ارسالی)
    val cardIndex = Math.abs(signal.id.toInt()) % 5
    val (cardGradient, cardAccent) = when (cardIndex) {
        0 -> Pair(SoftCardCyanGradient, SoftCardCyanAccent)
        1 -> Pair(SoftCardPurpleGradient, SoftCardPurpleAccent)
        2 -> Pair(SoftCardMintGradient, SoftCardMintAccent)
        3 -> Pair(SoftCardPeachGradient, SoftCardPeachAccent)
        else -> Pair(SoftCardRoseGradient, SoftCardRoseAccent)
    }

    val directionColor = when {
        isCall -> SoftCardCyanAccent
        isPut -> SoftCardPeachAccent
        else -> AmberGold
    }

    val directionLabel = when {
        isCall -> "CALL"
        isPut -> "PUT"
        else -> "HOLD"
    }

    Box(
        modifier = modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = cardAccent.copy(alpha = 0.2f),
                spotColor = cardAccent.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(cardGradient))
            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick?.invoke(signal) }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // سمت چپ: ستاره علاقه‌مندی + نام جفت ارز و قیمت
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onToggleFavorite?.invoke(signal) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (signal.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "نشان کردن",
                        tint = if (signal.isFavorite) CanaryYellowDark else cardAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = signal.asset,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextDarkPrimary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        
                        // بج کوچک دسته (مثلا OTC یا Forex)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(cardAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = signal.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = cardAccent
                            )
                        }
                        
                        // Status Badge
                        val statusBg = when (signal.status) {
                            "WON", "Win" -> SoftCardMintAccent.copy(alpha = 0.15f)
                            "LOST", "Loss" -> SoftCardPeachAccent.copy(alpha = 0.15f)
                            "ACTIVE", "Pending" -> SoftCardPurpleAccent.copy(alpha = 0.15f)
                            else -> TextDarkSecondary.copy(alpha = 0.15f)
                        }
                        val statusTextColor = when (signal.status) {
                            "WON", "Win" -> SoftCardMintAccent
                            "LOST", "Loss" -> SoftCardPeachAccent
                            "ACTIVE", "Pending" -> SoftCardPurpleAccent
                            else -> TextDarkSecondary
                        }
                        val statusLabel = when (signal.status) {
                            "WON", "Win" -> "WIN"
                            "LOST", "Loss" -> "LOSS"
                            "ACTIVE", "Pending" -> "PENDING"
                            else -> signal.status
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "قیمت: ${signal.currentPrice}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextDarkSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // سمت راست: زمان انقضا و درصد هوش مصنوعی (طرح دقیق نمونه ارسالی)
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // کپسول زمان و نوع سیگنال
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(0.8.dp, cardAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$directionLabel ${signal.expiry}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = directionColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // درصد تایید هوش مصنوعی
                Text(
                    text = "${signal.confidenceScore}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    ),
                    color = cardAccent
                )
            }
        }
    }
}

/**
 * شیت جزئیات کامل سیگنال مطابق با ستون وسط تصویر ارسالی (Gradient Soft UI Signal Details)
 */
@Composable
fun SignalDetailsModalSheet(
    signal: SignalEntity,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isCall = signal.direction.uppercase() == "CALL" || signal.direction.contains("خرید") || signal.direction.contains("بالا")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftUiBg.copy(alpha = 0.95f))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .shadow(12.dp, RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp)),
                colors = CardDefaults.cardColors(containerColor = SoftUiSurface),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // نوار هدر مودال: بازگشت + عنوان + ستاره
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftUiBg)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = TextDarkPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "جزئیات سیگنال",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDarkPrimary
                        )

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftUiBg)
                        ) {
                            Icon(
                                imageVector = if (signal.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "نشان کردن",
                                tint = if (signal.isFavorite) CanaryYellowDark else TextDarkSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // نام جفت ارز و آیکون هوش مصنوعی
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = signal.asset,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TextDarkPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoftCardPurpleGradient[0])
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "تایید هوش مصنوعی",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftCardPurpleAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ردیف جهت سیگنال CALL/PUT و درصد تایید AI
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.horizontalGradient(if (isCall) SoftCardBlueGradient else SoftCardRoseGradient))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isCall) "CALL" else "PUT",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = if (isCall) SoftCardBlueAccent else SoftCardRoseAccent
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${signal.confidenceScore}%",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                color = if (isCall) SoftCardBlueAccent else SoftCardRoseAccent
                            )
                            Text(
                                text = "درصد تایید AI",
                                fontSize = 10.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // چارت خطی نرم و گرادیانتی (Soft Gradient Wave Chart)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(SoftCardBlueGradient[0], SoftUiSurface)))
                            .border(1.dp, SoftUiCardBorder, RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val path = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.2f, h * 0.3f, w * 0.4f, h * 0.8f, w * 0.6f, h * 0.4f)
                                cubicTo(w * 0.75f, h * 0.2f, w * 0.85f, h * 0.6f, w, h * 0.25f)
                            }

                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }

                            // پر کردن نرم گرادیانتی چارت
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        SoftCardBlueAccent.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // خط اصلی چارت
                            drawPath(
                                path = path,
                                color = SoftCardBlueAccent,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // دو باکس فلت نئومورفیک: قیمت ورود و قیمت استرایک
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SoftUiBg)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("قیمت ورود", fontSize = 11.sp, color = TextDarkSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = signal.currentPrice,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextDarkPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SoftUiBg)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("قیمت استرایک", fontSize = 11.sp, color = TextDarkSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = signal.strikePrice,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextDarkPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // کادر زمان انقضا با پس‌زمینه نرم
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SoftUiBg)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("زمان انقضا (${signal.expiry})", fontSize = 11.sp, color = TextDarkSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "00:45",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = TextDarkPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // دو دکمه کپسولی گرادیانتی CALL و PUT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(SoftCallGradient))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CALL",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(SoftPutGradient))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PUT",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // افشای ریسک قانونی اجباری
                    Text(
                        text = "این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.",
                        fontSize = 10.sp,
                        color = TextDarkMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HighlightedRationaleText(
    text: String,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val words = text.split(" ")
            words.forEachIndexed { index, word ->
                when {
                    word.contains("CALL", ignoreCase = true) || word.contains("صعودی") || word.contains("خرید") -> {
                        withStyle(SpanStyle(color = SoftCardCyanAccent, fontWeight = FontWeight.Black)) { append(word) }
                    }
                    word.contains("PUT", ignoreCase = true) || word.contains("نزولی") || word.contains("فروش") -> {
                        withStyle(SpanStyle(color = SoftCardPeachAccent, fontWeight = FontWeight.Black)) { append(word) }
                    }
                    word.contains("OTC", ignoreCase = true) -> {
                        withStyle(SpanStyle(color = SoftCardPurpleAccent, fontWeight = FontWeight.Bold)) { append(word) }
                    }
                    word.contains("00s") || word.contains("ثانیه") || word.contains("انقضا") || word.contains("دقیقه") -> {
                        withStyle(SpanStyle(color = AmberGold, fontWeight = FontWeight.Bold)) { append(word) }
                    }
                    word.contains("RSI") || word.contains("MACD") || word.contains("EMA") || word.contains("Pivot") || word.contains("مقاومت") || word.contains("حمایت") -> {
                        withStyle(SpanStyle(color = CanaryYellowDark, fontWeight = FontWeight.Bold)) { append(word) }
                    }
                    word.contains("وتو") || word.contains("هشدار") || word.contains("ریسک") -> {
                        withStyle(SpanStyle(color = CrimsonRed, fontWeight = FontWeight.Bold)) { append(word) }
                    }
                    else -> {
                        withStyle(SpanStyle(color = TextDarkPrimary)) { append(word) }
                    }
                }
                if (index < words.size - 1) append(" ")
            }
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall.copy(
            lineHeight = 20.sp,
            fontSize = 11.8.sp
        ),
        modifier = modifier
    )
}
