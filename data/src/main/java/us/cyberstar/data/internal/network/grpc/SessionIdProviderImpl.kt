package us.cyberstar.data.internal.network.grpc

import timber.log.Timber
import us.cyberstar.data.SessionIdProvider
import java.util.*

internal class SessionIdProviderImpl : SessionIdProvider {
    var uniqueId: String? = null

    override fun sessionId(): String? {
        return uniqueId
    }

    override fun resetUUID() {
        uniqueId = UUID.randomUUID().toString()
        Timber.d("resetUUID with sessionId = $uniqueId")
    }
}