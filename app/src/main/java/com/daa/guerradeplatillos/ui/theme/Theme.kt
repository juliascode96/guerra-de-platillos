package com.daa.guerradeplatillos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/** Paletas de color disponibles (accesibilidad para daltonismo). */
enum class ColorPalette(val key: String) {
    NORMAL("normal"),
    PROTANOPIA("protanopia"),
    TRITANOPIA("tritanopia");

    companion object {
        fun fromKey(key: String?): ColorPalette = entries.firstOrNull { it.key == key } ?: NORMAL
    }
}

val LocalAppColors = staticCompositionLocalOf { NormalColors }

/** Acceso rápido a los colores propios de la app: `AppTheme.colors.success`, etc. */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current
}

fun ColorPalette.toAppColors(): AppColors = when (this) {
    ColorPalette.NORMAL -> NormalColors
    ColorPalette.PROTANOPIA -> ProtanopiaColors
    ColorPalette.TRITANOPIA -> TritanopiaColors
}

/**
 * Tema de la app. Es siempre oscuro (igual que la app original) y cambia
 * según la paleta de accesibilidad elegida en Configuración.
 */
@Composable
fun GuerraDePlatillosTheme(
    palette: ColorPalette = ColorPalette.NORMAL,
    content: @Composable () -> Unit
) {
    val appColors = palette.toAppColors()

    val colorScheme = darkColorScheme(
        primary = appColors.success,
        onPrimary = appColors.buttonPrimaryText,
        secondary = appColors.buttonSecondary,
        onSecondary = appColors.buttonSecondaryText,
        tertiary = appColors.warning,
        error = appColors.error,
        onError = appColors.textPrimary,
        background = appColors.background,
        onBackground = appColors.textPrimary,
        surface = appColors.background,
        onSurface = appColors.textPrimary,
        surfaceVariant = appColors.cardBg,
        onSurfaceVariant = appColors.textSecondary,
        outline = appColors.cardBorder,
    )

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
