package us.cyberstar.presentation.feature.scenes.devScene.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerActivity
import us.cyberstar.common.PerFragment
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.presentation.delegate.toolbar.ToolbarDelegate
import us.cyberstar.presentation.feature.glFragment.di.GlViewModule
import us.cyberstar.presentation.feature.glFragment.view.GlFragment
import us.cyberstar.presentation.feature.scenes.devScene.arcore.GlArCoreFrameEmitterImpl
import us.cyberstar.presentation.feature.scenes.devScene.view.DevActivity
import us.cyberstar.presentation.feature.settingsDev.di.SettingsDevModule
import us.cyberstar.presentation.feature.settingsDev.view.SettingsDevFragment
import us.cyberstar.presentation.helpers.PermissionHelper
import us.cyberstar.presentation.helpers.SnackBarProviderImpl

@Module
abstract class DevViewModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @PerActivity
        fun provideToolbarDelegate(activity: DevActivity) = ToolbarDelegate(activity)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideSnackBarHelper(activity: DevActivity, schedulersProvider: SchedulersProvider): SnackBarProvider =
            SnackBarProviderImpl(activity, schedulersProvider)

        @JvmStatic
        @Provides
        @PerActivity
        fun provideCameraPermissionHelper(activity: DevActivity, res: ResRepo) = PermissionHelper(activity, res)


        @JvmStatic
        @Provides
        @PerActivity
        fun provideArCoreFrameEmitter(
            arCoreSession: ArCoreSession,
            compositeDisposable: CompositeDisposable
        ): ArCoreFrameEmitter = GlArCoreFrameEmitterImpl(arCoreSession, compositeDisposable)

    }


    //@PerFragment
   // @ContributesAndroidInjector(modules = [GlViewModule::class])
   // abstract fun provideGlArFragmentFactory(): GlFragment


    //@PerFragment
    //@ContributesAndroidInjector(modules = [SettingsDevModule::class])
    //abstract fun provideSettingsDevFragmentFactory(): SettingsDevFragment

}
