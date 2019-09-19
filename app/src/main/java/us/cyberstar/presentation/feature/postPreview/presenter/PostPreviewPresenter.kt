package us.cyberstar.presentation.feature.postPreview.presenter


import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.loader.s3.MediaLoader
import us.cyberstar.domain.external.model.ArPostType
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.usecase.CreateQuickPostUseCase
import us.cyberstar.domain.external.usecase.CreateTargetPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.postPreview.view.PostPreviewView
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import javax.inject.Inject


@InjectViewState
class PostPreviewPresenter @Inject constructor(
    private val mediaLoader: MediaLoader,
    private val snackBarProvider: SnackBarProvider,
    private val schedulersProvider: SchedulersProvider,
    private val createkTargetPostUseCase: CreateTargetPostUseCase,
    private val createkQuickPostUseCase: CreateQuickPostUseCase,
    private val photoContentMaker: PhotoContentMaker,
    private val arSceneInteractor: ArSceneInteractor
) : BasePresenter<PostPreviewView>() {

    private var arPostModel: ArPostModel? = null
    companion object {
        val bundleKey = "arPostModel"
    }
    fun setupViews(bundle: Bundle) {
       //we don't show preview image... do we need it really ? TODO check
         Timber.d("setupViews bundle = ${bundle}")
        with(bundle.getParcelable<ArPostModel>(bundleKey)!!) {
            arPostModel = this
            /*fun onImageReady(bitmap: Bitmap?) {
                Timber.d("onImageReady = ${bitmap != null}")
                viewState.showPhotoPreview(bitmap)
            }
            mediaLoader.downLoadImageAsynch(mediaPath(), ::onImageReady)
            */
        }
    }

    fun createQuickPost(title: String) {
        Timber.d("createQuickPost")
        arPostModel!!.title = title
        with(arPostModel!!) {
            arPostType = ArPostType.QUICK
            createkQuickPostUseCase.prepareAndCreateQuickPost(this)
        }.subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    showCameraView()
                    createkQuickPostUseCase.closeUseCase()
                },
                { err -> snackBarProvider.showMessage(err.message?.let { it } ?: err.toString()) }
            ).addTo(compositeDisposable)
    }

    fun showTargetingView(title: String) {
        Timber.d("showTargetingView")
        arPostModel!!.title = title
        with(arPostModel!!) {
            arPostType = ArPostType.TARGETING
            arSceneInteractor.changeView(
                ArSceneInteractorImpl.SceneNavParam(
                    ArSceneInteractorImpl.SceneState.Targeting,
                    this
                )
            )
        }

    }

    fun showCameraView() {
        Timber.d("showCameraView")
        arSceneInteractor.changeView(ArSceneInteractorImpl.SceneNavParam(ArSceneInteractorImpl.SceneState.Camera))
    }
}