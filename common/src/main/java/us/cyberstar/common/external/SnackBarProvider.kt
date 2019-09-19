package us.cyberstar.common.external

interface SnackBarProvider {
    fun showShortMessage(message: String)
    fun showMessage(message: String)
    fun showError(message: String, finishActivity: Boolean)
    fun hide()
    fun showMessageWithSettingsButton(message: String)
}