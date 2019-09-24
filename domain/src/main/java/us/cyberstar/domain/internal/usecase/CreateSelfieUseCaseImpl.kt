package us.cyberstar.domain.internal.usecase

import android.content.Context
import com.cyber.ux.SceneFormNodeProvider
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.FrameTime
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.data.entity.social.PhotoPostContentEntity
import us.cyberstar.data.entity.social.PostContentEntity
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.helper.getNewId
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.external.model.mapper.getPostContentEntityFrom
import us.cyberstar.domain.external.usecase.CreateAr3dPostUseCase
import us.cyberstar.domain.external.usecase.CreateSelfieUseCase
import us.cyberstar.domain.faceToCamera
import us.cyberstar.domain.internal.usecase.base.Ar3dModelPostData
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever
import us.cyberstar.domain.internal.utils.getMatrix4
import us.cyberstar.domain.internal.utils.screenCenter
import us.cyberstar.domain.isAnimating
import us.cyberstar.domain.movePostNodeTo
import us.cyberstar.domain.movePostToPlane
import java.util.HashMap

class CreateSelfieUseCaseImpl(
    private val context: Context,
    private val sceneFormNodeProvider: SceneFormNodeProvider,
    private val renderableFactory: RenderableFactory,
    private val schedulersProvider: SchedulersProvider,
    arCoreFrameEmitter: ArCoreFrameEmitter,
    val arCoreSession: ArCoreSession,
    val currentSessionNodeManager: NodeManager,
    val createPostFabric: CreatePostFabric,
    val postEntityEmitter: PostEntityEmitter,
    val postDataRetriever: PostDataRetriever,
    val arCoreScene: ArCoreScene,
    val cloudAnchorManager: CloudAnchorManager
) : CreateSelfieUseCase(arCoreFrameEmitter) {

    /* override fun addNewModelToScene(arPosterModel: ArPosterModel) {
       Timber.d("addNewModelToScene")
       val postContentEntity = PostContentEntity(PhotoPostContentEntity(0, 0, "", HashMap()))
       entityPost = postEntityEmitter.createEntity(
           "",
           null,
           1,
           0,
           null,
           arPosterModel,
           null,
           postContentEntity,
           true,
           "anchor_id_temp" // this is temp anchor id to recognize it as 3d cloud post
       )
       controlMode.sceneMode = ControlMode.SceneMode.FACE_TO_CAMERA
       currentSessionNodeManager.addPostEntityToQueue(entityPost!!)
   }*/

    override fun addNewModelToScene(arPostModel: ArPostModel) {
        val postContentEntity = getPostContentEntityFrom(arPostModel)
        val arPoster = ArPosterModel(listOf(""))
        val arPostEntity = postEntityEmitter.createEntity(
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
        //controlMode.sceneMode = ControlMode.SceneMode.FACE_TO_CAMERA
        currentSessionNodeManager.addPostEntityToQueue(arPostEntity)
    }

    override fun onStopUseCase() {
        controlModeListener?.onComplete()
    }

    override fun startUseCase(): BehaviorSubject<ControlMode> {
        //   renderableFactory.loadFootPrint()
        controlModeListener = BehaviorSubject.create()
        return controlModeListener!!
    }

    private var controlModeListener: BehaviorSubject<ControlMode>? = null

    var controlMode: ControlMode = ControlMode(ControlMode.SceneMode.IDLE)

    override fun moveModelOnHorizontalPlane() {
        controlMode.sceneMode = ControlMode.SceneMode.DROP
    }

    override fun stopUpdate() {
        unsubscribeFromArCoreFrames()
    }

    override fun startUpdate() {
        subscribeToArCoreFrames()
    }

    val camera: Camera = arCoreScene.scene.camera

    override fun getInfo(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var entityPost: ArPostEntity? = null

    private fun resetCurrentNodeRef() {
        currentSessionNodeManager.transformableNodeCurrent = null
    }

    private fun currentNodeRemove() =
        entityPost?.let { currentSessionNodeManager.removeNode(it.postId()) }

    private fun currentNode() = currentSessionNodeManager.transformableNodeCurrent

    override fun onUpdate(frameTime: FrameTime) {
        currentNode()?.let { currentNode ->
            var planeHit = false
            if (!isAnimating()) {
                arCoreFrameEmitter.lastFrame()?.let { lastFrame ->
                    // if (lastFrame.camera.trackingState == TrackingState.TRACKING)
                    with(screenCenter(context)) {
                        val results = lastFrame.hitTest(x.toFloat(), y.toFloat())
                        for (res in results) {
                            if (res.trackable is Plane) {
                                schedulersProvider.ui().scheduleDirect {
                                    movePostNodeTo(currentNode, res.hitPose)
                                    planeHit = true
                                }
                                break
                            }
                        }
                    }

                    if (!planeHit) {
                        if (controlMode.sceneMode != ControlMode.SceneMode.FACE_TO_CAMERA) {
                            controlMode.sceneMode = ControlMode.SceneMode.FACE_TO_CAMERA
                            controlModeListener?.onNext(controlMode)
                        }
                        faceToCamera(arCoreScene.scene.camera, currentNode, null)
                        //schedulersProvider.ui().scheduleDirect {}
                    }
                }
            }
        }
    }

    override fun removeModelFromScene() {
        Timber.d("removeModelFromScene")
        currentNodeRemove()
        entityPost = null
        resetCurrentNodeRef()
        controlMode.sceneMode = ControlMode.SceneMode.IDLE
    }


    override fun lockModel(isLocked: Boolean) {
        Timber.d("lockModel $isLocked")
        if (isLocked) {
            controlMode.sceneMode = ControlMode.SceneMode.LOCKED
        } else {
            controlMode.sceneMode = ControlMode.SceneMode.FACE_TO_CAMERA
        }
    }

    override fun confirmPostCreation(): Single<Boolean> {
        Timber.d("confirmPostCreation")
        return Single.create<Boolean> {
            try {
                controlMode.sceneMode = ControlMode.SceneMode.SYNCH
                val currentNode = currentNode()!!
                postDataRetriever.retrieveArModel3dPostData(currentNode, object :
                    PostDataRetriever.Ar3dModelPostDataListener {
                    override fun onDataReady(postData: Ar3dModelPostData) {
                        Timber.d("onDataReady $postData")
                        entityPost!!.apply {
                            postTransform = currentNode.getMatrix4()
                            location = postData.postLocation
                            anchorId = postData.anchorId
                        }
                        val res = createPostFabric.getPostCreateFabric()
                            .createPostRequest(CreatePostRequestEntity(null, entityPost!!))
                        schedulersProvider.ui()
                            .scheduleDirect { sceneFormNodeProvider.transformationSystem.deselectNode() }
                        resetCurrentNodeRef()
                        it.onSuccess(res)
                        controlMode.sceneMode = ControlMode.SceneMode.IDLE
                    }
                })
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

}