package us.cyberstar.presentation.feature.glFragment.presenter

import android.view.View
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.usecase.CreateTapArPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.glFragment.view.GlView
import us.cyberstar.presentation.helpers.GpsDialogProvider
import javax.inject.Inject


@InjectViewState
class GlPresenter @Inject constructor(
    val nodeManager: NodeManager,
    private val arSceneInitializer: ArSceneInitializer,
    private val gpsDialogProvider: GpsDialogProvider,
    val createTapArPostUseCase: CreateTapArPostUseCase,
    val arCoreFrameEmitter: ArCoreFrameEmitter,
    val arCoreSession: ArCoreSession,
    val planesEmitter: PlanesEmitter,
    val snackBarProvider: SnackBarProvider
) : BasePresenter<GlView>() {

    fun addTapListenerToView(view: View) {
        view.setOnTouchListener(createTapArPostUseCase.tapHelper)
    }

    fun onResume() {
        arCoreSession.onResume()
        arCoreFrameEmitter.addFrameListener()
    }

    fun onPause() {
        arCoreSession.onPause()
        arCoreFrameEmitter.removeFrameListener()
    }

    fun onCreate() {
        Timber.d("onCreate")
        if (gpsDialogProvider.isGPSEnabled()) {
            arSceneInitializer.initScene()
        } else {
          //  gpsDialogProvider.showDialogGPS()
        }
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        arSceneInitializer.onDestroy()
    }
}
