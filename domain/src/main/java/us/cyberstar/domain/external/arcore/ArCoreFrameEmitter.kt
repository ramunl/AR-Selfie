package us.cyberstar.domain.external.arcore

import com.google.ar.core.Frame
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener

interface ArCoreFrameEmitter {

    fun removeFrameListener()
    fun addFrameListener()

    fun lastFrame(): Frame?
    fun addUpdateListener(onUpdateListener: OnUpdateListener)
    fun removeUpdateListener(onUpdateListener: OnUpdateListener)
}