package us.cyberstar.presentation.feature.phoneNumberEdit.view

import com.arellomobile.mvp.MvpView
import us.cyberstar.presentation.base.CanShowLoading

interface PhoneNumberEditView : MvpView, CanShowLoading {
    fun showResultView(success: Boolean, message: String)
    fun hideResultView()
}