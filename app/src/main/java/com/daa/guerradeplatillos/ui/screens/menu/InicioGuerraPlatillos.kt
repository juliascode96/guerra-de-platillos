package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme

@Composable
fun InicioGuerraPlatillosScreen(
    onStartGameClick: () -> Unit,
    onBack: () -> Unit
) {
    val cardBg = Color(0xFF162523)
    val accentGreen = Color(0xFFA5D6A7)

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera superior
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DUELO DE CARTAS",
                    color = accentGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Guerra de\nPlatillos",
                    color = accentGreen,
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
                    .background(cardBg)
                    .border(1.dp, accentGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
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
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[ Ilustración de Cartas / Duelo ]",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¡El duelo está servido!",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tus platillos favoritos, sobre la mesa.",
                        color = Color.LightGray,
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
                    .background(cardBg)
                    .border(1.dp, Color(0xFF1E3330), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = "Puntos",
                    color = accentGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón inferior ¡JUGAR!
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = onStartGameClick,
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "¡JUGAR! ▷",
                        color = Color(0xFF0F1B1A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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

