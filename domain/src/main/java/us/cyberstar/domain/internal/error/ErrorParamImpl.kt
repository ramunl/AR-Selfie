package us.cyberstar.domain.internal.error

import us.cyberstar.domain.external.common.ErrorParam

internal class ErrorParamImpl(
    val errCode: Int?,
    val errorMsg: String = ""
) : ErrorParam {
    override fun errCode(): Int? = errCode
    override fun errorMsg(): String = errorMsg
}