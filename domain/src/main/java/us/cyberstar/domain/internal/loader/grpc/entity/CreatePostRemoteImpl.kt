package us.cyberstar.domain.internal.loader.grpc.entity

import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.SessionIdProvider
import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity
import us.cyberstar.data.external.grpc.GrpcArService
import us.cyberstar.domain.external.loader.image.PostRequestImageLoader
import us.cyberstar.domain.external.loader.grpc.entity.CreatePostRemote

/**
 * The class runs grpc gridAsset with session data
 */
class CreatePostRemoteImpl(
    private val sessionIdProvider: SessionIdProvider,
    private val snackBarProvider: SnackBarProvider,
    private val postRequestImageLoader: PostRequestImageLoader,
    private val grpcArService: GrpcArService
) : CreatePostRemote {

    /**
     * The method processes CreatePostRequestEntity.
     * It checks if the all entity media content has been uploaded to S3 already
     * if it's been not, it uploads it synchronously one by one
     */
    override fun createPostRequest(entity: CreatePostRequestEntity): Boolean {
        Timber.d("new CreatePostRequestEntity came into recorder, checking are images uploaded to s3.. ")
        return with(postRequestImageLoader) {
            val res = uploadCreatePostRequestEntityImages(entity)
            if (res.isSuccess) {
                Timber.d("s3 images upload success!")
                return grpcArService.createPostRequestEntity(entity)
            } else {
                snackBarProvider.showError(res.errMessage, false)
                false
            }
        }
    }
}