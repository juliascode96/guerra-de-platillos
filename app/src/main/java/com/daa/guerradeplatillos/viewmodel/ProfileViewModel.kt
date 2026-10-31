package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.model.Avatar
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel del usuario/avatar:
 * - Expone el perfil guardado
 * - Valida (nombre y rango de edad) y persiste los cambios
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as GuerraDePlatillosApp).userRepository

    val avatar: StateFlow<Avatar?> = repo.avatar

    /** @return `true` si los datos eran válidos y se guardaron. */
    fun guardarPerfil(nombre: String, edadTexto: String, esMasculino: Boolean): Boolean {
        val nombreTrim = nombre.trim()
        val edad = edadTexto.trim().toIntOrNull()
        if (nombreTrim.isEmpty() || edad == null || edad !in 1..120) return false

        val sexo = if (esMasculino) Avatar.SEXO_MASCULINO else Avatar.SEXO_FEMENINO
        repo.guardarAvatar(Avatar(nombreTrim, sexo, edad))
        return true
    }
}
