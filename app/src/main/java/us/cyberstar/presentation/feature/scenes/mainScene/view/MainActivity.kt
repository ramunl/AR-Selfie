package us.cyberstar.presentation.feature.scenes.mainScene.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import us.cyberstar.arcyber.BuildConfig
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl
import us.cyberstar.presentation.feature.scenes.mainScene.presenter.MainViewPresenter
import us.cyberstar.presentation.helpers.FullScreenHelper
import us.cyberstar.presentation.helpers.addFragment
import us.cyberstar.presentation.helpers.replaceFragment
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseToolbarActivity(), MainView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun addFragment(fragment: Fragment, toAdd: Boolean) {
        addFragment(R.id.container, fragment, toAdd)
    }

    @Inject
    lateinit var providerPresenter: Provider<MainViewPresenter>

    @InjectPresenter
    lateinit var presenter: MainViewPresenter

    @ProvidePresenter
    fun providePresenter(): MainViewPresenter = providerPresenter.get()

    override fun layoutRes(): Int {
        return when (BuildConfig.FLAVOR) {
            "arcreator" -> {
                R.layout.activity_ar_creator
            }
            else -> {
                R.layout.activity_ar
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return when (BuildConfig.FLAVOR) {
            "arcreator" -> {
                menuInflater.inflate(R.menu.menu_main, menu)
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun viewCreated(isRestoring: Boolean) {
        if (!isRestoring) {
            presenter.addArSurfaceView()
            when (BuildConfig.FLAVOR) {
                "arcreator" -> {
                    presenter.addCloudArFragmentImpl()
                }
                "serviceApp" -> {
                    presenter.showCameraFragment()
                    presenter.addSettingsView()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        //photoPresenter.onPermissionsGrantedResult(requestCode)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }


    private var arCoreFragment: ArFragmentImpl? = null


    override fun addSettingsView(fragment: Fragment) {
        replaceFragment(R.id.container_settings, fragment)
    }


    override fun addArSurfaceView(arCoreFragment: ArFragmentImpl) {
        this.arCoreFragment = arCoreFragment
        replaceFragment(R.id.surfaceContainer, arCoreFragment)
    }

//    var cameraFragment: Camera2VideoFragment? = null
    /*override fun addCameraFragment(cameraFragment: Camera2VideoFragment) {
        this.cameraFragment = cameraFragment
        replaceFragment(R.id.container_camera, cameraFragment)
    }*/

    /*override fun addSettingsFragment(settingsDevFragment: SettingsDevFragment) {
        replaceFragment(R.id.container_settings, settingsDevFragment)
    }*/

    override fun putFragment(fragment: Fragment) {
        replaceFragment(R.id.container, fragment)
    }


    override fun onPause() {
        super.onPause()
        //photoPresenter.toggleArSession(false)
    }

    override fun onResume() {
        super.onResume()
        // photoPresenter.toggleArSession(true)
    }
}
