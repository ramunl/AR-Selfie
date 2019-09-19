package us.cyberstar.presentation.di.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import us.cyberstar.common.PerApp
import us.cyberstar.presentation.App

@Module
abstract class AppModule {

    @Module
    companion object {
        @PerApp
        @Provides
        @JvmStatic
        fun provideContext(application: App): Context = application

        @PerApp
        @Provides
        @JvmStatic
        fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

        @PerApp
        @Provides
        @JvmStatic
        fun provideSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(App::class.java.simpleName, Context.MODE_PRIVATE)

    }

}
