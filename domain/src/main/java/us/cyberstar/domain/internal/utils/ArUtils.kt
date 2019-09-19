package us.cyberstar.domain.internal.utils

import com.cyber.math.Matrix4
import com.google.ar.core.Camera
import com.google.ar.core.Plane
import com.google.ar.core.Pose

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.data.ext.andrMatrix4

private val PLANE_COLORS_RGBA = intArrayOf(
    -0x1,
    -0xbbcc901,
    -0x16e19c01,
    -0x63d84f01,
    0x673AB7FF,
    0x3F51B5FF,
    0x2196F3FF,
    0x03A9F4FF,
    0x00BCD4FF,
    0x009688FF,
    0x4CAF50FF,
    -0x743cb501,
    -0x3223c601,
    -0x14c401,
    -0x3ef801,
    -0x67ff01
)

private fun colorRgbaToFloat(planeColor: FloatArray, colorRgba: Int) {
    planeColor[0] = (colorRgba shr 24 and 0xff).toFloat() / 255.0f
    planeColor[1] = (colorRgba shr 16 and 0xff).toFloat() / 255.0f
    planeColor[2] = (colorRgba shr 8 and 0xff).toFloat() / 255.0f
    planeColor[3] = (colorRgba shr 0 and 0xff).toFloat() / 255.0f
}


fun calculateDistanceToPlane(planePose: Pose, cameraPose: Pose): Float {
    val normal = calculateNormalToPlane(planePose)
    val cameraX = cameraPose.tx()
    val cameraY = cameraPose.ty()
    val cameraZ = cameraPose.tz()
    // Get transformed Y axis of plane's coordinate
    // Compute dot product of plane's normal with vector from camera to plane center.
    return ((cameraX - planePose.tx()) * normal[0]
            + (cameraY - planePose.ty()) * normal[1]
            + (cameraZ - planePose.tz()) * normal[2])
}

fun faceToPlane(cameraPos: Vector3, worldPos: Vector3): Quaternion {
    Timber.d("faceToPlane camera = $cameraPos plane = $worldPos")

    val direction = Vector3.subtract(worldPos, cameraPos)
    return Quaternion.lookRotation(direction, Vector3.up())
}

fun faceToPlane(plane: Plane): Quaternion {
    val planePos = with(plane.centerPose.translation) { Vector3(this[0], this[1], this[2]) }
    val nodePos = with(plane.centerPose.translation) { Vector3(this[0], this[1], this[2] + 100) }
    val direction = Vector3.subtract(planePos, nodePos)
    return Quaternion.lookRotation(direction, Vector3.up())
}

fun calculateQuartToRotateInParallel(pose: Pose): Quaternion {
    val normalToPlane = with(calculateNormalToPlane(pose)) {
        Vector3(this[0], this[1], this[2])
    }
    return Quaternion.lookRotation(normalToPlane, Vector3.up())
}

/*fun calculateQuartToRotateInParallel(plane: ArPlaneEntity): Quaternion {
    val normalToPlane = with(calculateNormalToPlane(plane.pose)) {
        Vector3(this[0], this[1], this[2])
    }
    return Quaternion.lookRotation(normalToPlane, Vector3.up())
}*/


fun calculateNormalToPlane(planePose: Pose): FloatArray {
    val normal = FloatArray(3)
    // Get transformed Y axis of plane's coordinate system.
    planePose.getTransformedAxis(1, 1.0f, normal, 0)
    return normal
}

/*fun transformToPose(transformAndr: FloatArray): Pose {
    val quatTrans = matToQuatTrans2(toReal4x4(transformAndr))
    return Pose(quatTrans.position.asArray(), quatTrans.quaternion.asArray())
}*/

/*fun transformAndrToPose(transformAndr: FloatArray): Pose {
    val quatTrans = andrMatToQuatTrans2(toReal4x4(transformAndr))
    return Pose(quatTrans.position.asArray(), quatTrans.quaternion.asArray())
}

fun calculateNormalToPlane(transformAndr: FloatArray): FloatArray {
    val planePose = transformAndrToPose(transformAndr)
    return calculateNormalToPlane(planePose)
}*/
/*
fun calculateDistanceToPlane(transformAndr: Iterable<Float>, cameraPose: Pose): Float {
    val normal = calculateNormalToPlane(transformAndr)
    val cameraX = cameraPose.tx()
    val cameraY = cameraPose.ty()
    val cameraZ = cameraPose.tz()
    val quatTrans = andrMatToQuatTrans2(transformAndr)
    val planePose = Pose(quatTrans.position.asArray(), quatTrans.quaternion.asArray())
    // Get transformed Y axis of plane's coordinate
    // Compute dot product of plane's normal with vector from camera to plane center.
    return ((cameraX - planePose.tx()) * normal[0]
            + (cameraY - planePose.ty()) * normal[1]
            + (cameraZ - planePose.tz()) * normal[2])
}*/

//fun ArPostEntity.quat(): Quaternion = andrMatToQuatTrans2(toReal4x4(postTransform)).quaternion

//fun ArPostEntity.pose(): Pose = transformAndrToPose(postTransform)


fun Node.getPose(): Pose {
    val pos = worldPosition
    val rot = worldRotation
    return Pose(pos.asArray(), rot.asArray())
}

fun Node.getMatrix4(): Matrix4 {
    val pos = worldPosition
    val rot = worldRotation
    return Pose(pos.asArray(), rot.asArray()).andrMatrix4()
}

fun worldToScreenPoint(viewWidth: Int, viewHeight: Int, var1: Vector3, camera: Camera): Vector3 {

    val projmtx = FloatArray(16)
    camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)
    val projectionMatrix = Matrix(projmtx)

    // Get camera matrix and draw.
    val viewmtx = FloatArray(16)
    camera.getViewMatrix(viewmtx, 0)
    val viewMatrix = Matrix(viewmtx)

    val var2 = Matrix()
    Matrix.multiply(projectionMatrix, viewMatrix, var2)

    val var3 = viewHeight//getScrH(context)
    val var4 = viewWidth//getScrH(context)
    val var5 = var1.x
    val var6 = var1.y
    val var7 = var1.z
    val var9 = Vector3()
    var9.x = var5 * var2.data[0] + var6 * var2.data[4] + var7 * var2.data[8] + 1.0f * var2.data[12]
    var9.y = var5 * var2.data[1] + var6 * var2.data[5] + var7 * var2.data[9] + 1.0f * var2.data[13]
    val var8 = var5 * var2.data[3] + var6 * var2.data[7] + var7 * var2.data[11] + 1.0f * var2.data[15]
    var9.x = (var9.x / var8 + 1.0f) * 0.5f
    var9.y = (var9.y / var8 + 1.0f) * 0.5f
    var9.x *= var3.toFloat()
    var9.y *= var4.toFloat()
    var9.y = var4.toFloat() - var9.y
    return var9
}