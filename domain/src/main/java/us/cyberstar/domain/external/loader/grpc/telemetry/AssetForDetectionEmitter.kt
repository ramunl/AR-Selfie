package us.cyberstar.domain.external.loader.grpc.telemetry

import com.cyber.math.Matrix4
import com.google.ar.sceneform.math.Vector3
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase

abstract class AssetForDetectionEmitter : EntityEmitterBase<AssetForDetectionEntity>() {
    abstract fun createEntity(
        camera: com.google.ar.core.Camera,
        hitPoint: Vector3,
        distanceToCamera: Float,
        planeHitTransForm: Matrix4
    ): AssetForDetectionEntity
}