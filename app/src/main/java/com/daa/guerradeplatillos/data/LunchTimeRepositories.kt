package com.daa.guerradeplatillos.data

import android.content.Context
import com.daa.guerradeplatillos.model.Alimento
import com.daa.guerradeplatillos.model.DatoCurioso
import org.json.JSONArray
import org.json.JSONException

/** Carga el menú de alimentos desde assets/menu_alimentos_puntuado.txt (columnas separadas por tab). */
object AlimentoRepository {
    fun cargarAlimentos(context: Context): List<Alimento> {
        val lista = mutableListOf<Alimento>()
        context.assets.open("menu_alimentos_puntuado.txt").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val cols = line.split("\t")
                if (cols.size >= 6) {
                    val imagen = if (cols.size > 6) cols[6].trim() else ""
                    val nombreCorto = if (cols.size > 7) cols[7].trim() else cols[2].trim()
                    lista.add(
                        Alimento(
                            comida = cols[0].trim(),
                            tipo = cols[1].trim(),
                            opcion = cols[2].trim(),
                            nivelSaludable = cols[3].trim().toIntOrNull() ?: 0,
                            saciedad = cols[4].trim().toIntOrNull() ?: 0,
                            vitalidad = cols[5].trim().toIntOrNull() ?: 0,
                            imagen = imagen,
                            nombreCorto = nombreCorto.ifEmpty { cols[2].trim() },
                        )
                    )
                }
            }
        }
        return lista
    }
}

/**
 * Carga los datos curiosos desde assets/datos_curiosos.json.
 * El nombre del plato ("plato") coincide con [Alimento.opcion].
 */
object DatoCuriosoRepository {

    private const val ARCHIVO = "datos_curiosos.json"

    // Cache en memoria: el archivo es chico y no cambia durante la ejecución
    private var cache: Map<String, DatoCurioso>? = null

    /** Devuelve los datos indexados por nombre de plato. Nunca lanza: ante un error devuelve un mapa vacío. */
    fun cargarDatos(context: Context): Map<String, DatoCurioso> {
        cache?.let { return it }

        val datos = try {
            val json = context.assets.open(ARCHIVO).bufferedReader(Charsets.UTF_8).use { it.readText() }
            parsear(json)
        } catch (e: java.io.IOException) {
            emptyMap()
        }
        cache = datos
        return datos
    }

    /** Parsea el contenido JSON. Ignora entradas incompletas. */
    fun parsear(json: String): Map<String, DatoCurioso> {
        return try {
            val array = JSONArray(json)
            val mapa = LinkedHashMap<String, DatoCurioso>()
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val plato = item.optString("plato").trim()
                val dato = item.optString("dato").trim()
                if (plato.isNotEmpty() && dato.isNotEmpty()) {
                    mapa[plato] = DatoCurioso(plato, dato)
                }
            }
            mapa
        } catch (e: JSONException) {
            emptyMap()
        }
    }
}
