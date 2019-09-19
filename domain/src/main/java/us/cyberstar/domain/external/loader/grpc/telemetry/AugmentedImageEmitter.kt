package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.entity.telemetry.DetectedAssetEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase

abstract class AugmentedImageEmitter(open val arCoreFrameEmitter: ArCoreFrameEmitter) : OnUpdateListener,
    EntityEmitterBase<List<DetectedAssetEntity>>() {
    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }
 }