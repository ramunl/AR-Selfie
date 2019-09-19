package us.cyberstar.data.di.module

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.grpc.CallCredentials
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.*
import us.cyberstar.data.ext.appToken
import us.cyberstar.data.external.grpc.GrpcArService
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.internal.network.grpc.*


@Module
class GrpcSessionModule {


    @Provides
    @us.cyberstar.common.PerActivity
    fun provideTokenCredentials(sessionIdProvider: SessionIdProvider, preferences: SharedPreferences): CallCredentials {
        return TokenCredentialsImpl(preferences.appToken()!!, sessionIdProvider)
    }


    @Provides
    @us.cyberstar.common.PerActivity
    fun provideProtoArServiceProvider(
        credentials: CallCredentials,
        sessionIdProvider: SessionIdProvider
    ): ProtoServiceProvider {
        return ProtoServiceProviderImpl(credentials)
    }

    @Provides
    @us.cyberstar.common.PerActivity
    fun provideGrpcTelemetryService(
        snackBarProvider: SnackBarProvider,
        protoServiceProvider: ProtoServiceProvider
    ): GrpcTelemetryService {
        return GrpcTelemetryServiceImpl(snackBarProvider, protoServiceProvider)
    }

    @Provides
    @us.cyberstar.common.PerActivity
    fun provideGrpcArService(
        snackBarProvider: SnackBarProvider,
        protoServiceProvider: ProtoServiceProvider
    ): GrpcArService {
        return GrpcArServiceImpl(snackBarProvider, protoServiceProvider)
    }

}
