package us.cyberstar.domain.internal.loader

import us.cyberstar.domain.external.loader.TelemetryRecorderFabric
import us.cyberstar.domain.external.loader.local.TelemetryRecorderLocal
import us.cyberstar.domain.external.loader.grpc.telemetry.TelemetryRecorderRemote
import us.cyberstar.domain.external.loader.grpc.telemetry.TelemetryRecorderBase
import us.cyberstar.domain.internal.ArWorldLoaderSettings
import javax.inject.Inject

class TelemetryRecorderRecorderFabricImpl @Inject constructor(
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val telemetryRecorderLocal: TelemetryRecorderLocal,
    private val telemetryRecorderRemote: TelemetryRecorderRemote
) : TelemetryRecorderFabric {

    override fun getTelemetryRecorder(): TelemetryRecorderBase {
        return if (arWorldLoaderSettings.isLocal) {
            telemetryRecorderLocal
        } else {
            telemetryRecorderRemote
        }
    }
}