package us.cyberstar.data.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import us.cyberstar.data.BuildConfig
import us.cyberstar.data.HttpErrorMessageParser
import us.cyberstar.data.errorHandler.HttpErrorMessageParserImpl

@Module
class GrpcModule {

    @Module
    companion object {
        const val PORT_ARM: Int = 50052
        const val PORT: Int = 50051
        const val BASE_URL: String = BuildConfig.SERVER_URL//"arm.cyberstar.us"
    }

    @us.cyberstar.common.PerApp
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()


    @us.cyberstar.common.PerApp
    @Provides
    fun provideHttpErrorMessageParser(gson: Gson): HttpErrorMessageParser = HttpErrorMessageParserImpl(gson)

}
