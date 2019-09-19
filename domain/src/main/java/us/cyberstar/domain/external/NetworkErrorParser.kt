package us.cyberstar.domain.external

import us.cyberstar.domain.external.common.ErrorParam

interface NetworkErrorParser {
    fun getError(throwable: Throwable?): ErrorParam
}