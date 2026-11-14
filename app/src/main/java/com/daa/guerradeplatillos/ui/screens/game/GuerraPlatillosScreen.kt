package com.daa.guerradeplatillos.ui.screens.game

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
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
import kotlin.math.roundToInt

@Composable
fun GuerraPlatillosScreen(
    onBack: () -> Unit,
    onFinishGame: (playerScore: Int, rivalScore: Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val colors = AppTheme.colors
    var detailCard by remember { mutableStateOf<CardModel?>(null) }

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
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = viewModel.canClearBoardForNextRound,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    viewModel.clearBoardForNextRound()
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Cabecera: volver + contador de ronda + ajustes (sin título para evitar cortes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ronda ${viewModel.roundsPlayed}/${viewModel.totalRounds}",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                )
            }

            // 2. Tablero de Duelo VS (más amplio) + resultado de la última ronda
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.success.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = "VOS:",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${viewModel.playerScore} pts",
                                    color = colors.textSecondary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "RIVAL:",
                                    color = colors.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${viewModel.rivalScore} pts",
                                    color = colors.error,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "VS", color = colors.success, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MiniCard(
                                card = viewModel.playerPlayedCard,
                                width = 136.dp,
                                height = 204.dp,
                                isFaceDown = viewModel.isWaitingReveal,
                                onClick = {
                                    if (!viewModel.isWaitingReveal) {
                                        viewModel.playerPlayedCard?.let { detailCard = it }
                                    }
                                }
                            )

                            MiniCard(
                                card = viewModel.rivalPlayedCard,
                                width = 136.dp,
                                height = 204.dp,
                                isFaceDown = viewModel.isWaitingReveal,
                                onClick = {
                                    if (!viewModel.isWaitingReveal) {
                                        viewModel.rivalPlayedCard?.let { detailCard = it }
                                    }
                                }
                            )
                        }
                    }
                }

                if (viewModel.isWaitingReveal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RevealPromptBanner(onFallbackTap = { viewModel.revealCurrentRound() })
                } else {
                    viewModel.lastRoundResult?.let { result ->
                        Spacer(modifier = Modifier.height(8.dp))
                        RoundResultBanner(
                            result = result,
                            canContinue = viewModel.canClearBoardForNextRound,
                            onTapToClear = { viewModel.clearBoardForNextRound() }
                        )
                    }
                }
            }

            // 3. Mano del Jugador (sin cartel duplicado cuando espera giroscopio ni al terminar)
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!viewModel.isWaitingReveal && !viewModel.isGameFinished) {
                    Text(
                        text = "● Arrastrá una carta hacia arriba para jugar · Tocá para ver detalle",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (viewModel.isGameFinished) {
                    PrimaryButton(
                        text = "FINALIZAR PARTIDA  →",
                        onClick = { onFinishGame(viewModel.playerScore, viewModel.rivalScore) },
                        shape = RoundedCornerShape(16.dp),
                        minHeight = 52.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        viewModel.playerHand.forEach { card ->
                            DraggableHandCard(
                                card = card,
                                dragEnabled = !viewModel.isWaitingReveal && !viewModel.canClearBoardForNextRound,
                                onTap = { detailCard = card },
                                onPlay = { viewModel.playCard(card) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal de detalle al tocar cualquier carta
    detailCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { detailCard = null }
        )
    }
}

@Composable
private fun DraggableHandCard(
    card: CardModel,
    dragEnabled: Boolean,
    onTap: () -> Unit,
    onPlay: () -> Unit,
) {
    val dragThresholdPx = with(LocalDensity.current) { -72.dp.toPx() }
    var rawOffsetX by remember(card.id) { mutableFloatStateOf(0f) }
    var rawOffsetY by remember(card.id) { mutableFloatStateOf(0f) }
    var isDragging by remember(card.id) { mutableStateOf(false) }

    val animOffsetX by animateFloatAsState(targetValue = rawOffsetX, label = "cardDragX")
    val animOffsetY by animateFloatAsState(targetValue = rawOffsetY, label = "cardDragY")
    val readyToPlay = dragEnabled && rawOffsetY <= dragThresholdPx

    Box(
        modifier = Modifier
            .zIndex(if (isDragging) 10f else 0f)
            .offset {
                IntOffset(
                    (if (isDragging) rawOffsetX else animOffsetX).roundToInt(),
                    (if (isDragging) rawOffsetY else animOffsetY).roundToInt(),
                )
            }
            .then(
                if (dragEnabled) {
                    Modifier.pointerInput(card.id) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                if (rawOffsetY <= dragThresholdPx) {
                                    onPlay()
                                } else {
                                    rawOffsetX = 0f
                                    rawOffsetY = 0f
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                rawOffsetX = 0f
                                rawOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                rawOffsetX += dragAmount.x
                                rawOffsetY += dragAmount.y
                            }
                        )
                    }
                } else {
                    Modifier
                }
            )
    ) {
        MiniCard(
            card = card,
            width = 108.dp,
            height = 172.dp,
            isHighlighted = readyToPlay,
            onClick = onTap
        )
    }
}

@Composable
private fun RevealPromptBanner(onFallbackTap: () -> Unit) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBg)
            .border(1.dp, colors.warning, RoundedCornerShape(10.dp))
            .clickable { onFallbackTap() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¡Girá el celular o tocá acá para revelar las cartas!",
            color = colors.warning,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RoundResultBanner(
    result: RoundResult,
    canContinue: Boolean,
    onTapToClear: () -> Unit,
) {
    val colors = AppTheme.colors
    val (baseMessage, accentColor) = when (result.winner) {
        RoundWinner.PLAYER ->
            "Ronda ${result.roundNumber}: ¡Ganaste! +${result.playerPointsGained} pts" to colors.success
        RoundWinner.RIVAL ->
            "Ronda ${result.roundNumber}: Ganó el rival (+${result.rivalPointsGained} pts)" to colors.error
        RoundWinner.TIE ->
            "Ronda ${result.roundNumber}: Empate (+${result.playerPointsGained} vos / +${result.rivalPointsGained} rival)" to colors.warning
    }
    val message = if (canContinue) {
        "$baseMessage · Tocá la pantalla para seguir"
    } else {
        baseMessage
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBg)
            .border(1.dp, accentColor, RoundedCornerShape(10.dp))
            .then(if (canContinue) Modifier.clickable { onTapToClear() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 7.dp),
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
    width: Dp = 105.dp,
    height: Dp = 160.dp,
    isHighlighted: Boolean = false,
    isFaceDown: Boolean = false,
    onClick: () -> Unit = {}
) {
    val colors = AppTheme.colors
    val paperBg = colors.textPrimary
    val paperText = colors.background

    if (card == null) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.cardUnselectedBg)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Vacío", color = colors.textSecondary, fontSize = 11.sp)
        }
        return
    }

    if (isFaceDown) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, colors.warning, RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.img_card_cover),
                contentDescription = "Carta boca abajo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = paperBg),
        modifier = Modifier
            .width(width)
            .height(height)
            .border(
                width = if (isHighlighted) 3.dp else 1.dp,
                color = if (isHighlighted) colors.success else colors.textSecondary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Espacio grande para la imagen del platillo (_mini)
            val miniRes = cardDrawableRes(card.id, mini = true)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.textSecondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (miniRes != null) {
                    Image(
                        painter = painterResource(miniRes),
                        contentDescription = card.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Plato",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = paperText.copy(alpha = 0.7f)
                    )
                }
            }

            // Puntajes abreviados: E, S y V
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(paperText.copy(alpha = 0.08f))
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AbbrevStat(letter = "E", value = card.energyPoints, textColor = paperText)
                AbbrevStat(letter = "S", value = card.satietyPoints, textColor = paperText)
                AbbrevStat(letter = "V", value = card.vitalityPoints, textColor = paperText)
            }
        }
    }
}

@Composable
private fun AbbrevStat(
    letter: String,
    value: Int,
    textColor: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = letter,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = "$value",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
    }
}

@Composable
fun CardDetailDialog(
    card: CardModel,
    onDismiss: () -> Unit,
) {
    val colors = AppTheme.colors
    val fullRes = cardDrawableRes(card.id, mini = false)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (fullRes != null) {
                Image(
                    painter = painterResource(fullRes),
                    contentDescription = card.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(colors.cardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ Imagen: ${card.id} ]",
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            CircleIconButton(
                icon = R.drawable.ic_close,
                contentDescription = stringResource(R.string.btn_close_description),
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}

@DrawableRes
private fun cardDrawableRes(cardId: String, mini: Boolean): Int? = when (cardId) {
    "alfajor" -> if (mini) R.drawable.img_alfajor_mini else R.drawable.img_alfajor
    "avena" -> if (mini) R.drawable.img_avena_mini else R.drawable.img_avena
    "banana_mani" -> if (mini) R.drawable.img_banana_mani_mini else R.drawable.img_banana_mani
    "calabaza" -> if (mini) R.drawable.img_calabaza_mini else R.drawable.img_calabaza
    "churros" -> if (mini) R.drawable.img_churros_mini else R.drawable.img_churros
    "empanada_frita" -> if (mini) R.drawable.img_empanada_frita_mini else R.drawable.img_empanada_frita
    "ensalada_garbanzo" -> if (mini) R.drawable.img_ensalada_garbanzo_mini else R.drawable.img_ensalada_garbanzo
    "frutillas_nueces" -> if (mini) R.drawable.img_frutillas_nueces_mini else R.drawable.img_frutillas_nueces
    "galletitas" -> if (mini) R.drawable.img_galletitas_mini else R.drawable.img_galletitas
    "hamburguesa" -> if (mini) R.drawable.img_hamburguesa_mini else R.drawable.img_hamburguesa
    "licuado_verde" -> if (mini) R.drawable.img_licuado_verde_mini else R.drawable.img_licuado_verde
    "medialunas" -> if (mini) R.drawable.img_medialunas_mini else R.drawable.img_medialunas
    "merluza_con_pure" -> if (mini) R.drawable.img_merluza_con_pure_mini else R.drawable.img_merluza_con_pure
    "muffin_avena_banana" -> if (mini) R.drawable.muffin_avena_banana_mini else R.drawable.img_muffin_avena_banana
    "nuggets" -> if (mini) R.drawable.img_nuggets_mini else R.drawable.img_nuggets
    "pancho" -> if (mini) R.drawable.img_pancho_mini else R.drawable.img_pancho
    "ravioles" -> if (mini) R.drawable.img_ravioles_mini else R.drawable.img_ravioles
    "salmon" -> if (mini) R.drawable.img_salmon_mini else R.drawable.img_salmon
    "sandwich_milanesa" -> if (mini) R.drawable.img_sandwich_milanesa_mini else R.drawable.img_sandwich_milanesa
    "tortilla_zapallito" -> if (mini) R.drawable.img_tortilla_zapallito_mini else R.drawable.img_tortilla_zapallito
    "tostada_palta_huevo" -> if (mini) R.drawable.img_tostada_palta_huevo_mini else R.drawable.img_tostada_palta_huevo
    "wok_lomo" -> if (mini) R.drawable.img_wok_lomo_mini else R.drawable.img_wok_lomo
    "yogurt_griego" -> if (mini) R.drawable.img_yogurt_griego_mini else R.drawable.img_yogurt_griego
    else -> null
}
