package us.cyberstar.presentation.feature.cameraScreen.di

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.presentation.feature.cameraScreen.view.CameraActivity
import us.cyberstar.presentation.helpers.PermissionHelper

@Module
abstract class CameraViewModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PerActivity
        fun provideCameraPermissionHelper(activity: CameraActivity, res: us.cyberstar.common.external.ResRepo) =
            PermissionHelper(activity, res)
    }
}
