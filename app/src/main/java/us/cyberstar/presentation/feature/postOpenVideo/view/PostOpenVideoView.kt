package us.cyberstar.presentation.feature.postOpenVideo.view

import com.arellomobile.mvp.MvpView

interface PostOpenVideoView : MvpView {
    fun showVideo(mediaPath: String)
}