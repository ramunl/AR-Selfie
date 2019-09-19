package us.cyberstar.data.mapper.social

import com.cyber.math.Matrix4
import social.Social
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.mapper.*
import us.cyberstar.data.mapper.utils.mapToCoordinates
import us.cyberstar.data.mapper.utils.mapToLocation


fun mapToCreateARPostRequestEntity(createPostRequest: Social.CreatePostRequest): ArPostEntity {
    return ArPostEntity(
        createPostRequest.scaleVariant,
        createPostRequest.isQuickPost,
        createPostRequest.content.text.textObject.text,
        mapToArPosterEntity(createPostRequest.arPoster),
        mapToPostCompositeIdEntity(createPostRequest.extraId),
        Matrix4(createPostRequest.postTransform.mList.toFloatArray()),
        mapToLocation(createPostRequest.coordinates),
        mapToPostContentEntity(createPostRequest.content),
        false,
        createPostRequest.googleCloudAnchorsRoomId
    )
}


fun mapToCreateARPostRequest(arPostEntity: ArPostEntity): Social.CreatePostRequest =
    with(arPostEntity) {
        var builder = Social.CreatePostRequest.newBuilder()
            .setTitle(title)
            .setScaleVariant(scale)
            .setCoordinates(mapToCoordinates(location!!))
            .setPostType(Social.PostTypeEnum.original)
            .setExtraId(mapToPostCompositeId(arPostEntity.postCompId))
            .setArPoster(mapToArPoster(arPoster))

        arPostEntity.anchorId?.let { builder.setGoogleCloudAnchorsRoomId(it) }
        isQuick?.let { builder.setIsQuickPost(it) }
        postContentEntity?.let { builder = builder.setContent(mapToPostContent(it)) }
        postTransform?.let { builder = builder.setPostTransform(mapToMatrix4x4(it)) }
        builder.build()
    }