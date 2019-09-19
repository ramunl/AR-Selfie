package us.cyberstar.data.internal.network.socket

import android.content.SharedPreferences
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import us.cyberstar.data.external.socket.BackendConnector
import javax.inject.Inject
import org.json.JSONObject
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.entity.WebSocketReqWrapModel
import us.cyberstar.data.ext.appToken
import us.cyberstar.data.ext.setAppToken
import us.cyberstar.data.external.socket.BackendCommandHandler
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.data.model.BackendResponseModel
import java.lang.Exception
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.scheduleAtFixedRate


//TODO refactor it to separate in several providers
internal class BackendConnectorImpl @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val sharedPreferences: SharedPreferences,
    private val url: String,
    private val port: Int,
    private val backendSocketListener: BackendSocketListener,
    private val backendCommandHandler: BackendCommandHandler
) : BackendConnector {
    var isSocketConnected = false
    private var requestQueue: ArrayBlockingQueue<WebSocketReqWrapModel> = ArrayBlockingQueue(100, true)
    var webSocketCurrent: WebSocket? = null
    var timer: Timer? = null
    //val TIMEOUT_TO_CLOSE_SOCKET = 5000
    //var timeoutCheckPoint = 0L

    private fun stopQueueMonitoring() {
        timer?.cancel()
        timer = null
    }

    private fun startQueueMonitoring() {
        if (timer == null) {

            timer = Timer().apply {
                scheduleAtFixedRate(0, 1000) {
                    if (requestQueue.isNotEmpty()) {
                        if (isSocketConnected) {
                            doCallProcedure(requestQueue.poll())
                        } else {
                            connect()
                        }
                    }/* else {
                        if (timeoutCheckPoint == 0L) {
                            timeoutCheckPoint = System.currentTimeMillis()
                        } else if (System.currentTimeMillis() - timeoutCheckPoint >= TIMEOUT_TO_CLOSE_SOCKET) {
                            timeoutCheckPoint = 0L
                            disconnect()
                            stopQueueMonitoring()
                        }
                    }*/
                }
            }
        }
    }

    private var okHttpClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    }

    private fun buildRequest() = Request.Builder()
        .url("$url:$port")
        .build()

    fun connect() {
        webSocketCurrent?.let {
           // Timber.d("WebSocket is not null.")
        } ?: {
            webSocketCurrent = okHttpClient.newWebSocket(buildRequest(), backendSocketListener)
            backendSocketListener.addBackendResponseListener(backendResponseListener)
        }()
    }

    private val backendResponseListener = object : BackendResponseListener {
        override fun onOpen() {
            isSocketConnected = true
        }

        override fun onResponseReady(respModel: BackendResponseModel) {
            if(respModel.isSuccess) {
                respModel.token?.let {
                    sharedPreferences.setAppToken(it)
                }
            }
            disconnect()
            stopQueueMonitoring()
        }
        override fun onClosed(code: Int, reason: String) {
            doDisconnect()
        }
    }

    private fun disconnect() {
        backendSocketListener.removeBackendResponseListener(backendResponseListener)
        doDisconnect()
        Timber.d("webSocket:disconnect")
    }

    private fun doDisconnect() {
        webSocketCurrent?.close(1000,"")
        webSocketCurrent = null
        isSocketConnected = false
    }


    override fun callProcedure(webSocketReqWrapModel: WebSocketReqWrapModel) {
        requestQueue.add(webSocketReqWrapModel)
        startQueueMonitoring()
    }

    private fun doCallProcedure(webSocketReqWrapModel: WebSocketReqWrapModel) {
        with(webSocketReqWrapModel) {
            if (needAuth) {
                if (sharedPreferences.appToken()!!.isNotEmpty()) {
                    params["token"] = sharedPreferences.appToken()!!
                } else {
                    throw Exception("Auth token is empty, you must set it before use auth for requests!")
                }
            }
            val paramsJsonObject = JSONObject().apply {
                for (param in params) {
                    put(param.key, param.value)
                }
            }
            val newReqId = uniqueRequestID()
            val jsonObject = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", newReqId.toString())
                put("method", requestMethod)
                put("params", paramsJsonObject)
            }
            val res = webSocketCurrent?.send(jsonObject.toString())
            backendCommandHandler.addRequestToTempMap(newReqId, requestMethod)

            Timber.d("webSocket:send res = $res")
        }
    }

    private var requestID = 0

    private fun uniqueRequestID() = ++requestID

}