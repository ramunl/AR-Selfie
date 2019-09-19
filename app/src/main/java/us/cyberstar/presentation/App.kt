package us.cyberstar.presentation

import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import us.cyberstar.arcyber.BuildConfig
import us.cyberstar.presentation.di.DaggerAppComponent


class App : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "XXX ${element.fileName} : ${element.lineNumber}"
                }
            })
        }
    }


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().create(this)
}