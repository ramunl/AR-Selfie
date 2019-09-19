package us.cyberstar.framerecorder.media.external

import us.cyberstar.common.utils.ImageCopy

interface VideoRecordThread {
    fun onPreviewFrame(imageCopy: ImageCopy)
    fun startThread()
    fun stopThread()
    fun isRunning(): Boolean
    var mTotalProcessFrameTime: Long
    var mFrameRecordedCount: Int
}