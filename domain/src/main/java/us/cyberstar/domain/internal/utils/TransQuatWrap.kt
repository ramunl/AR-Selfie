package us.cyberstar.domain.internal.utils

import ArQuaternion
import ArVector3
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3


class TransQuatWrap {

    private var axisAngle: AxisAngle? = null
    private var trans: FloatArray? = null
    private var quaternion: Quaternion? = null

    constructor(trans: FloatArray, axisAngle: AxisAngle) {
        this.trans = trans
        this.axisAngle = axisAngle
    }

    constructor(trans: FloatArray, quaternion: Quaternion) {
        this.trans = trans
        this.quaternion = quaternion
    }


    val position: Vector3 by lazy {
        with(trans!!) { Vector3(this[0], this[1], this[2]) }
    }

    fun rotation(): Vector3 {
        return with(axisAngle()) { Vector3(x * w, y * w, z * w) }
    }


    fun quaternion(): Quaternion {
        if (quaternion == null) {
            quaternion = with(axisAngle!!) { Quaternion(Vector3(x, y, z), Math.toDegrees(w.toDouble()).toFloat()) }
        }
        return quaternion!!
    }

    fun axisAngle(): AxisAngle {
        if (axisAngle == null) {
            val arQuat = ArQuaternion(quaternion!!.x, quaternion!!.y, quaternion!!.z, quaternion!!.w)
            val axis = ArVector3()
            val angle = arQuat.getAxisAngleRad(axis)
            axisAngle = AxisAngle(axis.x, axis.y, axis.z, angle)
        }
        return axisAngle!!
    }

    data class AxisAngle(val x: Float, val y: Float, val z: Float, val w: Float)
}