package us.cyberstar.domain.internal.manger.arScene.base

import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.manger.arScene.CREATE_3D_ONLY
import us.cyberstar.domain.external.manger.arScene.NodeManager
import java.util.concurrent.ArrayBlockingQueue

/**
 * The class listens to new post entities
 * and creates and put new banner posts into the ar scene.

 * Banner can be created only if camera.trackingState = TrackingState.TRACKING,
 * so we check it here and emit entity when the state is appropriate
 */
abstract class NodeManagerBaseImpl constructor(
    arCoreFrameEmitter: ArCoreFrameEmitter,
    private val snackBarProvider: SnackBarProvider,
    open val schedulersProvider: SchedulersProvider
) : NodeManager(arCoreFrameEmitter) {

    private var arPostEntityQueue: ArrayBlockingQueue<ArPostEntity> = ArrayBlockingQueue(100, true)

    fun isTrackingState() =
        arCoreFrameEmitter.lastFrame()?.camera?.trackingState == TrackingState.TRACKING

    override fun onUpdate(frameTime: FrameTime) {
        if (arPostEntityQueue.isNotEmpty()) {
            arPostEntityQueue.poll()?.let {
                schedulersProvider.io().scheduleDirect {
                    createArPostAndAddToScene(it)
                }
            }
        }
    }

    override fun addPostEntityToQueue(arPostEntity: ArPostEntity) {
        if (!postMustBeSkipped(arPostEntity)) {
            Timber.d("addPostEntityToQueue $arPostEntity")
            arPostEntityQueue.add(arPostEntity)
        }
    }

    override fun postMustBeSkipped(arPostEntity: ArPostEntity): Boolean {
        var skip = false
        if (CREATE_3D_ONLY) {
            if (!arPostEntity.isTransformable && !arPostEntity.isAnchorId()) {
                skip = true
            }
        }
        return skip
    }

    abstract fun createArPostAndAddToScene(arPostEntity: ArPostEntity)
}



