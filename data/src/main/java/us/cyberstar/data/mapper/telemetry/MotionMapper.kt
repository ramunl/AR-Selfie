package us.cyberstar.data.mapper.telemetry

import base_types.BaseTypes
import us.cyberstar.data.entity.telemetry.SensorFrameEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.sensorFrameEntityCounter

fun mapToMotion(sensorFrameEntityArray: Collection<SensorFrameEntity>): List<BaseTypes.MotionFrame?> {
    return sensorFrameEntityArray.map { mapToMotion(it) }
}

fun mapToMotion(sensorFrameEntity: SensorFrameEntity): BaseTypes.MotionFrame? =
    with(sensorFrameEntity) {
        BaseTypes.MotionFrame.newBuilder()
            .setTimestamp(frameTime)
            .setGyroX(gyro.x.toDouble())
            .setGyroY(gyro.y.toDouble())
            .setGyroZ(gyro.z.toDouble())

            .setAccelerationX(acceleration.x.toDouble())
            .setAccelerationY(acceleration.y.toDouble())
            .setAccelerationZ(acceleration.z.toDouble())

            .setMagnetometerX(magnetometer.x.toDouble())
            .setMagnetometerY(magnetometer.y.toDouble())
            .setMagnetometerZ(magnetometer.z.toDouble())
            .setFrameIndex(sensorFrameEntityCounter++)
            .build()
    }


