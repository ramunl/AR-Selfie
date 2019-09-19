package us.cyberstar.domain.internal.loader.s3

import android.graphics.Bitmap
import org.apache.commons.io.FileUtils
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.data.external.s3.S3TransferUtilityProvider
import us.cyberstar.data.SnapshotLoadListener
import us.cyberstar.domain.external.loader.s3.MediaLoader
import java.io.File
import javax.inject.Inject


/**
 * The class uploads/downloads images either from local cache or s3 server
 * if an image uri starts with "http" logically it is s3 located
 */

internal class MediaLoaderImpl @Inject constructor(
    private val snackBarProvider: SnackBarProvider,
    private val s3TransferUtilityProvider: S3TransferUtilityProvider,
    private val s3Cache: S3Cache
) : MediaLoader {
    companion object {
        val MIME_TYPE_IMAGE = "image/jpg"
        val MIME_TYPE_VIDEO = "video/mp4"
    }

    override fun uploadMediaSynch(byteArray: ByteArray, mimeType: String): String? {
        return s3TransferUtilityProvider.uploadMediaSynch(byteArray, mimeType)
    }
    // val jpeg = bitmap.asJpeg()

    override fun uploadMediaSynch(localPath: String, mimeType: String): String? {
        Timber.e("uploadMediaSynch $localPath $mimeType")
        val file = File(localPath)
        Timber.d("uploadMediaSynch bytes = $file")
        val bytes = FileUtils.readFileToByteArray(file)
        Timber.d("uploadMediaSynch bytes = ${bytes.size}")
        return uploadMediaSynch(bytes, mimeType)
    }

    override fun uploadImage(localPath: String, mimeType: String, onMediaUploaded: (path: String?) -> (Unit)) {
        val bytes = FileUtils.readFileToByteArray(File(localPath))
        bytes?.let { uploadMedia(bytes, mimeType, onMediaUploaded) } ?: onMediaUploaded.invoke(null)
    }

    override fun uploadMedia(byteArray: ByteArray, mimeType: String, onMediaUploaded: (path: String) -> (Unit)) {
        s3TransferUtilityProvider.uploadMediaAsynch(byteArray, object : SnapshotLoadListener {
            override fun onUploaded(path: String) {
                onMediaUploaded.invoke(path)
            }

            override fun onError(exception: java.lang.Exception) {
                Timber.e(exception)
                //otherwise we save image to local cache
                //  val localFilePath = s3Cache.saveTempFile(jpeg!!)
                //  onMediaUploaded.invoke(localFilePath)
            }
        }, mimeType)
    }

    override fun downLoadImageAsynch(imgUrl: String, onBitmapReady: (bitmap: Bitmap?) -> (Unit)) {
        fun requestImage(imgUrl: String) {
            Timber.d("requestImage imgUrl = $imgUrl")
            s3TransferUtilityProvider.downloadMediaAsynch(imgUrl, object : SnapshotLoadListener {
                override fun onError(exception: Exception) {
                    snackBarProvider.showMessage(exception.toString())
                }

                override fun onDownloaded(it: Bitmap) {
                    onBitmapReady.invoke(it)
                }
            })
        }
        if (imgUrl.isUploadedToS3()) {
            requestImage(imgUrl)
        } else {
            val bitmap = s3Cache.loadTempFile(imgUrl)
            onBitmapReady.invoke(bitmap)
        }
    }

    override fun downLoadImageSynch(imgUrl: String): Bitmap? {
        Timber.d("requestImage imgUrl = $imgUrl")
        return if (imgUrl.isUploadedToS3()) {
            s3TransferUtilityProvider.downloadMediaSynch(imgUrl)
        } else {
            s3Cache.loadTempFile(imgUrl)
        }
    }
}

fun String.isUploadedToS3() = startsWith("http")