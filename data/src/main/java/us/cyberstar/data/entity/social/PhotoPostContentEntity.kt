package us.cyberstar.data.entity.social

import android.graphics.Bitmap

data class PhotoPostContentEntity(
    val photoWidth: Int,
    val photoHeight: Int,
    var photoUrl: String? = null, // it can be changed, if we save image to local storage and latter uploaded it to S3
    var thumbs: Map<String, String>
) {
    override fun toString(): String {
        return "PhotoPostContentEntity(photoWidth=$photoWidth, photoHeight=$photoHeight, photoUrl=$photoUrl, thumbs=$thumbs)"
    }
}