package us.cyberstar.data.external.s3

import android.graphics.Bitmap

interface S3Cache {
    fun loadTempFile(filePath: String): Bitmap?
    fun saveTempFile(byteArray: ByteArray, cacheFileType: CacheFileType): String
    fun saveTempFile(bitmap: Bitmap, cacheFileType: CacheFileType): String
}