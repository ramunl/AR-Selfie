package us.cyberstar.data.external.socket

import okhttp3.WebSocketListener

abstract class BackendSocketListener : WebSocketListener() {
    abstract fun addBackendResponseListener(backendResponseListener: BackendResponseListener)
    abstract fun removeBackendResponseListener(backendResponseListener: BackendResponseListener)
}
