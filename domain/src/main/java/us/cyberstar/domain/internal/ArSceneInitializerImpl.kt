package us.cyberstar.domain.internal

import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.ArWorldLoaderFabric
import us.cyberstar.domain.external.loader.TelemetryRecorderFabric
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.external.manger.AugImgDbManger
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.external.manger.arScene.MultiNodeManager
import us.cyberstar.domain.external.provider.RootNodeProvider
import us.cyberstar.domain.internal.manger.arScene.grid.HorGridManagerImpl
import us.cyberstar.framerecorder.media.external.FPS_DEFAULT
import us.cyberstar.framerecorder.media.external.FrameRecorderSettings
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Kk, let's describe the module duties:
 * 1) toggles gps coordinates listener
 * 2) toggles telemetry targetingPostEmitter
 * 3) switches recorder to local/remote
 */
internal class ArSceneInitializerImpl @Inject constructor(
    private val rootNodeProvider: RootNodeProvider,
    private val horGridManager: HorGridManagerImpl,
    private val framerRecorderSettings: FrameRecorderSettings,
    private val schedulersProvider: SchedulersProvider,
    private val videoRecorderWrapper: VideoRecorderWrapper,
    private val headFinalEntityEmitter: HeadFinalEntityEmitter,
    private val sessionIdProvider: SessionIdProvider,
    private val hardwareEntityEmitter: HardwareEntityEmitter,
    private val deviceInfo: DeviceInfo,
    private val arSessionStartTimeProvider: ArSessionStartTimeProvider,
    private val augImgDbManger: AugImgDbManger,
    private val multiNodeManager: MultiNodeManager,
    private val sensorFrameEntityEmitter: SensorFrameEntityEmitter,
    private val planesEmitter: PlanesEmitter,
    private val augmentedImageEmitter: AugmentedImageEmitter,
    private val dataFrameEntityEmitter: DataFrameEntityEmitter,
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val compositeDisposable: CompositeDisposable,
    private val arWorldLoaderFabric: ArWorldLoaderFabric,
    private val telemetryRecorderFabric: TelemetryRecorderFabric,
    private val gpsCoordinatesListener: GPSCoordinatesListener
) : ArSceneInitializer {

    var isTelemetryStarted: Boolean = false

    override fun destroyHorGrid() {
        Timber.d("destroyHorGrid")
        horGridManager.destroyGrid()
    }

    override fun createHorGrid() {
        Timber.d("createHorGrid")
        horGridManager.createGrid()
    }

    private fun toggleSession(connected: Boolean) {
        val recorder = telemetryRecorderFabric.getTelemetryRecorder()
        if (connected) {
            arSessionStartTimeProvider.startSession()
            recorder.startSession()
        } else {
            recorder.closeSession()
        }
    }

    override fun initScene() {
        Timber.d("initScene")
        sessionIdProvider.resetUUID()
        gpsCoordinatesListener.registerListener()

        multiNodeManager.subscribeToPostCreated()
        planesEmitter.subscribeToArCoreFrames()
        augmentedImageEmitter.subscribeToArCoreFrames()
        augImgDbManger.subscribeToAssetForDetection()
        rootNodeProvider.nodeIsVisible = true
    }

    override fun onDestroy() {

        multiNodeManager.destroy()
        planesEmitter.unsubscribeFromArCoreFrames()
        augmentedImageEmitter.unsubscribeFromArCoreFrames()
        augImgDbManger.unsubscribe()

        gpsCoordinatesListener.unRegisterListener()
        sensorFrameEntityEmitter.stopListener()
        Timber.d("onDestroy")
        stopTelemetry()
        compositeDisposable.clear()
        augImgDbManger.removeImages()
    }

    override fun loadWorld(isRunning: Boolean) {
        Timber.d("start  start Scene World updating ")
        if (isRunning) {
            arWorldLoaderFabric.getLoader().startSceneWorldUpdating()
        } else {
            arWorldLoaderFabric.getLoader().stopSceneWorldUpdating()
        }
    }

    override fun toggleLocalRemote(isLocal: Boolean) {
        Timber.d("toggleLocalRemote isLocal= $isLocal ")
        framerRecorderSettings.fps = if (isLocal) FPS_DEFAULT else 1.0
        val telemetryRecorder = telemetryRecorderFabric.getTelemetryRecorder()
        telemetryRecorder.resetCounter()
        arWorldLoaderSettings.isLocal = isLocal
        /**
         * Posts can be load localy or by grpc,
         *  so we resubscribe here to a new source
         */
        multiNodeManager.unsubscribeFromPostCreated()
        multiNodeManager.subscribeToPostCreated()
    }

    private fun clearAll() {
        Timber.d("clearAll")
        dataFrameEntityEmitter.unsubscribeFromArCoreFrames()
        multiNodeManager.unsubscribeFromPostCreated()
        planesEmitter.unsubscribeFromArCoreFrames()
        augmentedImageEmitter.unsubscribeFromArCoreFrames()
        augImgDbManger.unsubscribe()
        compositeDisposable.clear()
    }

    override fun startTelemetry(withVideo: Boolean) {
        if (!isTelemetryStarted) {
            isTelemetryStarted = true
            val telemetryRecorder = telemetryRecorderFabric.getTelemetryRecorder()
            schedulersProvider.io().scheduleDirect {
                sessionIdProvider.resetUUID()
                toggleSession(true)
                deviceInfo.registerBatteryTemperature()
                deviceInfo.registerLightListener()
                dataFrameEntityEmitter.subscribeToArCoreFrames()
                hardwareEntityEmitter.subscribeToArCoreFrames()
                sensorFrameEntityEmitter.startListener()
                telemetryRecorder.resetCounter()
                headFinalEntityEmitter.createHeadSessionEntity { telemetryRecorder.startTelemetry(it) }
                if (withVideo)
                    videoRecorderWrapper.toggleRecorder(true)
            }
        }
    }

    override fun stopTelemetry() {
        if (isTelemetryStarted) {
            isTelemetryStarted = false
            val telemetryRecorder = telemetryRecorderFabric.getTelemetryRecorder()
            videoRecorderWrapper.toggleRecorder(false)
            schedulersProvider.io().scheduleDirect(
                {
                    dataFrameEntityEmitter.unsubscribeFromArCoreFrames()
                    videoRecorderWrapper.saveVideoToFile()
                    arSessionStartTimeProvider.stopSession()
                    hardwareEntityEmitter.unsubscribeFromArCoreFrames()
                    deviceInfo.unregisterLightListener()
                    deviceInfo.unRegisterBatteryTemperature()
                    sensorFrameEntityEmitter.stopListener()
                    telemetryRecorder.stopAndSaveTelemetry(headFinalEntityEmitter.getSessionFinalEntity())
                    toggleSession(false)
                },
                2000, TimeUnit.MILLISECONDS
            )
        }
    }
}
