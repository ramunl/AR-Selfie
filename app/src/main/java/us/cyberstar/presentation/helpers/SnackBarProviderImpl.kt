/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.cyberstar.presentation.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_ar.*
import timber.log.Timber
import us.cyberstar.arcyber.R
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import javax.inject.Inject


/**
 * Helper to manage the sample snackbar. Hides the Android boilerplate code, and exposes simpler
 * methods.
 * TODO: we need to implement a message queue here to show all messages one by one
 */

class SnackBarProviderImpl @Inject constructor(
    private val activity: Activity,
    private val schedulersProvider: SchedulersProvider
) :
    SnackBarProvider {

    override fun showShortMessage(message: String) {
        showMessage(message, R.color.colorPrimary, DismissBehavior.SHOW, Snackbar.LENGTH_SHORT)
    }

    private enum class DismissBehavior {
        HIDE, SHOW, FINISH, SETTINGS
    }

    private var messageSnackbar: Snackbar? = null
    private var maxLines = 6

    override fun showMessageWithSettingsButton(message: String) {
        showMessage(message, R.color.colorPrimary, DismissBehavior.SETTINGS, Snackbar.LENGTH_LONG)
    }

    override fun showMessage(message: String) {
        showMessage(message, R.color.colorPrimary, DismissBehavior.SHOW, Snackbar.LENGTH_LONG)
    }

    override fun showError(message: String, finishActivity: Boolean) {
        showMessage(
            message,
            R.color.colorAccent,
            if (finishActivity) DismissBehavior.FINISH else DismissBehavior.HIDE,
            Snackbar.LENGTH_LONG
        )
    }


    private fun showMessage(
        message: String,
        backgroundColor: Int,
        behavior: DismissBehavior,
        dur: Int
    ) {
        schedulersProvider.ui().scheduleDirect {
            //it's not the best solution, but in case we have previous bar not closed,
            //we need to handle this case somehow
            if (messageSnackbar != null) {
                messageSnackbar!!.setText(message)
            } else {
                Timber.e(message)
                messageSnackbar =
                    Snackbar.make(
                        activity.rootView,
                        message,
                        if (behavior == DismissBehavior.FINISH || behavior == DismissBehavior.HIDE || behavior == DismissBehavior.SETTINGS) {
                            Snackbar.LENGTH_INDEFINITE
                        } else {
                            dur
                        }
                    ).apply {
                        addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                messageSnackbar = null
                            }
                        });
                        when (behavior) {
                            DismissBehavior.SETTINGS -> {
                                setAction("Settings") {
                                    val context = it.context
                                    val myAppSettings = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + context.packageName)
                                    )
                                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                                    myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(myAppSettings)
                                }
                            }
                            DismissBehavior.FINISH -> {
                                setAction("Finish") { activity.finish() }
                            }
                            DismissBehavior.HIDE -> {
                                setAction("Dismiss") {
                                    dismiss()
                                }
                            }
                            DismissBehavior.SHOW -> {
                            }
                        }
                        view.setBackgroundColor(backgroundColor)
                        (view.findViewById<View>(R.id.snackbar_text) as TextView).maxLines =
                            maxLines
                        show()
                    }
            }
        }
    }


    /**
     * Hides the currently showing snackbar, if there is one. Safe to call from any thread. Safe to
     * call even if snackbar is not shown.
     */
    override fun hide() {
        messageSnackbar?.let {
            activity.runOnUiThread { it.dismiss() }
        }
    }

}
