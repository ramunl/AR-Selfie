package us.cyberstar.domain.internal.arcore

import us.cyberstar.common.utils.timeNow
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import java.lang.Exception

class ArSessionStartTimeProviderImpl() : ArSessionStartTimeProvider {
    override var startTimeStamp: Long = 0L
        get() = { if (field == 0L) throw Exception("startSession() must be called before!!!") else field }()
        set(value) {
            field = value
        }
    override var stopTimeStamp: Long = 0L
        get() = { if (field == 0L) throw Exception("stopSession() must be called before!!!") else field }()
        set(value) {
            field = value
        }

    override fun startSession() {
        startTimeStamp = timeNow()
    }

    override fun stopSession() {
        stopTimeStamp = timeNow()
    }

    override fun cleanTimeStamps() {
        startTimeStamp = 0L
        stopTimeStamp = 0L
    }
}