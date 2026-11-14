package com.daa.guerradeplatillos

import android.app.Application
import com.daa.guerradeplatillos.data.AuthRepository
import com.daa.guerradeplatillos.data.LunchTimeHistoryRepository
import com.daa.guerradeplatillos.data.SettingsRepository
import com.daa.guerradeplatillos.data.UserRepository
import com.daa.guerradeplatillos.data.local.AppDatabase
import com.daa.guerradeplatillos.util.SoundManager

/**
 * Application: crea los repositorios compartidos (inyección manual simple)
 * y precarga los sonidos.
 */
class GuerraDePlatillosApp : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var lunchTimeHistoryRepository: LunchTimeHistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        userRepository = UserRepository(this)
        authRepository = AuthRepository(userRepository)
        val db = AppDatabase.getInstance(this)
        lunchTimeHistoryRepository = LunchTimeHistoryRepository(db.lunchTimeDao(), authRepository)
        SoundManager.init(this) { settingsRepository.soundEnabled.value }
    }
}
