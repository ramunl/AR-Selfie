package us.cyberstar.framerecorder.media.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.framerecorder.media.external.FrameRecorder
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import us.cyberstar.framerecorder.media.external.RecordFragmentsStack
import us.cyberstar.framerecorder.media.external.VideoRecordThread
import us.cyberstar.framerecorder.media.internal.FrameRecorderImpl
import us.cyberstar.framerecorder.media.internal.FrameRecorderSettingsImpl
import us.cyberstar.framerecorder.media.internal.RecordFragmentsStackImpl
import us.cyberstar.framerecorder.media.internal.VideoRecordThreadImpl

@Module
class FrameRecorderModule {


    @Provides
    @PerActivity
    fun provideFrameRecorderSettings(context: Context): FrameRecorderSettings = FrameRecorderSettingsImpl(context)


    @Provides
    @PerActivity
    fun provideRecordFragmentsContainer(): RecordFragmentsStack = RecordFragmentsStackImpl()


    @Provides
    @PerActivity
    fun provideFrameRecorder(
        schedulersProvider: SchedulersProvider,
        snackBarProvider: SnackBarProvider,
        frameRecorderSettings: FrameRecorderSettings,
        recordFragmentsStack: RecordFragmentsStack
    ): FrameRecorder =
        FrameRecorderImpl(schedulersProvider, snackBarProvider, frameRecorderSettings, recordFragmentsStack)


    @PerActivity
    @Provides
    fun provideVideoRecordThread(
        frameRecorderSettings: FrameRecorderSettings,
        recordFragmentsStack: RecordFragmentsStack,
        frameRecorder: FrameRecorder,
        schedulersProvider: SchedulersProvider,
        context: Context
    ): VideoRecordThread =
        VideoRecordThreadImpl(
            frameRecorderSettings,
            recordFragmentsStack,
            frameRecorder,
            schedulersProvider,
            context
        )

}
