package us.cyberstar.domain.external.provider

import com.google.ar.sceneform.Node
import us.cyberstar.domain.internal.model.PostNode
import us.cyberstar.domain.internal.utils.TransQuatWrap

interface RootNodeProvider {
    fun getRoot(): Node
    fun destroyGrid()
    fun getGrid(): Node
    fun removeAllNodes()
    fun setRootPos(pos: TransQuatWrap)
    fun removePostNode(node: Node)
    fun postNodes(): ArrayList<Node>
    fun getNewInstance(): RootNodeProvider
    fun addPostNode(postNode: Node)
    var nodeIsVisible: Boolean
}
