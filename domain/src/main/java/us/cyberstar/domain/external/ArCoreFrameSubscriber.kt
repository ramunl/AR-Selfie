package us.cyberstar.domain.external

import com.google.ar.sceneform.FrameTime
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener


/**
 * This is a base class for anyone who wants listen to ArCore frames.
 * It must be able both to subscribe and release the resource
 * so, we have those methods here and the main thing is arCoreFrameEmitterDisposable - unique object for every subscriber
 */
abstract class ArCoreFrameSubscriber(open val arCoreFrameEmitter: ArCoreFrameEmitter) :
    OnUpdateListener {

    override fun onUpdate(frameTime: FrameTime) {
    }

    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }

}