package us.cyberstar.domain.internal.utils

import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

// matrix-vector multiplication (y = A * x)

fun FloatArray.asQuaternion(): Quaternion {
    return Quaternion(this[0], this[1], this[2], this[3])
}

fun FloatArray.asVector3(): Vector3 {
    return Vector3(this[0], this[1], this[2])
}


fun Quaternion.asArray(): FloatArray {
    return floatArrayOf(this.x, this.y, this.z, this.w)
}

fun Vector3.asArray(): FloatArray {
    return floatArrayOf(this.x, this.y, this.z)
}