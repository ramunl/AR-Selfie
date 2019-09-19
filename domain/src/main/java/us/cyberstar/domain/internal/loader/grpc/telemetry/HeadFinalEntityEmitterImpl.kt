package us.cyberstar.domain.internal.loader.grpc.telemetry

import io.reactivex.disposables.Disposable
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.entity.telemetry.SessionFinalEntity
import us.cyberstar.data.entity.telemetry.SessionHeadEntity
import us.cyberstar.data.external.sensor.DeviceInfo
import us.cyberstar.domain.external.arcore.ArCoreFrameEmitter
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.grpc.telemetry.HeadFinalEntityEmitter
import us.cyberstar.domain.external.manger.VideoRecorderWrapper
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HeadFinalEntityEmitterImpl @Inject constructor(
    private val deviceInfo: DeviceInfo,
    private val schedulersProvider: SchedulersProvider,
    private val arCoreFrameEmitter: ArCoreFrameEmitter,
    private val snackBarProvider: SnackBarProvider,
    private val arSessionStartTimeProvider: ArSessionStartTimeProvider,
    private val videoRecorderWrapper: VideoRecorderWrapper
) : HeadFinalEntityEmitter {

    var createArHeadEntityDisposable: Disposable? = null

    var isSessionHeadEntitySent = false

    override fun createHeadSessionEntity(onSessionHeadEntityListener: (SessionHeadEntity) -> Unit) {
        fun doCreate(): Boolean {
            Timber.w("trying to create HeadSessionEntity..")
            arCoreFrameEmitter.lastFrame()?.let {
                it.camera?.imageIntrinsics?.let {
                    onSessionHeadEntityListener.invoke(
                        SessionHeadEntity(
                            arSessionStartTimeProvider.startTimeStamp,
                            0,
                            it,
                            videoRecorderWrapper.videoStartTimeStamp(),
                            deviceInfo.getDeviceModel(),
                            deviceInfo.getAndroidVersion()
                        ))
                    Timber.d("success!")
                    isSessionHeadEntitySent = true
                }
            } ?: Timber.d("Last Ar Frame null, can't create Final entity")
            return isSessionHeadEntitySent
        }

        createArHeadEntityDisposable = schedulersProvider.io().schedulePeriodicallyDirect({
            if (!isSessionHeadEntitySent && doCreate()) {
                createArHeadEntityDisposable?.dispose()
            }
        }, 0, 500, TimeUnit.MILLISECONDS)

    }

    override fun getSessionFinalEntity(): SessionFinalEntity {
        Timber.w("emit SessionFinalEntity")
        //now we can reset flag
        isSessionHeadEntitySent = false
           return SessionFinalEntity(
                arSessionStartTimeProvider.stopTimeStamp
            )
    }
}