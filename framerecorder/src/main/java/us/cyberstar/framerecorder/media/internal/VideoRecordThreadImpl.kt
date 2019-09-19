package us.cyberstar.framerecorder.media.internal

import DecoFrame
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.text.TextUtils
import android.view.Surface
import android.view.WindowManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import org.bytedeco.javacpp.avutil
import org.bytedeco.javacv.FFmpegFrameFilter
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.utils.ImageCopy
import us.cyberstar.common.utils.YUV_420_888toNV21
import us.cyberstar.framerecorder.media.FrameToRecord
import us.cyberstar.framerecorder.media.external.FrameRecorder
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import us.cyberstar.framerecorder.media.external.RecordFragmentsStack
import us.cyberstar.framerecorder.media.external.VideoRecordThread
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


internal class VideoRecordThreadImpl @Inject constructor(
    private val settings: FrameRecorderSettings,
    private val recordFragmentsStack: RecordFragmentsStack,
    private val frameRecorder: FrameRecorder,
    private val schedulersProvider: SchedulersProvider,
    private val context: Context
) : VideoRecordThread {

    private val frameEmitter = BehaviorSubject.create<ImageCopy>()

    fun subscribe() {
        compositeDisposable = CompositeDisposable()
        frameEmitter
            .observeOn(schedulersProvider.io())
            .subscribeOn(schedulersProvider.io())
            .subscribe(
                {
                    frameRecorder.doResume()
                    doProcessFrame(it)
                },
                { Timber.e("sourceObservable $it") },
                { Timber.d("sourceObservable onComplete called") })
            .addTo(compositeDisposable!!) // TODO clean it where it needs to be
    }


    private var isRunning = AtomicBoolean(false)
    private var compositeDisposable: CompositeDisposable? = null
    // Prevent any changes to camera capture session after CameraManager.openCamera() is called, but
    // before camera device becomes active.
    private val frameDepth = org.bytedeco.javacv.Frame.DEPTH_UBYTE
    private val frameChannels = 2
    override var mFrameRecordedCount: Int = 0

    override var mTotalProcessFrameTime: Long = 0

    private var lastPreviewFrameTime: Long = 0
    private var mFrameToRecordCount: Int = 0
    private val mFrameToRecordQueue = LinkedBlockingQueue<FrameToRecord>(10)
    private val mRecycledFrameQueue = LinkedBlockingQueue<FrameToRecord>(20)


    private val cameraManager: CameraManager by lazy { context.getSystemService(CAMERA_SERVICE) as CameraManager }


    private fun initRecorderSettings() {
        Timber.d("video record thread run..")
        val previewHeight = settings.previewSize.height
        val previewWidth = settings.previewSize.width
        val videoHeight = settings.outputFrameSize.height
        val videoWidth = settings.outputFrameSize.width

        val filters = ArrayList<String>()
        // Transpose
        var transpose: String? = null
        var hflip: String? = null
        var vflip: String? = null
        var crop: String? = null
        var scale: String? = null
        val cropWidth: Int
        val cropHeight: Int

        val characteristics = cameraManager.getCameraCharacteristics(settings.cameraId)
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)!!
        val orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> {
                when (orientation) {
                    270 -> if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        transpose = "transpose=clock_flip" // Same as preview display
                    } else {
                        transpose = "transpose=cclock" // Mirrored horizontally as
                        // preview display
                    }
                    90 -> if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        transpose = "transpose=cclock_flip" // Same as preview display
                    } else {
                        transpose = "transpose=clock" // Mirrored horizontally as
                        // preview display
                    }
                }
                cropWidth = previewHeight
                cropHeight = cropWidth * videoHeight / videoWidth
                crop = String.format(
                    "crop=%d:%d:%d:%d",
                    cropWidth, cropHeight,
                    (previewHeight - cropWidth) / 2, (previewWidth - cropHeight) / 2
                )
                // swap width and height
                scale = String.format("scale=%d:%d", videoHeight, videoWidth)
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                when (rotation) {
                    Surface.ROTATION_90 ->
                        // landscape-left
                        when (orientation) {
                            270 -> if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                                hflip = "hflip"
                            }
                        }
                    Surface.ROTATION_270 ->
                        // landscape-right
                        when (orientation) {
                            90 -> if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                                hflip = "hflip"
                                vflip = "vflip"
                            }
                            270 -> if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                                vflip = "vflip"
                            }
                        }
                }
                cropHeight = previewHeight
                cropWidth = cropHeight * videoWidth / videoHeight
                crop = String.format(
                    "crop=%d:%d:%d:%d",
                    cropWidth, cropHeight,
                    (previewWidth - cropWidth) / 2, (previewHeight - cropHeight) / 2
                )
                scale = String.format("scale=%d:%d", videoWidth, videoHeight)
            }
            Surface.ROTATION_180 -> {
            }
        }
        // transpose
        if (transpose != null) {
            filters.add(transpose)
        }
        // horizontal flip
        if (hflip != null) {
            filters.add(hflip)
        }
        // vertical flip
        if (vflip != null) {
            filters.add(vflip)
        }
        // crop
        if (crop != null) {
            filters.add(crop)
        }
        // scale (to designated size)
        if (scale != null) {
            filters.add(scale)
        }

        val frameFilter = FFmpegFrameFilter(TextUtils.join(",", filters), previewWidth, previewHeight);
        frameFilter.pixelFormat = avutil.AV_PIX_FMT_NV21;
        frameFilter.frameRate = settings.fps;
        try {
            frameFilter.start();
        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    private fun run() {
        if (isRunning.get() && mFrameToRecordQueue.size > 0) {
            try {
                val recordedFrame = mFrameToRecordQueue.take()
                frameRecorder.getTimestamp()?.let {
                    val timestamp = recordedFrame.timestamp
                    if (timestamp > it) {
                        frameRecorder.setTimestamp(timestamp)
                    }
                    val startTime = System.currentTimeMillis()

                    try {
                        frameRecorder.record(recordedFrame.frame)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val endTime = System.currentTimeMillis()
                    val processTime = endTime - startTime
                    mTotalProcessFrameTime += processTime
                    //Timber.d("This frame process time: " + processTime + "ms")
                    //val totalAvg = mTotalProcessFrameTime / ++mFrameRecordedCount
                    //  Timber.d("Avg frame process time: " + totalAvg + "ms")
                    ++mFrameRecordedCount
                    //Timber.d("$mFrameRecordedCount / $mFrameToRecordCount")
                    mRecycledFrameQueue.offer(recordedFrame)
                } ?: {
                    Timber.e("frameRecorder getTimestamp failed")
                }()
            } catch (ie: InterruptedException) {
                Timber.e(ie)
                /*try {
                    frameFilter.stop();
                } catch (e: Exception) {
                    Timber.e(e);
                }*/
                // break
            }
        }
    }


    override fun onPreviewFrame(imageCopy: ImageCopy) {
        frameEmitter.onNext(imageCopy)
    }

    private fun doProcessFrame(imageCopy: ImageCopy) {
        lastPreviewFrameTime = System.currentTimeMillis()
        // get video data
        //if (isRunning.get()) {
            // pop the current record fragment when calculate total recorded time
            val curFragment = recordFragmentsStack.pop()
            curFragment?.let {
                val recordedTime = recordFragmentsStack.calculateTotalRecordedTime()
                // push it back after calculation
                recordFragmentsStack.push(it)
                val curRecordedTime = System.currentTimeMillis() - it.startTimestamp + recordedTime
                val timestamp = 1000 * curRecordedTime
                var frameToRecord: FrameToRecord? = null
                var frame: DecoFrame? = null
                frameToRecord?.let {
                    frame = it.frame
                    it.timestamp = timestamp
                    frameToRecord = mRecycledFrameQueue.poll()
                } ?: {
                    val previewHeight = settings.previewSize.height
                    val previewWidth = settings.previewSize.width
                    frame = DecoFrame(previewWidth, previewHeight, frameDepth, frameChannels)
                    frameToRecord = FrameToRecord(timestamp, frame!!)
                }()
                frame?.let {
                    (it.image[0].position(0) as ByteBuffer).put(YUV_420_888toNV21(imageCopy))
                }

                if (mFrameToRecordQueue.offer(frameToRecord)) {
                    mTotalProcessFrameTime++
                }
            }
        //}
    }


    override fun startThread() {
        isRunning.set(true)
        mFrameToRecordQueue.clear()
        mRecycledFrameQueue.clear()
        //initRecorderSettings()
        subscribe()
        Timber.d("videoRecordThread startThread")
        //settings.fps.toLong()
        schedulersProvider.io().schedulePeriodicallyDirect({ run() }, 0, 10L, TimeUnit.MILLISECONDS)
            .addTo(compositeDisposable!!)
    }

    override fun isRunning() = isRunning.get()

    override fun stopThread() {
        Timber.d("videoRecordThread stopThread")
        if (isRunning.get()) {
            isRunning.set(false)
            compositeDisposable!!.clear()
            mFrameToRecordCount = 0
            mFrameRecordedCount = 0
            lastPreviewFrameTime = 0
            mTotalProcessFrameTime = 0
            mTotalProcessFrameTime = 0
        }

    }
}