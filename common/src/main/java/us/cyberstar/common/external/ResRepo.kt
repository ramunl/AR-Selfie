package us.cyberstar.common.external

import androidx.annotation.StringRes

interface ResRepo {
    fun getString(@StringRes id: Int): String
}