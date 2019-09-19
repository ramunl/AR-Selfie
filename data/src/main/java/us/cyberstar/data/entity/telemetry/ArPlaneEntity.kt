package us.cyberstar.data.entity.telemetry

import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import java.nio.FloatBuffer

data class ArPlaneEntity(
    val type: Plane.Type,
    val polygon: FloatBuffer,
    val anchors: Collection<Anchor>,
    val extentX: Float,
    val extentZ: Float,
    val pose: Pose
) : ArEntityTelemetry() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArPlaneEntity

        if (type != other.type) return false
        //if (polygon != other.polygon) return false
        //if (anchors != other.anchors) return false
        if (extentX != other.extentX) return false
        if (extentZ != other.extentZ) return false
        //if (pose != other.pose) return false

        return true
    }

    fun size(): Float = extentX * extentZ

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + polygon.hashCode()
        result = 31 * result + anchors.hashCode()
        result = 31 * result + extentX.hashCode()
        result = 31 * result + extentZ.hashCode()
        result = 31 * result + pose.hashCode()
        return result
    }
}
