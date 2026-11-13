package com.daa.guerradeplatillos.ui.screens.game

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val colors = AppTheme.colors
    // Color de la selección (antes amarillo fijo): usa el "warning" de la paleta activa
    val selectedBorder = colors.warning

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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera superior
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.btn_back_description),
                        onClick = onBack,
                    )

                    // Chip de tipo de comida
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.cardBorder)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "● DESAYUNO",
                            color = colors.success,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Chip de horario
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.cardBorder)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🕒 6-10hs",
                            color = colors.textSecondary,
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
                        .background(colors.cardBg)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍳 Elegí 3 alimentos para tu desayuno",
                        color = colors.success,
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
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) selectedBorder else colors.cardBorder,
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
                                    .background(colors.cardUnselectedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[ Imagen ]",
                                    color = colors.textSecondary,
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
                                        Text(
                                            text = "✓",
                                            color = colors.buttonPrimaryText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
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
                                    color = colors.textPrimary,
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
                    Text(text = "TU PLATO", color = colors.textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${selectedItems.size}/3 seleccionados",
                        color = colors.success,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Barra de progreso personalizada
                LinearProgressIndicator(
                    progress = { selectedItems.size / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors.success,
                    trackColor = colors.cardBg,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Confirmar Menú
                PrimaryButton(
                    text = "CONFIRMAR MENÚ",
                    onClick = onConfirmMenu,
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
private fun LunchTimeScreenPreview() {
    GuerraDePlatillosTheme {
        LunchTimeScreen(onConfirmMenu = {}, onBack = {})
    }
}
