package us.cyberstar.data.entity.social

import android.graphics.Bitmap

data class VideoPostContentEntity(
    val videoWidth: Int,
    val videoHeight: Int,
    val fps: Double,
    var videoUrl: String? = null, // it can be changed, if we save image to local storage and latter uploaded it to S3
    var thumbs: Map<String, String>
)