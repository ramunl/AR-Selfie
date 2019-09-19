package us.cyberstar.presentation.feature.cameraView.utils

import android.util.Size
import java.io.File

interface VideoRecorder {
    var videoFile: File?
    fun start()
    fun stop(): Boolean
    var videoSize: Size
    var thumbnailsArray: HashMap<String, String>
}