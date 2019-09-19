package us.cyberstar.presentation.feature.phoneNumberEdit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_phone_edit.*
import timber.log.Timber
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.phoneNumberEdit.presenter.PhoneNumberEditPresenter

import javax.inject.Inject
import javax.inject.Provider


class PhoneNumberEditFragment : PhoneNumberEditView, BaseFragment() {

    override fun showLoading(isLoading: Boolean) {
        Timber.d("showLoading $isLoading")
        if (isLoading) {
            authLoadingView.visibility = View.VISIBLE
        } else {
            authLoadingView.visibility = View.GONE
        }
    }

    override fun hideResultView() {
        authResultView.visibility = View.GONE
    }

    override fun showResultView(success: Boolean, message: String) {
        Timber.d("showResultView $success")
        authResultView.visibility = View.VISIBLE
        if (success) {
            authTryAgain.visibility = View.GONE
            authResultErrorIcon.visibility = View.GONE
            authResultSuccessIcon.visibility = View.VISIBLE
        } else {
            authTryAgain.visibility = View.VISIBLE
            authResultSuccessIcon.visibility = View.GONE
            authResultErrorIcon.visibility = View.VISIBLE
            authTryAgain.setOnClickListener {
                authResultView.visibility = View.GONE
            }
        }
        authResultMessage.text = message
    }


    @Inject
    lateinit var providerPresenter: Provider<PhoneNumberEditPresenter>

    @InjectPresenter
    lateinit var presenter: PhoneNumberEditPresenter

    @ProvidePresenter
    fun providePresenter(): PhoneNumberEditPresenter = providerPresenter.get()


    override fun layoutRes() = us.cyberstar.arcyber.R.layout.fragment_phone_edit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendSMSCode.setOnClickListener {
            hideKeyboard(activity, phoneEditText)
            presenter.requestSMSCode(phoneEditText.text.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        return rootView
    }
    override fun onStop() {
        super.onStop()
        presenter.cleanSocketBackendListener()
    }

    override fun onStart() {
        super.onStart()
        presenter.setSocketBackendListener()
    }
}