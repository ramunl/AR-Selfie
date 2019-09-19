package us.cyberstar.domain.internal.model

import com.google.ar.core.Plane
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import social.Social
import us.cyberstar.data.entity.telemetry.ArPlaneEntity
import us.cyberstar.domain.NodeSize

class CellNode(
    val nodeSize: NodeSize,
    val nodeName: String,
    arPlaneEntity: ArPlaneEntity,
    localPos: Vector3,
    renderable: Renderable?,
    visible: Boolean
) : Node() {

    //this distance is used in AssetForDetectionEmitter class
    var distanceToCamera: Float = 0f
    var renderableTemp: Renderable? = null
    val orientation: CellNodeOrientation =
        if (arPlaneEntity.type == Plane.Type.VERTICAL) CellNodeOrientation.CELL_NODE_VERTICAL else CellNodeOrientation.CELL_NODE_HORIZONTAL

    var isEmpty: Boolean = true

    init {
        localPosition = localPos
        renderableTemp = renderable
        if (visible) {
            this.renderable = renderable
        }
        name = nodeName
    }

    var isVisible: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                this.renderable = if (value) renderableTemp else null
            }
        }


    val parentPlane = arPlaneEntity
}