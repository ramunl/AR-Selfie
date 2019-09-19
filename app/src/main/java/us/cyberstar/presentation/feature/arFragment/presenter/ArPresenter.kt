package us.cyberstar.presentation.feature.arFragment.presenter

import android.view.View
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.usecase.CreateTapArPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.arFragment.view.ArView
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.helpers.GpsDialogProvider
import javax.inject.Inject

@InjectViewState
class ArPresenter @Inject constructor(
    private val mainActivity: MainActivity,
    private val createTapArPostUseCase: CreateTapArPostUseCase,
    private val arCoreFrameEmitter: ArCoreFrameEmitter,
    val arCoreSession: ArCoreSession,
    val snackBarProvider: SnackBarProvider,
    private val gpsDialogProvider: GpsDialogProvider,
    private val arSceneInitializer: ArSceneInitializer
) : BasePresenter<ArView>() {

    override fun onDestroy() {
        Timber.d("onDestroy")
        arSceneInitializer.onDestroy()
    }


    fun onCreate() {
        Timber.d("onCreate")
        if (gpsDialogProvider.isGPSEnabled()) {
            arSceneInitializer.initScene()
        } else {
            gpsDialogProvider.showDialogGPS(mainActivity)
        }
    }

    fun addTapListenerToView(view: View) {
        view.setOnTouchListener(createTapArPostUseCase.tapHelper)
    }

    fun onResume() {
        Timber.d("onStart")
        arCoreSession.onResume()
        arCoreFrameEmitter.addFrameListener()
    }

    fun onPause() {
        Timber.d("onStop")
        arCoreSession.onPause()
        arCoreFrameEmitter.removeFrameListener()
    }

}