package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.external.sensor.DeviceSensorEventListener
import us.cyberstar.data.entity.telemetry.SensorFrameEntity
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase

abstract class SensorFrameEntityEmitter : EntityEmitterBase<SensorFrameEntity>(),
    DeviceSensorEventListener.SensorDataListener {
    abstract fun startListener()
    abstract fun stopListener()
}