package us.cyberstar.presentation.feature.arFragment.di

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerFragment
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.presentation.feature.cameraView.utils.VideoRecorder
import us.cyberstar.presentation.helpers.VideoPostRecorderImpl
import us.cyberstar.presentation.helpers.VideoPostRecorder

@Module
class ArViewModule {

    @Provides
    @PerFragment
    fun provideVideoRecorderWrapper(
        videoRecorder: VideoRecorder,
        s3Cache: S3Cache,
        context: Context,
        snackBarProvider: SnackBarProvider
    ): VideoPostRecorder =
        VideoPostRecorderImpl(
            videoRecorder,
            s3Cache,
            context,
            snackBarProvider
        )

}