package us.cyberstar.domain.internal.loader.grpc.telemetry

import android.content.Context
import com.google.ar.sceneform.FrameTime
import us.cyberstar.common.external.ResRepo
import us.cyberstar.data.R
import us.cyberstar.data.entity.telemetry.HardwareFrameEntity
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.HardwareEntityEmitter
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class HardwareEntityEmitterImpl @Inject constructor(
    resRepo: ResRepo,
    arCoreFrameEmitter: ArCoreFrameEmitter,
    private val deviceInfo: DeviceInfo,
    private val context: Context,
    private val timeStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
) : HardwareEntityEmitter(arCoreFrameEmitter) {

    override fun onUpdate(frameTime: FrameTime) {
        with(deviceInfo) {
            val timestamp = timeStartTimeProvider.startTimeStamp
            val ram = getMemoryUsagePercentage(context)
            val battery = getBatteryLevel(context).toLong()
            val lux = getAmbientLightingLux()
            val temperature = getTemperature()
            hwInfo.set(
                        "ram: ${ram/1000} Kbytes \n" +
                        // "timestamp = $timestamp \n" +
                        "battery = $battery% \n" +
                        "temperature = $temperature C \n" +
                        "lux = $lux "
            )
            emitNext(
                HardwareFrameEntity(
                    timestamp,
                    ram,
                    battery,
                    lux,
                    temperature.toLong()
                )
            )
        }

    }


    override var hwInfo: AtomicReference<String> =
        AtomicReference(resRepo.getString(R.string.no_device_info))
}