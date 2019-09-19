package us.cyberstar.data.di.module

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import us.cyberstar.data.BuildConfig
import us.cyberstar.data.external.s3.S3TransferUtilityProvider
import us.cyberstar.data.internal.network.s3.S3TransferUtilityProviderImpl


@Module
class S3TransferModule {

    @Module
    companion object {
        const val UPLOAD_URL: String =
            BuildConfig.SCHEME + BuildConfig.SERVER_URL + BuildConfig.S3_PORT + "/upload"//"http://app.cyberstar.us:8971/upload"
        const val TOKEN: String =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJpYXQiOjE1NjIzNDQ3MjEsImV4cCI6MTU5MzQ0ODcyMX0.utBMaJX_RPH7UpmT06YkV0SUGpMad97cJL0fOeZKWIc"
    }


    @Provides
    @us.cyberstar.common.PerActivity
    fun provideTransferUtility(context: Context, gson: Gson): S3TransferUtilityProvider {
        return S3TransferUtilityProviderImpl(context, gson, UPLOAD_URL, TOKEN)
    }
}
