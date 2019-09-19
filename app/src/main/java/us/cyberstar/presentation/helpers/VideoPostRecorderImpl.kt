package us.cyberstar.presentation.helpers

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import io.reactivex.Maybe
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.model.ArPostVideoModel
import us.cyberstar.presentation.feature.cameraView.utils.VideoRecorder
import us.cyberstar.presentation.feature.cameraView.utils.VideoRecorderImpl
import javax.inject.Inject

class VideoPostRecorderImpl @Inject constructor(
    private val videoRecorder: VideoRecorder,
    private val s3Cache: S3Cache,
    private val context: Context,
    private val snackBarProvider: SnackBarProvider
) : VideoPostRecorder {


    override fun startRecord() {
        Timber.d("startRecord")
        videoRecorder.start()
    }

    override fun stopRecord(): Maybe<ArPostVideoModel> {
        Timber.d("stopRecord")
        return Maybe.create {
            if(videoRecorder.stop()) {
                try {
                    val videoPath = videoRecorder.videoFile!!.path
                    snackBarProvider.showMessage("Video saved: $videoPath")
                    // Send  notification of updated content.
                    val values = ContentValues()
                    values.put(MediaStore.Video.Media.TITLE, "Video post")
                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    values.put(MediaStore.Video.Media.DATA, videoPath)
                    context.contentResolver.insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                    with(videoRecorder) {
                        val with = videoSize.width
                        val height = videoSize.height
                        val path = videoPath
                        val fps = VideoRecorderImpl.DEFAULT_FRAMERATE
                        it.onSuccess(
                            ArPostVideoModel(
                                with, height, path, fps.toDouble(),
                                videoRecorder.thumbnailsArray
                            )
                        )
                    }
                } catch (e: Throwable) {
                    Timber.e("onToggleRecord $e")
                    it.onError(e)
                }
            } else {
                it.onComplete()
            }

        }
    }
}