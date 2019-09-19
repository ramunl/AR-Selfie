package us.cyberstar.domain.di.module


import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.manger.arScene.NodeManager
import us.cyberstar.domain.internal.manger.arScene.GlNodeManagerImpl

@Module
class SceneGLManagerModule {
    @Provides
    @PerActivity
    fun provideGlNodeManager(
        context: Context,
        arCoreFrameEmitterBase: ArCoreFrameEmitter,
        arCoreSession: ArCoreSession,
        snackBarProvider: SnackBarProvider,
        schedulersProvider: SchedulersProvider
    ): NodeManager = GlNodeManagerImpl(
        context,
        arCoreFrameEmitterBase,
        arCoreSession,
        snackBarProvider,
        schedulersProvider
    )
}
