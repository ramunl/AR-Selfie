package us.cyberstar.presentation.feature.scenes.splashScene.presenter

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.InjectViewState
import us.cyberstar.data.ext.appMode
import us.cyberstar.data.ext.appToken
import us.cyberstar.data.ext.setAppMode
import us.cyberstar.domain.external.arcore.ArCoreSupportVerifier
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.scenes.splashScene.view.SplashView
import javax.inject.Inject


/*
This the launch screen photoPresenter. Here we check:
 1)Does our device support arCore?
 2)Do we want to run either DEV or PROD mode?
 */
@InjectViewState
class SplashViewPresenter @Inject constructor(
    private val prefs: SharedPreferences,
    private val arCoreSupportVerifier: ArCoreSupportVerifier
) : BasePresenter<SplashView>() {

    fun checkForArCoreSupport(activity: AppCompatActivity) {
        arCoreSupportVerifier.isDeviceSupported(activity, object : ArCoreSupportVerifier.ArCoreSupportCheckListener {
            override fun isSupported() {
                tryToRunArCoreActivity()
            }
        })
    }

    fun tryToRunArCoreActivity() {
        val appMode = "prod"//prefs.appMode()
        /*if(appMode.isNullOrBlank()) {
            viewState.showAppModeButtons()
        } else {*/
            val appToken = prefs.appToken()
            viewState.runArCoreActivity(appMode == "dev", !appToken.isNullOrBlank())
        //}
    }


    fun setDevMode(devModeEnabled: Boolean) {
        prefs.setAppMode(if (devModeEnabled) "dev" else "prod")
        viewState.runArCoreActivity(devModeEnabled, !prefs.appToken().isNullOrBlank())
    }

}