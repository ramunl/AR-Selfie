package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.entity.telemetry.SessionFinalEntity
import us.cyberstar.data.entity.telemetry.SessionHeadEntity

interface HeadFinalEntityEmitter {
    fun getSessionFinalEntity(): SessionFinalEntity
    fun createHeadSessionEntity(onSessionHeadEntityListener: (SessionHeadEntity) -> Unit)
}