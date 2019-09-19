package us.cyberstar.presentation.feature.scenes.splashScene.di

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.presentation.feature.scenes.splashScene.view.SplashActivity
import us.cyberstar.presentation.helpers.PermissionHelper
import us.cyberstar.presentation.helpers.SnackBarProviderImpl

@Module
abstract class SplashViewModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @PerActivity
        fun provideSnackBarHelper(activity: SplashActivity, schedulersProvider: SchedulersProvider): SnackBarProvider = SnackBarProviderImpl(activity,schedulersProvider)

    }

}
