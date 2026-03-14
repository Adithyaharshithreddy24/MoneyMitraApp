package com.example.moneymitra.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
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
    val colors = MaterialTheme.colorScheme

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFF11123C),
        contentColor = colors.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.SupportAgent,
            contentDescription = "Assistant"
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
    val alpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomEnd
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(end = 18.dp, bottom = 104.dp)
        ) {

            Column(
                modifier = Modifier.padding(vertical = 6.dp)
            ) {

                AssistantMenuItemRow(
                    label = "Chit Funds",
                    icon = Icons.Default.AccountBalance,
                    onClick = onChitFunds
                )

                AssistantMenuItemRow(
                    label = "Goals",
                    icon = Icons.Default.Flag,
                    onClick = onGoals
                )

                AssistantMenuItemRow(
                    label = "Loans",
                    icon = Icons.Default.AttachMoney,
                    onClick = onLoans
                )
            }
        }
    }
}

@Composable
fun AssistantMenuItemRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(.45f)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF11123C)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}