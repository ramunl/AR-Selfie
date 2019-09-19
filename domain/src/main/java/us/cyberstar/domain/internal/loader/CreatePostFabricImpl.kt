package us.cyberstar.domain.internal.loader

import us.cyberstar.domain.external.loader.CreatePostFabric
import us.cyberstar.domain.external.loader.base.CreatePostBase
import us.cyberstar.domain.external.loader.grpc.entity.CreatePostRemote
import us.cyberstar.domain.external.loader.local.CreatePostLocal
import us.cyberstar.domain.internal.ArWorldLoaderSettings

class CreatePostFabricImpl(
    private val arWorldLoaderSettings: ArWorldLoaderSettings,
    private val createPostRemote: CreatePostRemote,
    private val createPostLocal: CreatePostLocal
) : CreatePostFabric {

    override fun getPostCreateFabric(): CreatePostBase =
        if (arWorldLoaderSettings.isLocal) createPostLocal
        else createPostRemote
}