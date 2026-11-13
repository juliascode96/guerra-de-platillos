package com.daa.guerradeplatillos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daa.guerradeplatillos.data.AlimentoRepository
import com.daa.guerradeplatillos.data.DatoCuriosoRepository
import com.daa.guerradeplatillos.model.Alimento
import com.daa.guerradeplatillos.model.Comida
import com.daa.guerradeplatillos.model.DatoCurioso
import com.daa.guerradeplatillos.model.ReglasJuego
import com.daa.guerradeplatillos.model.ResultadoPartida
import com.daa.guerradeplatillos.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Eventos únicos de la UI de Lunch Time. */
sealed interface LunchTimeEvent {
    data class ShowToast(val mensaje: String) : LunchTimeEvent

    /** La selección de menú fue confirmada: navegar al juego. */
    data object IrAlJuego : LunchTimeEvent

    /** La partida terminó (tiempo agotado o menú confirmado): navegar al resultado. */
    data object IrAlResultado : LunchTimeEvent
}

/** Puntajes de la pantalla de resultados, acotados a 0..[ReglasJuego.MAX_PUNTOS_POR_CATEGORIA]. */
data class PuntajeUi(
    val salud: Int,
    val saciedad: Int,
    val vitalidad: Int,
    val total: Int,
    val maximo: Int,
    val porcentaje: Double,
    val esVictoria: Boolean,
)

/**
 * ViewModel compartido por todo el flujo de Lunch Time (opciones → juego → resultado).
 * Se scopea a la Activity para que el estado sobreviva entre pantallas.
 *
 * Gestiona: modo automático/manual, comida de la partida, bandeja, temporizador
 * (se pausa/reanuda con el ciclo de vida) y el cálculo del resultado.
 */
class LunchTimeViewModel(application: Application) : AndroidViewModel(application) {

    // --- Selección de menú ---
    private val _modoAutomatico = MutableStateFlow(true)
    val modoAutomatico: StateFlow<Boolean> = _modoAutomatico.asStateFlow()

    private val _comidaElegida = MutableStateFlow<Comida?>(null)
    val comidaElegida: StateFlow<Comida?> = _comidaElegida.asStateFlow()

    private val _comidaHorarioActual = MutableStateFlow(Comida.obtenerPorHora())
    val comidaHorarioActual: StateFlow<Comida> = _comidaHorarioActual.asStateFlow()

    // --- Partida ---
    private val _comidaJuego = MutableStateFlow(Comida.DESAYUNO)
    val comidaJuego: StateFlow<Comida> = _comidaJuego.asStateFlow()

    private val _alimentos = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentos: StateFlow<List<Alimento>> = _alimentos.asStateFlow()

    private val _bandeja = MutableStateFlow<List<Alimento>>(emptyList())
    val bandeja: StateFlow<List<Alimento>> = _bandeja.asStateFlow()

    private val _tiempoRestanteMs = MutableStateFlow(ReglasJuego.TIEMPO_TOTAL_MS)
    val tiempoRestanteMs: StateFlow<Long> = _tiempoRestanteMs.asStateFlow()

    private val _partidaTerminada = MutableStateFlow(true)
    val partidaTerminada: StateFlow<Boolean> = _partidaTerminada.asStateFlow()

    // --- Resultado ---
    private val _resultado = MutableStateFlow<ResultadoPartida?>(null)
    val resultado: StateFlow<ResultadoPartida?> = _resultado.asStateFlow()

    private val _datoCurioso = MutableStateFlow<DatoCurioso?>(null)
    val datoCurioso: StateFlow<DatoCurioso?> = _datoCurioso.asStateFlow()

    private val _eventos = Channel<LunchTimeEvent>(Channel.BUFFERED)
    val eventos: Flow<LunchTimeEvent> = _eventos.receiveAsFlow()

    private var timerJob: Job? = null

    // =============================================
    //  Pantalla de opciones
    // =============================================

    fun actualizarHorarioActual() {
        _comidaHorarioActual.value = Comida.obtenerPorHora()
    }

    fun seleccionarModoAutomatico() {
        _modoAutomatico.value = true
        SoundManager.playBubbly()
    }

    fun seleccionarModoManual() {
        _modoAutomatico.value = false
        SoundManager.playBubbly()
    }

    fun seleccionarComida(comida: Comida) {
        _modoAutomatico.value = false
        _comidaElegida.value = comida
        SoundManager.playBubbly()
    }

    /** Valida la selección, prepara la partida y avisa a la UI para ir al juego. */
    fun confirmarSeleccionMenu() {
        val comidaFinal = if (_modoAutomatico.value) {
            Comida.obtenerPorHora().also { _comidaHorarioActual.value = it }
        } else {
            _comidaElegida.value
        }

        if (comidaFinal == null) {
            _eventos.trySend(LunchTimeEvent.ShowToast("Elegí una comida para continuar"))
            return
        }
        iniciarPartida(comidaFinal)
        _eventos.trySend(LunchTimeEvent.IrAlJuego)
    }

    // =============================================
    //  Juego
    // =============================================

    private fun iniciarPartida(comida: Comida) {
        pausarTimer()
        _comidaJuego.value = comida
        _alimentos.value = AlimentoRepository.cargarAlimentos(getApplication())
            .filter { it.comida.equals(comida.titulo, ignoreCase = true) }
        _bandeja.value = emptyList()
        _tiempoRestanteMs.value = ReglasJuego.TIEMPO_TOTAL_MS
        _resultado.value = null
        _datoCurioso.value = null
        _partidaTerminada.value = false
    }

    /** Inicia o reanuda el temporizador con el tiempo restante. */
    fun reanudarTimer() {
        if (_partidaTerminada.value || timerJob?.isActive == true) return

        if (_tiempoRestanteMs.value <= 0) {
            procesarFinal()
            return
        }

        timerJob = viewModelScope.launch {
            while (_tiempoRestanteMs.value > 0) {
                delay(ReglasJuego.INTERVALO_TIMER_MS)
                val restante = (_tiempoRestanteMs.value - ReglasJuego.INTERVALO_TIMER_MS).coerceAtLeast(0L)
                _tiempoRestanteMs.value = restante
                val segundos = (restante / 1000).toInt()
                if (segundos in 1..5) SoundManager.playUrgency()
            }
            procesarFinal()
        }
    }

    /** Pausa el temporizador cuando la pantalla pasa a segundo plano. */
    fun pausarTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /** Agrega o quita un alimento de la bandeja. */
    fun alternarAlimento(alimento: Alimento) {
        if (_partidaTerminada.value) return

        val lista = _bandeja.value.toMutableList()
        when {
            lista.contains(alimento) -> {
                lista.remove(alimento)
                _bandeja.value = lista
                SoundManager.playBubbly()
            }
            lista.size < ReglasJuego.MAX_ALIMENTOS -> {
                lista.add(alimento)
                _bandeja.value = lista
                SoundManager.playBubbly()
            }
            else -> {
                SoundManager.playError()
                _eventos.trySend(LunchTimeEvent.ShowToast("Máximo ${ReglasJuego.MAX_ALIMENTOS} alimentos"))
            }
        }
    }

    /** Confirma la bandeja y finaliza la partida. */
    fun confirmarBandeja() {
        if (_partidaTerminada.value) return
        if (_bandeja.value.isEmpty()) {
            _eventos.trySend(LunchTimeEvent.ShowToast("Elegí al menos un alimento"))
            return
        }
        procesarFinal()
    }

    /** Cancela la partida anticipadamente (por ejemplo, si el usuario sale con la X). */
    fun cancelarPartida() {
        _partidaTerminada.value = true
        pausarTimer()
    }

    private fun procesarFinal() {
        if (_partidaTerminada.value) return
        _partidaTerminada.value = true
        pausarTimer()

        val elegidos = _bandeja.value
        val salud = elegidos.sumOf { it.nivelSaludable }
        val saciedad = elegidos.sumOf { it.saciedad }
        val vitalidad = elegidos.sumOf { it.vitalidad }

        _resultado.value = ResultadoPartida(
            saludGanada = salud,
            saciedadGanada = saciedad,
            vitalidadGanada = vitalidad,
            puntajeTotal = salud + saciedad + vitalidad,
            alimentosElegidos = elegidos,
        )
        _datoCurioso.value = elegirDatoCurioso(
            elegidos,
            DatoCuriosoRepository.cargarDatos(getApplication()),
        )
        _eventos.trySend(LunchTimeEvent.IrAlResultado)
    }

    // =============================================
    //  Resultado
    // =============================================

    /** Convierte el resultado en puntajes listos para dibujar. */
    fun puntajeUi(res: ResultadoPartida): PuntajeUi {
        val maxCategoria = ReglasJuego.MAX_PUNTOS_POR_CATEGORIA
        val salud = res.saludGanada.coerceIn(0, maxCategoria)
        val saciedad = res.saciedadGanada.coerceIn(0, maxCategoria)
        val vitalidad = res.vitalidadGanada.coerceIn(0, maxCategoria)
        val total = salud + saciedad + vitalidad
        val maximo = res.maximoPosible
        val porcentaje = if (maximo > 0) (total.toDouble() / maximo) * 100 else 0.0
        return PuntajeUi(
            salud = salud,
            saciedad = saciedad,
            vitalidad = vitalidad,
            total = total,
            maximo = maximo,
            porcentaje = porcentaje,
            esVictoria = res.esVictoria,
        )
    }

    override fun onCleared() {
        super.onCleared()
        pausarTimer()
    }

    companion object {
        /**
         * Elige al azar un dato curioso entre los alimentos elegidos.
         * Ignora los alimentos sin dato. Devuelve null si no hay ninguno disponible.
         */
        fun elegirDatoCurioso(
            alimentos: List<Alimento>,
            datos: Map<String, DatoCurioso>,
            random: Random = Random.Default,
        ): DatoCurioso? {
            return alimentos.mapNotNull { datos[it.opcion] }.randomOrNull(random)
        }
    }
}
