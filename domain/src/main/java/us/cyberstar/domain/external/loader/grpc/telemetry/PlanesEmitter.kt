package us.cyberstar.domain.external.loader.grpc.telemetry

import com.google.ar.core.Plane
import us.cyberstar.data.entity.telemetry.ArPlaneEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase
import java.util.concurrent.atomic.AtomicReference

abstract class PlanesEmitter(val arCoreFrameEmitter: ArCoreFrameEmitter) : EntityEmitterBase<List<ArPlaneEntity>>(),
    OnUpdateListener {

    abstract var planesInfo: AtomicReference<String>
    abstract var planesFound: Collection<Plane>

    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }
}