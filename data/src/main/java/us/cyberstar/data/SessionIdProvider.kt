package us.cyberstar.data


interface SessionIdProvider {
    fun sessionId(): String?
    fun resetUUID()
}