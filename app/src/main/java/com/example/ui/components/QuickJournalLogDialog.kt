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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
        containerColor = SlateDark900,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = EmeraldGlow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ثبت سریع معامله در ژورنال",
                    color = TextPrimary,
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateDark800)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(signal.asset, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("جهت: ${signal.direction} | انقضا: ${signal.expiry}", color = TextMuted, fontSize = 11.sp)
                        }
                        Text(
                            text = "استرایک: ${signal.strikePrice}",
                            color = CyanGlow,
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
                        val activeColor = when (code) {
                            "WIN" -> EmeraldNeon
                            "LOSS" -> CrimsonRed
                            else -> AmberGold
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) activeColor.copy(alpha = 0.2f) else SlateDark800)
                                .border(
                                    0.8.dp,
                                    if (isSelected) activeColor else CardBorder,
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
                                color = if (isSelected) activeColor else TextMuted
                            )
                        }
                    }
                }

                // Stake Amount Input
                OutlinedTextField(
                    value = stakeAmountInput,
                    onValueChange = { stakeAmountInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("حجم معامله ($):", fontSize = 11.sp, color = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateDark800,
                        unfocusedContainerColor = SlateDark800,
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Calculated P&L display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سود / زیان محاسبه شده:", fontSize = 12.sp, color = TextMuted)
                    Text(
                        text = if (profitLoss >= 0) "+$${String.format(Locale.US, "%.2f", profitLoss)}" else "-$${String.format(Locale.US, "%.2f", -profitLoss)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (profitLoss > 0) EmeraldNeon else if (profitLoss < 0) CrimsonGlow else AmberGold
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
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier.testTag("confirm_quick_journal_log")
            ) {
                Text("ثبت در ژورنال", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SlateDark800)
            ) {
                Text("انصراف", color = TextMuted)
            }
        }
    )
}
