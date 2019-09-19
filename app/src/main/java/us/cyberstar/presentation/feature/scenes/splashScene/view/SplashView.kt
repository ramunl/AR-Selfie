package us.cyberstar.presentation.feature.scenes.splashScene.view

import com.arellomobile.mvp.MvpView


interface SplashView : MvpView {
    fun showAppModeButtons()
    fun runArCoreActivity(isDevMode: Boolean, hasToken: Boolean)
}