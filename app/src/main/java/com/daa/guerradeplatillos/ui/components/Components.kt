package com.daa.guerradeplatillos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.Dimens
import com.daa.guerradeplatillos.util.SoundManager

// =============================================
//  CONTENEDOR DE PANTALLA
// =============================================

/** Fondo de la paleta + insets del sistema + márgenes de pantalla estándar. */
@Composable
fun ScreenContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Dimens.ScreenMarginHorizontal,
        vertical = Dimens.ScreenMarginVertical
    ),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(contentPadding)
    ) {
        content()
    }
}

// =============================================
//  BOTONES
// =============================================

/**
 * Botón primario (Widget.CodigoVerde.Button.Primary).
 * Reproduce el sonido "bubbly" al presionarse.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(Dimens.ButtonCornerRadius),
    minHeight: Dp = Dimens.ButtonPrimaryMinHeight,
    playSound: Boolean = true,
) {
    val colors = AppTheme.colors
    Button(
        onClick = {
            if (playSound) SoundManager.playBubbly()
            onClick()
        },
        modifier = modifier.heightIn(min = minHeight),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.buttonPrimary,
            contentColor = colors.buttonPrimaryText,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(
            horizontal = Dimens.ButtonPrimaryPaddingHorizontal,
            vertical = Dimens.ButtonPrimaryPaddingVertical
        ),
    ) {
        Text(text = text, style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
    }
}

/** Botón secundario compacto, casi cuadrado (Widget.CodigoVerde.Button.Secondary). */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    playSound: Boolean = true,
) {
    val colors = AppTheme.colors
    Button(
        onClick = {
            if (playSound) SoundManager.playBubbly()
            onClick()
        },
        modifier = modifier.sizeIn(
            minWidth = Dimens.ButtonSecondarySize,
            minHeight = Dimens.ButtonSecondarySize,
        ),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.ButtonSecondaryCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.buttonSecondary,
            contentColor = colors.buttonSecondaryText,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(Dimens.SpacingSmall),
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}

/** Botón de ícono secundario (Widget.CodigoVerde.Button.Icon). */
@Composable
fun SecondaryIconButton(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    playSound: Boolean = true,
) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .size(Dimens.ButtonSecondarySize)
            .clip(RoundedCornerShape(Dimens.ButtonSecondaryCornerRadius))
            .background(colors.buttonSecondary)
            .clickable {
                if (playSound) SoundManager.playBubbly()
                onClick()
            }
            .padding(Dimens.SpacingSmall),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = colors.buttonSecondaryText
        )
    }
}

/** Botón circular con ícono blanco (bg_circle_button): settings, volver, cerrar. */
@Composable
fun CircleIconButton(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.CircleButtonSize,
    iconPadding: Dp = Dimens.CircleButtonIconPadding,
    tint: Color = AppTheme.colors.iconTint,
    playSound: Boolean = true,
) {
    val colors = AppTheme.colors
    IconButton(
        onClick = {
            if (playSound) SoundManager.playBubbly()
            onClick()
        },
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.cardBg)
            .border(1.dp, colors.cardBorder, CircleShape)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .fillMaxSize()
                .padding(iconPadding)
        )
    }
}

/** Badge / píldora con borde (bg_badge). Si recibe [onClick] es clickeable. */
@Composable
fun BadgePill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.translucentTeal)
            .border(1.dp, colors.textSecondary, shape)
            .then(
                if (onClick != null) Modifier.clickable {
                    SoundManager.playBubbly()
                    onClick()
                } else Modifier
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Etiqueta pequeña con fondo de color (shape_badge + backgroundTint). */
@Composable
fun TagChip(
    text: String,
    background: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = textColor,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

// =============================================
//  TARJETAS
// =============================================

/** Tarjeta redondeada (bg_card_unselected / bg_card_selected). */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.SpacingMedium),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(Dimens.CardCornerRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (selected) colors.cardSelectedBg else colors.cardUnselectedBg)
            .border(
                if (selected) BorderStroke(2.dp, colors.success)
                else BorderStroke(1.dp, colors.translucentTeal),
                shape
            )
            .padding(contentPadding),
        content = content
    )
}

/** Tarjeta con borde sutil (shape_card_border). */
@Composable
fun BorderCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.SpacingMedium),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(Dimens.BorderCardCornerRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.cardBg)
            .border(1.dp, colors.cardBorder, shape)
            .padding(contentPadding),
        content = content
    )
}

// =============================================
//  TEXTOS / FILAS
// =============================================

/** Encabezado de sección en mayúsculas (ej: "SONIDO"). */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = AppTheme.colors.textSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/** Fila con texto y switch, usada en Configuración. */
@Composable
fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.buttonPrimaryText,
                checkedTrackColor = colors.success,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.cardBg,
                uncheckedBorderColor = colors.textSecondary,
            )
        )
    }
}

/** Línea divisoria tenue. */
@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppTheme.colors.textSecondary.copy(alpha = 0.3f))
    )
}

/** Campo de texto con estilo de tarjeta (EditText con bg_card_unselected). */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(Dimens.CardCornerRadius)
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.cardUnselectedBg,
            unfocusedContainerColor = colors.cardUnselectedBg,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedPlaceholderColor = colors.textSecondary,
            unfocusedPlaceholderColor = colors.textSecondary,
            cursorColor = colors.success,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                colors.translucentTeal,
                shape
            )
    )
}

/** Opción seleccionable con estilo de tarjeta (RadioButton con bg_card_selected/unselected). */
@Composable
fun SelectableCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(Dimens.CardCornerRadius)
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(shape)
            .background(if (selected) colors.cardSelectedBg else colors.cardUnselectedBg)
            .border(
                if (selected) BorderStroke(2.dp, colors.success)
                else BorderStroke(1.dp, colors.translucentTeal),
                shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/** Puntos indicadores con los colores del semáforo (pantalla principal). */
@Composable
fun TrafficLightDots(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(colors.error, 8.dp)
        Dot(colors.warning, 8.dp)
        Dot(colors.success, 12.dp)
        Dot(colors.textSecondary, 8.dp)
        Dot(colors.textPrimary, 8.dp)
    }
}

@Composable
private fun RowScope.Dot(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}
