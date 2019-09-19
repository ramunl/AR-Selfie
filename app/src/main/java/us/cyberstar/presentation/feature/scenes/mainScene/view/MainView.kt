package us.cyberstar.presentation.feature.scenes.mainScene.view

import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpView
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl


interface MainView : MvpView {
    fun addArSurfaceView(arCoreFragment: ArFragmentImpl)
    // fun showCameraFragment()
    // fun showPostEditFragment()
    fun putFragment(fragment: Fragment)

    fun addFragment(fragment: Fragment, toAdd: Boolean = false)
    fun addSettingsView(fragment: Fragment)
    //fun addSettingsFragment(settingsDevFragment: SettingsDevFragment)
    // fun addCameraFragment(cameraFragment: Camera2VideoFragment)
}