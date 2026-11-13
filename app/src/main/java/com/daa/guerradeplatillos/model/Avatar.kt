package com.daa.guerradeplatillos.model

/** Datos del usuario / avatar guardados localmente. */
data class Avatar(
    val nombre: String,
    val sexo: String,
    val edad: Int,
    val avatarId: Int = DEFAULT_AVATAR_ID,
) {
    companion object {
        const val SEXO_MASCULINO = "Masculino"
        const val SEXO_FEMENINO = "Femenino"
        const val DEFAULT_AVATAR_ID = 1
    }
}
