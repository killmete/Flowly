package com.example.flowly.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FlowlyColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = PrimaryPurpleLight,
    secondary = SafeGreen,
    onSecondary = Color.Black,
    secondaryContainer = SafeGreenDark,
    onSecondaryContainer = SafeGreenLight,
    tertiary = WarningAmber,
    onTertiary = Color.Black,
    tertiaryContainer = WarningAmberDark,
    onTertiaryContainer = WarningAmberLight,
    error = DangerRed,
    onError = Color.White,
    errorContainer = DangerRedDark,
    onErrorContainer = DangerRedLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = DarkCardElevated
)

@Composable
fun FlowlyTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = DarkBackground.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = FlowlyColorScheme,
        typography = FlowlyTypography,
        content = content
    )
}