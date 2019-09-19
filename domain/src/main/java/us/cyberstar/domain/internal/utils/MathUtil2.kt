package us.cyberstar.domain.internal.utils

import com.cyber.math.Matrix4
import com.cyber.math.Vector3
import com.google.ar.sceneform.math.Quaternion


fun quaternionNormalize(quat: DoubleArray) {
    val magnitude = Math.sqrt((quat[0] * quat[0] + quat[1] * quat[1] + quat[2] * quat[2] + quat[3] * quat[3]))
    quat[0] = quat[0] / magnitude
    quat[1] = quat[1] / magnitude
    quat[2] = quat[2] / magnitude
    quat[3] = quat[3] / magnitude
}

fun getSign(param: Double): Double {
    if (param < 0.0) {
        return -1.0
    } else {
        return 1.0
    }
}


fun matToQuatTrans2(matAndroid: Matrix4): TransQuatWrap {

    val transMath = Vector3()
    matAndroid.getTranslation(transMath)

    val quatMath = com.cyber.math.Quaternion()
    matAndroid.getRotation(quatMath)

    val quat = Quaternion(quatMath.x, quatMath.y, quatMath.z, quatMath.w)
    val trans = floatArrayOf(transMath.x, transMath.y, transMath.z)
    return TransQuatWrap(trans, quat)
}

/*fun RealMatrix.asArray(): Array<Float> {
    val floats = ArrayList<Float>(columnDimension * rowDimension)
    this.data.forEach {
        floats.addAll(it.map { it.toFloat() })
    }
    return floats.toTypedArray()
}*/

fun transQuatToMatrix2(trans: FloatArray, quat: FloatArray): Matrix4 {
    val mat = Matrix4()
    mat.set(
        Vector3(trans),
        com.cyber.math.Quaternion(quat[0], quat[1], quat[2], quat[3])
    )
    return mat

    //MatrixUtils.createRowRealMatrix(mat.values.map { it.toDouble() }.toDoubleArray())

    //TODO make ASSERT trans 3 quat 4
    /*val mat = MatrixUtils.createRealMatrix(4, 4)

    mat.setEntry(0, 3, trans[0].toDouble())
    mat.setEntry(1, 3, trans[1].toDouble())
    mat.setEntry(2, 3, trans[2].toDouble())
    mat.setEntry(3, 3, 1.0)

    val wx: Double
    val wy: Double
    val wz: Double
    val xx: Double
    val yy: Double
    val yz: Double
    val xy: Double
    val xz: Double
    val zz: Double
    val x2: Double = (2 * quat[0]).toDouble()
    val y2: Double = (2 * quat[1]).toDouble()
    val z2: Double = (2 * quat[2]).toDouble()
    xx = quat[0] * x2
    xy = quat[0] * y2
    xz = quat[0] * z2
    yy = quat[1] * y2
    yz = quat[1] * z2
    zz = quat[2] * z2
    wx = quat[3] * x2
    wy = quat[3] * y2
    wz = quat[3] * z2
    mat.setEntry(0, 0, 1.0 - (yy + zz))
    mat.setEntry(0, 1, xy - wz)
    mat.setEntry(0, 2, xz + wy)
    mat.setEntry(1, 0, xy + wz)
    mat.setEntry(1, 1, 1.0 - (xx + zz))
    mat.setEntry(1, 2, yz - wx)
    mat.setEntry(2, 0, xz - wy)
    mat.setEntry(2, 1, yz + wx)
    mat.setEntry(2, 2, 1.0 - (xx + yy))
    return mat.transpose()
    */
}
