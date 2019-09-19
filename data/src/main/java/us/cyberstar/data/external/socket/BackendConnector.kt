package us.cyberstar.data.external.socket

import us.cyberstar.data.entity.WebSocketReqWrapModel

interface BackendConnector {
    fun callProcedure(webSocketReqWrapModel: WebSocketReqWrapModel)

}
