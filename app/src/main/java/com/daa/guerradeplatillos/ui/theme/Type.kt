package com.daa.guerradeplatillos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R

/** Tipografía global de la app: OpenDyslexic (https://opendyslexic.org/). */
val OpenDyslexic = FontFamily(
    Font(R.font.open_dyslexic_regular, FontWeight.Normal),
    Font(R.font.open_dyslexic_bold, FontWeight.Bold),
)

private val base = Typography()

private fun TextStyle.dyslexic() = copy(fontFamily = OpenDyslexic)

/**
 * Mapeo de los estilos de texto originales a Material 3:
 * - Title    (28sp bold)  -> headlineMedium
 * - Subtitle (20sp bold)  -> titleLarge
 * - Body     (16sp)       -> bodyLarge
 * - Button   (18sp)       -> labelLarge
 * El resto de los estilos de Material también usan OpenDyslexic.
 */
val Typography = Typography(
    displayLarge = base.displayLarge.dyslexic(),
    displayMedium = base.displayMedium.dyslexic(),
    displaySmall = base.displaySmall.dyslexic(),
    headlineLarge = base.headlineLarge.dyslexic(),
    headlineMedium = TextStyle(
        fontFamily = OpenDyslexic,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.TextTitle,
        lineHeight = 36.sp,
    ),
    headlineSmall = base.headlineSmall.dyslexic(),
    titleLarge = TextStyle(
        fontFamily = OpenDyslexic,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.TextSubtitle,
        lineHeight = 28.sp,
    ),
    titleMedium = base.titleMedium.dyslexic(),
    titleSmall = base.titleSmall.dyslexic(),
    bodyLarge = TextStyle(
        fontFamily = OpenDyslexic,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.TextBody,
        lineHeight = 24.sp,
    ),
    bodyMedium = base.bodyMedium.dyslexic(),
    bodySmall = base.bodySmall.dyslexic(),
    labelLarge = TextStyle(
        fontFamily = OpenDyslexic,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.TextButton,
        lineHeight = 24.sp,
    ),
    labelMedium = base.labelMedium.dyslexic(),
    labelSmall = base.labelSmall.dyslexic(),
)
