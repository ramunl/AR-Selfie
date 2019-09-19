package us.cyberstar.framerecorder.media.internal

import android.content.Context
import android.os.Environment
import android.util.AtomicFile
import android.util.Size
import us.cyberstar.common.utils.generateDirUnique
import us.cyberstar.common.utils.getCacheFilesPath
import us.cyberstar.framerecorder.media.external.FPS_DEFAULT
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import java.io.File
import javax.inject.Inject



internal class FrameRecorderSettingsImpl @Inject constructor(context: Context) : FrameRecorderSettings {

    override var outputFile: AtomicFile? = null

    override var videoFormat = "mp4"
    override var fps = FPS_DEFAULT

    override fun refreshOutputFile(uniqueId: String) {
        outputFile?.delete()
        outputFile =
            AtomicFile(File("${generateDirUnique(uniqueId)}video.$videoFormat"))
    }


    override var previewSize = Size(640, 480)
    override var outputFrameSize = Size(640, 480)
    override var cameraId: String = "0" // default value
}