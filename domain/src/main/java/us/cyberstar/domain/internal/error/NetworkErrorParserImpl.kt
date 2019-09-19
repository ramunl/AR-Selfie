package us.cyberstar.domain.internal.error

import us.cyberstar.data.HttpErrorMessageParser
import us.cyberstar.domain.R
import us.cyberstar.domain.external.NetworkErrorParser
import javax.inject.Inject

internal class NetworkErrorParserImpl @Inject constructor(
    private val resRepo: us.cyberstar.common.external.ResRepo,
    private val httpException: HttpErrorMessageParser
) : NetworkErrorParser {
    override fun getError(throwable: Throwable?): ErrorParamImpl {
        var errCode: Int? = 0
        var errorMsg: String

        val errDefault = resRepo.getString(R.string.network_error)

        if (httpException.isHttpException(throwable)) {
            errCode = httpException.parseCode(throwable)
            errorMsg = httpException.parseMessage(throwable) ?: errDefault
        } else {
            errorMsg = throwable?.localizedMessage ?: errDefault
        }
        return ErrorParamImpl(errCode, errorMsg)
    }

}