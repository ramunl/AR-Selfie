package us.cyberstar.data

interface HttpErrorMessageParser {
    fun isHttpException(throwable: Throwable?): Boolean
    fun parseCode(throwable: Throwable?): Int?
    fun parseMessage(throwable: Throwable?): String?
}