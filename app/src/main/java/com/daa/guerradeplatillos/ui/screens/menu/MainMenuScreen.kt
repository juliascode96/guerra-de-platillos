package com.daa.guerradeplatillos.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SecondaryButton
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

/**
 * Pantalla de Menú Principal.
 * Desde acá el usuario puede ir a Jugar, ver su Perfil o consultar el Ranking.
 */
@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRankingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: volver + título + ruedita de opciones
            Box(modifier = Modifier.fillMaxWidth()) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(R.string.main_menu_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 18.sp,
                    color = colors.textPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Contenido central con los 3 enlaces principales
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textHighlight,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.main_menu_subtitle),
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(36.dp))

                PrimaryButton(
                    text = stringResource(R.string.main_menu_play),
                    onClick = onPlayClick,
                    playSound = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                SecondaryButton(
                    text = stringResource(R.string.main_menu_profile),
                    onClick = onProfileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )

                Spacer(Modifier.height(16.dp))

                SecondaryButton(
                    text = stringResource(R.string.main_menu_ranking),
                    onClick = onRankingClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainMenuScreenPreview() {
    GuerraDePlatillosTheme {
        MainMenuScreen(
            onPlayClick = {},
            onProfileClick = {},
            onRankingClick = {},
            onSettingsClick = {},
            onBack = {},
        )
    }
}
