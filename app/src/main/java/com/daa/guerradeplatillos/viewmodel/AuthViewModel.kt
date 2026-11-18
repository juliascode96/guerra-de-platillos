package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = (application as GuerraDePlatillosApp).authRepository

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun iniciarSesion(
        username: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        if (_uiState.value.isLoading) return
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val result = authRepo.iniciarSesion(username, password)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isLoading = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo iniciar sesión",
                    )
                }
            )
        }
    }

    fun registrar(
        username: String,
        password: String,
        apodo: String,
        onSuccess: () -> Unit,
    ) {
        if (_uiState.value.isLoading) return
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val result = authRepo.registrar(username, password, apodo)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isLoading = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo crear la cuenta",
                    )
                }
            )
        }
    }
}
