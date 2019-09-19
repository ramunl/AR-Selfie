package us.cyberstar.data.mapper.telemetry

import base_types.BaseTypes
import us.cyberstar.data.entity.telemetry.HardwareFrameEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.hardwareFrameEntityCounter


fun mapToHardwareFrame(hwEntityArray: Collection<HardwareFrameEntity>): List<BaseTypes.HardwareFrame?> {
    return hwEntityArray.map { mapToHardwareFrame(it) }
}

fun mapToHardwareFrame( hardwareFrameEntity: HardwareFrameEntity) =
    with(hardwareFrameEntity) {
        BaseTypes.HardwareFrame.newBuilder()
            .setCpuUsage(0.0)
            .setClientTs(clientTs.toDouble())
            .setAmbientLightingLux(ambientLightingLux)
            .setBatteryLevel(batteryLevel.toDouble())
            .setFrameIndex(hardwareFrameEntityCounter++)
            .setRamUsage(ramUsage).build()
    }
