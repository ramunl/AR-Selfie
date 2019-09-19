package us.cyberstar.data.internal.network.grpc

import interactive.ModelRegistryServiceGrpc
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import proxy.ARServiceGrpc
import proxy.TelemetryServiceGrpc
import social.ARMSocialServiceGrpc
import timber.log.Timber
import us.cyberstar.data.ProtoServiceProvider
import us.cyberstar.data.di.module.GrpcModule.Companion.BASE_URL
import us.cyberstar.data.di.module.GrpcModule.Companion.PORT
import us.cyberstar.data.di.module.GrpcModule.Companion.PORT_ARM
import javax.inject.Inject


internal class ProtoServiceProviderImpl @Inject constructor(
    private val credentials: CallCredentials
) : ProtoServiceProvider {

    override val modelRegistryService: ModelRegistryServiceGrpc.ModelRegistryServiceStub? by lazy {
        createChannel()
        ModelRegistryServiceGrpc.newStub(channel).withCallCredentials(credentials)
    }

    override var armSocialServiceGrpc: ARMSocialServiceGrpc.ARMSocialServiceStub? = null
        get() {
            if (field == null) {
                createChannelARM()
                Timber.d("GrpcTelemetryService init..")
                field = ARMSocialServiceGrpc.newStub(channelARM)
                    //.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withCallCredentials(credentials)
                Timber.d("ArService stub created")
            }
            return field
        }

    override var telemetryServiceBlocking: TelemetryServiceGrpc.TelemetryServiceBlockingStub? = null
        get() {
            if (field == null) {
                createChannel()
                Timber.d("GrpcTelemetryService init..")
                field = TelemetryServiceGrpc.newBlockingStub(channel)
                    //.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withCallCredentials(credentials)
                Timber.d("ArService stub created")
            }
            return field
        }

    override var telemetryService: TelemetryServiceGrpc.TelemetryServiceStub? = null
        get() {
            if (field == null) {
                Timber.d("ArService init..")
                createChannel()
                field = TelemetryServiceGrpc.newStub(channel)
                    //.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withCallCredentials(credentials)
                Timber.d("ArService stub created")
            }
            return field
        }

    override var arServiceBlocking: ARServiceGrpc.ARServiceBlockingStub? = null
        get() {
            if (field == null) {
                Timber.w(".........arServiceBlocking init................")
                createChannel()
                field = ARServiceGrpc.newBlockingStub(channel)
                    //.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withCallCredentials(credentials)
                Timber.d("ArService stub created")
            }
            return field
        }

    override var arService: ARServiceGrpc.ARServiceStub? = null
        get() {
            if (field == null) {
                Timber.w(".........ArService init........")
                createChannel()
                field = ARServiceGrpc.newStub(channel)
                    //.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withCallCredentials(credentials)
                Timber.d("ArService stub created")
            }
            return field
        }

    private var channelARM: ManagedChannel? = null
    private var channel: ManagedChannel? = null

    private fun createChannelARM() {
        if (channelARM == null ||
            channelARM!!.isShutdown ||
            channelARM!!.isTerminated
        ) {
            Timber.e("...............channel create...............")
            channelARM = ManagedChannelBuilder.forAddress("arm.actant.app", PORT_ARM)
                .maxInboundMessageSize(1000 * 1024 * 1024)
                .maxInboundMetadataSize(1000 * 1024 * 1024)
                //.usePlaintext()
                .build()
        }
    }

    private fun createChannel() {
        if (channel == null ||
            channel!!.isShutdown ||
            channel!!.isTerminated
        ) {
            Timber.e("...............channel create...............")
            channel = ManagedChannelBuilder.forAddress(BASE_URL, PORT)
                .maxInboundMessageSize(1000 * 1024 * 1024)
                .maxInboundMetadataSize(1000 * 1024 * 1024)
                .usePlaintext().build()
        }
    }

    override fun shutDownChannel() {
        /*
        if(channel != null) {
            Timber.e("..................channel shutdown.........................")
            channel!!.shutdown()
            channel = null
            arService = null
        }*/
    }

}