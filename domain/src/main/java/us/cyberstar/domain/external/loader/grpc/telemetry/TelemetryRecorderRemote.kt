package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.internal.loader.TelemetryRecorderBaseImpl

abstract class TelemetryRecorderRemote constructor(
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
   // dataFrameEntityEmitter,
    augmentedImageEmitter,
    planesEmitter,
    arSessionStartTimeProvider,
    headFinalEntityEmitter
)