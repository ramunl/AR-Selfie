package us.cyberstar.domain.external.arcore

import android.app.Activity

interface ArCoreSupportVerifier {
    interface ArCoreSupportCheckListener {
        fun isSupported()
    }

    fun isDeviceSupported(activity: Activity, sessionCreationListener: ArCoreSupportCheckListener)
}
