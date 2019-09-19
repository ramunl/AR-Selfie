package us.cyberstar.domain.di.module


import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.TelemetryRecorderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.external.loader.local.TelemetryRecorderLocal
import us.cyberstar.domain.internal.ArWorldLoaderSettings
import us.cyberstar.domain.internal.arcore.ArSessionStartTimeProviderImpl
import us.cyberstar.domain.internal.loader.TelemetryRecorderRecorderFabricImpl
import us.cyberstar.domain.internal.loader.local.telemetry.TelemetryRecorderLocalImpl
import us.cyberstar.domain.internal.loader.grpc.telemetry.TelemetryRecorderGrpcImpl

@Module
class ArSessionTelemetryModule {

    @Provides
    @PerActivity
    fun provideArSessionStartTimeProvider(): ArSessionStartTimeProvider = ArSessionStartTimeProviderImpl()


    @Provides
    @PerActivity
    fun provideArCoreEntityRecorderFabric(
        arWorldLoaderSettings: ArWorldLoaderSettings,
        arCoreEntityRecorderLocal: TelemetryRecorderLocal,
        arCoreEntityRecorderRemote: TelemetryRecorderRemote
    ): TelemetryRecorderFabric =
        TelemetryRecorderRecorderFabricImpl(
            arWorldLoaderSettings,
            arCoreEntityRecorderLocal,
            arCoreEntityRecorderRemote
        )


    @Provides
    @PerActivity
    fun provideArCoreEntityRecorderLocal(
        hardwareEntityEmitter: HardwareEntityEmitter,
        sessionIdProvider: SessionIdProvider,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        snackBarProvider: SnackBarProvider,
        arEntityCache: ArEntityCache,
        schedulersProvider: SchedulersProvider,
        arSessionStartTimeProvider: ArSessionStartTimeProvider,
       // dataFrameEntityEmitter: DataFrameEntityEmitter,
        augmentedImageEmitter: AugmentedImageEmitter,
        planesEmitter: PlanesEmitter,
        headFinalEntityEmitter: HeadFinalEntityEmitter
    ): TelemetryRecorderLocal =
        TelemetryRecorderLocalImpl(
            hardwareEntityEmitter,
            sessionIdProvider,
            arCoreFrameEmitter,
            snackBarProvider,
            arEntityCache,
            schedulersProvider,
            arSessionStartTimeProvider,
           // dataFrameEntityEmitter,
            augmentedImageEmitter,
            planesEmitter,
            headFinalEntityEmitter
        )


    @Provides
    @PerActivity
    fun provideArCoreEntityRecorderRemote(
        hardwareEntityEmitter: HardwareEntityEmitter,
        compositeDisposable: CompositeDisposable,
        schedulersProvider: SchedulersProvider,
        //dataFrameEntityEmitter: DataFrameEntityEmitter,
        augmentedImageEmitter: AugmentedImageEmitter,
        planesEmitter: PlanesEmitter,
        grpcTelemetryService: GrpcTelemetryService,
        arSessionStartTimeProvider: ArSessionStartTimeProvider,
        sessionIdProvider: SessionIdProvider,
        headFinalEntityEmitter: HeadFinalEntityEmitter
    ): TelemetryRecorderRemote =
        TelemetryRecorderGrpcImpl(
            hardwareEntityEmitter,
            compositeDisposable,
            schedulersProvider,
         //   dataFrameEntityEmitter,
            augmentedImageEmitter,
            planesEmitter,
            grpcTelemetryService,
            arSessionStartTimeProvider,
            sessionIdProvider,
            headFinalEntityEmitter
        )

}
