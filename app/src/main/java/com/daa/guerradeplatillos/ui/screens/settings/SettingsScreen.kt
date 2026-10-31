package com.daa.guerradeplatillos.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.AppCard
import com.daa.guerradeplatillos.ui.components.AppDivider
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SectionHeader
import com.daa.guerradeplatillos.ui.components.SettingSwitchRow
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.ColorPalette
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.util.SoundManager
import com.daa.guerradeplatillos.viewmodel.SettingsViewModel

/** Pantalla de Configuración (equivalente a menuSettings de Código Verde). */
@Composable
fun SettingsScreen(
    onClose: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val palette by viewModel.palette.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()

    SettingsContent(
        palette = palette,
        soundEnabled = soundEnabled,
        onClose = onClose,
        onProfileClick = onProfileClick,
        onSoundChange = viewModel::setSoundEnabled,
        onProtanopiaChange = viewModel::setProtanopia,
        onTritanopiaChange = viewModel::setTritanopia,
    )
}

@Composable
private fun SettingsContent(
    palette: ColorPalette,
    soundEnabled: Boolean,
    onClose: () -> Unit,
    onProfileClick: () -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onProtanopiaChange: (Boolean) -> Unit,
    onTritanopiaChange: (Boolean) -> Unit,
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Cerrar (X) + título
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleIconButton(
                    icon = R.drawable.ic_close,
                    contentDescription = stringResource(R.string.btn_close_description),
                    onClick = onClose,
                    playSound = false,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = colors.textPrimary,
                )
            }

            // Avatar / Perfil del usuario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_person,
                    contentDescription = stringResource(R.string.settings_profile_description),
                    onClick = onProfileClick,
                    size = 80.dp,
                    tint = colors.success,
                )
                Text(
                    text = stringResource(R.string.settings_my_profile),
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clickable {
                            SoundManager.playBubbly()
                            onProfileClick()
                        }
                )
            }

            // SECCIÓN 1: SONIDO
            SectionHeader(
                text = stringResource(R.string.settings_header_sound),
                modifier = Modifier.padding(top = 24.dp)
            )
            AppCard(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)) {
                SettingSwitchRow(
                    label = stringResource(R.string.settings_sound_effects),
                    checked = soundEnabled,
                    onCheckedChange = onSoundChange,
                )
            }

            // SECCIÓN 2: PALETA DE COLORES (ACCESIBILIDAD)
            SectionHeader(
                text = stringResource(R.string.settings_header_colors),
                modifier = Modifier.padding(top = 28.dp)
            )
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingSwitchRow(
                        label = stringResource(R.string.settings_protanopia),
                        checked = palette == ColorPalette.PROTANOPIA,
                        onCheckedChange = onProtanopiaChange,
                    )
                    AppDivider()
                    SettingSwitchRow(
                        label = stringResource(R.string.settings_tritanopia),
                        checked = palette == ColorPalette.TRITANOPIA,
                        onCheckedChange = onTritanopiaChange,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsPreview() {
    GuerraDePlatillosTheme {
        SettingsContent(
            palette = ColorPalette.NORMAL,
            soundEnabled = true,
            onClose = {}, onProfileClick = {},
            onSoundChange = {}, onProtanopiaChange = {}, onTritanopiaChange = {},
        )
    }
}
