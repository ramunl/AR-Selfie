package us.cyberstar.domain.internal.loader.grpc.telemetry

import android.location.Location
import com.cyber.math.Matrix4
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.internal.factory.createPostEntity
import us.cyberstar.domain.internal.mapper.mapToArPosterEntity
import javax.inject.Inject


internal class PostEntityEmitterImpl @Inject constructor() : PostEntityEmitter() {
    override fun createEntity(
        title: String,
        isQuick: Boolean?,
        scale: Int,
        postId: Long,
        transformAndr: Matrix4?,
        arPosterModel: ArPosterModel,
        location: Location?,
        postContentEntity: PostContentEntity?,
        isTransformable: Boolean,
        anchorId: String?
    ): ArPostEntity {
        val entity = createPostEntity(
            isQuick,
            scale,
            postId,
            transformAndr,
            mapToArPosterEntity(arPosterModel),
            title,
            location,
            postContentEntity,
            isTransformable,
            anchorId
        )
        emitNext(entity)
        return entity
    }

}