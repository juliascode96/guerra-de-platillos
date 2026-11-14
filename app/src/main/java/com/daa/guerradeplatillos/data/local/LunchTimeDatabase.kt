package com.daa.guerradeplatillos.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction

/** Partida jugada en Lunch Time almacenada en SQLite vía Room. */
@Entity(
    tableName = "lunch_time_partidas",
    indices = [Index(value = ["userId", "fechaDia"])],
)
data class LunchTimePartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    /** Fecha local en formato ISO "YYYY-MM-DD" para agrupar y filtrar por día. */
    val fechaDia: String,
    val timestampMs: Long,
    val comida: String,
    val salud: Int,
    val saciedad: Int,
    val vitalidad: Int,
    val puntajeTotal: Int,
)

/** Plato elegido dentro de una partida de Lunch Time (relación 1:N). */
@Entity(
    tableName = "lunch_time_platos",
    foreignKeys = [
        ForeignKey(
            entity = LunchTimePartidaEntity::class,
            parentColumns = ["id"],
            childColumns = ["partidaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["partidaId"])],
)
data class LunchTimePlatoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val partidaId: Long,
    val nombrePlato: String,
)

/** Partida con la lista de platillos elegidos. */
data class PartidaConPlatos(
    @Embedded val partida: LunchTimePartidaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "partidaId",
    )
    val platos: List<LunchTimePlatoEntity>,
)

@Dao
interface LunchTimeDao {

    @Insert
    suspend fun insertarPartida(partida: LunchTimePartidaEntity): Long

    @Insert
    suspend fun insertarPlatos(platos: List<LunchTimePlatoEntity>)

    @Transaction
    suspend fun guardarPartidaConPlatos(
        partida: LunchTimePartidaEntity,
        nombresPlatos: List<String>,
    ) {
        val partidaId = insertarPartida(partida)
        if (nombresPlatos.isNotEmpty()) {
            insertarPlatos(
                nombresPlatos.map { nombre ->
                    LunchTimePlatoEntity(partidaId = partidaId, nombrePlato = nombre)
                }
            )
        }
    }

    @Transaction
    @Query(
        "SELECT * FROM lunch_time_partidas " +
            "WHERE userId = :userId AND fechaDia BETWEEN :desde AND :hasta " +
            "ORDER BY timestampMs ASC"
    )
    suspend fun obtenerPartidasRango(
        userId: String,
        desde: String,
        hasta: String,
    ): List<PartidaConPlatos>

    @Query(
        "SELECT * FROM lunch_time_partidas " +
            "WHERE userId = :userId " +
            "ORDER BY fechaDia DESC, timestampMs DESC"
    )
    suspend fun obtenerTodasPartidas(userId: String): List<LunchTimePartidaEntity>
}

@Database(
    entities = [LunchTimePartidaEntity::class, LunchTimePlatoEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lunchTimeDao(): LunchTimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codigo_verde.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
