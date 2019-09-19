package us.cyberstar.data.entity.telemetry

import com.google.ar.core.CameraIntrinsics
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry


data class SessionHeadEntity(
    val sessionStartTimestamp: Long,
    val motionFps: Int,
    val cameraIntrinsics: CameraIntrinsics,
    val videoStartTimeStamp: Long = 0,
    val deviceModel: String,
    val androidVersion: String
) : ArEntityTelemetry()