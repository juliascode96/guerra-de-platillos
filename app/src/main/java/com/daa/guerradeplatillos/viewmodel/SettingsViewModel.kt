package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.ui.theme.ColorPalette
import kotlinx.coroutines.flow.StateFlow

/** ViewModel de la pantalla de Configuración (sonido + paleta de accesibilidad). */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as GuerraDePlatillosApp).settingsRepository

    val palette: StateFlow<ColorPalette> = repo.palette
    val soundEnabled: StateFlow<Boolean> = repo.soundEnabled

    fun setSoundEnabled(enabled: Boolean) = repo.setSoundEnabled(enabled)

    /** Protanopia y Tritanopia son mutuamente excluyentes: activar una desactiva la otra. */
    fun setProtanopia(enabled: Boolean) =
        repo.setPalette(if (enabled) ColorPalette.PROTANOPIA else ColorPalette.NORMAL)

    fun setTritanopia(enabled: Boolean) =
        repo.setPalette(if (enabled) ColorPalette.TRITANOPIA else ColorPalette.NORMAL)
}
