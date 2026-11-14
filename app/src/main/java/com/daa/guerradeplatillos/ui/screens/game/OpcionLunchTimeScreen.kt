package com.daa.guerradeplatillos.ui.screens.game

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.Comida
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.viewmodel.LunchTimeEvent
import com.daa.guerradeplatillos.viewmodel.LunchTimeViewModel

/** Selección del modo de juego de Lunch Time: automático (según la hora) o "Yo elijo". */
@Composable
fun OpcionLunchTimeScreen(
    viewModel: LunchTimeViewModel,
    onStartGame: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val modoAutomatico by viewModel.modoAutomatico.collectAsStateWithLifecycle()
    val comidaElegida by viewModel.comidaElegida.collectAsStateWithLifecycle()
    val comidaActual by viewModel.comidaHorarioActual.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actualizarHorarioActual()
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LunchTimeEvent.ShowToast ->
                    Toast.makeText(context, evento.mensaje, Toast.LENGTH_SHORT).show()
                LunchTimeEvent.IrAlJuego -> onStartGame()
                LunchTimeEvent.IrAlResultado -> Unit
            }
        }
    }

    OpcionLunchTimeContent(
        modoAutomatico = modoAutomatico,
        comidaElegida = comidaElegida,
        comidaActual = comidaActual,
        onAutomaticoClick = viewModel::seleccionarModoAutomatico,
        onManualClick = viewModel::seleccionarModoManual,
        onComidaClick = viewModel::seleccionarComida,
        onConfirmClick = viewModel::confirmarSeleccionMenu,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun OpcionLunchTimeContent(
    modoAutomatico: Boolean,
    comidaElegida: Comida?,
    comidaActual: Comida,
    onAutomaticoClick: () -> Unit,
    onManualClick: () -> Unit,
    onComidaClick: (Comida) -> Unit,
    onConfirmClick: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    CircleIconButton(
                        icon = R.drawable.ic_settings,
                        contentDescription = stringResource(R.string.btn_settings_description),
                        onClick = onSettingsClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "JUEGO: ARMA TU MENÚ",
                    color = colors.textHighlight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "¿Cómo querés elegir tu comida hoy?",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tarjeta 1: Automático
                OpcionCard(
                    selected = modoAutomatico,
                    onClick = onAutomaticoClick,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏰ AUTOMÁTICO",
                            color = if (modoAutomatico) colors.success else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        CheckIndicator(selected = modoAutomatico)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "La app elige según la hora del día para que juegues con lo que te toca comer ahora.",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.cardUnselectedBg)
                            .padding(10.dp)
                    ) {
                        Column {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "HORARIOS DEL MENÚ",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ACTUAL: ${comidaActual.titulo.uppercase()}",
                                    color = colors.warning,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Comida.entries.joinToString(" • ") { "${it.titulo} ${it.horario}" },
                                color = colors.textSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta 2: Yo Elijo
                OpcionCard(
                    selected = !modoAutomatico,
                    onClick = onManualClick,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🍽️ YO ELIJO",
                            color = if (!modoAutomatico) colors.success else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        CheckIndicator(selected = !modoAutomatico)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Elegí vos mismo qué comida armar y descubrí opciones saludables.",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Comida.entries.forEach { comida ->
                            val seleccionada = !modoAutomatico && comida == comidaElegida
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (seleccionada) colors.success else colors.cardUnselectedBg)
                                    .clickable { onComidaClick(comida) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = comida.titulo.uppercase(),
                                    color = if (seleccionada) colors.buttonPrimaryText else colors.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta informativa inferior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sabías que comer variado ayuda a tener más energía para jugar, estudiar y hacer deportes? ¡Sumá puntos verdes!",
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Botón inferior de confirmación
            PrimaryButton(
                text = "CONFIRMAR Y EMPEZAR",
                onClick = onConfirmClick,
                shape = RoundedCornerShape(16.dp),
                minHeight = 54.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OpcionCard(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.cardBg)
            .border(1.dp, if (selected) colors.success else colors.cardBorder, shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun CheckIndicator(selected: Boolean) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(12.dp)
    if (selected) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(shape)
                .background(colors.success),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = colors.buttonPrimaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(shape)
                .border(1.dp, colors.textSecondary, shape)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OpcionLunchTimePreview() {
    GuerraDePlatillosTheme {
        OpcionLunchTimeContent(
            modoAutomatico = true,
            comidaElegida = null,
            comidaActual = Comida.ALMUERZO,
            onAutomaticoClick = {},
            onManualClick = {},
            onComidaClick = {},
            onConfirmClick = {},
            onBack = {},
        )
    }
}
