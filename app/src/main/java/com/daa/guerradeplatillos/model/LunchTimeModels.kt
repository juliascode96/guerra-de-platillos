package com.daa.guerradeplatillos.model

import java.util.Calendar

/** Alimento del menú de Lunch Time (cargado desde assets/menu_alimentos_puntuado.txt). */
data class Alimento(
    val comida: String,
    val tipo: String,
    val opcion: String,
    val nivelSaludable: Int,
    val saciedad: Int,
    val vitalidad: Int,
    val imagen: String = "",
    val nombreCorto: String = "",
)

/** Dato curioso asociado a un plato del menú. [plato] coincide con [Alimento.opcion]. */
data class DatoCurioso(
    val plato: String,
    val dato: String,
)

/** Tipos de comida disponibles en Lunch Time y su rango horario recomendado. */
enum class Comida(val titulo: String, val horario: String) {
    DESAYUNO("Desayuno", "6-10hs"),
    ALMUERZO("Almuerzo", "11-14hs"),
    MERIENDA("Merienda", "15-18hs"),
    CENA("Cena", "19-23hs");

    companion object {
        fun obtenerPorHora(hora: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Comida {
            return when (hora) {
                in 6..10 -> DESAYUNO
                in 11..14 -> ALMUERZO
                in 15..18 -> MERIENDA
                else -> CENA
            }
        }

        fun desdeTitulo(titulo: String?): Comida? {
            return entries.firstOrNull { it.titulo.equals(titulo, ignoreCase = true) }
        }
    }
}

/** Constantes y reglas centrales de Lunch Time. */
object ReglasJuego {
    const val MAX_ALIMENTOS = 3
    const val TIEMPO_TOTAL_MS = 20000L
    const val INTERVALO_TIMER_MS = 1000L

    // Cada alimento otorga como máximo 2 salud + 2 saciedad + 2 vitalidad = 6 puntos.
    const val MAX_PUNTOS_POR_ALIMENTO = 6

    // Máximo por categoría (salud / saciedad / vitalidad): 2 puntos por cada uno de los 3 alimentos
    const val MAX_PUNTOS_POR_CATEGORIA = MAX_ALIMENTOS * 2 // 6
    const val MAXIMO_POSIBLE = MAX_ALIMENTOS * MAX_PUNTOS_POR_ALIMENTO // 18
    const val UMBRAL_VICTORIA = 0.60 // 60%
}

/** Resultado obtenido al finalizar una partida de Lunch Time. */
data class ResultadoPartida(
    val saludGanada: Int,
    val saciedadGanada: Int,
    val vitalidadGanada: Int,
    val puntajeTotal: Int,
    val alimentosElegidos: List<Alimento> = emptyList(),
    val maximoPosible: Int = ReglasJuego.MAXIMO_POSIBLE,
    val porcentajeObtenido: Double =
        if (maximoPosible > 0) (puntajeTotal.toDouble() / maximoPosible) * 100 else 0.0,
    val esVictoria: Boolean = puntajeTotal >= (maximoPosible * ReglasJuego.UMBRAL_VICTORIA),
)
