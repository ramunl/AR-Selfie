package us.cyberstar.presentation.feature.cameraView.presenter


import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.view.View
import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.helper.TapHelper
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.domain.external.model.ArPostVideoModel
import us.cyberstar.domain.external.usecase.ArPostOpenUseCase
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.cameraView.view.CameraView
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import us.cyberstar.presentation.helpers.VideoPostRecorder
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import us.cyberstar.domain.external.helper.getNewId
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainActivity


@InjectViewState
class CameraPresenter @Inject constructor(
    private val activity: MainActivity,
    private val arPostOpenUseCase: ArPostOpenUseCase,
    private val tapHelper: TapHelper,
    private val snackBarProvider: SnackBarProvider,
    private val videoPostRecorder: VideoPostRecorder,
    private val schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    private val arSceneInteractor: ArSceneInteractor,
    private val photoContentMaker: PhotoContentMaker
) : BasePresenter<CameraView>() {

    fun startOpenPostUseCase() {
        arPostOpenUseCase.startUseCase(object : ArPostOpenUseCase.PostNodeTapListener {
            override fun onPostNodeTapped(arPostModel: ArPostModel) {
                arSceneInteractor.changeView(
                    ArSceneInteractorImpl.SceneNavParam(
                        ArSceneInteractorImpl.SceneState.Open, arPostModel
                    )
                )
            }
        })
    }

    fun stopOpenPostUseCase() {
        arPostOpenUseCase.stopUseCase()
    }

    fun stopVideoRecord() {
        videoPostRecorder.stopRecord()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                {
                    val postModel = ArPostModel(it)
                    Timber.e("-----------------create a new Video post with postId=${postModel}--")
                    arSceneInteractor.changeView(
                        ArSceneInteractorImpl.SceneNavParam(
                            ArSceneInteractorImpl.SceneState.Preview, postModel
                        )
                    )
                    viewState.togglePhotoVideoMode(false)
                }, { snackBarProvider.showError(it.toString(), false) }
            ).addTo(compositeDisposable)
    }

    private fun doesUserHaveRecordPermission(): Boolean {
        val result = activity.checkCallingOrSelfPermission(RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun startVideoRecord(): Boolean {
        return if (doesUserHaveRecordPermission()) {
            videoPostRecorder.startRecord()
            true
        } else {
            snackBarProvider.showMessageWithSettingsButton("RECORD_AUDIO permission must be granted to create video ar post")
            false
        }
    }

    fun takePicture() {
        photoContentMaker.makePhoto()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())
            .subscribe({
                Timber.d("came a new bitmap $it from photoEmitter ")
                val postModel = ArPostModel(it)
                Timber.e("-----------------create a new Photo post with postId=${postModel}--")
                arSceneInteractor.changeView(
                    ArSceneInteractorImpl.SceneNavParam(
                        ArSceneInteractorImpl.SceneState.Preview, postModel
                    )
                )
            }, { Timber.e("photoEmitter failed with $it") })
            .addTo(compositeDisposable)
    }

    fun addTapListenerToView(view: View) {
        view.setOnTouchListener(tapHelper)
    }


    fun testVideoPostOpen() {
        val videoModel = ArPostVideoModel(
            640,
            480,
            "http://s3.cyberstar.us/dev/user_1/chat/20190813_134827_8aee498c-2651-4f28-975b-5957a7179c94",
            30.0,
            HashMap()
        )
        val postModel = ArPostModel(videoModel, "")
        arSceneInteractor.changeView(
            ArSceneInteractorImpl.SceneNavParam(
                ArSceneInteractorImpl.SceneState.Open, postModel
            )
        )
    }

}