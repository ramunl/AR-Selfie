package us.cyberstar.presentation.feature.cameraView.view

import android.os.Bundle
import android.view.*
import android.view.View.VISIBLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_cam.*
import kotlinx.android.synthetic.main.fragment_cam.view.*
import kotlinx.android.synthetic.main.layout_record_view.view.photoButton
import kotlinx.android.synthetic.main.video_record_button.*
import kotlinx.android.synthetic.main.video_record_button.view.*
import timber.log.Timber
import us.cyberstar.domain.external.model.ArPostContentType
import us.cyberstar.domain.external.model.ArPostModel
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.cameraView.presenter.CameraPresenter
import us.cyberstar.presentation.helpers.changeVisibility
import us.cyberstar.presentation.views.CircularProgressBar
import javax.inject.Inject
import javax.inject.Provider


class CameraFragment : CameraView, BaseFragment() {

    override fun onStart() {
        super.onStart()
        presenter.startOpenPostUseCase()
    }

    override fun onStop() {
        super.onStop()
        presenter.stopOpenPostUseCase()
    }


    @Inject
    lateinit var providerPresenter: Provider<CameraPresenter>

    @InjectPresenter
    lateinit var presenter: CameraPresenter

    @ProvidePresenter
    fun providePresenter(): CameraPresenter = providerPresenter.get()


    override fun layoutRes() = us.cyberstar.arcyber.R.layout.fragment_cam


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // selfiePresenter.addTapListenerToView(rootView)

        recordProgressBar.progressListener =
            object : CircularProgressBar.ProgressListener {
                override fun onStop() {
                    presenter.stopVideoRecord()
                }

                override fun onStart() {
                }
            }
        togglePhotoVideoMode(false)

        photoVideoBtn.setOnClickListener { presenter.takePicture() }

        photoVideoBtn.setOnLongClickListener {
            Timber.d("OnLongClick")
            if (presenter.startVideoRecord()) {
                addOnTouchUpListener()
                recordProgressBar.startCounter()
                togglePhotoVideoMode(true)
            }
            true
        }


        testPost.setOnClickListener {
            presenter.testVideoPostOpen()
        }

    }


    private fun removeOnTouchUpListener() {
        photoVideoBtn.setOnTouchListener(null)
    }

    private fun addOnTouchUpListener() {
        photoVideoBtn.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    Timber.d("MotionEvent.ACTION_UP ")
                    if (videoBtn.visibility == VISIBLE) {
                        recordProgressBar.stopCounter()
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    Timber.d("MotionEvent.ACTION_DOWN ")
                }
            }
            true
        }
    }

    override fun togglePhotoVideoMode(isVideo: Boolean) {
        if (!isVideo) {
            removeOnTouchUpListener()
        }
        photoBtn.changeVisibility(!isVideo)
        videoBtn.changeVisibility(isVideo)
    }
}