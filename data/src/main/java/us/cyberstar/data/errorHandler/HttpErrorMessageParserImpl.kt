package us.cyberstar.data.errorHandler

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import us.cyberstar.data.HttpErrorMessageParser
import javax.inject.Inject


internal class HttpErrorMessageParserImpl @Inject constructor(private val gson: Gson) :
    HttpErrorMessageParser {

    override fun isHttpException(throwable: Throwable?) = throwable is HttpException

    override fun parseMessage(throwable: Throwable?): String? {
        return if (isHttpException(throwable)) {
            val errorBody = (throwable as HttpException).response().errorBody()?.string()
            return try {
                gson.fromJson(errorBody, JsonObject::class.java)
                    .get("non_field_errors")
                    .asString
            } catch (e: Exception) {
                errorBody?.let {
                    if (it.isNotEmpty()) errorBody
                    else null
                }

            }

        } else null
    }

    override fun parseCode(throwable: Throwable?): Int? {
        return if (isHttpException(throwable)) {
            (throwable as HttpException).response().code()
        } else null
    }

}
