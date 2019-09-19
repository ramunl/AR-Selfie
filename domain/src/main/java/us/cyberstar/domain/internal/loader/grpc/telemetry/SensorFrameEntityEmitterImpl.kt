package us.cyberstar.domain.internal.loader.grpc.telemetry

import timber.log.Timber
import us.cyberstar.common.utils.timeNow
import us.cyberstar.data.external.sensor.DeviceSensorEventListener
import us.cyberstar.data.entity.telemetry.SensorFrameEntity
import us.cyberstar.domain.external.loader.TelemetryRecorderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.SensorFrameEntityEmitter
import javax.inject.Inject


/**
 * The class is responsible for filling up arrays:
 * sensorFrames - the device' sensors data
 */
//TODO refactor this class, its name doesn't correspond the meanings
internal class SensorFrameEntityEmitterImpl @Inject constructor(

    private val telemetryRecorderFabric: TelemetryRecorderFabric,
    private val deviceSensorEventListener: DeviceSensorEventListener
) : SensorFrameEntityEmitter() {

    override fun onSensorDataUpdated(sensorData: DeviceSensorEventListener.SensorData) {
        with(sensorData) {
            telemetryRecorderFabric.getTelemetryRecorder().appendEntity(
                SensorFrameEntity(
                    acceleration,
                    gyro,
                    magnetometer,
                    timeNow().toDouble()
                )
            )
        }
    }


    override fun stopListener() {
        Timber.d("stopListener")
        deviceSensorEventListener.unRegisterListener()
    }

    override fun startListener() {
        Timber.d("startListener")
        deviceSensorEventListener.registerListener(this)
    }
}
