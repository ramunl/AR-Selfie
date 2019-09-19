package us.cyberstar.common.internal

import android.content.Context
import androidx.annotation.StringRes
import us.cyberstar.common.external.ResRepo
import javax.inject.Inject

internal class ResRepoImpl @Inject constructor(
    private val context: Context
) : ResRepo {
    override fun getString(@StringRes id: Int): String = context.resources.getString(id)
}