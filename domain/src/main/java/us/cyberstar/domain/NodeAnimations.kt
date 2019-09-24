package us.cyberstar.domain

import android.animation.Animator
import com.cyber.ux.TransformableNode
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.ext.distanceFromVector
import us.cyberstar.data.ext.rotationQuaternion
import us.cyberstar.data.ext.translationVector3
import us.cyberstar.data.ext.worldPosition
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.utils.asArray
import us.cyberstar.domain.internal.utils.asVector3
import us.cyberstar.domain.internal.utils.calculateQuartToRotateInParallel
import us.cyberstar.domain.internal.utils.getPose
import kotlin.math.abs

var isAnimatingPos = false
var isAnimatingRot = false

fun isAnimating() = isAnimatingPos || isAnimatingRot

fun movePostNodeTo(post: Node, cell: CellNode) {
    val targetPosition =
        with(cell.parent!!.localToWorldPoint(cell.localPosition)) { Vector3(x, y, z) }
    val targetRotation = calculateQuartToRotateInParallel(cell.parentPlane.pose)
    movePostNodeTo(post, targetPosition, targetRotation)
}

fun runQuickPostAnimation(
    camera: Camera,
    postNode: Node,
    animationEndListener: AnimationEndListener? = null
) {
    /*faceToCamera(camera, postNode, object : AnimationEndListener {
        override fun onAnimationEnd() {
            movePostDown(camera, postNode, animationEndListener)
        }
    })*/
}


fun faceToCamera(
    camera: Camera,
    postNode: Node,
    animationEndListener: AnimationEndListener? = null,
    noRotation: Boolean = false
) {
    if (!isAnimatingPos && !isAnimatingRot) {
        val scale = (postNode as TransformableNode).worldScale.y
        val size = (postNode.renderable!!.collisionShape!! as Box).size.y
        val fakeCamPos = with(camera.worldPosition) { Vector3(x, y - scale * size / 2, z) }
        val targetPosition = Vector3.add(fakeCamPos, camera.forward)
        val direction = Vector3.subtract(fakeCamPos, postNode.worldPosition)
        val targetRotation = Quaternion.lookRotation(direction, Vector3.up())
        movePostNodeTo(postNode, targetPosition, targetRotation, animationEndListener)
    }
}

fun movePostDown(
    camera: Camera,
    postNode: Node,
    animationEndListener: AnimationEndListener? = null
) {
    val targetPosition = Vector3.add(
        with(camera.worldPosition) { Vector3(x, y - 0.25f, z) },
        camera.forward.scaled(1.5f)
    )

    val direction = Vector3.subtract(camera.worldPosition, targetPosition)
    val targetRotation = Quaternion.lookRotation(direction, Vector3.up())
    movePostNodeTo(postNode, targetPosition, targetRotation, animationEndListener)
}

interface AnimationEndListener {
    fun onAnimationEnd()
}

fun movePostNodeTo(
    postNode: Node,
    targetPose: Pose,
    animationEndListener: AnimationEndListener? = null
) {
    movePostNodeTo(
        postNode,
        targetPose.translationVector3(),
        calculateQuartToRotateInParallel(targetPose),
        animationEndListener
    )
}

fun movePostNodeTo(
    postNode: Node,
    targetPosition: Vector3,
    targetRotation: Quaternion?,
    animationEndListener: AnimationEndListener? = null
) {
    val dist = targetPosition.distanceFromVector(postNode.worldPosition)
    if (dist > 0.1 && !isAnimatingPos) {
        isAnimatingPos = true
        postNode.playTranslateAnimation(
            "worldPosition",
            postNode.worldPosition.asArray(),
            targetPosition.asArray()
        ).addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                isAnimatingPos = false
                postNode.worldPosition = targetPosition
                if (!isAnimatingRot && !isAnimatingPos) {
                    animationEndListener?.onAnimationEnd()
                }
            }
        })
    }

    if (!isAnimatingRot && targetRotation != null) {
        Timber.d("targetRotation $targetRotation")
        val rotDiff = abs(targetRotation.w - postNode.worldRotation.w)
        if (rotDiff > 0.001f) {
            isAnimatingRot = true
            postNode.playTranslateAnimation(
                "worldRotation",
                postNode.worldRotation.asArray(),
                targetRotation.asArray()
            ).addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    isAnimatingRot = false
                    postNode.worldRotation = targetRotation
                    if (!isAnimatingRot && !isAnimatingPos) {
                        animationEndListener?.onAnimationEnd()
                    }
                }
            })
        }

    }
}

/**
 * This methods does several things:
 *  1) finds out for a targeting Cell node
 *  2) moves post node to this cell
 *  3) returns this cell
 */
fun updateTargetingPostNodePosition(
    frame: Frame,
    postNode: Node,
    arCoreScene: ArCoreScene,
    cellNodeSelectedCurrent: CellNode?
): CellNode? {
    var cellNodeRes: CellNode? = null
    val ray = Ray(frame.camera.worldPosition(), arCoreScene.scene.camera.forward)
    val results = arCoreScene.scene.hitTestAll(ray)
    for (result in results) {
        val cell = result.node
        if (cell is CellNode) {
            if (cellNodeSelectedCurrent != null && cell == cellNodeSelectedCurrent) {
                cellNodeRes = cell
                postNode.worldPosition = cellNodeRes.worldPosition
                postNode.worldRotation = cellNodeRes.worldRotation
                //Timber.d("the same cell detected!!!!!!!!!!!!")
                break
            } else if (
                result.distance > 0 && cell.nodeSize == NodeSize.CELL_NODE_SMALL && cell.isVisible/*cell.orientation == CellNodeOrientation.CELL_NODE_VERTICAL*/
            ) {
                movePostNodeTo(postNode, cell)
                cell.distanceToCamera = result.distance
                cellNodeRes = cell
                break
            }
        }

    }

    return cellNodeRes
}


fun movePostToPlane(
    scene: Scene,
    frame: Frame,
    postNode: Node
): Boolean {
    var hitSuccess = false
    val planes = frame.getUpdatedTrackables(Plane::class.java)
    for (plane in planes) {
        if (plane.type == Plane.Type.VERTICAL) {
            val targetPos = Vector3(postNode.worldPosition)
            targetPos.z = plane.centerPose.extractTranslation().ty()
            movePostNodeTo(postNode, targetPos, null)
            hitSuccess = true
        }
    }
    for (plane in planes) {
        //plane.isPoseInExtents()
    }
    val ray = Ray(frame.camera.worldPosition(), scene.camera.forward)
    val results = scene.hitTestAll(ray)
    for (result in results) {
        result.node
    }
    //val ray = Ray(postNode.worldPosition, scene.camera.worldToScreenPoint())
    //val results = arCoreScene.scene.hitTestAll(ray)
    /* for (result in results) {
         result.
         movePostNodeTo(postNode, result.point, null)
         hitSuccess = true
         break
     }*/
    return hitSuccess
}