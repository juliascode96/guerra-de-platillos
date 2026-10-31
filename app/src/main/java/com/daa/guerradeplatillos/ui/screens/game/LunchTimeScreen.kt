package com.daa.guerradeplatillos.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class FoodItem(
    val id: Int,
    val name: String,
    val isHealthy: Boolean
)

@Composable
fun LunchTimeScreen(
    onConfirmMenu: () -> Unit,
    onBack: () -> Unit
) {
    val accentGreen = Color(0xFFA5D6A7)
    val cardBg = Color(0xFF162523)
    val selectedBorder = Color(0xFFFFD54F) // Amarillo/Dorado para la selección como en tu imagen

    // Lista de ejemplo de alimentos para el desayuno
    val foodList = listOf(
        FoodItem(1, "Tostadas integrales con palta", true),
        FoodItem(2, "Porridge de avena con banana", true),
        FoodItem(3, "Medialunas de grasa", false),
        FoodItem(4, "Donas glaseadas", false),
        FoodItem(5, "Yogur griego con granola", true),
        FoodItem(6, "Cereales azucarados", false)
    )

    // Estado de los elementos seleccionados (IDs)
    var selectedItems by remember { mutableStateOf(setOf(1, 5)) } // Ejemplo con 2 seleccionados por defecto

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera superior
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Text(text = "‹", color = accentGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }

                    // Chip de tipo de comida
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E3330))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "● DESAYUNO",
                            color = accentGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Chip de horario
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E3330))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🕒 6-10hs",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subtítulo de instrucción
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF162523))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍳 Elegí 3 alimentos para tu desayuno",
                        color = accentGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Grilla de alimentos (2 columnas x 3 filas)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(foodList) { food ->
                    val isSelected = selectedItems.contains(food.id)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) selectedBorder else Color(0xFF1E3330),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedItems = if (isSelected) {
                                    selectedItems - food.id
                                } else {
                                    if (selectedItems.size < 3) selectedItems + food.id else selectedItems
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Espacio para la imagen del alimento (Placeholder visual)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(105.dp)
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[ Imagen ]",
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )

                                // Indicador de check si está seleccionado
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(11.dp))
                                            .background(selectedBorder),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "✓", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Texto del nombre del alimento
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = food.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Sección inferior: Progreso y Botón de Confirmación
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TU PLATO", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${selectedItems.size}/3 seleccionados", color = accentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Barra de progreso personalizada
                LinearProgressIndicator(
                    progress = { selectedItems.size / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accentGreen,
                    trackColor = Color(0xFF162523),
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Confirmar Menú
                Button(
                    onClick = onConfirmMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "CONFIRMAR MENÚ",
                        color = Color(0xFF0F1B1A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LunchTimeScreenPreview() {
    GuerraDePlatillosTheme {
        LunchTimeScreen(onConfirmMenu = {}, onBack = {})
    }
}