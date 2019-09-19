package us.cyberstar.domain.external.loader.s3

import android.graphics.Bitmap

interface MediaLoader {
    fun downLoadImageAsynch(imgUrl: String, onBitmapReady: (bitmap: Bitmap?) -> Unit)
    fun uploadMediaSynch(byteArray: ByteArray, mimeType: String): String?
    fun uploadMediaSynch(localPath: String, mimeType: String): String?
    fun uploadMedia(byteArray: ByteArray, mimeType: String, onMediaUploaded: (path: String) -> Unit)
    fun uploadImage(localPath: String, mimeType: String, onMediaUploaded: (path: String?) -> Unit)
    fun downLoadImageSynch(imgUrl: String): Bitmap?
}