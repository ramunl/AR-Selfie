package us.cyberstar.domain.external.arcore

import com.google.ar.core.Config

interface ArCoreConfig {
    fun initConfig()
    var config: Config?
    fun switchCameraMode(photoVideoMode: Boolean)
}