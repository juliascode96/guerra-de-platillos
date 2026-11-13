package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.daa.guerradeplatillos.ui.components.BorderCard
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SecondaryButton
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

/**
 * Pantalla de Fin de Juego: muestra el resultado de la partida cuando recibe puntajes,
 * o el mensaje placeholder si se llega desde un minijuego sin puntaje aún.
 */
@Composable
fun GameOverScreen(
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onSettingsClick: () -> Unit,
    playerScore: Int? = null,
    rivalScore: Int? = null,
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
                if (playerScore != null && rivalScore != null) {
                    val (headline, headlineColor) = when {
                        playerScore > rivalScore -> "¡GANASTE LA PARTIDA!" to colors.success
                        playerScore < rivalScore -> "GANÓ EL RIVAL" to colors.error
                        else -> "¡EMPATE!" to colors.warning
                    }

                    BorderCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = headline,
                                color = headlineColor,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("VOS", color = colors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("$playerScore", color = colors.success, fontSize = 32.sp, fontWeight = FontWeight.Black)
                                    Text("pts", color = colors.textSecondary, fontSize = 12.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("RIVAL", color = colors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("$rivalScore", color = colors.textPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                                    Text("pts", color = colors.textSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.game_over_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
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
        GameOverScreen(
            onPlayAgain = {},
            onBackToMenu = {},
            onSettingsClick = {},
            playerScore = 85,
            rivalScore = 30,
        )
    }
}
