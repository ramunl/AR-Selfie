package us.cyberstar.presentation.feature.scenes.mainScene.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import us.cyberstar.common.PerActivity
import us.cyberstar.common.PerFragment
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreScene
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.presentation.delegate.toolbar.ToolbarDelegate
import us.cyberstar.presentation.feature.arFragment.di.ArViewModule
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl
import us.cyberstar.presentation.feature.cameraView.di.CameraModule
import us.cyberstar.presentation.feature.cameraView.view.CameraFragment
import us.cyberstar.presentation.feature.cloudAnchor.di.CloudArModule
import us.cyberstar.presentation.feature.cloudAnchor.view.CloudArFragment
import us.cyberstar.presentation.feature.postEdit.di.PostEditModule
import us.cyberstar.presentation.feature.postEdit.view.PostEditFragment
import us.cyberstar.presentation.feature.postOpenPhoto.di.PostOpenPhotoModule
import us.cyberstar.presentation.feature.postOpenPhoto.view.PostOpenPhotoFragment
import us.cyberstar.presentation.feature.postOpenVideo.di.PostOpenVideoModule
import us.cyberstar.presentation.feature.postOpenVideo.view.PostOpenVideoFragment
import us.cyberstar.presentation.feature.postPreview.di.PostPreviewModule
import us.cyberstar.presentation.feature.postPreview.view.PostPreviewFragment
import us.cyberstar.presentation.feature.postQuick.di.PostQuickModule
import us.cyberstar.presentation.feature.postQuick.view.PostQuickFragment
import us.cyberstar.presentation.feature.postTargeting.di.PostTargetingModule
import us.cyberstar.presentation.feature.postTargeting.view.PostTargetingFragment
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.ArCoreFrameEmitterImpl
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.ArCoreSceneViewImpl
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneImpl
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import us.cyberstar.presentation.feature.settingsDev.di.SettingsDevModule
import us.cyberstar.presentation.feature.settingsDev.view.SettingsDevFragment
import us.cyberstar.presentation.helpers.SnackBarProviderImpl

@Module
abstract class MainViewModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @PerActivity
        fun provideToolbarDelegate(activity: MainActivity) = ToolbarDelegate(activity)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideSnackBarHelper(
            activity: MainActivity,
            schedulersProvider: SchedulersProvider
        ): SnackBarProvider =
            SnackBarProviderImpl(activity, schedulersProvider)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideArCoreSceneView(
            activity: MainActivity,
            arCoreSession: ArCoreSession
        ): ArCoreSceneView =
            ArCoreSceneViewImpl(activity, arCoreSession)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideArCoreScene(
            activity: MainActivity,
            arCoreSceneView: ArCoreSceneView
        ): ArCoreScene =
            ArCoreSceneImpl(activity, arCoreSceneView)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideArCoreFrameEmitter(
            schedulersProvider: SchedulersProvider,
            arCoreSceneView: ArCoreSceneView
        ): ArCoreFrameEmitter =
            ArCoreFrameEmitterImpl(schedulersProvider, arCoreSceneView)

    }


    @PerFragment
    @ContributesAndroidInjector(modules = [CloudArModule::class])
    abstract fun provideCloudArFragmentFactory(): CloudArFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [ArViewModule::class])
    abstract fun provideArFragmentFactory(): ArFragmentImpl


    @PerFragment
    @ContributesAndroidInjector(modules = [CameraModule::class])
    abstract fun provideCameraFragmentFactory(): CameraFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [PostTargetingModule::class])
    abstract fun providePostTargetingFragmentFactory(): PostTargetingFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [PostQuickModule::class])
    abstract fun providePostQuickFragmentFactory(): PostQuickFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [PostPreviewModule::class])
    abstract fun providePostPreviewFragmentFactory(): PostPreviewFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [PostEditModule::class])
    abstract fun providePostEditFragmentFactory(): PostEditFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [SettingsDevModule::class])
    abstract fun provideSettingsDevFragmentFactory(): SettingsDevFragment


    @PerFragment
    @ContributesAndroidInjector(modules = [PostOpenPhotoModule::class])
    abstract fun providePostOpenFragmentFactory(): PostOpenPhotoFragment

    @PerFragment
    @ContributesAndroidInjector(modules = [PostOpenVideoModule::class])
    abstract fun providePostOpenVideoFragmentFactory(): PostOpenVideoFragment



}
