package us.cyberstar.presentation.feature.cloudAnchor.view

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_ar_creator.*
import kotlinx.android.synthetic.main.fragment_cam.*
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.cloudAnchor.presenter.CloudArPresenter
import us.cyberstar.presentation.helpers.changeVisibility
import javax.inject.Inject
import javax.inject.Provider

class CloudArFragment : BaseFragment(), CloudArView {

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
        dropButton.changeVisibility(isVisible)
    }

    override fun setPhotoButtonVisible(isVisible: Boolean) {
        createButton.changeVisibility(isVisible)
    }

    override fun layoutRes(): Int = R.layout.fragment_ar_creator

    @Inject
    lateinit var providerPresenter: Provider<CloudArPresenter>

    @InjectPresenter
    lateinit var presenter: CloudArPresenter

    @ProvidePresenter
    fun providePresenter(): CloudArPresenter = providerPresenter.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createButton.setOnClickListener {
            presenter.createModel()
        }
        deleteButton.setOnClickListener {
            presenter.deleteModel()
        }
        hostButton.setOnClickListener {
            presenter.confirmPostCreate()
        }

        lockModelSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.setLockMode(isChecked)
        }

        dropButton.setOnClickListener {
            presenter.dropModel()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

}