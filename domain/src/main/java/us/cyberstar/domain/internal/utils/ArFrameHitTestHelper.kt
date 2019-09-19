package us.cyberstar.domain.internal.utils

import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.data.ext.worldPosition


/**
 * This class checks is it possible to create post at 2d hitPoint (x,y) cl
 */

fun findAnyPlaneForHit(hitPoint: Vector3, frame: Frame): HitTestResult {
    var errMessage: String? = null
    val hitsList = frame.hitTest(hitPoint.x, hitPoint.y)
    var hitTestRes: HitTestResult? = null
    if (frame.camera.trackingState == TrackingState.TRACKING) {
        Timber.d("hitsList size ${hitsList.size}")

        for (hitResult in hitsList) {
            val camToPlaneDistance = calculateDistanceToPlane(hitResult.hitPose, frame.camera.pose)
            Timber.d("cam to plane distanceToCamera = $camToPlaneDistance hitResult.distanceToCamera = ${hitResult.distance}")
            val trackable = hitResult.trackable
            if (trackable is Plane) {
                /*if (trackable.isPoseInPolygon(hitResult.hitPose))
                {*/
                //if (trackable.type == Plane.Type.VERTICAL) {
                if (camToPlaneDistance > 0) {
                    hitTestRes = HitTestResult.HitTestSuccess(trackable, camToPlaneDistance)
                } else {
                    errMessage = "Distance is not appropriate = $camToPlaneDistance"
                }
                /*} else {
                    errMessage = "Then plane clicked ${trackable.type.name}"
                }*/
                /*} else {
                    errMessage = "No any plane clicked"
                }*/
            }
        }
        Timber.d("Tracking state: ${frame.camera.trackingState}")
    } else {
        errMessage = "tracking state failed (${frame.camera.trackingState})"
    }
    if (hitTestRes == null) {
        if (errMessage == null) {
            errMessage = "No any Ar plane hit, try again!"
        }
        hitTestRes = HitTestResult.HitTestFailed(errMessage)
    }
    return hitTestRes
}

//TODO implement Either type here!
sealed class HitTestResult {
    class HitTestSuccess(val plane: Plane, val distance: Float) : HitTestResult()
    class HitTestFailed(val errMsg: String) : HitTestResult()

    fun isSuccess(): Boolean {
        return when (this) {
            is HitTestSuccess -> true
            else -> false
        }
    }
}


