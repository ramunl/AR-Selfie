/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.cyberstar.presentation.feature.postOpenVideo.view

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_post_open_video.*

import javax.inject.Inject

import us.cyberstar.arcyber.R
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.postOpenVideo.presenter.PostOpenVideoPresenter
import javax.inject.Provider

class PostOpenVideoFragment : BaseFragment(), PostOpenVideoView, MediaPlayer.OnPreparedListener {

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        videoView.start()
    }

    override fun showVideo(mediaPath: String) {
        videoView.setVideoURI(Uri.parse(mediaPath));
    }


    @Inject
    lateinit var schedulersProvider: SchedulersProvider


    @Inject
    lateinit var providerVideoPresenter: Provider<PostOpenVideoPresenter>

    @InjectPresenter
    lateinit var photoPresenter: PostOpenVideoPresenter

    @ProvidePresenter
    fun providePresenter(): PostOpenVideoPresenter = providerVideoPresenter.get()


    override fun onStop() {
        super.onStop()
        videoView.reset()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun initVideoView() {
            photoPresenter.setupViews(savedInstanceState?.let { it } ?: arguments!!)
        }

        initVideoView()

        closeOpenScreen.setOnClickListener {
            photoPresenter.popBackStack(activity)
        }

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener {
        }
    }

    override fun layoutRes() = R.layout.fragment_post_open_video

}
