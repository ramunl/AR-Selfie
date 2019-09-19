package us.cyberstar.domain.internal.loader.s3.video

import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.loader.image.PostRequestImageLoader
import us.cyberstar.domain.external.loader.video.SaveVideoRequestLoader
import us.cyberstar.domain.internal.loader.s3.MediaLoaderImpl
import us.cyberstar.domain.internal.loader.s3.isUploadedToS3
import javax.inject.Inject

class SaveVideoRequestLoaderImpl @Inject constructor(
    private val mediaLoader: MediaLoader
) : SaveVideoRequestLoader {

    override fun isUploadedToS3(saveVideoRequestEntity: SaveVideoRequestEntity) =
        saveVideoRequestEntity.videoUrl.isUploadedToS3()

    override fun uploadSaveVideoRequestEntity(saveVideoRequestEntity: SaveVideoRequestEntity): PostRequestImageLoader.UploadResult {
        var isUpladed = false
        var errMessage: String = ""
        try {
            val s3Uri = mediaLoader.uploadMediaSynch(saveVideoRequestEntity.videoUrl, MediaLoaderImpl.MIME_TYPE_VIDEO)
            s3Uri?.let {
                isUpladed = true
                saveVideoRequestEntity.videoUrl = it
            } ?: throw Exception("S3 server video uploading failed!")
        } catch (e: Exception) {
            errMessage = e.toString()
        }
        return PostRequestImageLoader.UploadResult(isUpladed, errMessage)
    }

}