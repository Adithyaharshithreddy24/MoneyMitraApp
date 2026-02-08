package com.example.moneymitra.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AssistantFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFF11123C)
    ) {
        Icon(
            imageVector = Icons.Default.SupportAgent,
            contentDescription = "Assistant" ,
            tint = Color.White
        )
    }
}

@Composable
fun AssistantRadialMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onChitFunds: () -> Unit,
    onGoals: () -> Unit,
    onLoans: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(modifier = Modifier.padding(end = 24.dp, bottom = 104.dp)) {

            AssistantMenuItem(
                label = "Chit Funds",
                icon = Icons.Default.AccountBalance,
                angle = -140f,
                visible = expanded,
                onClick = onChitFunds
            )

            AssistantMenuItem(
                label = "Goals",
                icon = Icons.Default.Flag,
                angle = -196f,
                visible = expanded,
                onClick = onGoals
            )

            AssistantMenuItem(
                label = "Loans",
                icon = Icons.Default.AttachMoney,
                angle = -75f,
                visible = expanded,
                onClick = onLoans
            )
        }
    }
}

@Composable
fun AssistantMenuItem(
    label: String,
    icon: ImageVector,
    angle: Float,
    visible: Boolean,
    radius: Dp = 90.dp,
    onClick: () -> Unit
) {
    val distance by animateDpAsState(
        targetValue = if (visible) radius else 0.dp,
        animationSpec = tween(1000),
        label = ""
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000),
        label = ""
    )

    val density = LocalDensity.current
    val angleRad = Math.toRadians(angle.toDouble())

    val offsetX = with(density) {
        (distance.toPx() * cos(angleRad).toFloat()).toDp()
    }

    val offsetY = with(density) {
        (distance.toPx() * sin(angleRad).toFloat()).toDp()
    }


    Column(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .alpha(alpha)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF11123C),
            shadowElevation = 10.dp
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(Modifier.height(1.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF11123C)
        )
    }
}
