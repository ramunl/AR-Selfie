package us.cyberstar.domain.external.usecase

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import us.cyberstar.domain.external.ArCoreFrameSubscriber
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.model.ArPostModel

abstract class CreateQuickPostUseCase(arCoreFrameEmitter: ArCoreFrameEmitter) : ArCoreFrameSubscriber(arCoreFrameEmitter) {
    abstract fun continueUseCase()
    abstract fun prepareAndCreateQuickPost(postModel: ArPostModel): Single<Boolean>
    abstract fun closeUseCase(destroyNode: Boolean = false)
    abstract fun getInfo(): String
}