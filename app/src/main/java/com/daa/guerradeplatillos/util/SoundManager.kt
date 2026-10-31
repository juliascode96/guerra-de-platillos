package com.daa.guerradeplatillos.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.daa.guerradeplatillos.R

/**
 * Efectos de sonido de la app (SoundPool). Respeta la preferencia de sonido
 * a través de [isEnabled], que se configura en [init].
 */
object SoundManager {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    private var appContext: Context? = null
    private var isEnabled: () -> Boolean = { true }

    private val sounds = intArrayOf(
        R.raw.sfx_btn_bubbly,
        R.raw.sfx_btn_play,
        R.raw.sfx_game_finish,
        R.raw.sfx_result_positive,
        R.raw.sfx_result_negative,
        R.raw.sfx_urgency_timer,
        R.raw.sfx_error,
    )

    fun init(context: Context, isEnabled: () -> Boolean) {
        this.appContext = context.applicationContext
        this.isEnabled = isEnabled
        load()
    }

    private fun load() {
        if (soundPool != null) return
        val context = appContext ?: return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
            .also { pool -> sounds.forEach { soundMap[it] = pool.load(context, it, 1) } }
    }

    private fun play(@RawRes resId: Int) {
        if (!isEnabled()) return
        load()
        val soundId = soundMap[resId] ?: return
        if (soundId != 0) soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun playBubbly() = play(R.raw.sfx_btn_bubbly)
    fun playStartGame() = play(R.raw.sfx_btn_play)
    fun playGameFinish() = play(R.raw.sfx_game_finish)
    fun playResultPositive() = play(R.raw.sfx_result_positive)
    fun playResultNegative() = play(R.raw.sfx_result_negative)
    fun playUrgency() = play(R.raw.sfx_urgency_timer)
    fun playError() = play(R.raw.sfx_error)

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
