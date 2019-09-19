package us.cyberstar.data.ext

import com.cyber.math.Matrix4
import com.google.ar.core.Camera
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import kotlin.math.sqrt


fun Camera.worldPosition() = pose.translationVector3()

fun Camera.cameraOrientation(): FloatArray = displayOrientedPose.andrMatrix4().`val`

fun Frame.cameraOrientation(): FloatArray = camera.displayOrientedPose.andrMatrix4().`val`

fun Pose.translationVector3(): Vector3 = Vector3(translation[0], translation[1], translation[2])

fun com.google.ar.core.Plane.size(): Float = extentX * extentZ

fun Pose.rotationQuaternion(): Quaternion =
    Quaternion(rotationQuaternion[0], rotationQuaternion[1], rotationQuaternion[2], rotationQuaternion[3])


const val TRANSFORM_SIZE = 4

fun Pose.andrMatrix4(): Matrix4 {
    with(rotationQuaternion) {
        val mat = Matrix4(
            com.cyber.math.Quaternion(
                this[0],
                this[1],
                this[2],
                this[3]
            )
        )
        mat.translation = com.cyber.math.Vector3(translation[0], translation[1], translation[2])
        return mat
    }
}
fun Node.distanceFromNode(to: Node): Float {
    // Compute the difference vector between the two hit locations.
    val dx: Double = (to.worldPosition.x - worldPosition.x).toDouble();
    val dy: Double = (to.worldPosition.y - worldPosition.y).toDouble();
    val dz: Double = (to.worldPosition.z - worldPosition.z).toDouble();
    // Compute the straight-line distance (distanceMeters)
    return sqrt(dx * dx + dy * dy + dz * dz).toFloat();
}

fun Vector3.distanceFromVector(to: Vector3): Float {
    // Compute the difference vector between the two hit locations.
    val dx: Double = (to.x - x).toDouble();
    val dy: Double = (to.y - y).toDouble();
    val dz: Double = (to.z - z).toDouble();
    // Compute the straight-line distance (distanceMeters)
    return sqrt(dx * dx + dy * dy + dz * dz).toFloat();
}