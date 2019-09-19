package us.cyberstar.data.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.PerApp
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.external.s3.S3Cache
import us.cyberstar.data.internal.network.grpc.SessionIdProviderImpl
import us.cyberstar.data.storage.ArEntityCacheImpl
import us.cyberstar.data.internal.network.s3.S3CacheImpl


@Module
class StorageModule {

    @PerApp
    @Provides
    fun provideSessionId(): SessionIdProvider {
        return SessionIdProviderImpl()
    }

    @PerApp
    @Provides
    fun provideArEntityCacheManager(context: Context): ArEntityCache =
        ArEntityCacheImpl(context)

    @PerApp
    @Provides
    fun provideS3CacheManager(context: Context, sessionIdProvider: SessionIdProvider): S3Cache =
        S3CacheImpl(context, sessionIdProvider)

}
