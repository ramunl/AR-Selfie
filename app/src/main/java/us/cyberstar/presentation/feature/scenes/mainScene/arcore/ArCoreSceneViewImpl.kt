package us.cyberstar.presentation.feature.scenes.mainScene.arcore

import android.app.Activity
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.PlaneRenderer
import com.google.ar.sceneform.rendering.Texture
import timber.log.Timber
import us.cyberstar.arcyber.BuildConfig
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.internal.utils.getColorPointsFromFrame
import us.cyberstar.domain.internal.utils.scene
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import javax.inject.Inject

class ArCoreSceneViewImpl @Inject constructor(
    activity: Activity,
    private val arCoreSession: ArCoreSession
) : ArCoreSceneView {
    override var arSceneView: ArSceneView = ArSceneView(activity).apply {
        arCoreSession.setupFlavor(BuildConfig.FLAVOR)
        Timber.d("ArSceneView session setup.")
        setupSession(arCoreSession.session)
        setPlaneTexture(this, "trigrid.png")
        test(this)
    }

    private fun test(arSceneView: ArSceneView) {
        scene = arSceneView.scene.also {
            Timber.e("setup scene $it to use it in telemetry dataFrame emitter")
        }
    }

    private fun setPlaneTexture(arSceneView: ArSceneView, texturePath: String) {
        val sampler = Texture.Sampler.builder()
            .setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
            .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
            .setWrapModeR(Texture.Sampler.WrapMode.REPEAT)
            .setWrapModeS(Texture.Sampler.WrapMode.REPEAT)
            .setWrapModeT(Texture.Sampler.WrapMode.REPEAT)
            .build()

        Texture.builder().setSource { arSceneView.context!!.assets.open(texturePath) }
            .setSampler(sampler)
            .build().thenAccept { texture ->
                arSceneView.planeRenderer.material
                    .thenAccept { material ->
                        material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture)
                        material.setFloat(PlaneRenderer.MATERIAL_UV_SCALE, 10f)
                    }
            }.exceptionally { ex ->
                Timber.e("Failed to read an asset file $ex")
                null
            }
    }
}
