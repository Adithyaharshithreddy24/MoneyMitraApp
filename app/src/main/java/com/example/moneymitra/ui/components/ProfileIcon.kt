package com.example.moneymitra.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = colors.onBackground,
        contentColor = colors.background,
        shape = RoundedCornerShape(50.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile"
        )
    }
}
