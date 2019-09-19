package us.cyberstar.domain.external.loader.grpc.telemetry

import com.google.ar.sceneform.FrameTime

interface OnUpdateListener {
    fun unsubscribeFromArCoreFrames()
    fun onUpdate(frameTime: FrameTime)
    fun subscribeToArCoreFrames()
}