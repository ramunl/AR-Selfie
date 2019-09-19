package us.cyberstar.domain.external.manger.arScene

import android.graphics.Bitmap
import com.cyber.ux.TransformableNode
import com.google.ar.core.Frame
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import us.cyberstar.data.entity.PostCompositeIdEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.domain.external.ArCoreFrameSubscriber
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.manger.arScene.NodeManagerImpl
import us.cyberstar.domain.internal.model.PostNode

var CREATE_3D_ONLY = false // this is another shitty hack to skip all post besides of 3d

abstract class NodeManager(arCoreFrameEmitter: ArCoreFrameEmitter) :
    ArCoreFrameSubscriber(arCoreFrameEmitter) {
    abstract fun getNewInstance(): NodeManager
    abstract fun destroy()
    abstract fun onDrawFrame(frame: Frame)
    abstract fun addPostEntityToQueue(arPostEntity: ArPostEntity)
    abstract fun updatePostsWithNewList(arPostEntityArrayNew: Collection<ArPostEntity>)
    abstract fun create()

    abstract fun getNodeById(id: Long): Node?
    abstract fun removeNode(id: Long)
    abstract fun getPostNodesCount(): Int
    abstract fun getPostNodesInfo(): String

    interface NodeFactoryCallback {
        fun onNodeReady(postNode: Node)
    }

    abstract fun isEnabled(): Boolean
    abstract var transformableNodeCurrent: TransformableNode?

    abstract fun postMustBeSkipped(arPostEntity: ArPostEntity): Boolean
}