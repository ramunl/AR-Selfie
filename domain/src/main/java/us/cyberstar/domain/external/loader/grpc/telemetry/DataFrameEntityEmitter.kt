package us.cyberstar.domain.external.loader.grpc.telemetry

import us.cyberstar.data.entity.telemetry.DataFrameEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase

abstract class DataFrameEntityEmitter(open val arCoreFrameEmitter: ArCoreFrameEmitter) :
    EntityEmitterBase<DataFrameEntity>(),
    OnUpdateListener {

    abstract fun pointCloudInfo(): String

    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }

    abstract fun setCalcDescriptorsFlag(flag: Boolean)
}