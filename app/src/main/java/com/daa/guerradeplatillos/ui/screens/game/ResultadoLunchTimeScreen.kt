package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.model.ReglasJuego
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SecondaryButton
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.util.SoundManager
import com.daa.guerradeplatillos.viewmodel.LunchTimeViewModel
import kotlinx.coroutines.delay

private const val PASO_MS = 260L

/** Pantalla de resultados de Lunch Time: victoria/derrota, puntajes por categoría y dato curioso. */
@Composable
fun ResultadoLunchTimeScreen(
    viewModel: LunchTimeViewModel,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val colors = AppTheme.colors
    val resultado by viewModel.resultado.collectAsStateWithLifecycle()
    val dato by viewModel.datoCurioso.collectAsStateWithLifecycle()

    val res = resultado
    if (res == null) {
        // Sin resultado (por ejemplo, la app se recreó): volver al menú
        LaunchedEffect(Unit) { onBackToMenu() }
        return
    }

    val puntaje = remember(res) { viewModel.puntajeUi(res) }

    // Círculos que se llenan en orden: salud → saciedad → vitalidad
    var llenosSalud by remember(res) { mutableIntStateOf(0) }
    var llenosSaciedad by remember(res) { mutableIntStateOf(0) }
    var llenosVitalidad by remember(res) { mutableIntStateOf(0) }

    LaunchedEffect(res) {
        delay(300)
        if (puntaje.esVictoria) SoundManager.playResultPositive() else SoundManager.playResultNegative()
    }

    LaunchedEffect(res) {
        delay(200)
        for (i in 1..puntaje.salud) { delay(PASO_MS); llenosSalud = i }
        for (i in 1..puntaje.saciedad) { delay(PASO_MS); llenosSaciedad = i }
        for (i in 1..puntaje.vitalidad) { delay(PASO_MS); llenosVitalidad = i }
    }

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = if (puntaje.esVictoria) R.drawable.img_resultado_victoria
                        else R.drawable.img_resultado_derrota
                    ),
                    contentDescription = "Resultado de la partida",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Text(
                    text = if (puntaje.esVictoria) "¡PARTIDA GANADA! 🎉" else "¡PARTIDA PERDIDA! ⚠️",
                    color = if (puntaje.esVictoria) colors.success else colors.error,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = if (puntaje.esVictoria) "Excelente combinación de alimentos saludables."
                    else "Cuidado con los ultraprocesados y calorías vacías.",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )

                // Tarjeta de puntajes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    PuntajeFila("Salud", puntaje.salud, llenosSalud)
                    Spacer(Modifier.height(16.dp))
                    PuntajeFila("Saciedad", puntaje.saciedad, llenosSaciedad)
                    Spacer(Modifier.height(16.dp))
                    PuntajeFila("Vitalidad", puntaje.vitalidad, llenosVitalidad)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(1.dp)
                            .background(colors.cardBorder)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL OBTENIDO",
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${puntaje.total} / ${puntaje.maximo} pts (${puntaje.porcentaje.toInt()}%)",
                            color = colors.success,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dato curioso (se oculta si no hay)
                dato?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.cardBg)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "¿SABÍAS QUE…?",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.plato,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Text(
                            text = it.dato,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = "Volver a jugar",
                onClick = onPlayAgain,
                shape = RoundedCornerShape(16.dp),
                minHeight = 54.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            SecondaryButton(
                text = "Volver al menú",
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

@Composable
private fun PuntajeFila(titulo: String, puntos: Int, llenos: Int) {
    val colors = AppTheme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "$puntos pts", color = colors.textSecondary, fontSize = 13.sp)
        }
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(ReglasJuego.MAX_PUNTOS_POR_CATEGORIA) { index ->
                PuntajeCirculo(lleno = index < llenos)
            }
        }
    }
}

@Composable
private fun PuntajeCirculo(lleno: Boolean) {
    val colors = AppTheme.colors
    // "Pop" al llenarse: arranca grande y rebota hasta su tamaño normal
    val extra by animateFloatAsState(
        targetValue = if (lleno) 0f else 0.35f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dotPop"
    )
    val popScale = if (lleno) 1f + extra else 1f
    Box(
        modifier = Modifier
            .size(28.dp)
            .scale(popScale)
            .clip(CircleShape)
            .background(if (lleno) colors.success else colors.cardUnselectedBg)
            .border(1.dp, if (lleno) colors.success else colors.cardBorder, CircleShape)
    )
}
