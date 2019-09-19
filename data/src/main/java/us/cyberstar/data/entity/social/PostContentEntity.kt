package us.cyberstar.data.entity.social

import timber.log.Timber


class PostContentEntity {
    enum class ContentType(val type: Int) {
        PhotoPostContent(1),
        VideoPostContent(2)
    }

    var videoPostContentEntity: VideoPostContentEntity? = null
    var photoPostContentEntity: PhotoPostContentEntity? = null
    var contentType: ContentType? = null

    constructor(photoPostContentEntity: PhotoPostContentEntity) {
        this.photoPostContentEntity = photoPostContentEntity
        contentType = ContentType.PhotoPostContent
    }

    constructor(videoPostContentEntity: VideoPostContentEntity) {
        this.videoPostContentEntity = videoPostContentEntity
        contentType = ContentType.VideoPostContent
    }

    fun setMediaUrl(url: String) {
        when (contentType) {
            ContentType.PhotoPostContent -> {
                photoPostContentEntity!!.photoUrl = url
            }
            ContentType.VideoPostContent -> {
                videoPostContentEntity!!.videoUrl = url
            }
        }
    }

    fun mediaUrl(): String {
        val mediaUrl = when (contentType) {
            ContentType.PhotoPostContent -> {
                photoPostContentEntity!!.photoUrl
            }
            ContentType.VideoPostContent -> {
                videoPostContentEntity!!.videoUrl
            }
            else -> null
        }
        return mediaUrl!!
    }

    fun setThumbnails(thumbnails: Map<String, String>) {
        Timber.d("setThumbnails size=${thumbnails.size}:")
        for (thumb in thumbnails) {
           Timber.d("thumb ${thumb.key} ${thumb.value}")
        }
        when (contentType) {
            ContentType.PhotoPostContent -> {
                photoPostContentEntity!!.thumbs = thumbnails
            }
            ContentType.VideoPostContent -> {
                videoPostContentEntity!!.thumbs = thumbnails
            }
        }
    }

    fun thumbnails(): Map<String, String> {
        val thumbs = when (contentType) {
            ContentType.PhotoPostContent -> {
                photoPostContentEntity!!.thumbs
            }
            ContentType.VideoPostContent -> {
                videoPostContentEntity!!.thumbs
            }
            else -> null
        }
        return thumbs!!
    }

    override fun toString(): String {
        return "PostContentEntity(videoPostContentEntity=$videoPostContentEntity, photoPostContentEntity=$photoPostContentEntity, contentType=$contentType)"
    }
}