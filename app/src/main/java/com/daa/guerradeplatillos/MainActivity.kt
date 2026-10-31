package com.daa.guerradeplatillos

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daa.guerradeplatillos.ui.navigation.AppNavHost
import com.daa.guerradeplatillos.ui.theme.GuerraDePlatillosTheme
import com.daa.guerradeplatillos.util.SoundManager

/**
 * Única Activity de la app. Toda la UI es Jetpack Compose y la navegación
 * entre pantallas se maneja en [AppNavHost].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app es siempre oscura: íconos claros en las barras del sistema.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        val settings = (application as GuerraDePlatillosApp).settingsRepository

        setContent {
            val palette by settings.palette.collectAsStateWithLifecycle()
            GuerraDePlatillosTheme(palette = palette) {
                AppNavHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) SoundManager.release()
    }
}
