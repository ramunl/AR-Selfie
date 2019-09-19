package us.cyberstar.domain.internal.factory

import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable

interface RenderableFactoryCallback {
    fun onRenderableReady(viewRenderable: ViewRenderable)
}