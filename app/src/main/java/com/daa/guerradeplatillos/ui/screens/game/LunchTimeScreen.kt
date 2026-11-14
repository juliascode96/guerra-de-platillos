package com.daa.guerradeplatillos.ui.screens.game

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.Alimento
import com.daa.guerradeplatillos.model.ReglasJuego
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.viewmodel.LunchTimeEvent
import com.daa.guerradeplatillos.viewmodel.LunchTimeViewModel

/** Resuelve la imagen (res/drawable) de un alimento; usa el logo si no existe. */
private fun imagenRes(context: Context, alimento: Alimento): Int {
    if (alimento.imagen.isNotEmpty()) {
        val id = context.resources.getIdentifier(alimento.imagen, "drawable", context.packageName)
        if (id != 0) return id
    }
    return R.drawable.logo_app
}

private fun nombreCorto(alimento: Alimento): String =
    alimento.nombreCorto.ifEmpty { alimento.opcion }

/** Pantalla de juego de Lunch Time: el jugador arma su bandeja antes de que se acabe el tiempo. */
@Composable
fun LunchTimeScreen(
    viewModel: LunchTimeViewModel,
    onFinish: () -> Unit,
    onExit: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val comida by viewModel.comidaJuego.collectAsStateWithLifecycle()
    val alimentos by viewModel.alimentos.collectAsStateWithLifecycle()
    val bandeja by viewModel.bandeja.collectAsStateWithLifecycle()
    val tiempoMs by viewModel.tiempoRestanteMs.collectAsStateWithLifecycle()

    // El timer se pausa cuando la app pasa a segundo plano y se reanuda al volver
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.reanudarTimer()
                Lifecycle.Event.ON_PAUSE -> viewModel.pausarTimer()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.pausarTimer()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LunchTimeEvent.ShowToast ->
                    Toast.makeText(context, evento.mensaje, Toast.LENGTH_SHORT).show()
                LunchTimeEvent.IrAlResultado -> onFinish()
                LunchTimeEvent.IrAlJuego -> Unit
            }
        }
    }

    val segundos = (tiempoMs / 1000).toInt()
    val timerText = if (tiempoMs <= 0) "Se acabó el tiempo" else "⏱ ${segundos}s"
    val timerColor = if (segundos <= 10 && tiempoMs > 0) colors.error else colors.textPrimary

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera: salir (X) + comida + horario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_close,
                    contentDescription = stringResource(R.string.btn_close_description),
                    onClick = {
                        viewModel.cancelarPartida()
                        onExit()
                    },
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBorder)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "● ${comida.titulo.uppercase()}",
                        color = colors.success,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBorder)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🕒 ${comida.horario}",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                }

                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instrucción + temporizador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.cardBg)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Elegí ${ReglasJuego.MAX_ALIMENTOS} alimentos para tu ${comida.titulo.lowercase()}",
                    color = colors.success,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = timerText,
                    color = timerColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Grilla de alimentos (2 columnas)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alimentos, key = { it.opcion }) { food ->
                    val isSelected = bandeja.contains(food)
                    val shape = RoundedCornerShape(16.dp)
                    val imagen = remember(food.imagen) { imagenRes(context, food) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(shape)
                            .background(colors.cardBg)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) colors.warning else colors.cardBorder,
                                shape = shape
                            )
                            .clickable { viewModel.alternarAlimento(food) },
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(colors.cardUnselectedBg)
                        ) {
                            Image(
                                painter = painterResource(id = imagen),
                                contentDescription = nombreCorto(food),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.warning),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        color = colors.buttonPrimaryText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nombreCorto(food),
                                color = colors.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Progreso de la bandeja y botón de confirmación
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TU BANDEJA",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bandeja ${bandeja.size}/${ReglasJuego.MAX_ALIMENTOS}",
                        color = colors.success,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { bandeja.size / ReglasJuego.MAX_ALIMENTOS.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors.success,
                    trackColor = colors.cardBg,
                )

                Spacer(modifier = Modifier.height(12.dp))

                PrimaryButton(
                    text = "CONFIRMAR MENÚ",
                    onClick = viewModel::confirmarBandeja,
                    shape = RoundedCornerShape(16.dp),
                    minHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
