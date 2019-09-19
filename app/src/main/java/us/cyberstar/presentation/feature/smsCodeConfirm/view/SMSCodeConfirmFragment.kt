package us.cyberstar.presentation.feature.smsCodeConfirm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_sms_code_confirm.*
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.smsCodeConfirm.presenter.SMSCodeConfirmPresenter

import javax.inject.Inject
import javax.inject.Provider


class SMSCodeConfirmFragment : SMSCodeConfirmView, BaseFragment() {
    override fun showLoading(isLoading: Boolean) {
        codeCheckLoadingView.visibility = if (isLoading) VISIBLE else GONE
    }


    override fun onCodeConfirmFailed() {
        invalidCode.visibility = VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Inject
    lateinit var providerPresenter: Provider<SMSCodeConfirmPresenter>

    @InjectPresenter
    lateinit var presenter: SMSCodeConfirmPresenter

    @ProvidePresenter
    fun providePresenter(): SMSCodeConfirmPresenter = providerPresenter.get()


    override fun layoutRes() = us.cyberstar.arcyber.R.layout.fragment_sms_code_confirm

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmSMSCode.setOnClickListener {
            invalidCode.visibility = GONE
            presenter.confirmPhoneNumberWithCode(phoneEditText.text.toString().toInt())
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