package us.cyberstar.domain.internal.usecase.base

import android.location.Location

data class Ar3dModelPostData(
    val postLocation: Location,
    val anchorId: String
)