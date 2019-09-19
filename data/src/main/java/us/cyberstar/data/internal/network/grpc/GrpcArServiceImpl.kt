package us.cyberstar.data.internal.network.grpc

import base_types.BaseTypes
import interactive.Integrator
import io.grpc.Metadata
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import proxy.Proxy
import social.Social
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.LoadWorldReplyObserver
import us.cyberstar.data.ProtoServiceProvider
import us.cyberstar.data.entity.telemetry.*
import us.cyberstar.data.external.grpc.GrpcArService
import us.cyberstar.data.mapper.*
import us.cyberstar.data.mapper.social.mapToAr3dModel
import us.cyberstar.data.mapper.social.mapToAr3dModelEntityList
import us.cyberstar.data.mapper.social.mapToSaveAsset
import javax.inject.Inject


internal class GrpcArServiceImpl @Inject constructor(
    private val snackBarProvider: SnackBarProvider,
    private val protoServiceProvider: ProtoServiceProvider
) : GrpcArService {

    override fun testARM() {
        protoServiceProvider.armSocialServiceGrpc!!.getGeneralFeedPostIds(Social.SBaseRequest.getDefaultInstance(),
            object : StreamObserver<social.Social.PostIdList> {
                override fun onNext(value: Social.PostIdList?) {
                    Timber.d(value.toString())
                }

                override fun onError(t: Throwable?) {
                    Timber.e(t)
                }

                override fun onCompleted() {
                    Timber.d("onCompleted")

                }

            })
    }

    override fun createPostRequestEntity(entity: CreatePostRequestEntity): Boolean {
        Timber.d("sending createPostRequestEntity to via GRPC (arService?.createARPost called)")
        protoServiceProvider.arService!!.createARPost(
            mapToSaveAsset(entity),
            object : StreamObserver<Proxy.CreateARPostReply> {
                override fun onError(t: Throwable) {
                    with(t as StatusRuntimeException) {
                        Timber.e("status $status")
                        for (key in trailers.keys()) {
                            val tokenKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)
                            Timber.e("$tokenKey ${trailers.get(tokenKey)}")
                        }
                    }
                    Timber.e("onError $t")
                }

                override fun onCompleted() {
                    Timber.d("onCompleted")
                }

                override fun onNext(value: Proxy.CreateARPostReply) {
                    entity.arPostEntity.postCompId.apply {
                        postId = value.post.id.postId
                        wallId = value.post.id.wallId
                        serverId = value.post.id.serverId
                    }

                    Timber.d("onNext $value")
                }
            })
        // val reply = protoServiceProvider.arServiceBlocking!!.createARPost(mapToSaveAsset(entity))
        return true
    }


    private var loadWorldRequestMessageObserver: StreamObserver<Proxy.LoadWorldRequest>? = null

    override fun setLoadWorldObserver(responseObserver: LoadWorldReplyObserver) {
        with(protoServiceProvider) {
            val debugInfo = "streamMultipleLoadWorld:"
            loadWorldRequestMessageObserver =
                arService?.streamMultipleLoadWorld(object :
                    StreamObserver<Proxy.MultipleLoadWorldReply> {
                    override fun onNext(value: Proxy.MultipleLoadWorldReply?) {
                        Timber.d("$debugInfo onNext data size ${value?.serializedSize}")
                        value?.let {
                            responseObserver.onWorldRequestReply(it)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        Timber.e("$debugInfo onError $t")
                    }

                    override fun onCompleted() {
                        Timber.e("$debugInfo onCompleted")
                    }
                })
        }
    }

    override fun sendLoadWorldRequestEntity(entity: LoadWorldRequestEntity) {
        loadWorldRequestMessageObserver!!.onNext(mapToLoadWorldRequest(entity))
    }

    override fun loadModelList(listener: GrpcArService.Model3dListListener) {
        protoServiceProvider.modelRegistryService!!.getModelsList(
            BaseTypes.EmptyRequest.getDefaultInstance(),
            object: StreamObserver<interactive.Integrator.GetModelsListReply> {
                override fun onNext(value: Integrator.GetModelsListReply) {
                    listener.onModelsFetched(mapToAr3dModelEntityList(value.modelsList))
                }
                override fun onError(t: Throwable) {
                    listener.onError(t)
                }
                override fun onCompleted() {
                }
            })
    }
}