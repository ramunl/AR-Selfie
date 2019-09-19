package us.cyberstar.data.external.socket

import us.cyberstar.data.model.BackendResponseModel

interface BackendCommandHandler {
    fun addRequestToTempMap(reqId: Int, requestMethod: String)
    fun getRequestNameById(reqId: Int): String
}
