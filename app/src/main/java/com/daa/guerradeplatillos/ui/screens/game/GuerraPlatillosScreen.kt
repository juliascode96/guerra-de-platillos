package com.daa.guerradeplatillos.ui.screens.game

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.viewmodel.GameUiEvent
import com.daa.guerradeplatillos.viewmodel.GameViewModel
import com.daa.guerradeplatillos.viewmodel.RoundResult
import com.daa.guerradeplatillos.viewmodel.RoundWinner

@Composable
fun GuerraPlatillosScreen(
    onBack: () -> Unit,
    onFinishGame: (playerScore: Int, rivalScore: Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val colors = AppTheme.colors

    // Sensores (shake): se activan al entrar a la pantalla y se pausan al salir
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is GameUiEvent.ShowShakeToast ->
                    Toast.makeText(context, "Agitaste el teléfono", Toast.LENGTH_SHORT).show()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startSensors()
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseSensors()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Cabecera: volver + título + ajustes, y marcador de ronda/puntaje
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "Guerra de Platillos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp,
                        color = colors.textHighlight,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    CircleIconButton(
                        icon = R.drawable.ic_settings,
                        contentDescription = stringResource(R.string.btn_settings_description),
                        onClick = onSettingsClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Marcador de ronda y puntos acumulados
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VOS: ${viewModel.playerScore} pts",
                        color = colors.success,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ronda ${viewModel.roundsPlayed}/${viewModel.totalRounds}",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RIVAL: ${viewModel.rivalScore} pts",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Tablero de Duelo VS + resultado de la última ronda
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.success.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "RIVAL", color = colors.textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            MiniCard(card = viewModel.rivalPlayedCard)
                        }

                        Text(text = "VS", color = colors.success, fontSize = 22.sp, fontWeight = FontWeight.Black)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "VOS", color = colors.success, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            MiniCard(card = viewModel.playerPlayedCard)
                        }
                    }
                }

                viewModel.lastRoundResult?.let { result ->
                    Spacer(modifier = Modifier.height(6.dp))
                    RoundResultBanner(result = result)
                }
            }

            // 3. Mano del Jugador (3 cartas iniciales, se descartan al jugar)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (viewModel.isGameFinished) {
                        "● ¡Terminaron las 3 rondas!"
                    } else {
                        "● ¡Te toca! Elegí una carta de tu mano"
                    },
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (viewModel.playerHand.isEmpty()) {
                        Text(
                            text = "Sin cartas en mano",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        viewModel.playerHand.forEach { card ->
                            MiniCard(
                                card = card,
                                isSelected = viewModel.selectedCard?.id == card.id,
                                onClick = { viewModel.selectCard(card) }
                            )
                        }
                    }
                }
            }

            // 4. Botón de Acción
            PrimaryButton(
                text = when {
                    viewModel.isGameFinished -> "VER RESULTADO FINAL  →"
                    viewModel.selectedCard != null -> "JUGAR CARTA ⇧"
                    else -> "SELECCIONÁ UNA CARTA"
                },
                enabled = viewModel.isGameFinished || viewModel.selectedCard != null,
                onClick = {
                    if (viewModel.isGameFinished) {
                        onFinishGame(viewModel.playerScore, viewModel.rivalScore)
                    } else {
                        viewModel.playTurn()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                minHeight = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoundResultBanner(result: RoundResult) {
    val colors = AppTheme.colors
    val (message, accentColor) = when (result.winner) {
        RoundWinner.PLAYER ->
            "Ronda ${result.roundNumber}: ¡Ganaste! +${result.playerPointsGained} pts (${result.playerCategoriesWon}/3)" to colors.success
        RoundWinner.RIVAL ->
            "Ronda ${result.roundNumber}: Ganó el rival (+${result.rivalPointsGained} pts)" to colors.error
        RoundWinner.TIE ->
            "Ronda ${result.roundNumber}: Empate (+${result.playerPointsGained} vos / +${result.rivalPointsGained} rival)" to colors.warning
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBg)
            .border(1.dp, accentColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MiniCard(
    card: CardModel?,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val colors = AppTheme.colors
    val paperBg = colors.textPrimary
    val paperText = colors.background

    if (card == null) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(145.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.cardUnselectedBg)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Vacío", color = colors.textSecondary, fontSize = 10.sp)
        }
        return
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = paperBg),
        modifier = Modifier
            .width(100.dp)
            .height(145.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) colors.success else colors.textSecondary,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.textSecondary.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Plato", fontSize = 8.sp, color = paperText)
            }
            Text(
                text = card.name,
                color = paperText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                StatLine(label = "Energía", value = card.energyPoints, textColor = paperText)
                StatLine(label = "Saciedad", value = card.satietyPoints, textColor = paperText)
                StatLine(label = "Vitalidad", value = card.vitalityPoints, textColor = paperText)
            }
        }
    }
}

@Composable
private fun StatLine(
    label: String,
    value: Int,
    textColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 8.sp, color = textColor.copy(alpha = 0.75f))
        Text(text = "$value", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}
