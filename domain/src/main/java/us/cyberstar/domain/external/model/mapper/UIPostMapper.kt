package us.cyberstar.domain.external.model.mapper

import social.Social
import us.cyberstar.data.entity.social.PhotoPostContentEntity
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.data.entity.social.VideoPostContentEntity
import us.cyberstar.domain.external.model.ArPostContentType
import us.cyberstar.domain.external.model.ArPostModel


fun getPostContentEntityFrom(arPostModel: ArPostModel): PostContentEntity {
    return with(arPostModel) {
        when (arPostModel.contentType()) {
            ArPostContentType.PHOTO ->
                PostContentEntity(PhotoPostContentEntity(width(), height(), mediaPath(), thumbs()))

            ArPostContentType.VIDEO -> {
                PostContentEntity(
                    VideoPostContentEntity(
                        width(),
                        height(),
                        fps(),
                        mediaPath(),
                        thumbs()
                    )
                )
            }
        }
    }
}