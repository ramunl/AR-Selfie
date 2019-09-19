package us.cyberstar.presentation.feature.scenes.opencv.di

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.presentation.feature.scenes.opencv.view.OpenCVActivity
import us.cyberstar.presentation.helpers.SnackBarProviderImpl

@Module
abstract class OpenCVModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @PerActivity
        fun provideSnackBarHelper(activity: OpenCVActivity, schedulersProvider: SchedulersProvider): SnackBarProvider =
            SnackBarProviderImpl(activity, schedulersProvider)

    }

}
