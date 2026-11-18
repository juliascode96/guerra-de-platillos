package com.daa.guerradeplatillos.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Partida de Guerra de Platillos en curso (1 fila por usuario).
 * Se actualiza con @Upsert para no disparar ON DELETE CASCADE sobre [GuerraManoCartaEntity].
 */
@Entity(tableName = "guerra_partida_en_curso")
data class GuerraPartidaEntity(
    @PrimaryKey val userId: String,
    val playerScore: Int,
    val rivalScore: Int,
    val roundsPlayed: Int,
    val areCardsRevealed: Boolean,
    val playerPlayedCardId: String?,
    val rivalPlayedCardId: String?,
    val lastRoundWinner: String?,
    val lastRoundPlayerGain: Int,
    val lastRoundRivalGain: Int,
    val lastRoundPlayerWins: Int,
    val lastRoundRivalWins: Int,
    val updatedAt: Long,
)

/** Carta que aún queda en la mano del jugador o del rival en una partida en curso. */
@Entity(
    tableName = "guerra_mano_cartas",
    foreignKeys = [
        ForeignKey(
            entity = GuerraPartidaEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"])],
)
data class GuerraManoCartaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    /** "PLAYER" o "RIVAL" */
    val owner: String,
    val cardId: String,
) {
    companion object {
        const val OWNER_PLAYER = "PLAYER"
        const val OWNER_RIVAL = "RIVAL"
    }
}

/** Partida de Guerra de Platillos con las cartas restantes en ambas manos. */
data class GuerraPartidaConCartas(
    @Embedded val partida: GuerraPartidaEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId",
    )
    val cartas: List<GuerraManoCartaEntity>,
)

@Dao
interface GuerraPartidaDao {

    @Upsert
    suspend fun upsertPartida(partida: GuerraPartidaEntity)

    @Insert
    suspend fun insertarCartas(cartas: List<GuerraManoCartaEntity>)

    @Query("DELETE FROM guerra_mano_cartas WHERE userId = :userId")
    suspend fun borrarCartasUsuario(userId: String)

    @Transaction
    suspend fun guardarPartidaConManos(
        partida: GuerraPartidaEntity,
        cartas: List<GuerraManoCartaEntity>,
    ) {
        upsertPartida(partida)
        borrarCartasUsuario(partida.userId)
        if (cartas.isNotEmpty()) {
            insertarCartas(cartas)
        }
    }

    @Transaction
    @Query("SELECT * FROM guerra_partida_en_curso WHERE userId = :userId")
    suspend fun obtenerPartida(userId: String): GuerraPartidaConCartas?

    @Query("SELECT EXISTS(SELECT 1 FROM guerra_partida_en_curso WHERE userId = :userId)")
    fun observarHayPartida(userId: String): Flow<Boolean>

    @Query("DELETE FROM guerra_partida_en_curso WHERE userId = :userId")
    suspend fun descartar(userId: String)
}
