package us.cyberstar.domain.external.usecase

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import us.cyberstar.domain.external.ArCoreFrameSubscriber
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.internal.usecase.base.Ar3dModelPostData
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

abstract class CreateSelfieUseCase(arCoreFrameEmitter: ArCoreFrameEmitter) :
    ArCoreFrameSubscriber(arCoreFrameEmitter) {
    abstract fun getInfo(): String
    abstract fun confirmPostCreation(): Single<Boolean>
    abstract fun removeModelFromScene()
    abstract fun stopUpdate()
    abstract fun startUpdate()
    abstract fun moveModelOnHorizontalPlane()
    abstract fun lockModel(isLocked: Boolean)

    class ControlMode(var sceneMode: SceneMode) {
        var postsNum = AtomicInteger(0)
        var horizontalPlaneNum = AtomicInteger(0)
        enum class SceneMode {
            FACE_TO_CAMERA,
            LOCKED,
            DROP,
            IDLE,
            SYNCH
        }
    }

    abstract fun startUseCase(): BehaviorSubject<ControlMode>
    abstract fun onStopUseCase()
    abstract fun addNewModelToScene(arPostModel: ArPostModel)
}