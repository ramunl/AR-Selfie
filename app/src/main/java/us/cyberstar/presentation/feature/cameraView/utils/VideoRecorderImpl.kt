/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.cyberstar.presentation.feature.cameraView.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.PixelCopy
import android.view.Surface

import com.google.ar.sceneform.SceneView
import timber.log.Timber
import us.cyberstar.common.utils.generateDirUnique
import us.cyberstar.common.utils.generateFileNameUnique
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.external.s3.CacheFileType
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.helpers.thumbSize

import java.io.File
import java.io.IOException
import java.util.*

import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Video Recorder class handles recording the contents of a SceneView. It uses MediaRecorder to
 * encode the video. The quality settings can be set explicitly or simply use the CamcorderProfile
 * class to select a predefined set of parameters.
 */
class VideoRecorderImpl @Inject constructor(
    private val s3Cache: S3Cache,
    private val context: Context,
    private val sessionIdProvider: SessionIdProvider,
    private val arCoreSceneView: ArCoreSceneView
) : VideoRecorder {

    val THUMB_COLLECT_DELAY = 1000L
    val THUMB_COLLECT_TIMER = 3000L
    val THUMB_COUNT_NEEDED = 3
    private var sceneView: SceneView? = null
    private var videoCodec: Int = 0
    override var videoFile: File? = null
    private var bitRate = DEFAULT_BITRATE
    private var frameRate = DEFAULT_FRAMERATE
    private var encoderSurface: Surface? = null


    override fun start() {
        if (!isRecording) {
            startRecordingVideo()
            startThumbCollectTimer()
        }
    }

    override fun stop(): Boolean {
        return if (isRecording) {
            stopThumbCollectTimer()
            stopRecordingVideo()
            true
        } else {
            false
        }
    }

    var timer: Timer? = null

    init {
        Timber.d("VideoRecorderImpl init")
        val orientation = context.resources.configuration.orientation
        setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation)
        setSceneView(arCoreSceneView.arSceneView)
    }

    // recordingVideoFlag is true when the media recorder is capturing video.
    var isRecording: Boolean = false
        private set

    lateinit var mediaRecorder: MediaRecorder

    override lateinit var videoSize: Size


    init {
        isRecording = false
    }

    fun setBitRate(bitRate: Int) {
        this.bitRate = bitRate
    }

    fun setFrameRate(frameRate: Int) {
        this.frameRate = frameRate
    }

    fun setSceneView(sceneView: SceneView) {
        this.sceneView = sceneView
    }


    private fun startThumbCollectTimer() {
        Timber.d("startThumbCollectTimer")
        val surfaceView = arCoreSceneView.arSceneView
        handlerThread = HandlerThread("PixelCopier").apply { start() }
        timer = Timer().apply {
            scheduleAtFixedRate(THUMB_COLLECT_DELAY, THUMB_COLLECT_TIMER) {
                val bitmap =
                    Bitmap.createBitmap(thumbSize.width, thumbSize.height, Bitmap.Config.ARGB_8888)
                PixelCopy.request(arCoreSceneView.arSceneView, bitmap, { copyResult: Int ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        val path = s3Cache.saveTempFile(bitmap, CacheFileType.VIDEO_THUMB)
                        Timber.d("video thumb added $path")
                        val key = with(thumbSize) { "${width}x$height" }
                        thumbnailsArray[key] = path
                        stopThumbCollectTimer()
                    }
                    /*if (thumbnailsArray.size == THUMB_COUNT_NEEDED) {
                        stopThumbCollectTimer()//we take only first 3 frames for thumbnails
                    }*/
                }, Handler(handlerThread!!.looper))
            }
        }
    }

    fun stopThumbCollectTimer() {
        Timber.d("stopThumbCollectTimer")
        timer?.let {
            it.cancel()
            timer = null
            handlerThread?.quitSafely()
            handlerThread?.interrupt()
            handlerThread = null
        }
    }

    /**
     * Toggles the state of video recording.
     *
     * @return true if recording is now active.
     */
    var handlerThread: HandlerThread? = null
    override var thumbnailsArray = HashMap<String, String>()


    private fun startRecordingVideo() {
        Timber.d("startRecordingVideo")
        mediaRecorder = MediaRecorder()
        // buildFilename()
        setUpMediaRecorder()
        // Set up Surface for the MediaRecorder
        encoderSurface = mediaRecorder.surface

        sceneView!!.startMirroringToSurface(
            encoderSurface, 0, 0, videoSize.width, videoSize.height
        )
        isRecording = true
    }


    private fun stopRecordingVideo() {
        Timber.d("stopRecordingVideo")
        isRecording = false
        if (encoderSurface != null) {
            sceneView!!.stopMirroringToSurface(encoderSurface)
            encoderSurface = null
        }
        // Stop recording
        mediaRecorder.stop()
        mediaRecorder.reset()
    }

    @Throws(IOException::class)
    private fun setUpMediaRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        val sessionId = sessionIdProvider.sessionId()!!
        val pathParent = File(generateDirUnique(sessionId))
        if (!pathParent.exists()) {
            pathParent.mkdirs()
        }
        videoFile = File(generateFileNameUnique(sessionId, "video_post.mp4"))
        videoFile!!.createNewFile()
        mediaRecorder.setOutputFile(videoFile!!.absolutePath)
        mediaRecorder.setVideoEncodingBitRate(bitRate)
        mediaRecorder.setVideoFrameRate(frameRate)
        mediaRecorder.setVideoSize(videoSize.width, videoSize.height)
        mediaRecorder.setVideoEncoder(videoCodec)
        mediaRecorder.prepare()
        mediaRecorder.start()
    }

    fun setVideoSize(width: Int, height: Int) {
        videoSize = Size(width, height)
    }

    fun setVideoQuality(quality: Int, orientation: Int) {
        var profile: CamcorderProfile? = null
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality)
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (level in FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level)
                    break
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile!!.videoFrameWidth, profile.videoFrameHeight)
        } else {
            setVideoSize(profile!!.videoFrameHeight, profile.videoFrameWidth)
        }
        setVideoCodec(profile.videoCodec)
        setBitRate(profile.videoBitRate)
        setFrameRate(profile.videoFrameRate)
    }

    fun setVideoCodec(videoCodec: Int) {
        this.videoCodec = videoCodec
    }

    companion object {
        private val DEFAULT_BITRATE = 10000000
        val DEFAULT_FRAMERATE = 30

        private val FALLBACK_QUALITY_LEVELS = intArrayOf(
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
        )
    }
}
