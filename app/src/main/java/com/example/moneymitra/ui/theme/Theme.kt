package com.example.moneymitra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LightGradientEnd,          // Blue
    secondary = LightLinkBlue,
    background = Color.White,
    // 🔥 Light grey screen
    surface = Color.White,               // White cards
    onPrimary = Color.White,
    onBackground = Color(0xFF0B1A3A),    // Dark text
    onSurface = Color(0xFF1C1C1C),
    outline=LightGradientEnd
)


private val DarkColorScheme = darkColorScheme(
    primary = DarkGradientEnd,
    secondary = DarkLinkBlue,
    background = DarkGradientStart,
    surface = DarkCardBackground,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    outline=Color.White
)

@Composable
fun MoneyMitraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
