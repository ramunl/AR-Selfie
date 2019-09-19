package us.cyberstar.presentation.feature.scenes.mainScene.presenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.domain.external.arcore.ArCoreSession
import us.cyberstar.domain.external.factory.RenderableFactory
import us.cyberstar.domain.external.model.ArPostContentType
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.arFragment.view.ArFragmentImpl
import us.cyberstar.presentation.feature.cameraView.view.CameraFragment
import us.cyberstar.presentation.feature.cloudAnchor.view.CloudArFragment
import us.cyberstar.presentation.feature.postEdit.view.PostEditFragment
import us.cyberstar.presentation.feature.postOpenPhoto.view.PostOpenPhotoFragment
import us.cyberstar.presentation.feature.postOpenVideo.view.PostOpenVideoFragment
import us.cyberstar.presentation.feature.postPreview.presenter.PostPreviewPresenter
import us.cyberstar.presentation.feature.postPreview.view.PostPreviewFragment
import us.cyberstar.presentation.feature.postTargeting.view.PostTargetingFragment
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractor
import us.cyberstar.presentation.feature.scenes.mainScene.interactor.ArSceneInteractorImpl
import us.cyberstar.presentation.feature.scenes.mainScene.provider.PhotoContentMaker
import us.cyberstar.presentation.feature.scenes.mainScene.view.MainView
import us.cyberstar.presentation.feature.settingsDev.view.SettingsDevFragment
import javax.inject.Inject


@InjectViewState
class MainViewPresenter @Inject constructor(
    private val arCoreSession: ArCoreSession,
    private val renderableFactory: RenderableFactory,
    private val arSceneInteractor: ArSceneInteractor,
    private val photoContentMaker: PhotoContentMaker,
    private val schedulersProvider: SchedulersProvider
) : BasePresenter<MainView>() {

    val settingsDevFragment: SettingsDevFragment by lazy { SettingsDevFragment() }
    val arFragmentImpl: ArFragmentImpl by lazy { ArFragmentImpl() }
    val cloudArFragment: CloudArFragment by lazy { CloudArFragment() }

    override fun attachView(view: MainView) {
        super.attachView(view)
        Timber.d("MainViewPresenter init")
        arSceneInteractor.stateSubject
            .observeOn(schedulersProvider.ui())
            .subscribe({ onNavigate(it) },
                { Timber.e("arSceneInteractor $it") },
                { Timber.d("arSceneInteractor onComplete called") })
            .addTo(compositeDisposable) // TODO clear it where it needs to be
    }

    fun addCloudArFragmentImpl() {
        viewState.addSettingsView(cloudArFragment)
    }
    fun addSettingsView() {
        viewState.addSettingsView(settingsDevFragment)
    }

    fun addArSurfaceView() {
        Timber.d("tryToRunArCoreActivity")
        viewState.addArSurfaceView(arFragmentImpl)
    }

    //************************main view methods*********************************
    var postOpenVideoFragment: PostOpenVideoFragment? = null
    var postOpenPhotoFragment: PostOpenPhotoFragment? = null
    var cameraFragment: CameraFragment? = null
    var postPreviewFragment: PostPreviewFragment? = null
    var postTargetingFragment: PostTargetingFragment? = null
    var postEditFragment: PostEditFragment? = null
    //var postQuickFragment: PostQuickFragment? = null
    //var arCoreFragment: ArFragmentImpl? = null

    fun showCameraFragment() {
        /*if (cameraFragment?.isAdded == true) {
            changeFragmentVisibility(cameraFragment, true)
        } else {*/
        photoContentMaker.provideArView(arFragmentImpl)
        cameraFragment = CameraFragment()
        viewState.putFragment(cameraFragment!!)
        //}
    }

    fun getBundle(arPostModel: ArPostModel) = Bundle().apply {
        putParcelable(PostPreviewPresenter.bundleKey, arPostModel)
    }

    fun showPostPreviewFragment(arPostModel: ArPostModel) {
        Timber.d("showPostPreviewFragment $arPostModel")
        postPreviewFragment = PostPreviewFragment().apply {
            arguments = getBundle(arPostModel)
        }
        viewState.putFragment(postPreviewFragment!!)
        //}
    }

    /*fun showPostQuickFragment(arPostModel: ArPostModel) {
        postQuickFragment = PostQuickFragment().apply {
            getBundle(arPostModel)
        }
        viewState.putFragment(postQuickFragment!!)
    }*/

    fun showPostTargetingFragment(arPostModel: ArPostModel) {
        Timber.d("showPostTargetingFragment $arPostModel")
        postTargetingFragment = PostTargetingFragment().apply {
            arguments = getBundle(arPostModel)
        }
        viewState.putFragment(postTargetingFragment!!)
    }


    fun showPostEditFragment() {
        /*if(postEditFragment?.isAdded == true) {
            changeFragmentVisibility(postEditFragment, true)
        } else {*/
        postEditFragment = PostEditFragment()
        viewState.putFragment(postEditFragment!!)
        //}
    }

    //hiding underlined


    fun onNavigate(sceneNavParam: ArSceneInteractorImpl.SceneNavParam) {
        Timber.d("onNavigate ${sceneNavParam}")
        when (sceneNavParam.sceneState) {
            //TODO for debug
            ArSceneInteractorImpl.SceneState.Camera -> {
                //arCoreSession.onStart()
                //arFragmentImpl.onStart()
                //arFragmentImpl.arCoreSceneView.arSceneView.resume()
                showCameraFragment()
            }
            ArSceneInteractorImpl.SceneState.Preview -> {
                showPostPreviewFragment(sceneNavParam.arPostModel!!)
            }
            ArSceneInteractorImpl.SceneState.Targeting -> {
                showPostTargetingFragment(sceneNavParam.arPostModel!!)
            }
            ArSceneInteractorImpl.SceneState.Open -> {
                //arFragmentImpl.onPause()
                //arFragmentImpl.arCoreSceneView.arSceneView.pause()
                //  arCoreSession.onPause()
                showPostOpenFragment(sceneNavParam.arPostModel!!)
            }
            ArSceneInteractorImpl.SceneState.None -> {

            }
        }
    }

    private fun showPostOpenFragment(arPostModel: ArPostModel) {
        if (arPostModel.contentType() == ArPostContentType.PHOTO) {
            postOpenPhotoFragment = PostOpenPhotoFragment().apply {
                arguments = getBundle(arPostModel)
                viewState.addFragment(this, true)
            }
        } else {
            postOpenVideoFragment = PostOpenVideoFragment().apply {
                arguments = getBundle(arPostModel)
                viewState.addFragment(this, true)
            }
        }
    }


    private fun changeFragmentVisibility(fragment: Fragment?, isVisible: Boolean) {
        fragment?.view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }
}