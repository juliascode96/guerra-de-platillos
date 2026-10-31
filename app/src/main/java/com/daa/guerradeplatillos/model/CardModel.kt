package com.daa.guerradeplatillos.model

data class CardModel(
    val id: Int,
    val name: String,
    val description: String,
    val healthPoints: Int,    // Puntos de Saludable (ej. 0 a 2)
    val satietyPoints: Int,   // Puntos de Saciedad
    val vitalityPoints: Int,  // Puntos de Vitalidad
    val cardNumber: Int       // El número grande en la esquina (ej. 2, 4, 5, 7)
)
