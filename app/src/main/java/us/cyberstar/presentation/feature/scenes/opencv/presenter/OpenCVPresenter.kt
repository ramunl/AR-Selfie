package us.cyberstar.presentation.feature.scenes.opencv.presenter

import com.arellomobile.mvp.InjectViewState
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.scenes.opencv.view.OpenCVView
import javax.inject.Inject


/*
This the launch screen photoPresenter. Here we check:
 1)Does our device support arCore?
 2)Do we want to run either DEV or PROD mode?
 */
@InjectViewState
class OpenCVPresenter @Inject constructor() : BasePresenter<OpenCVView>() {

}