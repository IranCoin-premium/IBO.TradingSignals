package com.example.ui.components

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SignalEntity
import com.example.data.local.TradeLogEntity
import com.example.ui.theme.SoftCardCyanAccent
import com.example.ui.theme.SoftCardMintAccent
import com.example.ui.theme.SoftCardMintGradient
import com.example.ui.theme.SoftCardPeachAccent
import com.example.ui.theme.SoftCardPeachGradient
import com.example.ui.theme.SoftCardPurpleAccent
import com.example.ui.theme.SoftCardPurpleGradient
import com.example.ui.theme.SoftUiBg
import com.example.ui.theme.SoftUiCardBorder
import com.example.ui.theme.SoftUiSurface
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import java.util.Locale

@Composable
fun QuickJournalLogDialog(
    signal: SignalEntity,
    onDismiss: () -> Unit,
    onConfirmLog: (TradeLogEntity) -> Unit
) {
    var stakeAmountInput by remember { mutableStateOf("10") }
    var selectedOutcome by remember { mutableStateOf("WIN") } // WIN, LOSS, DRAW
    var brokerNameInput by remember { mutableStateOf("Pocket Option") }
    var noteInput by remember { mutableStateOf("ورود بر اساس سیگنال AI") }

    val stakeAmount = stakeAmountInput.toDoubleOrNull() ?: 10.0
    val payoutFactor = 0.92 // 92%
    val profitLoss = when (selectedOutcome) {
        "WIN" -> stakeAmount * payoutFactor
        "LOSS" -> -stakeAmount
        else -> 0.0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SoftUiSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SoftCardMintGradient[0]),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = SoftCardMintAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ثبت سریع معامله در ژورنال",
                    color = TextDarkPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Signal details preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftUiBg)
                        .border(1.dp, SoftUiCardBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(signal.asset, color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("جهت: ${signal.direction} | انقضا: ${signal.expiry}", color = TextDarkSecondary, fontSize = 11.sp)
                        }
                        Text(
                            text = "استرایک: ${signal.strikePrice}",
                            color = SoftCardPurpleAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Outcome selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "WIN" to "برد (ITM) 🟢",
                        "LOSS" to "باخت (OTM) 🔴",
                        "DRAW" to "بازگشت ⚪"
                    ).forEach { (code, label) ->
                        val isSelected = selectedOutcome == code
                        val (activeColor, activeBg) = when (code) {
                            "WIN" -> Pair(SoftCardMintAccent, SoftCardMintGradient[0])
                            "LOSS" -> Pair(SoftCardPeachAccent, SoftCardPeachGradient[0])
                            else -> Pair(SoftCardPurpleAccent, SoftCardPurpleGradient[0])
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) activeBg else SoftUiBg)
                                .border(
                                    0.8.dp,
                                    if (isSelected) activeColor else SoftUiCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedOutcome = code }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) activeColor else TextDarkSecondary
                            )
                        }
                    }
                }

                // Stake Amount Input
                OutlinedTextField(
                    value = stakeAmountInput,
                    onValueChange = { stakeAmountInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("حجم معامله ($):", fontSize = 11.sp, color = TextDarkSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftUiBg,
                        unfocusedContainerColor = SoftUiBg,
                        focusedBorderColor = SoftCardMintAccent,
                        unfocusedBorderColor = SoftUiCardBorder,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary
                    )
                )

                // Calculated P&L display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سود / زیان محاسبه شده:", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(
                        text = if (profitLoss >= 0) "+$${String.format(Locale.US, "%.2f", profitLoss)}" else "-$${String.format(Locale.US, "%.2f", -profitLoss)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (profitLoss > 0) SoftCardMintAccent else if (profitLoss < 0) SoftCardPeachAccent else SoftCardPurpleAccent
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val log = TradeLogEntity(
                        asset = signal.asset,
                        direction = signal.direction,
                        result = selectedOutcome,
                        tradeAmount = stakeAmount,
                        payoutPercent = 92,
                        profitOrLoss = profitLoss,
                        broker = brokerNameInput,
                        entryPrice = signal.strikePrice,
                        expiry = signal.expiry,
                        strategy = "سیگنال هوش مصنوعی AI",
                        notes = noteInput,
                        timestamp = System.currentTimeMillis()
                    )
                    onConfirmLog(log)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SoftCardMintAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_quick_journal_log")
            ) {
                Text("ثبت در ژورنال", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SoftUiBg),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("انصراف", color = TextDarkSecondary)
            }
        }
    )
}
