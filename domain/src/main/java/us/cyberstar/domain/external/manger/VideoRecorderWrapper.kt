package us.cyberstar.domain.external.manger

import us.cyberstar.common.utils.ImageCopy

interface VideoRecorderWrapper {
    fun isRecording(): Boolean
    fun toggleRecorder(isOn: Boolean)
    fun recordFrame(imageCopy: ImageCopy)
    fun duration(): Long
    fun framesCount(): Int
    fun videoStartTimeStamp(): Long
    fun saveVideoToFile()
    fun videoRecordInfo(): String
}