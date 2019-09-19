package us.cyberstar.presentation.feature.cameraView.view

import com.arellomobile.mvp.MvpView
import us.cyberstar.domain.external.model.ArPostModel

interface CameraView : MvpView {
    fun togglePhotoVideoMode(isVideo: Boolean)
}