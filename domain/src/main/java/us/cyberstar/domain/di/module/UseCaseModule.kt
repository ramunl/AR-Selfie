package us.cyberstar.domain.di.module


import android.content.Context
import com.cyber.ux.SceneFormNodeProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.external.usecase.CreateTargetPostUseCase
import us.cyberstar.domain.external.usecase.ArPostOpenUseCase
import us.cyberstar.domain.external.usecase.CreateAr3dPostUseCase
import us.cyberstar.domain.external.usecase.CreateQuickPostUseCase
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.domain.internal.manger.arScene.grid.VertGridManagerImpl
import us.cyberstar.domain.internal.usecase.ArPostOpenUseCaseImpl
import us.cyberstar.domain.internal.usecase.CreateAr3dPostUseCaseImpl
import us.cyberstar.domain.internal.usecase.CreateQuickPostUseCaseImpl
import us.cyberstar.domain.internal.usecase.CreateTargetPostUseCaseImpl
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever

@Module
class UseCaseModule {


    @Provides
    @PerActivity
    fun provideArPostOpenUseCase(
        arCoreScene: ArCoreScene,
        tapHelper: TapHelper,
        compositeDisposable: CompositeDisposable,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        context: Context
    ): ArPostOpenUseCase = ArPostOpenUseCaseImpl(
        arCoreScene,
        tapHelper,
        compositeDisposable,
        arCoreFrameEmitter,
        context
    )

    @Provides
    @PerActivity
    fun provideCreateArPostUseCase(
        mediaLoader: MediaLoader,
        currentSessionNodeManager: NodeManager,
        postDataRetriever: PostDataRetriever,
        compositeDisposable: CompositeDisposable,
        arCoreFrameEmitterBase: ArCoreFrameEmitter,
        arCoreScene: ArCoreScene,
        s3Cache: S3Cache,
        postEntityEmitter: PostEntityEmitter,
        snackBarProvider: SnackBarProvider,
        rootNodeProvider: RootNodeProvider,
        renderableFactory: RenderableFactory,
        schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
        arGridManager: VertGridManagerImpl,
        assetForDetectionEmitter: AssetForDetectionEmitter,
        createPostFabric: CreatePostFabric
    ): CreateTargetPostUseCase = CreateTargetPostUseCaseImpl(
        mediaLoader,
        currentSessionNodeManager,
        postDataRetriever,
        arCoreFrameEmitterBase,
        arCoreScene,
        s3Cache,
        postEntityEmitter,
        snackBarProvider,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        arGridManager,
        assetForDetectionEmitter,
        createPostFabric,
        compositeDisposable
    )

    @Provides
    @PerActivity
    fun provideCreateQuickArPostUseCase(
        horGridManagerImpl: HorGridManagerImpl,
        mediaLoader: MediaLoader,
        currentSessionNodeManager: NodeManager,
        postDataRetriever: PostDataRetriever,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        arCoreScene: ArCoreScene,
        s3Cache: S3Cache,
        postEntityEmitter: PostEntityEmitter,
        snackBarProvider: SnackBarProvider,
        rootNodeProvider: RootNodeProvider,
        renderableFactory: RenderableFactory,
        schedulersProvider: SchedulersProvider,
        createPostFabric: CreatePostFabric
    ): CreateQuickPostUseCase = CreateQuickPostUseCaseImpl(
        horGridManagerImpl,
        mediaLoader,
        currentSessionNodeManager,
        postDataRetriever,
        arCoreFrameEmitter,
        arCoreScene,
        s3Cache,
        postEntityEmitter,
        snackBarProvider,
        rootNodeProvider,
        renderableFactory,
        schedulersProvider,
        createPostFabric
    )

    @Provides
    @PerActivity
    fun provideCreateAr3dPostUseCase(
        sceneFormNodeProvider: SceneFormNodeProvider,
        renderableFactory: RenderableFactory,
        schedulersProvider: SchedulersProvider,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        currentSessionNodeManager: NodeManager,
        createPostFabric: CreatePostFabric,
        postEntityEmitter: PostEntityEmitter,
        postDataRetriever: PostDataRetriever,
        arCoreSession: ArCoreSession,
        arCoreScene: ArCoreScene,
        cloudAnchorManager: CloudAnchorManager
    ): CreateAr3dPostUseCase =
        CreateAr3dPostUseCaseImpl(
            sceneFormNodeProvider,
            renderableFactory,
            schedulersProvider,
            arCoreFrameEmitter,
            arCoreSession,
            currentSessionNodeManager,
            createPostFabric,
            postEntityEmitter,
            postDataRetriever,
            arCoreScene,
            cloudAnchorManager
        )

}
