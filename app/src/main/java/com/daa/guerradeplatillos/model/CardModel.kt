package com.daa.guerradeplatillos.model

/**
 * Carta de platillo para Guerra de Platillos.
 * Sus tres categorías se comparan en cada ronda contra la carta del rival.
 */
data class CardModel(
    val id: String,
    val name: String,
    val energyPoints: Int,
    val satietyPoints: Int,
    val vitalityPoints: Int,
)
