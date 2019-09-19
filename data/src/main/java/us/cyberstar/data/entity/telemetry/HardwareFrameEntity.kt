package us.cyberstar.data.entity.telemetry

import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

data class HardwareFrameEntity(
    val clientTs: Long,
    val ramUsage: Long,
    val batteryLevel: Long,
    val ambientLightingLux: Double,
    val cpuUsage: Long? = null
) : ArEntityTelemetry()