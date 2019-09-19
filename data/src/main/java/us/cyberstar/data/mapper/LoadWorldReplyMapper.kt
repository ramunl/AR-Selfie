package us.cyberstar.data.mapper

import base_types.BaseTypes
import proxy.Proxy
import social.Social
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.data.entity.MultipleLoadWorldReplyEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.mapper.social.mapToAssetForDetection
import us.cyberstar.data.mapper.social.mapToAssetForDetectionEntityList
import us.cyberstar.data.mapper.social.mapToPostEntityList
import us.cyberstar.data.mapper.social.mapToPostList


fun mapToLoadWorldReply(createPostRequestEntities: Collection<CreatePostRequestEntity>): Proxy.LoadWorldReply {
    val size = createPostRequestEntities.size
    val assetForDetectionArray = ArrayList<AssetForDetectionEntity>(size)
    val postEntityArray = ArrayList<ArPostEntity>(size)

    for (createPostRequest in createPostRequestEntities) {
        createPostRequest.assetForDetection?.let {
            assetForDetectionArray.add(it)
        }
        postEntityArray.add(createPostRequest.arPostEntity)
    }
    return mapToLoadWorldReply(assetForDetectionArray, postEntityArray)
}

fun mapToLoadWorldReply(
    assetForDetectionArray: Collection<AssetForDetectionEntity>,
    arPostEntityArray: Collection<ArPostEntity>
): Proxy.LoadWorldReply {
    return Proxy.LoadWorldReply.newBuilder()
        .addAllPosts(mapToPostList(arPostEntityArray))
        .addAllAssets(mapToAssetForDetection(assetForDetectionArray)).build()
}


fun mapToLoadWorldReplyEntity(reply: Proxy.MultipleLoadWorldReply): MultipleLoadWorldReplyEntity =
    MultipleLoadWorldReplyEntity(reply.loadWorldReplyList.map { mapToLoadWorldReplyEntity(it) })

fun mapToLoadWorldReplyEntity(proxyLoadWorldReply: Proxy.LoadWorldReply) =
    LoadWorldReplyEntity(
        proxyLoadWorldReply.sessionId,
        proxyLoadWorldReply.sessionLastUpdatedTimestamp,
        mapToAssetForDetectionEntityList(proxyLoadWorldReply.assetsOrBuilderList.map { it as BaseTypes.AssetForDetection }),
        mapToPostEntityList(proxyLoadWorldReply.postsOrBuilderList.map { it as Social.Post })
    )



