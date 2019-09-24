package us.cyberstar.presentation.feature.settingsDev.presenter


import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.InjectViewState
import timber.log.Timber
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.domain.external.ArSceneInitializer
import us.cyberstar.domain.external.loader.grpc.telemetry.DataFrameEntityEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.HardwareEntityEmitter
import us.cyberstar.domain.external.loader.grpc.telemetry.PlanesEmitter
import us.cyberstar.domain.external.manger.AugImgDbManger
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import us.cyberstar.domain.external.manger.arScene.MultiNodeManager
import us.cyberstar.domain.external.loader.local.TelemetryRecorderLocal
import us.cyberstar.domain.internal.ArWorldLoaderSettings
import us.cyberstar.presentation.base.BasePresenter
import us.cyberstar.presentation.feature.settingsDev.view.SettingsDevView
import us.cyberstar.presentation.helpers.GpsDialogProvider
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate


@InjectViewState
class SettingsDevPresenter @Inject constructor(
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val telemetryRecorderLocal: TelemetryRecorderLocal,
    private val sessionIdProvider: SessionIdProvider,
    private val hardwareEntityEmitter: HardwareEntityEmitter,
    private val augImgDbManger: AugImgDbManger,
    private val multiNodeManager: MultiNodeManager,
    private val dataFrameEntityEmitter: DataFrameEntityEmitter,
    private val planesEmitter: PlanesEmitter,
    private val videoRecorderWrapper: VideoRecorderWrapper,
    private val gpsDialogProvider: GpsDialogProvider,
    private val arSceneInitializer: ArSceneInitializer
) : BasePresenter<SettingsDevView>() {

    override fun attachView(view: SettingsDevView) {
        super.attachView(view)
        toggleRemoteLocalStorage(true)
    }


    fun toggleRemoteLocalStorage(isLocal: Boolean) {
        Timber.d("toggleRemoteLocalStorage is local $isLocal")
        arSceneInitializer.toggleLocalRemote(isLocal)
    }

    var isTelemetryOn = false
    fun startTelemetry(withVideo: Boolean) {
        if (gpsDialogProvider.isGPSEnabled()) {
            isTelemetryOn = true
            arSceneInitializer.startTelemetry(withVideo)
        }
    }

    fun stopTelemetry() {
        isTelemetryOn = false
        arSceneInitializer.stopTelemetry()
    }

    fun cleanWorld() {
        augImgDbManger.removeImages()
        multiNodeManager.destroy()
    }

    fun toggleLoadWorld(start: Boolean) {
      //  arSceneInitializer.loadWorld(start)
    }

    var debugInfoUpdateTimer: Timer? = null

    fun stopInfoUpdating() {
        debugInfoUpdateTimer?.cancel()
        debugInfoUpdateTimer = null
    }

    fun runInfoUpdating() {
        if (debugInfoUpdateTimer == null) {
            debugInfoUpdateTimer = Timer().apply {
                scheduleAtFixedRate(0, 2000) {
                    val infoRes = StringBuilder()
                    infoRes.append("id: ${sessionIdProvider.sessionId()}\n")
                    infoRes.append(dataFrameEntityEmitter.pointCloudInfo())
                    infoRes.append(planesEmitter.planesInfo)
                    infoRes.append(hardwareEntityEmitter.hwInfo)
                    infoRes.append(videoRecorderWrapper.videoRecordInfo() + "\n")
                    //   infoRes.append(createQuickPostUseCase.getInfo() + "\n")
                    //   infoRes.append(createTargetPostUseCase.getInfo())
                    viewState.updateMainInfo(infoRes.toString())
                    if (isTelemetryOn) {
                        viewState.updateTelemetryInfo(
                            if (arWorldLoaderSettings.isLocal) {
                                with(telemetryRecorderLocal) {
                                    "Telemetry:\n" +
                                            "augmentedFrames = ${arAugnmentedArray.size}\n" +
                                            "dataFrames = ${dataFrameArray.size}\n" +
                                            "planes = ${arPlaneArray.size}\n" +
                                            "sensorFrames = ${sensorFrames.size}\n" +
                                            "hwFrames = ${hwFrames.size}\n" +
                                            "headFrames = ${headFrames.size}\n" +
                                            "finalFrames = ${finalFrames.size}"
                                }
                            } else {
                                with(ArEntityTelemetry) {
                                    "Telemetry:\n" +
                                            "augmentedFrames = ${detectedAssetCounter}\n" +
                                            "dataFrames = ${dataFrameEntityCounter}\n" +
                                            "planes = ${arPlaneEntityCounter}\n" +
                                            "sensorFrames = ${sensorFrameEntityCounter}\n" +
                                            "hwFrames = ${hardwareFrameEntityCounter}\n" +
                                            "headFrames = ${sessionHeadEntityCounter}\n" +
                                            "finalFrames = ${sessionFinalEntityCounter}"
                                }
                            })
                    }
                    viewState.updateNodesInfo(multiNodeManager.getInfo())
                }
            }
        }
    }

    /*fun toggleVideoRecord(checked: Boolean) {
        Timber.d("toggleVideoRecord $checked")
        videoRecorderWrapper.toggleRecorder(checked)
    }*/

    fun onStop() {
        arSceneInitializer.destroyHorGrid()
        stopInfoUpdating()
    }

    fun onStart() {
        arSceneInitializer.createHorGrid()
        runInfoUpdating()
    }

    fun runWithWriteExternalPermCheck(someMethodToCall: () -> Unit) {
        /*  permissionHelper.callWithPermissionCheck(
              someMethodToCall,
              PermissionRequestCode.WRITE_EXTERNAL_PERMISSION_CODE
          )*/
    }

}