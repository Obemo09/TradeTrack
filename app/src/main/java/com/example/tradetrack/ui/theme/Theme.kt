package com.example.tradetrack.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TradingBlue,
    onPrimary = TradingBlack,
    secondary = TradingLightGrey,
    background = TradingBlack,
    surface = TradingDarkGrey,
    onSurface = TradingTextPrimary,
    surfaceVariant = TradingLightGrey,
    onSurfaceVariant = TradingTextSecondary,
    outline = TradingLightGrey
)

private val LightColorScheme = lightColorScheme(
    primary = TradingBlue,
    onPrimary = Color.White,
    secondary = Color(0xFFE0E0E0),
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = Color(0xFF212529),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF495057),
    outline = Color(0xFFDEE2E6)
)

@Composable
fun TradeTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
