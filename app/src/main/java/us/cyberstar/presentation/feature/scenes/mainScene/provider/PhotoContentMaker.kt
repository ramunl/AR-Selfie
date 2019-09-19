package us.cyberstar.presentation.feature.scenes.mainScene.provider

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPostPhotoModel
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl

interface PhotoContentMaker {
    //val photoEmitter: BehaviorSubject<Bitmap>
    fun makePhoto(): Single<ArPostPhotoModel>
    fun provideArView(arView: ArFragmentImpl) //must be called from ArFragment
    //var lastBitmap: Bitmap?
}