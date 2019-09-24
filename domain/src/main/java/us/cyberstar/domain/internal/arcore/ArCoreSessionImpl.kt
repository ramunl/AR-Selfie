package us.cyberstar.domain.internal.arcore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.ConditionVariable
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.SharedCamera
import com.google.ar.core.exceptions.CameraNotAvailableException
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.common.utils.imageCopy
import us.cyberstar.data.BuildConfig
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

//TODO have no idea who will continue this shit, you will need to refactor this class,
// it's too much shit here in one class
internal class ArCoreSessionImpl @Inject constructor(
    private val framerRecorderSettings: FrameRecorderSettings,
    private val context: Context,
    private val snackBarProvider: SnackBarProvider,
    private val schedulersProvider: SchedulersProvider,
    private val videoRecorderWrapper: VideoRecorderWrapper
) : ArCoreSession {


    private lateinit var flavor: String

    //it's a dirty shity hack to pass app flavor
    override fun setupFlavor(flavor: String) {
        this.flavor = flavor
    }

    var useCameraSharing = false

    override val session: Session by lazy { createSession() }

    private fun createSession(): Session {
        Timber.d("createSession")
        var session: Session? = null
        try {
            session = if (useCameraSharing) Session(
                context,
                EnumSet.of(Session.Feature.SHARED_CAMERA)
            ) else Session(context)
            session.apply {
                val config = Config(this)
                Timber.d("sharedSession initialized!")
                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                when (flavor) {
                    "arcreator" -> {
                        config.cloudAnchorMode = Config.CloudAnchorMode.ENABLED
                        Timber.e(".............cloudAnchorMode enabled.................")
                    }
                    "serviceApp" -> {
                        config.augmentedImageDatabase =
                            AugmentedImageDatabase(this) // TODO move db to DI
                        Timber.e(".............AugmentedImageDatabase init.................")
                    }
                }
                configure(config)
            }
        } catch (e: Exception) {
            snackBarProvider.showError(e.toString(), false)
        }

        return session!!
    }

    private val lock = Object()
    /* The number of seconds in the continuous record loop (or 0 to disable loop). */
    // Ensure GL surface draws only occur when new frames are available.
    private val shouldUpdateSurfaceTexture = AtomicBoolean(false)
    // Various helper classes, see hello_ar_java sample to learn more.
    // Renderers, see hello_ar_java sample to learn more.
    // Temporary matrix allocated here to reduce number of allocations for each lastFrame.
    // Anchors created from taps, see hello_ar_java sample to learn more.
    private val automatorRun = AtomicBoolean(false)
    // A check mechanism to ensure that the camera closed properly so that the app can safely exit.
    private val safeToExitApp = ConditionVariable()
    // Repeating camera capture session capture callback.
    private val captureSessionCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            shouldUpdateSurfaceTexture.set(true)
        }

        override fun onCaptureBufferLost(
            session: CameraCaptureSession,
            request: CaptureRequest,
            target: Surface,
            frameNumber: Long
        ) {
            Timber.e("onCaptureBufferLost: $frameNumber")
        }

        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            // Timber.e( "onCaptureFailed: " + failure.getFrameNumber() + " " + failure.getReason());
        }

        override fun onCaptureSequenceAborted(
            session: CameraCaptureSession, sequenceId: Int
        ) {
            Timber.e("onCaptureSequenceAborted: $sequenceId $session")
        }
    }
    internal var isProcessing = false
    //CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
    // ARCore session that supports camera sharing.
    // Camera capture session. Used by both non-AR and AR modes.
    private var captureSession: CameraCaptureSession? = null
    // Reference to the camera system service.
    private var cameraManager: CameraManager? = null
    // A list of CaptureRequest keys that can cause delays when switching between AR and non-AR modes.
    private var keysThatCanCauseCaptureDelaysWhenModified: List<CaptureRequest.Key<*>>? = null
    // Camera device. Used by both non-AR and AR modes.
    private var cameraDevice: CameraDevice? = null
    // Looper handler thread.
    private var backgroundThread: HandlerThread? = null
    // Looper handler.
    private var backgroundHandler: Handler? = null
    // ARCore shared camera instance, obtained from ARCore session that supports sharing.
    private var sharedCamera: SharedCamera? = null
    // Camera ID for the camera used by ARCore.
    private var mCameraId: String? = null
    // Whether ARCore is currently active.
    private var arcoreActive: Boolean = false
    // Whether the GL surface has been created.
    // private var surfaceCreated: Boolean = false
    // Camera preview capture request builder
    private var previewCaptureRequestBuilder: CaptureRequest.Builder? = null
    // Image reader that continuously processes CPU images.
    private var cpuImageReader: ImageReader? = null

    // Prevent any changes to camera capture session after CameraManager.openCamera() is called, but
    // before camera device becomes active.
    private var captureSessionChangesPossible = true
    // Repeating camera capture session state callback.
    internal var cameraCaptureCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            // Called when the camera capture session is first configured after the app
            // is initialized, and again each time the activity is resumed.
            override fun onConfigured(session: CameraCaptureSession) {
                Timber.d("Camera capture session configured.")
                captureSession = session
                setRepeatingCaptureRequest()
                // Note, resumeARCore() must be called in onActive(), not here.
            }

            override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) {
                Timber.d("Camera capture surface prepared.")
            }

            override fun onReady(session: CameraCaptureSession) {
                Timber.d("Camera capture session ready.")
            }

            override fun onActive(session: CameraCaptureSession) {
                Timber.d("Camera capture session active.")
                if (!arcoreActive) {
                    resumeARCore()
                }
                synchronized(this@ArCoreSessionImpl) {
                    captureSessionChangesPossible = true
                    synchronized(lock) {
                        lock.notify()
                    }

                }
                //updateSnackbarMessage()
            }

            override fun onCaptureQueueEmpty(session: CameraCaptureSession) {
                Timber.w("Camera capture queue empty.")
            }

            override fun onClosed(session: CameraCaptureSession) {
                Timber.d("Camera capture session closed.")
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Timber.e("Failed to configure camera capture session.")
            }
        }
    // Camera device state callback.
    private val cameraDeviceCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            Timber.d("Camera device ID " + cameraDevice.id + " opened.")
            this@ArCoreSessionImpl.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onClosed(cameraDevice: CameraDevice) {
            Timber.d("Camera device ID " + cameraDevice.id + " closed.")
            this@ArCoreSessionImpl.cameraDevice = null
            safeToExitApp.open()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            Timber.w("Camera device ID " + cameraDevice.id + " disconnected.")
            cameraDevice.close()
            this@ArCoreSessionImpl.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            Timber.e("Camera device ID " + cameraDevice.id + " error " + error)
            cameraDevice.close()
            this@ArCoreSessionImpl.cameraDevice = null
            // Fatal error. Quit application.
        }
    }


    @Synchronized
    private fun waitUntilCameraCaptureSesssionIsActive() {
        while (!captureSessionChangesPossible) {
            synchronized(lock) {
                lock.wait()
            }

        }
    }

    override fun onResume() {
        if(useCameraSharing) {
            startBackgroundThread()
            waitUntilCameraCaptureSesssionIsActive()
        }
        resumeARCore()
        if(useCameraSharing) {
            // When the activity starts and resumes for the first time, openCamera() will be called
            // from onSurfaceCreated(). In subsequent resumes we call openCamera() here.
            openCamera()
        }
    }

    override fun onPause() {
        if(useCameraSharing) {
            waitUntilCameraCaptureSesssionIsActive()
        }
        pauseARCore()
        if(useCameraSharing) {
            closeCamera()
            stopBackgroundThread()
        }
    }

    private fun resumeCamera2() {
        setRepeatingCaptureRequest()
        //  sharedCamera!!.surfaceTexture.setOnFrameAvailableListener(this)
    }

    private fun resumeARCore() {
        Timber.d("trying to resume ARCore session..")
        // Ensure that session is valid before triggering ARCore resume. Handles the case where the user
        // manually uninstalls ARCore while the app is paused and then resumes.
        if(useCameraSharing) {
            if (!arcoreActive && sharedCamera != null && cameraDevice != null) {
                try {
                    // Resume ARCore.
                    session.resume()
                    arcoreActive = true
                    //updateSnackbarMessage()
                    // Set capture session callback while in AR mode.
                    sharedCamera!!.setCaptureCallback(captureSessionCallback, backgroundHandler)
                } catch (e: CameraNotAvailableException) {
                    Timber.e("Failed to resume ARCore session $e")
                }
            }
        } else {
            try {
                // Resume ARCore.
                session.resume()
                arcoreActive = true
                //updateSnackbarMessage()
            } catch (e: CameraNotAvailableException) {
                Timber.e("Failed to resume ARCore session $e")
            }
        }
    }

    private fun pauseARCore() {
        shouldUpdateSurfaceTexture.set(false)
        if (arcoreActive) {
            // Pause ARCore.
            session.pause()
            arcoreActive = false
            //updateSnackbarMessage()
        }
    }

    private fun updateSnackbarMessage() {
        snackBarProvider.showMessage(
            if (arcoreActive)
                "ARCore is active.\nSearch for plane, then tap to place a 3D model."
            else
                "ARCore is paused.\nCamera effects enabled."
        )
    }

    // Called when starting non-AR mode or switching to non-AR mode.
    // Also called when app starts in AR mode, or resumes in AR mode.
    private fun setRepeatingCaptureRequest() {
        try {
            Timber.e("setRepeatingCaptureRequest")
            //   setCameraEffects(previewCaptureRequestBuilder)
            captureSession!!.setRepeatingRequest(
                previewCaptureRequestBuilder!!.build(), captureSessionCallback, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Timber.e("Failed to set repeating request $e")
        }
    }

    private fun createCameraPreviewSession() {
        try {
            Timber.d("create camera preview session..")
            startBackgroundThread()
            // Note that isGlAttached will be set to true in AR mode in onDrawFrame().
            //  sharedSession!!.setCameraTextureName(backgroundRenderer.textureId)
            // sharedCamera!!.surfaceTexture.setOnFrameAvailableListener(this)

            // Create an ARCore compatible capture request using `TEMPLATE_RECORD`.
            previewCaptureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)

            // Build surfaces list, starting with ARCore provided surfaces.
            val surfaceList = sharedCamera!!.arCoreSurfaces

            // Add a CPU image reader surface. On devices that don't support CPU image access, the image
            // may arrive significantly later, or not arrive at all.
            surfaceList.add(cpuImageReader!!.surface)

            // Surface list should now contain three surfaces:
            // 0. sharedCamera.getSurfaceTexture()
            // 1. …
            // 2. cpuImageReader.getSurface()

            // Add ARCore surfaces and CPU image surface targets.
            for (surface in surfaceList) {
                previewCaptureRequestBuilder!!.addTarget(surface)
            }

            // Wrap our callback in a shared camera callback.
            val wrappedCallback = sharedCamera!!.createARSessionStateCallback(
                cameraCaptureCallback,
                backgroundHandler
            )

            // Create camera capture session for camera preview using ARCore wrapped callback.
            cameraDevice!!.createCaptureSession(surfaceList, wrappedCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Timber.e("CameraAccessException $e")
        }

    }

    // Start background handler thread, used to run callbacks without blocking UI thread.
    private fun startBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThread = HandlerThread("sharedCameraBackground")
            backgroundThread!!.start()
            backgroundHandler = Handler(backgroundThread!!.looper)
        }
    }

    // Stop background handler thread.
    private fun stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread!!.quitSafely()
            try {
                backgroundThread!!.join()
                backgroundThread = null
                backgroundHandler = null
            } catch (e: InterruptedException) {
                Timber.e("Interrupted while trying to join background handler thread $e")
            }
        }
    }

    // Perform various checks, then open camera device and create CPU image reader.
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        Timber.d("trying to open camera..")
        cameraDevice?.let {
            Timber.d("camera device not null, it's already opened!")
        } ?: {
            // Store the ARCore shared camera reference.
            sharedCamera = session.sharedCamera

            // Store the ID of the camera used by ARCore.
            mCameraId = session.cameraConfig.cameraId
            val desiredCpuImageSize = session.cameraConfig.imageSize
            //we init camera id here, it will be used by recorder a bit latter
            framerRecorderSettings.cameraId = mCameraId!!
            // Use the currently configured CPU image size.
            framerRecorderSettings.previewSize =
                Size(desiredCpuImageSize.width, desiredCpuImageSize.height)
            framerRecorderSettings.outputFrameSize =
                Size(desiredCpuImageSize.width, desiredCpuImageSize.height)
            cpuImageReader = ImageReader.newInstance(
                desiredCpuImageSize.width,
                desiredCpuImageSize.height,
                ImageFormat.YUV_420_888,
                5
            )

            var imageLatest: Image? = null

            cpuImageReader!!.setOnImageAvailableListener({
                if (imageLatest == null) {
                    imageLatest = it.acquireLatestImage()
                    if (videoRecorderWrapper.isRecording()) {
                        imageLatest?.let {
                            val imgCopy = imageCopy(it)
                            videoRecorderWrapper.recordFrame(imgCopy)
                        }
                    }
                    imageLatest?.close()
                    imageLatest = null
                }

            }, backgroundHandler)


            // When ARCore is running, make sure it also updates our CPU image surface.
            sharedCamera!!.setAppSurfaces(this.mCameraId, Arrays.asList(cpuImageReader!!.surface))


            try {
                // Wrap our callback in a shared camera callback.
                val wrappedCallback =
                    sharedCamera!!.createARDeviceStateCallback(
                        cameraDeviceCallback,
                        backgroundHandler
                    )

                // Store a reference to the camera system service.
                cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

                // Get the characteristics for the ARCore camera.
                val characteristics = cameraManager!!.getCameraCharacteristics(this.mCameraId!!)

                // On Android P and later, get list of keys that are difficult to apply per-lastFrame and can
                // result in unexpected delays when modified during the capture session lifetime.
                if (Build.VERSION.SDK_INT >= 28) {
                    keysThatCanCauseCaptureDelaysWhenModified = characteristics.availableSessionKeys
                    if (keysThatCanCauseCaptureDelaysWhenModified == null) {
                        // Initialize the list to an empty list if getAvailableSessionKeys() returns null.
                        keysThatCanCauseCaptureDelaysWhenModified = ArrayList();
                    }
                }

                // Prevent app crashes due to quick operations on camera open / close by waiting for the
                // capture session's onActive() callback to be triggered.
                captureSessionChangesPossible = false

                // Open the camera device using the ARCore wrapped callback.
                cameraManager!!.openCamera(mCameraId!!, wrappedCallback, backgroundHandler)
            } catch (e: CameraAccessException) {
                Timber.e("Failed to open camera $e")
            } catch (e: IllegalArgumentException) {
                Timber.e("Failed to open camera $e")
            } catch (e: SecurityException) {
                Timber.e("Failed to open camera $e")
            }
        }()


    }

    private fun <T> checkIfKeyCanCauseDelay(key: CaptureRequest.Key<T>): Boolean {
        if (Build.VERSION.SDK_INT >= 28) {
            // On Android P and later, return true if key is difficult to apply per-lastFrame.
            return keysThatCanCauseCaptureDelaysWhenModified!!.contains(key)
        } else {
            // On earlier Android versions, log a warning since there is no API to determine whether
            // the key is difficult to apply per-lastFrame. Certain keys such as CONTROL_AE_TARGET_FPS_RANGE
            // are known to cause a noticeable delay on certain devices.
            // If avoiding unexpected capture delays when switching between non-AR and AR modes is
            // important, verify the runtime behavior on each pre-Android P device on which the app will
            // be distributed. Note that this device-specific runtime behavior may change when the
            // device's operating system is updated.
            Timber.w(

                "Changing "
                        + key
                        + " may cause a noticeable capture delay. Please verify actual runtime behavior on"
                        + " specific pre-Android P devices that this app will be distributed on."
            )
            // Allow the change since we're unable to determine whether it can cause unexpected delays.
            return false
        }
    }

    // If possible, apply effect in non-AR mode, to help visually distinguish between from AR mode.
    /*private fun setCameraEffects(captureBuilder: CaptureRequest.Builder?) {
        if (checkIfKeyCanCauseDelay(CaptureRequest.CONTROL_EFFECT_MODE)) {
            Timber.w("Not setting CONTROL_EFFECT_MODE since it can cause delays between transitions.")
        } else {
            Timber.d("Setting CONTROL_EFFECT_MODE to SEPIA in non-AR mode.")
            captureBuilder!!.set(
                CaptureRequest.CONTROL_EFFECT_MODE, CaptureRequest.CONTROL_EFFECT_MODE_SEPIA
            )
        }
    }*/

    // Close the camera device.
    private fun closeCamera() {
        Timber.d("closeCamera")
        if (captureSession != null) {
            captureSession!!.close()
            captureSession = null
        }
        if (cameraDevice != null) {
            waitUntilCameraCaptureSesssionIsActive()
            safeToExitApp.close()
            cameraDevice!!.close()
            cameraDevice = null
            safeToExitApp.block()
        }
        if (cpuImageReader != null) {
            cpuImageReader!!.close()
            cpuImageReader = null
        }
    }
    //}

}