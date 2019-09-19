package us.cyberstar.presentation.helpers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.util.Size
import timber.log.Timber
import us.cyberstar.data.external.s3.CacheFileType
import us.cyberstar.data.external.s3.S3Cache
import java.io.File
import java.io.FileInputStream

//1920x1080
val thumbSize = Size(320, 240)

/*fun getVideoThumbsMap(
    context: Context,
    mediaPath: String,
    s3Cache: S3Cache
): HashMap<String, String> {
    Timber.d("getVideoThumbsMap $mediaPath")

    val thumbBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ThumbnailUtils.createVideoThumbnail(
            File(mediaPath),
            thumbSize,
            null
        );
    } else {
        val videoFile = File(mediaPath)
        val inputStream = FileInputStream(videoFile)
        val fileDescriptor = inputStream.fd
        MediaMetadataRetriever().let {
            it.setDataSource(fileDescriptor)
            it.frameAtTime
        }

    }
    val thumbMap = HashMap<String, String>()
    val thumbBitmapPath = s3Cache.saveTempFile(thumbBitmap, CacheFileType.VIDEO_THUMB)
    Timber.d("saved to $thumbBitmapPath")
    val key = with(thumbSize) { "${width}x$height" }
    thumbMap[key] = thumbBitmapPath
    return thumbMap
}*/

fun getPhotoThumbsMap(
    mediaPath: String,
    s3Cache: S3Cache
): HashMap<String, String> {
    val thumbBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ThumbnailUtils.createImageThumbnail(
            File(mediaPath),
            thumbSize,
            null
        );
    } else {
        ThumbnailUtils.extractThumbnail(
            s3Cache.loadTempFile(mediaPath)!!,
            thumbSize.width,
            thumbSize.height
        )
    }
    val thumbMap = HashMap<String, String>()
    val thumbBitmapPath = s3Cache.saveTempFile(thumbBitmap!!, CacheFileType.PHOTO_THUMB)
    val key = with(thumbSize) { "${width}x$height" }
    thumbMap[key] = thumbBitmapPath
    return thumbMap
}