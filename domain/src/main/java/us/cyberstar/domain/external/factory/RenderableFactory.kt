package us.cyberstar.domain.external.factory

import android.graphics.Bitmap
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import us.cyberstar.domain.internal.factory.ModelFactoryCallback
import us.cyberstar.domain.internal.factory.RenderableFactoryCallback
import us.cyberstar.domain.internal.factory.RenderableFactoryImpl

interface RenderableFactory {
    fun getCellRenderable(cellColor: CellColor): Renderable
    fun loadRenderables()
    fun loadPostRenderable(
        title: String,
        bitmap: Bitmap,
        callback: RenderableFactoryCallback
    )

    fun isRenderableLoaded(): Boolean
    fun loadFootPrint()
    fun load3dModel(idRes: Int, callback: ModelFactoryCallback?)
}
enum class CellColor {
    Red,
    Green,
    White
}