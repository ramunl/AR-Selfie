package us.cyberstar.presentation.feature.settingsDev.view

import com.arellomobile.mvp.MvpView

interface SettingsDevView : MvpView {
    fun updateMainInfo(planesInfo: String)
    fun updateTelemetryInfo(telemetryInfo: String)
    fun updateNodesInfo(info: String)
}