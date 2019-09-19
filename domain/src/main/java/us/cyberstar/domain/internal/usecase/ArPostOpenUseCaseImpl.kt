package us.cyberstar.domain.internal.usecase

import android.content.Context
import android.view.MotionEvent
import com.google.ar.core.Frame
import com.google.ar.sceneform.FrameTime
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener
import us.cyberstar.domain.external.usecase.ArPostOpenUseCase
import us.cyberstar.domain.internal.mapper.mapToPostModel
import us.cyberstar.domain.internal.model.PostNode
import javax.inject.Inject

class ArPostOpenUseCaseImpl @Inject constructor(
    val arCoreScene: ArCoreScene,
    val tapHelper: TapHelper,
    val compositeDisposable: CompositeDisposable,
    val arCoreFrameEmitter: ArCoreFrameEmitter,
    val context: Context
) : OnUpdateListener, ArPostOpenUseCase {

    var postNodeTapListener: ArPostOpenUseCase.PostNodeTapListener? = null

    override fun stopUseCase() {
        postNodeTapListener = null
        unsubscribeFromArCoreFrames()
    }

    override fun startUseCase(postNodeTapListener: ArPostOpenUseCase.PostNodeTapListener) {
        this.postNodeTapListener = postNodeTapListener
        subscribeToArCoreFrames()
    }

    override fun unsubscribeFromArCoreFrames() {
        arCoreFrameEmitter.removeUpdateListener(this)
    }

    override fun subscribeToArCoreFrames() {
        arCoreFrameEmitter.addUpdateListener(this)
    }


    override fun onUpdate(frameTime: FrameTime) {
        // Handle only one tap per lastFrame, as taps are usually low frequency compared to lastFrame rate.
        arCoreFrameEmitter.lastFrame()?.let {
            val tap = tapHelper.poll()
            if (tap != null) {
                handleTap(it, tap)
            }
        }
    }

    private fun handleTap(frame: Frame, tap: MotionEvent) {
        /*val res = arCoreScene.scene.hitTest(tap)
        val trackable = res.node
        if (trackable is PostNode) {
            val model =
                trackable.arPostModel?.let { it } ?: mapToPostModel(trackable.arPostEntity!!)
            postNodeTapListener?.onPostNodeTapped(model)
        }*/
    }
}
