package us.cyberstar.common.utils

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue


object MiscUtils {
    fun isOrientationLandscape(context: Context): Boolean {
        val isOrientationLandscape: Boolean
        val orientation = context.resources.configuration.orientation
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> isOrientationLandscape = true
            Configuration.ORIENTATION_PORTRAIT -> isOrientationLandscape = false
            else -> isOrientationLandscape = false
        }
        return isOrientationLandscape
    }

    fun dpToPixelConvertor(dp: Float, context: Context): Float {
        val r = context.resources

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }
}
