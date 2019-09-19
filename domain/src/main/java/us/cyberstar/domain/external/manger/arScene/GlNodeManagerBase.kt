package us.cyberstar.domain.external.manger.arScene

import com.google.ar.core.Anchor
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.internal.manger.arScene.base.NodeManagerBaseImpl
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The class listens to new post entities
 * and creates and put new banner posts into the ar scene.

 * Banner can be created only if camera.trackingState = TrackingState.TRACKING,
 * so we check it here and emit entity when the state is appropriate
 */
abstract class GlNodeManagerBase constructor(
    arCoreFrameEmitterBase: ArCoreFrameEmitter,
    private val snackBarProvider: SnackBarProvider,
    override val schedulersProvider: SchedulersProvider
) : NodeManagerBaseImpl(
    arCoreFrameEmitterBase,
    snackBarProvider,
    schedulersProvider
) {

    abstract var anchors: CopyOnWriteArrayList<ColoredAnchor>
    // Anchors created from taps used for object placing with a given color.
    class ColoredAnchor(val anchor: Anchor, val color: FloatArray)
}



