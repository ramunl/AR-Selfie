package us.cyberstar.domain

import android.animation.Animator
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import us.cyberstar.data.ext.worldPosition
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.utils.asArray
import us.cyberstar.domain.internal.utils.calculateQuartToRotateInParallel

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
    faceToCamera(camera, postNode, object : AnimationEndListener {
        override fun onAnimationEnd() {
            movePostDown(camera, postNode, animationEndListener)
        }
    })
}
/*
private fun faceToCamera(camera: com.google.ar.core.Camera, postNode: PostNode) {
    Timber.d("faceToCamera $postNode")
    if (!isAnimatingPos && !isAnimatingRot) {
        val direction = Vector3.subtract(
            camera.worldPosition(),
            with(camera.worldPosition()) { Vector3(x, y, z + 1) })
        val targetPosition = Vector3.add(
            camera.worldPosition(),
            camera.
        )
        val targetRotation = Quaternion.lookRotation(direction, Vector3.up())
        movePostNodeTo(postNode, targetPosition, targetRotation, null)
    }
}*/

fun faceToCamera(
    camera: Camera,
    postNode: Node,
    animationEndListener: AnimationEndListener? = null,
    noRotation:Boolean = false
) {
    if (!isAnimatingPos && !isAnimatingRot) {
        //   Timber.d("faceToCamera $postNode")
        val direction = Vector3.subtract(
            camera.worldPosition,
            postNode.worldPosition)

        val targetPosition = Vector3.add(
            camera.worldPosition,
            camera.forward
        )
        val targetRotation = if(noRotation) null else Quaternion.lookRotation(direction, Vector3.up())
        movePostNodeTo(postNode, targetPosition, targetRotation, animationEndListener)
    }
}

fun movePostDown(
    camera: Camera,
    postNode: Node,
    animationEndListener: AnimationEndListener? = null
) {
    val direction = Vector3.subtract(
        camera.worldPosition,
        with(camera.worldPosition) { Vector3(x, y + 1, z) })
    val targetPosition = Vector3.add(
        with(camera.worldPosition) { Vector3(x, y - 0.25f, z) },
        camera.forward.scaled(1.5f)
    )
    val targetRotation = Quaternion.lookRotation(direction, Vector3.up())
    movePostNodeTo(postNode, targetPosition, targetRotation, animationEndListener)
}

interface AnimationEndListener {
    fun onAnimationEnd()
}

fun movePostNodeTo(
    postNode: Node,
    targetPosition: Vector3,
    targetRotation: Quaternion?,
    animationEndListener: AnimationEndListener? = null
) {
    if (!isAnimatingPos) {
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
        isAnimatingRot = true
        postNode.playTranslateAnimation(
            "worldRotation",
            postNode.worldRotation.asArray(),
            targetRotation.asArray()
        )
            .addListener(object : Animator.AnimatorListener {
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


fun move3dModelToGround(
    frame: Frame,
    postNode: Node
): Boolean {
    var hitSuccess = false
    val planes = frame.getUpdatedTrackables(Plane::class.java)
    for(plane in planes) {
        if(plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING) {
            val targetPos = Vector3(postNode.worldPosition)
            targetPos.y = plane.centerPose.extractTranslation().ty()
            movePostNodeTo(postNode, targetPos, null)
            hitSuccess = true
        }
    }
    /*val ray = Ray(postNode.worldPosition, arCoreScene.scene.camera.down)
    val results = arCoreScene.scene.hitTestAll(ray)
    for (result in results) {
        result.
        movePostNodeTo(postNode, result.point, null)
        hitSuccess = true
        break
    }*/
    return hitSuccess
}