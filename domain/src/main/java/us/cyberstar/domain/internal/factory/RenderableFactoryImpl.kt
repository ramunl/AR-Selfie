package us.cyberstar.domain.internal.factory

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.cyber.ux.FootprintSelectionVisualizer
import com.cyber.ux.SceneFormNodeProvider
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import kotlinx.android.synthetic.main.layout_ar_post_frame.view.*
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.R
import us.cyberstar.domain.external.factory.CellColor
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.internal.model.*
import javax.inject.Inject


val FOOT_PRINT = R.raw.sceneform_footprint
val TEST_MODEL_3D = R.raw.tower

class RenderableFactoryImpl @Inject constructor(
    private val sceneFormNodeProvider: SceneFormNodeProvider,
    val context: Context,
    val schedulersProvider: SchedulersProvider
) : RenderableFactory {

    private val cellColorsNum = 3
    private val gridWhite = R.layout.layout_cell_white
    private val gridRed = R.layout.layout_cell_red
    private val gridGreen = R.layout.layout_cell_green

    override fun isRenderableLoaded(): Boolean =
        renderablesMap.size == cellColorsNum //&& sceneFormFootprint != null


    private var renderablesMap: MutableMap<Int, Renderable> = HashMap()


    override fun getCellRenderable(cellColor: CellColor): Renderable {
        return when (cellColor) {
            CellColor.Green -> renderablesMap[gridGreen]!!
            CellColor.White -> renderablesMap[gridWhite]!!
            CellColor.Red -> renderablesMap[gridRed]!!
        }
    }

    private fun setupPostRenderable(
        viewRenderable: ViewRenderable,
        photoBitmap: Bitmap,
        title: String
    ) {
        viewRenderable.apply {
            if (title.isBlank()) {
                view.postEditText.visibility = GONE
            } else {
                view.postEditText.visibility = VISIBLE
                view.postEditText.setText(title)
            }
            photoBitmap.let { view.postSnapshotImageView.setImageBitmap(it) }
        }
    }


    override fun loadPostRenderable(
        title: String,
        bitmap: Bitmap,
        callback: RenderableFactoryCallback
    ) {
        schedulersProvider.ui().scheduleDirect {
            Timber.e("loadPostRenderable on ui thread")
            with(cellSizeSmall) {
                ViewRenderable.builder()
                    .setView(context, R.layout.layout_ar_post_frame)
                    .setSizer { Vector3(x, y, 0.5f) }
                    .build().thenAccept { renderable ->
                        with(renderable) {
                            isShadowCaster = false
                            setupPostRenderable(this, bitmap, title)
                            callback.onRenderableReady(this)
                            Timber.e("post renderable created!")
                        }

                    }
                    .exceptionally {
                        Timber.e("Unable to load view renderable")
                        null
                    }
            }
        }
    }

    override fun loadFootPrint() {
        load3dModel(FOOT_PRINT, object : ModelFactoryCallback {
            override fun onRenderableReady(renderable: ModelRenderable) {
                sceneFormNodeProvider.transformationSystem.selectionVisualizer =
                    FootprintSelectionVisualizer().apply {
                        this.footprintRenderable = renderable
                    }
            }
        })
    }

    override fun loadRenderables() {
        if (!isRenderableLoaded()) {
            schedulersProvider.ui().scheduleDirect {
                Timber.d("init renderables")
                if (renderablesMap[gridWhite] == null) {
                    createCubesFor(gridWhite)
                }
                if (renderablesMap[gridRed] == null) {
                    createCubesFor(gridRed)
                }
                if (renderablesMap[gridGreen] == null) {
                    createCubesFor(gridGreen)
                }
            }
        }
    }

    private fun createCubesFor(@LayoutRes resId: Int) {
        schedulersProvider.ui().scheduleDirect {
            with(cellSizeSmall) {
                ViewRenderable.builder()
                    .setView(context, resId)
                    .setSizer { Vector3(x, y, 2f) }
                    .build().thenAccept { renderable ->
                        with(renderable) {
                            isShadowCaster = false
                            renderablesMap[resId] = this
                            Timber.e("cell renderable created!")
                        }

                    }
                    .exceptionally {
                        Timber.e("Unable to load view renderable")
                        null
                    }
            }
        }
    }

    /*interface MaterialLoaderListener {
        fun onReady(material: Material)
    }

    fun loadMaterial(fileName: String, listener: MaterialLoaderListener) {
        Texture.builder()
            .setSource { context.assets.open(fileName) }
            .build()
            .thenAccept {
                MaterialFactory.makeTransparentWithTexture(context, it)
                    .thenAccept { material ->
                        listener.onReady(material)
                    }
            }
    }

    private fun makeBigCube(material: Material): Renderable = with(cellSizeBig) {
        ShapeFactory.makeCube(
            Vector3(x, y, z),
            Vector3(0f, y / 2, 0f),
            material
        )
    }

    private fun makeSmallCube(material: Material): Renderable = with(cellSizeSmall) {
        ShapeFactory.makeCube(
            Vector3(x, y, z),
            Vector3(0f, y / 2, 0f),
            material
        )
    }*/

    override fun load3dModel(idRes: Int, callback: ModelFactoryCallback?) {
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        renderablesMap[idRes]?.let {
            callback?.onRenderableReady(it as ModelRenderable)
        } ?: schedulersProvider.ui().scheduleDirect {
            ModelRenderable.builder()
                .setSource(context, idRes)
                .build()
                .thenAccept { renderable ->
                    renderablesMap[idRes] = renderable
                    callback?.onRenderableReady(renderable)
                }
                .exceptionally { throwable ->
                    Timber.e("Unable to load andy renderable $throwable")
                    null
                }
        }
    }
}