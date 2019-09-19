package us.cyberstar.data.mapper

import proxy.Proxy
import us.cyberstar.data.entity.telemetry.LoadWorldRequestEntity
import us.cyberstar.data.mapper.utils.mapToCoordinates

fun mapToLoadWorldRequest(loadWorldRequestEntity: LoadWorldRequestEntity): Proxy.LoadWorldRequest {
    return Proxy.LoadWorldRequest.newBuilder().setCoordinates(
        mapToCoordinates(loadWorldRequestEntity.location)
    ).build()
}