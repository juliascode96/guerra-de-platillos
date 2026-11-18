package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.data.RankingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RankingUiState(
    val isLoading: Boolean = true,
    val entries: List<RankingEntry> = emptyList(),
    val errorMessage: String? = null,
)

class RankingViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = (application as GuerraDePlatillosApp).authRepository

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        cargarRankings()
    }

    fun cargarRankings() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            authRepo.obtenerTopRankings(limit = 10).fold(
                onSuccess = { list ->
                    _uiState.value = RankingUiState(
                        isLoading = false,
                        entries = list,
                    )
                },
                onFailure = { error ->
                    _uiState.value = RankingUiState(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "No se pudo cargar el ranking",
                    )
                }
            )
        }
    }
}
