package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.sensors.ShakeDetector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface GameUiEvent {
    data object ShowShakeToast : GameUiEvent
}

data class GameUiState(
    val isListening: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // --- LÓGICA DE SENSORES (Lo que ya tenías) ---
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<GameUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val shakeDetector = ShakeDetector(application) {
        onPhoneShaken()
    }

    private fun onPhoneShaken() {
        viewModelScope.launch {
            _uiEvent.send(GameUiEvent.ShowShakeToast)
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


    // --- LÓGICA DEL JUEGO DE CARTAS (Mazo de 40 y reparto de 5) ---
    private val fullDeck: List<CardModel> = List(40) { index ->
        val names = listOf("Pizza al horno", "Tacos de la casa", "Empanadas", "Ravioles", "Hamburguesa", "Bowl de quinoa", "Nuggets de pollo")
        val name = names[index % names.size] + " #${index + 1}"
        CardModel(
            id = index + 1,
            name = name,
            description = "Ingredientes frescos",
            healthPoints = (0..2).random(),
            satietyPoints = (0..2).random(),
            vitalityPoints = (0..2).random(),
            cardNumber = (1..9).random()
        )
    }

    var deck by mutableStateOf(listOf<CardModel>())
        private set
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

    init {
        startNewGame()
    }

    fun startNewGame() {
        val shuffled = fullDeck.shuffled()
        playerHand = shuffled.take(5)
        rivalHand = shuffled.drop(5).take(5)
        deck = shuffled.drop(10)

        selectedCard = null
        playerPlayedCard = null
        rivalPlayedCard = null
    }

    fun selectCard(card: CardModel) {
        selectedCard = card
    }

    fun playTurn() {
        val chosen = selectedCard ?: return
        playerHand = playerHand - chosen
        playerPlayedCard = chosen
        selectedCard = null

        if (rivalHand.isNotEmpty()) {
            val rivalChoice = rivalHand.random()
            rivalHand = rivalHand - rivalChoice
            rivalPlayedCard = rivalChoice
        }
    }
}