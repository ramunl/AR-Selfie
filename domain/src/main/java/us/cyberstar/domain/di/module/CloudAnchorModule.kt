package us.cyberstar.domain.di.module

import android.content.Context
import com.google.ar.core.Session
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorManager
import us.cyberstar.domain.external.cloudAnchor.FirebaseManager
import us.cyberstar.domain.external.cloudAnchor.HostResolveModeCurrent
import us.cyberstar.domain.external.cloudAnchor.RoomCodeAndCloudAnchorIdListener
import us.cyberstar.domain.internal.cloudAnchor.CloudAnchorManagerImpl
import us.cyberstar.domain.internal.cloudAnchor.FirebaseManagerImpl
import us.cyberstar.domain.internal.cloudAnchor.HostResolveModeCurrentImpl
import us.cyberstar.domain.internal.cloudAnchor.RoomCodeAndCloudAnchorIdListenerImpl

@Module
class CloudAnchorModule {

    @Provides
    @PerActivity
    fun provideRoomCodeAndCloudAnchorIdListener(
        cloudAnchorManager: CloudAnchorManager,
        //firebaseManager: FirebaseManager,
        resRepo: ResRepo,
        snackBarProvider: SnackBarProvider
    ): RoomCodeAndCloudAnchorIdListener = RoomCodeAndCloudAnchorIdListenerImpl(
        cloudAnchorManager,
      //  firebaseManager,
        resRepo,
        snackBarProvider
    )


    @Provides
    @PerActivity
    fun provideHostResolveModeCurrent(
    ): HostResolveModeCurrent = HostResolveModeCurrentImpl()


    @Provides
    @PerActivity
    fun provideFireBaseManager(
        context: Context,
        snackBarProvider: SnackBarProvider
    ): FirebaseManager = FirebaseManagerImpl(context, snackBarProvider)


    @Provides
    @PerActivity
    fun provideCloudAnchorManager(
        arCoreSession: ArCoreSession
    ): CloudAnchorManager =
        CloudAnchorManagerImpl(arCoreSession)

}
