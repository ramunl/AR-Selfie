package us.cyberstar.domain.internal.manger.arScene.grid

import com.google.ar.core.Plane
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.model.PostNode
import javax.inject.Inject

class VertGridManagerImpl @Inject constructor(
    arCoreFrameEmitter: ArCoreFrameEmitter,
    compositeDisposable: CompositeDisposable,
    arCoreScene: ArCoreScene,
    arCoreSession: ArCoreSession,
    rootNodeProvider: RootNodeProvider,
    renderableFactory: RenderableFactory,
    schedulersProvider: SchedulersProvider,
    planesEmitter: PlanesEmitter
) : ArGridManagerImpl(
    arCoreFrameEmitter,
    compositeDisposable,
    arCoreScene,
    arCoreSession,
    rootNodeProvider,
    renderableFactory,
    schedulersProvider,
    planesEmitter
) {

    override fun isVerticalPlane() = true

    override fun getNewInstance(rootNodeProvider: RootNodeProvider) = VertGridManagerImpl(
        arCoreFrameEmitter,
        compositeDisposable,
        arCoreScene,
        arCoreSession,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        planesEmitter
    )
}