package us.cyberstar.presentation.feature.scenes.splashScene.view

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.scenes.authScene.view.AuthActivity
import us.cyberstar.presentation.feature.scenes.devScene.view.DevActivity
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.feature.scenes.splashScene.presenter.SplashViewPresenter
import us.cyberstar.presentation.helpers.FullScreenHelper
import javax.inject.Inject
import javax.inject.Provider
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import us.cyberstar.common.external.SnackBarProvider


class SplashActivity : BaseToolbarActivity(), SplashView {

    val tokenRequestId = 1

    @Inject
    lateinit var snackBarProvider: SnackBarProvider

    override fun runArCoreActivity(isDevMode: Boolean, hasToken: Boolean) {
        if (!hasToken) {
            runAuthActivity()
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    CAMERA,
                    ACCESS_FINE_LOCATION,
                    WRITE_EXTERNAL_STORAGE,
                    RECORD_AUDIO
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        var msg = "The app needs:\n"
                        var permMessages = "";
                        var cameraPermAccepted = true
                        var locationPermAccepted = true

                        for (perm in report.deniedPermissionResponses) {
                            when (perm.permissionName) {
                                WRITE_EXTERNAL_STORAGE -> {
                                    //TODO implement this case processing..
                                }
                                RECORD_AUDIO -> {
                                    permMessages += perm.permissionName + " to make Video AR Posts\n"
                                }
                                ACCESS_FINE_LOCATION -> {
                                    locationPermAccepted = false
                                    permMessages += perm.permissionName + "  to remember your AR Post locations\n"
                                }
                                CAMERA -> {
                                    cameraPermAccepted = false
                                    permMessages += perm.permissionName + " to create AR Posts\n"
                                }
                            }
                        }
                        if (cameraPermAccepted && locationPermAccepted) {
                            if (isDevMode) {
                                runDevActivity()
                            } else {
                                runProdActivity()
                            }
                        } else {
                            msg += permMessages
                            snackBarProvider.showMessageWithSettingsButton(msg)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check();
        }
    }

    override fun showAppModeButtons() {
        splashProgressBar.visibility = GONE
        splashButtonsContainer.visibility = VISIBLE
    }

    @Inject
    lateinit var providerPresenter: Provider<SplashViewPresenter>

    @InjectPresenter
    lateinit var presenter: SplashViewPresenter

    @ProvidePresenter
    fun providePresenter(): SplashViewPresenter = providerPresenter.get()

    override fun layoutRes(): Int {
        return R.layout.activity_splash
    }

    override fun onStart() {
        super.onStart()
        presenter.checkForArCoreSupport(this)
    }

    override fun viewCreated(isRestoring: Boolean) {
        if (!isRestoring) {

        }
        devActivityBtn.setOnClickListener {
            presenter.setDevMode(true)
        }
        prodActivityBtn.setOnClickListener {
            presenter.setDevMode(false)
        }
    }

    private fun runAuthActivity() {
        with(Intent(this, AuthActivity::class.java)) {
            startActivityForResult(this, tokenRequestId);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult requestCode = $requestCode")
        if (tokenRequestId == requestCode) {
            val token = data?.getStringExtra("token")
            if (token != null) {
                Timber.d("onActivityResult token = $token")
                presenter.tryToRunArCoreActivity()
            }
        }
    }

    private fun runProdActivity() {
        with(Intent(this, MainActivity::class.java)) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        finish()
    }

    private fun runDevActivity() {
        with(Intent(this, DevActivity::class.java)) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        finish()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

}
