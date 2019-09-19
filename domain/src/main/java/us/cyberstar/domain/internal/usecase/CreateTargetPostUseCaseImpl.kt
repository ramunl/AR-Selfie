package us.cyberstar.domain.internal.usecase

import com.cyber.math.Matrix4
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.*
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.factory.CellColor
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.external.model.mapper.getPostContentEntityFrom
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.external.usecase.CreateTargetPostUseCase
import us.cyberstar.domain.internal.manger.arScene.grid.VertGridManagerImpl
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever
import us.cyberstar.domain.internal.usecase.base.TargetingPostData
import us.cyberstar.domain.internal.utils.getMatrix4
import javax.inject.Inject

/**
 * When user wants to create post, he has 2 options:
 *  1) quick post
 *  If it's quick post, it should be created right after this method was called:
 *   So the sequence is:
 *   1) prepareAndCreateQuickPost
 *   2) play such an animation: put the post in a front of the camera and move it to 1 meter down
 *   3) in onUpdate we check if it's Quick mode we look for horizontal planes
 *   4) once we found a horizontal plane our post drops down on the floor
 *  2) targeting:
 *    1) we toggle grid mode ON
 *    2) in update mode we check if it's a TARGETING mode
 *    3) we look for a grid cell node which is in a front of our Camera and put the post inside
 *    4) if grid is not visible the post is not visible either
 */

internal class CreateTargetPostUseCaseImpl @Inject constructor(
    private val mediaLoader: MediaLoader,
    private val currentSessionNodeManager: NodeManager,
    private val postDataRetriever: PostDataRetriever,
    override var arCoreFrameEmitter: ArCoreFrameEmitter,
    val arCoreScene: ArCoreScene,
    val s3Cache: S3Cache,
    val postEntityEmitter: PostEntityEmitter,
    val snackBarProvider: SnackBarProvider,
    private val rootNodeProvider: RootNodeProvider,
    private val renderableFactory: RenderableFactory,
    val schedulersProvider: SchedulersProvider,
    private val vertGridManager: VertGridManagerImpl,
    private val assetForDetectionEmitter: AssetForDetectionEmitter,
    private val createPostFabric: CreatePostFabric,
    private val compositeDisposable: CompositeDisposable
) : CreateTargetPostUseCase(arCoreFrameEmitter) {

    private var confirmTargetingPostButtonStateEmitter: BehaviorSubject<Boolean>? = null
    private var pauseTargetingPost = false
    //used in targeting mode
    //if user post is inside of a cell we saved it here
    private var selectedNode: CellNode? = null
    //we get this model from UI, and save it to this temp field
    //if user finally decides to create post, we use it
    var arPostModel: ArPostModel? = null
    var arPostEntity: ArPostEntity? = null
    val scene = arCoreScene.scene
    val camera: Camera = arCoreScene.scene.camera

    override fun continueUseCase() {
        pauseTargetingPost = false
    }


    override fun getInfo(): String {
        return "v cells size = ${vertGridManager.cellNodeList.size} anchors = ${vertGridManager.gridAnchorNodes.size}"
    }

    private fun startUseCase() {
        Timber.d("initUseCase")
        vertGridManager.cellsVisible = true
        renderableFactory.loadRenderables()
        vertGridManager.createGrid()
        rootNodeProvider.nodeIsVisible = true // set the current root node visible
        subscribeToArCoreFrames()
        pauseTargetingPost = false
    }

    override fun closeUseCase(destroyNode: Boolean) {
        Timber.d("stopUseCase")
        vertGridManager.cellsVisible = false
        if (destroyNode) {
            currentNodeRemove()
        }
        vertGridManager.destroyGrid()
        unsubscribeFromArCoreFrames()
        selectedNode = null
        arPostModel = null
    }


    private fun currentNodeRemove() =
        arPostEntity?.let { currentSessionNodeManager.removeNode(it.postId()) }

    private fun currentNode() = currentSessionNodeManager.transformableNodeCurrent


    private fun doCreateTargetArPost(
        arPostModel: ArPostModel
    ) {
        val postContentEntity = getPostContentEntityFrom(arPostModel)
        val arPoster = ArPosterModel(listOf(""))
        arPostEntity = postEntityEmitter.createEntity(
            arPostModel.title,
            false,
            1,
            0,
            null,
            arPoster,
            null,
            postContentEntity,
            true
        )
        currentSessionNodeManager.addPostEntityToQueue(arPostEntity!!)
    }


    override fun prepareTargetingPost(postModel: ArPostModel): BehaviorSubject<Boolean> {
        Timber.d("prepareTargetingPost")
        this.arPostModel = postModel
        startUseCase()
        doCreateTargetArPost(postModel)
        confirmTargetingPostButtonStateEmitter = BehaviorSubject.create()
        return confirmTargetingPostButtonStateEmitter!!
    }

    /*
    private fun isPostInsideCell(postNode: PostNode, cellNode: CellNode?) =
        cellNode?.let {
            abs(postNode.worldPosition.x - cellNode.worldPosition.x) < 0.1 &&
                    abs(postNode.worldPosition.y - cellNode.worldPosition.y) < 0.1 &&
                    abs(postNode.worldPosition.z - cellNode.worldPosition.z) < 0.1
        } ?: false

    */

    override fun onUpdate(frameTime: FrameTime) {
        if (!pauseTargetingPost && !isAnimating()) {
            var cellNew: CellNode?
            arCoreFrameEmitter.lastFrame()?.let { lastFrame ->
                if (lastFrame.camera.trackingState == TrackingState.TRACKING) {
                    currentNode()?.let { postNode ->
                        schedulersProvider.ui().scheduleDirect {
                            cellNew = updateTargetingPostNodePosition(
                                lastFrame,
                                postNode,
                                arCoreScene,
                                selectedNode
                            )
                            var isCollided = false
                            cellNew?.let {
                                if (selectedNode != it) {
                                    isCollided = cellNew!!.isCollided(rootNodeProvider)
                                    cellNew?.renderable =
                                        renderableFactory.getCellRenderable(if (isCollided) CellColor.Red else CellColor.Green)
                                    selectedNode = cellNew
                                    confirmTargetingPostButtonStateEmitter?.onNext(!isCollided)
                                }
                                vertGridManager.dropCellColors(selectedNode)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun confirmTargetingPost() = Single.create<Boolean> { emitter ->
        try { //val screenPos = with(arCoreScene.scene.camera) { worldToScreenPoint(worldPosition) }
            pauseTargetingPost = true
            currentNode()?.let { node ->
                val hitPoint = node.worldPosition
                var postData: TargetingPostData? = null
                while (true) {
                    try {
                        Timber.d("Trying to fetch TargetingPostData..")
                        postData = postDataRetriever.retrieveTargetingPostData(hitPoint)
                    } catch (e: Throwable) {
                        // Timber.e(e)
                    }
                    if (postData != null) {
                        break
                    } else {
                        Thread.sleep(100)
                    }
                }
                val scale = node.localScale.x
                arPostModel?.let { model ->
                    selectedNode?.let {
                        val distance = it.distanceToCamera
                        if (targetingPostCreateTask(
                                distance,
                                it.getMatrix4(),
                                model,
                                postData!!,
                                scale.toInt(),
                                hitPoint
                            )
                        ) {
                            emitter.onSuccess(true)
                        } else {
                            throw Throwable("TargetingPostTask failed")
                        }
                    } ?: throw Throwable("Cell node null!")
                } ?: throw Throwable("ArPostModel NULL!")
            } ?: throw Throwable("Post node NULL!")
        } catch (e: Throwable) {
            emitter.onError(e)
        }
    }

    private fun targetingPostCreateTask(
        distance: Float,
        planeHitTransForm: Matrix4,
        arPostModel: ArPostModel,
        targetingPostData: TargetingPostData,
        scaleToApply: Int,
        hitPoint: Vector3
    ): Boolean {
        Timber.d("targetingPostCreateTask")

        with(arPostEntity!!) {
            postContentEntity = getPostContentEntityFrom(arPostModel)
            scale = scaleToApply
            postTransform = planeHitTransForm
            location =  targetingPostData.postLocation
        }

        val lastFrame = arCoreFrameEmitter.lastFrame()
        val camera = lastFrame!!.camera
        //TODO we can just create assetForDetection(without emitting)
        val assetForDetectionEntity =
            assetForDetectionEmitter.createEntity(
                camera,
                hitPoint,
                distance,
                planeHitTransForm
            )
        return createPostFabric.getPostCreateFabric().createPostRequest(CreatePostRequestEntity(assetForDetectionEntity, arPostEntity!!))
    }

}
