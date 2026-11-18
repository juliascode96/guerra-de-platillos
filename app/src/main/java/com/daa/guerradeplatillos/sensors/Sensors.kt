package com.daa.guerradeplatillos.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.sqrt

class ShakeDetector(private val context: Context, private val onShakeDetected: () -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Obtener el servicio de vibración de forma compatible con versiones nuevas y antiguas de Android
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Arreos para guardar las lecturas de los sensores
    private var gravityValues = FloatArray(3)
    private var geomagneticValues = FloatArray(3)
    private var gyroValues = FloatArray(3)

    private var hasAccelerometer = false
    private var hasMagnetometer = false

    private var lastUpdate: Long = 0

    companion object {
        // Constantes de configuración
        private val TIME_THRESHOLD = 100 // Tiempo mínimo entre lecturas en milisegundos
        private val SHAKE_INTERVAL = 1000 // Tiempo de espera entre detecciones de agitación para evitar bucles
        private val VIBRATION_DURATION = 300L // Duración de la vibración en milisegundos

        /**
         * VALORES DE SENSIBILIDAD RECOMENDADOS (Umbral de Fuerza G + Giroscopio):
         * - 10.0f a 12.0f: Muy sensible (Se activa con movimientos leves).
         * - 15.0f a 18.0f: Sensibilidad Media (Recomendado para juegos, evita falsos positivos al jugar normalmente).
         * - 22.0f o más: Baja sensibilidad (Requiere agitar el teléfono con mucha fuerza).
         */
        private const val SHAKE_THRESHOLD = 16.0f
    }

    private var lastShakeTime: Long = 0

    fun startListening() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastUpdate) > TIME_THRESHOLD) {
            lastUpdate = currentTime

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    gravityValues = event.values.clone()
                    hasAccelerometer = true
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    geomagneticValues = event.values.clone()
                    hasMagnetometer = true
                }
                Sensor.TYPE_GYROSCOPE -> {
                    gyroValues = event.values.clone()
                }
            }

            // Si tenemos acelerómetro y magnetómetro, calculamos la aceleración lineal real libre de gravedad
            if (hasAccelerometer && hasMagnetometer) {
                val r = FloatArray(9)
                val i = FloatArray(9)

                if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
                    val realAcceleration = FloatArray(3)
                    // Remueve vector de gravedad de la aceleración actual
                    realAcceleration[0] = gravityValues[0] - (r[2] * SensorManager.GRAVITY_EARTH)
                    realAcceleration[1] = gravityValues[1] - (r[5] * SensorManager.GRAVITY_EARTH)
                    realAcceleration[2] = gravityValues[2] - (r[8] * SensorManager.GRAVITY_EARTH)

                    // Magnitud de la aceleración lineal neta
                    val linearAccelerationMagnitude = sqrt(
                        realAcceleration[0] * realAcceleration[0] +
                                realAcceleration[1] * realAcceleration[1] +
                                realAcceleration[2] * realAcceleration[2]
                    )

                    // Magnitud de la rotación del giroscopio (Rad/s)
                    val gyroMagnitude = sqrt(
                        gyroValues[0] * gyroValues[0] +
                                gyroValues[1] * gyroValues[1] +
                                gyroValues[2] * gyroValues[2]
                    )

                    // Combinación combinada: Movimiento lineal fuerte + rotación brusca
                    val combinedForce = linearAccelerationMagnitude + gyroMagnitude

                    if (combinedForce > SHAKE_THRESHOLD) {
                        if (currentTime - lastShakeTime > SHAKE_INTERVAL) {
                            lastShakeTime = currentTime
                            triggerVibration()
                            onShakeDetected()
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_DURATION)
        }
    }
}