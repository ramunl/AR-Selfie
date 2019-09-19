package com.cyber.ux

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable

interface SceneFormNodeProvider {
    //fun setupTransformationSystem(transformationSystem: TransformationSystem)
    //fun isAnimating(): Boolean?

    interface ScaleAnimatingListener {
        fun onScaleAnimationEnd()
    }

    fun setupScaleAnimatingListener(listener: ScaleAnimatingListener)
   // fun createTransformableNode(selectionRenderable: ModelRenderable?): Node

    val transformationSystem: TransformationSystem
}