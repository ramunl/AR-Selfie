package us.cyberstar.data.entity.telemetry

import android.location.Location
import com.google.ar.core.Anchor
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.model.PointWithColor
import java.nio.ByteBuffer

/**
 *
 */
data class DataFrameEntity(
    val location: Location,
    val pointsWithColor: Collection<PointWithColor>,
    val pointCloudIds: Collection<Long>,
    val anchors: Collection<Anchor>,
    val frameTime: Double,
    val cameraOrientation: FloatArray,
    val keyPointArray: List<Float>?,
    val descriptors: List<ByteArray>?
) : ArEntityTelemetry()
