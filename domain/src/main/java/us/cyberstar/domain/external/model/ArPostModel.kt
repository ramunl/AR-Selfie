package us.cyberstar.domain.external.model

import android.os.Parcel
import android.os.Parcelable

/**
 * This class represents a UI post model.
 * It can include Audio, photo, video, text content.
 */
class ArPostModel(var title: String = "") : Parcelable {
    constructor(arPostPhotoModel: ArPostPhotoModel, title: String = "") : this(

    title
    ) {
        this.photoModel = arPostPhotoModel
    }

    constructor(arPostVideoModel: ArPostVideoModel, title: String = "") : this(

    title
    ) {
        this.videoModel = arPostVideoModel
    }

    var videoModel: ArPostVideoModel? = null

    var photoModel: ArPostPhotoModel? = null

    var arPostType: ArPostType? = null

    fun width() = photoModel?.width ?: videoModel!!.width

    fun height() = photoModel?.height ?: videoModel!!.height

    fun mediaPath() = photoModel?.mediaPath ?: videoModel!!.mediaPath

    fun thumbs() = photoModel?.thumbs ?: videoModel!!.thumbs

    fun fps() = videoModel!!.fps

    fun contentType(): ArPostContentType =
        photoModel?.let { ArPostContentType.PHOTO } ?: { ArPostContentType.VIDEO }()

    override fun toString(): String {
        return "ArPostModel(title='$title', arPostType=$arPostType)"
    }

    constructor(source: Parcel) : this(source.readString()!!)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArPostModel> = object : Parcelable.Creator<ArPostModel> {
            override fun createFromParcel(source: Parcel): ArPostModel = ArPostModel(source)
            override fun newArray(size: Int): Array<ArPostModel?> = arrayOfNulls(size)
        }
    }
}