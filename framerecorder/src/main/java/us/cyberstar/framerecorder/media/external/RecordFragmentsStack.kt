package us.cyberstar.framerecorder.media.external

import us.cyberstar.framerecorder.media.RecordFragment

interface RecordFragmentsStack {
    fun push(recordFragment: RecordFragment)
    fun pop(): RecordFragment?
    fun clear()
    fun peek(): RecordFragment?
    fun calculateTotalRecordedTime(): Long
}