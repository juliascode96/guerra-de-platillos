package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.model.Avatar
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel del usuario/avatar:
 * - Expone el perfil guardado localmente y el apodo sincronizado con Firestore
 * - Valida (nombre/apodo y rango de edad), persiste los cambios en SharedPreferences
 *   y sobrescribe el apodo en Firestore.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GuerraDePlatillosApp
    private val userRepo = app.userRepository
    private val authRepo = app.authRepository

    val avatar: StateFlow<Avatar?> = userRepo.avatar
    val apodoRemoto: StateFlow<String> = userRepo.apodoRemoto

    /** @return `true` si los datos eran válidos y se guardaron. */
    fun guardarPerfil(
        nombre: String,
        edadTexto: String,
        esMasculino: Boolean,
        avatarId: Int = Avatar.DEFAULT_AVATAR_ID,
    ): Boolean {
        val nombreTrim = nombre.trim()
        val edad = edadTexto.trim().toIntOrNull()
        if (nombreTrim.isEmpty() || edad == null || edad !in 1..120) return false

        val sexo = if (esMasculino) Avatar.SEXO_MASCULINO else Avatar.SEXO_FEMENINO
        userRepo.guardarAvatar(Avatar(nombreTrim, sexo, edad, avatarId))
        authRepo.actualizarApodoEnFirestore(nombreTrim)
        return true
    }
}
