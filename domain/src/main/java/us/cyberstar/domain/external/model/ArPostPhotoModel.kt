package us.cyberstar.domain.external.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * This class represents a UI post model.
 * It can include Audio, photo, video, text content.
 */
data class ArPostPhotoModel(
    val width: Int,
    val height: Int,
    val mediaPath: String,
    val thumbs: HashMap<String, String>
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readString()!!,
        source.readSerializable() as HashMap<String, String>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(width)
        writeInt(height)
        writeString(mediaPath)
        writeSerializable(thumbs)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArPostPhotoModel> =
            object : Parcelable.Creator<ArPostPhotoModel> {
                override fun createFromParcel(source: Parcel): ArPostPhotoModel =
                    ArPostPhotoModel(source)

                override fun newArray(size: Int): Array<ArPostPhotoModel?> = arrayOfNulls(size)
            }
    }
}