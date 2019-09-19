package us.cyberstar.domain.external.loader

import us.cyberstar.domain.external.loader.grpc.telemetry.TelemetryRecorderBase

interface TelemetryRecorderFabric {
    fun getTelemetryRecorder(): TelemetryRecorderBase
}
