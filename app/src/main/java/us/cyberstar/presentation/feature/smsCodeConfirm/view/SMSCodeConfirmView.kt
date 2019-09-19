package us.cyberstar.presentation.feature.smsCodeConfirm.view

import com.arellomobile.mvp.MvpView
import us.cyberstar.presentation.base.CanShowLoading

interface SMSCodeConfirmView : MvpView, CanShowLoading {
    fun onCodeConfirmFailed()
}