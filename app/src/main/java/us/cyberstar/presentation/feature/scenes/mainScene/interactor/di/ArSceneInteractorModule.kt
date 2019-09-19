package us.cyberstar.presentation.feature.scenes.mainScene.interactor.di

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl

@Module
class ArSceneInteractorModule {

    @Provides
    @PerActivity
    fun provideArSceneInteractor(
    ): ArSceneInteractor = ArSceneInteractorImpl()
}
