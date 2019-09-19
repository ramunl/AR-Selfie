package us.cyberstar.data.entity.telemetry

import com.google.ar.sceneform.math.Vector3
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

data class SensorFrameEntity(
    var acceleration: Vector3,
    var gyro: Vector3,
    var magnetometer: Vector3,
    var frameTime: Double
) : ArEntityTelemetry()