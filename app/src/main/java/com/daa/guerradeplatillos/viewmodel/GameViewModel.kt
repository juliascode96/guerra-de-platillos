package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.sensors.ShakeDetector
import com.daa.guerradeplatillos.util.SoundManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONArray

sealed interface GameUiEvent {
    data object ShowShakeToast : GameUiEvent
}

data class GameUiState(
    val isListening: Boolean = false
)

enum class RoundWinner {
    PLAYER,
    RIVAL,
    TIE,
}

data class RoundResult(
    val roundNumber: Int,
    val winner: RoundWinner,
    val playerPointsGained: Int,
    val rivalPointsGained: Int,
    val playerCategoriesWon: Int,
    val rivalCategoriesWon: Int,
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // --- LÓGICA DE SENSORES ---
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<GameUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val shakeDetector = ShakeDetector(application) {
        onPhoneShaken()
    }

    private fun onPhoneShaken() {
        if (isWaitingReveal) {
            revealCurrentRound()
        } else {
            viewModelScope.launch {
                _uiEvent.send(GameUiEvent.ShowShakeToast)
            }
        }
    }

    fun startSensors() {
        shakeDetector.startListening()
    }

    fun pauseSensors() {
        shakeDetector.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        shakeDetector.stopListening()
    }

    // --- LÓGICA DE GUERRA DE PLATILLOS ---
    private val fullDeck: List<CardModel> = loadDeckFromAssets(application)

    var playerHand by mutableStateOf(listOf<CardModel>())
        private set
    var rivalHand by mutableStateOf(listOf<CardModel>())
        private set

    var selectedCard by mutableStateOf<CardModel?>(null)
        private set
    var playerPlayedCard by mutableStateOf<CardModel?>(null)
        private set
    var rivalPlayedCard by mutableStateOf<CardModel?>(null)
        private set

    var playerScore by mutableIntStateOf(0)
        private set
    var rivalScore by mutableIntStateOf(0)
        private set
    var roundsPlayed by mutableIntStateOf(0)
        private set
    var lastRoundResult by mutableStateOf<RoundResult?>(null)
        private set
    var areCardsRevealed by mutableStateOf(false)
        private set

    val totalRounds: Int = CARDS_PER_PLAYER
    val isWaitingReveal: Boolean
        get() = playerPlayedCard != null && !areCardsRevealed
    val canClearBoardForNextRound: Boolean
        get() = areCardsRevealed && !isGameFinished && playerPlayedCard != null
    val isGameFinished: Boolean
        get() = roundsPlayed >= totalRounds

    init {
        startNewGame()
    }

    fun startNewGame() {
        val shuffled = fullDeck.shuffled()
        playerHand = shuffled.take(CARDS_PER_PLAYER)
        rivalHand = shuffled.drop(CARDS_PER_PLAYER).take(CARDS_PER_PLAYER)

        selectedCard = null
        playerPlayedCard = null
        rivalPlayedCard = null
        playerScore = 0
        rivalScore = 0
        roundsPlayed = 0
        lastRoundResult = null
        areCardsRevealed = false
    }

    fun selectCard(card: CardModel) {
        if (isGameFinished || isWaitingReveal) return
        selectedCard = card
    }

    fun playTurn() {
        val chosen = selectedCard ?: return
        playCard(chosen)
    }

    fun playCard(chosen: CardModel) {
        if (isGameFinished || isWaitingReveal) return
        if (chosen !in playerHand) return
        val rivalChoice = rivalHand.randomOrNull() ?: return

        playerHand = playerHand - chosen
        rivalHand = rivalHand - rivalChoice
        playerPlayedCard = chosen
        rivalPlayedCard = rivalChoice
        selectedCard = null
        areCardsRevealed = false
        lastRoundResult = null
    }

    /**
     * Da vuelta ambas cartas jugadas (al girar/agitar el celular) y calcula
     * el puntaje de la ronda.
     */
    fun revealCurrentRound() {
        if (!isWaitingReveal) return
        val playerCard = playerPlayedCard ?: return
        val rivalCard = rivalPlayedCard ?: return

        val result = evaluateRound(
            roundNumber = roundsPlayed + 1,
            playerCard = playerCard,
            rivalCard = rivalCard,
        )
        playerScore += result.playerPointsGained
        rivalScore += result.rivalPointsGained
        roundsPlayed += 1
        lastRoundResult = result
        areCardsRevealed = true

        when (result.winner) {
            RoundWinner.PLAYER -> SoundManager.playResultPositive()
            RoundWinner.RIVAL -> SoundManager.playResultNegative()
            RoundWinner.TIE -> SoundManager.playBubbly()
        }
    }

    /** Limpia las cartas ya jugadas de la mesa para comenzar la siguiente ronda. */
    fun clearBoardForNextRound() {
        if (!canClearBoardForNextRound) return
        playerPlayedCard = null
        rivalPlayedCard = null
        areCardsRevealed = false
        lastRoundResult = null
    }

    /**
     * Compara energía, saciedad y vitalidad:
     * - Quien gana más categorías (2 de 3, o las 3) gana la ronda y suma la diferencia
     *   de puntos de las categorías que ganó.
     * - Si hay empate en cantidad de categorías ganadas, ambos suman la diferencia de
     *   la categoría que cada uno ganó.
     */
    private fun evaluateRound(
        roundNumber: Int,
        playerCard: CardModel,
        rivalCard: CardModel,
    ): RoundResult {
        val comparisons = listOf(
            playerCard.energyPoints to rivalCard.energyPoints,
            playerCard.satietyPoints to rivalCard.satietyPoints,
            playerCard.vitalityPoints to rivalCard.vitalityPoints,
        )

        var playerWins = 0
        var rivalWins = 0
        var playerWinDiff = 0
        var rivalWinDiff = 0

        for ((playerVal, rivalVal) in comparisons) {
            when {
                playerVal > rivalVal -> {
                    playerWins++
                    playerWinDiff += (playerVal - rivalVal)
                }
                rivalVal > playerVal -> {
                    rivalWins++
                    rivalWinDiff += (rivalVal - playerVal)
                }
            }
        }

        val (winner, playerGain, rivalGain) = when {
            playerWins > rivalWins -> Triple(RoundWinner.PLAYER, playerWinDiff, 0)
            rivalWins > playerWins -> Triple(RoundWinner.RIVAL, 0, rivalWinDiff)
            else -> Triple(RoundWinner.TIE, playerWinDiff, rivalWinDiff)
        }

        return RoundResult(
            roundNumber = roundNumber,
            winner = winner,
            playerPointsGained = playerGain,
            rivalPointsGained = rivalGain,
            playerCategoriesWon = playerWins,
            rivalCategoriesWon = rivalWins,
        )
    }

    private fun loadDeckFromAssets(application: Application): List<CardModel> =
        com.daa.guerradeplatillos.data.DeckRepository.cargarBaraja(application)

    companion object {
        const val CARDS_PER_PLAYER = 3
    }
}
