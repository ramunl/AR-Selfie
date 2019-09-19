package us.cyberstar.domain.di.module

import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerActivity
import us.cyberstar.common.PerFragment
import us.cyberstar.data.external.socket.BackendConnector
import us.cyberstar.data.external.socket.BackendResponseListener
import us.cyberstar.data.external.socket.BackendSocketListener
import us.cyberstar.domain.external.auth.AuthTokenRequestWrapper
import us.cyberstar.domain.internal.auth.AuthTokenRequestWrapperImpl

@Module
class ProfileAuthModule {
    @Provides
    @PerActivity
    fun provideAuthTokenRequestWrapper(
        backendConnector: BackendConnector
    ): AuthTokenRequestWrapper =
        AuthTokenRequestWrapperImpl(backendConnector)
}
