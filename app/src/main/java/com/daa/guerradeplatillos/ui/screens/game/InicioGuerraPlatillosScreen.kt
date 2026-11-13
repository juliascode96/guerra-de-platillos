package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

@Composable
fun InicioGuerraPlatillosScreen(
    onStartGameClick: () -> Unit,
    onBack: () -> Unit
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
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
                        Text(
                            text = "[ Ilustración de Cartas / Duelo ]",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
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

            // Sección de Puntos (vacía / placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.cardBg)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = "Puntos",
                    color = colors.success,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón inferior ¡JUGAR!
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioGuerraPlatillosPreview() {
    GuerraDePlatillosTheme {
        InicioGuerraPlatillosScreen(onStartGameClick = {}, onBack = {})
    }
}
