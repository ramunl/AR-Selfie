package us.cyberstar.domain.external.helper

import timber.log.Timber
import java.util.*
import kotlin.math.abs

fun getNewId(): Long {
    val id = abs(Random().nextLong())
    Timber.d("getNewId $id")
    return id
}
