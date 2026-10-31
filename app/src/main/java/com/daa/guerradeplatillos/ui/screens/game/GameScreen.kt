package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

/**
 * Punto de entrada del juego Guerra de Platillos.
 * Plantilla vacía: acá se empieza a desarrollar el juego (sensores, lógica, etc.).
 */
@Composable
fun GameScreen(
    onBack: () -> Unit,
    onFinishGame: () -> Unit,
    onSettingsClick: () -> Unit,
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
                    text = stringResource(R.string.game_title),
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
                    text = stringResource(R.string.game_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            PrimaryButton(
                text = stringResource(R.string.game_btn_finish_demo),
                onClick = onFinishGame,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameScreenPreview() {
    GuerraDePlatillosTheme {
        GameScreen(onBack = {}, onFinishGame = {}, onSettingsClick = {})
    }
}
