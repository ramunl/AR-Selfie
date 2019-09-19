package us.cyberstar.presentation.feature.postOpenPhoto.view

import com.arellomobile.mvp.MvpView

interface PostOpenPhotoView : MvpView {
    fun showPhoto(mediaPath: String)
}