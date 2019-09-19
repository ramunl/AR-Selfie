package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.entity.telemetry.HardwareFrameEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import java.util.concurrent.atomic.AtomicReference

abstract class HardwareEntityEmitter(val arCoreFrameEmitter: ArCoreFrameEmitter):
    OnUpdateListener, EntityEmitterBase<HardwareFrameEntity>() {

    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }

    abstract var hwInfo: AtomicReference<String>
}
