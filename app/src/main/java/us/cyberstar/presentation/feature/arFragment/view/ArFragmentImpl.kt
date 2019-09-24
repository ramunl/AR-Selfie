package us.cyberstar.presentation.feature.arFragment.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.cyber.ux.SceneFormNodeProvider
import com.cyber.ux.TransformationSystem
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.PlaneRenderer.MATERIAL_TEXTURE
import com.google.ar.sceneform.rendering.PlaneRenderer.MATERIAL_UV_SCALE
import com.google.ar.sceneform.rendering.Texture
import kotlinx.android.synthetic.main.layout_ar_buttons.view.*
import timber.log.Timber
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.feature.arFragment.presenter.ArPresenter
import us.cyberstar.presentation.feature.scenes.mainScene.arcore.scene.ArCoreSceneView
import us.cyberstar.presentation.helpers.changeVisibility
import javax.inject.Inject
import javax.inject.Provider


class ArFragmentImpl() : BaseArFragment(), ArView {

    @Inject
    lateinit var sceneFormNodeProvider: SceneFormNodeProvider
    @Inject
    lateinit var arCoreSceneView: ArCoreSceneView

    @Inject
    lateinit var providerPresenter: Provider<ArPresenter>

    @InjectPresenter
    lateinit var presenter: ArPresenter

    @ProvidePresenter
    fun providePresenter(): ArPresenter = providerPresenter.get()

    override fun layoutRes() = R.layout.sceneform_ux_fragment_layout

    override fun transformSystem(): TransformationSystem = sceneFormNodeProvider.transformationSystem

    override fun arSceneView(): ArSceneView = arCoreSceneView.arSceneView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  selfiePresenter.addTapListenerToView(arSceneView())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun showLoading(isLoading: Boolean) {
        rootView.progressBar.changeVisibility(isLoading)
    }

    lateinit var rootView: ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        return rootView
    }


}