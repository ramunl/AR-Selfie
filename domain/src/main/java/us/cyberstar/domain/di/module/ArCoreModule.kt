package us.cyberstar.domain.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.arcore.ArCoreSupportVerifier
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.internal.arcore.ArCoreSessionImpl
import us.cyberstar.domain.internal.arcore.ArCoreSupportVerifierImpl
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings

@Module
class ArCoreModule {

    @Provides
    @PerActivity
    fun provideTapHelper(context: Context) = TapHelper(context)

    @Provides
    @PerActivity
    fun provideArCoreSupportVerifier(snackBarProvider: SnackBarProvider): ArCoreSupportVerifier =
        ArCoreSupportVerifierImpl(snackBarProvider)

    @Provides
    @PerActivity
    fun provideArCoreSession(
        framerRecorderSettings: FrameRecorderSettings,
        content: Context, snackBarProvider: SnackBarProvider,
        schedulersProvider: SchedulersProvider,
        videoRecorderWrapper: VideoRecorderWrapper
    ): ArCoreSession =
        ArCoreSessionImpl(framerRecorderSettings, content, snackBarProvider, schedulersProvider, videoRecorderWrapper)


}
