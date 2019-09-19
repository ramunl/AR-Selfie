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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.core.app.ActivityCompat
import timber.log.Timber
import us.cyberstar.arcyber.R
import javax.inject.Inject

/** Helper to ask camera permission.  */
class PermissionHelper @Inject constructor(
    private val activity: Activity,
    private val repo: us.cyberstar.common.external.ResRepo
) {

    private val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val WRITE_EXTERNAL_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    /** Check to see we have the necessary permissions for this app.  */
    fun hasPermission(requestCode: PermissionRequestCode): Boolean {
        val permissionToCheck = getPermissionByCode(requestCode)
        return ContextCompat.checkSelfPermission(activity, permissionToCheck) == PackageManager.PERMISSION_GRANTED
    }

    private fun getPermissionByCode(requestCode: PermissionRequestCode): String {
        return when (requestCode) {
            PermissionRequestCode.ACCESS_FINE_LOCATION_CODE -> {
                ACCESS_FINE_LOCATION
            }
            PermissionRequestCode.CAMERA_PERMISSION_CODE -> {
                CAMERA_PERMISSION
            }
            PermissionRequestCode.WRITE_EXTERNAL_PERMISSION_CODE -> {
                WRITE_EXTERNAL_PERMISSION
            }
        }
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
    fun requestPermission(requestCode: PermissionRequestCode) {
        val permissionToRequest = getPermissionByCode(requestCode)
        ActivityCompat.requestPermissions(activity, arrayOf(permissionToRequest), requestCode.ordinal)
    }

    /** Check to see if we need to show the rationale for this permission.  */
    fun shouldShowRequestPermissionRationale(requestCode: PermissionRequestCode): Boolean {
        val permissionToRequest = getPermissionByCode(requestCode)
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionToRequest)
    }

    /** Launch Application Setting to grant permission.  */
    fun launchPermissionSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    fun onPermissionsGrantedResult(requestCode: Int) {
        Timber.d("check is camera permissions granted?")
        val permissionRequestCode = PermissionRequestCode.values()[requestCode]
        if (hasPermission(permissionRequestCode)) {
            when (permissionRequestCode) {
                PermissionRequestCode.CAMERA_PERMISSION_CODE,
                PermissionRequestCode.WRITE_EXTERNAL_PERMISSION_CODE,
                PermissionRequestCode.ACCESS_FINE_LOCATION_CODE -> {
                    methodToCall?.invoke()
                    methodToCall = null
                }
            }
        } else {
            if (!shouldShowRequestPermissionRationale(permissionRequestCode)) {
                Timber.d("Permission denied with checking \"Do not ask again\".")
                launchPermissionSettings()
            }
            @StringRes
            val resId = when (permissionRequestCode) {
                PermissionRequestCode.CAMERA_PERMISSION_CODE -> {
                    R.string.camera_permission_denied
                }
                PermissionRequestCode.WRITE_EXTERNAL_PERMISSION_CODE -> {
                    R.string.storage_permission_denied
                }
                PermissionRequestCode.ACCESS_FINE_LOCATION_CODE -> {
                    R.string.location_permission_denied
                }
            }
            finishActivityWithMessage(repo.getString(resId))
        }
    }

    private var methodToCall: (() -> (Unit))? = null

    fun callWithPermissionCheck(someMethodToCall: () -> Unit, requestCode: PermissionRequestCode) {
        Timber.d("checkPermissionAnd")
        if (hasPermission(requestCode)) {
            someMethodToCall.invoke()
        } else {
            methodToCall = someMethodToCall
            requestPermission(requestCode)
        }
    }

    private fun finishActivityWithMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        activity.finish()
    }
}
