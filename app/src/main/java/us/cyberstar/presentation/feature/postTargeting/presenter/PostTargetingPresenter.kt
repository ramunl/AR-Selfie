package us.cyberstar.presentation.feature.postTargeting.presenter


import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.usecase.CreateTargetPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.postPreview.presenter.PostPreviewPresenter.Companion.bundleKey
import us.cyberstar.presentation.feature.postTargeting.view.PostTargetingView
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import javax.inject.Inject


@InjectViewState
class PostTargetingPresenter @Inject constructor(
    private val snackBarProvider: SnackBarProvider,
    private val schedulersProvider: SchedulersProvider,
    private val arSceneInteractor: ArSceneInteractor,
    private val createTargetPostUseCase: CreateTargetPostUseCase
) : BasePresenter<PostTargetingView>() {

    lateinit var uiArPostModelBundle: Bundle

    fun setBundle(bundle: Bundle) {
        uiArPostModelBundle = bundle
    }

    override fun attachView(view: PostTargetingView) {
        super.attachView(view)
        startUseCase()
    }

    private fun startUseCase() {
        Timber.d("PostTargetingPresenter:createModel")
        val uiArPostModel = uiArPostModelBundle.getParcelable<ArPostModel>(bundleKey)!!
        createTargetPostUseCase.prepareTargetingPost(uiArPostModel)
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    updatePostConfirmButtonState(it)
                },
                { err -> snackBarProvider.showMessage(err.message?.let { it } ?: err.toString()) },
                {
                }
            ).addTo(compositeDisposable)
    }

    fun stopUseCase(destroyPost: Boolean = false) {
        Timber.d("stopUseCase ")
        createTargetPostUseCase.closeUseCase(destroyPost)
    }


    fun confirmTargetingPostCreation() {
        Timber.d("confirmTargetingPostCreation ")
        viewState.showProgressView(true)
        createTargetPostUseCase.confirmTargetingPost()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    viewState.showProgressView(false)
                    stopUseCase(false)
                    showCameraFragment()
                },
                { err ->
                    viewState.showProgressView(false)
                    viewState.showErrorView(err.message?.let { it } ?: err.toString())
                }
            ).addTo(compositeDisposable)
    }

    private fun updatePostConfirmButtonState(isEnabled: Boolean) {
        viewState.updatePostConfirmButtonState(isEnabled)
    }

    fun showCameraFragment() {
        Timber.d("showCameraFragment")
        arSceneInteractor.changeView(ArSceneInteractorImpl.SceneNavParam(ArSceneInteractorImpl.SceneState.Camera))
    }

    fun tryAgain() {
        viewState.hideErrorView()
        createTargetPostUseCase.continueUseCase()
    }
}