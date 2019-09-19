package us.cyberstar.domain.di.module

import dagger.Module
import dagger.Provides
import us.cyberstar.common.external.ResRepo
import us.cyberstar.data.HttpErrorMessageParser
import us.cyberstar.domain.external.NetworkErrorParser
import us.cyberstar.domain.external.common.ErrorParam
import us.cyberstar.domain.internal.error.ErrorParamImpl
import us.cyberstar.domain.internal.error.NetworkErrorParserImpl

@Module
class StaticModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun provideErrorParam(errCode: Int?, errorMsg: String): ErrorParam = ErrorParamImpl(errCode, errorMsg)


        @Provides
        @JvmStatic
        fun provideErrorHandler(
            resRepo: ResRepo,
            httpException: HttpErrorMessageParser
        ): NetworkErrorParser =
            NetworkErrorParserImpl(resRepo, httpException)
    }
}
