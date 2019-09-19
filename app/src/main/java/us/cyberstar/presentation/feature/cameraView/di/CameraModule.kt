package us.cyberstar.presentation.feature.cameraView.di

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.presentation.feature.cameraView.utils.VideoRecorder
import us.cyberstar.presentation.feature.cameraView.utils.VideoRecorderImpl
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.helpers.VideoPostRecorder
import us.cyberstar.presentation.helpers.VideoPostRecorderImpl

@Module
abstract class CameraModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun provideVideoRecorder(
            s3Cache: S3Cache,
            context: Context,
            sessionIdProvider: SessionIdProvider,
            arCoreSceneView: ArCoreSceneView
        ): VideoRecorder =
            VideoRecorderImpl(
                s3Cache,
                context,
                sessionIdProvider,
                arCoreSceneView
            )

        @Provides
        @JvmStatic
        fun provideVideoPostRecorder(
            videoRecorder: VideoRecorder,
            s3Cache: S3Cache,
            context: Context, snackBarProvider: SnackBarProvider
        ): VideoPostRecorder =
            VideoPostRecorderImpl(
                videoRecorder,
                s3Cache,
                context,
                snackBarProvider
            )
    }
}
