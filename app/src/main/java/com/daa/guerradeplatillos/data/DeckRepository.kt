package com.daa.guerradeplatillos.data

import android.content.Context
import com.daa.guerradeplatillos.model.CardModel
import org.json.JSONArray

/** Carga la baraja completa de Guerra de Platillos desde assets/cartas.json. */
object DeckRepository {

    private const val DECK_ASSET_FILE = "cartas.json"

    /** Devuelve todas las cartas. Nunca lanza: ante un error devuelve una lista vacía. */
    fun cargarBaraja(context: Context): List<CardModel> {
        return try {
            val jsonText = context.assets.open(DECK_ASSET_FILE)
                .bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(jsonText)
            List(jsonArray.length()) { index ->
                val obj = jsonArray.getJSONObject(index)
                CardModel(
                    id = obj.getString("id"),
                    name = obj.getString("nombre"),
                    energyPoints = obj.getInt("energia"),
                    satietyPoints = obj.getInt("saciedad"),
                    vitalityPoints = obj.getInt("vitalidad"),
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
