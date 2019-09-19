package us.cyberstar.presentation.feature.postOpenVideo.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.postOpenVideo.view.PostOpenVideoView
import us.cyberstar.presentation.feature.postPreview.presenter.PostPreviewPresenter
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity
import javax.inject.Inject

@InjectViewState
class PostOpenVideoPresenter @Inject constructor(
    private val mainActivity: MainActivity,
    private val arSceneInteractor: ArSceneInteractor) :
    BasePresenter<PostOpenVideoView>() {
    fun setupViews(bundle: Bundle) {
        Timber.d("setupViews bundle = ${bundle}")
        with(bundle.getParcelable<ArPostModel>(PostPreviewPresenter.bundleKey)!!) {
            viewState.showVideo(mediaPath())
        }
    }
}