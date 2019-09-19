package us.cyberstar.data.entity.telemetry

import android.location.Location
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

data class LoadWorldRequestEntity(val location: Location) : ArEntityTelemetry()