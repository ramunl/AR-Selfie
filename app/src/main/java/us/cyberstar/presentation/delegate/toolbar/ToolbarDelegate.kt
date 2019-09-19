package us.cyberstar.presentation.delegate.toolbar

import android.app.Activity
import android.content.Context
import javax.inject.Inject

class ToolbarDelegate @Inject constructor(activity: Activity) {

    private var manager: ToolbarManager
    private var context: Context = activity.applicationContext

    init {
        if (activity is ToolbarManager) this.manager = activity
        else throw ClassCastException("Your activity has to implement ToolbarTitleManager interface") as Throwable
    }

    fun changeTitle(title: String) {
        manager.changeTitle(title)
    }

    fun hideToolbar() {
        manager.hideToolbar()
    }

    fun showToolbar() {
        manager.showToolbar()
    }

    fun showBackButton() {
        manager.showBackButton()
    }

    fun hideBackButton() {
        manager.hideBackButton()
    }
}