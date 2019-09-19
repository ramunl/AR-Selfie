package us.cyberstar.data.mapper.utils

import android.location.Location
import base_types.BaseTypes

fun mapToLocation(coordinates: BaseTypes.GpsCoordinates): Location {
    return Location("").apply {
        latitude = coordinates.latitude
        longitude = coordinates.longitude
        altitude = coordinates.altitude
    }
}

fun mapToCoordinates(location: Location): BaseTypes.GpsCoordinates {
    return BaseTypes.GpsCoordinates.newBuilder()
        .setLatitude(location.latitude)
        .setAltitude(location.altitude)
        .setLongitude(location.longitude).build()
}