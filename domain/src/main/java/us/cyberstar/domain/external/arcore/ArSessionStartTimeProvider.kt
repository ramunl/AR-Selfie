package us.cyberstar.domain.external.arcore

interface ArSessionStartTimeProvider {
    fun startSession()
    fun stopSession()
    var startTimeStamp: Long
    var stopTimeStamp: Long
    fun cleanTimeStamps()
}