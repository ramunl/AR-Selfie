package us.cyberstar.domain.external.arcore

import com.google.ar.core.Session

interface ArCoreSession {
    val session: Session
    fun onPause()
    fun onResume()
    fun setupFlavor(flavor: String)
}