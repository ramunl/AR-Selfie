package us.cyberstar.domain.external.loader.base

import us.cyberstar.data.entity.telemetry.*


interface CreatePostBase {
    fun createPostRequest(entity: CreatePostRequestEntity): Boolean
}