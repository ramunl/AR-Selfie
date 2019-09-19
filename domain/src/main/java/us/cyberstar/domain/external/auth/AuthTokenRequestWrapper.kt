package us.cyberstar.domain.external.auth

import us.cyberstar.data.external.socket.BackendResponseListener

interface AuthTokenRequestWrapper {
    fun requestTokenBySMS(command: String, phoneNumber: String)
    fun confirmPhoneNumberWithCode(command: String, phoneNumber: String, code: Int)
}
