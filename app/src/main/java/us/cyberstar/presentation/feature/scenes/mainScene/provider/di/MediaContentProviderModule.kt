package us.cyberstar.presentation.feature.scenes.mainScene.provider.di

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMakerImpl

@Module
class MediaContentProviderModule {

    @Provides
    @PerActivity
    fun providePhotoContentMaker(
        arCoreSceneView: ArCoreSceneView,
        s3Cache: S3Cache,
        schedulersProvider: us.cyberstar.common.external.SchedulersProvider
    ): PhotoContentMaker = PhotoContentMakerImpl(
        arCoreSceneView,
        s3Cache,
        schedulersProvider
    )
}
