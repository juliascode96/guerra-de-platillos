package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun InicioLunchTimeScreen(
    onStartGameClick: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors

    ScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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
                    CircleIconButton(
                        icon = R.drawable.ic_settings,
                        contentDescription = stringResource(R.string.btn_settings_description),
                        onClick = onSettingsClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EL MINIJUEGO DE LA APP",
                    color = colors.textHighlight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "LUNCH TIME",
                    color = colors.textHighlight,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }

            // Portada del juego
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.cardBg)
                    .border(1.dp, colors.success.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.portada),
                    contentDescription = "Portada de Lunch Time",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Explicación del juego
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.cardBg)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Elegí la comida saludable y ganá puntos! Tenés 20 segundos para armar tu bandeja con hasta 3 alimentos.",
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            PrimaryButton(
                text = "¡JUGAR! ▷",
                onClick = onStartGameClick,
                shape = RoundedCornerShape(16.dp),
                minHeight = 50.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioLunchTimePreview() {
    GuerraDePlatillosTheme {
        InicioLunchTimeScreen(onStartGameClick = {}, onBack = {})
    }
}
