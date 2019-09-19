package us.cyberstar.presentation.feature.scenes.authScene.view

import com.arellomobile.mvp.MvpView


interface AuthView : MvpView {
    fun showCodeConfirmView()
    fun onTokenReceived(token: String?)
}