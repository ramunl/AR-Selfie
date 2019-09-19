package us.cyberstar.presentation.feature.postOpenPhoto.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.domain.external.model.ArPostContentType
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.postOpenPhoto.view.PostOpenPhotoView
import us.cyberstar.presentation.feature.postPreview.presenter.PostPreviewPresenter
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import javax.inject.Inject

@InjectViewState
class PostOpenPhotoPresenter @Inject constructor(private val arSceneInteractor: ArSceneInteractor) : BasePresenter<PostOpenPhotoView>() {
    fun setupViews(bundle: Bundle) {
        Timber.d("setupViews bundle = ${bundle}")
        with(bundle.getParcelable<ArPostModel>(PostPreviewPresenter.bundleKey)!!) {
            viewState.showPhoto(mediaPath())
        }
    }
}