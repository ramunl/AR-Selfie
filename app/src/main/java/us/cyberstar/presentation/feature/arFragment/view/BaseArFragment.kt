/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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
 */
package us.cyberstar.presentation.feature.arFragment.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.*
import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.cyber.ux.TransformationSystem
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Scene
import timber.log.Timber
import us.cyberstar.presentation.base.BaseFragment

/**
 * The AR fragment brings in the required view layout and controllers for common AR features.
 */
abstract class BaseArFragment : BaseFragment(), Scene.OnPeekTouchListener, Scene.OnUpdateListener {
    private val installRequested: Boolean = false
    private var sessionInitializationFailed = false
    /**
     * Gets the ArSceneView for this fragment.
     */
    abstract fun arSceneView(): ArSceneView
    abstract fun transformSystem(): TransformationSystem


    private var gestureDetector: GestureDetector? = null
    private var isStarted: Boolean = false
    private var canRequestDangerousPermissions = true
    private var onTapArPlaneListener: OnTapArPlaneListener? = null

    private val onFocusListener = OnWindowFocusChangeListener { hasFocus ->
        val activity = activity
        if (hasFocus && activity != null) {
            // Standard Android full-screen functionality.
            activity
                .window
                .decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }


    //  val additionalPermissions = Array<String>()

    /**
     * Invoked when an ARCore plane is tapped.
     */
    interface OnTapArPlaneListener {
        /**
         * Called when an ARCore plane is tapped. The callback will only be invoked if no [ ] was tapped.
         *
         * @param hitResult   The ARCore hit result that occurred when tapping the plane
         * @param plane       The ARCore Plane that was tapped
         * @param motionEvent the motion event that triggered the tap
         * @see .setOnTapArPlaneListener
         */
        fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent)
    }

    /**
     * Registers a callback to be invoked when an ARCore Plane is tapped. The callback will only be
     * invoked if no [com.google.ar.sceneform.Node] was tapped.
     *
     * @param onTapArPlaneListener the [OnTapArPlaneListener] to attach
     */
    fun setOnTapArPlaneListener(onTapArPlaneListener: OnTapArPlaneListener?) {
        this.onTapArPlaneListener = onTapArPlaneListener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated.. ")
        (view as FrameLayout).addView(arSceneView())
        Timber.d("arSceneView addView called ")

        gestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    onSingleTap(e)
                    return true
                }

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }
            })

        arSceneView().scene.addOnPeekTouchListener(this)
        arSceneView().scene.addOnUpdateListener(this)
        // Make the app immersive and don't turn off the display.
        arSceneView().viewTreeObserver.addOnWindowFocusChangeListener(onFocusListener)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        arSceneView().viewTreeObserver.removeOnWindowFocusChangeListener(onFocusListener)
    }

    /**
     * Starts the process of requesting dangerous permissions. This combines the CAMERA permission
     * required of ARCore and any permissions returned from getAdditionalPermissions(). There is no
     * specific processing on the result of the request, subclasses can override
     * onRequestPermissionsResult() if additional processing is needed.
     *
     *
     * [.setCanRequestDangerousPermissions] can stop this function from doing
     * anything.
     */
    protected fun requestDangerousPermissions() {
        if (!canRequestDangerousPermissions) {
            // If this is in progress, don't do it again.
            return
        }
        canRequestDangerousPermissions = false

        /*val permissions = ArrayList<String>()
        val additionalPermissions = additionalPermissions
        val permissionLength = additionalPermissions?.size ?: 0
        for (i in 0 until permissionLength) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    additionalPermissions[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(additionalPermissions[i])
            }
        }*/
        /*
        // Always check for camera permission
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (!permissions.isEmpty()) {
            // Request the permissions
            requestPermissions(permissions.toTypedArray(), RC_PERMISSIONS)
        }*/
    }

    /**
     * Receives the results for permission requests.
     *
     *
     * Brings up a dialog to request permissions. The dialog can send the user to the Settings app,
     * or finish the activity.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val builder: AlertDialog.Builder
        builder = AlertDialog.Builder(requireActivity(), android.R.style.Theme_Material_Dialog_Alert)

        builder
            .setTitle("Camera permission required")
            .setMessage("Add camera permission via Settings?")
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, which ->
                // If Ok was hit, bring up the Settings app.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", requireActivity().packageName, null)
                requireActivity().startActivity(intent)
                // When the user closes the Settings app, allow the app to resume.
                // Allow the app to ask for permissions again now.
                setCanRequestDangerousPermissions(true)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setOnDismissListener {
                // canRequestDangerousPermissions will be true if "OK" was selected from the dialog,
                // false otherwise.  If "OK" was selected do nothing on dismiss, the app will
                // continue and may ask for permission again if needed.
                // If anything else happened, finish the activity when this dialog is
                // dismissed.
                if ((getCanRequestDangerousPermissions() == false)) {
                    requireActivity().finish()
                }
            }
            .show()
    }

    /**
     * If true, [.requestDangerousPermissions] returns without doing anything, if false
     * permissions will be requested
     */
    protected fun getCanRequestDangerousPermissions(): Boolean? {
        return canRequestDangerousPermissions
    }

    /**
     * If true, [.requestDangerousPermissions] returns without doing anything, if false
     * permissions will be requested
     */
    protected fun setCanRequestDangerousPermissions(canRequestDangerousPermissions: Boolean?) {
        this.canRequestDangerousPermissions = canRequestDangerousPermissions!!
    }


    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ start() }, 3000)//TODO to remove delay implement ARCore is active. callback here
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onDestroy() {
        arSceneView().destroy()
        super.onDestroy()
    }

    override fun onPeekTouch(hitTestResult: HitTestResult, motionEvent: MotionEvent) {
        transformSystem().onTouch(hitTestResult, motionEvent)

        if (hitTestResult.node == null) {
            gestureDetector!!.onTouchEvent(motionEvent)
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        val frame = arSceneView().arFrame ?: return

        for (plane in frame.getUpdatedTrackables(Plane::class.java)) {
            if (plane.trackingState == TrackingState.TRACKING) {
            }
        }
    }

    private fun start() {
        if (isStarted) {
            return
        }

        if (activity != null) {
            isStarted = true
            try {
                Timber.d("arSceneView resume")
                arSceneView().resume()
            } catch (ex: CameraNotAvailableException) {
                sessionInitializationFailed = true
            }

            if (!sessionInitializationFailed) {
            }
        }
    }

    private fun stop() {
        if (!isStarted) {
            return
        }

        isStarted = false
        arSceneView().pause()
    }


    private fun onSingleTap(motionEvent: MotionEvent?) {
        val frame = arSceneView().arFrame

        transformSystem().selectNode(null)

        // Local variable for nullness static-analysis.
        val onTapArPlaneListener = this.onTapArPlaneListener

        if (frame != null && onTapArPlaneListener != null) {
            if (motionEvent != null && frame.camera.trackingState == TrackingState.TRACKING) {
                for (hit in frame.hitTest(motionEvent)) {
                    val trackable = hit.trackable
                    if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                        onTapArPlaneListener.onTapPlane(hit, trackable, motionEvent)
                        break
                    }
                }
            }
        }
    }

    protected fun handleSessionException(sessionException: UnavailableException) {

        val message: String
        if (sessionException is UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore"
        } else if (sessionException is UnavailableApkTooOldException) {
            message = "Please update ARCore"
        } else if (sessionException is UnavailableSdkTooOldException) {
            message = "Please update this app"
        } else if (sessionException is UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR"
        } else {
            message = "Failed to create AR session"
        }
        Timber.e("Error: $message $sessionException")
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = BaseArFragment::class.java.simpleName

        private val RC_PERMISSIONS = 1010
    }

}
