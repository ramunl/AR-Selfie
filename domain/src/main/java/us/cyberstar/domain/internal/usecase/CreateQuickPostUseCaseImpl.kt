package us.cyberstar.domain.internal.usecase

import com.cyber.math.Matrix4
import io.reactivex.Single
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.external.s3.S3Cache
import com.cyber.ux.SceneFormNodeProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.external.model.mapper.getPostContentEntityFrom
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.external.usecase.CreateQuickPostUseCase
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever
import us.cyberstar.domain.internal.usecase.base.QuickPostData
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

internal class CreateQuickPostUseCaseImpl @Inject constructor(
    private val horGridManager: HorGridManagerImpl,
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
    private val createPostFabric: CreatePostFabric
) : CreateQuickPostUseCase(arCoreFrameEmitter) {

    var quickPostPlaced = false

    override fun getInfo(): String {
        return "h cells size = ${horGridManager.cellNodeList.size} anchors = ${horGridManager.gridAnchorNodes.size}"
    }

    override fun continueUseCase() {

    }

    //we get this model from UI, and save it to this temp field
    //if user finally decides to create post, we use it
    var arPostModel: ArPostModel? = null
    val scene = arCoreScene.scene
    val camera = arCoreScene.scene.camera


    private fun startUseCase() {
        Timber.d("initUseCase")
        horGridManager.cellsVisible = true
        renderableFactory.loadRenderables()
    }

    override fun closeUseCase(destroyNode: Boolean) {
        Timber.d("stopUseCase")
        horGridManager.cellsVisible = false
        if (destroyNode) {
            //quick post can't be removed or canceled
           // currentNodeRemove()
        }
        arPostModel = null
    }


    override fun prepareAndCreateQuickPost(postModel: ArPostModel) =
        Single.create<Boolean> {
            try {
                startUseCase()
                quickPostPlaced = false
                Timber.d("prepareAndCreateQuickPost")
                this.arPostModel = postModel
                if (!doCreateQuickArPost(
                        postModel,
                        postDataRetriever.retrieveQuickPostData()
                    )
                ) {
                    throw Throwable("CreateQuickPost failed")
                }
                it.onSuccess(true)
            } catch (e: Throwable) {
                it.onError(e)
            }
        }

   // private fun currentNodeRemove() = arPostModel?.let { currentSessionNodeManager.removeNode(it.postId) }


    private fun doCreateQuickArPost(
        arPostModel: ArPostModel,
        quickPostData: QuickPostData
    ): Boolean {
        val postContentEntity = getPostContentEntityFrom(arPostModel)
        val postLocation = quickPostData.postLocation
        val arPoster = ArPosterModel(listOf(""))
        val entityPost = postEntityEmitter.createEntity(
            arPostModel.title,
            true,
            1,
            0,
            Matrix4(),
            arPoster,
            postLocation,
            postContentEntity,
            false
        )
        val res = createPostFabric.getPostCreateFabric()
            .createPostRequest(CreatePostRequestEntity(null, entityPost))
        if (res) {
            currentSessionNodeManager.addPostEntityToQueue(entityPost)
        }
        return res
    }

}
