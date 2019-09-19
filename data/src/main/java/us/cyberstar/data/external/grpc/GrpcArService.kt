package us.cyberstar.data.external.grpc

import us.cyberstar.data.LoadWorldReplyObserver
import us.cyberstar.data.entity.social.Ar3dModelEntity
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.entity.telemetry.LoadWorldRequestEntity
import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.data.entity.telemetry.base.ArEntityTelemetry

interface GrpcArService {
    fun createPostRequestEntity(entity: CreatePostRequestEntity): Boolean
    fun setLoadWorldObserver(responseObserver: LoadWorldReplyObserver)
    fun sendLoadWorldRequestEntity(entity: LoadWorldRequestEntity)
    fun testARM()
    interface Model3dListListener{
        fun onModelsFetched(models:List<Ar3dModelEntity>)
        fun onError(error: Throwable)
    }

    fun loadModelList(listener: Model3dListListener)
}