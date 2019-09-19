package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.entity.telemetry.SessionFinalEntity
import us.cyberstar.data.entity.telemetry.SessionHeadEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

/**
 * The class runs grpc gridAsset with session data
 */

interface TelemetryRecorderBase {

    //used in Remote
    fun startSession()
    fun closeSession()

    fun stopAndSaveTelemetry(finalEntity: SessionFinalEntity)
    fun startTelemetry(headEntity: SessionHeadEntity)

    fun appendEntity(entity: ArEntityTelemetry)

    fun resetCounter()
}