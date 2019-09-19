package us.cyberstar.domain.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import com.cyber.ux.SceneFormNodeProvider
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.internal.factory.RenderableFactoryImpl

@Module
class ArModelFactoryModule {

    @Provides
    @PerActivity
    fun provideRenderableFactory(
        sceneFormNodeProvider:SceneFormNodeProvider,
        context: Context,
        schedulersProvider: SchedulersProvider
    ): RenderableFactory =
        RenderableFactoryImpl(sceneFormNodeProvider, context, schedulersProvider)


}
