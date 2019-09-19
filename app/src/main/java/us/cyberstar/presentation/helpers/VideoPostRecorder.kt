package us.cyberstar.presentation.helpers

import com.google.ar.sceneform.ArSceneView
import io.reactivex.Maybe
import io.reactivex.Single
import us.cyberstar.domain.external.model.ArPostVideoModel

interface VideoPostRecorder {
    fun stopRecord(): Maybe<ArPostVideoModel>
    fun startRecord()
}