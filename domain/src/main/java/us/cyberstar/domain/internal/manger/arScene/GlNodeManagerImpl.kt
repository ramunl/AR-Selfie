package us.cyberstar.domain.internal.manger.arScene

import android.content.Context
import android.graphics.Bitmap
import com.cyber.ux.TransformableNode
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.PostCompositeIdEntity
import us.cyberstar.data.entity.social.ArPostEntity
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.manger.arScene.GlNodeManagerBase
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.gl.common.rendering.ObjectRenderer
import us.cyberstar.domain.internal.model.PostNode
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

/**
 * The class listens to new post entities
 * and creates and put new banner posts into the ar scene.

 * Banner can be created only if camera.trackingState = TrackingState.TRACKING,
 * so we check it here and emit entity when the state is appropriate
 */
class GlNodeManagerImpl @Inject constructor(
    private val context: Context,
    private val arCoreFrameEmitterBase: ArCoreFrameEmitter,
    private val arCoreSession: ArCoreSession,
    private val snackBarProvider: SnackBarProvider,
    override val schedulersProvider: us.cyberstar.common.external.SchedulersProvider
) : GlNodeManagerBase(
    arCoreFrameEmitterBase,
    snackBarProvider,
    schedulersProvider
) {

    override var transformableNodeCurrent: TransformableNode? = null

    override fun isEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




    override fun getPostNodesInfo(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPostNodesCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    override fun removeNode(id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNodeById(id: Long): PostNode? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }





    override fun create() {

    }

    override fun updatePostsWithNewList(arPostEntityArrayNew: Collection<ArPostEntity>) {
        //TODO implement this logic if needed
    }

    override fun getNewInstance(): NodeManager = GlNodeManagerImpl(
        context,
        arCoreFrameEmitterBase,
        arCoreSession,
        snackBarProvider,
        schedulersProvider
    )


    // Temporary matrix allocated here to reduce number of allocations for each lastFrame.
    private val anchorMatrix = FloatArray(16)
    private var arSession: Session? = arCoreSession.session
    override var anchors: CopyOnWriteArrayList<ColoredAnchor> = CopyOnWriteArrayList()

    private val virtualObject: ObjectRenderer by lazy {
        ObjectRenderer().apply {
            createOnGlThread(/*context=*/context, "models/andy.obj", "models/andy.png")
            setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f)
        }
    }

    private val virtualObjectShadow: ObjectRenderer by lazy {
        ObjectRenderer().apply {
            createOnGlThread(context, "models/andy_shadow.obj", "models/andy_shadow.png")
            setBlendMode(ObjectRenderer.BlendMode.Shadow)
            setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f)
        }
    }

    override fun onDrawFrame(frame: Frame) {
        // Visualize anchors created by touch.
        val scaleFactor = 1.0f

        // Get projection matrix.
        val projmtx = FloatArray(16)
        frame.camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

        // Get camera matrix and draw.
        val viewmtx = FloatArray(16)
        frame.camera.getViewMatrix(viewmtx, 0)

        // Compute lighting from average intensity of the image.
        // The first three components are color scaling factors.
        // The last one is the average pixel intensity in gamma space.
        val colorCorrectionRgba = FloatArray(4)
        frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)


        for (coloredAnchor in anchors) {
            if (coloredAnchor.anchor.trackingState != TrackingState.TRACKING) {
                continue
            }
            // Get the current pose of an Anchor in world space. The Anchor pose is updated
            // during calls to session.update() as ARCore refines its estimate of the world.
            coloredAnchor.anchor.pose.toMatrix(anchorMatrix, 0)

            // Update and draw the model and its shadow.
            virtualObject.updateModelMatrix(anchorMatrix, scaleFactor)
            virtualObjectShadow.updateModelMatrix(anchorMatrix, scaleFactor)
            virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color)
            virtualObjectShadow.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color)
        }

    }

    override fun destroy() {
        anchors.clear()
    }

    override fun createArPostAndAddToScene(arPostEntity: ArPostEntity) {
        Timber.d("createPostNode $arPostEntity")
        /*val anchor = arSession!!.createAnchor(arPostEntity.pose())
        val objColor = floatArrayOf(139.0f, 195.0f, 74.0f, 255.0f)
        anchors.add(ColoredAnchor(anchor, objColor))*/
    }
}



