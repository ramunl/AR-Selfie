package us.cyberstar.presentation.feature.postPreview.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_post_preview.*
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postPreview.presenter.PostPreviewPresenter
import us.cyberstar.presentation.helpers.changeVisibility
import javax.inject.Inject
import javax.inject.Provider


class PostPreviewFragment : PostPreviewView, BaseFragment() {


    @Inject
    lateinit var providerPresenter: Provider<PostPreviewPresenter>

    @InjectPresenter
    lateinit var presenter: PostPreviewPresenter

    @ProvidePresenter
    fun providePresenter(): PostPreviewPresenter = providerPresenter.get()

    override fun layoutRes() = R.layout.fragment_post_preview

    override fun showPhotoPreview(lastBitmap: Bitmap?) {
        //photoPreview.setImageBitmap(lastBitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setupViews(arguments!!)

        quickPostBtn.setOnClickListener {
            presenter.createQuickPost(titleEditText.text.toString())
        }

        targetingModeBtn.setOnClickListener {
            presenter.showTargetingView(titleEditText.text.toString())
        }

        closePreviewScreen.setOnClickListener {
            photoPreview.changeVisibility(false)
            presenter.showCameraView()
        }

    }

}