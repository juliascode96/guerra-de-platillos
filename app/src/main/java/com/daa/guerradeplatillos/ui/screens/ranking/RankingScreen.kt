package com.daa.guerradeplatillos.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daa.guerradeplatillos.R
import com.daa.guerradeplatillos.data.RankingEntry
import com.daa.guerradeplatillos.ui.components.AppCard
import com.daa.guerradeplatillos.ui.components.CircleIconButton
import com.daa.guerradeplatillos.ui.components.PrimaryButton
import com.daa.guerradeplatillos.ui.components.ScreenContainer
import com.daa.guerradeplatillos.ui.theme.AppTheme
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.viewmodel.RankingUiState
import com.daa.guerradeplatillos.viewmodel.RankingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Ranking: muestra los 10 puntajes más altos registrados en Firestore (`matches`).
 */
@Composable
fun RankingScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: RankingViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RankingContent(
        uiState = uiState,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
        onRetry = viewModel::cargarRankings,
    )
}

@Composable
private fun RankingContent(
    uiState: RankingUiState,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit,
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
                    text = stringResource(R.string.ranking_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
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

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.success)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PrimaryButton(
                                text = "Reintentar",
                                onClick = onRetry,
                                shape = RoundedCornerShape(14.dp),
                                minHeight = 46.dp,
                            )
                        }
                    }
                }

                uiState.entries.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Todavía no hay partidas registradas.\n¡Jugá una partida para estrenar el ranking!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Text(
                        text = "TOP 10 MEJORES PARTIDAS",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.entries,
                            key = { _, entry -> entry.id }
                        ) { index, entry ->
                            RankingCard(position = index + 1, entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingCard(
    position: Int,
    entry: RankingEntry,
) {
    val colors = AppTheme.colors
    val badgeColor = when (position) {
        1 -> colors.goldXp
        2 -> colors.textSecondary
        3 -> colors.warning
        else -> colors.cardBorder
    }
    val badgeTextColor = if (position <= 3) colors.buttonPrimaryText else colors.textPrimary

    AppCard(
        selected = entry.isCurrentUser,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición (#1..#10)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(1.dp, colors.cardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$position",
                    color = badgeTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nickname + Fecha de la partida
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.nickname,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatMatchDate(entry.timestampMillis),
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Puntaje
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score}",
                    color = if (position == 1) colors.goldXp else colors.success,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "pts",
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatMatchDate(timestampMillis: Long?): String {
    if (timestampMillis == null) return "Fecha desconocida"
    val formatter = SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.getDefault())
    return formatter.format(Date(timestampMillis))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RankingScreenPreview() {
    GuerraDePlatillosTheme {
        RankingContent(
            uiState = RankingUiState(
                isLoading = false,
                entries = listOf(
                    RankingEntry("1", "el_mejor", 125, System.currentTimeMillis(), isCurrentUser = true),
                    RankingEntry("2", "ChefMateo", 95, System.currentTimeMillis() - 3_600_000L),
                    RankingEntry("3", "VerdePro", 80, System.currentTimeMillis() - 86_400_000L),
                ),
            ),
            onBack = {},
            onSettingsClick = {},
            onRetry = {},
        )
    }
}
