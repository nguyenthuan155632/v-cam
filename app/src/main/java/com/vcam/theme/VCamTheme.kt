package com.vcam.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalVColors = staticCompositionLocalOf { VColorsActive(accent = VColors.Coral) }

data class VColorsActive(val accent: androidx.compose.ui.graphics.Color)

private val LightScheme = lightColorScheme(
    primary = VColors.Coral,
    onPrimary = VColors.Paper,
    secondary = VColors.Beige,
    onSecondary = VColors.Ink,
    background = VColors.Paper,
    onBackground = VColors.Ink,
    surface = VColors.Paper,
    onSurface = VColors.Ink,
    surfaceVariant = VColors.PaperWarm,
    onSurfaceVariant = VColors.Ink70,
    outline = VColors.Ink12,
    outlineVariant = VColors.Divider,
)

private val DarkScheme = darkColorScheme(
    primary = VColors.Coral,
    onPrimary = VColors.Paper,
    secondary = VColors.Beige,
    onSecondary = VColors.Ink,
    background = VColors.CameraBackground,
    onBackground = VColors.White95,
    surface = VColors.CameraBackground,
    onSurface = VColors.White95,
    surfaceVariant = VColors.DarkGlass55,
    onSurfaceVariant = VColors.White65,
    outline = VColors.White18,
    outlineVariant = VColors.White08,
)

@Composable
fun VCamTheme(
    darkTheme: Boolean = false,
    accent: androidx.compose.ui.graphics.Color = VColors.Coral,
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme
    CompositionLocalProvider(LocalVColors provides VColorsActive(accent = accent)) {
        MaterialTheme(
            colorScheme = scheme,
            typography = VTypography,
            shapes = VMaterialShapes,
            content = content,
        )
    }
}
