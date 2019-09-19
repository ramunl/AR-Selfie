package us.cyberstar.presentation.feature.cloudAnchor.presenter

import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.manger.arScene.CREATE_3D_ONLY
import us.cyberstar.domain.external.manger.arScene.MultiNodeManager
import us.cyberstar.domain.external.model.ArPosterModel
import us.cyberstar.domain.external.usecase.CreateAr3dPostUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.cloudAnchor.view.CloudArView
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class CloudArPresenter @Inject constructor(
    private val multiNodeManager: MultiNodeManager,
    private val sceneInitializer: ArSceneInitializer,
    private val snackBarProvider: SnackBarProvider,
    private val schedulersProvider: SchedulersProvider,
    private val createAr3dPostUseCase: CreateAr3dPostUseCase
) : BasePresenter<CloudArView>() {

    fun onStart() {
        initialViewsState()
        CREATE_3D_ONLY = true
        createAr3dPostUseCase.startUseCase()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    when (it.sceneMode) {
                        CreateAr3dPostUseCase.ControlMode.SceneMode.FACE_TO_CAMERA -> {
                            faceToCameraMode()
                            if (it.horizontalPlaneNum.get() > 0) {
                                viewState.setDropControlVisible(true)
                            }
                        }
                        CreateAr3dPostUseCase.ControlMode.SceneMode.DROP -> {
                        }
                        CreateAr3dPostUseCase.ControlMode.SceneMode.IDLE -> {
                            initialViewsState()
                        }
                        CreateAr3dPostUseCase.ControlMode.SceneMode.LOCKED -> {
                            viewState.switchLockState(true)
                        }
                        CreateAr3dPostUseCase.ControlMode.SceneMode.SYNCH -> {
                            viewState.setLoadingView(true)
                        }
                    }
                    viewState.updateStatusText("nodes num = ${it.postsNum.get()}, horizontal planes = ${it.horizontalPlaneNum.get()}")

                },
                { err -> snackBarProvider.showMessage(err.message?.let { it } ?: err.toString()) },
                {
                    Timber.d("complete use case")
                    initialViewsState()
                }
            ).addTo(compositeDisposable)
        createAr3dPostUseCase.startUpdate()
        sceneInitializer.toggleLocalRemote(false)
        sceneInitializer.loadWorld(true)
    }

    fun onStop() {
        createAr3dPostUseCase.onStopUseCase()
        createAr3dPostUseCase.stopUpdate()
        sceneInitializer.loadWorld(false)
    }

    fun setLockMode(isLocked: Boolean) {
        createAr3dPostUseCase.lockModel(isLocked)
    }

    fun dropModel() {
        Timber.d("dropModel")
        createAr3dPostUseCase.moveModelOnHorizontalPlane()
    }

    fun deleteModel() {
        createAr3dPostUseCase.removeModelFromScene()
    }

    fun createModel() {
        Timber.d("createModel")
        initialViewsState()
        createAr3dPostUseCase.addNewModelToScene(ArPosterModel(listOf(""), "testurl"))
        viewState.setDropControlVisible(false)
    }

    private fun faceToCameraMode() {
        viewState.setDeleteControlVisible(true)
        viewState.setLockControlVisible(true)
        viewState.setSendControlVisible(true)
        viewState.setPhotoButtonVisible(false)
    }

    private fun initialViewsState() {
        viewState.setLoadingView(false)
        viewState.setDeleteControlVisible(false)
        viewState.setLockControlVisible(false)
        viewState.setDropControlVisible(false)
        viewState.setSendControlVisible(false)
        viewState.setPhotoButtonVisible(true)
    }

    fun confirmPostCreate() {
        createAr3dPostUseCase.confirmPostCreation()
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