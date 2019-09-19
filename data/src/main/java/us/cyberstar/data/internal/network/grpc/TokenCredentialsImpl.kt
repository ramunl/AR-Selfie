package us.cyberstar.data.internal.network.grpc

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Status
import timber.log.Timber
import us.cyberstar.data.SessionIdProvider
import java.util.concurrent.Executor


internal class TokenCredentialsImpl(
    private val token: String,
    private val sessionIdProvider: SessionIdProvider
) : CallCredentials() {
    override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
        appExecutor.execute {
            try {
                val metadata = Metadata()
                val tokenKey = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER)
                val sessionKey = Metadata.Key.of("session", Metadata.ASCII_STRING_MARSHALLER)
                metadata.put(tokenKey, token)
                metadata.put(sessionKey, sessionIdProvider.sessionId())
                applier.apply(metadata)
            } catch (e: Throwable) {
                applier.fail(Status.UNAUTHENTICATED.withCause(e))
                Timber.e("applyRequestMetadata failed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! with:$e")
            }
        }
    }

    override fun thisUsesUnstableApi() {
        // intentionally blank
    }

}