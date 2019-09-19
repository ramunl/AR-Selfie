package us.cyberstar.framerecorder.media.external

import DecoFrame

interface FrameRecorder {
    fun startRecorder()
    fun stopRecorder()
    fun getTimestamp(): Long?
    fun setTimestamp(timestamp: Long)
    fun record(frame: DecoFrame)
    fun createRecorder(uniqueId: String)
    fun isRecording(): Boolean
    var videoStartTimeStamp: Long
    fun releaseRecorder()
    fun doResume()
}