package com.mindshift.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Esquema de color fijo (azul + blanco), igual que la web. No usa el color dinámico del sistema. */
private val MindShiftColorScheme = lightColorScheme(
    primary = Blue700,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Blue600,
    onSecondary = White,
    secondaryContainer = Blue100,
    onSecondaryContainer = Blue900,
    tertiary = Cyan600,
    onTertiary = White,
    tertiaryContainer = Cyan100,
    onTertiaryContainer = Cyan800,
    background = White,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    surfaceContainerLowest = White,
    surfaceContainerLow = Slate50,
    surfaceContainer = Slate100,
    surfaceContainerHigh = Slate200,
    surfaceContainerHighest = Slate200,
    outline = Slate300,
    outlineVariant = Slate200,
    error = Red600,
    onError = White,
    errorContainer = RedContainer,
    onErrorContainer = OnRedContainer,
    inversePrimary = Blue200
)

@Composable
fun MindShiftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Siempre la paleta azul/blanca, sin importar el modo del sistema ni el wallpaper.
    MaterialTheme(
        colorScheme = MindShiftColorScheme,
        typography = Typography,
        content = content
    )
}
