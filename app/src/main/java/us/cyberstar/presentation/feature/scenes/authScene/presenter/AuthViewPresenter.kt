package us.cyberstar.presentation.feature.scenes.authScene.presenter

import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.data.model.BackendResponseModel
import us.cyberstar.data.model.CODE_CONFIRM
import us.cyberstar.data.model.TOKEN_REQ_BY_SMS
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.scenes.authScene.view.AuthView
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class AuthViewPresenter @Inject constructor(
    private val backendSocketListener: BackendSocketListener,
    private val schedulersProvider: SchedulersProvider,
    private val snackBarProvider: SnackBarProvider
) : BackendResponseListener, BasePresenter<AuthView>() {

    override fun onOpen() {

    }

    override fun onClosed(code: Int, reason: String) {
        schedulersProvider.ui().scheduleDirect {
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
        schedulersProvider.ui().scheduleDirect {
            var timeOut = 0L
            respModel.responseMsg?.let {
                timeOut = 2000L
            }
            if (respModel.isSuccess) {
                schedulersProvider.ui().scheduleDirect({
                    when (respModel.requestCommand) {
                        CODE_CONFIRM -> {
                            Timber.d("onResponseReady CODE_CONFIRM: $respModel")
                            viewState.onTokenReceived(respModel.token)
                        }
                        TOKEN_REQ_BY_SMS -> {
                            Timber.d("onResponseReady TOKEN_REQ_BY_SMS: $respModel")
                            viewState.showCodeConfirmView()
                        }
                    }
                }, timeOut, TimeUnit.MILLISECONDS)
            }
        }
    }

}