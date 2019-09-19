package us.cyberstar.presentation.helpers

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun hideKeyboard(context: AppCompatActivity) {
    try {
        val view = context.currentFocus
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}