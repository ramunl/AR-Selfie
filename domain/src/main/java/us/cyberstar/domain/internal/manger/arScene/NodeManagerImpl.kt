package us.cyberstar.domain.internal.manger.arScene

import android.graphics.Bitmap
import android.util.LongSparseArray
import com.cyber.math.Matrix4
import com.google.ar.core.Frame
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.social.ArPostEntity
import com.cyber.ux.SceneFormNodeProvider
import com.cyber.ux.TransformableNode
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.Renderable
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.arScene.CREATE_3D_ONLY
import us.cyberstar.domain.external.manger.arScene.RootNodeManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.factory.ModelFactoryCallback
import us.cyberstar.domain.internal.factory.RenderableFactoryCallback
import us.cyberstar.domain.internal.factory.TEST_MODEL_3D
import us.cyberstar.domain.internal.manger.arScene.base.NodeManagerBaseImpl
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.domain.internal.model.PostNode
import us.cyberstar.domain.internal.utils.matToQuatTrans2
import java.lang.Exception
import javax.inject.Inject

/**
 * The class listens to new post entities
 * and creates and put new banner posts into the ar scene.

 * Banner can be created only if camera.trackingState = TrackingState.TRACKING,
 * so we check it here and emit entity when the state is appropriate
 */
class NodeManagerImpl @Inject constructor(
    private val arCoreScene: ArCoreScene,
    private val cloudAnchorManager: CloudAnchorManager,
    // private val firebaseManager: FirebaseManager,
    private val sceneFormNodeProvider: SceneFormNodeProvider,
    private val horGridManager: HorGridManagerImpl,
    private val rootNodeManager: RootNodeManager,
    override var arCoreFrameEmitter: ArCoreFrameEmitter,
    private val rootNodeProvider: RootNodeProvider,
    private val mediaLoader: MediaLoader,
    private val snackBarProvider: SnackBarProvider,
    private val renderableFactory: RenderableFactory,
    override val schedulersProvider: SchedulersProvider
) : NodeManagerBaseImpl(arCoreFrameEmitter, snackBarProvider, schedulersProvider) {


    private fun addPostToMap(arPostEntity: ArPostEntity, node: Node) {
        val postId = arPostEntity.postId()
        Timber.d(".............addPostToMap postId = $postId node = $node...")
        currentNodeCreatedMap.append(postId, node)
    }

    private fun postExists(arPostEntity: ArPostEntity): Boolean {
        val postId = arPostEntity.postId()
        val exist = currentNodeCreatedMap[postId] != null
        Timber.d(".............addPostToMap postId = $postId exist = $exist...")
        return exist
    }

    override fun isEnabled() = rootNodeProvider.nodeIsVisible


    override fun getPostNodesCount() = currentNodeCreatedMap.size()

    override fun getPostNodesInfo(): String {
        var counterQuick = 0
        var counterTargeting = 0

        //Timber.d("currentPostEntityToCreateMap = $currentPostEntityToCreateMap , size = ${currentPostEntityToCreateMap.size()}")
        for (ind in 0 until currentNodeCreatedMap.size()) {
            /*val postNode = currentNodeCreatedMap.valueAt(ind)
            if (postNode.isQuick()) {
                ++counterQuick
            } else {
                ++counterTargeting
            }*/
        }
        return "quick=$counterQuick target=$counterTargeting"
    }

    override fun removeNode(id: Long) {
        getNodeById(id)?.let {
            Timber.d("remove post with postId = $id")
            rootNodeProvider.removePostNode(it)
        }
        ignoredMap.remove(id)
        currentNodeCreatedMap.remove(id)
    }

    override fun getNodeById(id: Long): Node? = currentNodeCreatedMap[id]

    private val ignoredMap = LongSparseArray<ArPostEntity>()
    private val currentPostEntityToCreateMap = LongSparseArray<ArPostEntity>()
    private val currentNodeCreatedMap = LongSparseArray<Node>() // current node created list

    override fun updatePostsWithNewList(arPostEntityArrayNew: Collection<ArPostEntity>) {
        Timber.d("updatePostsWithNewList")
        val postNodeMapNew = LongSparseArray<ArPostEntity>()
        for (postEntity in arPostEntityArrayNew) {
            if (!postMustBeSkipped(postEntity)) {
                postEntity.postId().let {
                    //init map with new posts
                    postNodeMapNew.append(it, postEntity)
                    if (currentPostEntityToCreateMap.get(it) == null) {//check is the post already processed?
                        currentPostEntityToCreateMap.append(it, postEntity)
                        Timber.d("add new Post $it to scene from arPostEntityArrayNew")
                        addPostEntityToQueue(postEntity)
                    }
                }
            }
        }

        /**
         * In 3d mode we use only one current session node manager,
         * we don't check if post removed or not, we just add posts we got from multiple worlds
         */
        if (!CREATE_3D_ONLY) {
            //we iterate through currentNodeCreatedMap keys and remove all node
            //which don't exist in the new list
            for (i in 0..currentNodeCreatedMap.size()) {
                val postCompIdCurrent = currentNodeCreatedMap.keyAt(i)
                if (postNodeMapNew.get(postCompIdCurrent) == null) {
                    currentNodeCreatedMap[postCompIdCurrent]?.let {
                        Timber.d("removePostNode $it")
                        rootNodeProvider.removePostNode(it)
                    }
                }
            }
        }

    }

    override fun getNewInstance() = with(rootNodeProvider.getNewInstance()) {
        NodeManagerImpl(
            arCoreScene,
            cloudAnchorManager,
            // firebaseManager,
            sceneFormNodeProvider,
            horGridManager,
            rootNodeManager.getNewInstance(this),
            arCoreFrameEmitter,
            this,
            mediaLoader,
            snackBarProvider,
            renderableFactory,
            schedulersProvider
        )
    }


    override fun onDrawFrame(frame: Frame) {
        //used with GL scene, we need to update node transforms every drawn GL lastFrame
    }

    override fun create() {
        Timber.d("Node manager create()")
        rootNodeManager.subscribeToAugmentedImgDbChanges()
        //horGridManager is one instance global manager
        super.subscribeToArCoreFrames()
    }

    override fun destroy() {
        super.unsubscribeFromArCoreFrames()
        rootNodeManager.unSubscribeFromAugmentedImgDbChanges()
        rootNodeProvider.removeAllNodes()
        rootNodeManager.removeAllNodes()
    }

    override var transformableNodeCurrent: TransformableNode? = null

    override fun createArPostAndAddToScene(arPostEntity: ArPostEntity) {
        if (ignoredMap.get(arPostEntity.postId()) == null) {
            ignoredMap.append(arPostEntity.postId(), arPostEntity)
            Timber.w("create ArPost and add to scene called $arPostEntity")
            if (!postExists(arPostEntity)) {
                if (arPostEntity.isAnchorId()) {
                    if(CREATE_3D_ONLY) {
                        if (arPostEntity.isTransformable) {
                            doCreate3dNode(arPostEntity, object : NodeFactoryCallback {
                                override fun onNodeReady(postNode: Node) {
                                    transformableNodeCurrent = postNode as TransformableNode
                                }
                            })
                        } else {
                            loadNodeCloudAnchor(
                                object : CloudAnchorListener {
                                    override fun onAnchorLoaded(anchor: Anchor) {
                                        doCreate3dNode(arPostEntity, object : NodeFactoryCallback {
                                            override fun onNodeReady(postNode: Node) {
                                                postNode.setParent(
                                                    AnchorNode(anchor).apply {
                                                        Timber.d("create AnchorNode $this")
                                                        setParent(arCoreScene.scene)
                                                    })
                                            }
                                        })

                                    }
                                },
                                arPostEntity.anchorId!!
                            )
                        }
                    }
                } else {
                    doCreatePostNode(arPostEntity, object : NodeFactoryCallback {
                        override fun onNodeReady(postNode: Node) {
                            Timber.d("onNodeReady $postNode")
                            if (arPostEntity.isTransformable) {
                                transformableNodeCurrent = postNode as TransformableNode
                            } else {
                                Timber.d("onNodeReady $postNode")
                                postNode.apply {
                                    if (arPostEntity.isQuick == true) {
                                        Timber.d("adding a new quick post to current root $this")
                                        rootNodeProvider.addPostNode(this)
                                        horGridManager.onQuickPostAdded(postNode as PostNode)
                                    } else {
                                        Timber.d("adding a new targeting post to current root $this")
                                        rootNodeProvider.addPostNode(this)
                                        setNodeTransform(postNode as PostNode)
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }
    }


    interface CloudAnchorListener {
        fun onAnchorLoaded(anchor: Anchor)
    }

    private fun loadNodeCloudAnchor(
        cloudAnchorListener: CloudAnchorListener,
        cloudAnchorId: String
    ) {
        Timber.d("loadNodeCloudAnchor for cloudAnchorId = $cloudAnchorId")
        cloudAnchorManager.resolveCloudAnchor(cloudAnchorId,
            object : CloudAnchorManager.CloudAnchorListener {
                override fun onCloudTaskComplete(anchor: Anchor) {
                    val cloudState = anchor.cloudAnchorState
                    Timber.d("onCloudTaskComplete $anchor")
                    if (!cloudState.isError) {
                        cloudAnchorListener.onAnchorLoaded(anchor)
                    } else {
                        anchor.detach()
                        Timber.e("cloudState error = $cloudState")
                    }
                }
            }
        )
    }

    var anchor: Anchor? = null


    private fun setNodeTransform(postNode: PostNode) {
        postNode.apply {
            localScale =
                Vector3(
                    arPostEntity.scale.toFloat(),
                    arPostEntity.scale.toFloat(),
                    arPostEntity.scale.toFloat()
                )
            arPostEntity.postTransform?.let {
                with(matToQuatTrans2(Matrix4(it))) {
                    worldRotation = quaternion()
                    worldPosition = position
                }
            }
        }
    }


    private fun getPostBitmap(arPostEntity: ArPostEntity): Bitmap? {
        var bitmap: Bitmap? = null
        val thumbs = arPostEntity.postContentEntity?.thumbnails()
        thumbs?.let {
            if (thumbs.isNotEmpty()) {
                Timber.d("thumbs num = ${thumbs.size} thumbs = $thumbs")
                val thumbToSet = thumbs.values.iterator().next()
                Timber.d("thumbToSet = $thumbToSet")
                bitmap = mediaLoader.downLoadImageSynch(thumbToSet)
            } else {
                Timber.e("thumbs empty, can't create post")
            }
        }
        return bitmap
    }


    private fun onRenderableLoaded(
        renderable: Renderable,
        arPostEntity: ArPostEntity,
        nodeFactoryCallback: NodeFactoryCallback?
    ) {
        schedulersProvider.ui().scheduleDirect {
            Timber.d("onRenderableLoaded $renderable for $arPostEntity")
            var isNodeAdded = false
            try {
                createNode(arPostEntity, renderable, nodeFactoryCallback)
                isNodeAdded = true
            } catch (e: Exception) {
                Timber.e(e)
            }
            if (!isNodeAdded) {
                ignoredMap.remove(arPostEntity.postId()) //remove it from ignore list, will try to create it again later if we get it from world reply request
                Timber.e("isNodeAdded $isNodeAdded !!!!!!!!!!!!!!!!!!!!!")
                addPostEntityToQueue(arPostEntity)
            }
        }
    }

    private fun doCreate3dNode(
        arPostEntity: ArPostEntity,
        nodeFactoryCallback: NodeFactoryCallback?
    ) {
        Timber.e("..................createPostNode 3d model.....................")
        renderableFactory.load3dModel(
            TEST_MODEL_3D,
            object : ModelFactoryCallback { //TODO update it with real 3d model!!!
                override fun onRenderableReady(renderable: ModelRenderable) {
                    onRenderableLoaded(renderable, arPostEntity, nodeFactoryCallback)
                }
            })
    }

    private fun doCreatePostNode(
        arPostEntity: ArPostEntity,
        nodeFactoryCallback: NodeFactoryCallback?
    ) {
        Timber.d("doCreatePostNode")
        if (isTrackingState()) {
            getPostBitmap(arPostEntity)?.let {
                renderableFactory.loadPostRenderable(
                    arPostEntity.title,
                    getPostBitmap(arPostEntity)!!,
                    object : RenderableFactoryCallback {
                        override fun onRenderableReady(viewRenderable: ViewRenderable) {
                            onRenderableLoaded(viewRenderable, arPostEntity, nodeFactoryCallback)
                        }
                    })
            } ?: { Timber.e("can't load bitmap for $arPostEntity") }()
        } else {
            Timber.e("can't create post, not trackable state!!!!!")
        }
    }


    private fun createNode(
        arPostEntity: ArPostEntity,
        modelRenderable: Renderable,
        nodeFactoryCallback: NodeFactoryCallback?
    ) {
        Timber.d("createNode transformable ? = ${arPostEntity.isTransformable}")

        val node: Node?
        if (arPostEntity.isTransformable) {
            node = TransformableNode(sceneFormNodeProvider.transformationSystem).apply {
                renderable = modelRenderable
                select()
            }
        } else {
            node = PostNode(arPostEntity).also {
                it.renderable = modelRenderable
            }
        }
        addPostToMap(arPostEntity, node)
        rootNodeProvider.addPostNode(node)
        nodeFactoryCallback?.onNodeReady(node)
    }
}



