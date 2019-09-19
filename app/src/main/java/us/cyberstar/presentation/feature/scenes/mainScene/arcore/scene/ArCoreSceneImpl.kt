package us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene

import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.Scene
import us.cyberstar.domain.external.arcore.ArCoreScene
import javax.inject.Inject

class ArCoreSceneImpl @Inject constructor(
    activity: AppCompatActivity,
    arCoreSceneView: ArCoreSceneView
) : ArCoreScene {
    override var scene: Scene = arCoreSceneView.arSceneView.scene
}