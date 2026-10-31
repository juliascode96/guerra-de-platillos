package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
 * Pantalla de Fin de Juego (placeholder donde se verán los resultados de la partida).
 */
@Composable
fun GameOverScreen(
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: volver al menú + título + ruedita de opciones
            Box(modifier = Modifier.fillMaxWidth()) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBackToMenu,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = stringResource(R.string.game_over_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.game_over_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            PrimaryButton(
                text = stringResource(R.string.game_over_play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.game_over_back_menu),
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameOverScreenPreview() {
    GuerraDePlatillosTheme {
        GameOverScreen(onPlayAgain = {}, onBackToMenu = {}, onSettingsClick = {})
    }
}
