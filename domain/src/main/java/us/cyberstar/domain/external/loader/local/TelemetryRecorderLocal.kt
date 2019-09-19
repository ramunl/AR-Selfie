package us.cyberstar.domain.external.loader.local

import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.internal.loader.TelemetryRecorderBaseImpl

abstract class TelemetryRecorderLocal constructor(
    hardwareEntityEmitter: HardwareEntityEmitter,
    schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    arSessionStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider,
    //dataFrameEntityEmitter: DataFrameEntityEmitter,
    augmentedImageEmitter: AugmentedImageEmitter,
    planesEmitter: PlanesEmitter,
    headFinalEntityEmitter: HeadFinalEntityEmitter
) : TelemetryRecorderBaseImpl(
    hardwareEntityEmitter,
    schedulersProvider,
    //dataFrameEntityEmitter,
    augmentedImageEmitter,
    planesEmitter,
    arSessionStartTimeProvider,
    headFinalEntityEmitter
) {
    abstract val finalFrames: ArrayList<SessionFinalEntity>
    abstract val headFrames: ArrayList<SessionHeadEntity>
    abstract val hwFrames: ArrayList<HardwareFrameEntity>
    abstract val sensorFrames: ArrayList<SensorFrameEntity>
    abstract val arPlaneArray: ArrayList<ArPlaneEntity>
    abstract val dataFrameArray: ArrayList<DataFrameEntity>
    abstract val arAugnmentedArray: ArrayList<DetectedAssetEntity>
}
