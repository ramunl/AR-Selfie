package us.cyberstar.data.mapper

import base_types.BaseTypes
import com.cyber.math.Matrix4
import com.google.ar.core.CameraIntrinsics


fun mapToMatrix3x3(cameraIntrinsics: CameraIntrinsics): BaseTypes.Matrix3x3? {
    val intrinsicMatrix = BaseTypes.Matrix3x3.newBuilder()
    // Focal length in x and y direction
    val focalLength = cameraIntrinsics.focalLength
    // Principal point in x and y directions
    val principalPoint = cameraIntrinsics.principalPoint
    // Initialize the matrix
    val cameraIntrinsicMatrix = Array(3) { FloatArray(3) }
    for (i in 0..2) {
        val values = FloatArray(3)
        cameraIntrinsicMatrix[i] = values
    }
    // Fill up all values
    cameraIntrinsicMatrix[0][0] = focalLength[0]
    cameraIntrinsicMatrix[0][2] = principalPoint[0]
    cameraIntrinsicMatrix[1][1] = focalLength[1]
    cameraIntrinsicMatrix[1][2] = principalPoint[1]
    cameraIntrinsicMatrix[2][2] = 1f
    val elements = cameraIntrinsicMatrix.flatMap { it.toList() }
    intrinsicMatrix.addAllM(elements)
    return intrinsicMatrix.build()
}


fun mapToMatrix4x4(arMatrix: Iterable<Float>) =
    BaseTypes.Matrix4x4.newBuilder().addAllM(arMatrix).build()!!

fun mapToMatrix4x4(arMatrix: Matrix4) =
    BaseTypes.Matrix4x4.newBuilder().addAllM(arMatrix.`val`.asIterable()).build()!!
