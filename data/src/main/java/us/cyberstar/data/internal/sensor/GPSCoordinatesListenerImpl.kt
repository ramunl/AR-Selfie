package us.cyberstar.data.internal.sensor

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import javax.inject.Inject

internal class GPSCoordinatesListenerImpl @Inject constructor(
    context: Context
) : GPSCoordinatesListener() {

    private var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationRequest: LocationRequest? = null


    @Throws(java.lang.SecurityException::class)
    override fun getLastKnownLocation(): Location? = location


    override fun onLocationResult(var1: LocationResult) {
        for (location in var1.locations) {
            this.location = location
        }
    }

    override fun onLocationAvailability(locationAvailability: LocationAvailability) {
        // Timber.d("isLocationAvailable = ${locationAvailability.isLocationAvailable}")
    }

    @Throws(SecurityException::class)
    override fun registerListener() {
        if (locationRequest == null) {
            Timber.d("registerListener gps listener")
            locationRequest = LocationRequest.create()
                .apply {
                    priority = PRIORITY_HIGH_ACCURACY
                    interval = 10 * 1000
                    fastestInterval = 2 * 1000
                }
            mFusedLocationClient.requestLocationUpdates(locationRequest, this, null)
        }
        //val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    }

    override fun unRegisterListener() {
        Timber.d("unRegisterListener gps listener")
        mFusedLocationClient.removeLocationUpdates(this)
    }

}