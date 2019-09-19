package us.cyberstar.domain.external.loader.video

import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.domain.external.loader.image.PostRequestImageLoader

interface SaveVideoRequestLoader {
    fun uploadSaveVideoRequestEntity(saveVideoRequestEntity: SaveVideoRequestEntity): PostRequestImageLoader.UploadResult
    fun isUploadedToS3(saveVideoRequestEntity: SaveVideoRequestEntity): Boolean
}