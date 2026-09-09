package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

/**
 * FontAwesome-style Category Pill and Navigation Icon Badges
 * Aligned strictly with Soft UI brand identity, pastel dual-tone containers, and high contrast.
 */
@Composable
fun FontAwesomeCategoryPill(
    categoryCode: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, activeColor, gradient) = when (categoryCode) {
        "OTC" -> Triple(Icons.Default.Bolt, SoftCardCyanAccent, SoftCardCyanGradient)
        "FOREX" -> Triple(Icons.Default.Public, SoftCardMintAccent, SoftCardMintGradient)
        "CRYPTO" -> Triple(Icons.Default.CurrencyBitcoin, SoftCardPeachAccent, SoftCardPeachGradient)
        "COMMODITIES" -> Triple(Icons.Default.Diamond, SoftCardPurpleAccent, SoftCardPurpleGradient)
        else -> Triple(Icons.Default.Layers, SoftCardPurpleAccent, SoftCardPurpleGradient)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) gradient[0] else SoftUiBg)
            .border(
                1.dp,
                if (isSelected) activeColor else SoftUiCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) activeColor else TextDarkSecondary,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                color = if (isSelected) activeColor else TextDarkSecondary
            )
        }
    }
}

@Composable
fun FontAwesomeStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (icon, color, bgGradient, text) = when (status) {
        "ACTIVE" -> Quadruple(Icons.Default.PlayArrow, SoftCardMintAccent, SoftCardMintGradient, "در حال معامله")
        "WON" -> Quadruple(Icons.Default.CheckCircle, SoftCardMintAccent, SoftCardMintGradient, "موفق 🟢")
        "NO_TRADE" -> Quadruple(Icons.Default.Shield, SoftCardPeachAccent, SoftCardPeachGradient, "No Trade 🛡️")
        "FAVORITES" -> Quadruple(Icons.Default.Star, SoftCardPeachAccent, SoftCardPeachGradient, "نشان‌شده ⭐")
        else -> Quadruple(Icons.Default.AutoGraph, SoftCardCyanAccent, SoftCardCyanGradient, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgGradient[0])
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                ),
                color = color
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
