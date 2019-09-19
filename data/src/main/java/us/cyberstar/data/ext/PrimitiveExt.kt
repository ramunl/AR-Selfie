package us.cyberstar.data.ext

import java.nio.FloatBuffer
import java.nio.IntBuffer


fun FloatBuffer.asArray(): FloatArray {
    val limit = limit()
    var array = FloatArray(limit)
    if (limit > 0) {
        get(array)
    }
    return array
}


fun IntBuffer.asArray(): IntArray {
    val limit = limit()
    var array = IntArray(limit)
    if (limit > 0) {
        get(array)
    }
    return array
}