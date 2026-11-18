package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.model.Avatar
import com.daa.guerradeplatillos.ui.theme.ColorPalette
import kotlinx.coroutines.flow.StateFlow

/** ViewModel de la pantalla de Configuración (sonido + paleta de accesibilidad + sesión). */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GuerraDePlatillosApp
    private val repo = app.settingsRepository
    private val authRepo = app.authRepository
    private val userRepo = app.userRepository

    val palette: StateFlow<ColorPalette> = repo.palette
    val soundEnabled: StateFlow<Boolean> = repo.soundEnabled
    val currentUserUid: StateFlow<String?> = authRepo.currentUserUid
    val avatar: StateFlow<Avatar?> = userRepo.avatar
    val apodoRemoto: StateFlow<String> = userRepo.apodoRemoto

    fun setSoundEnabled(enabled: Boolean) = repo.setSoundEnabled(enabled)

    /** Protanopia y Tritanopia son mutuamente excluyentes: activar una desactiva la otra. */
    fun setProtanopia(enabled: Boolean) =
        repo.setPalette(if (enabled) ColorPalette.PROTANOPIA else ColorPalette.NORMAL)

    fun setTritanopia(enabled: Boolean) =
        repo.setPalette(if (enabled) ColorPalette.TRITANOPIA else ColorPalette.NORMAL)

    fun cerrarSesion() {
        authRepo.cerrarSesion()
    }
}
