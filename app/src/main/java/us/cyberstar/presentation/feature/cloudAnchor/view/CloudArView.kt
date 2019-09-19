package us.cyberstar.presentation.feature.cloudAnchor.view

import com.arellomobile.mvp.MvpView

interface CloudArView: MvpView {
    fun setLockControlVisible(isVisible: Boolean)
    fun setDropControlVisible(isVisible: Boolean)
    fun setPhotoButtonVisible(isVisible: Boolean)
    fun switchLockState(isLocked: Boolean)
    fun setSendControlVisible(isVisible: Boolean)
    fun setDeleteControlVisible(isVisible: Boolean)
    fun setLoadingView(isVisible: Boolean)
    fun updateStatusText(statusText: String)

}