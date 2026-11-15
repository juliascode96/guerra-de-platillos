package com.daa.guerradeplatillos.data

import com.daa.guerradeplatillos.data.local.GuerraManoCartaEntity
import com.daa.guerradeplatillos.data.local.GuerraPartidaDao
import com.daa.guerradeplatillos.data.local.GuerraPartidaEntity
import com.daa.guerradeplatillos.model.CardModel
import com.daa.guerradeplatillos.viewmodel.RoundResult
import com.daa.guerradeplatillos.viewmodel.RoundWinner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Estado recuperado desde Room para continuar una partida de Guerra de Platillos. */
data class GuerraSavedState(
    val playerHand: List<CardModel>,
    val rivalHand: List<CardModel>,
    val playerPlayedCard: CardModel?,
    val rivalPlayedCard: CardModel?,
    val playerScore: Int,
    val rivalScore: Int,
    val roundsPlayed: Int,
    val areCardsRevealed: Boolean,
    val lastRoundResult: RoundResult?,
)

/**
 * Repositorio para persistir la partida en curso de Guerra de Platillos en Room,
 * asociada al usuario logueado en [AuthRepository].
 */
class GuerraGameRepository(
    private val dao: GuerraPartidaDao,
    private val authRepository: AuthRepository,
) {
    private val mutex = Mutex()

    private fun currentUserId(): String = authRepository.currentUserUid.value ?: "local"

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observarHayPartida(): Flow<Boolean> =
        authRepository.currentUserUid.flatMapLatest { uid ->
            dao.observarHayPartida(uid ?: "local")
        }

    suspend fun guardarPartida(
        playerHand: List<CardModel>,
        rivalHand: List<CardModel>,
        playerPlayedCard: CardModel?,
        rivalPlayedCard: CardModel?,
        playerScore: Int,
        rivalScore: Int,
        roundsPlayed: Int,
        areCardsRevealed: Boolean,
        lastRoundResult: RoundResult?,
    ) {
        val uid = currentUserId()
        val entity = GuerraPartidaEntity(
            userId = uid,
            playerScore = playerScore,
            rivalScore = rivalScore,
            roundsPlayed = roundsPlayed,
            areCardsRevealed = areCardsRevealed,
            playerPlayedCardId = playerPlayedCard?.id,
            rivalPlayedCardId = rivalPlayedCard?.id,
            lastRoundWinner = lastRoundResult?.winner?.name,
            lastRoundPlayerGain = lastRoundResult?.playerPointsGained ?: 0,
            lastRoundRivalGain = lastRoundResult?.rivalPointsGained ?: 0,
            lastRoundPlayerWins = lastRoundResult?.playerCategoriesWon ?: 0,
            lastRoundRivalWins = lastRoundResult?.rivalCategoriesWon ?: 0,
            updatedAt = System.currentTimeMillis(),
        )
        val mano = playerHand.map {
            GuerraManoCartaEntity(userId = uid, owner = GuerraManoCartaEntity.OWNER_PLAYER, cardId = it.id)
        } + rivalHand.map {
            GuerraManoCartaEntity(userId = uid, owner = GuerraManoCartaEntity.OWNER_RIVAL, cardId = it.id)
        }

        withContext(NonCancellable) {
            mutex.withLock {
                dao.guardarPartidaConManos(entity, mano)
            }
        }
    }

    suspend fun cargarPartida(deckById: Map<String, CardModel>): GuerraSavedState? {
        val uid = currentUserId()
        return mutex.withLock {
            val conCartas = try {
                dao.obtenerPartida(uid)
            } catch (_: Exception) {
                dao.descartar(uid)
                return@withLock null
            } ?: return@withLock null

            val p = conCartas.partida
            val playerHandIds = conCartas.cartas
                .filter { it.owner == GuerraManoCartaEntity.OWNER_PLAYER }
                .map { it.cardId }
            val rivalHandIds = conCartas.cartas
                .filter { it.owner == GuerraManoCartaEntity.OWNER_RIVAL }
                .map { it.cardId }

            val playerHand = playerHandIds.mapNotNull { deckById[it] }
            val rivalHand = rivalHandIds.mapNotNull { deckById[it] }
            val playerPlayed = p.playerPlayedCardId?.let { deckById[it] }
            val rivalPlayed = p.rivalPlayedCardId?.let { deckById[it] }

            val invalido = playerHand.size != playerHandIds.size ||
                rivalHand.size != rivalHandIds.size ||
                (p.playerPlayedCardId != null && playerPlayed == null) ||
                (p.rivalPlayedCardId != null && rivalPlayed == null)

            if (invalido) {
                dao.descartar(uid)
                return@withLock null
            }

            val roundResult = p.lastRoundWinner?.let { winnerName ->
                val winner = runCatching { RoundWinner.valueOf(winnerName) }.getOrNull() ?: return@let null
                RoundResult(
                    roundNumber = p.roundsPlayed,
                    winner = winner,
                    playerPointsGained = p.lastRoundPlayerGain,
                    rivalPointsGained = p.lastRoundRivalGain,
                    playerCategoriesWon = p.lastRoundPlayerWins,
                    rivalCategoriesWon = p.lastRoundRivalWins,
                )
            }

            GuerraSavedState(
                playerHand = playerHand,
                rivalHand = rivalHand,
                playerPlayedCard = playerPlayed,
                rivalPlayedCard = rivalPlayed,
                playerScore = p.playerScore,
                rivalScore = p.rivalScore,
                roundsPlayed = p.roundsPlayed,
                areCardsRevealed = p.areCardsRevealed,
                lastRoundResult = roundResult,
            )
        }
    }

    suspend fun descartarPartida() {
        val uid = currentUserId()
        withContext(NonCancellable) {
            mutex.withLock {
                dao.descartar(uid)
            }
        }
    }
}
