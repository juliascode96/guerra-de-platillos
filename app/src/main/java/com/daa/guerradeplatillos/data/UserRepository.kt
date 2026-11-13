package com.daa.guerradeplatillos.data

import android.content.Context
import androidx.core.content.edit
import com.daa.guerradeplatillos.model.Avatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Datos del usuario/avatar persistidos en SharedPreferences
 * (equivalente al UserPreferences de Código Verde), con soporte
 * para sincronizar el apodo (nombre) con Firestore.
 */
class UserRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _avatar = MutableStateFlow(leerAvatar())
    val avatar: StateFlow<Avatar?> = _avatar.asStateFlow()

    private val _apodoRemoto = MutableStateFlow(prefs.getString(KEY_APODO_REMOTO, "") ?: "")
    /** Apodo traído de Firestore (para precargar al crear el avatar). */
    val apodoRemoto: StateFlow<String> = _apodoRemoto.asStateFlow()

    private val _highScore = MutableStateFlow(prefs.getInt(KEY_HIGH_SCORE, 0))
    /** Puntaje máximo traído de Firestore (`users/{uid}.highScore`). */
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    fun guardarAvatar(avatar: Avatar) {
        prefs.edit {
            putString(KEY_NOMBRE, avatar.nombre)
            putString(KEY_APODO_REMOTO, avatar.nombre)
            putInt(KEY_EDAD, avatar.edad)
            putString(KEY_SEXO, avatar.sexo)
            putBoolean(KEY_HAS_AVATAR, true)
        }
        _apodoRemoto.value = avatar.nombre
        _avatar.value = avatar
    }

    /**
     * Se invoca al registrarse, iniciar sesión o sincronizar con Firestore.
     * - Si entra un UID distinto al anterior en este teléfono, limpia el avatar anterior
     *   para que el nuevo usuario complete su edad y género con su apodo precargado.
     * - Si ya tenía avatar en este teléfono, sincroniza su nombre con el apodo de Firestore.
     */
    fun sincronizarConFirestore(uid: String, apodoFirestore: String, highScoreFirestore: Int = 0) {
        val lastUid = prefs.getString(KEY_UID, null)
        val apodoLimpio = apodoFirestore.trim()

        if (lastUid != null && lastUid != uid) {
            // Cambió de cuenta en el mismo dispositivo: reiniciamos el avatar local
            prefs.edit {
                clear()
                putString(KEY_UID, uid)
                putString(KEY_APODO_REMOTO, apodoLimpio)
                putInt(KEY_HIGH_SCORE, highScoreFirestore)
            }
            _apodoRemoto.value = apodoLimpio
            _highScore.value = highScoreFirestore
            _avatar.value = null
            return
        }

        prefs.edit {
            putString(KEY_UID, uid)
            putInt(KEY_HIGH_SCORE, highScoreFirestore)
            if (apodoLimpio.isNotEmpty()) {
                putString(KEY_APODO_REMOTO, apodoLimpio)
                if (prefs.getBoolean(KEY_HAS_AVATAR, false)) {
                    putString(KEY_NOMBRE, apodoLimpio)
                }
            }
        }
        _highScore.value = highScoreFirestore
        if (apodoLimpio.isNotEmpty()) {
            _apodoRemoto.value = apodoLimpio
        }
        _avatar.value = leerAvatar()
    }

    fun borrarAvatar() {
        prefs.edit { clear() }
        _apodoRemoto.value = ""
        _highScore.value = 0
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
        const val KEY_UID = "user_uid"
        const val KEY_APODO_REMOTO = "user_apodo_remoto"
        const val KEY_HIGH_SCORE = "user_high_score"
        const val KEY_NOMBRE = "user_nombre"
        const val KEY_EDAD = "user_edad"
        const val KEY_SEXO = "user_sexo"
        const val KEY_HAS_AVATAR = "has_avatar"
    }
}
