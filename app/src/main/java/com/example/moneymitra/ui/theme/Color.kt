package com.example.moneymitra.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val Orange = Color(0xFFFF6B00)
/* -------- LIGHT THEME COLORS -------- */

val LightGradientStart = Color(0xFF000000)
val LightGradientEnd = Color(0xFF282B8C)
val LightLinkBlue = Color(0xFF2563EB)
val LightCardBackground = Color.White

/* -------- DARK THEME -------- */

val DarkGradientStart = Color(0xFF000000)
val DarkGradientEnd = Color(0xFF1A237E)
val DarkLinkBlue = Color(0xFF1278E7)
val DarkCardBackground = Color(0xFF000000)
val DarkGoogleCard = Color(0xFF1F2023)
/* -------- LOGIN GRADIENTS -------- */

val LoginGradientLight = Brush.horizontalGradient(
    listOf(
        LightGradientStart,
        LightGradientEnd
    )
)

val LoginGradientDark = Brush.horizontalGradient(
    listOf(
        DarkGradientStart,
        DarkGradientEnd
    )
)
