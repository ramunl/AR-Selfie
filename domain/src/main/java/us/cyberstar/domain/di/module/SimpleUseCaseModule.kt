package us.cyberstar.domain.di.module


import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.AssetForDetectionEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PostEntityEmitter
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.usecase.CreateTapArPostUseCase
import us.cyberstar.domain.internal.usecase.CreateTapArPostUseCaseImpl

@Module
class SimpleUseCaseModule {

    @Provides
    @PerActivity
    fun provideCreateTapArPostUseCase(
        tapHelper: TapHelper,
        compositeDisposable: CompositeDisposable,
        arCoreFrameEmitter: ArCoreFrameEmitter,
        context: Context,
        s3Cache: S3Cache,
        snackBarProvider: SnackBarProvider,
        postEntityEmitter: PostEntityEmitter,
        assetForDetectionEmitter: AssetForDetectionEmitter,
        createPostFabric: CreatePostFabric,
        gpsCoordinatesListener: GPSCoordinatesListener
    ): CreateTapArPostUseCase = CreateTapArPostUseCaseImpl(
        tapHelper,
        compositeDisposable,
        arCoreFrameEmitter,
        context,
        s3Cache,
        snackBarProvider,
        postEntityEmitter,
        assetForDetectionEmitter,
        createPostFabric,
        gpsCoordinatesListener
    )
}
