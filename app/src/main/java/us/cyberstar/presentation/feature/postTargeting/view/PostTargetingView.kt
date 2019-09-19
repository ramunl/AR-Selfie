package us.cyberstar.presentation.feature.postTargeting.view

import com.arellomobile.mvp.MvpView

interface PostTargetingView : MvpView {
    fun updatePostConfirmButtonState(enabled: Boolean)
    fun showProgressView(isVisible: Boolean)
    fun showErrorView(errMessage: String)
    fun hideErrorView()
}