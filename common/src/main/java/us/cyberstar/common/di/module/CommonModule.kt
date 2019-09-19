package us.cyberstar.common.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.external.ResRepo
import us.cyberstar.common.internal.ResRepoImpl

@Module
class CommonModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideSchedulers(): us.cyberstar.common.external.SchedulersProvider =
            us.cyberstar.common.internal.AppSchedulersProvider()

        @Provides
        @JvmStatic
        fun provideResRepo(context: Context): ResRepo = ResRepoImpl(context)
    }
}
