package us.cyberstar.presentation.feature.smsCodeConfirm.presenter


import android.content.SharedPreferences
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.ext.userPhone
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.data.model.BackendResponseModel
import us.cyberstar.domain.external.auth.AuthTokenRequestWrapper
import us.cyberstar.data.model.CODE_CONFIRM
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.smsCodeConfirm.view.SMSCodeConfirmView
import javax.inject.Inject


@InjectViewState
class SMSCodeConfirmPresenter @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val backendSocketListener: BackendSocketListener,
    private val sharedPreferences: SharedPreferences,
    private val authTokenRequestWrapper: AuthTokenRequestWrapper
) : BasePresenter<SMSCodeConfirmView>(), BackendResponseListener {

    override fun onOpen() {
        schedulersProvider.ui().scheduleDirect {
            Timber.d("Backend socket connected")
            viewState.showLoading(true)
        }
    }

    override fun onClosed(code: Int, reason: String) {
        schedulersProvider.ui().scheduleDirect {
            viewState.showLoading(false)
        }
    }

    fun confirmPhoneNumberWithCode(code: Int) {
        authTokenRequestWrapper.confirmPhoneNumberWithCode(CODE_CONFIRM, sharedPreferences.userPhone()!!, code)
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
        schedulersProvider.ui().scheduleDirect {
            viewState.showLoading(false)
            if (respModel.requestCommand == CODE_CONFIRM) {
                if (!respModel.isSuccess) {
                    viewState.onCodeConfirmFailed()
                }
            }
        }
    }

}