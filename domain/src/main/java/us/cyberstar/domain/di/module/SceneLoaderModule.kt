package us.cyberstar.domain.di.module


import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.*
import us.cyberstar.data.external.grpc.GrpcArService
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.data.external.s3.S3TransferUtilityProvider
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.*
import us.cyberstar.domain.external.loader.grpc.entity.ArWorldLoaderGrpc
import us.cyberstar.domain.external.loader.grpc.entity.CreatePostRemote
import us.cyberstar.domain.external.loader.grpc.entity.SaveVideoRemote
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.loader.image.PostRequestImageLoader
import us.cyberstar.domain.external.loader.video.SaveVideoRequestLoader
import us.cyberstar.domain.external.manger.AugImgDbManger
import us.cyberstar.domain.external.manger.arScene.MultiNodeManager
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.internal.ArSceneInitializerImpl
import us.cyberstar.domain.internal.ArWorldLoaderSettings
import us.cyberstar.domain.internal.loader.ArWorldLoaderFabricImpl
import us.cyberstar.domain.internal.loader.local.entity.ArWorldLoaderFromFileImpl
import us.cyberstar.domain.internal.loader.grpc.entity.ArWorldLoaderRemoteImpl
import us.cyberstar.domain.internal.loader.s3.MediaLoaderImpl
import us.cyberstar.domain.internal.loader.s3.image.PostRequestImageLoaderImpl
import us.cyberstar.domain.internal.loader.s3.video.SaveVideoRequestLoaderImpl
import us.cyberstar.domain.internal.manger.arScene.MultiNodeManagerImpl
import us.cyberstar.domain.external.loader.local.ArWorldLoaderFromFile
import us.cyberstar.domain.external.loader.local.CreatePostLocal
import us.cyberstar.domain.external.loader.local.SaveVideoLocal
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.loader.CreatePostFabricImpl
import us.cyberstar.domain.internal.loader.SaveVideoFabricImpl
import us.cyberstar.domain.internal.loader.grpc.entity.CreatePostRemoteImpl
import us.cyberstar.domain.internal.loader.grpc.entity.SaveVideoRemoteImpl
import us.cyberstar.domain.internal.loader.local.entity.CreatePostLocalImpl
import us.cyberstar.domain.internal.loader.local.entity.SaveVideoLocalImpl
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings

@Module
class SceneLoaderModule {


    @Provides
    @PerActivity
    fun provideArWorldLoaderSettings(): ArWorldLoaderSettings = ArWorldLoaderSettings()


    //TODO we need to make fabric from these providers
    @Provides
    @PerActivity
    fun provideArCoreGrpcSceneWorldLoader(
        gpsCoordinatesListener: GPSCoordinatesListener,
        snackBarProvider: SnackBarProvider,
        grpcArService: GrpcArService,
        context: Context,
        schedulersProvider: SchedulersProvider
    ): ArWorldLoaderGrpc =
        ArWorldLoaderRemoteImpl(
            gpsCoordinatesListener,
            snackBarProvider,
            grpcArService,
            context,
            schedulersProvider
        )

    @Provides
    @PerActivity
    fun provideArSceneWorldLoader(
        context: Context,
        snackBarProvider: SnackBarProvider,
        arEntityCache: ArEntityCache,
        schedulersProvider: SchedulersProvider
    ): ArWorldLoaderFromFile =
        ArWorldLoaderFromFileImpl(
            context,
            snackBarProvider,
            arEntityCache,
            schedulersProvider
        )


    @Provides
    @PerActivity
    fun provideSaveVideoFabric(
        arWorldLoaderSettings: ArWorldLoaderSettings,
        remote: SaveVideoRemote,
        local: SaveVideoLocal
    ): SaveVideoFabric =
        SaveVideoFabricImpl(
            arWorldLoaderSettings,
            local,
            remote
        )


    @Provides
    @PerActivity
    fun provideSaveVideoRemote(
        saveVideoRequestLoader: SaveVideoRequestLoader,
        snackBarProvider: SnackBarProvider,
        grpcTelemetryService: GrpcTelemetryService
    ): SaveVideoRemote = SaveVideoRemoteImpl(
        saveVideoRequestLoader,
        snackBarProvider,
        grpcTelemetryService
    )

    @Provides
    @PerActivity
    fun provideSaveVideoLocal(
        saveVideoRequestLoader: SaveVideoRequestLoader,
        snackBarProvider: SnackBarProvider
    ): SaveVideoLocal = SaveVideoLocalImpl()

    @Provides
    @PerActivity
    fun provideCreatePostRemote(
        sessionIdProvider: SessionIdProvider,
        snackBarProvider: SnackBarProvider,
        postRequestImageLoader: PostRequestImageLoader,
        grpcArService: GrpcArService
    ): CreatePostRemote = CreatePostRemoteImpl(
        sessionIdProvider,
        snackBarProvider,
        postRequestImageLoader,
        grpcArService
    )


    @Provides
    @PerActivity
    fun provideCreatePostLocal(
        sessionIdProvider: SessionIdProvider,
        snackBarProvider: SnackBarProvider,
        arEntityCache: ArEntityCache
    ): CreatePostLocal = CreatePostLocalImpl(
        sessionIdProvider,
        snackBarProvider,
        arEntityCache
    )

    @Provides
    @PerActivity
    fun provideCreatePostFabric(
        arWorldLoaderSettings: ArWorldLoaderSettings,
        remote: CreatePostRemote,
        local: CreatePostLocal
    ): CreatePostFabric =
        CreatePostFabricImpl(
            arWorldLoaderSettings,
            remote,
            local
        )

    @Provides
    @PerActivity
    fun provideArWorldLoaderFabric(
        arWorldLoaderSettings: ArWorldLoaderSettings,
        arWorldLoaderFromFile: ArWorldLoaderFromFile,
        arWorldLoaderGrpc: ArWorldLoaderGrpc
    ): ArWorldLoaderFabric =
        ArWorldLoaderFabricImpl(
            arWorldLoaderSettings,
            arWorldLoaderFromFile,
            arWorldLoaderGrpc
        )

    @Provides
    @PerActivity
    fun provideArCoreSceneInitializer(
        rootNodeProvider: RootNodeProvider,
        horGridManager: HorGridManagerImpl,
        framerRecorderSettings: FrameRecorderSettings,
        schedulersProvider: SchedulersProvider,
        videoRecorderWrapper: VideoRecorderWrapper,
        headFinalEntityEmitter: HeadFinalEntityEmitter,
        sessionIdProvider: SessionIdProvider,
        hardwareEntityEmitter: HardwareEntityEmitter,
        deviceInfo: DeviceInfo,
        arSessionStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider,
        augImgDbManger: AugImgDbManger,
        multiNodeManager: MultiNodeManager,
        sensorFrameEntityEmitter: SensorFrameEntityEmitter,
        planesEmitter: PlanesEmitter,
        augmentedImageEmitter: AugmentedImageEmitter,
        dataFrameEntityEmitter: DataFrameEntityEmitter,
        arWorldLoaderSettings: ArWorldLoaderSettings,
        compositeDisposable: CompositeDisposable,
        arWorldLoaderFabric: ArWorldLoaderFabric,
        telemetryRecorderFabric: TelemetryRecorderFabric,
        gpsCoordinatesListener: GPSCoordinatesListener
    ): ArSceneInitializer = ArSceneInitializerImpl(
        rootNodeProvider,
        horGridManager,
        framerRecorderSettings,
        schedulersProvider,
        videoRecorderWrapper,
        headFinalEntityEmitter,
        sessionIdProvider,
        hardwareEntityEmitter,
        deviceInfo,
        arSessionStartTimeProvider,
        augImgDbManger,
        multiNodeManager,
        sensorFrameEntityEmitter,
        planesEmitter,
        augmentedImageEmitter,
        dataFrameEntityEmitter,
        arWorldLoaderSettings,
        compositeDisposable,
        arWorldLoaderFabric,
        telemetryRecorderFabric,
        gpsCoordinatesListener
    )


    @Provides
    @PerActivity
    fun provideSaveVideoRequestLoader(
        mediaLoader: MediaLoader
    ): SaveVideoRequestLoader = SaveVideoRequestLoaderImpl(
        mediaLoader
    )

    @Provides
    @PerActivity
    fun providePostRequestImageLoader(
        mediaLoader: MediaLoader
    ): PostRequestImageLoader = PostRequestImageLoaderImpl(
        mediaLoader
    )

    @Provides
    @PerActivity
    fun provideImageDownloader(
        snackBarProvider: SnackBarProvider,
        s3TransferUtilityProvider: S3TransferUtilityProvider,
        s3Cache: S3Cache
    ): MediaLoader = MediaLoaderImpl(
        snackBarProvider,
        s3TransferUtilityProvider,
        s3Cache
    )

    @Provides
    @PerActivity
    fun provideMultiNodeManager(
        nodeManager: NodeManager,
        postEntityEmitter: PostEntityEmitter,
        arWorldLoaderFabric: ArWorldLoaderFabric,
        compositeDisposable: CompositeDisposable,
        schedulersProvider: SchedulersProvider
    ): MultiNodeManager = MultiNodeManagerImpl(
        nodeManager,
        postEntityEmitter,
        arWorldLoaderFabric,
        compositeDisposable,
        schedulersProvider
    )

}
