package us.cyberstar.domain.internal.provider

import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.model.PostNode
import us.cyberstar.domain.internal.utils.TransQuatWrap
import javax.inject.Inject

internal class RootNodeProviderImpl @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val arCoreScene: ArCoreScene,
    private val arCoreSession: ArCoreSession
) : RootNodeProvider {

    override fun getNewInstance(): RootNodeProvider =
        RootNodeProviderImpl(schedulersProvider, arCoreScene, arCoreSession)

    override fun removePostNode(node: Node) {
        Timber.d("removePostNode $node")
        schedulersProvider.ui().scheduleDirect {
            childNodes.remove(node)
            getRoot().removeChild(node)
        }
    }

    override fun postNodes() = childNodes


    private val childNodes = ArrayList<Node>()

    override fun addPostNode(postNode: Node) {
        Timber.d("addPostNode $postNode")
        postNode.isEnabled = getRoot().isEnabled
        childNodes.add(postNode)
        //postNode.setParent(arCoreScene.scene)
        //arCoreScene.scene.addChild(postNode)
        getRoot().addChild(postNode)
    }

    private var rootNodeAnchor: AnchorNode? = null
    //private var rootNode: Node? = null
    override fun getRoot(): Node {
        if (rootNodeAnchor == null) {
            Timber.d("create root node anchor isVisible $nodeIsVisible")
            //rootNodeAnchor = AnchorNode()
            rootNodeAnchor = AnchorNode(arCoreSession.session.createAnchor(Pose.IDENTITY)).apply {
                setParent(arCoreScene.scene)
                isEnabled = nodeIsVisible
            }
        }
        return rootNodeAnchor!!
    }

    /*
        private var rootNode: Node? = null
        override fun getRoot(): Node {
            if (rootNode == null) {
                Timber.d("init root node..!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                rootNode = Node()
                arCoreScene.scene.addChild(rootNode)
            }
            return rootNode!!
        }*/
    override fun setRootPos(transQuatWrap: TransQuatWrap) {
        with(transQuatWrap) {
            getRoot().worldRotation = quaternion()
            getRoot().worldPosition = position
            // getRoot().worldPosition = with(getRoot()) {Vector3(this.worldPosition.x + 0.1f, this.worldPosition.y, this.worldPosition.z)}
            Timber.d("root pos $position quat ${quaternion()}")

        }
    }

    private var gridNode: Node? = null

    private fun getGridNode(): Node {
        if (gridNode == null) {
            gridNode = Node().apply {
                getRoot().addChild(this)
            }
        }
        return gridNode!!
    }


    override fun removeAllNodes() {
        schedulersProvider.ui().scheduleDirect {
            Timber.d("clear all root node children..")
            for (child in childNodes) {
                if (child is AnchorNode) {
                    child.anchor?.detach()
                }
                getRoot().removeChild(child)
                // arCoreScene.scene.removeChild(child)
            }
            childNodes.clear()
            rootNodeAnchor?.let {
                arCoreScene.scene.removeChild(it)
                rootNodeAnchor?.anchor?.detach()
                rootNodeAnchor = null
            }
        }
    }

    override fun destroyGrid() {
        Timber.d("destroyGrid")
        gridNode?.let {
            it.parent?.removeChild(it)
            gridNode = null
        }
    }

    override fun getGrid() = getGridNode()

    override var nodeIsVisible: Boolean = true
        set(value) {
            field = value
            if (rootNodeAnchor != null) {
                getRoot().isEnabled = value
                for (postNode in postNodes()) {
                    postNode.isEnabled = value
                }
            }
        }

}
