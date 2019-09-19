package us.cyberstar.domain.internal.manger

import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.common.utils.ImageCopy
import us.cyberstar.domain.external.loader.SaveVideoFabric
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.framerecorder.media.external.FrameRecorder
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import us.cyberstar.framerecorder.media.external.VideoRecordThread
import javax.inject.Inject

internal class VideoRecorderWrapperImpl @Inject constructor(
    private val uuidProvider: SessionIdProvider,
    private val schedulersProvider: SchedulersProvider,
    private val frameRecorder: FrameRecorder,
    private val videoRecordThread: VideoRecordThread,
    private val saveVideoFabric: SaveVideoFabric,
    private val frameRecorderSettings: FrameRecorderSettings
) : VideoRecorderWrapper {


    private var sessionId: String? = null

    override fun videoRecordInfo(): String {
        val info = StringBuilder()
        info.append("\nvideo:\n sec= ${duration()} frames= ${framesCount()} fps:${frameRecorderSettings.fps} \n")
        with(frameRecorderSettings) {
            outputFile?.let {
                with(it.baseFile) {
                    info.append("$absolutePath\n bytes: ${length()}\n")
                }
            }

            with(previewSize) {
                info.append("w=$width, h=$height")
            }
        }
        return info.toString()
    }

    override fun toggleRecorder(isOn: Boolean) {
        val isRunning = videoRecordThread.isRunning()
        Timber.d("toggleRecorder isOn = $isOn isRunning = $isRunning")
        sessionId = uuidProvider.sessionId()
        if (isOn) {
            if (!isRunning) {
                videoRecordThread.startThread()
                if (!frameRecorder.isRecording()) {
                    Timber.d("frameRecorder start $sessionId")
                    frameRecorder.createRecorder(sessionId!!)
                    frameRecorder.startRecorder()
                }
            }
        } else {
            if (isRunning) {
                videoRecordThread.stopThread()
                if (frameRecorder.isRecording()) {
                    Timber.d("frameRecorder stop")
                    frameRecorder.stopRecorder()
                }
            }
        }
    }

    /**
     * Must be called at least in 1sec after stopRecorder was called
     */
    override fun saveVideoToFile() {
        Timber.d("saveVideoToFile")
        if (sessionId != null) {
            if (frameRecorderSettings.outputFile != null) {
                frameRecorder.releaseRecorder()
                saveVideoFabric.getSaveVideoFabric().saveVideoRequestEntity(
                    SaveVideoRequestEntity(
                        sessionId!!,
                        frameRecorderSettings.outputFile!!.baseFile.absolutePath
                    )
                )
            } else {
                Timber.e("outputFile null")
            }
        } else {
            Timber.e("sessionId null")
        }

    }

    override fun recordFrame(imageCopy: ImageCopy) {
        if (videoRecordThread.isRunning() && frameRecorder.isRecording()) {
            videoRecordThread.onPreviewFrame(imageCopy)
        }

    }

    override fun videoStartTimeStamp(): Long = frameRecorder.videoStartTimeStamp

    override fun isRecording(): Boolean = frameRecorder.isRecording()

    override fun duration() = videoRecordThread.mTotalProcessFrameTime / 1000L //to seconds

    override fun framesCount() = videoRecordThread.mFrameRecordedCount
}