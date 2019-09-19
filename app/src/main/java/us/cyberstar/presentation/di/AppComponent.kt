package us.cyberstar.presentation.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import us.cyberstar.common.PerApp
import us.cyberstar.common.di.module.CommonModule
import us.cyberstar.data.di.module.DeviceSensorsModule
import us.cyberstar.data.di.module.GrpcModule
import us.cyberstar.data.di.module.StorageModule
import us.cyberstar.domain.di.module.StaticModule
import us.cyberstar.presentation.App
import us.cyberstar.presentation.di.module.AppBuilderModule
import us.cyberstar.presentation.di.module.AppModule

@PerApp
@Component(
    modules = [
        CommonModule::class,
        AppModule::class,
        AppBuilderModule::class,
        StaticModule::class,
        GrpcModule::class,
        StorageModule::class,
        DeviceSensorsModule::class,
        AndroidSupportInjectionModule::class]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()

}