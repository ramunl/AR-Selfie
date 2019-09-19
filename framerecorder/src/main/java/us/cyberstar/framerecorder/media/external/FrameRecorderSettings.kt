package us.cyberstar.framerecorder.media.external

import android.util.AtomicFile
import android.util.Size

val FPS_DEFAULT = 28.0

interface FrameRecorderSettings {
    var cameraId: String
    /* The sides of width and height are based on camera orientation.
     That is, the preview size is the size before it is rotated. */
    var previewSize: Size

    var outputFrameSize: Size
    var fps: Double
    var videoFormat: String
    var outputFile: AtomicFile?
    fun refreshOutputFile(uniqueId: String)
}