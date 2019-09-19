package us.cyberstar.domain.external.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * This class represents a UI post model.
 * It can include Audio, photo, video, text content.
 */
data class ArPostVideoModel(
    val width: Int,
    val height: Int,
    val mediaPath: String,
    val fps: Double,
    val thumbs: HashMap<String, String>
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readString()!!,
        source.readDouble(),
        source.readSerializable() as HashMap<String, String>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(width)
        writeInt(height)
        writeString(mediaPath)
        writeDouble(fps)
        writeSerializable(thumbs)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArPostVideoModel> =
            object : Parcelable.Creator<ArPostVideoModel> {
                override fun createFromParcel(source: Parcel): ArPostVideoModel =
                    ArPostVideoModel(source)

                override fun newArray(size: Int): Array<ArPostVideoModel?> = arrayOfNulls(size)
            }
    }
}