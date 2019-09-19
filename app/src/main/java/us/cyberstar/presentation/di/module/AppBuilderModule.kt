package us.cyberstar.presentation.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import us.cyberstar.common.PerActivity
import us.cyberstar.data.di.module.GrpcSessionModule
import us.cyberstar.data.di.module.S3TransferModule
import us.cyberstar.data.di.module.SocketModule
import us.cyberstar.domain.di.module.*
import us.cyberstar.framerecorder.media.di.module.FrameRecorderModule
import us.cyberstar.presentation.feature.cameraScreen.di.CameraViewModule
import us.cyberstar.presentation.feature.cameraScreen.view.CameraActivity
import us.cyberstar.presentation.feature.cameraView.di.CameraModule
import us.cyberstar.presentation.feature.postEdit.di.PostEditModule
import us.cyberstar.presentation.feature.postPreview.di.PostPreviewModule
import us.cyberstar.presentation.feature.postTargeting.di.PostTargetingModule
import us.cyberstar.presentation.feature.scenes.authScene.di.AuthViewModule
import us.cyberstar.presentation.feature.scenes.authScene.view.AuthActivity
import us.cyberstar.presentation.feature.scenes.devScene.di.DevViewModule
import us.cyberstar.presentation.feature.scenes.devScene.view.DevActivity
import us.cyberstar.presentation.feature.scenes.mainScene.di.MainViewModule
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.di.ArSceneInteractorModule
import us.cyberstar.presentation.feature.scenes.mainScene.provider.di.MediaContentProviderModule
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.feature.scenes.opencv.di.OpenCVModule
import us.cyberstar.presentation.feature.scenes.opencv.view.OpenCVActivity
import us.cyberstar.presentation.feature.scenes.splashScene.di.SplashViewModule
import us.cyberstar.presentation.feature.scenes.splashScene.view.SplashActivity

@Module
interface AppBuilderModule {

    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            SimpleUseCaseModule::class,
            FrameRecorderWrapModule::class,
            MainViewModule::class,
            FrameRecorderModule::class,
            ArCoreModule::class,
            ArSceneInteractorModule::class,
            PostTargetingModule::class,
            MediaContentProviderModule::class,
            PostPreviewModule::class,
            PostEditModule::class,
            CameraModule::class,
            SceneFormManagerModule::class,
            S3TransferModule::class,
            GrpcSessionModule::class,
            UseCaseModule::class,
            ArSessionTelemetryModule::class,
            ArModelFactoryModule::class,
            EntityLoaderModule::class,// emits telemetry events, posts, detected assets, assets saved, snapshots
            SceneLoaderModule::class,
            CloudAnchorModule::class] // initializes root node, load posts saved
    )
    fun provideMainActivityFactory(): MainActivity


    @PerActivity
    @ContributesAndroidInjector(
        modules = [
            SimpleUseCaseModule::class,
            SceneGLManagerModule::class,
            DevViewModule::class,
            FrameRecorderModule::class,
            ArCoreModule::class,
            FrameRecorderWrapModule::class,
            S3TransferModule::class,
            GrpcSessionModule::class,
            ArSessionTelemetryModule::class,
            EntityLoaderModule::class,// emits telemetry events, posts, detected assets, assets saved, snapshots
            SceneLoaderModule::class]
    )
    fun provideDevActivityFactory(): DevActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [SplashViewModule::class, ArCoreModule::class])
    fun provideSplashActivityFactory(): SplashActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [CameraViewModule::class])
    fun provideCameraActivityFactory(): CameraActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [AuthViewModule::class, SocketModule::class, ProfileAuthModule::class])
    fun provideAuthActivityFactory(): AuthActivity


    @PerActivity
    @ContributesAndroidInjector(modules = [OpenCVModule::class])
    fun provideOpenCVActivityFactory(): OpenCVActivity

}