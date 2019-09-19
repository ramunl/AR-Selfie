package us.cyberstar.domain.internal.auth

import us.cyberstar.data.external.socket.BackendConnector
import us.cyberstar.data.entity.WebSocketReqWrapModel
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.domain.external.auth.AuthTokenRequestWrapper
import javax.inject.Inject

internal class AuthTokenRequestWrapperImpl @Inject constructor(
    private val backendConnector: BackendConnector
) : AuthTokenRequestWrapper {


    override fun confirmPhoneNumberWithCode(command: String, phoneNumber: String, code: Int) {
        val params = HashMap<String, String>().also {
            it["phone"] = phoneNumber
            it["code"] = code.toString()
        }
        backendConnector.callProcedure(WebSocketReqWrapModel(command, params, false))
    }

    override fun requestTokenBySMS(
        command: String,
        phoneNumber: String
    ) {
        val params = HashMap<String, String>().also {
            it["phone"] = phoneNumber
            it["via"] = "sms"
        }
        backendConnector.callProcedure(WebSocketReqWrapModel(command, params, false))
    }
}