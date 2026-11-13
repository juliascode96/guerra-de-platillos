package com.daa.guerradeplatillos.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.components.SecondaryButton
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

/**
 * Pantalla de Menú Principal con los dos minijuegos, Perfil, Ranking y Cerrar sesión.
 */
@Composable
fun MainMenuScreen(
    onPlayGame1Click: () -> Unit,
    onPlayGame2Click: () -> Unit,
    onProfileClick: () -> Unit,
    onRankingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    onLogoutClick: () -> Unit = {},
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

            // Contenido central con los juegos y las opciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Títulos superiores
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textHighlight,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.main_menu_subtitle),
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Tarjetas de los Dos Juegos (Juego 1 y Juego 2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameCardItem(
                        title = "LUNCH TIME",
                        description = "Armá tu menú saludable.",
                        buttonText = "¡JUGAR! ▷",
                        onPlayClick = onPlayGame1Click
                    )

                    GameCardItem(
                        title = "GUERRA DE PLATILLOS",
                        description = "Desafío Máximo",
                        buttonText = "¡JUGAR! ▷",
                        onPlayClick = onPlayGame2Click
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Botones inferiores de Perfil, Ranking y Cerrar sesión
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.main_menu_profile),
                        onClick = onProfileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    )

                    SecondaryButton(
                        text = stringResource(R.string.main_menu_ranking),
                        onClick = onRankingClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    )

                    SecondaryButton(
                        text = "Cerrar sesión",
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCardItem(
    title: String,
    description: String,
    buttonText: String,
    onPlayClick: () -> Unit
) {
    val cardBg = AppTheme.colors.cardBg
    val accentGreen = AppTheme.colors.success

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, accentGreen, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen o recuadro de miniatura del juego
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.cardUnselectedBg)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Textos y botón de jugar individual
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = accentGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = AppTheme.colors.textSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = buttonText,
                        color = AppTheme.colors.buttonPrimaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainMenuScreenPreview() {
    GuerraDePlatillosTheme {
        MainMenuScreen(
            onPlayGame1Click = {},
            onPlayGame2Click = {},
            onProfileClick = {},
            onRankingClick = {},
            onSettingsClick = {},
            onBack = {},
        )
    }
}
