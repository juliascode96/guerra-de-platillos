package com.daa.guerradeplatillos.data

import com.daa.guerradeplatillos.data.local.LunchTimeDao
import com.daa.guerradeplatillos.data.local.LunchTimePartidaEntity
import com.daa.guerradeplatillos.data.local.PartidaConPlatos
import com.daa.guerradeplatillos.model.ReglasJuego
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class NivelDia {
    SIN_DATOS,
    ROJO,
    AMARILLO,
    VERDE,
}

data class DiaSemanaEstado(
    val fecha: LocalDate,
    val etiqueta: String,
    val nivel: NivelDia,
    val partidas: List<PartidaConPlatos>,
)

data class ResumenLunchTimeUsuario(
    val diasSinUltraprocesados: Int = 0,
    val diasSemana: List<DiaSemanaEstado> = emptyList(),
)

/**
 * Repositorio para guardar partidas de Lunch Time en Room y calcular:
 * - "Días sin ultraprocesados" (racha de días consecutivos con >= 4/6 en las 3 categorías).
 * - Estado diario de la semana calendario actual (lunes a domingo) según el puntaje mínimo del día.
 */
class LunchTimeHistoryRepository(
    private val dao: LunchTimeDao,
    private val authRepository: AuthRepository,
) {
    private fun currentUserId(): String = authRepository.currentUserUid.value ?: "local"

    suspend fun registrarPartida(
        comida: String,
        salud: Int,
        saciedad: Int,
        vitalidad: Int,
        nombresPlatos: List<String>,
        fecha: LocalDate = LocalDate.now(),
        timestampMs: Long = System.currentTimeMillis(),
    ) {
        val maxCat = ReglasJuego.MAX_PUNTOS_POR_CATEGORIA
        val saludAcotada = salud.coerceIn(0, maxCat)
        val saciedadAcotada = saciedad.coerceIn(0, maxCat)
        val vitalidadAcotada = vitalidad.coerceIn(0, maxCat)
        val total = saludAcotada + saciedadAcotada + vitalidadAcotada

        dao.guardarPartidaConPlatos(
            partida = LunchTimePartidaEntity(
                userId = currentUserId(),
                fechaDia = fecha.toString(),
                timestampMs = timestampMs,
                comida = comida,
                salud = saludAcotada,
                saciedad = saciedadAcotada,
                vitalidad = vitalidadAcotada,
                puntajeTotal = total,
            ),
            nombresPlatos = nombresPlatos,
        )
    }

    suspend fun obtenerResumenSemanal(hoy: LocalDate = LocalDate.now()): ResumenLunchTimeUsuario {
        val uid = currentUserId()
        val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val domingo = lunes.plusDays(6)

        val partidasSemana = dao.obtenerPartidasRango(uid, lunes.toString(), domingo.toString())
        val porFecha = partidasSemana.groupBy { it.partida.fechaDia }

        val etiquetas = listOf("L", "M", "M", "J", "V", "S", "D")
        val diasSemana = (0L..6L).map { offset ->
            val fecha = lunes.plusDays(offset)
            val partidasDia = porFecha[fecha.toString()].orEmpty()
            val nivel = if (partidasDia.isEmpty()) {
                NivelDia.SIN_DATOS
            } else {
                val minimo = partidasDia.minOf { it.partida.puntajeTotal }
                when {
                    minimo < 6 -> NivelDia.ROJO
                    minimo <= 12 -> NivelDia.AMARILLO
                    else -> NivelDia.VERDE
                }
            }
            DiaSemanaEstado(
                fecha = fecha,
                etiqueta = etiquetas[offset.toInt()],
                nivel = nivel,
                partidas = partidasDia,
            )
        }

        val todas = dao.obtenerTodasPartidas(uid)
        val racha = calcularDiasSinUltraprocesados(todas, hoy)

        return ResumenLunchTimeUsuario(
            diasSinUltraprocesados = racha,
            diasSemana = diasSemana,
        )
    }

    companion object {
        private const val MIN_CATEGORIA_SALUDABLE = 4

        /**
         * Cuenta días consecutivos (hasta hoy o ayer si hoy aún no jugó) en los que
         * todas las partidas del día tuvieron >= 4/6 en salud, saciedad y vitalidad.
         */
        fun calcularDiasSinUltraprocesados(
            partidas: List<LunchTimePartidaEntity>,
            hoy: LocalDate,
        ): Int {
            if (partidas.isEmpty()) return 0

            val porDia = partidas.groupBy { LocalDate.parse(it.fechaDia) }
            var dia = when {
                hoy in porDia -> hoy
                hoy.minusDays(1) in porDia -> hoy.minusDays(1)
                else -> return 0
            }

            var racha = 0
            while (true) {
                val partidasDia = porDia[dia] ?: break
                val cumpleDia = partidasDia.all {
                    it.salud >= MIN_CATEGORIA_SALUDABLE &&
                        it.saciedad >= MIN_CATEGORIA_SALUDABLE &&
                        it.vitalidad >= MIN_CATEGORIA_SALUDABLE
                }
                if (!cumpleDia) break
                racha++
                dia = dia.minusDays(1)
            }
            return racha
        }
    }
}
