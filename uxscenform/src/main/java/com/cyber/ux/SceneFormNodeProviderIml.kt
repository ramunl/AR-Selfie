package com.cyber.ux

import android.content.Context
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import javax.inject.Inject

class SceneFormNodeProviderIml @Inject constructor(private val context: Context) :
    SceneFormNodeProvider {

    /**
     * Gets the transformation system, which is used by [TransformableNode] for detecting
     * gestures and coordinating which node is selected.
     */
    override val transformationSystem: TransformationSystem by lazy { TransformationSystem(context.resources.displayMetrics, FootprintSelectionVisualizer()) }


    override fun setupScaleAnimatingListener(listener: SceneFormNodeProvider.ScaleAnimatingListener) {
      //  transformableNode?.scaleController?.scaleAnimatingListener = listener
    }

    var transformableNode: TransformableNode? = null

   // override fun isAnimating() = transformableNode?.scaleController?.isAnimating

   /* override fun createTransformableNode(
        selectionRenderable: ModelRenderable?
    ): Node {
        transformationSystem.let {
            it!!.selectionVisualizer =
                FootprintSelectionVisualizer().apply {
                    //                    footprintRenderable = selectionRenderable
                }
            transformableNode = TransformableNode(it).apply {
                select()
            }
        }
        return transformableNode!!
    }*/


}