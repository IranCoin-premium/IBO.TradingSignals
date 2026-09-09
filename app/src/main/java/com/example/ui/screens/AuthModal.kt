package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardCyanGradient
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

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
                    color = TextDarkPrimary
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SoftUiBg)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
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
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleGradient[0]),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("ورود با Google", color = SoftCardPurpleAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        onGitHubSignIn()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCardCyanGradient[0]),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("ورود با GitHub", color = SoftCardCyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(SoftUiCardBorder))
                Text(" یا ورود با ایمیل و رمز ", color = TextDarkMuted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Box(modifier = Modifier.weight(1f).height(1.dp).background(SoftUiCardBorder))
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isRegisterMode) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("نام و نام خانوادگی", color = TextDarkSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SoftCardCyanAccent) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftUiBg,
                        unfocusedContainerColor = SoftUiBg,
                        focusedBorderColor = SoftCardPurpleAccent,
                        unfocusedBorderColor = SoftUiCardBorder,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("آدرس ایمیل", color = TextDarkSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SoftCardPurpleAccent) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SoftUiBg,
                    unfocusedContainerColor = SoftUiBg,
                    focusedBorderColor = SoftCardPurpleAccent,
                    unfocusedBorderColor = SoftUiCardBorder,
                    focusedTextColor = TextDarkPrimary,
                    unfocusedTextColor = TextDarkPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passInput,
                onValueChange = { passInput = it },
                label = { Text("کلمه عبور", color = TextDarkSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SoftCardMintAccent) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SoftUiBg,
                    unfocusedContainerColor = SoftUiBg,
                    focusedBorderColor = SoftCardPurpleAccent,
                    unfocusedBorderColor = SoftUiCardBorder,
                    focusedTextColor = TextDarkPrimary,
                    unfocusedTextColor = TextDarkPrimary
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(errorMessage!!, color = SoftCardPeachAccent, fontSize = 11.sp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftCardPurpleAccent),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isRegisterMode) "ثبت‌نام و ورود به پلتفرم" else "ورود به حساب کاربری",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isRegisterMode) "قبلاً ثبت‌نام کرده‌اید؟ ورود به حساب" else "حساب کاربری ندارید؟ ثبت‌نام و عضویت VIP",
                color = SoftCardPurpleAccent,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { isRegisterMode = !isRegisterMode }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
