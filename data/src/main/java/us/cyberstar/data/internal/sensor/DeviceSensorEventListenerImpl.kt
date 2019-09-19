package us.cyberstar.data.internal.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.common.utils.timeNow
import us.cyberstar.data.external.sensor.DeviceSensorEventListener
import javax.inject.Inject

internal class DeviceSensorEventListenerImpl @Inject constructor(
    private val context: Context
) : DeviceSensorEventListener {
    val sensorDelayUpdate = 10
    var timeStamp = 0L

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.run {
            if (timeNow() - timeStamp >= sensorDelayUpdate) {
                timeStamp = timeNow()
                var gyro = Vector3()
                var acceleration = Vector3()
                var magnetometer = Vector3()
                val vectorTemp = Vector3(values[0], values[1], values[2])
                when (sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> acceleration = vectorTemp
                    Sensor.TYPE_MAGNETIC_FIELD -> magnetometer = vectorTemp
                    Sensor.TYPE_GYROSCOPE -> gyro = vectorTemp
                }
                val sensorData = DeviceSensorEventListener.SensorData(gyro, acceleration, magnetometer)
                sensorDataListener?.onSensorDataUpdated(sensorData)
            }
        }
    }

    var sensorDataListener: DeviceSensorEventListener.SensorDataListener? = null
    override fun registerListener(sensorDataListener: DeviceSensorEventListener.SensorDataListener) {
        Timber.d("registerListener")
        this.sensorDataListener = sensorDataListener
        registerListener(Sensor.TYPE_ACCELEROMETER)
        registerListener(Sensor.TYPE_MAGNETIC_FIELD)
        registerListener(Sensor.TYPE_GYROSCOPE)
    }

    override fun unRegisterListener() {
        Timber.d("unRegisterListener")
        unRegisterListener(Sensor.TYPE_ACCELEROMETER)
        unRegisterListener(Sensor.TYPE_MAGNETIC_FIELD)
        unRegisterListener(Sensor.TYPE_GYROSCOPE)
        sensorDataListener = null
    }

    private fun registerListener(type: Int) {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(type),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun unRegisterListener(type: Int) {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(type))
    }
}