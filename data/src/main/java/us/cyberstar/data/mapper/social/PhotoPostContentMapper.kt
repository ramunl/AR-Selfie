package us.cyberstar.data.mapper.social

import base_types.BaseTypes
import social.Social
import us.cyberstar.data.entity.social.PhotoPostContentEntity
import us.cyberstar.data.entity.social.PostContentEntity

fun mapToPhotoPostContentEntity(photoPostContent: Social.PhotoPostContent): PhotoPostContentEntity {
    return with(photoPostContent.photoObject) {
        PhotoPostContentEntity(width, height, url, thumbnailsMap)
    }
}
//map to protobuf
fun mapToPhotoPostContent(photoPostContentEntity: PhotoPostContentEntity): Social.PhotoPostContent {
    with(photoPostContentEntity) {
        return Social.PhotoPostContent.newBuilder()
            .setPhotoObject(
                BaseTypes.Photo.newBuilder()
                    .putAllThumbnails(thumbs)
                    .setUrl(photoUrl)
                    .setWidth(photoWidth)
                    .setHeight(photoHeight)
                    .build()
            ).build()
    }
}

