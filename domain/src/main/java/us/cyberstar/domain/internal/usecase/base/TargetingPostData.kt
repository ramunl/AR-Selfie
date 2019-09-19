package us.cyberstar.domain.internal.usecase.base

import android.graphics.Bitmap
import android.location.Location
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import us.cyberstar.domain.internal.utils.HitTestResult

data class TargetingPostData(
    val postLocation: Location,
    val arFrameBitmap: Bitmap//,
    //val snapshotLocalPath: String
)