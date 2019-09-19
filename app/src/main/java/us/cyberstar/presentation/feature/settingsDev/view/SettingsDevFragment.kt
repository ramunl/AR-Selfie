package us.cyberstar.presentation.feature.settingsDev.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_settings_dev.*
import kotlinx.android.synthetic.main.fragment_settings_dev.view.*
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.presentation.base.BaseFragment
import us.cyberstar.presentation.feature.settingsDev.presenter.SettingsDevPresenter
import javax.inject.Inject
import javax.inject.Provider


class SettingsDevFragment : SettingsDevView, BaseFragment() {

    override fun updateNodesInfo(info: String) {
        schedulersProvider.ui().scheduleDirect { nodeInfoTextView?.text = info }
    }


    @Inject
    lateinit var providerPresenter: Provider<SettingsDevPresenter>

    @InjectPresenter
    lateinit var presenter: SettingsDevPresenter

    @ProvidePresenter
    fun providePresenter(): SettingsDevPresenter = providerPresenter.get()

    @Inject
    lateinit var schedulersProvider: SchedulersProvider

    override fun layoutRes() = us.cyberstar.arcyber.R.layout.fragment_settings_dev


    override fun updateTelemetryInfo(telemetryInfo: String) {
        schedulersProvider.ui().scheduleDirect { telemetryInfoTextView?.text = telemetryInfo }
    }

    override fun updateMainInfo(planesInfo: String) {
        schedulersProvider.ui().scheduleDirect { planesInfoTextView?.text = planesInfo }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        with(rootView) {
            localRemoteRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (sessionStartStopSwitch.isChecked) {
                    sessionStartStopSwitch.isChecked = false
                }
                when (checkedId) {
                    localRadioBtn.id -> {
                        presenter.toggleRemoteLocalStorage(true)
                    }
                    grpcRadioBtn.id -> {
                        presenter.toggleRemoteLocalStorage(false)
                    }
                }
            }


            telemetrySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!sessionStartStopSwitch.isChecked) {
                        sessionStartStopSwitch.isChecked = true
                    }
                }
                if(isChecked) {
                    presenter.startTelemetry(telemtryVideoCheckBox.isChecked)
                } else {
                    presenter.stopTelemetry()
                }
            }

            loadWorldSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                presenter.toggleLoadWorld(isChecked)
            }

            /*videoRecordSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    //if we start video record we must start session to reset session ID
                    if(!sessionStartStopSwitch.isChecked) {
                        sessionStartStopSwitch.isChecked = true
                    }
                }
                photoPresenter.toggleVideoRecord(isChecked)
            }*/
            cleanScene.setOnClickListener { presenter.cleanWorld() }


            testOpenCV.setOnClickListener { presenter.runOpenCV(activity!!) }

        }
        return rootView
    }


    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }
}