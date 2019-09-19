package us.cyberstar.presentation.feature.cameraScreen.view

import android.content.Intent
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_camera.*
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.cameraScreen.presenter.CameraViewPresenter
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import javax.inject.Inject
import javax.inject.Provider


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CameraActivity : BaseToolbarActivity(), CameraView {

    @Inject
    lateinit var providerPresenter: Provider<CameraViewPresenter>

    @InjectPresenter
    lateinit var presenter: CameraViewPresenter

    @ProvidePresenter
    fun providePresenter(): CameraViewPresenter = providerPresenter.get()


    override fun layoutRes() = R.layout.activity_camera

    override fun viewCreated(isRestoring: Boolean) {
        switchCam.setOnClickListener {
            cameraKitView.toggleFacing()
        }
        photoButton.setOnClickListener {
            cameraKitView.captureImage { camView, bytes -> runArActivity(bytes, camView.width, camView.height) }
        }
    }

    private fun runArActivity(jpeg: ByteArray, width: Int, height: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("width", width)
            putExtra("height", height)
            putExtra("jpeg", jpeg)
        }
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        cameraKitView.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView.onResume()
    }

    override fun onPause() {
        cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
