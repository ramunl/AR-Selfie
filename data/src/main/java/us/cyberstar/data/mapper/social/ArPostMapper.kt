package us.cyberstar.data.mapper.social

import com.cyber.math.Matrix4
import social.Social
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.mapper.utils.mapToCoordinates
import us.cyberstar.data.mapper.utils.mapToLocation
import us.cyberstar.data.mapper.mapToMatrix4x4
import us.cyberstar.data.mapper.mapToPostCompositeId
import us.cyberstar.data.mapper.mapToPostCompositeIdEntity


fun mapToPostEntityList(array: Collection<Social.Post>): List<ArPostEntity> {
    return array.map { mapToPostEntity(it) }
}

fun mapToPostList(array: Collection<ArPostEntity>): Collection<Social.Post> {
    return array.map { mapToPost(it) }
}

fun mapToPost(arPostEntity: ArPostEntity) = with(arPostEntity) {
    var builder = Social.Post.newBuilder()
        .setId(mapToPostCompositeId(postCompId))
        .setArPoster(mapToArPoster(arPoster))
        .setCoordinates(mapToCoordinates(arPostEntity.location!!))
        .setPostType(Social.PostTypeEnum.original)
        .setArPoster(mapToArPoster(arPostEntity.arPoster))
        .setScaleVariant(scale)

    arPostEntity.anchorId?.let { builder = builder.setGoogleCloudAnchorsRoomId(it) }
    postContentEntity?.let { builder = builder.setContent(mapToPostContent(it)) }
    postTransform?.let { builder = builder.setPostTransform(mapToMatrix4x4(it)) }

    builder.build()
}

fun mapToPostEntity(post: Social.Post): ArPostEntity {
    return with(post) {
        ArPostEntity(
            scaleVariant,
            isQuickPost,
            content.text.textObject.text,
            mapToArPosterEntity(arPoster),
            mapToPostCompositeIdEntity(id),
            Matrix4(postTransform.mList.toFloatArray()),
            mapToLocation(coordinates),
            mapToPostContentEntity(post.content),
            false,
            googleCloudAnchorsRoomId
        )
    }
}


