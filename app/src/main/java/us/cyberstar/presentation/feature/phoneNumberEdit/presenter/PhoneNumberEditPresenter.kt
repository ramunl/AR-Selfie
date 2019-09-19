package us.cyberstar.presentation.feature.phoneNumberEdit.presenter


import android.content.SharedPreferences
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ext.setUserPhone
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.data.model.BackendResponseModel
import us.cyberstar.data.model.CODE_CONFIRM
import us.cyberstar.domain.external.auth.AuthTokenRequestWrapper
import us.cyberstar.data.model.TOKEN_REQ_BY_SMS
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.phoneNumberEdit.view.PhoneNumberEditView
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class PhoneNumberEditPresenter @Inject constructor(
    private val snackBarProvider: SnackBarProvider,
    private val backendSocketListener: BackendSocketListener,
    private val schedulersProvider: SchedulersProvider,
    private val preferences: SharedPreferences,
    private val authTokenRequestWrapper: AuthTokenRequestWrapper
) : BackendResponseListener, BasePresenter<PhoneNumberEditView>() {

    override fun onOpen() {
        schedulersProvider.ui().scheduleDirect {
            Timber.d("Backend socket connected")
            viewState.showLoading(true)
        }
    }

    override fun onClosed(code: Int, reason: String) {
        schedulersProvider.ui().scheduleDirect {
            viewState.showLoading(false)
            if (code < 0) {
                snackBarProvider.showError("Network error: $code $reason", false)
            }
        }
    }

    fun cleanSocketBackendListener() {
        Timber.d("set backend response listener")
        backendSocketListener.removeBackendResponseListener(this)
    }

    fun setSocketBackendListener() {
        Timber.d("set backend response listener")
        backendSocketListener.addBackendResponseListener(this)
    }

    override fun onResponseReady(respModel: BackendResponseModel) {
        if(respModel.requestCommand == TOKEN_REQ_BY_SMS) {
            schedulersProvider.ui().scheduleDirect {
                viewState.showLoading(false)
                viewState.showResultView(respModel.isSuccess, respModel.responseMsg!!)
            }
        }
    }

    fun requestSMSCode(phoneNumber: String) {
        preferences.setUserPhone(phoneNumber)
        authTokenRequestWrapper.requestTokenBySMS(TOKEN_REQ_BY_SMS, phoneNumber)
    }
}