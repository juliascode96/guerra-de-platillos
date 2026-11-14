package com.daa.guerradeplatillos.ui.screens.deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.data.DeckRepository
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.screens.game.CardDetailDialog
import com.daa.guerradeplatillos.ui.screens.game.MiniCard
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.util.SoundManager

/**
 * Pantalla de galería de la baraja completa de Guerra de Platillos.
 * Muestra cada carta en su formato mini (imagen + puntajes E/S/V) y permite
 * abrir la imagen completa al tocarla.
 */
@Composable
fun DeckScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    val cards = remember(context) { DeckRepository.cargarBaraja(context) }

    DeckContent(
        cards = cards,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun DeckContent(
    cards: List<CardModel>,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val colors = AppTheme.colors
    var detailCard by remember { mutableStateOf<CardModel?>(null) }

    ScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: volver / título / settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    contentDescription = stringResource(R.string.btn_back_description),
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "BARAJA",
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircleIconButton(
                    icon = R.drawable.ic_settings,
                    contentDescription = stringResource(R.string.btn_settings_description),
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Text(
                text = "${cards.size} cartas · Tocá una carta para verla completa",
                color = colors.textSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (cards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se pudieron cargar las cartas.",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 104.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cards, key = { it.id }) { card ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            MiniCard(
                                card = card,
                                width = 102.dp,
                                height = 154.dp,
                                onClick = {
                                    SoundManager.playBubbly()
                                    detailCard = card
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    detailCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { detailCard = null }
        )
    }
}
