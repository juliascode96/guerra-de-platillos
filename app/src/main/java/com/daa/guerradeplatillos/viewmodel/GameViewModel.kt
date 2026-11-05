package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Canal para notificar eventos de un solo disparo (como Toasts o Navegación) a la View
    private val _uiEvent = Channel<GameUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Inicializamos el detector. Al agitar, se ejecuta esta lógica.
    private val shakeDetector = ShakeDetector(application) {
        onPhoneShaken()
    }

    private fun onPhoneShaken() {
        viewModelScope.launch {
            // Enviamos el evento para que la View pinte el Toast
            _uiEvent.send(GameUiEvent.ShowShakeToast)
        }
    }

    // Métodos para que la View controle los sensores según su ciclo de vida
    fun startSensors() {
        shakeDetector.startListening()
    }

    fun pauseSensors() {
        shakeDetector.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        shakeDetector.stopListening() // Limpieza definitiva de recursos
    }
}