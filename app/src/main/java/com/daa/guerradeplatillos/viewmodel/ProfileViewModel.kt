package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.GuerraDePlatillosApp
import com.daa.guerradeplatillos.data.DatoCuriosoRepository
import com.daa.guerradeplatillos.data.EstadisticasJugador
import com.daa.guerradeplatillos.data.ResumenLunchTimeUsuario
import com.daa.guerradeplatillos.model.Avatar
import com.daa.guerradeplatillos.model.DatoCurioso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel del usuario/avatar:
 * - Expone el perfil guardado localmente y el apodo sincronizado con Firestore
 * - Valida (nombre/apodo y rango de edad), persiste los cambios en SharedPreferences
 *   y sobrescribe el apodo en Firestore.
 * - Expone las estadísticas del jugador (racha y partidas ganadas) y un dato curioso al azar.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GuerraDePlatillosApp
    private val userRepo = app.userRepository
    private val authRepo = app.authRepository

    val avatar: StateFlow<Avatar?> = userRepo.avatar
    val apodoRemoto: StateFlow<String> = userRepo.apodoRemoto

    /** `null` mientras se cargan (o si falló la consulta). */
    private val _estadisticas = MutableStateFlow<EstadisticasJugador?>(null)
    val estadisticas: StateFlow<EstadisticasJugador?> = _estadisticas.asStateFlow()

    private val _datoCurioso = MutableStateFlow<DatoCurioso?>(null)
    val datoCurioso: StateFlow<DatoCurioso?> = _datoCurioso.asStateFlow()

    private val _resumenLunchTime = MutableStateFlow<ResumenLunchTimeUsuario?>(null)
    val resumenLunchTime: StateFlow<ResumenLunchTimeUsuario?> = _resumenLunchTime.asStateFlow()

    /** Recarga estadísticas desde Firestore/Room y sortea un nuevo dato curioso. */
    fun cargarDatosPerfil() {
        viewModelScope.launch {
            _datoCurioso.value = withContext(Dispatchers.IO) {
                DatoCuriosoRepository.cargarDatos(app).values.randomOrNull()
            }
            _resumenLunchTime.value = withContext(Dispatchers.IO) {
                app.lunchTimeHistoryRepository.obtenerResumenSemanal()
            }
            authRepo.obtenerEstadisticasJugador().onSuccess { _estadisticas.value = it }
        }
    }

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
