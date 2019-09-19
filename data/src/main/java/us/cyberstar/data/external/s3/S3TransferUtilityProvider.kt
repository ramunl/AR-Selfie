package us.cyberstar.data.external.s3

import android.graphics.Bitmap
import us.cyberstar.data.SnapshotLoadListener

interface S3TransferUtilityProvider {
    fun downloadMediaAsynch(imgUrl: String, listener: SnapshotLoadListener)
    fun uploadMediaAsynch(bytes: ByteArray?, listener: SnapshotLoadListener, mimeType: String)
    fun uploadMediaSynch(bytes: ByteArray?, mimeType: String): String?
    fun downloadMediaSynch(imgUrl: String): Bitmap?
}