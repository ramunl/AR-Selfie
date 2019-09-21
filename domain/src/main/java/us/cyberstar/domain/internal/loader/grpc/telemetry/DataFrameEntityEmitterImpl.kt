package us.cyberstar.domain.internal.loader.grpc.telemetry

import com.google.ar.sceneform.FrameTime
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.entity.telemetry.DataFrameEntity
import us.cyberstar.data.ext.asArray
import us.cyberstar.data.ext.cameraOrientation
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.DataFrameEntityEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.OnUpdateListener
import us.cyberstar.domain.internal.utils.getColorPointsFromFrame
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import us.cyberstar.domain.external.loader.TelemetryRecorderFabric


/**
 * The class is responsible for emitting of a telemetry event - DataFrameEntity
 */
//TODO refactor this class, its name doesn't correspond the meanings
internal class DataFrameEntityEmitterImpl @Inject constructor(
    private val framerRecorderSettings: FrameRecorderSettings,
    private val compositeDisposable: CompositeDisposable,
    override val arCoreFrameEmitter: ArCoreFrameEmitter,
    private val gpsCoordinatesListener: GPSCoordinatesListener,
    private val telemetryRecorderFabric: TelemetryRecorderFabric
) : DataFrameEntityEmitter(arCoreFrameEmitter),
    OnUpdateListener {
    override fun setCalcDescriptorsFlag(flag: Boolean) {
        flatToCalcDescriptors = flag
    }

    var flatToCalcDescriptors = true
    var pointCloudIdsArraySize = AtomicInteger()

    override fun pointCloudInfo() = "points cloud id's = $pointCloudIdsArraySize\n"

    var previousFrameLocked = false

    override fun onUpdate(frameTime: FrameTime) {
        try {
            arCoreFrameEmitter.lastFrame()?.let { frame ->
                gpsCoordinatesListener.location?.let { loc ->
                    val pointWithColor = getColorPointsFromFrame(frame)
                    if (pointWithColor.isNotEmpty()) {
                        val pointCloudIdsArray = frame.acquirePointCloud()
                            .use { pointCloud -> pointCloud.ids.asArray().map { it.toLong() }.toList() }
                        if (pointCloudIdsArray.isNotEmpty()) {
                            pointCloudIdsArraySize.set(pointCloudIdsArray.size)//for debug

                            var descriptors: List<ByteArray>? = null
                            var keypoints: List<Float>? = null

                            if (pointCloudIdsArray.isNotEmpty()) {
                                if (!previousFrameLocked && flatToCalcDescriptors) {
                                    frame.acquireCameraImage()?.use {
                                        previousFrameLocked = true
                                        // sanity checks - 3 planes
                                        val planes = it.planes
                                        assert(planes[0].pixelStride == 1)
                                        assert(planes[1].pixelStride == 2)
                                        assert(planes[2].pixelStride == 2)
                                        val w = framerRecorderSettings.previewSize.width
                                        val h = framerRecorderSettings.previewSize.height
                                        Timber.d("acquireCameraImage $w $h ")
                                        val y_plane = planes[0].buffer
                                        val uv_plane = planes[1].buffer
                                        Timber.d("acquireCameraImage close ")
                                        previousFrameLocked = false
                                    }
                                }
                                val arFrameModel = DataFrameEntity(
                                    loc,
                                    pointWithColor,
                                    pointCloudIdsArray,
                                    frame.updatedAnchors.toList(),
                                    frame.timestamp.toDouble(),
                                    frame.cameraOrientation(),
                                    keypoints,
                                    descriptors
                                )
                                telemetryRecorderFabric.getTelemetryRecorder().appendEntity(arFrameModel)
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            //Timber.w(e)
        }
    }

}


