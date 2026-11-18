package com.daa.guerradeplatillos.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de:
 * 1. Autenticación con Firebase Auth usando un email sintético (`username@guerradeplatillos.app`).
 *    La sesión se mantiene abierta automáticamente entre reinicios de la app.
 * 2. Sincronización del documento del jugador en Cloud Firestore (`users/{uid}`),
 *    donde se almacena `username`, `apodo` y `highScore`.
 */
class AuthRepository(
    private val userRepository: UserRepository,
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUserUid = MutableStateFlow(auth.currentUser?.uid)
    val currentUserUid: StateFlow<String?> = _currentUserUid.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUserUid.value = user?.uid
            if (user != null) {
                ioScope.launch {
                    sincronizarDesdeFirestore(user.uid)
                }
            }
        }
    }

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Registra un nuevo usuario en Firebase Auth y crea su documento en `users/{uid}`
     * con `username`, `apodo` y `highScore = 0`.
     */
    suspend fun registrar(
        username: String,
        password: String,
        apodo: String,
    ): Result<Unit> {
        val userClean = username.trim().lowercase()
        val apodoClean = apodo.trim()

        if (!USERNAME_REGEX.matches(userClean)) {
            return Result.failure(
                IllegalArgumentException("El usuario debe tener entre 3 y 20 letras o números (sin espacios)")
            )
        }
        if (apodoClean.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingresá un apodo para mostrar en el juego"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        }

        return try {
            val authResult = auth
                .createUserWithEmailAndPassword(usernameToEmail(userClean), password)
                .await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("No se pudo obtener el ID del usuario"))

            val datosUsuario = mapOf(
                FIELD_USERNAME to userClean,
                FIELD_APODO to apodoClean,
                FIELD_HIGH_SCORE to 0L,
            )
            firestore.collection(COLLECTION_USERS)
                .document(uid)
                .set(datosUsuario)
                .await()

            userRepository.sincronizarConFirestore(uid, apodoClean)
            _currentUserUid.value = uid
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(traducirErrorFirebase(e)))
        }
    }

    /**
     * Inicia sesión con `username` y `password`, y descarga el `apodo` guardado en Firestore.
     */
    suspend fun iniciarSesion(
        username: String,
        password: String,
    ): Result<Unit> {
        val userClean = username.trim().lowercase()
        if (userClean.isEmpty() || password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Completá tu usuario y contraseña"))
        }

        return try {
            val authResult = auth
                .signInWithEmailAndPassword(usernameToEmail(userClean), password)
                .await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("No se pudo iniciar sesión"))

            sincronizarDesdeFirestore(uid, fallbackApodo = userClean)
            _currentUserUid.value = uid
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(traducirErrorFirebase(e)))
        }
    }

    /**
     * Lee el documento `users/{uid}` desde Firestore y sincroniza `apodo` y `highScore`
     * en el almacenamiento local.
     */
    suspend fun sincronizarDesdeFirestore(uid: String, fallbackApodo: String = "") {
        try {
            val snapshot = firestore.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            val apodoRemoto = snapshot.getString(FIELD_APODO)
                ?.takeIf { it.isNotBlank() }
                ?: snapshot.getString(FIELD_USERNAME)
                ?: fallbackApodo
            val highScore = snapshot.getLong(FIELD_HIGH_SCORE)?.toInt() ?: 0
            userRepository.sincronizarConFirestore(uid, apodoRemoto, highScore)
        } catch (_: Exception) {
            if (fallbackApodo.isNotBlank()) {
                userRepository.sincronizarConFirestore(uid, fallbackApodo)
            }
        }
    }

    /** Refresca los datos del usuario logueado desde Firestore (ej. highScore). */
    suspend fun refrescarUsuarioActual() {
        val uid = auth.currentUser?.uid ?: return
        sincronizarDesdeFirestore(uid)
    }

    /**
     * Actualiza el campo `apodo` en Firestore cuando el usuario crea o edita su avatar.
     */
    fun actualizarApodoEnFirestore(nuevoApodo: String) {
        val uid = auth.currentUser?.uid ?: return
        val apodoClean = nuevoApodo.trim()
        if (apodoClean.isEmpty()) return

        ioScope.launch {
            try {
                firestore.collection(COLLECTION_USERS)
                    .document(uid)
                    .set(mapOf(FIELD_APODO to apodoClean), SetOptions.merge())
                    .await()
            } catch (_: Exception) {
                // Si no hay conexión en ese instante, Firestore encola el cambio localmente.
            }
        }
    }

    /**
     * Registra la partida en la colección `matches` de Firestore y actualiza el
     * `highScore` del usuario en `users/{uid}` si superó su marca anterior.
     */
    fun registrarResultadoPartida(playerScore: Int, rivalScore: Int) {
        val uid = auth.currentUser?.uid ?: return
        val nickname = userRepository.avatar.value?.nombre?.takeIf { it.isNotBlank() }
            ?: userRepository.apodoRemoto.value.takeIf { it.isNotBlank() }
            ?: "Jugador"

        ioScope.launch {
            try {
                // 1. Consultar highScore actual en Firestore (con respaldo local)
                val userDocRef = firestore.collection(COLLECTION_USERS).document(uid)
                val snapshot = try {
                    userDocRef.get().await()
                } catch (_: Exception) {
                    null
                }
                val highScoreActual = snapshot?.getLong(FIELD_HIGH_SCORE)?.toInt()
                    ?: userRepository.highScore.value

                if (playerScore > highScoreActual) {
                    userRepository.actualizarHighScoreLocal(playerScore)
                    userDocRef
                        .set(mapOf(FIELD_HIGH_SCORE to playerScore.toLong()), SetOptions.merge())
                        .await()
                }

                // 2. Guardar la partida en la colección `matches`
                val ganoUsuario = playerScore > rivalScore
                val matchData = mapOf(
                    FIELD_MATCH_USER_ID to uid,
                    FIELD_MATCH_NICKNAME to nickname,
                    FIELD_MATCH_TIMESTAMP to Timestamp.now(),
                    FIELD_MATCH_WINNER to ganoUsuario,
                    FIELD_MATCH_WINNER_SCORE to maxOf(playerScore, rivalScore).toDouble(),
                    FIELD_MATCH_WINNER_USER_ID to if (ganoUsuario) uid else "RIVAL",
                )
                firestore.collection(COLLECTION_MATCHES)
                    .add(matchData)
                    .await()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al guardar resultado de partida", e)
            }
        }
    }

    /**
     * Obtiene las [limit] partidas con mayor puntaje donde ganó el jugador,
     * ordenadas de mayor a menor `winnerScore`.
     */
    suspend fun obtenerTopRankings(limit: Int = 10): Result<List<RankingEntry>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_MATCHES)
                .orderBy(FIELD_MATCH_WINNER_SCORE, Query.Direction.DESCENDING)
                .limit((limit * 3).toLong())
                .get()
                .await()

            val currentUid = auth.currentUser?.uid
            val entries = snapshot.documents
                .filter { doc -> doc.getBoolean(FIELD_MATCH_WINNER) == true }
                .take(limit)
                .map { doc ->
                    val score = doc.getDouble(FIELD_MATCH_WINNER_SCORE)?.toInt()
                        ?: doc.getLong(FIELD_MATCH_WINNER_SCORE)?.toInt()
                        ?: 0
                    RankingEntry(
                        id = doc.id,
                        nickname = doc.getString(FIELD_MATCH_NICKNAME)?.takeIf { it.isNotBlank() } ?: "Jugador",
                        score = score,
                        timestampMillis = doc.getTimestamp(FIELD_MATCH_TIMESTAMP)?.toDate()?.time,
                        isCurrentUser = currentUid != null && doc.getString(FIELD_MATCH_WINNER_USER_ID) == currentUid,
                    )
                }
            Result.success(entries)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al obtener rankings", e)
            Result.failure(e)
        }
    }

    /**
     * Calcula las estadísticas del jugador actual a partir de sus partidas en `matches`:
     * - `partidasGanadas`: cantidad de partidas con `winner == true`.
     * - `rachaDias`: días consecutivos (hasta hoy o ayer) en los que jugó al menos una partida.
     */
    suspend fun obtenerEstadisticasJugador(): Result<EstadisticasJugador> {
        val uid = auth.currentUser?.uid ?: return Result.success(EstadisticasJugador())
        return try {
            val snapshot = firestore.collection(COLLECTION_MATCHES)
                .whereEqualTo(FIELD_MATCH_USER_ID, uid)
                .get()
                .await()

            val ganadas = snapshot.documents.count { it.getBoolean(FIELD_MATCH_WINNER) == true }

            val zona = java.time.ZoneId.systemDefault()
            val diasJugados = snapshot.documents
                .mapNotNull { it.getTimestamp(FIELD_MATCH_TIMESTAMP)?.toDate()?.toInstant() }
                .map { it.atZone(zona).toLocalDate() }
                .toSet()

            Result.success(
                EstadisticasJugador(
                    partidasGanadas = ganadas,
                    rachaDias = calcularRacha(diasJugados, java.time.LocalDate.now(zona)),
                )
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al obtener estadísticas del jugador", e)
            Result.failure(e)
        }
    }

    /** Cierra la sesión actual de Firebase. */
    fun cerrarSesion() {
        auth.signOut()
        _currentUserUid.value = null
    }

    private fun usernameToEmail(username: String): String = "$username@$SYNTHETIC_DOMAIN"

    private fun traducirErrorFirebase(e: Exception): String = when (e) {
        is FirebaseAuthUserCollisionException -> "Ese nombre de usuario ya está en uso. Elegí otro."
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil (mínimo 6 caracteres)."
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Usuario o contraseña incorrectos."
        else -> e.localizedMessage ?: "Error de conexión con el servidor. Intentá de nuevo."
    }

    companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_MATCHES = "matches"
        const val FIELD_USERNAME = "username"
        const val FIELD_APODO = "apodo"
        const val FIELD_HIGH_SCORE = "highScore"

        const val FIELD_MATCH_USER_ID = "userId"
        const val FIELD_MATCH_NICKNAME = "nicknameUser"
        const val FIELD_MATCH_TIMESTAMP = "timestamp"
        const val FIELD_MATCH_WINNER = "winner"
        const val FIELD_MATCH_WINNER_SCORE = "winnerScore"
        const val FIELD_MATCH_WINNER_USER_ID = "winnerUserId"

        private const val SYNTHETIC_DOMAIN = "guerradeplatillos.app"
        private val USERNAME_REGEX = Regex("^[a-z0-9_.-]{3,20}$")

        /**
         * Días consecutivos con partidas jugadas, contando hacia atrás desde hoy.
         * Si hoy todavía no jugó, la racha sigue viva si jugó ayer.
         */
        fun calcularRacha(diasJugados: Set<java.time.LocalDate>, hoy: java.time.LocalDate): Int {
            var dia = when {
                hoy in diasJugados -> hoy
                hoy.minusDays(1) in diasJugados -> hoy.minusDays(1)
                else -> return 0
            }
            var racha = 0
            while (dia in diasJugados) {
                racha++
                dia = dia.minusDays(1)
            }
            return racha
        }
    }
}

/** Estadísticas del jugador calculadas desde la colección `matches`. */
data class EstadisticasJugador(
    val partidasGanadas: Int = 0,
    val rachaDias: Int = 0,
)

data class RankingEntry(
    val id: String,
    val nickname: String,
    val score: Int,
    val timestampMillis: Long?,
    val isCurrentUser: Boolean = false,
)
