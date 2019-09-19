package us.cyberstar.data.mapper.utils

import base_types.BaseTypes
import com.google.ar.core.Anchor
import us.cyberstar.data.ext.andrMatrix4
import us.cyberstar.data.mapper.mapToMatrix4x4


fun mapToProtoAnchor(array: Collection<Anchor>?): Collection<BaseTypes.Anchor>? =
    array?.map { mapToProtoAnchor(it) }


fun mapToProtoAnchor(anchor: Anchor): BaseTypes.Anchor {
    val orientationMatrix = mapToMatrix4x4(anchor.pose.andrMatrix4())
    return BaseTypes.Anchor.newBuilder().setMatrix(orientationMatrix).setId(anchor.cloudAnchorId).build()
}