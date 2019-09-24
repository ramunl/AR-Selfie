package us.cyberstar.presentation.feature.arSelfie.presenter

import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.manger.arScene.CREATE_3D_ONLY
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.usecase.CreateSelfieUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.arSelfie.view.ArSelfieView
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import javax.inject.Inject


@InjectViewState
class ArSelfiePresenter @Inject constructor(
    private val photoContentMaker: PhotoContentMaker,
    private val sceneInitializer: ArSceneInitializer,
    private val snackBarProvider: SnackBarProvider,
    private val schedulersProvider: SchedulersProvider,
    private val createselfieUseCase: CreateSelfieUseCase
) : BasePresenter<ArSelfieView>() {


    fun makeArPhoto() {
        resetViewsState()
        photoContentMaker.makePhoto()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())
            .subscribe({
                Timber.d("came a new bitmap $it from photoEmitter ")
                createselfieUseCase.addNewModelToScene(ArPostModel(it))
                Timber.e("-----------------create a new Photo post with postId")

            }, { Timber.e("photoEmitter failed with $it") })
            .addTo(compositeDisposable)
    }

    fun onStart() {
        resetViewsState()
        CREATE_3D_ONLY = true
        createselfieUseCase.startUseCase()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    when (it.sceneMode) {
                        CreateSelfieUseCase.ControlMode.SceneMode.FACE_TO_CAMERA -> {
                            faceToCameraMode()
                        }
                        CreateSelfieUseCase.ControlMode.SceneMode.DROP -> {
                        }
                        CreateSelfieUseCase.ControlMode.SceneMode.IDLE -> {
                            resetViewsState()
                        }
                        CreateSelfieUseCase.ControlMode.SceneMode.LOCKED -> {
                            viewState.switchLockState(true)
                        }
                        CreateSelfieUseCase.ControlMode.SceneMode.SYNCH -> {
                            viewState.setLoadingView(true)
                        }
                    }
                    viewState.updateStatusText("nodes num = ${it.postsNum.get()}, horizontal planes = ${it.horizontalPlaneNum.get()}")

                },
                { err -> snackBarProvider.showMessage(err.message?.let { it } ?: err.toString()) },
                {
                    Timber.d("complete use case")
                    resetViewsState()
                }
            ).addTo(compositeDisposable)
        createselfieUseCase.startUpdate()
    }

    fun onStop() {
        createselfieUseCase.onStopUseCase()
        createselfieUseCase.stopUpdate()
        //sceneInitializer.loadWorld(false)
    }

    fun setLockMode(isLocked: Boolean) {
        createselfieUseCase.lockModel(isLocked)
    }

    fun dropModel() {
        Timber.d("dropModel")
        createselfieUseCase.moveModelOnHorizontalPlane()
    }

    fun deleteModel() {
        createselfieUseCase.removeModelFromScene()
    }

    private fun faceToCameraMode() {
        viewState.setDeleteControlVisible(true)
        viewState.setLockControlVisible(true)
        viewState.setSendControlVisible(true)
        viewState.setPhotoButtonVisible(false)
    }

    private fun resetViewsState() {
        viewState.setLoadingView(false)
        viewState.setDeleteControlVisible(false)
        viewState.setLockControlVisible(false)
        viewState.setDropControlVisible(false)
        viewState.setSendControlVisible(false)
        viewState.setPhotoButtonVisible(true)
    }

    fun confirmPostCreate() {
        createselfieUseCase.confirmPostCreation()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    snackBarProvider.showMessage("Model posted!")
                    viewState.setLoadingView(false)
                },
                { err ->
                    snackBarProvider.showMessage(err.message?.let { it } ?: err.toString())
                    viewState.setLoadingView(false)
                }
            ).addTo(compositeDisposable)
    }
}