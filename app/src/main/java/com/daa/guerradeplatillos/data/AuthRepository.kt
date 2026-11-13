package com.daa.guerradeplatillos.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
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
        const val FIELD_USERNAME = "username"
        const val FIELD_APODO = "apodo"
        const val FIELD_HIGH_SCORE = "highScore"

        private const val SYNTHETIC_DOMAIN = "guerradeplatillos.app"
        private val USERNAME_REGEX = Regex("^[a-z0-9_.-]{3,20}$")
    }
}
