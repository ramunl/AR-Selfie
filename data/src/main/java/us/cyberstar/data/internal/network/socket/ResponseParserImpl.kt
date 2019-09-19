package us.cyberstar.data.internal.network.socket

import com.google.gson.Gson
import com.google.gson.JsonObject
import us.cyberstar.data.external.socket.BackendCommandHandler
import us.cyberstar.data.external.socket.ResponseParser
import us.cyberstar.data.model.BackendResponseModel
import us.cyberstar.data.model.CODE_CONFIRM
import us.cyberstar.data.model.TOKEN_REQ_BY_SMS
import us.cyberstar.data.model.UNKNOWN_COMMAND
import javax.inject.Inject

class ResponseParserImpl @Inject constructor(
    private val gson: Gson,
    private val backendCommandHandler: BackendCommandHandler
) : ResponseParser {

    override fun parse(response: String): BackendResponseModel {
        val json = gson.fromJson(response, JsonObject::class.java)
        val result = json.getAsJsonObject("result")
        val id = json.getAsJsonPrimitive("id").asInt
        val command = backendCommandHandler.getRequestNameById(id)
        return when (command) {
            TOKEN_REQ_BY_SMS -> {
                parseTokenResponse(command, result)
            }
            CODE_CONFIRM -> {
                parseCodeConfirmResponse(command, result)
            }
            else -> {
                BackendResponseModel(UNKNOWN_COMMAND)
            }
        }
    }

    private fun parseCodeConfirmResponse(command: String, result: JsonObject): BackendResponseModel {
        val respModel = BackendResponseModel(command)
        with(result) {
            getAsJsonObject("result")?.apply {
                val isSuccess = getAsJsonPrimitive("success")?.asBoolean
                isSuccess?.let {
                    respModel.isSuccess = it
                    if (it) {
                        getAsJsonObject("data")?.apply {
                            respModel.token = get("token")?.asString
                        }
                    }
                }
            }
            return respModel
        }
    }

    private fun parseTokenResponse(command: String, result: JsonObject): BackendResponseModel {
        val respModel = BackendResponseModel(command)
        with(result) {
            val resultInternal = getAsJsonObject("result")
            with(resultInternal) {
                val isSuccess = getAsJsonPrimitive("success").asBoolean
                if (isSuccess) {
                    respModel.secondsToExpire = getAsJsonPrimitive("seconds_to_expire")?.asInt
                }
                respModel.responseMsg = getAsJsonPrimitive("message")?.asString
                respModel.isSuccess = isSuccess
            }
        }
        return respModel
    }
}