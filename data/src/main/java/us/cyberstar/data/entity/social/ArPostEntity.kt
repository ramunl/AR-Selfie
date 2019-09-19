package us.cyberstar.data.entity.social

import android.location.Location
import com.cyber.math.Matrix4
import us.cyberstar.data.entity.PostCompositeIdEntity

data class ArPostEntity(
    var scale: Int,
    val isQuick: Boolean?,
    var title: String,
    val arPoster: ARPosterEntity, // Ar 3d-model structure
    val postCompId: PostCompositeIdEntity,
    var postTransform: Matrix4?,
    var location: Location?, //can be changed if we create 3d post
    var postContentEntity: PostContentEntity?,
    val isTransformable: Boolean = false,
    var anchorId: String? = null
) {
    fun isAnchorId() = anchorId?.isNotBlank() ?: false
    fun postId() = postCompId.postId
}
