package us.cyberstar.presentation.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import us.cyberstar.common.external.SnackBarProvider
import javax.inject.Inject

class GpsDialogProvider @Inject constructor(
    private val context: Context,
    private val snackBarProvider: SnackBarProvider
) {
    fun isGPSEnabled(): Boolean {
        val locManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun showDialogGPS(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        builder.setTitle("Location services disabled")
        builder.setMessage("\nPlease enable GPS")
        builder.setPositiveButton("Enable") { dialog, _ ->
            dialog.dismiss()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        builder.setNegativeButton("Ignore") { dialog, _ ->
            dialog.dismiss()
            snackBarProvider.showMessage("Please turn on GPS to continue")
        }
        val alert = builder.create()
        alert.show()
    }
}