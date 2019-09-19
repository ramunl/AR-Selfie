package us.cyberstar.presentation.feature.postEdit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postEdit.presenter.PostEditPresenter

import javax.inject.Inject
import javax.inject.Provider


class PostEditFragment : PostEditView, BaseFragment() {

    @Inject
    lateinit var providerPresenter: Provider<PostEditPresenter>

    @InjectPresenter
    lateinit var presenter: PostEditPresenter

    @ProvidePresenter
    fun providePresenter(): PostEditPresenter = providerPresenter.get()

    override fun layoutRes() = us.cyberstar.arcyber.R.layout.fragment_post_edit

}