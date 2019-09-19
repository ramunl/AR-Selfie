package us.cyberstar.data.external.socket

import us.cyberstar.data.model.BackendResponseModel

interface BackendResponseListener {
    fun onOpen()
    fun onClosed(code: Int, reason: String)
    fun onResponseReady(respModel: BackendResponseModel)
}