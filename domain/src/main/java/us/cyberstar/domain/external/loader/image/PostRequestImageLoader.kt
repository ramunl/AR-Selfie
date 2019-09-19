package us.cyberstar.domain.external.loader.image

import us.cyberstar.data.entity.telemetry.CreatePostRequestEntity

interface PostRequestImageLoader {
    class UploadResult(val isSuccess: Boolean, val errMessage: String)

    fun uploadCreatePostRequestEntityImages(createPostRequestEntity: CreatePostRequestEntity): UploadResult
}