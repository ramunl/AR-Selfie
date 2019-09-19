package us.cyberstar.data.internal.network.socket

import android.util.SparseArray
import us.cyberstar.data.external.socket.BackendCommandHandler

class BackendCommandHandlerImpl : BackendCommandHandler {

    override fun getRequestNameById(reqId: Int): String {
        return requestToTempMap.get(reqId)
    }

    private val requestToTempMap = SparseArray<String>()
    override fun addRequestToTempMap(reqId: Int, requestMethod: String) {
        requestToTempMap.append(reqId, requestMethod)
    }
}