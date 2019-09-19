package us.cyberstar.domain.internal.factory

import com.google.ar.sceneform.rendering.ModelRenderable

interface ModelFactoryCallback {
    fun onRenderableReady(renderable: ModelRenderable)
}