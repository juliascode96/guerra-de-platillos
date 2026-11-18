package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.daa.guerradeplatillos.ui.components.HowToPlayDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.SecondaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

@Composable
fun InicioGuerraPlatillosScreen(
    onStartGameClick: () -> Unit,
    onContinueGameClick: () -> Unit = {},
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val app = LocalContext.current.applicationContext as? GuerraDePlatillosApp
    val highScore by (app?.userRepository?.highScore?.collectAsStateWithLifecycle()
        ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) })

    val hayPartidaGuardada by (app?.guerraGameRepository?.observarHayPartida()
        ?.collectAsStateWithLifecycle(initialValue = false)
        ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) })

    LaunchedEffect(app) {
        app?.authRepository?.refrescarUsuarioActual()
    }

    InicioGuerraPlatillosContent(
        highScore = highScore,
        hayPartidaGuardada = hayPartidaGuardada,
        onContinueGameClick = onContinueGameClick,
        onStartGameClick = onStartGameClick,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun InicioGuerraPlatillosContent(
    highScore: Int,
    hayPartidaGuardada: Boolean = false,
    onStartGameClick: () -> Unit,
    onContinueGameClick: () -> Unit = {},
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors
    var showHowTo by rememberSaveable { mutableStateOf(false) }

    if (showHowTo) {
        HowToPlayDialog(
            gameTitle = "Guerra de Platillos",
            slides = guerraHowToSlides,
            onDismiss = { showHowTo = false },
        )
    }

    ScreenContainer {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (isLandscape) Arrangement.spacedBy(12.dp) else Arrangement.SpaceBetween
            ) {
            // Cabecera superior: volver + títulos
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircleIconButton(
                            icon = R.drawable.ic_help,
                            contentDescription = "Cómo jugar",
                            onClick = { showHowTo = true },
                            tint = colors.textHighlight
                        )
                        CircleIconButton(
                            icon = R.drawable.ic_settings,
                            contentDescription = stringResource(R.string.btn_settings_description),
                            onClick = onSettingsClick
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DUELO DE CARTAS",
                    color = colors.textHighlight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Guerra de\nPlatillos",
                    color = colors.textHighlight,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
            }

            // Tarjeta principal con la ilustración de cartas (Placeholder visual)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.cardBg)
                    .border(1.dp, colors.success.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.cardUnselectedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_guerra_de_platillos_cover),
                            contentDescription = "Guerra de Platillos",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¡El duelo está servido!",
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tus platillos favoritos, sobre la mesa.",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Sección de Puntaje máximo (desde Firestore users/{uid}.highScore)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.cardBg)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mejor puntaje",
                    color = colors.success,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$highScore pts",
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Botones inferiores
            if (hayPartidaGuardada) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PrimaryButton(
                        text = "CONTINUAR PARTIDA ▷",
                        onClick = onContinueGameClick,
                        shape = RoundedCornerShape(16.dp),
                        minHeight = 52.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    SecondaryButton(
                        text = "NUEVA PARTIDA",
                        onClick = onStartGameClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }
            if (!hayPartidaGuardada)
            PrimaryButton(
                text = "¡JUGAR! ▷",
                onClick = onStartGameClick,
                shape = RoundedCornerShape(16.dp),
                minHeight = 52.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioGuerraPlatillosPreview() {
    GuerraDePlatillosTheme {
        InicioGuerraPlatillosContent(highScore = 120, onStartGameClick = {}, onBack = {})
    }
}
