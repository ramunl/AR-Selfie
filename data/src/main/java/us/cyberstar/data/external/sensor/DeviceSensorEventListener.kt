package us.cyberstar.data.external.sensor

import android.hardware.SensorEventListener
import com.google.ar.sceneform.math.Vector3

interface DeviceSensorEventListener : SensorEventListener {
    fun registerListener(sensorDataListener: SensorDataListener)
    fun unRegisterListener()

    class SensorData(
        val gyro: Vector3,
        val acceleration: Vector3,
        val magnetometer: Vector3
    )

    interface SensorDataListener {
        fun onSensorDataUpdated(sensorData: SensorData)
    }
}