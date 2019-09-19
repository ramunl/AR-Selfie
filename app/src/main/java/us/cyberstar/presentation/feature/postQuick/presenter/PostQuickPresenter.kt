package us.cyberstar.presentation.feature.postQuick.presenter


import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.usecase.CreateTargetPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.postQuick.view.PostQuickView
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import javax.inject.Inject


@InjectViewState
class PostQuickPresenter @Inject constructor() : BasePresenter<PostQuickView>() {

    fun startUseCase(arguments: Bundle) {
        /*Timber.d("PostQuickPresenter:createModel")
        createQuickTargetPostUseCase.prepareAndCreateQuickPost(ArPostModel(ArPostType.QUICK, photoContentMaker.lastBitmap!!, ""))
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribeToAssetForDetection(
                {},
                { err -> snackBarProvider.showMessage(err.message?.let { it } ?: err.toString()) },
                { showCameraFragment() }
            ).addTo(compositeDisposable)*/
    }

    fun closePost() {
        //createQuickTargetPostUseCase.removeModelFromScene()
    }


    fun showCameraFragment() {
        //arSceneInteractor.changeView(ArSceneInteractorImpl.SceneState.Camera)
    }
}