package us.cyberstar.data.internal.network.socket

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import timber.log.Timber
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.data.external.socket.ResponseParser
import us.cyberstar.data.model.BackendResponseModel
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.SynchronousQueue
import javax.inject.Inject

class BackendSocketListenerImpl @Inject constructor(
    private val responseParser: ResponseParser
) : BackendSocketListener() {

    var backendResponseListeners = ArrayBlockingQueue<BackendResponseListener>(100)

    override fun addBackendResponseListener(backendResponseListener: BackendResponseListener) {
        backendResponseListeners.add(backendResponseListener)
    }

    override fun removeBackendResponseListener(backendResponseListener: BackendResponseListener) {
        backendResponseListeners.remove(backendResponseListener)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        onConnected()
        Timber.d("webSocket:onOpen body:${response.body()} code:${response.code()}")
    }


    /** Invoked when a text (type `0x1`) message has been received.  */
    override fun onMessage(webSocket: WebSocket, text: String) {
        Timber.d("webSocket:onMessage $text")
        val respModel = responseParser.parse(text)
        onResponseReady(respModel)
    }

    private fun onConnected() {
        backendResponseListeners.forEach { it.onOpen() }
    }

    private fun onSocketDisconnected(code: Int, reason: String) {
        backendResponseListeners.forEach { it.onClosed(code, reason) }
    }

    private fun onResponseReady(respModel: BackendResponseModel) {
        backendResponseListeners.forEach { it.onResponseReady(respModel) }
    }

    /** Invoked when a binary (type `0x2`) message has been received.  */
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Timber.d("webSocket:onMessage $bytes")
        val respModel = responseParser.parse(bytes.toString())
        onResponseReady(respModel)
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be
     * transmitted.
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        onSocketDisconnected(code, reason)
        Timber.d("webSocket:onClosing code = $code reason:$reason")
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onSocketDisconnected(code, reason)
        Timber.d("webSocket:onSocketDisconnected code = $code reason:$reason")
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onSocketDisconnected(-1, t.toString())
        Timber.e("webSocket:onFailure code = $t response:${response.toString()}")
    }
}