package com.daa.guerradeplatillos.data

import android.content.Context
import androidx.core.content.edit
import com.daa.guerradeplatillos.model.Avatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Datos del usuario/avatar persistidos en SharedPreferences
 * (equivalente al UserPreferences de Código Verde).
 */
class UserRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _avatar = MutableStateFlow(leerAvatar())
    val avatar: StateFlow<Avatar?> = _avatar.asStateFlow()

    fun guardarAvatar(avatar: Avatar) {
        prefs.edit {
            putString(KEY_NOMBRE, avatar.nombre)
            putInt(KEY_EDAD, avatar.edad)
            putString(KEY_SEXO, avatar.sexo)
            putBoolean(KEY_HAS_AVATAR, true)
        }
        _avatar.value = avatar
    }

    fun borrarAvatar() {
        prefs.edit { clear() }
        _avatar.value = null
    }

    private fun leerAvatar(): Avatar? {
        if (!prefs.getBoolean(KEY_HAS_AVATAR, false)) return null
        val sexo = when (prefs.getString(KEY_SEXO, null)?.lowercase()) {
            "femenino", "mujer" -> Avatar.SEXO_FEMENINO
            else -> Avatar.SEXO_MASCULINO
        }
        return Avatar(
            nombre = prefs.getString(KEY_NOMBRE, "") ?: "",
            sexo = sexo,
            edad = prefs.getInt(KEY_EDAD, 0),
        )
    }

    private companion object {
        const val PREFS_NAME = "GuerraDePlatillosUserPrefs"
        const val KEY_NOMBRE = "user_nombre"
        const val KEY_EDAD = "user_edad"
        const val KEY_SEXO = "user_sexo"
        const val KEY_HAS_AVATAR = "has_avatar"
    }
}
