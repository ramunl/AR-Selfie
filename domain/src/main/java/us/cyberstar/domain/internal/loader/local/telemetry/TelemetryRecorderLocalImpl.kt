package us.cyberstar.domain.internal.loader.local.telemetry

import io.reactivex.Maybe
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ArEntityCache
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import us.cyberstar.domain.external.loader.local.TelemetryRecorderLocal
import javax.inject.Inject

/**
 * The class saves session data ( post, plane, assets)
 * to local storage
 */
internal class TelemetryRecorderLocalImpl @Inject constructor(
    hardwareEntityEmitter: HardwareEntityEmitter,
    private val sessionIdProvider: SessionIdProvider,
    private val arCoreFrameEmitter: ArCoreFrameEmitter,
    private val snackBarProvider: SnackBarProvider,
    private val arEntityCache: ArEntityCache,
    private val schedulersProvider: us.cyberstar.common.external.SchedulersProvider,
    arSessionStartTimeProvider: ArSessionStartTimeProvider,
//    dataFrameEntityEmitter: DataFrameEntityEmitter,
    augmentedImageEmitter: AugmentedImageEmitter,
    planesEmitter: PlanesEmitter,
    headFinalEntityEmitter: HeadFinalEntityEmitter
) : TelemetryRecorderLocal(
    hardwareEntityEmitter,
    schedulersProvider,
    arSessionStartTimeProvider,
    //dataFrameEntityEmitter,
    augmentedImageEmitter,
    planesEmitter,
    headFinalEntityEmitter
) {

    //private var arFinalEntity: SessionFinalEntity? = null
    private val createPostRequestEntityArray = ArrayList<CreatePostRequestEntity>()
    //telemetry

    override val finalFrames = ArrayList<SessionFinalEntity>()
    override val headFrames = ArrayList<SessionHeadEntity>()
    override val hwFrames = ArrayList<HardwareFrameEntity>()
    override val sensorFrames = ArrayList<SensorFrameEntity>()
    override val arPlaneArray = ArrayList<ArPlaneEntity>()
    override val dataFrameArray = ArrayList<DataFrameEntity>()
    override val arAugnmentedArray = ArrayList<DetectedAssetEntity>()

    override fun startSession() {
        resetCounter()
        Timber.d("Recorder Local: channel connected")
        //not used in local recorder
        //here probably we could create and open File object  to save entities
    }

    override fun resetCounter() {
        finalFrames.clear()
        headFrames.clear()
        sensorFrames.clear()
        dataFrameArray.clear()
        arPlaneArray.clear()
        arAugnmentedArray.clear()
        hwFrames.clear()
    }
    override fun closeSession() {
        Timber.d("Recorder Local: channel disconnected")
        stopAndSaveEntities()
    }


    override fun stopAndSaveTelemetry(finalEntity: SessionFinalEntity) {
        snackBarProvider.showMessage("..........Saving data......Please wait!................")
        Timber.d("Recorder Local: stopAndSaveTelemetry")
        super.stopAndSaveTelemetry(finalEntity)
        saveTelemetryToFile()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())
            .subscribe().addTo(compositeDisposable)

    }


    override fun appendEntity(entity: ArEntityTelemetry) {
        synchronized(this) {
            if (entity is SaveVideoRequestEntity) {
                //do nothing we don't save this entity to local storage
            } else if (entity is CreatePostRequestEntity) {
                createPostRequestEntityArray.add(entity)
            } /*else if (entity is SessionFinalEntity) {
                arFinalEntity = entity
            } */ else {
                when (entity) {
                    is SessionFinalEntity -> finalFrames.add(entity)
                    is SessionHeadEntity -> headFrames.add(entity)
                    is DetectedAssetEntity -> arAugnmentedArray.add(entity)
                    is HardwareFrameEntity -> hwFrames.add(entity)
                    is SensorFrameEntity -> sensorFrames.add(entity)
                    is ArPlaneEntity -> arPlaneArray.add(entity)
                    is DataFrameEntity -> dataFrameArray.add(entity)
                    else -> {
                    }//Timber.e(" What's the fuck?? It's wrong type(${entity.javaClass}) and must not be here !")
                }
            }
        }
    }


    private fun saveTelemetryToFile(): Maybe<String> {
        return Maybe.create<String> { fileEmitter ->
            try {
                Timber.d("stop recording And Save entities to files")
                Timber.d("headArray ${headFrames.size}")
                Timber.d("finalArray ${finalFrames.size}")
                Timber.d("arAugnmentedArray ${arAugnmentedArray.size}")
                Timber.d("sensorFrames ${sensorFrames.size}")
                Timber.d("dataFrameArray ${dataFrameArray.size}")
                Timber.d("arPlaneArray ${arPlaneArray.size}")
                Timber.d("hwFrames ${hwFrames.size}")
                try {
                    val telemetry = arEntityCache.saveArSession(
                        sessionIdProvider.sessionId()!!,
                        arSessionStartTimeProvider.startTimeStamp,
                        arCoreFrameEmitter.lastFrame()!!.camera.imageIntrinsics,
                        sensorFrames,
                        dataFrameArray,
                        arPlaneArray,
                        hwFrames,
                        arAugnmentedArray,
                        headFrames,
                        finalFrames
                    )
                    snackBarProvider.showMessage("Telemetry saved: size = ${telemetry.length()} ${telemetry.absolutePath}")
                } catch (e: Exception) {
                    snackBarProvider.showError(e.toString(), false)
                }
            } catch (e: Exception) {
                fileEmitter.onError(e)
            }
        }
    }

    private fun saveLoadWorldReplyToFile(): Maybe<String> {
        return Maybe.create<String> { fileEmitter ->
            try {
                Timber.d("stop recording And Save createPostRequest array = ${createPostRequestEntityArray.size} to file")
                try {
                    if (createPostRequestEntityArray.size > 0) {
                        val worldReply = arEntityCache.saveLoadWorldReply(
                            sessionIdProvider.sessionId()!!,
                            createPostRequestEntityArray
                        )
                        snackBarProvider.showMessage("Data saved! createPostRequestEntity = ${createPostRequestEntityArray.size} world size = ${worldReply.length()}")
                        // arFinalEntity = null
                        createPostRequestEntityArray.clear()
                    }
                } catch (e: Exception) {
                    snackBarProvider.showError(e.toString(), false)
                }
            } catch (e: Exception) {
                fileEmitter.onError(e)
            }
        }
    }


    private fun stopAndSaveEntities() {
        saveLoadWorldReplyToFile()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())
            .subscribe().addTo(compositeDisposable)
    }
}