package us.cyberstar.domain.external

import android.content.Context
import android.opengl.GLSurfaceView

abstract class ArGlSurfaceView(context: Context?) : GLSurfaceView(context) {
    abstract fun onActivityResume()
    abstract fun onActivityPause()
}