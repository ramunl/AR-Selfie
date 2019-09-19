package us.cyberstar.domain.internal.utils

import android.renderscript.Matrix3f
import android.renderscript.Matrix4f
import com.cyber.math.Matrix3
import com.cyber.math.Matrix4


fun Matrix3.multiply(right: Matrix3): Matrix3 {
    var temp = Matrix3f(values)
    temp.multiply(Matrix3f(right.values))
    return Matrix3(temp.array)
}

fun Matrix4.multiply(right: Matrix4): Matrix4 {
    var temp = Matrix4f(values)
    temp.multiply(Matrix4f(right.values))
    return Matrix4(temp.array)
}