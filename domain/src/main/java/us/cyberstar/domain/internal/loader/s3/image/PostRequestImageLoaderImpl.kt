package us.cyberstar.domain.internal.loader.s3.image

import timber.log.Timber
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.common.utils.asJpeg
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.loader.image.PostRequestImageLoader
import us.cyberstar.domain.internal.loader.s3.MediaLoaderImpl.Companion.MIME_TYPE_IMAGE
import us.cyberstar.domain.internal.loader.s3.MediaLoaderImpl.Companion.MIME_TYPE_VIDEO
import us.cyberstar.domain.internal.loader.s3.isUploadedToS3
import javax.inject.Inject


/**
 * The idea of this util is:
 * 1) to check, are of the CreatePostRequestEntity media files uploaded to server
 * 2) if it is not, it uploads, otherwise return false
 */
internal class PostRequestImageLoaderImpl @Inject constructor(
    private val mediaLoader: MediaLoader
) : PostRequestImageLoader {

    override fun uploadCreatePostRequestEntityImages(createPostRequestEntity: CreatePostRequestEntity): PostRequestImageLoader.UploadResult {
        Timber.d("uploadCreatePostRequestEntityImages $createPostRequestEntity")
        var errMessage = "s3 lmage loading not finished!"
        var isUploaded = true
        try {
            with(createPostRequestEntity) {
                assetForDetection?.let { asset ->
                    asset.snapshotS3Path =
                        mediaLoader.uploadMediaSynch(
                            asset.snapshotBitmap!!.asJpeg()!!,
                            MIME_TYPE_IMAGE
                        )
                    if (asset.snapshotS3Path == null) {
                        throw Exception("S3 server assetForDetection image uploading failed!")
                    }
                }
                with(arPostEntity.arPoster) {
                    val layoutImagesS3Urls = mutableListOf<String>()
                    for (url in layoutImagesUrls) {
                        if (url.isNotEmpty()) {
                            if (!url.isUploadedToS3()) {
                                val urlS3 = mediaLoader.uploadMediaSynch(url, MIME_TYPE_IMAGE)
                                urlS3?.let { layoutImagesS3Urls.add(it) }
                                    ?: throw Exception("S3 server image uploading failed!")
                            } else {
                                //it' already uploaded to S3, so just add it to new array
                                layoutImagesS3Urls.add(url)
                            }
                        }
                        layoutImagesUrls = layoutImagesS3Urls
                    }
                }
                createPostRequestEntity.arPostEntity.postContentEntity?.let { postEntity->
                    var urlS3: String? = null
                    val mediaUrl = postEntity.mediaUrl()

                    if (mediaUrl.isUploadedToS3()) {
                        Timber.d("photoPostContentEntity is uloaded already to s3 $mediaUrl")
                    } else {
                        if(mediaUrl.isEmpty()) {
                            urlS3 = "" // TODO need to understand what to do with 3d posts here
                        } else {
                            urlS3 = mediaLoader.uploadMediaSynch(
                                mediaUrl,
                                if(postEntity.contentType == PostContentEntity.ContentType.PhotoPostContent) MIME_TYPE_IMAGE else MIME_TYPE_VIDEO
                            )
                        }

                    }
                    urlS3?.let { postEntity.setMediaUrl(it) }
                        ?: throw Exception("S3 server image uploading failed!")

                    val thumbS3Urls = HashMap<String, String>()
                    with(postEntity.thumbnails()) {
                        for (thumb in this.iterator()) {
                            val thumbToUpload = thumb.value
                            Timber.d("thumbToUpload $thumbToUpload")
                            if (!thumbToUpload.isUploadedToS3()) {
                                mediaLoader.uploadMediaSynch(
                                    thumbToUpload,
                                    MIME_TYPE_IMAGE
                                )?.let {
                                    thumbS3Urls[thumb.key] = it
                                } ?: throw Exception("S3 server image uploading failed!")
                            } else {
                                //it' already uploaded to S3, so just add it to new array
                                thumbS3Urls[thumb.key] = thumb.value
                            }
                        }
                    }
                    postEntity.setThumbnails(thumbS3Urls)
                }
            }
        } catch (e: Exception) {
            Timber.e("s3 lmage loading failed with $e")
            isUploaded = false
            errMessage = e.toString()
            Timber.e(e)
        }
        return PostRequestImageLoader.UploadResult(isUploaded, errMessage)
    }
}