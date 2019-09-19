package us.cyberstar.presentation.feature.postOpenPhoto.view

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_post_open_photo.*
import kotlinx.android.synthetic.main.fragment_post_open_video.*
import kotlinx.android.synthetic.main.fragment_post_open_video.closeOpenScreen
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postOpenPhoto.presenter.PostOpenPhotoPresenter
import javax.inject.Inject
import javax.inject.Provider

class PostOpenPhotoFragment : PostOpenPhotoView, BaseFragment() {

    override fun showPhoto(mediaPath: String) {
        activity?.let {
            Glide.with(this)
                .load(mediaPath).into(photoContent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeOpenScreen.setOnClickListener {
            photoPresenter.popBackStack(activity)
        }
        photoPresenter.setupViews(savedInstanceState?.let { it } ?: arguments!!)
    }

    @Inject
    lateinit var providerPhotoPresenter: Provider<PostOpenPhotoPresenter>

    @InjectPresenter
    lateinit var photoPresenter: PostOpenPhotoPresenter

    @ProvidePresenter
    fun providePresenter(): PostOpenPhotoPresenter = providerPhotoPresenter.get()

    override fun layoutRes() = R.layout.fragment_post_open_photo
}