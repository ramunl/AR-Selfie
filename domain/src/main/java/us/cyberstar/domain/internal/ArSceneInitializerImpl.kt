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
import us.cyberstar.domain.external.manger.arScene.NodeManager
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
    private val currentSessionNodeManager: NodeManager,
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
    override fun startTelemetry(withVideo: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopTelemetry() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        //multiNodeManager.subscribeToPostCreated()
        // planesEmitter.subscribeToArCoreFrames()
        // augmentedImageEmitter.subscribeToArCoreFrames()
        // augImgDbManger.subscribeToAssetForDetection()
        rootNodeProvider.nodeIsVisible = true
        currentSessionNodeManager.subscribeToArCoreFrames()
    }

    override fun onDestroy() {
        currentSessionNodeManager.unsubscribeFromArCoreFrames()
        //multiNodeManager.destroy()
        //planesEmitter.unsubscribeFromArCoreFrames()
        //  augmentedImageEmitter.unsubscribeFromArCoreFrames()
        //  augImgDbManger.unsubscribe()

        gpsCoordinatesListener.unRegisterListener()
        sensorFrameEntityEmitter.stopListener()
        Timber.d("onDestroy")
        //stopTelemetry()
        compositeDisposable.clear()
        //augImgDbManger.removeImages()
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
}
