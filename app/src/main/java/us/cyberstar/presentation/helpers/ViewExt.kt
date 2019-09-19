package us.cyberstar.presentation.helpers

import android.view.View


fun View.changeVisibility(boolean: Boolean) {
    this.visibility = if (boolean) View.VISIBLE else View.GONE
}