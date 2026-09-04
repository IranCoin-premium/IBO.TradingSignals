package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModal(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onGitHubSignIn: () -> Unit,
    onManualLogin: (email: String, pass: String) -> Unit,
    onManualRegister: (email: String, pass: String, name: String) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateDark900
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRegisterMode) "ثبت‌نام کاربر جدید" else "ورود به حساب کاربری",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick OAuth Buttons (Google & GitHub)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onGoogleSignIn()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ورود با Google G", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }

                Button(
                    onClick = {
                        onGitHubSignIn()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ورود با GitHub", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(CardBorder))
                Text(" یا ورود با ایمیل و رمز ", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Box(modifier = Modifier.weight(1f).height(1.dp).background(CardBorder))
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isRegisterMode) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("نام و نام خانوادگی") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanGlow) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("آدرس ایمیل") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldNeon) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passInput,
                onValueChange = { passInput = it },
                label = { Text("کلمه عبور") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberGold) }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(errorMessage!!, color = AmberGold, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (emailInput.isBlank() || passInput.isBlank()) {
                        errorMessage = "لطفاً تمامی فیلدها را پر نمایید."
                        return@Button
                    }
                    if (isRegisterMode) {
                        onManualRegister(emailInput, passInput, if (nameInput.isBlank()) "کاربر محترم" else nameInput)
                    } else {
                        onManualLogin(emailInput, passInput)
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRegisterMode) "ثبت‌نام و ورود به پلتفرم" else "ورود به حساب کاربری",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isRegisterMode) "قبلاً ثبت‌نام کرده‌اید؟ ورود به حساب" else "حساب کاربری ندارید؟ ثبت‌نام رایگان",
                color = CyanGlow,
                fontSize = 11.5.sp,
                modifier = Modifier
                    .clickable { isRegisterMode = !isRegisterMode }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
