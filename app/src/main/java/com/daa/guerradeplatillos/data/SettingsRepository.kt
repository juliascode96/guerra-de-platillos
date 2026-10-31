package com.daa.guerradeplatillos.data

import android.content.Context
import androidx.core.content.edit
import com.daa.guerradeplatillos.ui.theme.ColorPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preferencias generales de la app (sonido y paleta de colores),
 * persistidas en SharedPreferences y expuestas como StateFlow para Compose.
 */
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _palette = MutableStateFlow(ColorPalette.fromKey(prefs.getString(KEY_THEME, null)))
    val palette: StateFlow<ColorPalette> = _palette.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    fun setPalette(palette: ColorPalette) {
        prefs.edit { putString(KEY_THEME, palette.key) }
        _palette.value = palette
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SOUND, enabled) }
        _soundEnabled.value = enabled
    }

    private companion object {
        const val PREFS_NAME = "GuerraDePlatillosPrefs"
        const val KEY_THEME = "selected_theme"
        const val KEY_SOUND = "sound_enabled"
    }
}
