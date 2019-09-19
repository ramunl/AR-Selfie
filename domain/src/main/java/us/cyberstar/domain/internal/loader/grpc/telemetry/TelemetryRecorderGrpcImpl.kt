package us.cyberstar.domain.internal.loader.grpc.telemetry

import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import us.cyberstar.common.external.SchedulersProvider
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider
import us.cyberstar.domain.external.loader.grpc.telemetry.*
import javax.inject.Inject

/**
 * The class runs grpc gridAsset with session data
 */
internal class TelemetryRecorderGrpcImpl @Inject constructor(
    hardwareEntityEmitter: HardwareEntityEmitter,
    compositeDisposable: CompositeDisposable,
    schedulersProvider: SchedulersProvider,
  //  dataFrameEntityEmitter: DataFrameEntityEmitter,
    augmentedImageEmitter: AugmentedImageEmitter,
    planesEmitter: PlanesEmitter,
    private val grpcTelemetryService: GrpcTelemetryService,
    arSessionStartTimeProvider: us.cyberstar.domain.external.arcore.ArSessionStartTimeProvider,
    private val sessionIdProvider: SessionIdProvider,
    private val headFinalEntityEmitter: HeadFinalEntityEmitter
) : TelemetryRecorderRemote(
    hardwareEntityEmitter,
    schedulersProvider,
    arSessionStartTimeProvider,
    //dataFrameEntityEmitter,
    augmentedImageEmitter,
    planesEmitter,
    headFinalEntityEmitter
) {
    override fun startSession() {
        Timber.d("startSession:openChannel")
        grpcTelemetryService.openChannel(null)
    }

    override fun closeSession() {
        Timber.d("closeSession")
        //grpcTelemetryService.closeChannel()
        //before channel shutdown we must send ArFinal message
        //Not sure it is good solution, but I am gonna to close the channel inside GrpcTelemetryService class
    }

    override fun appendEntity(entity: ArEntityTelemetry) {
        grpcTelemetryService.sendStreamMessage(entity)
    }

}