package us.cyberstar.data.mapper.social

import proxy.Proxy
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity


fun mapToSaveAssetEntity(saveAsset: Proxy.CreateARPostRequest) = with(saveAsset) {
    CreatePostRequestEntity(
        mapToAssetForDetectionEntity(asset),
        mapToCreateARPostRequestEntity(createPostRequest)
        //social.CreatePostRequest
    )
}


fun mapToSaveAsset(createPostRequest: CreatePostRequestEntity) = with(createPostRequest) {
    var builder = Proxy.CreateARPostRequest.newBuilder()
    if (assetForDetection != null) {
        builder = builder.setAsset(mapToAssetForDetection(assetForDetection))
    }
    builder.setCreatePostRequest(mapToCreateARPostRequest(arPostEntity)).build()
}


