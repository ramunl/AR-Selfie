package us.cyberstar.data.external.grpc

import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

interface GrpcTelemetryService {
    fun sendStreamMessage(entity: ArEntityTelemetry)
    fun closeChannel()
    fun openChannel(nextMethodToCall: (() -> Unit)?)
    fun saveTelemetryVideo(entity: SaveVideoRequestEntity): Boolean
}