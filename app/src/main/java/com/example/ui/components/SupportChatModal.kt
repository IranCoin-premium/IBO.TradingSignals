package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "AGENT"
    val text: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatModal(
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "AGENT", "سلام و درود به پلتفرم رسمی ایران باینری آپشن خوش آمدید! 👋 من کارشناس پشتیبانی ۲۴ ساعته هستم. چگونه می‌توانم در زمینه سیگنال‌ها، اشتراک و بروکرهای باینری به شما کمک کنم؟", "هم‌اکنون")
        )
    }
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "نحوه خرید و فعال‌سازی اشتراک",
        "کدام بروکر برای ایران بهتر است؟",
        "آیا سیگنال‌های OTC سودآور هستند؟",
        "تفاوت CALL و PUT چیست؟",
        "آدرس کانال تلگرام و واتساپ"
    )

    fun sendMessage(msg: String) {
        if (msg.isBlank()) return
        messages.add(ChatMessage(System.currentTimeMillis().toString(), "USER", msg, "هم‌اکنون"))
        inputText = ""

        // Automated intelligent response
        val reply = when {
            "اشتراک" in msg || "خرید" in msg -> {
                "پلن‌های اشتراکی ایران باینری آپشن در ۵ دوره (۱ هفته، ۱ ماه، ۳ ماه، ۶ ماه و ۱ ساله) ارائه می‌شوند. در تب فروشگاه می‌توانید با کارت به کارت شتابی یا رمزارز USDT به صورت آنی فعال نمایید."
            }
            "بروکر" in msg || "ایران" in msg -> {
                "بروکرهای Pocket Option، Quotex و CloseOption در حال حاضر بالاترین سرعت واریز و برداشت ریالی و تتر را برای معامله‌گران ایرانی بدون نیاز به فیلترشکن ارائه می‌دهند."
            }
            "otc" in msg.lowercase() || "او تی سی" in msg -> {
                "بازارهای OTC به صورت ۲۴ ساعته حتی روزهای تعطیل فعال هستند. سیگنال‌های OTC ما با هوش مصنوعی و فیلتر No Trade برای جلوگیری از ضرر ناشی از اسپرد تحلیل می‌شوند."
            }
            "تلگرام" in msg || "واتساپ" in msg -> {
                "کانال رسمی تلگرام: @IranBinaryOfficial\nارتباط با پشتیبانی مستقیم تلگرام: @IranBinary_Support\nپشتیبانی فوری ۲۴ ساعته در خدمت شماست."
            }
            else -> {
                "پیام شما به واحد تحلیل و پشتیبانی ارشد ارسال شد. برای راهنمایی دقیق‌تر، سوالات خود را مطرح نمایید یا به پشتیبانی تلگرام مراجعه فرمایید."
            }
        }
        messages.add(ChatMessage((System.currentTimeMillis() + 1).toString(), "AGENT", reply, "هم‌اکنون"))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateDark900,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            // Support Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldDark)
                            .border(1.dp, EmeraldNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = EmeraldGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "پشتیبانی فوری ۲۴ ساعته",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "آنلاین و پاسخگوی سریع",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldGlow
                            )
                        }
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            // Quick Questions Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                items(quickQuestions) { q ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SlateDark800)
                            .border(0.8.dp, CardBorder, RoundedCornerShape(20.dp))
                            .clickable { sendMessage(q) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = q,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = CyanGlow
                        )
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { chat ->
                    val isUser = chat.sender == "USER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .background(
                                    if (isUser) EmeraldDark else CardSurface
                                )
                                .border(
                                    1.dp,
                                    if (isUser) EmeraldNeon.copy(alpha = 0.4f) else CardBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isUser) "شما" else "پشتیبانی ایران باینری",
                                    color = if (isUser) EmeraldGlow else AmberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = chat.text,
                                    color = TextPrimary,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = chat.time,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("پیام خود را بنویسید...", fontSize = 12.sp, color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardSurface,
                        unfocusedContainerColor = CardSurface,
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { sendMessage(inputText) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}
