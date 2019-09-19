package us.cyberstar.data.mapper.social

import social.Social
import us.cyberstar.data.entity.social.PostContentEntity

//map to protobuf
fun mapToPostContent(postContentEntity: PostContentEntity): Social.PostContent {
    var builder = Social.PostContent.newBuilder()
    when (postContentEntity.contentType) {
        PostContentEntity.ContentType.PhotoPostContent ->
            builder = builder.setPhoto(mapToPhotoPostContent(postContentEntity.photoPostContentEntity!!))
        PostContentEntity.ContentType.VideoPostContent ->
            builder = builder.setVideo(mapToVideoPostContent(postContentEntity.videoPostContentEntity!!))
    }
    return builder.build()
}

//map from protobuf model
fun mapToPostContentEntity(postContent: Social.PostContent): PostContentEntity {
    val photoPostContentEntity = postContent.photo?.let { mapToPhotoPostContentEntity(it) }
    val videoPostContentEntity = postContent.video?.let { mapToVideoPostContentEntity(it) }
    return photoPostContentEntity?.let { PostContentEntity(it) }
        ?: PostContentEntity(videoPostContentEntity!!)
}

