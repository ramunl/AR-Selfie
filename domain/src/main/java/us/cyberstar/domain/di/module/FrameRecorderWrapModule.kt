package us.cyberstar.domain.di.module

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.domain.external.loader.SaveVideoFabric
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.internal.manger.VideoRecorderWrapperImpl
import us.cyberstar.framerecorder.media.external.FrameRecorder
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import us.cyberstar.framerecorder.media.external.VideoRecordThread

@Module
class FrameRecorderWrapModule {

    @Provides
    @PerActivity
    fun provideRecorderWrapper(
        sessionIdProvider: SessionIdProvider,
        frameRecorder: FrameRecorder,
        videoRecordThread: VideoRecordThread,
        schedulersProvider: SchedulersProvider,
        frameRecorderSettings: FrameRecorderSettings,
        saveVideoFabric: SaveVideoFabric
    ): VideoRecorderWrapper = VideoRecorderWrapperImpl(
        sessionIdProvider,
        schedulersProvider, frameRecorder, videoRecordThread, saveVideoFabric,
        frameRecorderSettings
    )
}
