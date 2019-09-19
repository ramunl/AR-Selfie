package us.cyberstar.presentation.feature.scenes.devScene.view

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.scenes.devScene.presenter.DevViewPresenter
import us.cyberstar.presentation.helpers.FullScreenHelper
import javax.inject.Inject
import javax.inject.Provider

class DevActivity : BaseToolbarActivity(), DevView {


    override fun layoutRes() = R.layout.activity_dev

    override fun viewCreated(isRestoring: Boolean) {

    }

    @Inject
    lateinit var providerPresenter: Provider<DevViewPresenter>

    @InjectPresenter
    lateinit var presenter: DevViewPresenter

    @ProvidePresenter
    fun providePresenter(): DevViewPresenter = providerPresenter.get()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        //photoPresenter.onPermissionsGrantedResult(requestCode)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

}
