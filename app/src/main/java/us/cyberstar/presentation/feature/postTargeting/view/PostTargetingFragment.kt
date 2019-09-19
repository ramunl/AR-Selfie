package us.cyberstar.presentation.feature.postTargeting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_post_targeting_or_quick.*
import kotlinx.android.synthetic.main.fragment_post_targeting_or_quick.view.*
import kotlinx.android.synthetic.main.fragment_post_targeting_or_quick.view.confirmArPostCreateBtn
import timber.log.Timber
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postTargeting.presenter.PostTargetingPresenter
import us.cyberstar.presentation.helpers.changeVisibility
import javax.inject.Inject
import javax.inject.Provider


class PostTargetingFragment : PostTargetingView, BaseFragment() {
    override fun hideErrorView() {
        tryAgainView.changeVisibility(false)
    }

    override fun showErrorView(errMessage: String) {
        tryAgainView.changeVisibility(true)
        errorMessage.text = errMessage
    }

    override fun updatePostConfirmButtonState(enabled: Boolean) {
        confirmArPostCreateBtn.isEnabled = enabled
    }

    override fun showProgressView(isVisible: Boolean) {
        progressView.changeVisibility(isVisible)
        confirmArPostCreateBtn.changeVisibility(!isVisible)
    }

    @Inject
    lateinit var providerPresenter: Provider<PostTargetingPresenter>

    @InjectPresenter
    lateinit var presenter: PostTargetingPresenter

    @ProvidePresenter
    fun providePresenter(): PostTargetingPresenter = providerPresenter.get()

    override fun layoutRes() = R.layout.fragment_post_targeting_or_quick

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("PostTargetingFragment::onCreateView")
        val rootView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        presenter.setBundle(arguments!!)

        rootView.confirmArPostCreateBtn.isEnabled = false

        rootView.confirmArPostCreateBtn.setOnClickListener {
            presenter.confirmTargetingPostCreation()
        }

        rootView.tryAgainView.setOnClickListener {
            presenter.tryAgain()
        }

        rootView.cancelPostingInAr.setOnClickListener {
            presenter.stopUseCase(true)
            presenter.showCameraFragment()
        }

        rootView.closePostingScreen.setOnClickListener {
            presenter.stopUseCase(true)
            presenter.showCameraFragment()
        }
        return rootView
    }
}