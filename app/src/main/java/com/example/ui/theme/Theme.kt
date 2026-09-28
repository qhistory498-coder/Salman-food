package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SalmanFoodColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = TextPrimary,
    primaryContainer = FlameOrangeDark,
    onPrimaryContainer = GoldenYellowLight,
    secondary = GoldenYellow,
    onSecondary = CharcoalDark,
    secondaryContainer = CharcoalSurfaceVariant,
    onSecondaryContainer = GoldenYellow,
    tertiary = FlameOrangeLight,
    onTertiary = CharcoalDark,
    background = CharcoalDark,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalCardElevated,
    outlineVariant = CharcoalSurfaceVariant,
    error = NonVegRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep the custom Salman Food street food branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SalmanFoodColorScheme,
        typography = Typography,
        content = content
    )
}
