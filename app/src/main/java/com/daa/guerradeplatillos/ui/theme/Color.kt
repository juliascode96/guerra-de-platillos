package com.daa.guerradeplatillos.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// =============================================
//  PALETA PRINCIPAL (Visión Normal)
// =============================================
val Background = Color(0xFF172929)
val TextPrimary = Color(0xFFE7F1F2)
val TextSecondary = Color(0xFF86BCBD)
val TextHighlight = Color(0xFFA4CE8B)

// Semáforo
val Success = Color(0xFFA4CE8B)
val Warning = Color(0xFFF7E49B)
val Error = Color(0xFFBA5A5A)

// Botones
val ButtonPrimary = Color(0xFFA4CE8B)
val ButtonPrimaryText = Color(0xFF172929)
val ButtonSecondary = Color(0xFF86BCBD)
val ButtonSecondaryText = Color(0xFF172929)

// =============================================
//  PALETA PROTANOPIA / DEUTERANOPIA
// =============================================
val ProtanBackground = Color(0xFF1F1F29)
val ProtanTextPrimary = Color(0xFFEBEBF3)
val ProtanTextSecondary = Color(0xFF9D9EBD)
val ProtanTextHighlight = Color(0xFFB7B79B)
val ProtanSuccess = Color(0xFFB7B79B)
val ProtanWarning = Color(0xFFEEEFAD)
val ProtanError = Color(0xFF90905A)

// =============================================
//  PALETA TRITANOPIA
// =============================================
val TritanBackground = Color(0xFF192929)
val TritanTextPrimary = Color(0xFFE8F2F2)
val TritanTextSecondary = Color(0xFF89BCBD)
val TritanTextHighlight = Color(0xFFA7A8AC)
val TritanSuccess = Color(0xFFA7A8AC)
val TritanWarning = Color(0xFFF6BBBF)
val TritanError = Color(0xFFB5595A)

// =============================================
//  DETALLES USUARIO / CONTENEDORES
// =============================================
val BgDark = Color(0xFF0D1F22)
val CardBg = Color(0xFF132B2F)
val CardBorder = Color(0xFF1D3E43)
val TextWhite = Color(0xFFFFFFFF)
val TextMuted = Color(0xFF8C9E9E)

// Acentos y badges
val GreenPrimary = Color(0xFF76C893)
val GreenLight = Color(0xFF99D98C)
val OrangeAccent = Color(0xFFD97706)
val RedAlert = Color(0xFFE56B6F)
val GoldXp = Color(0xFFE5A93C)

// Badges translúcidos
val BadgeGreenBg = Color(0xFF1B382B)
val BadgeBlueBg = Color(0xFF18363E)
val BadgeOrangeBg = Color(0xFF3A2C1D)

// Tarjetas (equivalentes a bg_card_unselected / bg_card_selected / bg_badge)
val CardUnselectedBg = Color(0xFF0D191C)
val CardSelectedBg = Color(0xFF0F2023)
val TranslucentTeal = Color(0x1A86BCBD)

/**
 * Colores de la app que dependen de la paleta de accesibilidad elegida.
 * Se accede desde cualquier composable con `AppTheme.colors`.
 */
@Immutable
data class AppColors(
    val background: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHighlight: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val buttonPrimary: Color,
    val buttonPrimaryText: Color,
    val buttonSecondary: Color,
    val buttonSecondaryText: Color,
    // Los siguientes no cambian entre paletas, pero se exponen acá para tener
    // un único punto de acceso a todos los colores.
    val cardBg: Color = CardBg,
    val cardBorder: Color = CardBorder,
    val cardUnselectedBg: Color = CardUnselectedBg,
    val cardSelectedBg: Color = CardSelectedBg,
    val translucentTeal: Color = TranslucentTeal,
    val iconTint: Color = TextWhite,
    val goldXp: Color = GoldXp,
    val greenLight: Color = GreenLight,
    val badgeGreenBg: Color = BadgeGreenBg,
    val badgeBlueBg: Color = BadgeBlueBg,
    val badgeOrangeBg: Color = BadgeOrangeBg,
)

val NormalColors = AppColors(
    background = Background,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textHighlight = TextHighlight,
    success = Success,
    warning = Warning,
    error = Error,
    buttonPrimary = ButtonPrimary,
    buttonPrimaryText = ButtonPrimaryText,
    buttonSecondary = ButtonSecondary,
    buttonSecondaryText = ButtonSecondaryText,
)

val ProtanopiaColors = AppColors(
    background = ProtanBackground,
    textPrimary = ProtanTextPrimary,
    textSecondary = ProtanTextSecondary,
    textHighlight = ProtanTextHighlight,
    success = ProtanSuccess,
    warning = ProtanWarning,
    error = ProtanError,
    buttonPrimary = ProtanSuccess,
    buttonPrimaryText = ProtanBackground,
    buttonSecondary = ProtanTextSecondary,
    buttonSecondaryText = ProtanBackground,
)

val TritanopiaColors = AppColors(
    background = TritanBackground,
    textPrimary = TritanTextPrimary,
    textSecondary = TritanTextSecondary,
    textHighlight = TritanTextHighlight,
    success = TritanSuccess,
    warning = TritanWarning,
    error = TritanError,
    buttonPrimary = TritanSuccess,
    buttonPrimaryText = TritanBackground,
    buttonSecondary = TritanTextSecondary,
    buttonSecondaryText = TritanBackground,
)
