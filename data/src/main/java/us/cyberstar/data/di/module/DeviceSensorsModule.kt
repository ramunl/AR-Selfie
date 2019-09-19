package us.cyberstar.data.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import us.cyberstar.common.external.ResRepo
import us.cyberstar.data.external.sensor.DeviceSensorEventListener
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.data.internal.sensor.DeviceInfoImpl
import us.cyberstar.data.internal.sensor.DeviceSensorEventListenerImpl
import us.cyberstar.data.internal.sensor.GPSCoordinatesListenerImpl


@Module
class DeviceSensorsModule {

    @Provides
    @us.cyberstar.common.PerApp
    fun provideDeviceInfo(context: Context, resRepo: ResRepo): DeviceInfo =
        DeviceInfoImpl(context, resRepo)

    @Provides
    @us.cyberstar.common.PerApp
    fun provideDeviceSensorListener(context: Context): DeviceSensorEventListener =
        DeviceSensorEventListenerImpl(context)

    @Provides
    @us.cyberstar.common.PerApp
    fun provideGPSCoordinatesListener(context: Context): GPSCoordinatesListener =
        GPSCoordinatesListenerImpl(context)

}
