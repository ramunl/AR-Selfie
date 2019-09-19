/*
 * Copyright 2018 Google Inc. All Rights Reserved.
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

package us.cyberstar.framerecorder.media.internal

import DecoFrame
import org.bytedeco.javacpp.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.framerecorder.media.RecordFragment
import us.cyberstar.framerecorder.media.external.FrameRecorder
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import us.cyberstar.framerecorder.media.external.RecordFragmentsStack
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * This is a simple example that demonstrates how to use the Camera2 API while sharing camera access
 * with ARCore. An on-screen switch can be used to pause and resume ARCore. The app utilizes a
 * trivial sepia camera effect while ARCore is paused, and seamlessly hands camera capture request
 * control over to ARCore when it is running.
 *
 *
 * This app demonstrates:
 *
 *
 *  * Starting in AR or non-AR mode by setting the initial value of `arMode`
 *  * Toggling between non-AR and AR mode using an on screen switch
 *  * Pausing and resuming the app while in AR or non-AR mode
 *  * Requesting CAMERA_PERMISSION when app starts, and each time the app is resumed
 *
 */
internal class FrameRecorderImpl @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val snackBarProvider: SnackBarProvider,
    private val frameRecorderSettings: FrameRecorderSettings,
    private val recordFragmentsStack: RecordFragmentsStack
) : FrameRecorder {

    override fun isRecording() = isRecording.get()

    // A check mechanism to ensure that the camera closed properly so that the app can safely exit.
    var isRecording = AtomicBoolean(false)
    private var mFrameRecorder: FFmpegFrameRecorder? = null

    override var videoStartTimeStamp: Long = 0

    override fun createRecorder(uniqueId: String) {
        Timber.d("createRecorder..")
        with(frameRecorderSettings) {
            refreshOutputFile(uniqueId)
            Timber.d("Output Video: ${outputFile!!.baseFile!!.absolutePath}")
            with(outputFrameSize) {
                outputFile!!.baseFile!!.apply {
                    val parent = outputFile!!.baseFile!!.parentFile
                    if (!parent!!.exists()) {
                        val res = parent.mkdirs()
                        Timber.d("mkdirs res = $res")
                    }
                }
                mFrameRecorder =
                    FFmpegFrameRecorder(outputFile!!.baseFile, width, height, 0).apply {
                        format = videoFormat
                        frameRate = fps
                        videoCodec = avcodec.AV_CODEC_ID_H264
                        // See: https://trac.ffmpeg.org/wiki/Encode/H.264#crf
                        /*
                         * The range of the quantizer scale is 0-51: where 0 is lossless, 23 is default, and 51
                         * is worst possible. A lower value is a higher quality and a subjectively sane range is
                         * 18-28. Consider 18 to be visually lossless or nearly so: it should look the same or
                         * nearly the same as the input but it isn't technically lossless.
                         * The range is exponential, so increasing the CRF value +6 is roughly half the bitrate
                         * while -6 is roughly twice the bitrate. General usage is to choose the highest CRF
                         * value that still provides an acceptable quality. If the output looks good, then try a
                         * higher value and if it looks bad then choose a lower value.
                         */

                        setVideoOption("crf", "$fps")
                        //setVideoOption("preset", "superfast")
                        setVideoOption("tune", "zerolatency")
                        start()
                    }
            }
        }
        Timber.d("mFrameRecorder initialize success")
    }

    override fun releaseRecorder() {
        try {
            mFrameRecorder?.let {
                it.stop()
                Timber.d("release FrameRecorder ")
                it.release()
                mFrameRecorder = null
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        /*if (deleteFile) {
            frameRecorderSettings.outputFile?.delete()
        }*/
    }

    override fun startRecorder() {
        Timber.d("startRecorder")
        try {
            resumeRecording()
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    override fun stopRecorder() {
        Timber.d("stopRecorder")
        try {
            pauseRecording()

            val res =
                with(frameRecorderSettings.outputFile!!.baseFile) { "size = ${length()} bytes saved to $absolutePath " }
            Timber.d(res)
            schedulersProvider.ui().scheduleDirect(
                { snackBarProvider.showMessage(res) },
                1500,
                TimeUnit.MILLISECONDS
            )
            recordFragmentsStack.clear()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun doResume() {
        if (videoStartTimeStamp == 0L) {
            videoStartTimeStamp = System.currentTimeMillis()
            val recordFragment = RecordFragment()
            Timber.d("do resumeRecording")
            recordFragment.startTimestamp = videoStartTimeStamp
            recordFragmentsStack.push(recordFragment)
        }
    }

    private fun resumeRecording() {
        Timber.d("resume Recording")
        if (!isRecording.get()) {
            videoStartTimeStamp = 0L
            isRecording.set(true)
        }
    }

    private fun pauseRecording() {
        Timber.d("pauseRecording")
        if (isRecording.get()) {
            isRecording.set(false)
            recordFragmentsStack.peek()?.endTimestamp = System.currentTimeMillis()
        }
    }

    override fun setTimestamp(timestamp: Long) {
        mFrameRecorder?.timestamp = timestamp
    }

    override fun getTimestamp() = mFrameRecorder?.timestamp

    override fun record(frame: DecoFrame) {
        try {
            mFrameRecorder?.record(frame)
        } catch (e: java.lang.Exception) {
            Timber.e("frame record failed with: $e")
        }

    }
}
