package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Screen
import com.example.ui.theme.*

@Composable
fun AnimatedBottomNavBar(
    items: List<Screen>,
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SoftUiBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = SoftUiShadowDark)
                .clip(RoundedCornerShape(26.dp))
                .background(SoftUiSurface)
                .border(1.dp, SoftUiCardBorder, RoundedCornerShape(26.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    AnimatedNavItem(
                        screen = screen,
                        isSelected = isSelected,
                        onClick = { onItemSelected(screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isCenterHome = screen.route == Screen.Home.route

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        label = "nav_item_scale"
    )

    val activeColor = if (isCenterHome) SoftCardPurpleAccent else SoftCardPurpleAccent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCenterHome) 40.dp else 32.dp)
                .clip(CircleShape)
                .background(
                    if (isCenterHome) {
                        if (isSelected) Brush.linearGradient(listOf(SoftCardPurpleAccent, SoftCardCyanAccent))
                        else Brush.linearGradient(listOf(SoftCardPurpleAccent.copy(alpha = 0.8f), SoftCardPurpleAccent))
                    } else if (isSelected) {
                        SolidColor(activeColor.copy(alpha = 0.15f))
                    } else {
                        SolidColor(Color.Transparent)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                tint = if (isCenterHome) Color.White else if (isSelected) activeColor else TextDarkMuted,
                modifier = Modifier.size(if (isCenterHome) 22.dp else 19.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = screen.title,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected || isCenterHome) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isCenterHome) SoftCardPurpleAccent else if (isSelected) activeColor else TextDarkMuted
        )
    }
}
