package us.cyberstar.data.mapper.utils

import base_types.BaseTypes
import com.google.ar.sceneform.math.Vector3
import java.nio.FloatBuffer


fun BaseTypes.VectorFloat4.asArray() = FloatArray(4)
    .apply {
        this[0] = x
        this[1] = y
        this[2] = z
        this[3] = w
    }

fun BaseTypes.VectorFloat3.asArray() = FloatArray(3)
    .apply {
        this[0] = x
        this[1] = y
        this[2] = z
    }


fun mapToVectorFloat3(iterable: Iterable<Float>): BaseTypes.VectorFloat3 =
    mapToVectorFloat3(iterable.toList())

fun mapToVectorFloat4(iterable: Iterable<Float>): BaseTypes.VectorFloat4 =
    mapToVectorFloat4(iterable.toList())

fun mapToVectorFloat3(array: FloatArray): BaseTypes.VectorFloat3 {
    return BaseTypes.VectorFloat3.newBuilder()
        .setX(array[0])
        .setY(array[1])
        .setZ(array[2])
        .build()
}


fun mapToVectorFloat4(array: FloatArray): BaseTypes.VectorFloat4 {
    return BaseTypes.VectorFloat4.newBuilder()
        .setX(array[0])
        .setY(array[1])
        .setZ(array[2])
        .setW(array[3])
        .build()
}

fun mapToVectorFloat3(vector: Vector3): BaseTypes.VectorFloat3 {
    return BaseTypes.VectorFloat3.newBuilder()
        .setX(vector.x)
        .setY(vector.y)
        .setZ(vector.z)
        .build()
}

fun mapToVectorFloat3List(floatArray: FloatArray) {
    val size = floatArray.size
    val vectorSize = 3
    val vectorList = mutableListOf<BaseTypes.VectorFloat3>()
    for (i in 0..size - vectorSize step vectorSize) {
        vectorList.add(
            BaseTypes.VectorFloat3.newBuilder()
                .setX(floatArray[i])
                .setY(floatArray[i + 1])
                .setZ(floatArray[i + 2]).build()
        )
    }
}

fun mapToVectorFloat2List(floatArray: FloatArray): List<BaseTypes.VectorFloat2> {
    val size = floatArray.size
    val vectorSize = 2
    val vectorList = mutableListOf<BaseTypes.VectorFloat2>()
    for (i in 0..size - vectorSize step vectorSize) {
        vectorList.add(
            BaseTypes.VectorFloat2.newBuilder()
                .setX(floatArray[i])
                .setY(floatArray[i + 1]).build()
        )
    }
    return vectorList
}

fun mapToVectorFloat2List(floatBuffer: FloatBuffer): List<BaseTypes.VectorFloat2> {
    val array = floatBuffer.array()
    val vectorSize = 2
    val vectorList = mutableListOf<BaseTypes.VectorFloat2>()
    for (i in 0..array.size - vectorSize step vectorSize) {
        vectorList.add(
            BaseTypes.VectorFloat2.newBuilder()
                .setX(array[i])
                .setY(array[i + 1]).build()
        )
    }
    return vectorList
}

fun mapToVectorFloat2(x: Float, y: Float) = BaseTypes.VectorFloat2.newBuilder().setX(x).setY(y).build()


