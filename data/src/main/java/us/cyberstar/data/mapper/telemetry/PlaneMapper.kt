package us.cyberstar.data.mapper.telemetry

import base_types.BaseTypes
import com.google.ar.core.Plane
import us.cyberstar.data.entity.telemetry.ArPlaneEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry.Companion.arPlaneEntityCounter
import us.cyberstar.data.ext.asArray
import us.cyberstar.data.mapper.utils.mapToPose
import us.cyberstar.data.mapper.utils.mapToProtoAnchor
import us.cyberstar.data.mapper.utils.mapToVectorFloat2
import us.cyberstar.data.mapper.utils.mapToVectorFloat2List


fun mapToPlane(arPlainArray: Collection<ArPlaneEntity>?): Collection<BaseTypes.Plane>? =
    arPlainArray?.map { mapToPlane(it) }

fun mapToPlane(plane: ArPlaneEntity): BaseTypes.Plane =
    with(plane) {
        BaseTypes.Plane.newBuilder()
            .setIsVertical(type == Plane.Type.VERTICAL)
            .addAllAndroidVertices(mapToVectorFloat2List(polygon))
            .addAllAnchors(mapToProtoAnchor(anchors))
            .setAndroidExtentXz(mapToVectorFloat2(extentX, extentZ))
            .setAndroidPose(mapToPose(pose))
            .setFrameIndex(arPlaneEntityCounter++)
            .build()
    }