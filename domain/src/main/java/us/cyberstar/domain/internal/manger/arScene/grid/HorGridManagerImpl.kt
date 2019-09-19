package us.cyberstar.domain.internal.manger.arScene.grid

import com.google.ar.sceneform.FrameTime
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.AnimationEndListener
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.model.CellNode
import us.cyberstar.domain.internal.model.PostNode
import us.cyberstar.domain.movePostNodeTo
import us.cyberstar.domain.runQuickPostAnimation
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class HorGridManagerImpl @Inject constructor(
    arCoreFrameEmitter: ArCoreFrameEmitter,
    compositeDisposable: CompositeDisposable,
    arCoreScene: ArCoreScene,
    arCoreSession: ArCoreSession,
    rootNodeProvider: RootNodeProvider,
    renderableFactory: RenderableFactory,
    schedulersProvider: SchedulersProvider,
    planesEmitter: PlanesEmitter
) : ArGridManagerImpl(
    arCoreFrameEmitter,
    compositeDisposable,
    arCoreScene,
    arCoreSession,
    rootNodeProvider,
    renderableFactory,
    schedulersProvider,
    planesEmitter
) {

    val scene = arCoreScene.scene
    val camera = arCoreScene.scene.camera
    private val quickPostsWaitingForSurfaceQueue = LinkedBlockingQueue<PostNode>(100)

    override fun createGrid() {
        super.createGrid()
        Timber.d("HorGridManager createGrid()")
        rootNodeProvider.nodeIsVisible = true // set the current root node visible
    }

    fun onQuickPostAdded(postNode: PostNode) {
        Timber.d("onQuickPostAdded")
        schedulersProvider.ui().scheduleDirect {
            getCellForAQuickPost()?.let { cell->
                schedulersProvider.ui().scheduleDirect {
                    movePostNodeTo(postNode, cell)
                }
            } ?: {
                runQuickPostAnimation(camera, postNode, object : AnimationEndListener {
                    override fun onAnimationEnd() {
                        Timber.d("onAnimationEnd")
                        quickPostsWaitingForSurfaceQueue.put(postNode)
                    }
                })
            }()
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        synchronized(this) {
            if (quickPostsWaitingForSurfaceQueue.isNotEmpty()) {
                getCellForAQuickPost()?.let { cell ->
                    quickPostsWaitingForSurfaceQueue.poll()?.let {
                        schedulersProvider.ui().scheduleDirect {
                            movePostNodeTo(it, cell)
                        }
                    }
                }
            }
        }
    }


    private fun getCellForAQuickPost(): CellNode? {
        if (cellNodeList.isNotEmpty()) {
            Timber.d("get a position for a Quick Post in cell list ${cellNodeList.size}")
            for (cellNode in cellNodeList) {
                if (cellNode.isEmpty) {
                    cellNode.isEmpty = false
                    return cellNode
                }
            }
        }
        return null
    }


    override fun isVerticalPlane() = false

    override fun getNewInstance(rootNodeProvider: RootNodeProvider) = HorGridManagerImpl(
        arCoreFrameEmitter, compositeDisposable, arCoreScene,
        arCoreSession,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        planesEmitter
    )
}