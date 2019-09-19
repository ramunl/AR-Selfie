package us.cyberstar.data.entity.telemetry

import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

class SaveVideoRequestEntity(val sessionId:String, var videoUrl: String) : ArEntityTelemetry()