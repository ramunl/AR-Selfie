package us.cyberstar.data.mapper.social

import base_types.BaseTypes
import com.cyber.math.Matrix4
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.data.mapper.utils.mapToCoordinates
import us.cyberstar.data.mapper.utils.mapToLocation
import us.cyberstar.data.mapper.mapToMatrix4x4
import java.util.*


fun mapToAssetForDetectionEntityList(array: Collection<BaseTypes.AssetForDetection>): Collection<AssetForDetectionEntity> =
    array.map { mapToAssetForDetectionEntity(it) }

fun mapToAssetForDetectionEntity(assertForDetection: BaseTypes.AssetForDetection): AssetForDetectionEntity {
    return with(assertForDetection) {
        AssetForDetectionEntity(
           // mapToArPose(androidPose),
            cameraTransform.mList.toFloatArray(),
            Matrix4(sessionToAssetTransform.mList.toFloatArray()),
            //sessionToAssetTransform.mList.toFloatArray(),
            //ArSceneCenterHitTest(hitNormal.asArray(), hitPoint.asArray()),
            imagePhysicalHeight,
            imagePhysicalWidth,
            mapToLocation(coordinates),
            timestamp.toLong(),
            null,
            imageUrl
        )
    }
}

fun mapToAssetForDetection(array: Collection<AssetForDetectionEntity>): Collection<BaseTypes.AssetForDetection?> =
    array.map { mapToAssetForDetection(it) }


fun mapToAssetForDetection(assertForDetection: AssetForDetectionEntity): BaseTypes.AssetForDetection? {
    return with(assertForDetection) {
        BaseTypes.AssetForDetection.newBuilder()
            .setImagePhysicalHeight(physicalHeight)
            .setImagePhysicalWidth(physicalWidth)
            .setSessionToAssetTransform(mapToMatrix4x4(sessionToAssetTransform))
            .setCameraTransform(mapToMatrix4x4(cameraTransform.asIterable()))
            .setAssetId(UUID.randomUUID().toString())
         //   .setHitNormal(mapToVectorFloat3(arSceneCenterHitTest.worldNormal))
         //   .setHitPoint(mapToVectorFloat3(arSceneCenterHitTest.worldCoordinate))
            .setCoordinates(mapToCoordinates(location))
            .setTimestamp(timeStamp.toDouble())
            .setImageUrl(snapshotS3Path)
            //.setAndroidPose(mapToPose(pose))
            .build()
    }
}


