package com.daa.guerradeplatillos.model

/** Datos del usuario / avatar (mismo modelo que en Código Verde). */
data class Avatar(
    val nombre: String,
    val sexo: String,
    val edad: Int,
) {
    companion object {
        const val SEXO_MASCULINO = "Masculino"
        const val SEXO_FEMENINO = "Femenino"
    }
}
