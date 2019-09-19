package us.cyberstar.domain.internal.loader.grpc.entity

import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.data.external.grpc.GrpcTelemetryService
import us.cyberstar.data.entity.telemetry.SaveVideoRequestEntity
import us.cyberstar.domain.external.loader.video.SaveVideoRequestLoader
import us.cyberstar.domain.external.loader.grpc.entity.SaveVideoRemote

/**
 * The class runs grpc gridAsset with session data
 */
class SaveVideoRemoteImpl(
    private val saveVideoRequestLoader: SaveVideoRequestLoader,
    private val snackBarProvider: SnackBarProvider,
    protected val grpcTelemetryService: GrpcTelemetryService
) : SaveVideoRemote {

    override fun saveVideoRequestEntity(entity: SaveVideoRequestEntity): Boolean {
        Timber.d("new SaveVideoRequestEntity came into recorder, checking video already uploaded to s3.. ")
        with(saveVideoRequestLoader) {
            return if (!isUploadedToS3(entity)) {
                val res = uploadSaveVideoRequestEntity(entity)
                if (res.isSuccess) {
                    Timber.d("video s3 upload success!")
                    return grpcTelemetryService.saveTelemetryVideo(entity)
                } else {
                    snackBarProvider.showError(res.errMessage, false)
                    false
                }
            } else {
                true
            }
        }
    }
}