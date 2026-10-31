package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.viewmodel.GameViewModel

@Composable
fun GuerraPlatillosScreen(
    onBack: () -> Unit,
    onFinishGame: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val accentGreen = Color(0xFFA5D6A7)
    val cardBg = Color(0xFF162523)

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Cabecera superior con el Mazo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Text(text = "‹", color = accentGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Guerra de Platillos", color = accentGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(cardBg)
                        .border(1.dp, accentGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📚 ${viewModel.deck.size + viewModel.playerHand.size}",
                        color = accentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Tablero de Duelo VS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, accentGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Carta del Rival
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "RIVAL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        MiniCard(card = viewModel.rivalPlayedCard)
                    }

                    Text(text = "VS", color = accentGreen, fontSize = 22.sp, fontWeight = FontWeight.Black)

                    // Tu carta jugada
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "VOS", color = accentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        MiniCard(card = viewModel.playerPlayedCard)
                    }
                }
            }

            // 3. Mano del Jugador (Cartas repartidas)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "● ¡Te toca! Elegí una carta de tu mano",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (viewModel.playerHand.isEmpty()) {
                        Text(text = "¡Mano vacía!", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        viewModel.playerHand.take(3).forEach { card ->
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
            Button(
                onClick = {
                    if (viewModel.selectedCard != null) {
                        viewModel.playTurn()
                    } else if (viewModel.playerHand.isEmpty()) {
                        onFinishGame()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                val textBtn = if (viewModel.selectedCard != null) "JUGAR CARTA ⇧" else "SELECCIONÁ UNA CARTA"
                Text(text = textBtn, color = Color(0xFF0F1B1A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MiniCard(
    card: CardModel?,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val cardBg = Color(0xFFFDFBF7)
    val selectedBorder = Color(0xFFA5D6A7)

    if (card == null) {
        Box(
            modifier = Modifier
                .width(95.dp)
                .height(135.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF101B1A))
                .border(1.dp, Color(0xFF1E3330), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Vacío", color = Color.Gray, fontSize = 10.sp)
        }
        return
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .width(95.dp)
            .height(135.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) selectedBorder else Color.DarkGray,
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
            Text(
                text = "${card.cardNumber}",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Plato", fontSize = 8.sp, color = Color.DarkGray)
            }
            Text(
                text = card.name,
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column {
                Text(text = "Saludable: ${card.healthPoints}", fontSize = 7.sp, color = Color.Gray)
                Text(text = "Saciedad: ${card.satietyPoints}", fontSize = 7.sp, color = Color.Gray)
            }
        }
    }
}