package us.cyberstar.framerecorder.media

class RecordFragment {
    var startTimestamp: Long = 0
    var endTimestamp: Long = 0

    val duration: Long
        get() = endTimestamp - startTimestamp
}