package us.cyberstar.data.entity.telemetry

import com.cyber.math.Matrix4
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

data class DetectedAssetEntity(
    val name: String,
    val cameraTransform: Matrix4,
    val anchors: Collection<Anchor>,
    val extentX: Float,
    val extentZ: Float,
    val pose: Pose
) : ArEntityTelemetry()
