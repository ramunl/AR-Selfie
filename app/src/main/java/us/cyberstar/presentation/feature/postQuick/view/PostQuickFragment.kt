package us.cyberstar.presentation.feature.postQuick.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_post_targeting_or_quick.view.*
import us.cyberstar.arcyber.R
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postQuick.presenter.PostQuickPresenter
import javax.inject.Inject
import javax.inject.Provider


class PostQuickFragment : PostQuickView, BaseFragment() {

    @Inject
    lateinit var providerPresenter: Provider<PostQuickPresenter>

    @InjectPresenter
    lateinit var presenter: PostQuickPresenter

    @ProvidePresenter
    fun providePresenter(): PostQuickPresenter = providerPresenter.get()

    override fun layoutRes() = R.layout.fragment_post_targeting_or_quick

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        presenter.startUseCase(arguments!!)

        rootView.confirmArPostCreateBtn.visibility = GONE

        rootView.confirmArPostCreateBtn.setOnClickListener {
           // photoPresenter.confirmTargetingPostCreation()
        }

        rootView.closePostingScreen.setOnClickListener {
            presenter.showCameraFragment()
            presenter.closePost()
        }
        return rootView
    }
}