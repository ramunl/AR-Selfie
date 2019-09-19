package us.cyberstar.data.entity.telemetry

import com.google.ar.core.CameraIntrinsics
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import base_types.BaseTypes.Matrix3x3
import base_types.BaseTypes.ARSessionFrame


data class SessionFinalEntity(
    val sessionEndTimestamp: Long
) : ArEntityTelemetry()