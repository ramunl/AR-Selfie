package us.cyberstar.data.entity.telemetry

import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

data class CreatePostRequestEntity(
    val assetForDetection: AssetForDetectionEntity?,
    val arPostEntity: ArPostEntity
) : ArEntityTelemetry()