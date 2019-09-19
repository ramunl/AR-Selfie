package us.cyberstar.data.mapper.telemetry

import base_types.BaseTypes
import com.google.protobuf.ByteString
import timber.log.Timber
import us.cyberstar.data.entity.telemetry.DataFrameEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.mapper.utils.mapToCoordinates
import us.cyberstar.data.mapper.mapToMatrix4x4
import java.nio.ByteBuffer


fun mapToDataFrame(array: Collection<DataFrameEntity>?) = array?.map { mapToDataFrame(it) }


fun mapToDataFrame(dataFrameEntity: DataFrameEntity): BaseTypes.DataFrame? {
    val pointsCloud = BaseTypes.PointsCloud.newBuilder()
    for ((i, pointWithColor) in dataFrameEntity.pointsWithColor.withIndex()) {
        val point = BaseTypes.Point.newBuilder()
        point.pos = pointWithColor.point
        point.color = pointWithColor.color
        try {
            point.id = dataFrameEntity.pointCloudIds.elementAt(i)
        } catch (e: Exception) {
            point.id = 0
//                Timber.d(e)
        }
        pointsCloud.addPoints4(point)
    }

    val arSessionFrameBuilder = BaseTypes.ARSessionFrame.newBuilder()
    arSessionFrameBuilder.pointsCloud = pointsCloud.build()

    val dataFrame = BaseTypes.DataFrame.newBuilder()
    with(dataFrame) {
        cameraTransform = mapToMatrix4x4(dataFrameEntity.cameraOrientation.asIterable())
        timestamp = dataFrameEntity.frameTime
        gps = mapToCoordinates(dataFrameEntity.location)
        arSessionFrame = arSessionFrameBuilder.build()
    }
    if (!dataFrameEntity.keyPointArray.isNullOrEmpty() && !dataFrameEntity.descriptors.isNullOrEmpty())
        dataFrame.addAllOrbFeaturePoints(
            mapFloatArrayToKeyPoints(
                dataFrameEntity.keyPointArray,
                dataFrameEntity.descriptors
            )
        )
    dataFrame.frameIndex = ArEntityTelemetry.dataFrameEntityCounter++

    return dataFrame.build()
}


fun mapFloatArrayToKeyPoints(
    keyPointArray: List<Float>,
    descriptors: List<ByteArray>
): MutableList<BaseTypes.OrbFeaturePoint> {
    val featurePoints = mutableListOf<BaseTypes.OrbFeaturePoint>()
    var desriptorCounter = 0
    Timber.d("keyPointArray size ${keyPointArray.size}")
    for (i in 0..keyPointArray.size - 5 step 5) {
        val orbFeaturePointBuilder = BaseTypes.OrbFeaturePoint.newBuilder();
        val x = keyPointArray[i]
        val y = keyPointArray[i + 1]
        val angle = keyPointArray[i + 2]
        val size = keyPointArray[i + 3]
        val response = keyPointArray[i + 4]
        orbFeaturePointBuilder.keyPoint = BaseTypes.KeyPoint.newBuilder()
            .setPos(BaseTypes.VectorFloat2.newBuilder().setX(x).setY(y).build())
            .setRadius(size)
            .setResponse(response)
            .setOrientation(angle).build()
        orbFeaturePointBuilder.orbDescriptor = ByteString.copyFrom(descriptors[desriptorCounter++])
        featurePoints.add(orbFeaturePointBuilder.build())
    }
    Timber.d("desriptorCounter  ${desriptorCounter}")
    return featurePoints
}
