package us.cyberstar.domain

import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import us.cyberstar.data.ext.distanceFromNode
import us.cyberstar.domain.external.manger.arScene.ArGridManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.model.*
import us.cyberstar.domain.internal.utils.asQuaternion
import us.cyberstar.domain.internal.utils.asVector3

/**
 * Runs a default animation that can be used to introduce new AR elements.
 *
 * @param targetScale The desired final scale of the [Node] after the animation.
 */

const val ANIM_DURATION_MS = 500L

fun Node.playTranslateAnimation(
    property: String,
    source: FloatArray,
    target: FloatArray,
    duration: Long = ANIM_DURATION_MS
): ObjectAnimator {
    val animator = ObjectAnimator().apply {
        setPropertyName(property)
        if (property.contains("Position") || property.contains("Scale")) {
            setObjectValues(source.asVector3(), target.asVector3())
            setEvaluator(Vector3Evaluator())
        } else {
            setObjectValues(source.asQuaternion(), target.asQuaternion())
            setEvaluator(QuaternionEvaluator())
        }
        interpolator = AccelerateDecelerateInterpolator()
    }.also {
        it.target = this
        it.duration = duration
        it.start()
    }
    return animator
}


enum class NodeSize {
    CELL_NODE_PARENT,
    CELL_NODE_SMALL,
    CELL_NODE_BIG,
    CELL_NODE_RECTANGLE,
    CELL_NODE_RECTANGLE_LARGE;

    fun getNodeSize(): Vector3? {
        return when (this) {
            CELL_NODE_SMALL -> cellSizeSmall
            CELL_NODE_BIG -> cellSizeBig
            else -> null
        }
    }
}

fun Node.isCollided(rootNodeProvider: RootNodeProvider): Boolean {
    var isCollided = false
    for (postNode in rootNodeProvider.postNodes()) {
        if (this != postNode && postNode is PostNode && postNode.distanceFromNode(this) < postNode.size.x) {
            isCollided = true
            break
        }
    }
    return isCollided
}

fun Node.isCollided(gridManager: ArGridManager): Boolean {
    var isCollided = false
    for (cellNode in gridManager.cellNodeList) {
        if (cellNode.distanceFromNode(this) < cellNode.nodeSize.getNodeSize()!!.x) {
            isCollided = true
            break
        }
    }
    return isCollided
}