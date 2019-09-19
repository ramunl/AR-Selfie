package us.cyberstar.data.internal.sensor

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.google.common.util.concurrent.AtomicDouble
import us.cyberstar.common.external.ResRepo
import us.cyberstar.data.external.sensor.DeviceInfo
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import timber.log.Timber
import us.cyberstar.data.utils.StringHelper.convertToUTF8
import java.lang.Exception
import java.net.URLEncoder
import java.util.concurrent.atomic.AtomicInteger


class DeviceInfoImpl(
    val context: Context,
    resRepo: ResRepo
) : DeviceInfo {

    val mySensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager?
    val lightSensor = mySensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
    val ambientLightingLux = AtomicDouble()
    val temperature = AtomicInteger()

    val sensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (sensorEvent.sensor.name == lightSensor.name) {
                ambientLightingLux.set(sensorEvent.values[0].toDouble())
            }
        }

        override fun onAccuracyChanged(p0: Sensor, p1: Int) {

        }
    }

    override fun getAmbientLightingLux() = ambientLightingLux.get()
    override fun getTemperature() = temperature.get()

    override fun registerBatteryTemperature() {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(temperatureBroadcastReceiver, iFilter)
    }

    override fun unRegisterBatteryTemperature() {
        try {
            context.unregisterReceiver(temperatureBroadcastReceiver)
        } catch (e: Exception) {
            //Timber.e(e)
        }
    }

    override fun unregisterLightListener() {
        try {
            mySensorManager!!.unregisterListener(sensorEventListener)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun registerLightListener() {
        mySensorManager!!.registerListener(
            sensorEventListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    override fun getBatteryLevel(context: Context): Int {
        val batteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager?
        return batteryManager!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    override fun getMemoryUsagePercentage(context: Context): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem - memoryInfo.availMem
    }


    @SuppressLint("DefaultLocale")
    override fun getDeviceModel(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return convertToUTF8("$manufacturer $model").capitalize()
    }

    override fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return convertToUTF8("Android: $sdkVersion ($release)")
    }

    private val temperatureBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get the battery temperature
            // Battery temperature in tenths of a degree Centigrade
            temperature.set(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10)

        }
    }


}

