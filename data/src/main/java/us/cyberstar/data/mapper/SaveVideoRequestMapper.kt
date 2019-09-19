package us.cyberstar.data.mapper

import proxy.Proxy
import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity

/*
fun mapToSaveVideoRequestEntity(saveVideoRequest: Proxy.SaveVideoRequest) =
    SaveVideoRequestEntity(saveVideoRequest.videoUrl)
*/


fun mapToSaveVideoRequest(saveVideoRequestEntity: SaveVideoRequestEntity) =
    Proxy.SaveVideoRequest.newBuilder()
        .setSessionId(saveVideoRequestEntity.sessionId)
        .setVideoUrl(saveVideoRequestEntity.videoUrl)
        .build()