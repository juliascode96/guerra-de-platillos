package com.daa.guerradeplatillos.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.Avatar
import com.daa.guerradeplatillos.ui.components.BorderCard
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.TagChip
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.viewmodel.ProfileViewModel

/**
 * Pantalla de Perfil de usuario (equivalente a DetallesUsuario de Código Verde).
 * Las estadísticas, badges y días son contenido de ejemplo (placeholder).
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()
    ProfileContent(avatar, onBack, onSettingsClick, onEditClick)
}

@Composable
private fun ProfileContent(
    avatar: Avatar?,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: volver / título / ajustes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(R.string.profile_title),
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Foto y nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    // TODO: reemplazar por la imagen elegida (ver AvatarCatalog) cuando se implemente.
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(colors.goldXp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = stringResource(R.string.desc_avatar_usuario),
                            tint = colors.background,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    // Badge verificado
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colors.success)
                            .border(2.dp, colors.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = colors.buttonPrimaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = avatar?.nombre ?: stringResource(R.string.profile_no_user),
                    color = colors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = avatar?.let { stringResource(R.string.profile_user_info, it.edad, it.sexo) }
                        ?: stringResource(R.string.profile_no_user_info),
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Barra de experiencia (XP)
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = stringResource(R.string.profile_experience),
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.profile_xp_placeholder),
                        color = colors.success,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { 0.68f },
                    color = colors.greenLight,
                    trackColor = colors.cardBorder,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }

            // Badges de logros rápidos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TagChip(stringResource(R.string.profile_badge_1), colors.badgeGreenBg, colors.success, Modifier.weight(1f))
                TagChip(stringResource(R.string.profile_badge_2), colors.badgeBlueBg, colors.textPrimary, Modifier.weight(1.2f))
                TagChip(stringResource(R.string.profile_badge_3), colors.badgeOrangeBg, colors.textPrimary, Modifier.weight(1f))
            }

            // Tarjetas de estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("--", stringResource(R.string.profile_stat_1), colors.success, Modifier.weight(1f))
                StatCard("--", stringResource(R.string.profile_stat_2), colors.textPrimary, Modifier.weight(1f))
                StatCard("-", stringResource(R.string.profile_stat_3), colors.success, Modifier.weight(1f))
            }

            // Pestañas (Resumen / Registro / Logros) — estáticas por ahora
            BorderCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
            ) {
                Row {
                    TabItem(stringResource(R.string.profile_tab_summary), selected = true, Modifier.weight(1f))
                    TabItem(stringResource(R.string.profile_tab_log), selected = false, Modifier.weight(1f))
                    TabItem(stringResource(R.string.profile_tab_achievements), selected = false, Modifier.weight(1f))
                }
            }

            // Días de la semana
            Text(
                text = stringResource(R.string.profile_week_header),
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                val semana = listOf(
                    "L" to colors.error, "M" to colors.success, "M" to colors.cardBorder,
                    "J" to colors.success, "V" to colors.cardBorder, "S" to colors.success,
                    "D" to colors.cardBorder,
                )
                semana.forEach { (dia, color) -> DayBox(dia, color, Modifier.weight(1f)) }
            }

            // Bloques informativos
            InfoCard(
                emoji = "💡",
                title = stringResource(R.string.profile_fact_title),
                titleColor = colors.success,
                body = stringResource(R.string.profile_fact_body),
                modifier = Modifier.padding(top = 20.dp)
            )
            InfoCard(
                emoji = "⚠️",
                title = stringResource(R.string.profile_alert_title),
                titleColor = colors.error,
                body = stringResource(R.string.profile_alert_body),
                modifier = Modifier.padding(top = 12.dp)
            )

            // Botón Editar datos
            PrimaryButton(
                text = stringResource(R.string.btn_editar_datos),
                onClick = onEditClick,
                shape = RoundedCornerShape(28.dp),
                minHeight = 54.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    BorderCard(
        modifier = modifier.height(115.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, color = valueColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                label,
                color = AppTheme.colors.textSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun TabItem(text: String, selected: Boolean, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) colors.cardBorder else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) colors.textPrimary else colors.textSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DayBox(day: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Text(
            text = day,
            color = AppTheme.colors.textSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun InfoCard(
    emoji: String,
    title: String,
    titleColor: Color,
    body: String,
    modifier: Modifier = Modifier,
) {
    BorderCard(modifier = modifier.fillMaxWidth()) {
        Row {
            Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text(title, color = titleColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    body,
                    color = AppTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfilePreview() {
    GuerraDePlatillosTheme {
        ProfileContent(
            avatar = Avatar("Mateo", Avatar.SEXO_MASCULINO, 15),
            onBack = {}, onSettingsClick = {}, onEditClick = {}
        )
    }
}
