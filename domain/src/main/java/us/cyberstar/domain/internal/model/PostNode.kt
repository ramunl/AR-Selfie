package us.cyberstar.domain.internal.model

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.domain.NodeSize
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPostType

open class PostNode(val arPostEntity: ArPostEntity) : Node() {

    val size: Vector3 = NodeSize.CELL_NODE_SMALL.getNodeSize()!!


    init {
        collisionShape = Box(Vector3(size.x, size.y, size.z))
    }

    fun isQuick() = arPostEntity.isQuick

}