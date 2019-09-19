package us.cyberstar.presentation.feature.scenes.opencv.view

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_open_cv.*
import us.cyberstar.arcyber.R
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.presentation.base.BaseToolbarActivity
import us.cyberstar.presentation.feature.scenes.opencv.presenter.OpenCVPresenter
import com.opencv.wrapper.OpenCVWrapper
import javax.inject.Inject
import javax.inject.Provider


class OpenCVActivity : BaseToolbarActivity(), OpenCVView {

    lateinit var openCVWrapper: OpenCVWrapper

    @Inject
    lateinit var snackBarProvider: SnackBarProvider

    @Inject
    lateinit var providerPresenter: Provider<OpenCVPresenter>

    @InjectPresenter
    lateinit var presenter: OpenCVPresenter

    @ProvidePresenter
    fun providePresenter(): OpenCVPresenter = providerPresenter.get()

    override fun layoutRes(): Int {
        return R.layout.activity_open_cv
    }

    override fun onResume() {
        super.onResume()
        openCVWrapper.onResume()
    }

    override fun onPause() {
        super.onPause()
        openCVWrapper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        openCVWrapper.onDestroy()
    }

    override fun viewCreated(isRestoring: Boolean) {
        if (!isRestoring) {
            openCVWrapper = OpenCVWrapper(this.applicationContext)
            openCVWrapper.onCreate(surfaceOpenCV)
        }
    }


}
