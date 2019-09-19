package us.cyberstar.domain.di.module


import android.content.Context
import com.opencv.wrapper.OpenCVConverter
import com.opencv.wrapper.OpenCVConverterImpl
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.sensor.DeviceSensorEventListener
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.RoomCodeAndCloudAnchorIdListener
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.TelemetryRecorderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.manger.AugImgDbManger
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.internal.loader.grpc.telemetry.*
import us.cyberstar.domain.internal.loader.grpc.telemetry.DataFrameEntityEmitterImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.AssetForDetectionEmitterImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.AugmentedImageEmitterImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.PlanesEmitterImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.PostEntityEmitterImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.SensorFrameEntityEmitterImpl
import us.cyberstar.domain.internal.manger.arScene.AugImgDbMangerImpl
import us.cyberstar.domain.internal.usecase.base.PostDataRetriever
import us.cyberstar.domain.internal.usecase.base.PostDataRetrieverImpl
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings

@Module
class EntityLoaderModule {


    @Provides
    @PerActivity
    fun provideHardwareEntityEmitter(
        resRepo: ResRepo,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        deviceInfo: DeviceInfo,
        context: Context,
        timeStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
    ): HardwareEntityEmitter =
        HardwareEntityEmitterImpl(
            resRepo,
            arCoreFrameEmitter,
            deviceInfo,
            context,
            timeStartTimeProvider
        )

    @Provides
    @PerActivity
    fun providePostDataRetriever(
        schedulersProvider: SchedulersProvider,
        arCoreSession: ArCoreSession,
        s3Cache: S3Cache,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        gpsCoordinatesListener: GPSCoordinatesListener,
        roomCodeAndCloudAnchorIdListener: RoomCodeAndCloudAnchorIdListener
    ): PostDataRetriever = PostDataRetrieverImpl(
        schedulersProvider,
        arCoreSession,
        s3Cache,
        arCoreFrameEmitter,
        gpsCoordinatesListener,
        roomCodeAndCloudAnchorIdListener
    )

    @Provides
    @PerActivity
    fun provideAssetForDetectionEmitter(
        postDataRetriever: PostDataRetriever
    ): AssetForDetectionEmitter =
        AssetForDetectionEmitterImpl(
            postDataRetriever
        )

    @Provides
    @PerActivity
    fun providePostEntityEmitter(
    ): PostEntityEmitter =
        PostEntityEmitterImpl()

    @Provides
    @PerActivity
    fun provideAugmentImgEmitterImpl(
        compositeDisposable: CompositeDisposable,
        arCoreFrameEmitterBase: ArCoreFrameEmitter,
        snackBarProvider: SnackBarProvider,
        schedulersProvider: SchedulersProvider
    ): AugmentedImageEmitter =
        AugmentedImageEmitterImpl(
            compositeDisposable,
            arCoreFrameEmitterBase,
            snackBarProvider,
            schedulersProvider
        )

    @Provides
    @PerActivity
    fun providePlanesEmitterImpl(
        resRepo: ResRepo,
        arCoreFrameEmitterBase: ArCoreFrameEmitter
    ): PlanesEmitter =
        PlanesEmitterImpl(
            resRepo,
            arCoreFrameEmitterBase
        )


    @Provides
    @PerActivity
    fun provideDataFrameEntityEmitter(
        framerRecorderSettings: FrameRecorderSettings,
        openCVConverter: OpenCVConverter,
        compositeDisposable: CompositeDisposable,
        arCoreFrameEmitterBase: ArCoreFrameEmitter,
        gpsCoordinatesListener: GPSCoordinatesListener,
        telemetryRecorderFabric: TelemetryRecorderFabric
    ): DataFrameEntityEmitter =
        DataFrameEntityEmitterImpl(
            framerRecorderSettings,
            openCVConverter,
            compositeDisposable,
            arCoreFrameEmitterBase,
            gpsCoordinatesListener,
                    telemetryRecorderFabric
        )

    @Provides
    @PerActivity
    fun provideOpenCVConverter(context: Context): OpenCVConverter = OpenCVConverterImpl(context)


    @Provides
    @PerActivity
    fun provideSensorFrameEntityEmitter(
        telemetryRecorderFabric: TelemetryRecorderFabric,
        deviceSensorEventListener: DeviceSensorEventListener
    ): SensorFrameEntityEmitter =
        SensorFrameEntityEmitterImpl(
            telemetryRecorderFabric,
            deviceSensorEventListener
        )


    @Provides
    @PerActivity
    fun provideArFinalEntityEmitter(
        deviceInfo: DeviceInfo,
        schedulersProvider: SchedulersProvider,
        snackBarProvider: SnackBarProvider,
        arSessionStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider,
        arCoreFrameEmitter: ArCoreFrameEmitter, videoRecorderWrapper: VideoRecorderWrapper
    ): HeadFinalEntityEmitter =
        HeadFinalEntityEmitterImpl(
            deviceInfo,
            schedulersProvider,
            arCoreFrameEmitter,
            snackBarProvider,
            arSessionStartTimeProvider,
            videoRecorderWrapper
        )

    @Provides
    @PerActivity
    fun provideAugImgDbManger(
        compositeDisposable: CompositeDisposable,
        arCoreSession: ArCoreSession,
        assetForDetectionEmitter: AssetForDetectionEmitter,
        snackBarProvider: SnackBarProvider,
        context: Context,
        arWorldLoaderFabric: ArWorldLoaderFabric,
        schedulersProvider: SchedulersProvider,
        loaderMediaLoader: MediaLoader
    ): AugImgDbManger = AugImgDbMangerImpl(
        compositeDisposable,
        arCoreSession,
        assetForDetectionEmitter,
        snackBarProvider,
        context,
        arWorldLoaderFabric,
        schedulersProvider,
        loaderMediaLoader
    )
}
