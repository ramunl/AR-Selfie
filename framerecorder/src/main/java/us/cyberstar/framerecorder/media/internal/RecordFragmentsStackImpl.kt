package us.cyberstar.framerecorder.media.internal

import us.cyberstar.framerecorder.media.RecordFragment
import us.cyberstar.framerecorder.media.external.RecordFragmentsStack
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

internal class RecordFragmentsStackImpl : RecordFragmentsStack {

    private val recordFragments = CopyOnWriteArraySet<RecordFragment>()

    override fun push(recordFragment: RecordFragment) {
        recordFragments.add(recordFragment)
    }

    override fun pop(): RecordFragment? {
        var res: RecordFragment? = null
        if (recordFragments.isNotEmpty()) {
            res = recordFragments.last()
            recordFragments.remove(res)
        }
        return res
    }

    override fun peek(): RecordFragment? {
        return if(recordFragments.isNotEmpty()) {
            recordFragments.last()
        } else {
            null
        }
    }

    override fun clear() {
        recordFragments.clear()

    }

    override fun calculateTotalRecordedTime(): Long {
        var recordedTime: Long = 0
        for (recordFragment in recordFragments) {
            recordedTime += recordFragment.duration
        }
        return recordedTime
    }
}