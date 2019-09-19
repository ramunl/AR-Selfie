/*
 * Copyright 2017 Google Inc. All Rights Reserved.
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

package us.cyberstar.presentation.feature.glFragment.view

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.ar.core.TrackingState
import kotlinx.android.synthetic.main.fragment_gl.*
import timber.log.Timber
import us.cyberstar.arcyber.R
import us.cyberstar.domain.gl.common.helpers.DisplayRotationHelper
import us.cyberstar.domain.gl.common.helpers.TrackingStateHelper
import us.cyberstar.domain.gl.common.rendering.BackgroundRenderer
import us.cyberstar.domain.gl.common.rendering.PlaneRenderer
import us.cyberstar.domain.gl.common.rendering.PointCloudRenderer
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.glFragment.presenter.GlPresenter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3d model of the Android robot.
 */

class GlFragment : BaseFragment(), GlView, GLSurfaceView.Renderer {

    override fun layoutRes(): Int = R.layout.fragment_gl

    @Inject
    lateinit var providerPresenter: Provider<GlPresenter>

    @InjectPresenter
    lateinit var presenter: GlPresenter

    @ProvidePresenter
    fun providePresenter(): GlPresenter = providerPresenter.get()


    private var displayRotationHelper: DisplayRotationHelper? = null
    private val backgroundRenderer = BackgroundRenderer()
    private val planeRenderer = PlaneRenderer()
    private val pointCloudRenderer = PointCloudRenderer()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.addTapListenerToView(glSurfaceView)
        // Set up renderer.
        glSurfaceView.preserveEGLContextOnPause = true
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        glSurfaceView.setWillNotDraw(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayRotationHelper = DisplayRotationHelper(context!!)
        presenter.onCreate()
    }

    override fun onResume() {
        super.onResume()

        displayRotationHelper!!.onResume()
        presenter.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
        presenter.onPause()
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call session.update() and get a SessionPausedException.
        displayRotationHelper!!.onPause()
    }


    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(/*context=*/context)
            planeRenderer.createOnGlThread(/*context=*/context, "models/trigrid.png")
            pointCloudRenderer.createOnGlThread(/*context=*/context)
        } catch (e: IOException) {
            Timber.e("Failed to read an asset file $e")
        }

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        displayRotationHelper!!.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        // Clear screen to notify driver it should not load any pixels from previous lastFrame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper!!.updateSessionIfNeeded(presenter.arCoreSession.session)

        try {
            presenter.arCoreSession.session.setCameraTextureName(backgroundRenderer.textureId)
            // Obtain the current lastFrame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.

            //TODO check do we need to revert it???
            //photoPresenter.arCoreFrameEmitter.onUpdate(null)
            val frame = presenter.arCoreFrameEmitter.lastFrame()
            frame?.let {

                // If lastFrame is ready, render camera preview image to the GL surface.
                backgroundRenderer.draw(frame)

                val camera = frame.camera
                // If not tracking, don't draw 3D objects, show tracking failure reason instead.
                if (camera.trackingState == TrackingState.PAUSED) {
                    presenter.snackBarProvider.showMessage("Tracking state PAUSED, reason:${TrackingStateHelper.getTrackingFailureReasonString(camera)}")
                    return
                }

                // Get projection matrix.
                val projmtx = FloatArray(16)
                camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

                // Get camera matrix and draw.
                val viewmtx = FloatArray(16)
                camera.getViewMatrix(viewmtx, 0)

                // Compute lighting from average intensity of the image.
                // The first three components are color scaling factors.
                // The last one is the average pixel intensity in gamma space.

                // Visualize tracked points.
                // Use try-with-resources to automatically release the point cloud.
                frame.acquirePointCloud().use { pointCloud ->
                    pointCloudRenderer.update(pointCloud)
                    pointCloudRenderer.draw(viewmtx, projmtx)
                }

                // Visualize planes.
                planeRenderer.drawPlanes(
                    presenter.planesEmitter.planesFound,
                    camera.displayOrientedPose,
                    projmtx
                )
                presenter.nodeManager.onDrawFrame(frame)
            }

        } catch (t: Throwable) {
            // Avoid crashing the application due to unhandled exceptions.
            Timber.e("Exception on the OpenGL thread $t")
        }

    }
}
