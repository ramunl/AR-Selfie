package us.cyberstar.data.internal.network.grpc

import io.grpc.stub.StreamObserver
import proxy.Proxy
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.ProtoServiceProvider
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry
import us.cyberstar.data.mapper.*
import us.cyberstar.data.mapper.telemetry.*
import java.lang.Exception
import javax.inject.Inject


internal class GrpcTelemetryServiceImpl @Inject constructor(
    private val snackBarProvider: SnackBarProvider,
    private val protoServiceProvider: ProtoServiceProvider
) : GrpcTelemetryService {

    private var sessionStreamMessageObserver: StreamObserver<Proxy.SessionStreamMessage>? = null

    override fun sendStreamMessage(entity: ArEntityTelemetry) {
        sessionStreamMessageObserver?.let {
            when (entity) {
                is SessionFinalEntity -> {
                    createSessionFinalEntity(entity)
                }
                else -> {
                    try {
                        //Timber.d("sendStreamMessage $entity")
                        val sessionStreamBuilder = Proxy.SessionStreamMessage.newBuilder()
                        with(sessionStreamBuilder) {
                            when (entity) {
                                is SessionHeadEntity -> setSessionHeadData(mapToSessionDataHead(entity))
                                is HardwareFrameEntity -> setHwFrame(mapToHardwareFrame(entity))
                                is DetectedAssetEntity -> setDetectedAsset(mapToDetectedAsset(entity))
                                is DataFrameEntity -> setDataFrame(mapToDataFrame(entity))
                                is SensorFrameEntity -> setMotionFrame(mapToMotion(entity))
                                is ArPlaneEntity -> setPlane(mapToPlane(entity))
                                else -> Timber.e("Wrong entity Type!!! ${entity::class.java}")
                            }
                        }
                        it.onNext(sessionStreamBuilder.build())
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                }
            }
        } ?: { Timber.e("sessionStreamMessageObserver = null") }()

    }


    private fun createSessionFinalEntity(entity: SessionFinalEntity) {
        Timber.e("sending SessionFinalEntity to via GRPC")
        with(protoServiceProvider) {
            arService?.finalize(
                mapToSessionDataFinal(entity),
                object : StreamObserver<Proxy.BaseReply> {
                    override fun onNext(value: Proxy.BaseReply?) {
                        Timber.d("onNext $value")
                        closeChannel() //TODO check is it called
                    }

                    override fun onError(t: Throwable) {
                        snackBarProvider.showError(
                            "createSessionFinalEntity failed with:${t.message}",
                            false
                        )
                        Timber.d("onError $t")
                    }

                    override fun onCompleted() {
                        Timber.d("onCompleted")
                    }
                })
        }
    }


    override fun saveTelemetryVideo(entity: SaveVideoRequestEntity): Boolean {
        Timber.d("sending saveTelemetryVideo to via GRPC (arService?.saveTelemetryVideo called)")
        val reply =
            protoServiceProvider.arServiceBlocking!!.saveTelemetryVideo(mapToSaveVideoRequest(entity))
        return reply.isInitialized
    }


    override fun openChannel(nextMethodToCall: (() -> Unit)?) {
        Timber.d("GrpcTelemetryService openChannel ")
        with(protoServiceProvider) {
            sessionStreamMessageObserver =
                arService?.inStream(createObserver("Open channel", nextMethodToCall, true))
        }
    }

    override fun closeChannel() {
        Timber.d("GrpcTelemetryService closeChannel ")
        protoServiceProvider.shutDownChannel()
    }


    private fun createObserver(
        debugInfo: String,
        nextMethodToCall: (() -> Unit)? = null,
        toComplete: Boolean = false
    ): StreamObserver<Proxy.BaseReply> {
        with(protoServiceProvider) {
            return object : StreamObserver<Proxy.BaseReply> {
                override fun onError(t: Throwable?) {
                    Timber.e("onError ${t.toString()}")
                    /*arService?.let {
                        snackBarProvider.showError("$debugInfo failed with $t", false)
                        Timber.e(t)
                    }*/

                }

                override fun onCompleted() {
                    Timber.e("onCompleted")
                    arService?.let {
                        if (toComplete)
                            closeChannel()
                    }
                }

                override fun onNext(value: Proxy.BaseReply?) {
                    Timber.e("onNext ${value.toString()}")
                    arService?.let {
                        // snackBarProvider.showMessage("$debugInfo complete")
                        nextMethodToCall?.invoke()
                    }
                }
            }
        }
    }
}