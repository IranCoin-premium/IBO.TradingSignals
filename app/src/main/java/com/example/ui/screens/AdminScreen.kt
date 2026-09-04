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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NewsEntity
import com.example.data.local.PlanEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserEntity
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
    onRunAiAgent: (String) -> String
) {
    var adminEmailInput by remember { mutableStateOf("admin@iranbinary.ir") }
    var adminPassInput by remember { mutableStateOf("IranBinaryAdmin2026!") }
    var showPassword by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("AI_AGENT") } // AI_AGENT, SIGNALS, PLANS, NEWS, SECURITY

    val isAdminLoggedIn = currentUser != null && (currentUser.role == "ADMIN" || currentUser.role == "STAFF")

    if (!isAdminLoggedIn) {
        // Admin Login Screen with pre-configured default credentials
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
                            text = "Iran Binary Option Staff & Admin Portal",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanGlow
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Explicit Banner informing user of default login and password as requested
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateDark900)
                                .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
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
                                Text("💡 پس از ورود، می‌توانید رمز جدید ایجاد کرده یا ادمین و کارمند جدید تعریف نمایید.", color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = adminEmailInput,
                            onValueChange = { adminEmailInput = it },
                            label = { Text("نام کاربری یا ایمیل ادمین") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateDark900,
                                unfocusedContainerColor = SlateDark900,
                                focusedBorderColor = EmeraldNeon,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = adminPassInput,
                            onValueChange = { adminPassInput = it },
                            label = { Text("رمز عبور مدیریت") },
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
                                focusedContainerColor = SlateDark900,
                                unfocusedContainerColor = SlateDark900,
                                focusedBorderColor = EmeraldNeon,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLogin(adminEmailInput, adminPassInput) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "ورود به پنل مدیریت و نظارت",
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
        // Logged-in Admin Dashboard
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDark950),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGlow, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentUser?.fullName ?: "مدیریت ارشد",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "سطح دسترسی: ${currentUser?.role} | ${currentUser?.email}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanGlow
                            )
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateDark800)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "خروج", tint = CrimsonGlow, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Navigation Tabs
            item {
                val tabs = listOf(
                    "AI_AGENT" to "دستیار AI ادیتور",
                    "SIGNALS" to "مدیریت سیگنال‌ها",
                    "PLANS" to "ویرایش پلن‌ها",
                    "NEWS" to "انتشار اخبار",
                    "SECURITY" to "رمز و کارمندان"
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
                                .padding(horizontal = 14.dp, vertical = 7.dp)
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
                "AI_AGENT" -> {
                    item {
                        AiEditorAgentSection(onRunAiAgent = onRunAiAgent)
                    }
                }
                "SIGNALS" -> {
                    item {
                        SignalManagementSection(
                            signals = signals,
                            onAddSignal = onAddSignal,
                            onDeleteSignal = onDeleteSignal,
                            onUpdateSignalStatus = onUpdateSignalStatus
                        )
                    }
                }
                "PLANS" -> {
                    item {
                        PlanManagementSection(
                            plans = plans,
                            onUpdatePlan = onUpdatePlan
                        )
                    }
                }
                "NEWS" -> {
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
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

// Sub-Section: AI Assistant Chatbox / Prompt Box for Editor
@Composable
fun AiEditorAgentSection(onRunAiAgent: (String) -> String) {
    var promptInput by remember { mutableStateOf("") }
    var aiResponse by remember {
        mutableStateOf("سلام ادمین گرامی! 👋 من دستیار هوش مصنوعی ادیتور پلتفرم ایران باینری هستم. شما می‌توانید دستورات خود را مانند: 'تولید سیگنال جدید برای EUR/USD'، 'نگارش خبر تحلیلی یک‌ساعته'، یا 'محاسبه نرخ سربه‌سر Payout' بنویسید.")
    }

    val quickPrompts = listOf(
        "سیگنال جدید باینری آپشن بساز",
        "پیش‌نویس خبر فاندامنتال بنویس",
        "محاسبه مدیریت ریسک و No Trade"
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
                Icon(Icons.Default.Psychology, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("چت‌باکس و پرامپت دستیار هوش مصنوعی ادیتور", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("اتوماسیون ایجنت‌وار تولید سیگنال، نگارش فیدهای خبری و ممیزی ریسک", color = TextSecondary, fontSize = 10.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Prompt Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPrompts) { p ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateDark800)
                            .clickable {
                                promptInput = p
                                aiResponse = onRunAiAgent(p)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(p, color = CyanGlow, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Response Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SlateDark900)
                    .border(1.dp, EmeraldDark, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = aiResponse,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 21.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prompt Input Field
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("دستور خود را برای هوش مصنوعی بنویسید...", color = TextMuted, fontSize = 11.5.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateDark900,
                        unfocusedContainerColor = SlateDark900,
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
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}

// Sub-Section: Signal Management
@Composable
fun SignalManagementSection(
    signals: List<SignalEntity>,
    onAddSignal: (SignalEntity) -> Unit,
    onDeleteSignal: (Long) -> Unit,
    onUpdateSignalStatus: (SignalEntity, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    var assetInput by remember { mutableStateOf("GBP/USD (OTC)") }
    var categoryInput by remember { mutableStateOf("OTC") }
    var directionInput by remember { mutableStateOf("CALL") }
    var strikeInput by remember { mutableStateOf("1.29450") }
    var expiryInput by remember { mutableStateOf("1m") }
    var payoutInput by remember { mutableStateOf("۹۲٪") }
    var regimeInput by remember { mutableStateOf("شکست مومنتوم صعودی") }
    var confidenceInput by remember { mutableStateOf("87") }
    var brokersInput by remember { mutableStateOf("Quotex, Pocket Option") }
    var rationaleInput by remember { mutableStateOf("حمایت قوی M1 و تایید میانگین متحرک ۲۰ دوره‌ای") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("فهرست و ویرایش سیگنال‌ها (${signals.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("افزودن سیگنال جدید", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
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
                        Text(signal.asset, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(signal.direction, color = if (signal.direction == "CALL") EmeraldGlow else CrimsonGlow, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("استرایک: ${signal.strikePrice} | انقضا: ${signal.expiry} | بازدهی: ${signal.payoutRate}", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onUpdateSignalStatus(signal, "WON") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("علامت‌گذاری برد (WON)", color = EmeraldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onUpdateSignalStatus(signal, "LOST") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("علامت‌گذاری باخت (LOST)", color = CrimsonGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { onDeleteSignal(signal.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
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
                                riskScore = "کنترل‌شده (Controlled)",
                                vetoStatus = "تایید شده",
                                rationale = rationaleInput,
                                recommendedBrokers = brokersInput,
                                status = "ACTIVE"
                            )
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("انتشار سیگنال در اپلیکیشن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }, shape = RoundedCornerShape(12.dp)) {
                    Text("انصراف")
                }
            },
            title = { Text("انتشار سیگنال جدید", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = assetInput, onValueChange = { assetInput = it }, label = { Text("نام دارایی (مانند EUR/USD OTC)") }, singleLine = true)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = directionInput, onValueChange = { directionInput = it }, label = { Text("جهت (CALL/PUT/NO_TRADE)") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = expiryInput, onValueChange = { expiryInput = it }, label = { Text("انقضا (1m/5m)") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = strikeInput, onValueChange = { strikeInput = it }, label = { Text("قیمت استرایک") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = payoutInput, onValueChange = { payoutInput = it }, label = { Text("بازدهی (۹۲٪)") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    OutlinedTextField(value = brokersInput, onValueChange = { brokersInput = it }, label = { Text("بروکرهای بهینه") }, singleLine = true)
                    OutlinedTextField(value = rationaleInput, onValueChange = { rationaleInput = it }, label = { Text("تحلیل چندلایه هوش مصنوعی") }, maxLines = 3)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// Sub-Section: Plan Management
@Composable
fun PlanManagementSection(
    plans: List<PlanEntity>,
    onUpdatePlan: (PlanEntity) -> Unit
) {
    var editingPlan by remember { mutableStateOf<PlanEntity?>(null) }
    var priceTomanEdit by remember { mutableStateOf("") }
    var priceUsdtEdit by remember { mutableStateOf("") }
    var discountEdit by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ویرایش مشخصات و قیمت ۵ پلن اشتراک:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        plans.forEach { plan ->
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
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(plan.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${plan.durationText} | ${plan.priceToman} (${plan.priceUsdt})", color = EmeraldGlow, fontSize = 11.5.sp)
                        if (plan.discountPercent > 0) {
                            Text("تخفیف اعمال شده: ${plan.discountPercent}٪", color = AmberGold, fontSize = 10.5.sp)
                        }
                    }

                    Button(
                        onClick = {
                            editingPlan = plan
                            priceTomanEdit = plan.priceToman
                            priceUsdtEdit = plan.priceUsdt
                            discountEdit = plan.discountPercent.toString()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ویرایش", color = CyanNeon, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (editingPlan != null) {
        val plan = editingPlan!!
        AlertDialog(
            onDismissRequest = { editingPlan = null },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdatePlan(
                            plan.copy(
                                priceToman = priceTomanEdit,
                                priceUsdt = priceUsdtEdit,
                                discountPercent = discountEdit.toIntOrNull() ?: plan.discountPercent
                            )
                        )
                        editingPlan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ذخیره تغییرات پلن", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingPlan = null }, shape = RoundedCornerShape(10.dp)) {
                    Text("انصراف")
                }
            },
            title = { Text("ویرایش قیمت ${plan.title}", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = priceTomanEdit, onValueChange = { priceTomanEdit = it }, label = { Text("قیمت تومان") }, singleLine = true)
                    OutlinedTextField(value = priceUsdtEdit, onValueChange = { priceUsdtEdit = it }, label = { Text("قیمت دلاری USDT") }, singleLine = true)
                    OutlinedTextField(value = discountEdit, onValueChange = { discountEdit = it }, label = { Text("درصد تخفیف") }, singleLine = true)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(18.dp)
        )
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
            Text("فهرست اخبار یک‌ساعته (${newsList.size})", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

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
                    Text("ایجاد ادمین یا کارمند جدید", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(value = newStaffName, onValueChange = { newStaffName = it }, label = { Text("نام و نام خانوادگی کارمند") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = newStaffEmail, onValueChange = { newStaffEmail = it }, label = { Text("ایمیل یا نام کاربری") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = newStaffPass, onValueChange = { newStaffPass = it }, label = { Text("کلمه عبور اختصاصی") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = newStaffRole, onValueChange = { newStaffRole = it }, label = { Text("نقش کاربری (ADMIN یا STAFF)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

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
                    Text("افزودن و فعال‌سازی دسترسی کارمند", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (staffAddSuccess) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("✅ کارمند/ادمین جدید با موفقیت اضافه گردید.", color = CyanGlow, fontSize = 11.sp)
                }
            }
        }

        // List of Active Staff
        Text("فهرست مدیران و کارمندان ثبت‌شده در SQL:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        staffList.forEach { staff ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(staff.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text("${staff.email} | دسترسی: ${staff.role}", color = TextSecondary, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldDark)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(staff.role, color = EmeraldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
