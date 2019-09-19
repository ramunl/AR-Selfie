package us.cyberstar.data

import android.graphics.Bitmap
import timber.log.Timber

interface SnapshotLoadListener {
    fun onUploaded(path: String) {}
    fun onError(exception: Exception) {
        Timber.e(exception)
    }

    fun onDownloaded(it: Bitmap) {}
}