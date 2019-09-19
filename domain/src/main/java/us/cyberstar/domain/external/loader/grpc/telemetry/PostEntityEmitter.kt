package us.cyberstar.domain.external.loader.grpc.telemetry

import android.location.Location
import com.cyber.math.Matrix4
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.internal.loader.grpc.telemetry.EntityEmitterBase

abstract class PostEntityEmitter : EntityEmitterBase<ArPostEntity>() {
    abstract fun createEntity(
        title: String,
        isQuick: Boolean?,
        scale: Int,
        postId: Long,
        transformAndr: Matrix4?,
        arPosterModel: ArPosterModel,
        location: Location?,
        postContentEntity: PostContentEntity?,
        isTransformable: Boolean,
        anchorId: String? = null
    ): ArPostEntity
}