package us.cyberstar.domain.external.loader.base

import us.cyberstar.data.entity.MultipleLoadWorldReplyEntity
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase


abstract class ArWorldLoader : EntityEmitterBase<MultipleLoadWorldReplyEntity>() {
    abstract fun startSceneWorldUpdating()
    abstract fun stopSceneWorldUpdating()
}