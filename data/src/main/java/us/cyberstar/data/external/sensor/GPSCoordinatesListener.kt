package us.cyberstar.data.external.sensor

import android.location.Location
import com.google.android.gms.location.LocationCallback


abstract class GPSCoordinatesListener : LocationCallback() {
    abstract fun registerListener()
    abstract fun unRegisterListener()
    var location: Location? = null
        protected set;

    abstract fun getLastKnownLocation(): Location?
}