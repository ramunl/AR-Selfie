package us.cyberstar.data.mapper.telemetry

import base_types.BaseTypes
import us.cyberstar.data.entity.telemetry.DetectedAssetEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.detectedAssetCounter
import us.cyberstar.data.mapper.mapToMatrix4x4
import us.cyberstar.data.mapper.utils.mapToPose
import us.cyberstar.data.mapper.utils.mapToProtoAnchor
import us.cyberstar.data.mapper.utils.mapToVectorFloat2


fun mapToDetectedAsset(array: Collection<DetectedAssetEntity>?): Collection<BaseTypes.DetectedAsset>? =
    array?.map { mapToDetectedAsset(it) }

fun mapToDetectedAsset(augmentedImage: DetectedAssetEntity): BaseTypes.DetectedAsset =
    with(augmentedImage) {
        BaseTypes.DetectedAsset.newBuilder()
            .setCameraTransform(mapToMatrix4x4(cameraTransform))
            .setAssetId(name)
            .addAllAnchors(mapToProtoAnchor(anchors))
            .setAndroidExtentXz(mapToVectorFloat2(extentX, extentZ))
            .setAndroidPose(mapToPose(pose))
            .setFrameIndex(detectedAssetCounter++)
            .build()
    }