package us.cyberstar.presentation.feature.arSelfie.view

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_ar_creator.*
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.arSelfie.presenter.ArSelfiePresenter
import us.cyberstar.presentation.helpers.changeVisibility
import javax.inject.Inject
import javax.inject.Provider

class ArSelfieFragment : BaseFragment(), ArSelfieView {

    override fun updateStatusText(statusText: String) {
        statusTextView.text = statusText
    }

    override fun setLoadingView(isVisible: Boolean) {
        progressView.changeVisibility(isVisible)
    }

    override fun switchLockState(isLocked: Boolean) {
        lockModelSwitch.isChecked = isLocked
    }

    override fun setSendControlVisible(isVisible: Boolean) {
        hostButton.changeVisibility(isVisible)
    }

    override fun setDeleteControlVisible(isVisible: Boolean) {
        deleteButton.changeVisibility(isVisible)
    }

    override fun setLockControlVisible(isVisible: Boolean) {
        lockModelSwitch.changeVisibility(isVisible)
    }

    override fun setDropControlVisible(isVisible: Boolean) {
        makePhotoBtn.changeVisibility(isVisible)
    }

    override fun setPhotoButtonVisible(isVisible: Boolean) {
        makeArPhotoBtn.changeVisibility(isVisible)
    }

    override fun layoutRes(): Int = R.layout.fragment_ar_creator

    @Inject
    lateinit var providerSelfiePresenter: Provider<ArSelfiePresenter>

    @InjectPresenter
    lateinit var selfiePresenter: ArSelfiePresenter

    @ProvidePresenter
    fun providePresenter(): ArSelfiePresenter = providerSelfiePresenter.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeArPhotoBtn.setOnClickListener {
            selfiePresenter.makeArPhoto()
        }
        deleteButton.setOnClickListener {
            selfiePresenter.deleteModel()
        }
        hostButton.setOnClickListener {
            selfiePresenter.confirmPostCreate()
        }

        lockModelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            selfiePresenter.setLockMode(isChecked)
        }

        makePhotoBtn.setOnClickListener {
            selfiePresenter.dropModel()
        }
    }

    override fun onStop() {
        super.onStop()
        selfiePresenter.onStop()
    }

    override fun onStart() {
        super.onStart()
        selfiePresenter.onStart()
    }

}