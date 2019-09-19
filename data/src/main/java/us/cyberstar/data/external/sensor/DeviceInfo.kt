package us.cyberstar.data.external.sensor

import android.content.Context
import java.util.concurrent.atomic.AtomicReference

interface DeviceInfo {
    fun unregisterLightListener()
    fun registerLightListener()
    fun getBatteryLevel(context: Context): Int
    fun getAmbientLightingLux(): Double
    fun getMemoryUsagePercentage(context: Context): Long
    fun getDeviceModel(): String
    fun getAndroidVersion(): String
    fun unRegisterBatteryTemperature()
    fun registerBatteryTemperature()
    fun getTemperature(): Int
}