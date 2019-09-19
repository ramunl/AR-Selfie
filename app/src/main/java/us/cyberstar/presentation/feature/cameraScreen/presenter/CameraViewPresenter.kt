package us.cyberstar.presentation.feature.cameraScreen.presenter

import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.cameraScreen.view.CameraView
import us.cyberstar.presentation.helpers.PermissionHelper
import javax.inject.Inject


@InjectViewState
class CameraViewPresenter @Inject constructor(
) : BasePresenter<CameraView>() {

}