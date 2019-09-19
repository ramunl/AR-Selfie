package us.cyberstar.domain.di.module

import android.content.Context
import com.cyber.ux.SceneFormNodeProvider
import com.cyber.ux.SceneFormNodeProviderIml
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import us.cyberstar.domain.external.cloudAnchor.FirebaseManager
import us.cyberstar.domain.external.dictionary.AssetForDetectionHashMapWrap
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.AugmentedImageEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.arScene.ArGridManager
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.manger.arScene.RootNodeManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.dictionary.AssetForDetectionHashMapWrapImpl
import us.cyberstar.domain.internal.manger.arScene.NodeManagerImpl
import us.cyberstar.domain.internal.manger.arScene.RootNodeManagerImpl
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.domain.internal.manger.arScene.grid.VertGridManagerImpl
import us.cyberstar.domain.internal.provider.RootNodeProviderImpl

@Module
class SceneFormManagerModule {

    @Provides
    @PerActivity
    fun provideAssetForDetectionHashMapWrapImpl(
        compositeDisposable: CompositeDisposable,
        context: Context,
        arWorldLoaderFabric: ArWorldLoaderFabric,
        assetForDetectionEmitter: AssetForDetectionEmitter,
        schedulersProvider: SchedulersProvider
    ): AssetForDetectionHashMapWrap = AssetForDetectionHashMapWrapImpl(
        compositeDisposable,
        arWorldLoaderFabric,
        assetForDetectionEmitter,
        schedulersProvider
    )


    @Provides
    @PerActivity
    fun provideNodeManager(
         arCoreScene: ArCoreScene,
         cloudAnchorManager: CloudAnchorManager,
        //firebaseManager: FirebaseManager,
        sceneFormNodeProvider: com.cyber.ux.SceneFormNodeProvider,
        horGridManager: HorGridManagerImpl,
        rootNodeManager: RootNodeManager,
        rootNodeProvider: RootNodeProvider,
        arCoreFrameEmitterBase: ArCoreFrameEmitter,
        mediaLoader: MediaLoader,
        snackBarProvider: SnackBarProvider,
        renderableFactoryImpl: RenderableFactory,
        schedulersProvider: SchedulersProvider
    ): NodeManager = NodeManagerImpl(
        arCoreScene,
        cloudAnchorManager,
        //firebaseManager,
        sceneFormNodeProvider,
        horGridManager,
        rootNodeManager,
        arCoreFrameEmitterBase,
        rootNodeProvider,
        mediaLoader,
        snackBarProvider,
        renderableFactoryImpl,
        schedulersProvider
    )

    @Provides
    @PerActivity
    fun provideRootNodeManager(
        compositeDisposable: CompositeDisposable,
        rootNodeProvider: RootNodeProvider,
        assetForDetectionHashMapWrap: AssetForDetectionHashMapWrap,
        snackBarProvider: SnackBarProvider,
        augmentedImageEmitter: AugmentedImageEmitter,
        schedulersProvider: SchedulersProvider
    ): RootNodeManager = RootNodeManagerImpl(
        compositeDisposable,
        rootNodeProvider,
        assetForDetectionHashMapWrap,
        snackBarProvider,
        augmentedImageEmitter,
        schedulersProvider
    )

    @Provides
    @PerActivity
    fun provideVertGridManager(
        arCoreFrameEmitter: ArCoreFrameEmitter,
        arCoreScene: ArCoreScene,
        compositeDisposable: CompositeDisposable,
        arCoreSession: ArCoreSession,
        rootNodeProvider: RootNodeProvider,
        renderableFactory: RenderableFactory,
        schedulersProvider: SchedulersProvider,
        planesEmitter: PlanesEmitter
    ): ArGridManager = VertGridManagerImpl(
        arCoreFrameEmitter,
        compositeDisposable,
        arCoreScene,
        arCoreSession,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        planesEmitter
    )

    @Provides
    @PerActivity
    fun provideHorGridManager(
        arCoreFrameEmitter: ArCoreFrameEmitter,
        arCoreScene: ArCoreScene,
        compositeDisposable: CompositeDisposable,
        arCoreSession: ArCoreSession,
        rootNodeProvider: RootNodeProvider,
        renderableFactory: RenderableFactory,
        schedulersProvider: SchedulersProvider,
        planesEmitter: PlanesEmitter
    ): ArGridManager = HorGridManagerImpl(
        arCoreFrameEmitter,
        compositeDisposable,
        arCoreScene,
        arCoreSession,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        planesEmitter
    )

    @Provides
    @PerActivity
    fun provideRootNodeProvider(
        schedulersProvider: SchedulersProvider,
        arCoreSession: ArCoreSession,
        arCoreScene: ArCoreScene): RootNodeProvider =
        RootNodeProviderImpl(schedulersProvider, arCoreScene, arCoreSession)

    @Provides
    @PerActivity
    fun provideSceneFormNodeProvider(context: Context): SceneFormNodeProvider =
        SceneFormNodeProviderIml(context)

}
