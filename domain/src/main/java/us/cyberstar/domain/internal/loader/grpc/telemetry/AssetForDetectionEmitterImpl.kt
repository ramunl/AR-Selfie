package us.cyberstar.domain.internal.loader.grpc.telemetry

import com.cyber.math.Matrix4
import com.google.ar.sceneform.math.Vector3
import timber.log.Timber
import us.cyberstar.data.entity.AssetForDetectionEntity
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.internal.factory.createAssetForDetectionEntity
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever
import javax.inject.Inject

internal class AssetForDetectionEmitterImpl @Inject constructor(
    private val postDataRetriever: PostDataRetriever
) : AssetForDetectionEmitter() {

    override fun createEntity(
        camera: com.google.ar.core.Camera,
        hitPoint: Vector3,
        distanceToCamera: Float,
        planeHitTransForm: Matrix4
    ): AssetForDetectionEntity {
        Timber.d("createAssetForDetectionEntity AssetForDetectionEntity")
        val entity = with(postDataRetriever.retrieveTargetingPostData(hitPoint)) {
            createAssetForDetectionEntity(
                hitPoint,
                planeHitTransForm,
                distanceToCamera,
                camera,
                postLocation,
                arFrameBitmap.width.toFloat(),
                arFrameBitmap.height.toFloat(),
                //snapshotLocalPath,
                arFrameBitmap
            )
        }
        return entity
    }

}


