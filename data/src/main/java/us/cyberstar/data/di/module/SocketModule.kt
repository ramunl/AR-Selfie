package us.cyberstar.data.di.module

import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.BuildConfig
import us.cyberstar.data.external.socket.*
import us.cyberstar.data.internal.network.socket.BackendCommandHandlerImpl
import us.cyberstar.data.internal.network.socket.BackendConnectorImpl
import us.cyberstar.data.internal.network.socket.BackendSocketListenerImpl
import us.cyberstar.data.internal.network.socket.ResponseParserImpl

@Module
class SocketModule {

    @Module
    companion object {
        const val PORT: Int = 5000
        const val BASE_URL: String = "ws://" + BuildConfig.SERVER_URL
    }

    @Provides
    @PerActivity
    fun provideBackendConnector(
        backendCommandHandler: BackendCommandHandler,
        schedulersProvider: SchedulersProvider,
        sharedPreferences: SharedPreferences,
        backendSocketListener: BackendSocketListener
    ): BackendConnector {
        return BackendConnectorImpl(
            schedulersProvider,
            sharedPreferences,
            BASE_URL,
            PORT,
            backendSocketListener,
            backendCommandHandler
        )
    }

    @Provides
    @PerActivity
    fun provideBackendSocketListener(
        responseParser: ResponseParser
    ): BackendSocketListener {
        return BackendSocketListenerImpl(responseParser)
    }

    @Provides
    @PerActivity
    fun provideResponseParser(gson: Gson, backendCommandHandler: BackendCommandHandler): ResponseParser {
        return ResponseParserImpl(gson, backendCommandHandler)
    }

    @Provides
    @PerActivity
    fun provideBackendCallbackHandler(): BackendCommandHandler {
        return BackendCommandHandlerImpl()
    }

}
