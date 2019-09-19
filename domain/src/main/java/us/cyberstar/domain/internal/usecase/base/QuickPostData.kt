package us.cyberstar.domain.internal.usecase.base

import android.graphics.Bitmap
import android.location.Location
import com.google.ar.core.Frame
import us.cyberstar.domain.internal.utils.HitTestResult

data class QuickPostData(
    val postLocation: Location
)