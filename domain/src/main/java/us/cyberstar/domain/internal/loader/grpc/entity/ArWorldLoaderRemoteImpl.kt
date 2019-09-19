package us.cyberstar.domain.internal.loader.grpc.entity

import android.content.Context
import proxy.Proxy
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.*
import us.cyberstar.data.entity.LoadWorldReplyEntity
import us.cyberstar.data.entity.telemetry.LoadWorldRequestEntity
import us.cyberstar.data.external.grpc.GrpcArService
import us.cyberstar.data.external.sensor.GPSCoordinatesListener
import us.cyberstar.data.mapper.mapToLoadWorldReplyEntity
import us.cyberstar.domain.external.loader.grpc.entity.ArWorldLoaderGrpc
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

internal class ArWorldLoaderRemoteImpl @Inject constructor(
    private val gpsCoordinatesListener: GPSCoordinatesListener,
    override val snackBarProvider: SnackBarProvider,
    private val grpcArService: GrpcArService,
    override val context: Context,
    val schedulersProvider: us.cyberstar.common.external.SchedulersProvider
) : ArWorldLoaderGrpc() {

    var timer: Timer? = null

    override fun stopSceneWorldUpdating() {
        Timber.d("stopSceneWorldUpdating")
        timer?.cancel()
        timer = null
    }

    override fun startSceneWorldUpdating() {
        Timber.d("startSceneWorldUpdating")
        grpcArService.setLoadWorldObserver(object : LoadWorldReplyObserver {
            override fun onWorldRequestReply(reply: Proxy.MultipleLoadWorldReply) {
                onLoadWorldReplyEntityReady(mapToLoadWorldReplyEntity(reply))
            }

            override fun onWorldRequestReply(replyEntity: LoadWorldReplyEntity?) {
            }
        })
        if (timer == null) {
            timer = Timer().apply {
                scheduleAtFixedRate(0, 15000) {
                    refreshWorld()
                }
            }
        }
    }

    private fun refreshWorld() {
        gpsCoordinatesListener.getLastKnownLocation()?.let {
            grpcArService.sendLoadWorldRequestEntity(LoadWorldRequestEntity(it))
            Timber.d("GRPC world request for $it is called!!")
        } ?: Timber.e("Can't load WorldRequest, postLocation not defined")
    }
}