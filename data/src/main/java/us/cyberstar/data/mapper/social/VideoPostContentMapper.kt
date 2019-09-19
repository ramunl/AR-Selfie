package us.cyberstar.data.mapper.social

import base_types.BaseTypes
import social.Social
import us.cyberstar.data.entity.social.VideoPostContentEntity

fun mapToVideoPostContentEntity(photoPostContent: Social.VideoPostContent): VideoPostContentEntity {
    return with(photoPostContent.videoObject) {
        VideoPostContentEntity(width, height, fps, url, thumbnailsMap)
    }
}

//map to protobuf
fun mapToVideoPostContent(videoPostContentEntity: VideoPostContentEntity): Social.VideoPostContent {
    with(videoPostContentEntity) {
        return Social.VideoPostContent.newBuilder()
            .setVideoObject(
                BaseTypes.Video.newBuilder()
                    .putAllThumbnails(thumbs)
                    .setUrl(videoUrl)
                    .setWidth(videoWidth)
                    .setHeight(videoHeight)
                    .build()
            ).build()
    }
}

