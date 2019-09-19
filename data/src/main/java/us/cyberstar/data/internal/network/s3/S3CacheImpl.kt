package us.cyberstar.data.internal.network.s3

import android.content.Context
import android.graphics.Bitmap
import timber.log.Timber
import us.cyberstar.common.utils.generateFileNameUnique
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.external.s3.CacheFileType
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.common.utils.asJpeg
import us.cyberstar.common.utils.fileToBitmap
import us.cyberstar.data.utils.saveToDisk
import javax.inject.Inject

internal class S3CacheImpl @Inject constructor(
    private val context: Context,
    private val sessionIdProvider: SessionIdProvider
) : S3Cache {

    override fun saveTempFile(bitmap: Bitmap, cacheFileType: CacheFileType): String =
        saveTempFile(bitmap.asJpeg()!!, cacheFileType)

    override fun saveTempFile(byteArray: ByteArray, cacheFileType: CacheFileType): String {
        val baseFileName = when (cacheFileType) {
            CacheFileType.SNAPSHOT -> {
                "snapshot"
            }
            CacheFileType.PHOTO_THUMB -> {
                "photo_thumb"
            }
            CacheFileType.VIDEO_THUMB -> {
                "video_thumb"
            }
        }
        val fileName = generateFileNameUnique(sessionIdProvider.sessionId()!!, baseFileName)
        saveToDisk(fileName, byteArray)
        return fileName
    }

    override fun loadTempFile(filePath: String): Bitmap? {
        val res = fileToBitmap(filePath)
        Timber.d("loadTempFile res = $res")
        return res
    }
}